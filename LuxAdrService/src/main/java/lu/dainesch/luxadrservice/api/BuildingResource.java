package lu.dainesch.luxadrservice.api;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import lu.dainesch.luxadrservice.adr.entity.Building;
import lu.dainesch.luxadrservice.adr.handler.BuildingHandler;

@Path("building")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class BuildingResource {

    @Inject
    private BuildingHandler builHand;

    @GET
    @Path("{id}")
    public Response getById(@PathParam("id") Long id) {
        Building b = builHand.getById(id);
        if (b == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(b.toJson(true).build()).build();
    }

}