package lu.dainesch.luxadrservice.admin;

import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import lu.dainesch.luxadrservice.base.ProcessHandler;
import lu.dainesch.luxadrservice.base.ProcessingLog;

@Path("log")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProcessLogResource {
    
    @Inject
    private ProcessHandler impHand;
    
    @GET
    public List<ProcessingLog> getLatestLog() {
        return impHand.getLatestLog();
    }
    
}
