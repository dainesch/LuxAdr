package lu.dainesch.luxadrdto.entity;

import java.io.Serializable;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class PostalCodeDTO implements Serializable {

    private Long id;
    private boolean active;
    private String code;
    private PostCodeType type;
    private Integer minMailbox;
    private Integer maxMailbox;

    public PostalCodeDTO(Long id, boolean active, String code, PostCodeType type, Integer minMailbox, Integer maxMailbox) {
        this.id = id;
        this.active = active;
        this.code = code;
        this.type = type;
        this.minMailbox = minMailbox;
        this.maxMailbox = maxMailbox;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public PostCodeType getType() {
        return type;
    }

    public void setType(PostCodeType type) {
        this.type = type;
    }

    public Integer getMinMailbox() {
        return minMailbox;
    }

    public void setMinMailbox(Integer minMailbox) {
        this.minMailbox = minMailbox;
    }

    public Integer getMaxMailbox() {
        return maxMailbox;
    }

    public void setMaxMailbox(Integer maxMailbox) {
        this.maxMailbox = maxMailbox;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.code);
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
        final PostalCodeDTO other = (PostalCodeDTO) obj;
        if (!Objects.equals(this.code, other.code)) {
            return false;
        }
        return true;
    }

}
