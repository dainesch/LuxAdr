package lu.dainesch.luxadrservice.adr.handler;

import java.util.concurrent.Future;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import lu.dainesch.luxadrservice.base.Import;
import lu.dainesch.luxadrservice.base.ImportedEntity;
import lu.dainesch.luxadrservice.input.FixedParser;

public abstract class ImportedEntityHandler<I extends ImportedEntity> {

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

    public abstract int[] getLineFormat();

    public abstract Future<Boolean> importLine(FixedParser.ParsedLine line, Import currentImport);

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
