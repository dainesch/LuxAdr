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
import lu.dainesch.luxadrservice.adr.handler.BuildingHandler;
import lu.dainesch.luxadrservice.adr.handler.CantonHandler;
import lu.dainesch.luxadrservice.adr.handler.CommuneHandler;
import lu.dainesch.luxadrservice.adr.handler.DistrictHandler;
import lu.dainesch.luxadrservice.adr.handler.LocalityHandler;
import lu.dainesch.luxadrservice.adr.handler.PostCodeHandler;
import lu.dainesch.luxadrservice.adr.handler.QuarterHandler;
import lu.dainesch.luxadrservice.adr.handler.StreetHandler;
import lu.dainesch.luxadrservice.base.ImportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@MultipartConfig
@WebServlet("/admin/upload")
public class AdminFileServlet extends HttpServlet {

    private static final Logger LOG = LoggerFactory.getLogger(AdminFileServlet.class);

    @Inject
    private PostCodeHandler codeHand;
    @Inject
    private DistrictHandler distHand;
    @Inject
    private CantonHandler canHand;
    @Inject
    private CommuneHandler commHand;
    @Inject
    private LocalityHandler locHand;
    @Inject
    private QuarterHandler quaHand;
    @Inject
    private StreetHandler strHand;
    @Inject
    private BuildingHandler buildHand;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String sType = new Scanner(req.getPart("type").getInputStream()).nextLine();
        Part file = req.getPart("file");

        UploadType type = UploadType.valueOf(sType);

        try {
            switch (type) {
                case POSTALCODE:
                    codeHand.updatePostCodes(file.getInputStream());
                    break;
                case DISTRICT:
                    distHand.updateDistricts(file.getInputStream());
                    break;
                case CANTON:
                    canHand.updateCantons(file.getInputStream());
                    break;
                case COMMUNE:
                    commHand.updateCommunes(file.getInputStream());
                    break;
                case LOCALITY:
                    locHand.updateLocalities(file.getInputStream());
                    break;
                case LOCALITY_ALT:
                    locHand.updateAltNames(file.getInputStream());
                    break;
                case QUARTER:
                    quaHand.updateQuarters(file.getInputStream());
                    break;
                case STREET:
                    strHand.updateStreets(file.getInputStream());
                    break;
                case STREET_ALT:
                    strHand.updateAltNames(file.getInputStream());
                    break;
                case BUILDING:
                    buildHand.updateBuildings(file.getInputStream());
                    break;
                case BUILDING_DES:
                    buildHand.updateBuildingsDesign(file.getInputStream());
                    break;
            }
            
        } catch (ImportException ex) {
            LOG.error("Error importing file", ex);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error importing file");
        }

    }

}
