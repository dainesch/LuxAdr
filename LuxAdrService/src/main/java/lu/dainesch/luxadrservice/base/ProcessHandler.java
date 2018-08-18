package lu.dainesch.luxadrservice.base;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Stateless
public class ProcessHandler {

    @PersistenceContext
    private EntityManager em;

    public AppProcess createNewProcess() {
        AppProcess ret = new AppProcess();
        ret.setState(AppProcess.ImportState.RUNNING);
        ret.setStart(new Date());
        em.persist(ret);
        return ret;
    }

    public AppProcess error(AppProcess proc) {
        proc.setState(AppProcess.ImportState.ERROR);
        proc.setEnd(new Date());
        return em.merge(proc);
    }

    public AppProcess complete(AppProcess proc) {
        proc.setState(AppProcess.ImportState.COMPLETED);
        proc.setEnd(new Date());
        return em.merge(proc);
    }

    public void log(AppProcess proc, ProcessingStep step, String message) {
        ProcessingLog l = new ProcessingLog();
        l.setCreated(new Date());
        l.setProcess(proc);
        l.setLog(message);
        l.setStep(step);
        em.persist(l);
    }

    public AppProcess getLatest() {
        try {
            return em.createNamedQuery("process.latest", AppProcess.class).getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    public List<ProcessingLog> getLatestLog() {
        AppProcess proc = getLatest();
        if (proc == null) {
            return Collections.EMPTY_LIST;
        }
        return em.createNamedQuery("processinglog.by.proc", ProcessingLog.class)
                .setParameter("proc", proc)
                .setMaxResults(10)
                .getResultList();
    }

}
