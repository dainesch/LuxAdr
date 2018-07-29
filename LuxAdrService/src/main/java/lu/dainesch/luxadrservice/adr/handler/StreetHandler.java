package lu.dainesch.luxadrservice.adr.handler;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import lu.dainesch.luxadrservice.adr.entity.AlternateName;
import lu.dainesch.luxadrservice.adr.entity.Street;
import lu.dainesch.luxadrservice.base.Import;
import lu.dainesch.luxadrservice.base.ImportException;
import lu.dainesch.luxadrservice.base.ImportHandler;
import lu.dainesch.luxadrservice.input.FixedParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
public class StreetHandler extends ImportedEntityHandler<Street> {

    private static final Logger LOG = LoggerFactory.getLogger(StreetHandler.class);

    @Inject
    private ImportHandler impHand;
    @Inject
    private LocalityHandler locHand;

    public StreetHandler() {
        super(Street.class);
    }

    public Street getByNumber(int num) {
        try {
            return em.createNamedQuery("street.by.num", Street.class)
                    .setParameter("num", num)
                    .getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    public Street createOrUpdate(Street st, Import imp) {
        Street ret = getByNumber(st.getNumber());
        if (ret == null) {
            ret = new Street();
            ret.setSince(imp);
        }
        ret.setActive(true);
        ret.setNumber(st.getNumber());
        ret.setName(st.getName());
        ret.setSortValue(st.getSortValue());
        ret.setStreetCode(st.getStreetCode());
        ret.setTemporary(st.isTemporary());

        ret.setLocality(st.getLocality());
        ret.getLocality().getStreets().add(ret);

        ret.setUntil(null);
        if (ret.getId() == null) {
            em.persist(ret);
        }
        return ret;
    }

    public void updateStreets(InputStream in) throws ImportException {

        try (FixedParser parser = new FixedParser(in, 5, 40, 40, 10, 5, 1, 1, 10, 1, 10, 2, 1, 4, 1, 5, 1, 1, 30)) {
            if (!parser.hasNext()) {
                LOG.warn("Empty file given as input, aborting");
                return;
            }

            Import currentImport = impHand.createNewImport();
            int count = 0;

            invalidate();

            while (parser.hasNext()) {

                FixedParser.ParsedLine line = parser.next();
                Street s = new Street();
                s.setNumber(line.getInteger(0));
                s.setName(line.getString(1));
                s.setSortValue(line.getString(3));

                if (!line.getString(10).isEmpty()) {
                    s.setStreetCode(line.getString(10) + " " + line.getString(12));
                }

                s.setLocality(locHand.getByNumber(line.getInteger(14)));
                s.setTemporary(line.getBoolean(16));

                Date valid = line.getDate(7);
                if (valid == null || valid.after(new Date())) {
                    createOrUpdate(s, currentImport);

                    count++;
                }
            }

            currentImport.setEnd(new Date());
            int deleted = postprocess(currentImport);

            LOG.info("Imported " + count + " streets and deleted " + deleted);

        } catch (IOException ex) {
            throw new ImportException("Error during street import", ex);
        }

    }

    public void updateAltNames(InputStream in) throws ImportException {

        try (FixedParser parser = new FixedParser(in, 3, 40, 40, 1, 10, 5, 80, 1)) {
            if (!parser.hasNext()) {
                LOG.warn("Empty file given as input, aborting");
                return;
            }

            int deleted = deleteAltNames();
            int count = 0;

            while (parser.hasNext()) {

                FixedParser.ParsedLine line = parser.next();

                AlternateName n = new AlternateName(line.getLanguage(3), line.getString(1));

                Street str = getByNumber(line.getInteger(5));
                if (str != null) {
                    n.setStreet(str);
                    str.getAltNames().clear(); // just to be sure
                    str.getAltNames().add(n);
                    count++;
                }

            }

            LOG.info("Imported " + count + " street names and deleted " + deleted);

        } catch (IOException ex) {
            throw new ImportException("Error during street name import", ex);
        }

    }

    private int deleteAltNames() {
        return em.createNamedQuery("alternatename.del.str").executeUpdate();
    }

}
