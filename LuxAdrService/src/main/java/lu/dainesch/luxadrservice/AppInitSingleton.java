package lu.dainesch.luxadrservice;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import lu.dainesch.luxadrservice.base.ConfigHandler;

@Startup
@Singleton
public class AppInitSingleton {

    @Inject
    private ConfigHandler cfgHand;

    @PostConstruct
    public void init() {
        cfgHand.initConfig();
    }
}
