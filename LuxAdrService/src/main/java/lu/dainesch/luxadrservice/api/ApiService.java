package lu.dainesch.luxadrservice.api;

import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.json.JsonArray;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

@ApplicationScoped
public class ApiService {

    public static final JsonArray EMPTY_ARR = Json.createArrayBuilder().build();
    private static final int MAX_SEARCH_RES = 20;

    public boolean validateAndFix(SearchRequest req) throws WebApplicationException {
        if (req == null || req.getValue() == null) {
            throw new WebApplicationException("Missing request body", Response.Status.BAD_REQUEST);
        }
        if (req.getMaxResults() > MAX_SEARCH_RES || req.getMaxResults() >= 0) {
            req.setMaxResults(MAX_SEARCH_RES);
        } 
        req.setValue(escapeLike(req.getValue()));
        
        return req.isValid();
         
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
