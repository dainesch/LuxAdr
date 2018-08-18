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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    @NamedQuery(name = "processinglog.by.proc",
            query = "Select l from ProcessingLog l "
            + "where l.process = :proc "
            + "order by l.created desc")
})
@Table(name = "PROCESS_LOG")
public class ProcessingLog implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "ProcessingLog")
    @TableGenerator(name = "ProcessingLog")
    @Column(name = "LOG_ID")
    private Long id;

    @Column(name = "CREATED", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column(name = "LOG")
    private String log;

    @Enumerated(EnumType.STRING)
    @Column(name = "STEP")
    private ProcessingStep step;

    @ManyToOne
    @XmlTransient
    @JoinColumn(name = "PROC_ID", nullable = false)
    private AppProcess process;

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

    public AppProcess getProcess() {
        return process;
    }

    public void setProcess(AppProcess proc) {
        this.process = proc;
    }

    public ProcessingStep getStep() {
        return step;
    }

    public void setStep(ProcessingStep step) {
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
        final ProcessingLog other = (ProcessingLog) obj;
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
