package lu.dainesch.luxadrservice.adr.handler;

import java.util.concurrent.Future;
import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import lu.dainesch.luxadrservice.adr.entity.Canton;
import lu.dainesch.luxadrservice.adr.entity.Commune;
import lu.dainesch.luxadrservice.base.Import;
import lu.dainesch.luxadrservice.input.FixedParser;

@Stateless
public class CommuneHandler extends ImportedEntityHandler<Commune> {

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
        ret.setCurrent(imp);

        if (ret.getId() == null) {
            em.persist(ret);
        }
        return ret;
    }

    @Asynchronous
    public Future<Boolean> importCommune(FixedParser.ParsedLine line, Import currentImport) {

        Commune comm = new Commune();
        comm.setCode(line.getInteger(0));
        comm.setName(line.getString(1));
        comm.setCanton(canHand.getByCode(line.getInteger(4)));

        createOrUpdate(comm, currentImport);

        return new AsyncResult<>(true);

    }

}
