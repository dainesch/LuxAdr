
package lu.dainesch.luxadrdto;

import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class SearchResultAdrEntry extends SearchResult {
    
    private List<AdrEntry> results = Collections.EMPTY_LIST;

    public SearchResultAdrEntry() {
    }

    public SearchResultAdrEntry(SearchRequest req) {
        super(req);
    }

    public SearchResultAdrEntry(SearchRequest req, List<AdrEntry> results) {
        super(req, results);
        this.results = results;
    }

    public List<AdrEntry> getResults() {
        return results;
    }

    public void setResults(List<AdrEntry> results) {
        this.results = results;
    }
    
    
    
}
