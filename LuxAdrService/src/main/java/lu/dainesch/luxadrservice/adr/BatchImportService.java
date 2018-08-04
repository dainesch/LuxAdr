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
import lu.dainesch.luxadrservice.base.Import;
import lu.dainesch.luxadrservice.base.ImportException;
import lu.dainesch.luxadrservice.base.ImportHandler;
import lu.dainesch.luxadrservice.base.ImportStep;
import lu.dainesch.luxadrservice.input.FixedParser;
import lu.dainesch.luxadrservice.input.GeoParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
@TransactionAttribute(TransactionAttributeType.NEVER)
public class BatchImportService {

    private static final Logger LOG = LoggerFactory.getLogger(StreetHandler.class);
    private static final int BATCH_SIZE = 1000;

    @Inject
    private ImportHandler impHand;
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

    public void updateAll(InputStream in) throws ImportException {

        byte[] buff = new byte[4 * 1024];
        Map<ImportStep, Path> fileMap = new HashMap<>();
        try {
            // map files contained in zip to steps to execute in order later
            try (ZipInputStream zin = new ZipInputStream(in)) {

                ZipEntry entry;
                while ((entry = zin.getNextEntry()) != null) {
                    LOG.info("Reading file " + entry.getName() + " from zip");

                    ImportStep step = ImportStep.getStepFromFile(entry.getName());

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

            if (fileMap.size() != ImportStep.values().length) {
                throw new ImportException("Zip does not contain required files");
            }

            // fix order and start
            List<ImportStep> steps = Arrays.asList(ImportStep.values());
            Collections.sort(steps, ImportStep.comparator());

            Import currentImport = impHand.createNewImport();

            for (ImportStep step : steps) {
                try (InputStream stepIn = Files.newInputStream(fileMap.get(step))) {

                    update(currentImport, step, stepIn);

                } catch (IOException ex) {
                    throw new ImportException("Error reading input from " + fileMap.get(step), ex);
                }
            }

            impHand.complete(currentImport);

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

    public void update(Import currentImport, ImportStep step, InputStream in) throws ImportException {

        ImportedEntityHandler objHand = getHandler(step);
        int[] format;
        if (step.isAlt()) {
            format = objHand.getAltLineFormat();
        } else {
            format = objHand.getLineFormat();
        }

        boolean grouped = true;
        if (currentImport == null) {
            currentImport = impHand.createNewImport();
            grouped = false;
        }

        impHand.log(currentImport, step, "Starting step " + step.getStepName());

        try (FixedParser parser = new FixedParser(in, format)) {
            if (!parser.hasNext()) {
                LOG.warn("Empty file given as input, aborting");
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
                    res = objHand.importLine(parser.next(), currentImport);
                }
                results.add(res);

                sub++;
                if (sub % BATCH_SIZE == 0) {
                    LOG.info("Submitted " + count + " " + step.getStepName());
                    for (Future<Boolean> r : results) {
                        if (r.get()) {
                            count++;
                        }
                    }
                    results.clear();
                    LOG.info("Imported " + count + " " + step.getStepName());
                }
            }

            // postprocess remaining
            for (Future<Boolean> r : results) {
                if (r.get()) {
                    count++;
                }
            }

            if (!step.isAlt()) {
                deleted = objHand.postprocess(currentImport);
            }

            LOG.info("Imported " + count + " " + step.getStepName() + " and deleted " + deleted);
            impHand.log(currentImport, step, "Imported " + count + " " + step.getStepName() + " and deleted " + deleted);

            if (!grouped) {
                impHand.complete(currentImport);
            }

        } catch (IOException | InterruptedException | ExecutionException ex) {
            impHand.log(currentImport, step, "Error during step " + step.getStepName() + ": view log for more info");
            impHand.error(currentImport);
            throw new ImportException("Error during " + step.getStepName() + " import", ex);
        }

    }

    private ImportedEntityHandler getHandler(ImportStep step) throws ImportException {
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
        try (GeoParser parser = new GeoParser(in)) {
            if (!parser.hasNext()) {
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
                if (sub % BATCH_SIZE == 0) {
                    LOG.info("Submitted " + count + " coordinates");
                    for (Future<Boolean> r : results) {
                        if (r.get()) {
                            count++;
                        }
                    }
                    results.clear();
                    LOG.info("Imported " + count + " coordinates");
                }
            }

            // postprocess remaining
            for (Future<Boolean> r : results) {
                if (r.get()) {
                    count++;
                }
            }

            LOG.info("Imported " + count + " coordinates ");

        } catch (InterruptedException | ExecutionException ex) {
            throw new ImportException("Error during coordinates import", ex);
        }
    }

}
