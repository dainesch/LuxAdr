package lu.dainesch.luxadrservice.adr.handler;

import java.util.concurrent.Future;
import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import lu.dainesch.luxadrservice.adr.entity.Canton;
import lu.dainesch.luxadrservice.base.AppProcess;
import lu.dainesch.luxadrservice.input.FixedParser;

@Stateless
public class CantonHandler extends ImportedEntityHandler<Canton> {

    @Inject
    private DistrictHandler distHand;

    public CantonHandler() {
        super(Canton.class);
    }
    
    @Override
    public int[] getLineFormat() {
        return new int[]{2, 40, 10, 4, 1};
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

    public Canton createOrUpdate(Canton can, AppProcess proc) {
        Canton ret = getByCode(can.getCode());
        if (ret == null) {
            ret = new Canton();
            ret.setSince(proc);
        }
        ret.setActive(true);
        ret.setCode(can.getCode());
        ret.setName(can.getName());

        ret.setDistrict(can.getDistrict());
        ret.getDistrict().getCantons().add(ret);

        ret.setUntil(null);
        ret.setCurrent(proc);

        if (ret.getId() == null) {
            em.persist(ret);
        }
        return ret;
    }

    @Asynchronous
    @Override
    public Future<Boolean> importLine(FixedParser.ParsedLine line, AppProcess currentProc) {

        Canton cant = new Canton();
        cant.setCode(line.getInteger(0));
        cant.setName(line.getString(1));
        cant.setDistrict(distHand.getByCode(line.getString(3)));

        createOrUpdate(cant, currentProc);
        return new AsyncResult<>(true);

    }

}
