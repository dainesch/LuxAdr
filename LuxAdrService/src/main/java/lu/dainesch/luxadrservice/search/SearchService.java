package lu.dainesch.luxadrservice.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import lu.dainesch.luxadrdto.entity.PostCodeType;
import lu.dainesch.luxadrservice.adr.entity.Building;
import lu.dainesch.luxadrservice.adr.handler.BuildingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class SearchService {

    private static final Logger LOG = LoggerFactory.getLogger(SearchService.class);

    private static final int BATCH_SIZE = 1000;

    @Inject
    private LuceneSingleton lucene;
    @Inject
    private BuildingHandler buildHand;

    public Set<AdrSearchEntry> search(String search, int maxResults) throws SearchException {
        return lucene.getSearchResults(search, maxResults);
    }

    public void indexData() throws SearchException {
        // first wipe
        lucene.wipeData();
        
        int count = 0;

        List<Building> buildings = buildHand.getBuildingsPaginated(count, BATCH_SIZE, PostCodeType.Normal);
        List<AdrSearchEntry> entries = new ArrayList<>();

        while (!buildings.isEmpty()) {

            buildings.forEach(b -> {
                b.getNumbers().forEach(n -> {
                    entries.add(new AdrSearchEntry(b, n));
                });
            });
            LOG.info("Prepared " + entries.size() + " search entries...");

            lucene.addToIndex(entries);

            entries.clear();
            count += buildings.size();
            LOG.info("Indexed " + count + " search entries.");

            buildings = buildHand.getBuildingsPaginated(count, BATCH_SIZE, PostCodeType.Normal);
        }

        LOG.info("Done indexing ");

    }

    public void wipeData() throws SearchException {
        lucene.wipeData();
    }
}
