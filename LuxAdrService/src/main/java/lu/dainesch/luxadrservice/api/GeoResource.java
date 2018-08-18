package lu.dainesch.luxadrservice.api;

import io.swagger.v3.oas.annotations.Operation;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import lu.dainesch.luxadrservice.adr.entity.Building;
import lu.dainesch.luxadrservice.adr.handler.BuildingHandler;
import lu.dainesch.luxadrdto.GeoRequest;
import lu.dainesch.luxadrdto.entity.BuildingDTO;
import lu.dainesch.luxadrservice.search.AdrSearchEntry;
import lu.dainesch.luxadrservice.search.LuceneSingleton;
import lu.dainesch.luxadrservice.search.SearchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("geo")
@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
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
    @Operation(summary = "Returns buildings in distance of location",
            description = "Returns the buildings in the distance of the given location. "
            + "If properly indexed the search can be performed using the better lucene option.",
            tags = {"geo", "building"}
    )
    public List<BuildingDTO> getBuildingsInDistance(GeoRequest req) {
        if (!as.validateAndFix(req)) {
            return Collections.EMPTY_LIST;
        }
        checkLucene(req);

        List<Building> ret = null;
        if (!req.getLucene()) {
            ret = builHand.getInRange(req.getLatitude(), req.getLongitude(), req.getDistance());
        } else {
            try {
                Set<AdrSearchEntry> results = lucene.getBuildingsInDistance(req.getLatitude(), req.getLongitude(), req.getDistance() * 1000, Integer.MAX_VALUE);
                if (results.isEmpty()) {
                    return Collections.EMPTY_LIST;
                }
                List<Long> ids = results.stream().map(r -> r.getId()).collect(Collectors.toList());
                ret = builHand.getBuildingsRangeByIds(ids);

            } catch (SearchException ex) {
                LOG.error("Error during geo search", ex);
                throw new WebApplicationException("Error during geo search", Response.Status.INTERNAL_SERVER_ERROR);
            }
        }
        return ret.stream().map(b -> b.toDTO(true)).collect(Collectors.toList());
    }

    @POST
    @Path("building")
    @Operation(summary = "Returns the nearest building to the given location",
            description = "Returns the nearest building to the given location. "
            + "If properly indexed the search can be performed using the better lucene option.",
            tags = {"geo", "building"}
    )
    public BuildingDTO getNearestBuilding(GeoRequest req) {
        if (!as.validateAndFix(req)) {
            return null;
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
            return null;
        }
        return ret.toDTO(true);
    }

    private void checkLucene(GeoRequest req) {
        if (req.getLucene()) {
            if (!lucene.isEnabled()) {
                throw new WebApplicationException("Trying to use lucene search while lucene not available", Response.Status.SERVICE_UNAVAILABLE);
            }
            if (!lucene.hasData()) {
                throw new WebApplicationException("Lucene has not yet indexed data", Response.Status.SERVICE_UNAVAILABLE);
            }
        }
    }

}
