package lu.dainesch.luxadrservice.base;

import java.util.Date;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class ImportHandler {

    @PersistenceContext
    private EntityManager em;

    public Import createNewImport() {
        Import ret = new Import();
        ret.setStart(new Date());
        em.persist(ret);
        return ret;
    }

}
