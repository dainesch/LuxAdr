package lu.dainesch.luxadrservice.api;

import lu.dainesch.luxadrservice.api.dto.SearchRequest;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import lu.dainesch.luxadrservice.api.dto.GeoRequest;
import lu.dainesch.luxadrservice.base.Config;
import lu.dainesch.luxadrservice.base.ConfigType;
import lu.dainesch.luxadrservice.base.ConfigValue;

@ApplicationScoped
public class ApiService {

    public static final JsonArray EMPTY_ARR = Json.createArrayBuilder().build();

    @Inject
    @Config(ConfigType.MAX_SEARCH_RES)
    private ConfigValue maxSearchResults;
    @Inject
    @Config(ConfigType.MAX_SEARCH_RES)
    private ConfigValue maxDistKM;

    public boolean validateAndFix(SearchRequest req, boolean beginning) throws WebApplicationException {
        if (req == null || req.getValue() == null) {
            throw new WebApplicationException("Missing request body", Response.Status.BAD_REQUEST);
        }
        if (req.getMaxResults() > maxSearchResults.getInt() || req.getMaxResults() <= 0) {
            req.setMaxResults(maxSearchResults.getInt());
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
        if (req.getDistance() > maxDistKM.getFloat() || req.getDistance() <= 0) {
            req.setDistance(maxDistKM.getFloat());
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
