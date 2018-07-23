package lu.dainesch.luxadrservice.adr.entity;

import java.io.Serializable;
import java.util.Locale;
import java.util.Objects;
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
@NamedQueries({
    @NamedQuery(name = "alternatename.del.loc", query = "DELETE FROM AlternateName WHERE localiity != null")
})
public class AlternateName implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "AlternateName")
    @TableGenerator(name = "AlternateName")
    private Long id;

    @Column(name = "LANG", length = 3)
    private String lang;
    @Column(name = "NAME", nullable = false, length = 40)
    private String name;

    @ManyToOne
    @JoinColumn(name = "LOC_ID", nullable = true)
    private Locality locality;

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

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.lang);
        hash = 53 * hash + Objects.hashCode(this.locality);
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
        if (!Objects.equals(this.locality, other.locality)) {
            return false;
        }
        return true;
    }

}
