package lu.dainesch.luxadrservice.adr.handler;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.regex.Pattern;
import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import lu.dainesch.luxadrservice.adr.entity.Building;
import lu.dainesch.luxadrservice.adr.entity.HouseNumber;
import lu.dainesch.luxadrservice.base.Import;
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

    public Building createOrUpdate(Building bui, Import imp) {
        Building ret = getByNumber(bui.getNumber());
        if (ret == null) {
            ret = new Building();
            ret.setSince(imp);
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
        ret.setCurrent(imp);

        if (ret.getId() == null) {
            em.persist(ret);
        }
        return ret;
    }

    @Asynchronous
    @Override
    public Future<Boolean> importLine(FixedParser.ParsedLine line, Import currentImport) {

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
            createOrUpdate(bui, currentImport);

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
