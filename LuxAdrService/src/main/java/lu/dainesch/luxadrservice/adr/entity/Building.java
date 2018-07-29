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
@Table(name = "BUILDING")
@NamedQueries({
    @NamedQuery(name = "building.invalidate", query = "UPDATE Building SET active = false")
    ,
    @NamedQuery(name = "building.deleted", query = "UPDATE Building SET until=:imp WHERE active = false and until is null")
    ,
    @NamedQuery(name = "building.by.num", query = "SELECT b from Building b where b.number = :num")
    ,
    @NamedQuery(name = "building.by.nums", query = "SELECT b from Building b where b.number IN :nums")
})
public class Building extends ImportedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "Building")
    @TableGenerator(name = "Building")
    @Column(name = "BUIL_ID")
    private Long id;

    @Column(name = "NUMBER", nullable = false, unique = true)
    private int number;

    @Column(name = "NAME")
    private String name;

    @ManyToOne
    @JoinColumn(name = "QUA_ID")
    private Quarter quarter;

    @ManyToOne
    @JoinColumn(name = "PC_ID")
    private PostalCode postalCode;

    @ManyToOne
    @JoinColumn(name = "STR_ID")
    private Street street;

    @OneToMany(mappedBy = "building", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<HouseNumber> numbers = new HashSet<>();

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

    public Set<HouseNumber> getNumbers() {
        return numbers;
    }

    public void setNumbers(Set<HouseNumber> numbers) {
        this.numbers = numbers;
    }

    public Quarter getQuarter() {
        return quarter;
    }

    public void setQuarter(Quarter quarter) {
        this.quarter = quarter;
    }

    public PostalCode getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(PostalCode postalCode) {
        this.postalCode = postalCode;
    }

    public Street getStreet() {
        return street;
    }

    public void setStreet(Street street) {
        this.street = street;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + this.number;
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
        final Building other = (Building) obj;
        if (this.number != other.number) {
            return false;
        }
        return true;
    }

}
