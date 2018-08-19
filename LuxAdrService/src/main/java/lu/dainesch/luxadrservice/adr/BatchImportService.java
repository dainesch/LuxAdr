package lu.dainesch.luxadrservice.adr;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import lu.dainesch.luxadrservice.adr.handler.BuildingHandler;
import lu.dainesch.luxadrservice.adr.handler.CantonHandler;
import lu.dainesch.luxadrservice.adr.handler.CommuneHandler;
import lu.dainesch.luxadrservice.adr.handler.CoordinatesHandler;
import lu.dainesch.luxadrservice.adr.handler.DistrictHandler;
import lu.dainesch.luxadrservice.adr.handler.ImportedEntityHandler;
import lu.dainesch.luxadrservice.adr.handler.LocalityHandler;
import lu.dainesch.luxadrservice.adr.handler.PostCodeHandler;
import lu.dainesch.luxadrservice.adr.handler.QuarterHandler;
import lu.dainesch.luxadrservice.adr.handler.StreetHandler;
import lu.dainesch.luxadrservice.base.Config;
import lu.dainesch.luxadrservice.base.ConfigHandler;
import lu.dainesch.luxadrservice.base.ConfigType;
import lu.dainesch.luxadrservice.base.ConfigValue;
import lu.dainesch.luxadrservice.base.AppProcess;
import lu.dainesch.luxadrservice.base.ImportException;
import lu.dainesch.luxadrservice.base.ProcessHandler;
import lu.dainesch.luxadrservice.base.ProcessingStep;
import lu.dainesch.luxadrservice.input.FixedParser;
import lu.dainesch.luxadrservice.input.GeoParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
@TransactionAttribute(TransactionAttributeType.NEVER)
public class BatchImportService {

    private static final Logger LOG = LoggerFactory.getLogger(StreetHandler.class);

    private static final int EXPECTED_FILES = 11;

    @Inject
    @Config(ConfigType.BATCH_SIZE)
    private ConfigValue batchSize;

    @Inject
    private ProcessHandler procHand;
    @Inject
    private StreetHandler streetHand;
    @Inject
    private QuarterHandler quartHand;
    @Inject
    private PostCodeHandler pcHand;
    @Inject
    private LocalityHandler locHand;
    @Inject
    private DistrictHandler distHand;
    @Inject
    private CommuneHandler commHand;
    @Inject
    private CantonHandler cantHand;
    @Inject
    private BuildingHandler buildHand;
    @Inject
    private CoordinatesHandler coordHand;
    @Inject
    private ConfigHandler confHand;

    public void importAdrRemote() throws ImportException {
        ConfigValue url = confHand.getValue(ConfigType.DATA_PUBLIC_ADR_URL);

        Path tmpFile = null;
        try {

            tmpFile = Files.createTempFile("CACLR", ".zip");

            Client cl = ClientBuilder.newClient();

            try (InputStream in = cl.target(url.getValue()).request().get().readEntity(InputStream.class)) {
                updateAll(in);
            }

        } catch (IOException ex) {
            throw new ImportException("Error retrieving file remote url " + url.getValue(), ex);
        } finally {
            try {
                if (tmpFile != null) {
                    Files.deleteIfExists(tmpFile);
                }
            } catch (IOException ex) {
                LOG.error("Error cleaning up file " + tmpFile, ex);
            }
        }
    }

    public void importGeoRemote() throws ImportException {
        ConfigValue url = confHand.getValue(ConfigType.DATA_PUBLIC_GEO_URL);

        Path tmpFile = null;
        try {

            tmpFile = Files.createTempFile("addresses", ".geojson");

            Client cl = ClientBuilder.newClient();

            try (InputStream in = cl.target(url.getValue()).request().get().readEntity(InputStream.class)) {
                importGeodata(in);
            }

        } catch (IOException ex) {
            throw new ImportException("Error retrieving file remote url " + url.getValue(), ex);
        } finally {
            try {
                if (tmpFile != null) {
                    Files.deleteIfExists(tmpFile);
                }
            } catch (IOException ex) {
                LOG.error("Error cleaning up file " + tmpFile, ex);
            }
        }
    }

    public void updateAll(InputStream in) throws ImportException {

        byte[] buff = new byte[4 * 1024];
        Map<ProcessingStep, Path> fileMap = new HashMap<>();
        try {
            // map files contained in zip to steps to execute in order later
            try (ZipInputStream zin = new ZipInputStream(in)) {

                ZipEntry entry;
                while ((entry = zin.getNextEntry()) != null) {
                    LOG.info("Reading file " + entry.getName() + " from zip");

                    ProcessingStep step = ProcessingStep.getStepFromFile(entry.getName());

                    if (step != null) {
                        Path temp = Files.createTempFile(entry.getName(), "");
                        fileMap.put(step, temp);
                        LOG.info("Creating temp file " + temp + " from zip");
                        try (OutputStream out = Files.newOutputStream(temp)) {
                            int read;
                            while ((read = zin.read(buff)) != -1) {
                                out.write(buff, 0, read);
                            }
                        }
                    }
                }

            } catch (IOException ex) {
                throw new ImportException("Error during step temp file creation", ex);
            }

            if (fileMap.size() != EXPECTED_FILES) {
                throw new ImportException("Zip does not contain required files");
            }

            // fix order and start
            List<ProcessingStep> steps = Arrays.asList(ProcessingStep.values());
            Collections.sort(steps, ProcessingStep.comparator());

            AppProcess currentProcess = procHand.createNewProcess();

            for (ProcessingStep step : steps) {
                if (step.getOrder() > EXPECTED_FILES) {
                    continue;
                }
                try (InputStream stepIn = Files.newInputStream(fileMap.get(step))) {

                    update(currentProcess, step, stepIn);

                } catch (IOException ex) {
                    throw new ImportException("Error reading input from " + fileMap.get(step), ex);
                }
            }

            procHand.complete(currentProcess);

        } finally {
            fileMap.entrySet().forEach(e -> {
                try {
                    Files.deleteIfExists(e.getValue());
                } catch (IOException ex) {
                    LOG.error("Error cleaning up file " + e.getValue(), ex);
                }
            });
        }

    }

    public void update(AppProcess currentProcess, ProcessingStep step, InputStream in) throws ImportException {

        ImportedEntityHandler objHand = getHandler(step);
        int[] format;
        if (step.isAlt()) {
            format = objHand.getAltLineFormat();
        } else {
            format = objHand.getLineFormat();
        }

        boolean grouped = true;
        if (currentProcess == null) {
            currentProcess = procHand.createNewProcess();
            grouped = false;
        }

        procHand.log(currentProcess, step, "Starting step " + step.getStepName());

        try (FixedParser parser = new FixedParser(in, format)) {
            if (!parser.hasNext()) {
                LOG.warn("Empty file given as input, aborting");
                procHand.log(currentProcess, step, "Empty file given as input, aborting");
                return;
            }
            int deleted = 0;

            if (step.isAlt()) {
                deleted = objHand.deleteAltNames();
            }

            List<Future<Boolean>> results = new ArrayList<>();
            int count = 0;

            int sub = 0;
            while (parser.hasNext()) {
                Future<Boolean> res;
                if (step.isAlt()) {
                    res = objHand.importAltName(parser.next());
                } else {
                    res = objHand.importLine(parser.next(), currentProcess);
                }
                results.add(res);

                sub++;
                if (sub % batchSize.getInt() == 0) {
                    LOG.info("Submitted " + count + " " + step.getStepName());
                    procHand.log(currentProcess, step, "Submitted " + count + " " + step.getStepName());
                    for (Future<Boolean> r : results) {
                        if (r.get()) {
                            count++;
                        }
                    }
                    results.clear();
                    LOG.info("Imported " + count + " " + step.getStepName());
                    procHand.log(currentProcess, step, "Imported " + count + " " + step.getStepName());
                }
            }

            // postprocess remaining
            for (Future<Boolean> r : results) {
                if (r.get()) {
                    count++;
                }
            }

            if (!step.isAlt()) {
                deleted = objHand.postprocess(currentProcess);
            }

            LOG.info("Done! Imported " + count + " " + step.getStepName() + " and deleted " + deleted);
            procHand.log(currentProcess, step, "Done! Imported " + count + " " + step.getStepName() + " and deleted " + deleted);

            if (!grouped) {
                procHand.complete(currentProcess);
            }

        } catch (IOException | InterruptedException | ExecutionException ex) {
            procHand.log(currentProcess, step, "Error during step " + step.getStepName() + ": view log for more info");
            procHand.error(currentProcess);
            throw new ImportException("Error during " + step.getStepName() + " import", ex);
        }

    }

    private ImportedEntityHandler getHandler(ProcessingStep step) throws ImportException {
        switch (step) {
            case BUILDING:
            case BUILDING_DES:
                return buildHand;
            case CANTON:
                return cantHand;
            case COMMUNE:
                return commHand;
            case DISTRICT:
                return distHand;
            case LOCALITY:
            case LOCALITY_ALT:
                return locHand;
            case POSTALCODE:
                return pcHand;
            case QUARTER:
                return quartHand;
            case STREET:
            case STREET_ALT:
                return streetHand;
            default:
                throw new ImportException("Unable to resolvew handler for step " + step);
        }
    }

    public void importGeodata(InputStream in) throws ImportException {

        AppProcess currentProcess = procHand.createNewProcess();

        procHand.log(currentProcess, ProcessingStep.GEODATA, "Starting step " + ProcessingStep.GEODATA.getStepName());

        try (GeoParser parser = new GeoParser(in)) {
            if (!parser.hasNext()) {
                procHand.log(currentProcess, ProcessingStep.GEODATA, "Empty file given as input, aborting");
                LOG.warn("Empty file given as input, aborting");
                return;
            }

            List<Future<Boolean>> results = new ArrayList<>();
            int count = 0;

            int sub = 0;
            while (parser.hasNext()) {

                Future<Boolean> res = coordHand.importCoords(parser.next());
                results.add(res);

                sub++;
                if (sub % batchSize.getInt() == 0) {
                    LOG.info("Submitted " + count + " coordinates");
                    procHand.log(currentProcess, ProcessingStep.GEODATA, "Submitted " + count + " coordinates");
                    for (Future<Boolean> r : results) {
                        if (r.get()) {
                            count++;
                        }
                    }
                    results.clear();
                    LOG.info("Imported " + count + " coordinates");
                    procHand.log(currentProcess, ProcessingStep.GEODATA, "Imported " + count + " coordinates");
                }
            }

            // postprocess remaining
            for (Future<Boolean> r : results) {
                if (r.get()) {
                    count++;
                }
            }

            LOG.info("Done! Imported " + count + " coordinates ");
            procHand.log(currentProcess, ProcessingStep.GEODATA, "Done! Imported " + count + " coordinates ");
            procHand.complete(currentProcess);

        } catch (InterruptedException | ExecutionException ex) {
            procHand.log(currentProcess, ProcessingStep.GEODATA, "Error during step " + ProcessingStep.GEODATA.getStepName() + ": view log for more info");
            procHand.error(currentProcess);
            throw new ImportException("Error during coordinates import", ex);
        }
    }

}
