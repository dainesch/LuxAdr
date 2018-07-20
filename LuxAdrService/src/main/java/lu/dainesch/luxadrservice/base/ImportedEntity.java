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
    @JoinColumn(name = "IMP_SINCE", nullable = false)
    protected Import since;

    @ManyToOne
    @JoinColumn(name = "IMP_UNTIL")
    protected Import until;

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Import getSince() {
        return since;
    }

    public void setSince(Import since) {
        this.since = since;
    }

    public Import getUntil() {
        return until;
    }

    public void setUntil(Import until) {
        this.until = until;
    }

}
