package lu.dainesch.luxadrservice.adr.handler;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import lu.dainesch.luxadrservice.adr.entity.Quarter;
import lu.dainesch.luxadrservice.base.Import;
import lu.dainesch.luxadrservice.base.ImportException;
import lu.dainesch.luxadrservice.base.ImportHandler;
import lu.dainesch.luxadrservice.input.FixedParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
public class QuarterHandler extends ImportedEntityHandler<Quarter> {

    private static final Logger LOG = LoggerFactory.getLogger(QuarterHandler.class);

    @Inject
    private ImportHandler impHand;
    @Inject
    private LocalityHandler locHand;

    public QuarterHandler() {
        super(Quarter.class);
    }

    public Quarter getByNumber(int num) {
        try {
            return em.createNamedQuery("quarter.by.num", Quarter.class)
                    .setParameter("num", num)
                    .getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    public Quarter createOrUpdate(Quarter qua, Import imp) {
        Quarter ret = getByNumber(qua.getNumber());
        if (ret == null) {
            ret = new Quarter();
            ret.setSince(imp);
        }
        ret.setActive(true);
        ret.setNumber(qua.getNumber());
        ret.setName(qua.getName());

        ret.setLocality(qua.getLocality());
        ret.getLocality().getQuarters().add(ret);

        ret.setUntil(null);
        if (ret.getId() == null) {
            em.persist(ret);
        }
        return ret;
    }

    public void updateQuarters(InputStream in) throws ImportException {

        try (FixedParser parser = new FixedParser(in,5,40,10,5,1)) {
            if (!parser.hasNext()) {
                LOG.warn("Empty file given as input, aborting");
                return;
            }

            Import currentImport = impHand.createNewImport();
            int count = 0;

            invalidate();

            while (parser.hasNext()) {

                FixedParser.ParsedLine line = parser.next();
                Quarter cant = new Quarter();
                cant.setNumber(line.getInteger(0));
                cant.setName(line.getString(1));
                cant.setLocality(locHand.getByNumber(line.getInteger(3)));

                createOrUpdate(cant, currentImport);

                count++;
            }

            currentImport.setEnd(new Date());
            int deleted = postprocess(currentImport);

            LOG.info("Imported " + count + " quarters and deleted " + deleted);

        } catch (IOException ex) {
            throw new ImportException("Error during quarter import", ex);
        }

    }

}
