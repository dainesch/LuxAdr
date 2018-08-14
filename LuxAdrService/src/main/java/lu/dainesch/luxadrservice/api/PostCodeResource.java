package lu.dainesch.luxadrservice.api;

import lu.dainesch.luxadrdto.SearchRequest;
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
import lu.dainesch.luxadrservice.adr.entity.PostalCode;
import lu.dainesch.luxadrservice.adr.entity.Street;
import lu.dainesch.luxadrservice.adr.handler.PostCodeHandler;

@Path("postcode")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PostCodeResource {

    @Inject
    private ApiService as;
    @Inject
    private PostCodeHandler pcHand;

    @GET
    @Path("{id}")
    public Response getById(@PathParam("id") Long id) {
        PostalCode pc = pcHand.getById(id);
        if (pc == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(pc.toJson().build()).build();
    }

    @GET
    @Path("{id}/streets")
    public Response getStreets(@PathParam("id") Long id) {
        List<Street> streets = pcHand.getStreets(id);
        JsonArrayBuilder ret = Json.createArrayBuilder();
        streets.forEach((st) -> {
            ret.add(st.toJson(true));
        });

        return Response.ok(ret.build()).build();
    }

    @GET
    @Path("code/{code}")
    public Response getByCode(@PathParam("code") String code) {

        PostalCode pc = pcHand.getByCode(code);
        if (pc == null || !pc.isActive()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(pc.toJson().build()).build();

    }

    @GET
    @Path("code/{code}/streets")
    public Response getStreets(@PathParam("code") String code) {
        List<Street> streets = pcHand.getStreets(code);
        JsonArrayBuilder ret = Json.createArrayBuilder();
        streets.forEach((st) -> {
            ret.add(st.toJson(true));
        });

        return Response.ok(ret.build()).build();
    }

    @POST
    @Path("search")
    public Response search(SearchRequest req) {
        if (!as.validateAndFix(req, true)) {
            return as.emptyArrayResponse();
        }
        List<PostalCode> codes = pcHand.search(req);
        JsonArrayBuilder ret = Json.createArrayBuilder();
        codes.forEach((st) -> {
            ret.add(st.toJson());
        });

        return Response.ok(as.wrapSearchResult(req, ret)).build();

    }

}
