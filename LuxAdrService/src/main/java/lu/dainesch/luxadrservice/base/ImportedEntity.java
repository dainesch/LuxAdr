package lu.dainesch.luxadrservice.base;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class ImportedEntity implements Serializable {

    @Column(name = "ACTIVE", nullable = false)
    protected boolean active = true;

    @ManyToOne
    @JoinColumn(name = "PROC_SINCE", nullable = false)
    protected AppProcess since;

    @ManyToOne
    @JoinColumn(name = "PROC_CURR", nullable = false)
    protected AppProcess current;

    @ManyToOne
    @JoinColumn(name = "PROC_UNTIL")
    protected AppProcess until;

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public AppProcess getSince() {
        return since;
    }

    public void setSince(AppProcess since) {
        this.since = since;
    }

    public AppProcess getUntil() {
        return until;
    }

    public void setUntil(AppProcess until) {
        this.until = until;
    }

    public AppProcess getCurrent() {
        return current;
    }

    public void setCurrent(AppProcess current) {
        this.current = current;
    }

}
