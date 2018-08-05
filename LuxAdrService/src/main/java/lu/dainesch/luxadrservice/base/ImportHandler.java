package lu.dainesch.luxadrservice.base;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Stateless
public class ImportHandler {

    @PersistenceContext
    private EntityManager em;

    public Import createNewImport() {
        Import ret = new Import();
        ret.setState(Import.ImportState.RUNNING);
        ret.setStart(new Date());
        em.persist(ret);
        return ret;
    }

    public Import error(Import imp) {
        imp.setState(Import.ImportState.ERROR);
        imp.setEnd(new Date());
        return em.merge(imp);
    }

    public Import complete(Import imp) {
        imp.setState(Import.ImportState.COMPLETED);
        imp.setEnd(new Date());
        return em.merge(imp);
    }

    public void log(Import imp, ImportStep step, String message) {
        ImportLog l = new ImportLog();
        l.setCreated(new Date());
        l.setImp(imp);
        l.setLog(message);
        l.setStep(step);
        em.persist(l);
    }

    public Import getLatest() {
        try {
            return em.createNamedQuery("import.latest", Import.class).getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    public List<ImportLog> getLatestLog() {
        Import imp = getLatest();
        if (imp == null) {
            return Collections.EMPTY_LIST;
        }
        return em.createNamedQuery("importlog.by.imp", ImportLog.class)
                .setParameter("imp", imp)
                .setMaxResults(10)
                .getResultList();
    }

}
