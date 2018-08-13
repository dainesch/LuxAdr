package lu.dainesch.luxadrservice.api;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import lu.dainesch.luxadrservice.adr.entity.Building;
import lu.dainesch.luxadrservice.adr.handler.BuildingHandler;
import lu.dainesch.luxadrservice.api.dto.GeoRequest;
import lu.dainesch.luxadrservice.search.AdrSearchEntry;
import lu.dainesch.luxadrservice.search.LuceneSingleton;
import lu.dainesch.luxadrservice.search.SearchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("geo")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class GeoResource {

    private static final Logger LOG = LoggerFactory.getLogger(GeoResource.class);

    @Inject
    private ApiService as;
    @Inject
    private BuildingHandler builHand;
    @Inject
    private LuceneSingleton lucene;

    @POST
    @Path("building/all")
    public Response getBuildingsInDistance(GeoRequest req) {
        if (!as.validateAndFix(req)) {
            return as.emptyArrayResponse();
        }
        checkLucene(req);

        List<Building> ret = null;
        if (!req.getLucene()) {
            ret = builHand.getInRange(req.getLatitude(), req.getLongitude(), req.getDistance());
        } else {
            try {
                Set<AdrSearchEntry> results = lucene.getBuildingsInDistance(req.getLatitude(), req.getLongitude(), req.getDistance() * 1000, Integer.MAX_VALUE);
                if (results.isEmpty()) {
                    return as.emptyArrayResponse();
                }
                List<Long> ids = results.stream().map(r -> r.getId()).collect(Collectors.toList());
                ret = builHand.getBuildingsRangeByIds(ids);

            } catch (SearchException ex) {
                LOG.error("Error during geo search", ex);
                throw new WebApplicationException("Error during geo search", Response.Status.INTERNAL_SERVER_ERROR);
            }
        }
        if (ret.isEmpty()) {
            return as.emptyArrayResponse();
        }

        JsonArrayBuilder arr = Json.createArrayBuilder();
        ret.forEach(b -> arr.add(b.toJson(true)));

        return Response.ok(arr.build()).build();
    }

    @POST
    @Path("building")
    public Response getNearestBuilding(GeoRequest req) {
        if (!as.validateAndFix(req)) {
            return as.emptyArrayResponse();
        }
        checkLucene(req);

        Building ret = null;
        if (!req.getLucene()) {
            ret = builHand.getNearest(req.getLatitude(), req.getLongitude(), req.getDistance());
        } else {
            try {
                // lucene
                AdrSearchEntry e = lucene.getNearest(req.getLatitude(), req.getLongitude());
                if (e != null) {
                    ret = builHand.getById(e.getId());
                }
            } catch (SearchException ex) {
                LOG.error("Error during geo search", ex);
                throw new WebApplicationException("Error during geo search", Response.Status.INTERNAL_SERVER_ERROR);
            }
        }

        if (ret == null) {
            return as.emptyArrayResponse();
        }
        return Response.ok(ret.toJson(true).build()).build();
    }

    private void checkLucene(GeoRequest req) {
        if (req.getLucene()) {
            if (!lucene.isEnabled()) {
                throw new WebApplicationException("Trying to use lucene search while lucene not available", Response.Status.SERVICE_UNAVAILABLE);
            }
            try {
                if (!lucene.hasData()) {
                    throw new WebApplicationException("Lucene has not yet indexed data", Response.Status.SERVICE_UNAVAILABLE);
                }
            } catch (IOException ex) {
                LOG.error("Error while checking lucene status", ex);
                throw new WebApplicationException("Error while checking lucene status", Response.Status.INTERNAL_SERVER_ERROR);
            }
        }
    }

}
