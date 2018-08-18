package lu.dainesch.luxadrservice.adr.handler;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;
import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import lu.dainesch.luxadrservice.adr.entity.AlternateName;
import lu.dainesch.luxadrservice.adr.entity.Locality;
import lu.dainesch.luxadrservice.adr.entity.PostalCode;
import lu.dainesch.luxadrservice.adr.entity.Street;
import lu.dainesch.luxadrdto.SearchRequest;
import lu.dainesch.luxadrservice.base.AppProcess;
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

    @Override
    public int[] getLineFormat() {
        return new int[]{5, 40, 40, 2, 1, 10, 1, 10, 2, 1, 2, 1};
    }

    @Override
    public int[] getAltLineFormat() {
        return new int[]{3, 40, 40, 1, 10, 5};
    }

    public List<Street> getStreets(Long id) {
        return em.createNamedQuery("locality.by.id.streets", Street.class)
                .setParameter("id", id)
                .getResultList();
    }

    public List<PostalCode> getPostCodes(Long id) {
        return em.createNamedQuery("locality.by.id.postcodes", PostalCode.class)
                .setParameter("id", id)
                .getResultList();
    }

    public List<Locality> search(SearchRequest req) {
        return em.createNamedQuery("locality.search.name", Locality.class)
                .setParameter("name", req.getValue())
                .setMaxResults(req.getMaxResults()).getResultList();
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

    public Locality createOrUpdate(Locality loc, AppProcess proc) {
        Locality ret = getByNumber(loc.getNumber());
        if (ret == null) {
            ret = new Locality();
            ret.setSince(proc);
        }
        ret.setActive(true);
        ret.setNumber(loc.getNumber());
        ret.setCity(loc.isCity());
        ret.setCode(loc.getCode());
        ret.setName(loc.getName());

        ret.setCommune(loc.getCommune());
        ret.getCommune().getLocalities().add(ret);

        ret.setUntil(null);
        ret.setCurrent(proc);

        if (ret.getId() == null) {
            em.persist(ret);
        }
        return ret;
    }

    @Asynchronous
    @Override
    public Future<Boolean> importLine(FixedParser.ParsedLine line, AppProcess currentProcess) {

        Locality loc = new Locality();
        loc.setNumber(line.getInteger(0));
        loc.setName(line.getString(1));
        loc.setCode(line.getInteger(3));
        loc.setCity(line.getBoolean(4));

        loc.setCommune(commHand.getByCantonCode(canHand.getByCode(line.getInteger(8)), line.getInteger(10)));

        Date valid = line.getDate(5);
        if (valid == null || valid.after(new Date())) {
            createOrUpdate(loc, currentProcess);
            return new AsyncResult<>(true);
        }
        return new AsyncResult<>(false);
    }

    @Asynchronous
    @Override
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

    @Override
    public int deleteAltNames() {
        return em.createNamedQuery("alternatename.del.loc").executeUpdate();
    }

}
