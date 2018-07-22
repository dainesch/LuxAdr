package lu.dainesch.luxadrservice.adr.handler;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import lu.dainesch.luxadrservice.adr.entity.District;
import lu.dainesch.luxadrservice.base.Import;
import lu.dainesch.luxadrservice.base.ImportException;
import lu.dainesch.luxadrservice.base.ImportHandler;
import lu.dainesch.luxadrservice.input.FixedParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
public class DistrictHandler {

    private static final Logger LOG = LoggerFactory.getLogger(PostCodeHandler.class);

    @PersistenceContext
    private EntityManager em;

    @Inject
    private ImportHandler impHand;

    public District getByCode(String code) {
        try {
            return em.createNamedQuery("district.by.code", District.class)
                    .setParameter("code", code)
                    .getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    public District createOrUpdate(District distr, Import imp) {
        District ret = getByCode(distr.getCode());
        if (ret == null) {
            ret = new District();
            ret.setSince(imp);
        }
        ret.setActive(true);
        ret.setCode(distr.getCode());
        ret.setName(distr.getName());
        ret.setUntil(null);
        if (ret.getId() == null) {
            em.persist(ret);
        }
        return ret;
    }

    public void updateDistricts(InputStream in) throws ImportException {

        try (FixedParser parser = new FixedParser(in, 4, 40, 10)) {
            if (!parser.hasNext()) {
                LOG.warn("Empty file given as input, aborting");
                return;
            }

            Import currentImport = impHand.createNewImport();
            int count = 0;

            invalidate();

            while (parser.hasNext()) {

                FixedParser.ParsedLine line = parser.next();
                District code = new District();
                code.setCode(line.getString(0));
                code.setName(line.getString(1));

                createOrUpdate(code, currentImport);

                count++;
            }

            currentImport.setEnd(new Date());
            int deleted = postprocess(currentImport);

            LOG.info("Imported " + count + " districts and deleted " + deleted);

        } catch (IOException ex) {
            throw new ImportException("Error during district import", ex);
        }

    }

    private int invalidate() {
        return em.createNamedQuery("district.invalidate").executeUpdate();

    }

    private int postprocess(Import imp) {
        return em.createNamedQuery("district.deleted")
                .setParameter("imp", imp).executeUpdate();

    }
}
