package lu.dainesch.luxadrservice.admin;

import java.util.Date;
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
import lu.dainesch.luxadrservice.adr.handler.BuildingHandler;
import lu.dainesch.luxadrservice.adr.handler.CoordinatesHandler;
import lu.dainesch.luxadrservice.base.AppProcess;
import lu.dainesch.luxadrservice.base.ConfigHandler;
import lu.dainesch.luxadrservice.base.ConfigType;
import lu.dainesch.luxadrservice.base.ConfigValue;
import lu.dainesch.luxadrservice.base.ProcessHandler;
import lu.dainesch.luxadrservice.base.ProcessingStep;
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
    @Inject
    private ProcessHandler procHand;

    @GET
    public List<ConfigValue> getConfig() {
        return confHand.getAll();
    }

    @POST
    public JsonObject saveConfig(List<ConfigValue> values) {

        boolean lucenceEnabled = confHand.getValue(ConfigType.LUCENE_ENABLED).getBoolean();

        for (ConfigValue v : values) {
            ConfigValue exist = confHand.getValue(v.getType());

            switch (v.getType()) {
                case LUCENE_DATA_DIR:
                    if (lucenceEnabled && v.getValue() != null && !v.getValue().equals(exist.getValue())) {
                        // data dir changed, switch over
                        lucene.init();
                    }
                    break;
                case LUCENE_ENABLED:
                    if (!lucenceEnabled && v.getBoolean()) {
                        // enable lucene
                        lucene.init();
                    }
                    break;

            }

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

        Date never = new Date(0);

        AppProcess build = procHand.getLastProcess(ProcessingStep.BUILDING);
        AppProcess geo = procHand.getLastProcess(ProcessingStep.GEODATA);
        AppProcess ind = procHand.getLastProcess(ProcessingStep.INDEXLUCENE);

        Date dBuild = build == null ? never : build.getEnd();
        Date dGeo = geo == null ? never : geo.getEnd();
        Date dInd = ind == null ? never : ind.getEnd();

        boolean requiresRebuild = (dGeo.after(dInd) || dBuild.after(dInd));

        return Json.createObjectBuilder()
                .add("buildingCount", buildHand.getBuildingCount())
                .add("coordCount", coordHand.getCoordCount())
                .add("luceneEnabled", lucene.isEnabled())
                .add("indexCount", lucene.indexCount())
                .add("requiresRebuild", requiresRebuild)
                .add("lastGeoImport", dGeo.getTime())
                .add("lastDataImport", dBuild.getTime())
                .add("lastIndexBuild", dInd.getTime())
                .build();
    }

}
