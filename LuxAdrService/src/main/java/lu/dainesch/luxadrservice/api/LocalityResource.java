package lu.dainesch.luxadrservice.api;

import lu.dainesch.luxadrservice.api.dto.SearchRequest;
import java.util.List;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import lu.dainesch.luxadrservice.adr.entity.Locality;
import lu.dainesch.luxadrservice.adr.entity.PostalCode;
import lu.dainesch.luxadrservice.adr.entity.Street;
import lu.dainesch.luxadrservice.adr.handler.LocalityHandler;

@Path("locality")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class LocalityResource {

    @Inject
    private ApiService as;
    @Inject
    private LocalityHandler locHand;

    @GET
    @Path("{id}")
    public Response getById(@PathParam("id") Long id) {
        Locality loc = locHand.getById(id);
        if (loc == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(loc.toJson().build()).build();
    }

    @GET
    @Path("{id}/streets")
    public Response getStreets(@PathParam("id") Long id) {
        List<Street> streets = locHand.getStreets(id);
        JsonArrayBuilder ret = Json.createArrayBuilder();
        streets.forEach((st) -> {
            ret.add(st.toJson(false));
        });

        return Response.ok(ret.build()).build();
    }

    @GET
    @Path("{id}/postcodes")
    public Response getPostCodes(@PathParam("id") Long id) {
        List<PostalCode> streets = locHand.getPostCodes(id);
        JsonArrayBuilder ret = Json.createArrayBuilder();
        streets.forEach((st) -> {
            ret.add(st.toJson());
        });

        return Response.ok(ret.build()).build();
    }

    @POST
    @Path("search")
    public Response search(SearchRequest req) {
        if (!as.validateAndFix(req, true)) {
            return as.emptyArrayResponse();
        }
        List<Locality> locs = locHand.search(req);
        JsonArrayBuilder ret = Json.createArrayBuilder();
        locs.forEach((loc) -> {
            ret.add(loc.toJson());
        });

        return Response.ok(as.wrapSearchResult(req, ret)).build();

    }

}
