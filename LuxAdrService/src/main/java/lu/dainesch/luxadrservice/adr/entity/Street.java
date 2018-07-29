package lu.dainesch.luxadrservice.adr.entity;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
@Table(name = "STREET")
@NamedQueries({
    @NamedQuery(name = "street.invalidate", query = "UPDATE Street SET active = false")
    ,
    @NamedQuery(name = "street.deleted", query = "UPDATE Street SET until=:imp WHERE active = false and until is null")
    ,
    @NamedQuery(name = "street.by.num", query = "SELECT s from Street s where s.number = :num")
})
public class Street extends ImportedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "Street")
    @TableGenerator(name = "Street")
    @Column(name = "STR_ID")
    private Long id;

    @Column(name = "NUMBER", nullable = false, unique = true)
    private int number;

    @Column(name = "NAME", nullable = false, length = 40)
    private String name;

    @Column(name = "SORT_VAL", nullable = false, length = 10)
    private String sortValue;

    @Column(name = "STREET_CODE", length = 7)
    private String streetCode;

    @Column(name = "TEMP", nullable = false)
    private boolean temporary;

    @ManyToOne
    @JoinColumn(name = "LOC_ID", nullable = false)
    private Locality locality;

    @OneToMany(mappedBy = "street", fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)
    private Set<AlternateName> altNames = new HashSet<>();

    @OneToMany(mappedBy = "street")
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

    public String getSortValue() {
        return sortValue;
    }

    public void setSortValue(String sortValue) {
        this.sortValue = sortValue;
    }

    public String getStreetCode() {
        return streetCode;
    }

    public void setStreetCode(String streetCode) {
        this.streetCode = streetCode;
    }

    public boolean isTemporary() {
        return temporary;
    }

    public void setTemporary(boolean temporary) {
        this.temporary = temporary;
    }

    public Set<AlternateName> getAltNames() {
        return altNames;
    }

    public void setAltNames(Set<AlternateName> altNames) {
        this.altNames = altNames;
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
        final Street other = (Street) obj;
        if (this.number != other.number) {
            return false;
        }
        return true;
    }

}
