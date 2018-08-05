package lu.dainesch.luxadrservice.api;

import java.io.IOException;
import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import lu.dainesch.luxadrservice.base.Config;
import lu.dainesch.luxadrservice.base.ConfigType;
import lu.dainesch.luxadrservice.base.ConfigValue;

@Provider
public class CORSFilter implements ContainerResponseFilter {

    @Inject
    @Config(ConfigType.CORS_ORIGIN)
    private ConfigValue origin;

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        final MultivaluedMap<String, Object> headers = responseContext.getHeaders();
        headers.add("Access-Control-Allow-Origin", origin.getValue());
        headers.add("Access-Control-Allow-Headers", createHeaderList(requestContext.getHeaders().get("Access-Control-Allow-Headers"), "origin,accept,content-type"));
        headers.add("Access-Control-Expose-Headers", createHeaderList(requestContext.getHeaders().get("Access-Control-Expose-Headers"), "location,info"));
        headers.add("Access-Control-Allow-Credentials", "true");
        headers.add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
        headers.add("Access-Control-Max-Age", 42 * 60 * 60);

    }

    private String createHeaderList(List<String> headers, String defaultHeaders) {
        if (headers == null || headers.isEmpty()) {
            return defaultHeaders;
        }
        StringBuilder retVal = new StringBuilder();
        for (int i = 0; i < headers.size(); i++) {
            String header = (String) headers.get(i);
            retVal.append(header);
            retVal.append(',');
        }
        retVal.append(defaultHeaders);
        return retVal.toString();
    }

}
