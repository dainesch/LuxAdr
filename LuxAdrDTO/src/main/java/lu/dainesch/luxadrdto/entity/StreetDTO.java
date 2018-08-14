package lu.dainesch.luxadrdto.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class StreetDTO implements Serializable {

    private Long id;
    private boolean active;

    private String name;
    private String streetCode;

    private LocalityDTO locality;
    private Set<AlternateNameDTO> altNames = new HashSet<>();

    public StreetDTO(Long id, boolean active, String name, String streetCode) {
        this.id = id;
        this.active = active;
        this.name = name;
        this.streetCode = streetCode;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalityDTO getLocality() {
        return locality;
    }

    public void setLocality(LocalityDTO locality) {
        this.locality = locality;
    }

    public String getStreetCode() {
        return streetCode;
    }

    public void setStreetCode(String streetCode) {
        this.streetCode = streetCode;
    }

    public Set<AlternateNameDTO> getAltNames() {
        return altNames;
    }

    public void setAltNames(Set<AlternateNameDTO> altNames) {
        this.altNames = altNames;
    }

}
