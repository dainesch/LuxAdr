package lu.dainesch.luxadrservice.search;

import java.io.Serializable;
import java.util.StringTokenizer;
import lu.dainesch.luxadrservice.adr.AddressFormater;
import lu.dainesch.luxadrservice.adr.entity.Building;
import lu.dainesch.luxadrservice.adr.entity.HouseNumber;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LatLonPoint;
import org.apache.lucene.document.StoredField;

import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BoostQuery;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

public class AdrSearchEntry implements Serializable {

    private static final SearchType TYPE = SearchType.ADDRESS;
    private static final String ID = "id";
    private static final String T = "type";
    private static final String ADR = "address";
    private static final String PC = "postalCode";
    private static final String NUM = "number";
    private static final String ALL = "all";

    private final Long id;
    private final String address;

    private final Building building;
    private final HouseNumber number;

    public AdrSearchEntry(Building b, HouseNumber number) {
        this.building = b;
        this.number = number;
        this.id = building.getId();
        this.address = AddressFormater.formatNormal(b, number);
    }

    AdrSearchEntry(Document doc) {
        this.id = doc.getField(ID).numericValue().longValue();
        this.address = doc.get(ADR);
        this.building = null;
        this.number = null;
    }

    Document toDocument() {
        Document doc = new Document();
        doc.add(new StoredField(ID, id));
        doc.add(new StringField(T, TYPE.toString(), Field.Store.YES));
        doc.add(new StringField(PC, building.getPostalCode().getCode(), Field.Store.NO));
        doc.add(new StringField(NUM, number.getNumber(), Field.Store.NO));
        doc.add(new StringField(ADR, address, Field.Store.YES));

        StringBuilder all = new StringBuilder(number.getNumber());

        all.append(" ").append(building.getStreet().getName());
        if (!building.getStreet().getAltNames().isEmpty()) {
            building.getStreet().getAltNames().forEach(n -> {
                all.append(" ").append(n.getName());
            });
        }

        all.append(" L-").append(building.getPostalCode().getCode());

        all.append(" ").append(building.getStreet().getLocality().getName());
        if (!building.getStreet().getLocality().getAltNames().isEmpty()) {
            building.getStreet().getLocality().getAltNames().forEach(n -> {
                all.append(" ").append(n.getName());
            });
        }

        doc.add(new TextField(ALL, all.toString(), Field.Store.NO));

        if (building.getCoordinates() != null) {
            doc.add(new LatLonPoint("pos", building.getCoordinates().getLatitude(), building.getCoordinates().getLongitude()));

        }

        return doc;
    }

    static Query getQuery(String search, Analyzer analyser) throws ParseException {
        BooleanQuery.Builder b = new BooleanQuery.Builder();
        StringTokenizer tok = new StringTokenizer(search, ", ");

        // filter by type
        b.add(new TermQuery(new Term("type", TYPE.toString())), BooleanClause.Occur.MUST);

        String num = AddressFormater.extractNumber(search);
        String pc = AddressFormater.extractPostcode(search);

        while (tok.hasMoreTokens()) {
            String st = QueryParser.escape(tok.nextToken());

            if (num != null && AddressFormater.isNumber(st)) {
                b.add(new BoostQuery(new TermQuery(new Term(NUM, st)), 2), BooleanClause.Occur.SHOULD);
            } else if (pc != null && AddressFormater.isPostCode(st)) {
                b.add(new BoostQuery(new TermQuery(new Term(PC, st)), 2), BooleanClause.Occur.SHOULD);
            }
            b.add(new FuzzyQuery(new Term(ALL, st)), BooleanClause.Occur.SHOULD);
        }

        return b.build();
    }

    Term getTerm() {
        return new Term("id", String.valueOf(id));
    }

    public String getAddress() {
        return address;
    }

    public Long getId() {
        return id;
    }

}
