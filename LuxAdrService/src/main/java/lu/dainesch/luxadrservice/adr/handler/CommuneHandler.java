package lu.dainesch.luxadrservice.adr.handler;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import lu.dainesch.luxadrservice.adr.entity.Canton;
import lu.dainesch.luxadrservice.adr.entity.Commune;
import lu.dainesch.luxadrservice.base.Import;
import lu.dainesch.luxadrservice.base.ImportException;
import lu.dainesch.luxadrservice.base.ImportHandler;
import lu.dainesch.luxadrservice.input.FixedParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
public class CommuneHandler extends ImportedEntityHandler<Commune> {

    private static final Logger LOG = LoggerFactory.getLogger(CommuneHandler.class);

    @Inject
    private ImportHandler impHand;
    @Inject
    private CantonHandler canHand;

    public CommuneHandler() {
        super(Commune.class);
    }

    public Commune getByCantonCode(Canton can, int code) {
        try {
            return em.createNamedQuery("commune.by.canton.code", Commune.class)
                    .setParameter("code", code)
                    .setParameter("can", can)
                    .getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    public Commune createOrUpdate(Commune comm, Import imp) {
        Commune ret = getByCantonCode(comm.getCanton(), comm.getCode());
        if (ret == null) {
            ret = new Commune();
            ret.setSince(imp);
        }
        ret.setActive(true);
        ret.setCode(comm.getCode());
        ret.setName(comm.getName());

        ret.setCanton(comm.getCanton());
        ret.getCanton().getCommunes().add(ret);

        ret.setUntil(null);
        if (ret.getId() == null) {
            em.persist(ret);
        }
        return ret;
    }

    public void updateCommunes(InputStream in) throws ImportException {

        try (FixedParser parser = new FixedParser(in, 2, 40, 40, 10, 2, 1)) {
            if (!parser.hasNext()) {
                LOG.warn("Empty file given as input, aborting");
                return;
            }

            Import currentImport = impHand.createNewImport();
            int count = 0;

            invalidate();

            while (parser.hasNext()) {

                FixedParser.ParsedLine line = parser.next();
                Commune comm = new Commune();
                comm.setCode(line.getInteger(0));
                comm.setName(line.getString(1));
                comm.setCanton(canHand.getByCode(line.getInteger(4)));

                createOrUpdate(comm, currentImport);

                count++;
            }

            currentImport.setEnd(new Date());
            int deleted = postprocess(currentImport);

            LOG.info("Imported " + count + " communes and deleted " + deleted);

        } catch (IOException ex) {
            throw new ImportException("Error during commune import", ex);
        }

    }

}
