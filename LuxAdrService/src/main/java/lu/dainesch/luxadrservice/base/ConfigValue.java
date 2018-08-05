package lu.dainesch.luxadrservice.base;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

@Entity
@Cacheable
@NamedQueries({
    @NamedQuery(name = "cfg.by.type", query = "Select c from ConfigValue c where c.type = :type")
})
@Table(name = "CONFIG_VALUE")
public class ConfigValue implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "ConfigValue")
    @TableGenerator(name = "ConfigValue")
    @Column(name = "CFG_ID")
    private Long id;

    @Column(name = "CFG_KEY", nullable = false, unique = true)
    @Enumerated(EnumType.STRING)
    private ConfigType type;

    @Column(name = "CFG_VALUE", nullable = false)
    private String value;

    public ConfigValue() {

    }

    public ConfigValue(ConfigType type, String value) {
        this.type = type;
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ConfigType getType() {
        return type;
    }

    public void setType(ConfigType type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setInt(int v) {
        this.value = String.valueOf(v);
    }

    public Integer getInt() {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    public void setFloat(float v) {
        this.value = String.valueOf(v);
    }

    public Float getFloat() {
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    public void setBoolean(boolean val) {
        this.value = Boolean.toString(val);
    }

    public boolean getBoolean() {
        return Boolean.valueOf(value);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 43 * hash + Objects.hashCode(this.type);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ConfigValue other = (ConfigValue) obj;
        if (this.type != other.type) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ConfigValue{" + "type=" + type + ", value=" + value + '}';
    }

}
