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
    
}
