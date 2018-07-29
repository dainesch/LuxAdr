package lu.dainesch.luxadrservice.adr.handler;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import lu.dainesch.luxadrservice.base.Import;
import lu.dainesch.luxadrservice.base.ImportedEntity;

public class ImportedEntityHandler<I extends ImportedEntity> {

    private final String entity;

    @PersistenceContext
    protected EntityManager em;

    public ImportedEntityHandler(Class<I> clazz) {
        entity = clazz.getSimpleName().toLowerCase();
    }

    public int postprocess(Import imp) {
        return em.createNamedQuery(entity + ".invalidate")
                .setParameter("imp", imp).executeUpdate();

    }

}
