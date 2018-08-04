package lu.dainesch.luxadrservice.api;

import java.util.List;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import lu.dainesch.luxadrservice.adr.entity.Building;
import lu.dainesch.luxadrservice.adr.handler.BuildingHandler;
import lu.dainesch.luxadrservice.api.dto.GeoRequest;

@Path("geo")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class GeoResource {

    @Inject
    private ApiService as;
    @Inject
    private BuildingHandler builHand;

    @POST
    @Path("building/all")
    public Response getNearestBuilding(GeoRequest req) {
        if (!as.validateAndFix(req)) {
            return as.emptyArrayResponse();
        }

        List<Building> ret = builHand.getInRange(req.getLatitude(), req.getLongitude(), req.getDistance());
        if (ret.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        JsonArrayBuilder arr = Json.createArrayBuilder();
        ret.forEach(b -> arr.add(b.toJson(true)));

        return Response.ok(arr.build()).build();
    }

    @POST
    @Path("building")
    public Response getNearestBuildingNearest(GeoRequest req) {
        if (!as.validateAndFix(req)) {
            return as.emptyArrayResponse();
        }

        Building ret = builHand.getNearest(req.getLatitude(), req.getLongitude(), req.getDistance());
        if (ret == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(ret.toJson(true).build()).build();
    }

}
