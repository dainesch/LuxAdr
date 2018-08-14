package lu.dainesch.luxadrservice.admin;

import java.io.IOException;
import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lu.dainesch.luxadrservice.base.ConfigHandler;
import lu.dainesch.luxadrservice.base.ConfigType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebFilter("/admin/*")
public class AdminAuthFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(AdminAuthFilter.class);

    private static final String AUTH_HEADER = "AccessKey";

    @Inject
    private ConfigHandler confHand;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (!confHand.getValue(ConfigType.ADMIN_ACCESS_CHECK).getBoolean()) {
            chain.doFilter(request, response);
            return;
        }
        if (request instanceof HttpServletRequest) {
            HttpServletRequest req = (HttpServletRequest) request;
            HttpServletResponse resp = (HttpServletResponse) response;

            if (confHand.getValue(ConfigType.ADMIN_ACCESS_KEY).getValue().equals(req.getHeader(AUTH_HEADER))) {
                chain.doFilter(request, response);
                return;
            }

            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid access key provided");
        }
        LOG.warn("Unauthorized request on admin by " + request.getRemoteAddr());

    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void destroy() {
        
    }
    
    

}
