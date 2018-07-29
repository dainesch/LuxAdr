package lu.dainesch.luxadrservice.adr.handler;

import java.util.concurrent.Future;
import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import lu.dainesch.luxadrservice.adr.entity.Quarter;
import lu.dainesch.luxadrservice.base.Import;
import lu.dainesch.luxadrservice.input.FixedParser;

@Stateless
public class QuarterHandler extends ImportedEntityHandler<Quarter> {

    @Inject
    private LocalityHandler locHand;

    public QuarterHandler() {
        super(Quarter.class);
    }

    @Override
    public int[] getLineFormat() {
        return new int[]{5, 40, 10, 5, 1};
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
        ret.setCurrent(imp);

        if (ret.getId() == null) {
            em.persist(ret);
        }
        return ret;
    }

    @Asynchronous
    @Override
    public Future<Boolean> importLine(FixedParser.ParsedLine line, Import currentImport) {

        Quarter cant = new Quarter();
        cant.setNumber(line.getInteger(0));
        cant.setName(line.getString(1));
        cant.setLocality(locHand.getByNumber(line.getInteger(3)));

        createOrUpdate(cant, currentImport);
        return new AsyncResult<>(true);
    }

}
