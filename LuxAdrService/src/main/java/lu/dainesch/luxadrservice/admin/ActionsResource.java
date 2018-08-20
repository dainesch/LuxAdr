package lu.dainesch.luxadrservice.admin;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import lu.dainesch.luxadrservice.adr.BatchImportService;
import lu.dainesch.luxadrservice.base.ImportException;
import lu.dainesch.luxadrservice.base.ProcessHandler;
import lu.dainesch.luxadrservice.base.ProcessingStep;
import lu.dainesch.luxadrservice.search.SearchException;
import lu.dainesch.luxadrservice.search.SearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("action")
@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class ActionsResource {

    private static final Logger LOG = LoggerFactory.getLogger(ActionsResource.class);

    @Inject
    private SearchService search;
    @Inject
    private BatchImportService importServ;
    @Inject
    private ProcessHandler procHand;

    @Path("ping")
    @POST
    public JsonObject ping() {
        return Json.createObjectBuilder().add("time", System.currentTimeMillis()).build();
    }

    @Path("index")
    @POST
    public JsonObject index() {
        if (procHand.getLastCompletedProcess(ProcessingStep.BUILDING) == null
                || procHand.getLastCompletedProcess(ProcessingStep.GEODATA) == null) {
            throw new WebApplicationException("Please import all data first", Response.Status.SERVICE_UNAVAILABLE);
        }
        try {
            search.indexData();
        } catch (SearchException ex) {
            LOG.error("Error indexing data", ex);
            throw new WebApplicationException("Error while indexing data, please see log", Response.Status.INTERNAL_SERVER_ERROR);
        }
        return Json.createObjectBuilder()
                .add("info", "Indexing done")
                .build();
    }

    @Path("wipeIndex")
    @POST
    public JsonObject wipeIndex() {
        try {
            search.wipeData();
        } catch (SearchException ex) {
            LOG.error("Error indexing data", ex);
            throw new WebApplicationException("Error while clearing index, please see log", Response.Status.INTERNAL_SERVER_ERROR);
        }
        return Json.createObjectBuilder()
                .add("info", "Wiping done")
                .build();
    }

    @Path("importAddr")
    @POST
    public JsonObject importAddressData() {
        try {
            importServ.importAdrRemote();
        } catch (ImportException ex) {
            LOG.error("Error importing address data", ex);
            throw new WebApplicationException("Error while importing address data", Response.Status.INTERNAL_SERVER_ERROR);
        }
        return Json.createObjectBuilder()
                .add("info", "Address import done")
                .build();
    }

    @Path("importGeo")
    @POST
    public JsonObject importGeoData() {
        if (procHand.getLastCompletedProcess(ProcessingStep.BUILDING) == null) {
            throw new WebApplicationException("Please import address data first", Response.Status.SERVICE_UNAVAILABLE);
        }
        try {
            importServ.importGeoRemote();
        } catch (ImportException ex) {
            LOG.error("Error importing geo data", ex);
            throw new WebApplicationException("Error while importing geo data", Response.Status.INTERNAL_SERVER_ERROR);
        }
        return Json.createObjectBuilder()
                .add("info", "Geo import done")
                .build();
    }

}
