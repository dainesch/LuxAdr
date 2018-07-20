package lu.dainesch.luxadrservice.adr.entity;

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
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import lu.dainesch.luxadrservice.base.ImportedEntity;

@Entity
@Table(name = "POSTALCODE", indexes = {
    @Index(name = "IDX_POSTALCODE_CODE", columnList = "CODE")
})
@NamedQueries({
    @NamedQuery(name = "postalcode.invalidate", query = "UPDATE PostalCode SET active = false"),
    @NamedQuery(name = "postalcode.deleted", query = "UPDATE PostalCode SET until=:imp WHERE active = false and until is null"),
    @NamedQuery(name = "postalcode.by.code", query = "SELECT p from PostalCode p where p.code = :code")
})
public class PostalCode extends ImportedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "PostalCode")
    @TableGenerator(name = "PostalCode")
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

}
