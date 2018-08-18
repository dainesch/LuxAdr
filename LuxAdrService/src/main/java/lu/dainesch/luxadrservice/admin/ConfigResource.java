package lu.dainesch.luxadrservice.admin;

import java.util.List;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import lu.dainesch.luxadrdto.entity.PostCodeType;
import lu.dainesch.luxadrservice.adr.handler.BuildingHandler;
import lu.dainesch.luxadrservice.adr.handler.CoordinatesHandler;
import lu.dainesch.luxadrservice.base.ConfigHandler;
import lu.dainesch.luxadrservice.base.ConfigValue;
import lu.dainesch.luxadrservice.search.LuceneSingleton;

@Path("config")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ConfigResource {

    @Inject
    private ConfigHandler confHand;
    @Inject
    private BuildingHandler buildHand;
    @Inject
    private CoordinatesHandler coordHand;
    @Inject
    private LuceneSingleton lucene;

    @GET
    public List<ConfigValue> getConfig() {
        return confHand.getAll();
    }

    @POST
    public JsonObject saveConfig(List<ConfigValue> values) {

        for (ConfigValue v : values) {
            ConfigValue exist = confHand.getValue(v.getType());
            exist.setValue(v.getValue());
            confHand.save(exist);
        }

        return Json.createObjectBuilder()
                .add("info", "Config values saved")
                .build();
    }
    
    @GET
    @Path("status")
    public JsonObject getStatus() {
        return Json.createObjectBuilder()
                .add("hasBuildings", !buildHand.getBuildingsPaginated(0, 1, PostCodeType.Normal).isEmpty())
                .add("hasCoordinates", coordHand.hasCoordinates())
                .add("luceneEnabled", lucene.isEnabled())
                .add("hasIndex", lucene.hasData())
                .build();
    }

}
