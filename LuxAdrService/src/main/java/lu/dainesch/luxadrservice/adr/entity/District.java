package lu.dainesch.luxadrservice.adr.entity;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import lu.dainesch.luxadrservice.base.ImportedEntity;

@Entity
@Table(name = "DISTRICT")
@Cacheable
@NamedQueries({
    @NamedQuery(name = "district.invalidate", query = "UPDATE District SET active = false, until = :proc where current != :proc")
    ,
    @NamedQuery(name = "district.by.code", query = "SELECT d from District d where d.code = :code")
})
public class District extends ImportedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "District")
    @TableGenerator(name = "District")
    @Column(name = "DIST_ID")
    private Long id;

    @Column(name = "CODE", nullable = false, unique = true, length = 4)
    private String code;

    @Column(name = "NAME", nullable = false, length = 40)
    private String name;

    @OneToMany(mappedBy = "district")
    private Set<Canton> cantons = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Canton> getCantons() {
        return cantons;
    }

    public void setCantons(Set<Canton> cantons) {
        this.cantons = cantons;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.code);
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
        final District other = (District) obj;
        if (!Objects.equals(this.code, other.code)) {
            return false;
        }
        return true;
    }

}
