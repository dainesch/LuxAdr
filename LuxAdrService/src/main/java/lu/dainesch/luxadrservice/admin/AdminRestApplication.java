package lu.dainesch.luxadrservice.admin;

import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("admin/api/")
public class AdminRestApplication extends Application {

    private final Set<Class<?>> resources = new HashSet<>();

    public AdminRestApplication() {
        resources.add(OpenApiResource.class);
        //
        resources.add(ActionsResource.class);
        resources.add(ConfigResource.class);
        resources.add(ProcessLogResource.class);

    }

    @Override
    public Set<Class<?>> getClasses() {
        return resources;
    }

}
