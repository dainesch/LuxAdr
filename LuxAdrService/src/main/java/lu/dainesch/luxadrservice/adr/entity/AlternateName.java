package lu.dainesch.luxadrservice.adr.entity;

import java.io.Serializable;
import java.util.Locale;
import java.util.Objects;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

@Entity
@Table(name = "ALT_NAME")
@Cacheable
@NamedQueries({
    @NamedQuery(name = "alternatename.del.loc", query = "DELETE FROM AlternateName WHERE localiity != null")
    ,
    @NamedQuery(name = "alternatename.del.str", query = "DELETE FROM AlternateName WHERE street != null")
})
public class AlternateName implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "AlternateName")
    @TableGenerator(name = "AlternateName")
    @Column(name = "AN_ID")
    private Long id;

    @Column(name = "LANG", length = 3)
    private String lang;
    @Column(name = "NAME", nullable = false, length = 40)
    private String name;

    @ManyToOne
    @JoinColumn(name = "LOC_ID", nullable = true)
    private Locality locality;

    @ManyToOne
    @JoinColumn(name = "STR_ID", nullable = true)
    private Street street;

    public AlternateName() {

    }

    public AlternateName(Locale lang, String name) {
        this.lang = lang != null ? lang.getLanguage() : null;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Locality getLocality() {
        return locality;
    }

    public void setLocality(Locality locality) {
        this.locality = locality;
    }

    public Street getStreet() {
        return street;
    }

    public void setStreet(Street street) {
        this.street = street;
    }
    
    public JsonObjectBuilder toJson() {
        JsonObjectBuilder ret = Json.createObjectBuilder();
        if (lang!=null) {
            ret.add("lang", lang);
        }
        ret.add("name", name);
        return ret;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.lang);
        hash = 79 * hash + Objects.hashCode(this.name);
        hash = 79 * hash + Objects.hashCode(this.locality);
        hash = 79 * hash + Objects.hashCode(this.street);
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
        final AlternateName other = (AlternateName) obj;
        if (!Objects.equals(this.lang, other.lang)) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.locality, other.locality)) {
            return false;
        }
        if (!Objects.equals(this.street, other.street)) {
            return false;
        }
        return true;
    }

   

}
