package lu.dainesch.luxadrservice.adr.handler;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import lu.dainesch.luxadrservice.adr.entity.Canton;
import lu.dainesch.luxadrservice.base.Import;
import lu.dainesch.luxadrservice.base.ImportException;
import lu.dainesch.luxadrservice.base.ImportHandler;
import lu.dainesch.luxadrservice.input.FixedParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
public class CantonHandler {

    private static final Logger LOG = LoggerFactory.getLogger(PostCodeHandler.class);

    @PersistenceContext
    private EntityManager em;

    @Inject
    private ImportHandler impHand;
    @Inject
    private DistrictHandler distHand;

    public Canton getByCode(String code) {
        try {
            return em.createNamedQuery("canton.by.code", Canton.class)
                    .setParameter("code", code)
                    .getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    public Canton createOrUpdate(Canton can, Import imp) {
        Canton ret = getByCode(can.getCode());
        if (ret == null) {
            ret = new Canton();
            ret.setSince(imp);
        }
        ret.setActive(true);
        ret.setCode(can.getCode());
        ret.setName(can.getName());
        
        ret.setDistrict(can.getDistrict());
        ret.getDistrict().getCantons().add(ret);

        ret.setUntil(null);
        if (ret.getId() == null) {
            em.persist(ret);
        }
        return ret;
    }

    public void updateCantons(InputStream in) throws ImportException {

        try (FixedParser parser = new FixedParser(in, 2, 40, 10, 4, 1)) {
            if (!parser.hasNext()) {
                LOG.warn("Empty file given as input, aborting");
                return;
            }

            Import currentImport = impHand.createNewImport();
            int count = 0;

            invalidate();

            while (parser.hasNext()) {

                FixedParser.ParsedLine line = parser.next();
                Canton code = new Canton();
                code.setCode(line.getString(0));
                code.setName(line.getString(1));
                code.setDistrict(distHand.getByCode(line.getString(3)));

                createOrUpdate(code, currentImport);

                count++;
            }

            currentImport.setEnd(new Date());
            int deleted = postprocess(currentImport);

            LOG.info("Imported " + count + " cantons and deleted " + deleted);

        } catch (IOException ex) {
            throw new ImportException("Error during canton import", ex);
        }

    }

    private int invalidate() {
        return em.createNamedQuery("canton.invalidate").executeUpdate();

    }

    private int postprocess(Import imp) {
        return em.createNamedQuery("canton.deleted")
                .setParameter("imp", imp).executeUpdate();

    }
}
