package lu.dainesch.luxadrservice.adr.handler;

import java.util.concurrent.Future;
import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import lu.dainesch.luxadrservice.adr.entity.Canton;
import lu.dainesch.luxadrservice.base.Import;
import lu.dainesch.luxadrservice.input.FixedParser;

@Stateless
public class CantonHandler extends ImportedEntityHandler<Canton> {

    @Inject
    private DistrictHandler distHand;

    public CantonHandler() {
        super(Canton.class);
    }

    public Canton getByCode(int code) {
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
        ret.setCurrent(imp);

        if (ret.getId() == null) {
            em.persist(ret);
        }
        return ret;
    }

    @Asynchronous
    public Future<Boolean> importCanton(FixedParser.ParsedLine line, Import currentImport) {

        Canton cant = new Canton();
        cant.setCode(line.getInteger(0));
        cant.setName(line.getString(1));
        cant.setDistrict(distHand.getByCode(line.getString(3)));

        createOrUpdate(cant, currentImport);
        return new AsyncResult<>(true);

    }

}
