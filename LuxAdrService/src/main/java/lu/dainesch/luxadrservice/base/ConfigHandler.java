package lu.dainesch.luxadrservice.base;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import javax.ejb.Stateless;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
public class ConfigHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigHandler.class);

    @PersistenceContext
    private EntityManager em;

    public void initConfig() {
        // save default values
        for (ConfigType t : ConfigType.values()) {
            if (t.isAutocreate() && getValue(t) == null) {
                em.persist(new ConfigValue(t, t.getDefaultValue()));
            }
        }

        if (getValue(ConfigType.LUCENE_DATA_DIR) == null) {
            try {
                Path temp = Files.createTempDirectory("LuxAdrLuceneData");
                em.persist(new ConfigValue(ConfigType.LUCENE_DATA_DIR, temp.toString()));
            } catch (IOException ex) {
                LOG.error("Error creating lucence temp dir", ex);
            }
        }
    }

    public ConfigValue getValue(ConfigType type) {
        try {
            return em.createNamedQuery("cfg.by.type", ConfigValue.class)
                    .setParameter("type", type)
                    .getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    public List<ConfigValue> getAll() {
        return em.createNamedQuery("cfg.all", ConfigValue.class).getResultList();
    }

    public void save(ConfigValue val) {
        em.merge(val);
    }

    @Config
    @Produces
    public ConfigValue produce(InjectionPoint ip) {
        ConfigType type = getType(ip);
        if (type == null || type == ConfigType.NONE) {
            return null;
        }
        return getValue(type);
    }

    private ConfigType getType(final InjectionPoint ip) {

        if (ip.getAnnotated().isAnnotationPresent(Config.class)) {
            return ip.getAnnotated().getAnnotation(Config.class).value();
        }
        return null;

    }

}
