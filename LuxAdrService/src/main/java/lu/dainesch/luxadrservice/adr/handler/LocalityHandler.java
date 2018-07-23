package lu.dainesch.luxadrservice.adr.handler;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import lu.dainesch.luxadrservice.adr.entity.AlternateName;
import lu.dainesch.luxadrservice.adr.entity.Locality;
import lu.dainesch.luxadrservice.base.Import;
import lu.dainesch.luxadrservice.base.ImportException;
import lu.dainesch.luxadrservice.base.ImportHandler;
import lu.dainesch.luxadrservice.input.FixedParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
public class LocalityHandler extends ImportedEntityHandler<Locality> {

    private static final Logger LOG = LoggerFactory.getLogger(LocalityHandler.class);

    @Inject
    private ImportHandler impHand;
    @Inject
    private CantonHandler canHand;
    @Inject
    private CommuneHandler commHand;

    public LocalityHandler() {
        super(Locality.class);
    }

    public Locality getByNumber(int num) {
        try {
            return em.createNamedQuery("locality.by.number", Locality.class)
                    .setParameter("num", num)
                    .getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    public Locality createOrUpdate(Locality loc, Import imp) {
        Locality ret = getByNumber(loc.getNumber());
        if (ret == null) {
            ret = new Locality();
            ret.setSince(imp);
        }
        ret.setActive(true);
        ret.setNumber(loc.getNumber());
        ret.setCity(loc.isCity());
        ret.setCode(loc.getCode());
        ret.setName(loc.getName());

        ret.setCommune(loc.getCommune());
        ret.getCommune().getLocalities().add(ret);

        ret.setUntil(null);
        if (ret.getId() == null) {
            em.persist(ret);
        }
        return ret;
    }

    public void updateLocalities(InputStream in) throws ImportException {

        try (FixedParser parser = new FixedParser(in, 5, 40, 40, 2, 1, 10, 1, 10, 2, 1, 2, 1)) {
            if (!parser.hasNext()) {
                LOG.warn("Empty file given as input, aborting");
                return;
            }

            Import currentImport = impHand.createNewImport();
            int count = 0;

            invalidate();

            while (parser.hasNext()) {

                FixedParser.ParsedLine line = parser.next();
                Locality loc = new Locality();
                loc.setNumber(line.getInteger(0));
                loc.setName(line.getString(1));
                loc.setCode(line.getInteger(3));
                loc.setCity(line.getBoolean(4));

                loc.setCommune(commHand.getByCantonCode(canHand.getByCode(line.getInteger(8)), line.getInteger(10)));

                Date valid = line.getDate(5);
                if (valid == null || valid.before(new Date())) {
                    createOrUpdate(loc, currentImport);
                    count++;
                }
            }

            currentImport.setEnd(new Date());
            int deleted = postprocess(currentImport);

            LOG.info("Imported " + count + " localities and deleted " + deleted);

        } catch (IOException ex) {
            throw new ImportException("Error during locality import", ex);
        }

    }

    public void updateAltNames(InputStream in) throws ImportException {

        try (FixedParser parser = new FixedParser(in, 3, 40, 40, 1, 10, 5)) {
            if (!parser.hasNext()) {
                LOG.warn("Empty file given as input, aborting");
                return;
            }

            int deleted = deleteAltNames();
            int count = 0;


            while (parser.hasNext()) {

                FixedParser.ParsedLine line = parser.next();

                AlternateName n = new AlternateName(line.getLanguage(3), line.getString(1));

                Locality loc = getByNumber(line.getInteger(5));
                if (loc != null) {
                    n.setLocality(loc);
                    loc.getAltNames().clear(); // just to be sure
                    loc.getAltNames().add(n);
                    count++;
                }

            }
            

            LOG.info("Imported " + count + " localities names and deleted " + deleted);

        } catch (IOException ex) {
            throw new ImportException("Error during locality name import", ex);
        }

    }
    
    private int deleteAltNames() {
        return em.createNamedQuery("alternatename.del.loc").executeUpdate();
    }

}
