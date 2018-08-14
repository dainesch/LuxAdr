package lu.dainesch.luxadrservice.api;

import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("api/v1/")
public class RestApplication extends Application {

    private final Set<Class<?>> resources = new HashSet<>();

    public RestApplication() {
        resources.add(OpenApiResource.class);
        //
        resources.add(BuildingResource.class);
        resources.add(GeoResource.class);
        resources.add(LocalityResource.class);
        resources.add(NumberResource.class);
        resources.add(PostCodeResource.class);
        resources.add(SearchResource.class);
        resources.add(StreetResource.class);
    }

    @Override
    public Set<Class<?>> getClasses() {
        return resources;
    }

}
