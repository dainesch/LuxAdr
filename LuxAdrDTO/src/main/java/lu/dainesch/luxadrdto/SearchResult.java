package lu.dainesch.luxadrdto;

import java.io.Serializable;
import java.util.List;


public abstract class SearchResult implements Serializable {

    private int corrId;
    private int count;

    public SearchResult() {
    }

    public SearchResult(SearchRequest req) {
        this.corrId = req.getCorrId();
        this.count = 0;
    }

    public SearchResult(SearchRequest req, List<?> results) {
        this.corrId = req.getCorrId();
        this.count = results.size();
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


}
