package lu.dainesch.luxadrservice.api;

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
import lu.dainesch.luxadrservice.adr.entity.HouseNumber;
import lu.dainesch.luxadrservice.adr.entity.PostalCode;
import lu.dainesch.luxadrservice.adr.entity.Street;
import lu.dainesch.luxadrservice.adr.handler.StreetHandler;

@Path("street")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class StreetResource {

    @Inject
    private ApiService as;
    @Inject
    private StreetHandler strHand;

    @GET
    @Path("{id}")
    public Response getById(@PathParam("id") Long id) {
        Street str = strHand.getById(id);
        if (str == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(str.toJson(true).build()).build();
    }

    @GET
    @Path("{id}/postcodes")
    public Response getPostCodes(@PathParam("id") Long id) {
        List<PostalCode> streets = strHand.getPostCodes(id);
        JsonArrayBuilder ret = Json.createArrayBuilder();
        streets.forEach((st) -> {
            ret.add(st.toJson());
        });

        return Response.ok(ret.build()).build();
    }

    @GET
    @Path("{id}/numbers")
    public Response getNumbers(@PathParam("id") Long id) {
        List<HouseNumber> nums = strHand.getHouseNumbers(id);
        JsonArrayBuilder ret = Json.createArrayBuilder();
        nums.forEach((st) -> {
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
        List<Street> streets = strHand.search(req);
        JsonArrayBuilder ret = Json.createArrayBuilder();
        streets.forEach((str) -> {
            ret.add(str.toJson(true));
        });

        return Response.ok(as.wrapSearchResult(req, ret)).build();

    }

}
