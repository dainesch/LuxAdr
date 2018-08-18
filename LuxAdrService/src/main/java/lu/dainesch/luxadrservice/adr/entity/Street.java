package lu.dainesch.luxadrservice.adr.entity;

import java.util.HashSet;
import java.util.Set;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.persistence.Cacheable;
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
import lu.dainesch.luxadrdto.entity.StreetDTO;
import lu.dainesch.luxadrservice.base.ImportedEntity;

@Entity
@Table(name = "STREET")
@Cacheable
@NamedQueries({
    @NamedQuery(name = "street.invalidate", query = "UPDATE Street SET active = false, until = :proc where current != :proc")
    ,
    @NamedQuery(name = "street.by.num", query = "SELECT s from Street s where s.number = :num")
    ,
    @NamedQuery(name = "street.by.id.postcodes",
            query = "SELECT distinct p from Street s "
            + "join s.buildings b "
            + "join b.postalCode p "
            + "where s.id = :id "
            + "order by p.code ")
    ,
    @NamedQuery(name = "street.by.id.numbers",
            query = "SELECT distinct n from Street s "
            + "join s.buildings b "
            + "join b.numbers n "
            + "where s.id = :id ")
    ,
    @NamedQuery(name = "street.search.name",
            query = "SELECT distinct s from Street s "
            + "left join s.altNames a "
            + "where s.active = true "
            + "and ("
            + " LOWER(s.name) like :name "
            + " OR LOWER(a.name) like :name"
            + ") "
            + "order by s.sortValue")

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

    public JsonObjectBuilder toJson(boolean includeLoc) {
        JsonObjectBuilder ret = Json.createObjectBuilder()
                .add("id", id)
                .add("active", active)
                .add("name", name);
        if (streetCode != null) {
            ret.add("streetCode", streetCode);
        }
        if (!altNames.isEmpty()) {
            JsonArrayBuilder locs = Json.createArrayBuilder();
            altNames.forEach(a -> locs.add(a.toJson()));
            ret.add("altNames", locs);
        }
        if (includeLoc) {
            ret.add("locality", locality.toJson());
        }

        return ret;

    }
    
    public StreetDTO toDTO(boolean includeLoc) {
        StreetDTO ret = new StreetDTO(id, active, name, streetCode);
        altNames.stream().map(n -> n.toDTO()).forEach(d -> ret.getAltNames().add(d));
        if (includeLoc) {
            ret.setLocality(locality.toDTO());
        }
        return ret;
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
