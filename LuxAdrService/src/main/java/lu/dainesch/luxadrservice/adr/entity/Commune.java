package lu.dainesch.luxadrservice.adr.entity;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.UniqueConstraint;
import lu.dainesch.luxadrservice.base.ImportedEntity;

@Entity
@Table(name = "COMMUNE", uniqueConstraints = {
    @UniqueConstraint(name = "UN_COMMUNE_CANT_CODE", columnNames = {"CANT_ID", "CODE"})
})
@NamedQueries({
    @NamedQuery(name = "commune.invalidate", query = "UPDATE Commune SET active = false")
    ,
    @NamedQuery(name = "commune.deleted", query = "UPDATE Commune SET until=:imp WHERE active = false and until is null")
    ,
    @NamedQuery(name = "commune.by.canton.code", query = "SELECT c FROM Commune c WHERE c.code = :code AND c.canton = :can")
})
public class Commune extends ImportedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "Commune")
    @TableGenerator(name = "Commune")
    private Long id;

    @Column(name = "CODE", nullable = false)
    private int code;

    @Column(name = "NAME", nullable = false, length = 40)
    private String name;

    @ManyToOne
    @JoinColumn(name = "CANT_ID", nullable = false)
    private Canton canton;

    @OneToMany(mappedBy = "commune")
    private Set<Locality> localities = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Canton getCanton() {
        return canton;
    }

    public void setCanton(Canton canton) {
        this.canton = canton;
    }

    public Set<Locality> getLocalities() {
        return localities;
    }

    public void setLocalities(Set<Locality> localities) {
        this.localities = localities;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 13 * hash + this.code;
        hash = 13 * hash + Objects.hashCode(this.canton);
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
        final Commune other = (Commune) obj;
        if (this.code != other.code) {
            return false;
        }
        if (!Objects.equals(this.canton, other.canton)) {
            return false;
        }
        return true;
    }

 

}
