package lu.dainesch.luxadrservice.adr.entity;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import lu.dainesch.luxadrdto.entity.PostCodeType;
import lu.dainesch.luxadrdto.entity.PostalCodeDTO;
import lu.dainesch.luxadrservice.base.ImportedEntity;

@Entity
@Table(name = "POSTALCODE", indexes = {
    @Index(name = "IDX_POSTALCODE_CODE", columnList = "CODE")
})
@Cacheable
@NamedQueries({
    @NamedQuery(name = "postalcode.invalidate", query = "UPDATE PostalCode SET active = false, until = :proc where current != :proc")
    ,
    @NamedQuery(name = "postalcode.by.code", query = "SELECT p from PostalCode p where p.code = :code")
            ,
    @NamedQuery(name = "postalcode.search.code", query = "SELECT p from PostalCode p "
            + "where p.code like :code "
            + "and p.active = true "
            + "order by p.code")
    ,
    @NamedQuery(name = "postalcode.by.id.streets",
            query = "SELECT distinct s FROM PostalCode c "
            + "join c.buildings b "
            + "join b.street s "
            + "where c.id = :id "
            + "order by s.sortValue")
    ,
    @NamedQuery(name = "postalcode.by.code.streets",
            query = "SELECT distinct s FROM PostalCode c "
            + "join c.buildings b "
            + "join b.street s "
            + "where c.code = :code "
            + "and c.active = true "
            + "and s.active = true "
            + "order by s.sortValue")
})
public class PostalCode extends ImportedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "PostalCode")
    @TableGenerator(name = "PostalCode")
    @Column(name = "PC_ID")
    private Long id;

    @Column(name = "CODE", length = 4, unique = true)
    private String code;

    @Column(name = "POSTOFFICE_NAME", length = 40, nullable = false)
    private String postOfficeName;

    @Column(name = "TYPE", nullable = false, length = 16)
    @Enumerated(EnumType.STRING)
    private PostCodeType type;

    @Column(name = "MIN_MAILBOX")
    private Integer minMailbox;

    @Column(name = "MAX_MAILBOX")
    private Integer maxMailbox;

    @OneToMany(mappedBy = "postalCode")
    private Set<Building> buildings = new HashSet<>();

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

    public String getPostOfficeName() {
        return postOfficeName;
    }

    public void setPostOfficeName(String postOfficeName) {
        this.postOfficeName = postOfficeName;
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

    public Set<Building> getBuildings() {
        return buildings;
    }

    public void setBuildings(Set<Building> buildings) {
        this.buildings = buildings;
    }

    public JsonObjectBuilder toJson() {
        JsonObjectBuilder ret = Json.createObjectBuilder()
                .add("id", id)
                .add("active", active)
                .add("code", code)
                .add("type", type.toString());
        if (type == PostCodeType.Mailbox) {
            ret.add("minMailbox", minMailbox);
            ret.add("maxMailbox", maxMailbox);
        }
        return ret;
    }
    
    public PostalCodeDTO toDTO() {
        return new PostalCodeDTO(id, active, code, type, minMailbox, maxMailbox);
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
        final PostalCode other = (PostalCode) obj;
        if (!Objects.equals(this.code, other.code)) {
            return false;
        }
        return true;
    }

}
