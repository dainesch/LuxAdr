package lu.dainesch.luxadrservice.base;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "IMPORT_LOG")
public class ImportLog implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "ImportLog")
    @TableGenerator(name = "ImportLog")
    private Long id;

    @Column(name = "CREATED", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column(name = "LOG")
    private String log;

    @Enumerated(EnumType.STRING)
    @Column(name = "STEP")
    private ImportStep step;

    @ManyToOne
    @JoinColumn(name = "IMP_ID", nullable = false)
    private Import imp;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public Import getImp() {
        return imp;
    }

    public void setImp(Import imp) {
        this.imp = imp;
    }

    public ImportStep getStep() {
        return step;
    }

    public void setStep(ImportStep step) {
        this.step = step;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + Objects.hashCode(this.id);
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
        final ImportLog other = (ImportLog) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ImportLog{" + "created=" + created + ", log=" + log + ", step=" + step + '}';
    }

}
