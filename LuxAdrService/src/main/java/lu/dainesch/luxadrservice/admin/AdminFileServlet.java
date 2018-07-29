package lu.dainesch.luxadrservice.admin;

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

        UploadType type = UploadType.valueOf(sType);

        try {
            switch (type) {
                case POSTALCODE:
                    impServ.updatePostCodes(file.getInputStream());
                    break;
                case DISTRICT:
                    impServ.updateDistricts(file.getInputStream());
                    break;
                case CANTON:
                    impServ.updateCantons(file.getInputStream());
                    break;
                case COMMUNE:
                    impServ.updateCommunes(file.getInputStream());
                    break;
                case LOCALITY:
                    impServ.updateLocalities(file.getInputStream());
                    break;
                case LOCALITY_ALT:
                    impServ.updateLocalityAltNames(file.getInputStream());
                    break;
                case QUARTER:
                    impServ.updateQuarters(file.getInputStream());
                    break;
                case STREET:
                    impServ.updateStreets(file.getInputStream());
                    break;
                case STREET_ALT:
                    impServ.updateStreetAltNames(file.getInputStream());
                    break;
                case BUILDING:
                    impServ.updateBuildings(file.getInputStream());
                    break;
                case BUILDING_DES:
                    impServ.updateBuildingsDesign(file.getInputStream());
                    break;
            }

        } catch (ImportException ex) {
            LOG.error("Error importing file", ex);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error importing file");
        }

    }

}
