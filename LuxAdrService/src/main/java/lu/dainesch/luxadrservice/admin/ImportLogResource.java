package lu.dainesch.luxadrservice.admin;

import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import lu.dainesch.luxadrservice.base.ImportHandler;
import lu.dainesch.luxadrservice.base.ImportLog;

@Path("log")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ImportLogResource {
    
    @Inject
    private ImportHandler impHand;
    
    @GET
    public List<ImportLog> getLatestLog() {
        return impHand.getLatestLog();
    }
    
}
