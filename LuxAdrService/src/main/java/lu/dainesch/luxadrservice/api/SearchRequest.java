package lu.dainesch.luxadrservice.api;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SearchRequest implements Serializable {

    private int corrId;
    private int maxResults;
    private String value;

    public int getCorrId() {
        return corrId;
    }

    public void setCorrId(int corrId) {
        this.corrId = corrId;
    }

    public int getMaxResults() {
        return maxResults;
    }

    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isValid() {
        return value != null && !value.isEmpty() && maxResults > 0;
    }
}
