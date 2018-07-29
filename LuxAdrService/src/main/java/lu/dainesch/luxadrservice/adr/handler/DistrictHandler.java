package lu.dainesch.luxadrservice.adr.handler;

import java.util.concurrent.Future;
import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import lu.dainesch.luxadrservice.adr.entity.District;
import lu.dainesch.luxadrservice.base.Import;
import lu.dainesch.luxadrservice.input.FixedParser;

@Stateless
public class DistrictHandler extends ImportedEntityHandler<District> {

    public DistrictHandler() {
        super(District.class);
    }

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
        ret.setCurrent(imp);

        if (ret.getId() == null) {
            em.persist(ret);
        }
        return ret;
    }

    @Asynchronous
    public Future<Boolean> importDistrict(FixedParser.ParsedLine line, Import currentImport) {

        District dist = new District();
        dist.setCode(line.getString(0));
        dist.setName(line.getString(1));

        createOrUpdate(dist, currentImport);

        return new AsyncResult<>(true);

    }

}
