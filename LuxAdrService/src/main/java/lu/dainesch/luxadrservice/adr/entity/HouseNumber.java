package lu.dainesch.luxadrservice.adr.entity;

import java.io.Serializable;
import java.util.Objects;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import lu.dainesch.luxadrdto.entity.HouseNumberDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
@Table(name = "HOUSENUMBER", indexes = {
    @Index(name = "IDX_HOUSENUMBER_BUIL_ID", columnList = "BUIL_ID")
})
@Cacheable
public class HouseNumber implements Serializable, Comparable<HouseNumber> {

    private static final Logger LOG = LoggerFactory.getLogger(HouseNumber.class);

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

    public JsonObjectBuilder toJson(boolean includeBuilding) {
        JsonObjectBuilder ret = Json.createObjectBuilder()
                .add("id", id)
                .add("number", number);
        if (includeBuilding) {
            ret.add("building", building.toJson(false));
        } else {
            ret.add("buildingId", building.getId());
        }

        return ret;

    }
    
    public HouseNumberDTO toDTO(boolean includeBuilding) {
        HouseNumberDTO ret = new HouseNumberDTO(id, number);
        if (includeBuilding) {
            ret.setBuilding(building.toDTO(false));
        } else {
            ret.setBuildingId(building.getId());
        }
        return ret;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.number);
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
        return true;
    }

    @Override
    public int compareTo(HouseNumber o) {
        if (number.equals(o.number)) {
            return 0;
        }
        String[] a = splitNum(number);
        String[] b = splitNum(o.number);

        try {
            int an = Integer.parseInt(a[0]);
            int bn = Integer.parseInt(b[0]);
            if (an > bn) {
                return 1;
            } else if (an < bn) {
                return -1;
            } else if (a.length == 1) {
                return -1;
            } else if (b.length == 1) {
                return 1;
            } else {
                return a[1].compareTo(b[1]);
            }
        } catch (NumberFormatException ex) {
            LOG.error("Error comparing alphanum " + number + " to " + o.number);
        }
        return 0;
    }

    private static String[] splitNum(String str) {
        return str.split("(?i)((?<=[A-Z])(?=\\d))|((?<=\\d)(?=[A-Z]))");
    }

}
