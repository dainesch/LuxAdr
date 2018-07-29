package lu.dainesch.luxadrservice.adr.entity;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import lu.dainesch.luxadrservice.base.ImportedEntity;

@Entity
@Table(name = "QUARTER")
@NamedQueries({
    @NamedQuery(name = "quarter.invalidate", query = "UPDATE Quarter SET active = false")
    ,
    @NamedQuery(name = "quarter.deleted", query = "UPDATE Quarter SET until=:imp WHERE active = false and until is null")
    ,
    @NamedQuery(name = "quarter.by.num", query = "SELECT q from Quarter q where q.number = :num")
})
public class Quarter extends ImportedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "Quarter")
    @TableGenerator(name = "Quarter")
    @Column(name = "QUA_ID")
    private Long id;

    @Column(name = "NUMBER", nullable = false, unique = true)
    private int number;

    @Column(name = "NAME", nullable = false, length = 40)
    private String name;

    @ManyToOne
    @JoinColumn(name = "LOC_ID", nullable = false)
    private Locality locality;

    @OneToMany(mappedBy = "quarter")
    private Set<Building> buildings = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
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

    public Set<Building> getBuildings() {
        return buildings;
    }

    public void setBuildings(Set<Building> buildings) {
        this.buildings = buildings;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 61 * hash + this.number;
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
        final Quarter other = (Quarter) obj;
        if (this.number != other.number) {
            return false;
        }
        return true;
    }

}
