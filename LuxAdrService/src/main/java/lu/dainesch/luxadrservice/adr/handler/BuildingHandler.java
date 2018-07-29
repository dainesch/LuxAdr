package lu.dainesch.luxadrservice.adr.handler;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import lu.dainesch.luxadrservice.adr.entity.Building;
import lu.dainesch.luxadrservice.adr.entity.HouseNumber;
import lu.dainesch.luxadrservice.base.Import;
import lu.dainesch.luxadrservice.base.ImportException;
import lu.dainesch.luxadrservice.base.ImportHandler;
import lu.dainesch.luxadrservice.input.FixedParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
public class BuildingHandler extends ImportedEntityHandler<Building> {

    private static final Logger LOG = LoggerFactory.getLogger(BuildingHandler.class);

    @Inject
    private ImportHandler impHand;
    @Inject
    private StreetHandler strHand;
    @Inject
    private QuarterHandler quaHand;
    @Inject
    private PostCodeHandler pcHand;

    public BuildingHandler() {
        super(Building.class);
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
        if (ret.getId() == null) {
            em.persist(ret);
        }
        return ret;
    }

    public void updateBuildings(InputStream in) throws ImportException {

        try (FixedParser parser = new FixedParser(in, 8, 3, 6, 10, 1, 10, 4, 1, 5, 1, 5, 1, 1, 1)) {
            if (!parser.hasNext()) {
                LOG.warn("Empty file given as input, aborting");
                return;
            }

            Import currentImport = impHand.createNewImport();
            int count = 0;

            invalidate();

            while (parser.hasNext()) {

                FixedParser.ParsedLine line = parser.next();
                //LOG.info(line.toString());
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

                    count++;
                }
                if (count % 1000 == 0) {
                    LOG.info("Processing line " + count);
                }
            }

            currentImport.setEnd(new Date());
            int deleted = postprocess(currentImport);

            LOG.info("Imported " + count + " buildings and deleted " + deleted);

        } catch (IOException ex) {
            throw new ImportException("Error during building import", ex);
        }

    }

    public void updateBuildingsDesign(InputStream in) throws ImportException {

        try (FixedParser parser = new FixedParser(in, 8, 3, 6, 10, 1, 10, 4, 1, 5, 1, 5, 1, 1, 1, 40, 1)) {
            if (!parser.hasNext()) {
                LOG.warn("Empty file given as input, aborting");
                return;
            }
            int count = 0;

            while (parser.hasNext()) {

                FixedParser.ParsedLine line = parser.next();

                Building b = getByNumber(line.getInteger(0));
                if (b == null) {
                    continue;
                }

                Date valid = line.getDate(4);
                if (!line.getBoolean(12) && !line.getBoolean(13)
                        && (valid == null || valid.after(new Date()))) {

                    b.setName(line.getString(14));
                    count++;
                }
            }

            LOG.info("Imported " + count + " buildings names ");

        } catch (IOException ex) {
            throw new ImportException("Error during building name import", ex);
        }

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
                int max = Integer.parseInt(mult.substring(1));
                for (int n = num; n <= max; n++) {
                    HouseNumber ret = new HouseNumber(String.valueOf(n));
                    b.getNumbers().add(ret);
                }
            } catch (NumberFormatException ex) {
                LOG.error("Error parsing house number, expected -number got " + mult);
            }
        }

    }

}
