package lu.dainesch.luxadrservice.base;

import javax.ejb.Stateless;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Stateless
public class ConfigHandler {

    @PersistenceContext
    private EntityManager em;

    public void initConfig() {
        // save default values
        for (ConfigType t : ConfigType.values()) {
            if (t != ConfigType.NONE && getValue(t) == null) {
                em.persist(new ConfigValue(t, t.getDefaultValue()));
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
