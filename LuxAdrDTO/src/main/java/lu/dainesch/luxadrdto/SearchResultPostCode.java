package lu.dainesch.luxadrdto;

import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import lu.dainesch.luxadrdto.entity.PostalCodeDTO;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class SearchResultPostCode extends SearchResult {

    private List<PostalCodeDTO> results = Collections.EMPTY_LIST;

    public SearchResultPostCode() {
    }

    public SearchResultPostCode(SearchRequest req) {
        super(req);
    }

    public SearchResultPostCode(SearchRequest req, List<PostalCodeDTO> results) {
        super(req, results);
        this.results = results;
    }

    public List<PostalCodeDTO> getResults() {
        return results;
    }

    public void setResults(List<PostalCodeDTO> results) {
        this.results = results;
    }

}
