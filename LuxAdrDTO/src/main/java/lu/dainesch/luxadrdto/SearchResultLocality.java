package lu.dainesch.luxadrdto;

import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import lu.dainesch.luxadrdto.entity.LocalityDTO;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class SearchResultLocality extends SearchResult {

    private List<LocalityDTO> results = Collections.EMPTY_LIST;

    public SearchResultLocality() {
    }

    public SearchResultLocality(SearchRequest req) {
        super(req);
    }

    public SearchResultLocality(SearchRequest req, List<LocalityDTO> results) {
        super(req, results);
        this.results = results;
    }

    public List<LocalityDTO> getResults() {
        return results;
    }

    public void setResults(List<LocalityDTO> results) {
        this.results = results;
    }

}
