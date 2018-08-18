package lu.dainesch.luxadrservice.adr.handler;

import java.awt.geom.Rectangle2D;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.regex.Pattern;
import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import lu.dainesch.luxadrdto.entity.PostCodeType;
import lu.dainesch.luxadrservice.GeoUtil;
import lu.dainesch.luxadrservice.adr.entity.Building;
import lu.dainesch.luxadrservice.adr.entity.HouseNumber;
import lu.dainesch.luxadrservice.base.AppProcess;
import lu.dainesch.luxadrservice.input.FixedParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
public class BuildingHandler extends ImportedEntityHandler<Building> {

    private static final Logger LOG = LoggerFactory.getLogger(BuildingHandler.class);
    private static final Pattern ALPHA_END = Pattern.compile("^-[0-9]+[A-Z]$");

    @Inject
    private StreetHandler strHand;
    @Inject
    private QuarterHandler quaHand;
    @Inject
    private PostCodeHandler pcHand;

    public BuildingHandler() {
        super(Building.class);
    }

    @Override
    public int[] getLineFormat() {
        return new int[]{8, 3, 6, 10, 1, 10, 4, 1, 5, 1, 5, 1, 1, 1};
    }

    @Override
    public int[] getAltLineFormat() {
        return new int[]{8, 3, 6, 10, 1, 10, 4, 1, 5, 1, 5, 1, 1, 1, 40, 1};
    }

    public Building getByNumber(int num) {
        try {
            return em.createNamedQuery("building.by.num", Building.class)
                    .setParameter("num", num)
                    .getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    public Building getNearest(float lat, float lon, float distance) {

        Rectangle2D.Float r = GeoUtil.getBoundingBox(lat, lon, distance);

        List<Building> ret = em.createNamedQuery("building.geo.rect", Building.class)
                .setParameter("minLat", r.getMinX())
                .setParameter("maxLat", r.getMaxX())
                .setParameter("minLon", r.getMinY())
                .setParameter("maxLon", r.getMaxY())
                .setParameter("lat", lat)
                .setParameter("lon", lon)
                .setParameter("cos", Math.cos(Math.toRadians(lat)))
                .setMaxResults(5)
                .getResultList();

        if (ret.isEmpty()) {
            return null;
        }

        Collections.sort(ret, GeoUtil.getNearestBuildingComp(lat, lon));

        return ret.get(0);
    }

    public List<Building> getInRange(float lat, float lon, float distance) {
        
        Rectangle2D.Float r = GeoUtil.getBoundingBox(lat, lon, distance);

        List<Building> ret = em.createNamedQuery("building.geo.rect", Building.class)
                .setParameter("minLat", r.getMinX())
                .setParameter("maxLat", r.getMaxX())
                .setParameter("minLon", r.getMinY())
                .setParameter("maxLon", r.getMaxY())
                .setParameter("lat", lat)
                .setParameter("lon", lon)
                .setParameter("cos", Math.cos(Math.toRadians(lat)))
                .getResultList();
        return ret;

    }

    public List<Building> getBuildingsPaginated(int start, int count, PostCodeType type) {
        return em.createNamedQuery("building.all.active.type", Building.class)
                .setParameter("type", type)
                .setFirstResult(start)
                .setMaxResults(count)
                .getResultList();
    }

    public List<Building> getBuildingsRangeByIds(List<Long> ids) {
        return em.createNamedQuery("building.by.ids", Building.class)
                .setParameter("ids", ids)
                .getResultList();

    }

    public Building createOrUpdate(Building bui, AppProcess proc) {
        Building ret = getByNumber(bui.getNumber());
        if (ret == null) {
            ret = new Building();
            ret.setSince(proc);
        }
        ret.setActive(true);
        ret.setNumber(bui.getNumber());
        ret.setName(bui.getName());

        Set<HouseNumber> exist = new HashSet<>(ret.getNumbers());
        for (HouseNumber num : bui.getNumbers()) {
            if (!ret.getNumbers().contains(num)) {
                ret.getNumbers().add(num);
                num.setBuilding(ret);
            }
            exist.remove(num);
        }
        ret.getNumbers().removeAll(exist);

        ret.setPostalCode(bui.getPostalCode());
        if (ret.getPostalCode() != null) {
            ret.getPostalCode().getBuildings().add(ret);
        }
        ret.setStreet(bui.getStreet());
        if (ret.getStreet() != null) {
            ret.getStreet().getBuildings().add(ret);
        }
        ret.setQuarter(bui.getQuarter());
        if (ret.getQuarter() != null) {
            ret.getQuarter().getBuildings().add(ret);
        }

        ret.setUntil(null);
        ret.setCurrent(proc);

        if (ret.getId() == null) {
            em.persist(ret);
        }
        return ret;
    }

    @Asynchronous
    @Override
    public Future<Boolean> importLine(FixedParser.ParsedLine line, AppProcess currentProc) {

        Building bui = new Building();
        bui.setNumber(line.getInteger(0));
        bui.setPostalCode(pcHand.getByCode(line.getString(6)));
        if (line.getInteger(8) != null) {
            bui.setQuarter(quaHand.getByNumber(line.getInteger(8)));
        }
        bui.setStreet(strHand.getByNumber(line.getInteger(10)));

        Date valid = line.getDate(4);
        if (!line.getBoolean(12) && !line.getBoolean(13)
                && (valid == null || valid.after(new Date()))) {

            setNumbers(bui, line.getInteger(1), line.getString(2));
            createOrUpdate(bui, currentProc);

            return new AsyncResult<>(true);
        }
        return new AsyncResult<>(false);
    }

    @Asynchronous
    @Override
    public Future<Boolean> importAltName(FixedParser.ParsedLine line) {

        Building b = getByNumber(line.getInteger(0));
        if (b != null) {

            Date valid = line.getDate(4);
            if (!line.getBoolean(12) && !line.getBoolean(13)
                    && (valid == null || valid.after(new Date()))) {

                b.setName(line.getString(14));
                return new AsyncResult<>(true);
            }
        }
        return new AsyncResult<>(false);

    }

    @Override
    public int deleteAltNames() {
        //noop
        return 0;
    }

    private void setNumbers(Building b, int num, String mult) {
        if (mult == null || mult.isEmpty() || "-..".equals(mult)) {
            // no multiple numbers or undefined
            HouseNumber ret = new HouseNumber(String.valueOf(num));
            b.getNumbers().add(ret);

        } else if (!mult.startsWith("-")) {
            HouseNumber ret = new HouseNumber(String.valueOf(num) + mult);
            b.getNumbers().add(ret);

        } else {
            try {
                String alph = null;
                if (ALPHA_END.matcher(mult).matches()) {
                    alph = mult.substring(mult.length() - 2);
                    mult = mult.substring(0, mult.length() - 2);
                }

                int max = Integer.parseInt(mult.substring(1));
                for (int n = num; n <= max; n++) {
                    HouseNumber ret = new HouseNumber(String.valueOf(n));
                    b.getNumbers().add(ret);
                }
                if (alph != null) {
                    HouseNumber ret = new HouseNumber(String.valueOf(max) + alph);
                    b.getNumbers().add(ret);
                    LOG.warn("Adding aplha ended range number " + ret.getNumber());
                }
            } catch (NumberFormatException ex) {
                LOG.error("Error parsing house number, expected -number got " + mult);
            }
        }

    }

}
