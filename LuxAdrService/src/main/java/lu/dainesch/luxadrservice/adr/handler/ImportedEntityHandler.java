package lu.dainesch.luxadrservice.adr.handler;

import java.util.concurrent.Future;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import lu.dainesch.luxadrservice.base.AppProcess;
import lu.dainesch.luxadrservice.base.ImportedEntity;
import lu.dainesch.luxadrservice.input.FixedParser;

public abstract class ImportedEntityHandler<I extends ImportedEntity> {

    private final Class<I> clazz;

    @PersistenceContext
    protected EntityManager em;

    public ImportedEntityHandler(Class<I> clazz) {
        this.clazz = clazz;
    }
    
    public I getById(Long id) {
        return em.find(clazz, id);
    }

    public int postprocess(AppProcess proc) {
        return em.createNamedQuery(clazz.getSimpleName().toLowerCase() + ".invalidate")
                .setParameter("proc", proc).executeUpdate();

    }

    public abstract int[] getLineFormat();

    public abstract Future<Boolean> importLine(FixedParser.ParsedLine line, AppProcess currentProcess);

    public int[] getAltLineFormat() {
        throw new UnsupportedOperationException("Not implemented");
    }

    public int deleteAltNames() {
        throw new UnsupportedOperationException("Not implemented");
    }

    public Future<Boolean> importAltName(FixedParser.ParsedLine line) {
        throw new UnsupportedOperationException("Not implemented");
    }

}
