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
import lu.dainesch.luxadrservice.search.SearchException;
import lu.dainesch.luxadrservice.search.SearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("action")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ActionsResource {

    private static final Logger LOG = LoggerFactory.getLogger(ActionsResource.class);

    @Inject
    private SearchService search;

    @Path("index")
    @POST
    public JsonObject index() {
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

}
