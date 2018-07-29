package lu.dainesch.luxadrservice.adr.handler;

import java.util.Date;
import java.util.concurrent.Future;
import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import lu.dainesch.luxadrservice.adr.entity.AlternateName;
import lu.dainesch.luxadrservice.adr.entity.Locality;
import lu.dainesch.luxadrservice.base.Import;
import lu.dainesch.luxadrservice.input.FixedParser;

@Stateless
public class LocalityHandler extends ImportedEntityHandler<Locality> {

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
        ret.setCurrent(imp);

        if (ret.getId() == null) {
            em.persist(ret);
        }
        return ret;
    }

    @Asynchronous
    public Future<Boolean> importLocality(FixedParser.ParsedLine line, Import currentImport) {

        Locality loc = new Locality();
        loc.setNumber(line.getInteger(0));
        loc.setName(line.getString(1));
        loc.setCode(line.getInteger(3));
        loc.setCity(line.getBoolean(4));

        loc.setCommune(commHand.getByCantonCode(canHand.getByCode(line.getInteger(8)), line.getInteger(10)));

        Date valid = line.getDate(5);
        if (valid == null || valid.after(new Date())) {
            createOrUpdate(loc, currentImport);
            return new AsyncResult<>(true);
        }
        return new AsyncResult<>(false);
    }

    @Asynchronous
    public Future<Boolean> importAltName(FixedParser.ParsedLine line) {

        AlternateName n = new AlternateName(line.getLanguage(3), line.getString(1));

        Locality loc = getByNumber(line.getInteger(5));
        if (loc != null) {
            n.setLocality(loc);
            loc.getAltNames().clear(); // just to be sure
            loc.getAltNames().add(n);
            return new AsyncResult<>(true);
        }
        return new AsyncResult<>(false);
    }

    public int deleteAltNames() {
        return em.createNamedQuery("alternatename.del.loc").executeUpdate();
    }

}
