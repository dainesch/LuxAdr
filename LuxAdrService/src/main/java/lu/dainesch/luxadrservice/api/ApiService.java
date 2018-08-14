package lu.dainesch.luxadrservice.api;

import lu.dainesch.luxadrdto.SearchRequest;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import lu.dainesch.luxadrdto.GeoRequest;
import lu.dainesch.luxadrservice.base.Config;
import lu.dainesch.luxadrservice.base.ConfigType;
import lu.dainesch.luxadrservice.base.ConfigValue;

@RequestScoped
public class ApiService {

    public static final JsonArray EMPTY_ARR = Json.createArrayBuilder().build();

    @Inject
    @Config(ConfigType.MAX_SEARCH_RES)
    private ConfigValue maxSearchResults;
    @Inject
    @Config(ConfigType.MAX_SEARCH_RES)
    private ConfigValue maxDistKM;
    @Inject
    @Config(ConfigType.LUCENE_GEO_SEARCH)
    private ConfigValue useLuceneGeoSearch;

    public boolean validateAndFix(SearchRequest req) throws WebApplicationException {
        if (req == null || req.getValue() == null) {
            throw new WebApplicationException("Missing request body", Response.Status.BAD_REQUEST);
        }
        if (req.getMaxResults() > maxSearchResults.getInt() || req.getMaxResults() <= 0) {
            req.setMaxResults(maxSearchResults.getInt());
        }
        req.setValue(req.getValue().trim());

        return req.isValid();

    }

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
        if (req.getLucene() == null) {
            req.setLucene(useLuceneGeoSearch.getBoolean());
        }

        return true;
    }

    public String escapeLike(String in) {
        if (in == null) {
            return null;
        }
        return in.replace("_", "").replace("%", "").trim();
    }

}
