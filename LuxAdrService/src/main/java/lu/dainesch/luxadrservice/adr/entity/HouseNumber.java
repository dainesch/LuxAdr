package lu.dainesch.luxadrservice.adr.entity;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

@Entity
@Table(name = "HOUSENUMBER")
public class HouseNumber implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "HouseNumber")
    @TableGenerator(name = "HouseNumber")
    @Column(name = "NUM_ID")
    private Long id;

    @Column(name = "NUMBER", length = 9, nullable = false)
    private String number;

    @ManyToOne
    @JoinColumn(name = "BUIL_ID", nullable = false)
    private Building building;

    public HouseNumber() {
    }

    public HouseNumber(String number) {
        this.number = number;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Building getBuilding() {
        return building;
    }

    public void setBuilding(Building building) {
        this.building = building;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + Objects.hashCode(this.number);
        hash = 29 * hash + Objects.hashCode(this.building);
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
        final HouseNumber other = (HouseNumber) obj;
        if (!Objects.equals(this.number, other.number)) {
            return false;
        }
        if (!Objects.equals(this.building, other.building)) {
            return false;
        }
        return true;
    }

}
