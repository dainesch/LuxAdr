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
@Table(name = "LOCALITY")
@NamedQueries({
    @NamedQuery(name = "locality.invalidate", query = "UPDATE Locality SET active = false")
    ,
    @NamedQuery(name = "locality.deleted", query = "UPDATE Locality SET until=:imp WHERE active = false and until is null")
    ,
    @NamedQuery(name = "locality.by.number", query = "SELECT l from Locality l where l.number = :num")
})
public class Locality extends ImportedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "Locality")
    @TableGenerator(name = "Locality")
    private Long id;

    @Column(name = "NUMBER", nullable = false, unique = true)
    private int number;

    @Column(name = "NAME", nullable = false, length = 40)
    private String name;

    @Column(name = "CODE", nullable = false)
    private int code;
    
    @Column(name = "CITY", nullable = false)
    private boolean city;
        
    @ManyToOne
    @JoinColumn(name = "COMM_ID", nullable = false)
    private Commune commune;
    
    @OneToMany(mappedBy = "locality", fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)
    private Set<AlternateName> altNames = new HashSet<>();

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

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isCity() {
        return city;
    }

    public void setCity(boolean city) {
        this.city = city;
    }

    public Commune getCommune() {
        return commune;
    }

    public void setCommune(Commune commune) {
        this.commune = commune;
    }

    public Set<AlternateName> getAltNames() {
        return altNames;
    }

    public void setAltNames(Set<AlternateName> altNames) {
        this.altNames = altNames;
    }
    
    

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + this.number;
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
        final Locality other = (Locality) obj;
        if (this.number != other.number) {
            return false;
        }
        return true;
    }
    
    

}
