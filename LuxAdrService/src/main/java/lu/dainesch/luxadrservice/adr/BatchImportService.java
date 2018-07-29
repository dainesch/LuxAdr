package lu.dainesch.luxadrservice.adr;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import lu.dainesch.luxadrservice.adr.handler.BuildingHandler;
import lu.dainesch.luxadrservice.adr.handler.CantonHandler;
import lu.dainesch.luxadrservice.adr.handler.CommuneHandler;
import lu.dainesch.luxadrservice.adr.handler.DistrictHandler;
import lu.dainesch.luxadrservice.adr.handler.LocalityHandler;
import lu.dainesch.luxadrservice.adr.handler.PostCodeHandler;
import lu.dainesch.luxadrservice.adr.handler.QuarterHandler;
import lu.dainesch.luxadrservice.adr.handler.StreetHandler;
import lu.dainesch.luxadrservice.base.Import;
import lu.dainesch.luxadrservice.base.ImportException;
import lu.dainesch.luxadrservice.base.ImportHandler;
import lu.dainesch.luxadrservice.input.FixedParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
@TransactionAttribute(TransactionAttributeType.NEVER)
public class BatchImportService {

    private static final Logger LOG = LoggerFactory.getLogger(StreetHandler.class);

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

    public void updateStreets(InputStream in) throws ImportException {

        try (FixedParser parser = new FixedParser(in, 5, 40, 40, 10, 5, 1, 1, 10, 1, 10, 2, 1, 4, 1, 5, 1, 1, 30)) {
            if (!parser.hasNext()) {
                LOG.warn("Empty file given as input, aborting");
                return;
            }

            Import currentImport = impHand.createNewImport();
            List<Future<Boolean>> results = new ArrayList<>();
            int count = 0;

            while (parser.hasNext()) {
                results.add(streetHand.importStreet(parser.next(), currentImport));
            }
            for (Future<Boolean> r : results) {
                if (r.get()) {
                    count++;
                }
            }

            currentImport.setEnd(new Date());
            impHand.update(currentImport);

            int deleted = streetHand.postprocess(currentImport);

            LOG.info("Imported " + count + " streets and deleted " + deleted);

        } catch (IOException | InterruptedException | ExecutionException ex) {
            throw new ImportException("Error during street import", ex);
        }

    }

    public void updateStreetAltNames(InputStream in) throws ImportException {

        try (FixedParser parser = new FixedParser(in, 3, 40, 40, 1, 10, 5, 80, 1)) {
            if (!parser.hasNext()) {
                LOG.warn("Empty file given as input, aborting");
                return;
            }

            int deleted = streetHand.deleteAltNames();

            List<Future<Boolean>> results = new ArrayList<>();
            int count = 0;

            while (parser.hasNext()) {
                results.add(streetHand.importAltName(parser.next()));
            }
            for (Future<Boolean> r : results) {
                if (r.get()) {
                    count++;
                }
            }

            LOG.info("Imported " + count + " street names and deleted " + deleted);

        } catch (IOException | InterruptedException | ExecutionException ex) {
            throw new ImportException("Error during street name import", ex);
        }

    }

    public void updateQuarters(InputStream in) throws ImportException {

        try (FixedParser parser = new FixedParser(in, 5, 40, 10, 5, 1)) {
            if (!parser.hasNext()) {
                LOG.warn("Empty file given as input, aborting");
                return;
            }

            Import currentImport = impHand.createNewImport();
            List<Future<Boolean>> results = new ArrayList<>();
            int count = 0;

            while (parser.hasNext()) {
                results.add(quartHand.importQuarter(parser.next(), currentImport));
            }
            for (Future<Boolean> r : results) {
                if (r.get()) {
                    count++;
                }
            }

            currentImport.setEnd(new Date());
            impHand.update(currentImport);
            int deleted = quartHand.postprocess(currentImport);

            LOG.info("Imported " + count + " quarters and deleted " + deleted);

        } catch (IOException | InterruptedException | ExecutionException ex) {
            throw new ImportException("Error during quarter import", ex);
        }

    }

    public void updatePostCodes(InputStream in) throws ImportException {

        try (FixedParser parser = new FixedParser(in, 4, 40, 1, 4, 4, 10)) {
            if (!parser.hasNext()) {
                LOG.warn("Empty file given as input, aborting");
                return;
            }

            Import currentImport = impHand.createNewImport();
            List<Future<Boolean>> results = new ArrayList<>();
            int count = 0;

            while (parser.hasNext()) {
                results.add(pcHand.importPostCode(parser.next(), currentImport));
            }
            for (Future<Boolean> r : results) {
                if (r.get()) {
                    count++;
                }
            }

            currentImport.setEnd(new Date());
            impHand.update(currentImport);

            int deleted = pcHand.postprocess(currentImport);

            LOG.info("Imported " + count + " postal codes and deleted " + deleted);

        } catch (IOException | InterruptedException | ExecutionException ex) {
            throw new ImportException("Error during postalcode import", ex);
        }

    }

    public void updateLocalities(InputStream in) throws ImportException {

        try (FixedParser parser = new FixedParser(in, 5, 40, 40, 2, 1, 10, 1, 10, 2, 1, 2, 1)) {
            if (!parser.hasNext()) {
                LOG.warn("Empty file given as input, aborting");
                return;
            }

            Import currentImport = impHand.createNewImport();
            List<Future<Boolean>> results = new ArrayList<>();
            int count = 0;

            while (parser.hasNext()) {
                results.add(locHand.importLocality(parser.next(), currentImport));
            }
            for (Future<Boolean> r : results) {
                if (r.get()) {
                    count++;
                }
            }

            currentImport.setEnd(new Date());
            impHand.update(currentImport);

            int deleted = locHand.postprocess(currentImport);

            LOG.info("Imported " + count + " localities and deleted " + deleted);

        } catch (IOException | InterruptedException | ExecutionException ex) {
            throw new ImportException("Error during locality import", ex);
        }

    }

    public void updateLocalityAltNames(InputStream in) throws ImportException {

        try (FixedParser parser = new FixedParser(in, 3, 40, 40, 1, 10, 5)) {
            if (!parser.hasNext()) {
                LOG.warn("Empty file given as input, aborting");
                return;
            }

            int deleted = locHand.deleteAltNames();
            List<Future<Boolean>> results = new ArrayList<>();
            int count = 0;

            while (parser.hasNext()) {
                results.add(locHand.importAltName(parser.next()));
            }
            for (Future<Boolean> r : results) {
                if (r.get()) {
                    count++;
                }
            }

            LOG.info("Imported " + count + " localities names and deleted " + deleted);

        } catch (IOException | InterruptedException | ExecutionException ex) {
            throw new ImportException("Error during locality name import", ex);
        }

    }

    public void updateDistricts(InputStream in) throws ImportException {

        try (FixedParser parser = new FixedParser(in, 4, 40, 10)) {
            if (!parser.hasNext()) {
                LOG.warn("Empty file given as input, aborting");
                return;
            }

            Import currentImport = impHand.createNewImport();
            List<Future<Boolean>> results = new ArrayList<>();
            int count = 0;

            while (parser.hasNext()) {
                results.add(distHand.importDistrict(parser.next(), currentImport));
            }
            for (Future<Boolean> r : results) {
                if (r.get()) {
                    count++;
                }
            }

            currentImport.setEnd(new Date());
            impHand.update(currentImport);

            int deleted = distHand.postprocess(currentImport);

            LOG.info("Imported " + count + " districts and deleted " + deleted);

        } catch (IOException | InterruptedException | ExecutionException ex) {
            throw new ImportException("Error during district import", ex);
        }

    }

    public void updateCommunes(InputStream in) throws ImportException {

        try (FixedParser parser = new FixedParser(in, 2, 40, 40, 10, 2, 1)) {
            if (!parser.hasNext()) {
                LOG.warn("Empty file given as input, aborting");
                return;
            }

            Import currentImport = impHand.createNewImport();
            List<Future<Boolean>> results = new ArrayList<>();
            int count = 0;

            while (parser.hasNext()) {
                results.add(commHand.importCommune(parser.next(), currentImport));
            }
            for (Future<Boolean> r : results) {
                if (r.get()) {
                    count++;
                }
            }

            currentImport.setEnd(new Date());
            impHand.update(currentImport);

            int deleted = commHand.postprocess(currentImport);

            LOG.info("Imported " + count + " communes and deleted " + deleted);

        } catch (IOException | InterruptedException | ExecutionException ex) {
            throw new ImportException("Error during commune import", ex);
        }

    }

    public void updateCantons(InputStream in) throws ImportException {

        try (FixedParser parser = new FixedParser(in, 2, 40, 10, 4, 1)) {
            if (!parser.hasNext()) {
                LOG.warn("Empty file given as input, aborting");
                return;
            }

            Import currentImport = impHand.createNewImport();
            List<Future<Boolean>> results = new ArrayList<>();
            int count = 0;

            while (parser.hasNext()) {
                results.add(cantHand.importCanton(parser.next(), currentImport));
            }
            for (Future<Boolean> r : results) {
                if (r.get()) {
                    count++;
                }
            }

            currentImport.setEnd(new Date());
            impHand.update(currentImport);

            int deleted = cantHand.postprocess(currentImport);

            LOG.info("Imported " + count + " cantons and deleted " + deleted);

        } catch (IOException | InterruptedException | ExecutionException ex) {
            throw new ImportException("Error during canton import", ex);
        }

    }

    public void updateBuildings(InputStream in) throws ImportException {

        try (FixedParser parser = new FixedParser(in, 8, 3, 6, 10, 1, 10, 4, 1, 5, 1, 5, 1, 1, 1)) {
            if (!parser.hasNext()) {
                LOG.warn("Empty file given as input, aborting");
                return;
            }

            Import currentImport = impHand.createNewImport();
            List<Future<Boolean>> results = new ArrayList<>();
            int count = 0;

            int sub = 0;
            while (parser.hasNext()) {
                results.add(buildHand.importBuilding(parser.next(), currentImport));
                sub++;
                if (sub % 1000 == 0) {
                    LOG.info("Submitted " + count + " buildings");
                    for (Future<Boolean> r : results) {
                        if (r.get()) {
                            count++;
                        }
                    }
                    results.clear();
                    LOG.info("Imported " + count + " buildings");
                }
            }
            
            // postprocess remaining
            for (Future<Boolean> r : results) {
                if (r.get()) {
                    count++;
                }
            }

            currentImport.setEnd(new Date());
            impHand.update(currentImport);

            int deleted = buildHand.postprocess(currentImport);

            LOG.info("Imported " + count + " buildings and deleted " + deleted);

        } catch (IOException | InterruptedException | ExecutionException ex) {
            throw new ImportException("Error during building import", ex);
        }

    }

    public void updateBuildingsDesign(InputStream in) throws ImportException {

        try (FixedParser parser = new FixedParser(in, 8, 3, 6, 10, 1, 10, 4, 1, 5, 1, 5, 1, 1, 1, 40, 1)) {
            if (!parser.hasNext()) {
                LOG.warn("Empty file given as input, aborting");
                return;
            }

            List<Future<Boolean>> results = new ArrayList<>();
            int count = 0;

            while (parser.hasNext()) {
                results.add(buildHand.importBuildingName(parser.next()));
            }
            for (Future<Boolean> r : results) {
                if (r.get()) {
                    count++;
                }
            }

            LOG.info("Imported " + count + " buildings names ");

        } catch (IOException | InterruptedException | ExecutionException ex) {
            throw new ImportException("Error during building name import", ex);
        }

    }
    
    

}
