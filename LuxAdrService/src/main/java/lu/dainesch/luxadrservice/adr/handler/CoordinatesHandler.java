package lu.dainesch.luxadrservice.adr.handler;

import java.util.concurrent.Future;
import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import lu.dainesch.luxadrservice.adr.entity.Building;
import lu.dainesch.luxadrservice.adr.entity.Coordinates;
import lu.dainesch.luxadrservice.input.GeoParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
public class CoordinatesHandler {

    private static final Logger LOG = LoggerFactory.getLogger(CoordinatesHandler.class);

    @PersistenceContext
    private EntityManager em;

    @Inject
    private BuildingHandler buildHand;

    @Asynchronous
    public Future<Boolean> importCoords(GeoParser.CoordImp imp) {

        Building build = buildHand.getByNumber(imp.getBuildingId());
        if (build == null) {
            LOG.warn("No match found for coordinates " + imp);
            return new AsyncResult<>(false);
        }
        Coordinates coords = build.getCoordinates();
        if (coords == null) {
            coords = new Coordinates();
            coords.setBuilding(build);
            build.setCoordinates(coords);
        }

        coords.setLatitude(imp.getLatitude());
        coords.setLongitude(imp.getLongitude());

        if (coords.getId() == null) {
            em.persist(build);
        }
        return new AsyncResult<>(true);
    }

}
