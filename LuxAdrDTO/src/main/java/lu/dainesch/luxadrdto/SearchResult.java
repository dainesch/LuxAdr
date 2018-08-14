package lu.dainesch.luxadrdto;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class SearchResult<D extends Serializable> implements Serializable {

    private int corrId;
    private int count;
    private List<D> results;

    public SearchResult() {
    }

    public SearchResult(SearchRequest req) {
        this.corrId = req.getCorrId();
        this.count = 0;
        this.results = Collections.EMPTY_LIST;
    }

    public SearchResult(SearchRequest req, List<D> results) {
        this.corrId = req.getCorrId();
        this.count = results.size();
        this.results = results;
    }

    public int getCorrId() {
        return corrId;
    }

    public void setCorrId(int corrId) {
        this.corrId = corrId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<D> getResults() {
        return results;
    }

    public void setResults(List<D> results) {
        this.results = results;
    }

}
