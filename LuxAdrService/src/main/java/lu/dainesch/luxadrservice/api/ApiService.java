package lu.dainesch.luxadrservice.api;

import lu.dainesch.luxadrservice.api.dto.SearchRequest;
import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import lu.dainesch.luxadrservice.api.dto.GeoRequest;

@ApplicationScoped
public class ApiService {

    public static final JsonArray EMPTY_ARR = Json.createArrayBuilder().build();
    private static final int MAX_SEARCH_RES = 20;
    private static final float MAX_DIST_KM = 0.2f;

    public boolean validateAndFix(SearchRequest req, boolean beginning) throws WebApplicationException {
        if (req == null || req.getValue() == null) {
            throw new WebApplicationException("Missing request body", Response.Status.BAD_REQUEST);
        }
        if (req.getMaxResults() > MAX_SEARCH_RES || req.getMaxResults() <= 0) {
            req.setMaxResults(MAX_SEARCH_RES);
        }
        if (req.isBeginning() == null) {
            req.setBeginning(beginning);
        }
        req.setValue((req.isBeginning() ? "" : "%") + escapeLike(req.getValue().toLowerCase()) + "%");

        return req.isValid();

    }

    public boolean validateAndFix(GeoRequest req) throws WebApplicationException {
        if (req == null) {
            throw new WebApplicationException("Missing request body", Response.Status.BAD_REQUEST);
        }
        if (req.getDistance()> MAX_DIST_KM || req.getDistance() <= 0) {
            req.setDistance(MAX_DIST_KM);
        }

        return true;
    }

    public JsonObject wrapSearchResult(SearchRequest req, JsonArrayBuilder b) {
        JsonArray arr = b.build();
        return Json.createObjectBuilder()
                .add("corrId", req.getCorrId())
                .add("count", arr.size())
                .add("results", arr)
                .build();
    }

    public String escapeLike(String in) {
        if (in == null) {
            return null;
        }
        return in.replace("_", "").replace("%", "").trim();
    }

    public Response emptyArrayResponse() {
        return Response.ok(EMPTY_ARR).build();
    }
}
