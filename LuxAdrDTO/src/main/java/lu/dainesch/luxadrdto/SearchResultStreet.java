package lu.dainesch.luxadrdto;

import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import lu.dainesch.luxadrdto.entity.StreetDTO;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class SearchResultStreet extends SearchResult {

    private List<StreetDTO> results = Collections.EMPTY_LIST;

    public SearchResultStreet() {
    }

    public SearchResultStreet(SearchRequest req) {
        super(req);
    }

    public SearchResultStreet(SearchRequest req, List<StreetDTO> results) {
        super(req, results);
        this.results = results;
    }

    public List<StreetDTO> getResults() {
        return results;
    }

    public void setResults(List<StreetDTO> results) {
        this.results = results;
    }

}
