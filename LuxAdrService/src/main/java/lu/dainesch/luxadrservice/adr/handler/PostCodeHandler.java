package lu.dainesch.luxadrservice.adr.handler;

import java.util.concurrent.Future;
import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import lu.dainesch.luxadrservice.adr.entity.PostCodeType;
import lu.dainesch.luxadrservice.adr.entity.PostalCode;
import lu.dainesch.luxadrservice.base.Import;
import lu.dainesch.luxadrservice.input.FixedParser;

@Stateless
public class PostCodeHandler extends ImportedEntityHandler<PostalCode> {

    public PostCodeHandler() {
        super(PostalCode.class);
    }

    @Override
    public int[] getLineFormat() {
        return new int[]{4, 40, 1, 4, 4, 10};
    }

    public PostalCode getByCode(String code) {
        try {
            return em.createNamedQuery("postalcode.by.code", PostalCode.class)
                    .setParameter("code", code)
                    .getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    public PostalCode createOrUpdate(PostalCode code, Import imp) {
        PostalCode ret = getByCode(code.getCode());
        if (ret == null) {
            ret = new PostalCode();
            ret.setSince(imp);
        }
        ret.setActive(true);
        ret.setCode(code.getCode());
        ret.setMaxMailbox(code.getMaxMailbox());
        ret.setMinMailbox(code.getMinMailbox());
        ret.setPostOfficeName(code.getPostOfficeName());
        ret.setType(code.getType());

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

        PostalCode code = new PostalCode();
        code.setCode(line.getString(0));
        code.setPostOfficeName(line.getString(1));
        code.setType("B".equals(line.getString(2)) ? PostCodeType.Mailbox : PostCodeType.Normal);
        code.setMinMailbox(line.getInteger(3));
        code.setMaxMailbox(line.getInteger(4));

        createOrUpdate(code, currentImport);
        return new AsyncResult<>(true);
    }

}
