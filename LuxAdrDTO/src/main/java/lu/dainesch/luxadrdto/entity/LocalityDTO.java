package lu.dainesch.luxadrdto.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class LocalityDTO implements Serializable {

    private Long id;
    private boolean active;

    private String name;
    private boolean city;

    private Set<AlternateNameDTO> altNames = new HashSet<>();

    public LocalityDTO(Long id, boolean active, String name, boolean city) {
        this.id = id;
        this.active = active;
        this.name = name;
        this.city = city;
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

    public boolean isCity() {
        return city;
    }

    public void setCity(boolean city) {
        this.city = city;
    }

    public Set<AlternateNameDTO> getAltNames() {
        return altNames;
    }

    public void setAltNames(Set<AlternateNameDTO> altNames) {
        this.altNames = altNames;
    }

}
