package lu.dainesch.luxadrservice.admin;

import lu.dainesch.luxadrservice.base.ImportStep;
import java.io.IOException;
import java.util.Scanner;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import lu.dainesch.luxadrservice.adr.BatchImportService;
import lu.dainesch.luxadrservice.base.ImportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@MultipartConfig
@WebServlet("/admin/upload")
public class AdminFileServlet extends HttpServlet {

    private static final Logger LOG = LoggerFactory.getLogger(AdminFileServlet.class);

    @Inject
    private BatchImportService impServ;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String sType = new Scanner(req.getPart("type").getInputStream()).nextLine();
        Part file = req.getPart("file");

        try {
            if ("ALL".equalsIgnoreCase(sType)) {
                impServ.updateAll(file.getInputStream());
            } else if ("GEO".equals(sType)) {
                impServ.importGeodata(file.getInputStream());
            } else {
                ImportStep type = ImportStep.valueOf(sType);
                impServ.update(null, type, file.getInputStream());
            }

        } catch (ImportException ex) {
            LOG.error("Error importing file", ex);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error importing file");
        }

    }

}
