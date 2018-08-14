package lu.dainesch.luxadrservice.adr.entity;

import java.io.Serializable;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import lu.dainesch.luxadrdto.entity.CoordinateDTO;

@Entity
@Table(name = "COORDINATES")
@Cacheable
public class Coordinates implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "Coordinates")
    @TableGenerator(name = "Coordinates")
    @Column(name = "COO_ID")
    private Long id;

    @Column(name = "LATITUDE", nullable = false)
    private float latitude;

    @Column(name = "LONGITUDE", nullable = false)
    private float longitude;

    @OneToOne(mappedBy = "coordinates")
    private Building building;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public Building getBuilding() {
        return building;
    }

    public void setBuilding(Building building) {
        this.building = building;
    }

    public JsonObjectBuilder toJson() {
        JsonObjectBuilder ret = Json.createObjectBuilder()
                .add("lat", latitude)
                .add("long", longitude);

        return ret;

    }
    
    public CoordinateDTO toDTO() {
        return new CoordinateDTO(latitude, longitude);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Float.floatToIntBits(this.latitude);
        hash = 53 * hash + Float.floatToIntBits(this.longitude);
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
        final Coordinates other = (Coordinates) obj;
        if (Float.floatToIntBits(this.latitude) != Float.floatToIntBits(other.latitude)) {
            return false;
        }
        if (Float.floatToIntBits(this.longitude) != Float.floatToIntBits(other.longitude)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Coordinates{" + "id=" + id + ", latitude=" + latitude + ", longitude=" + longitude + '}';
    }

}
