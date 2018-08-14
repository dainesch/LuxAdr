package lu.dainesch.luxadrdto.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class BuildingDTO implements Serializable {

    private Long id;
    private boolean active;

    private PostalCodeDTO postalCode;
    private StreetDTO street;
    private Set<HouseNumberDTO> numbers = new HashSet<>();
    private CoordinateDTO coordinates;

    public BuildingDTO(Long id, boolean active) {
        this.id = id;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Set<HouseNumberDTO> getNumbers() {
        return numbers;
    }

    public void setNumbers(Set<HouseNumberDTO> numbers) {
        this.numbers = numbers;
    }

    public PostalCodeDTO getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(PostalCodeDTO postalCode) {
        this.postalCode = postalCode;
    }

    public StreetDTO getStreet() {
        return street;
    }

    public void setStreet(StreetDTO street) {
        this.street = street;
    }

    public CoordinateDTO getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(CoordinateDTO coordinates) {
        this.coordinates = coordinates;
    }

}
