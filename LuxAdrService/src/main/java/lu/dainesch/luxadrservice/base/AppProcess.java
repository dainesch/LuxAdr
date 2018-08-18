package lu.dainesch.luxadrservice.base;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@NamedQueries({
    @NamedQuery(name = "process.latest",
            query = "Select i from AppProcess i "
            + "where i.start = (Select MAX(i2.start) from AppProcess i2)")
    ,
    @NamedQuery(name = "process.by.state.step",
            query = "Select distinct p from AppProcess p "
            + "join p.logEntries l "
            + "where p.state = :state "
            + "and l.step = :step "
            + "order by p.end desc ")
})
@Table(name = "APP_PROCESS")
public class AppProcess implements Serializable {

    public static enum ImportState {
        RUNNING, COMPLETED, ERROR
    }

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "AppProcess")
    @TableGenerator(name = "AppProcess")
    @Column(name = "PROC_ID")
    private Long id;

    @Column(name = "START_TIME", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date start;

    @Column(name = "END_TIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date end;

    @Enumerated(EnumType.STRING)
    @Column(name = "IMP_STATE", nullable = false)
    private ImportState state;

    @OneToMany(mappedBy = "process")
    private Set<ProcessingLog> logEntries = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public ImportState getState() {
        return state;
    }

    public void setState(ImportState state) {
        this.state = state;
    }

    public Set<ProcessingLog> getLogEntries() {
        return logEntries;
    }

    public void setLogEntries(Set<ProcessingLog> logEntries) {
        this.logEntries = logEntries;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AppProcess)) {
            return false;
        }
        AppProcess other = (AppProcess) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Import{" + "id=" + id + ", start=" + start + ", end=" + end + '}';
    }

}
