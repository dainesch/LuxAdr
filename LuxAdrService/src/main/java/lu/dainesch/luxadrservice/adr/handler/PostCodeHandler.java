package lu.dainesch.luxadrservice.adr.handler;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import lu.dainesch.luxadrservice.adr.entity.PostCodeType;
import lu.dainesch.luxadrservice.adr.entity.PostalCode;
import lu.dainesch.luxadrservice.base.Import;
import lu.dainesch.luxadrservice.base.ImportException;
import lu.dainesch.luxadrservice.base.ImportHandler;
import lu.dainesch.luxadrservice.input.FixedParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
public class PostCodeHandler extends ImportedEntityHandler<PostalCode> {

    private static final Logger LOG = LoggerFactory.getLogger(PostCodeHandler.class);

    @Inject
    private ImportHandler impHand;

    public PostCodeHandler() {
        super(PostalCode.class);
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
        if (ret.getId() == null) {
            em.persist(ret);
        }
        return ret;
    }

    public void updatePostCodes(InputStream in) throws ImportException {

        try (FixedParser parser = new FixedParser(in, 4, 40, 1, 4, 4, 10)) {
            if (!parser.hasNext()) {
                LOG.warn("Empty file given as input, aborting");
                return;
            }

            Import currentImport = impHand.createNewImport();
            int count = 0;

            invalidate();

            while (parser.hasNext()) {

                FixedParser.ParsedLine line = parser.next();
                PostalCode code = new PostalCode();
                code.setCode(line.getString(0));
                code.setPostOfficeName(line.getString(1));
                code.setType("B".equals(line.getString(2)) ? PostCodeType.Mailbox : PostCodeType.Normal);
                code.setMinMailbox(line.getInteger(3));
                code.setMaxMailbox(line.getInteger(4));

                createOrUpdate(code, currentImport);

                count++;
            }

            currentImport.setEnd(new Date());
            int deleted = postprocess(currentImport);

            LOG.info("Imported " + count + " postal codes and deleted " + deleted);

        } catch (IOException ex) {
            throw new ImportException("Error during postalcode import", ex);
        }

    }

}
