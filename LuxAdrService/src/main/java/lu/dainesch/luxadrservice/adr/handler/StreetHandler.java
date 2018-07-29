package lu.dainesch.luxadrservice.adr.handler;

import java.util.Date;
import java.util.concurrent.Future;
import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import lu.dainesch.luxadrservice.adr.entity.AlternateName;
import lu.dainesch.luxadrservice.adr.entity.Street;
import lu.dainesch.luxadrservice.base.Import;
import lu.dainesch.luxadrservice.input.FixedParser;

@Stateless
public class StreetHandler extends ImportedEntityHandler<Street> {

    @Inject
    private LocalityHandler locHand;

    public StreetHandler() {
        super(Street.class);
    }

    @Override
    public int[] getLineFormat() {
        return new int[]{5, 40, 40, 10, 5, 1, 1, 10, 1, 10, 2, 1, 4, 1, 5, 1, 1, 30};
    }

    @Override
    public int[] getAltLineFormat() {
        return new int[]{3, 40, 40, 1, 10, 5, 80, 1};
    }

    public Street getByNumber(int num) {
        try {
            return em.createNamedQuery("street.by.num", Street.class)
                    .setParameter("num", num)
                    .getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    public Street createOrUpdate(Street st, Import imp) {
        Street ret = getByNumber(st.getNumber());
        if (ret == null) {
            ret = new Street();
            ret.setSince(imp);
        }
        ret.setActive(true);
        ret.setNumber(st.getNumber());
        ret.setName(st.getName());
        ret.setSortValue(st.getSortValue());
        ret.setStreetCode(st.getStreetCode());
        ret.setTemporary(st.isTemporary());

        ret.setLocality(st.getLocality());
        ret.getLocality().getStreets().add(ret);

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

        Street s = new Street();
        s.setNumber(line.getInteger(0));
        s.setName(line.getString(1));
        s.setSortValue(line.getString(3));

        if (!line.getString(10).isEmpty()) {
            s.setStreetCode(line.getString(10) + " " + line.getString(12));
        }

        s.setLocality(locHand.getByNumber(line.getInteger(14)));
        s.setTemporary(line.getBoolean(16));

        Date valid = line.getDate(7);
        if (valid == null || valid.after(new Date())) {
            createOrUpdate(s, currentImport);

            return new AsyncResult<>(true);

        }
        return new AsyncResult<>(false);
    }

    @Asynchronous
    @Override
    public Future<Boolean> importAltName(FixedParser.ParsedLine line) {

        AlternateName n = new AlternateName(line.getLanguage(3), line.getString(1));

        Street str = getByNumber(line.getInteger(5));
        if (str != null) {
            n.setStreet(str);
            str.getAltNames().clear(); // just to be sure
            str.getAltNames().add(n);
            return new AsyncResult<>(true);
        }
        return new AsyncResult<>(false);
    }

    @Override
    public int deleteAltNames() {
        return em.createNamedQuery("alternatename.del.str").executeUpdate();
    }

}
