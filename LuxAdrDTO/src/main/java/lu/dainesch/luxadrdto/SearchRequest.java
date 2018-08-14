package lu.dainesch.luxadrdto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class SearchRequest implements Serializable {

    @Schema(description = "Correlation id. Server will return the same id you sent to avoid overtaking autocomplete requests.", example = "999")
    private int corrId;
    @Schema(description = "Maximum results you request. The server however limits the value if it is too big", example = "10")
    private int maxResults;
    @Schema(description = "Should the value you search be at the beginning of the text or just included somewhere?")
    private Boolean beginning;
    @Schema(description = "The value you search for.", example = "text")
    private String value;

    public SearchRequest() {
    }

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

    public Boolean isBeginning() {
        return beginning;
    }

    public void setBeginning(Boolean beginning) {
        this.beginning = beginning;
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
