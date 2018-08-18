package lu.dainesch.luxadrservice.search;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import lu.dainesch.luxadrservice.base.ConfigHandler;
import lu.dainesch.luxadrservice.base.ConfigType;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.fr.FrenchAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.LatLonPoint;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Startup
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class LuceneSingleton {

    private static final Logger LOG = LoggerFactory.getLogger(LuceneSingleton.class);

    @Inject
    private ConfigHandler confHand;

    private boolean enabled;
    private Analyzer analyzer;
    private Directory directory;
    private IndexReader reader;

    @PostConstruct
    public synchronized void init() {
        enabled = confHand.getValue(ConfigType.LUCENE_ENABLED).getBoolean();
        if (reader != null) {
            // close first
            shutDown();
        }
        if (!enabled) {
            return;
        }
        Path path = Paths.get(confHand.getValue(ConfigType.LUCENE_DATA_DIR).getValue());
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
                LOG.info("Creating index path " + path);
            } catch (IOException ex) {
                LOG.error("Error creating lucence path. Disabling ...", ex);
                enabled = false;
                return;
            }
        }

        try {
            directory = FSDirectory.open(path);
            analyzer = new FrenchAnalyzer();

        } catch (IOException ex) {
            LOG.error("Error initializing lucence", ex);
            enabled = false;
        }
    }

    public synchronized void wipeData() throws SearchException {
        if (!enabled) {
            return;
        }
        try {
            shutDown();
            Path path = Paths.get(confHand.getValue(ConfigType.LUCENE_DATA_DIR).getValue());
            Files.walk(path)
                    .map(Path::toFile)
                    .sorted((o1, o2) -> -o1.compareTo(o2))
                    .forEach(File::delete);
            Files.createDirectories(path);
            init();
        } catch (IOException ex) {
            throw new SearchException("Error wiping data", ex);
        }
    }

    public synchronized void addToIndex(List<AdrSearchEntry> entries) throws SearchException {
        if (!enabled) {
            return;
        }

        IndexWriter writer = null;
        try {
            if (reader != null) {
                reader.close();
                reader = null;
            }

            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
            iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
            writer = new IndexWriter(directory, iwc);

            for (AdrSearchEntry entry : entries) {
                writer.updateDocument(entry.getTerm(), entry.toDocument());
            }
            writer.commit();

        } catch (IOException ex) {
            throw new SearchException("Error indexing data", ex);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ex) {
                    throw new SearchException("Error indexing data", ex);
                }
            }
        }

    }

    public Set<AdrSearchEntry> getSearchResults(String search, int maxResults) throws SearchException {
        LinkedHashSet<AdrSearchEntry> ret = new LinkedHashSet<>();
        if (!isEnabled() || search == null || search.trim().isEmpty()) {
            return ret;
        }

        try {
            IndexReader read = getReader();

            IndexSearcher searcher = new IndexSearcher(read);

            TopDocs results = searcher.search(AdrSearchEntry.getQuery(search, analyzer), maxResults);
            ScoreDoc[] hits = results.scoreDocs;

            for (ScoreDoc sd : hits) {
                Document doc = searcher.doc(sd.doc);
                AdrSearchEntry res = new AdrSearchEntry(doc);
                if (!ret.contains(res)) {
                    ret.add(res);
                }
            }
            return ret;
        } catch (IOException | ParseException ex) {
            throw new SearchException("Error searching data", ex);
        }

    }

    public Set<AdrSearchEntry> getBuildingsInDistance(float latitude, float longitude, float distMeter, int maxResults) throws SearchException {
        LinkedHashSet<AdrSearchEntry> ret = new LinkedHashSet<>();
        if (!isEnabled()) {
            return ret;
        }

        try {
            IndexReader read = getReader();

            IndexSearcher searcher = new IndexSearcher(read);

            TopDocs results = searcher.search(LatLonPoint.newDistanceQuery(AdrSearchEntry.POS, latitude, longitude, distMeter), maxResults);
            ScoreDoc[] hits = results.scoreDocs;

            for (ScoreDoc sd : hits) {
                Document doc = searcher.doc(sd.doc);
                AdrSearchEntry res = new AdrSearchEntry(doc);
                if (!ret.contains(res)) {
                    ret.add(res);
                }
            }
            return ret;
        } catch (IOException ex) {
            throw new SearchException("Error searching data", ex);
        }

    }

    public AdrSearchEntry getNearest(float latitude, float longitude) throws SearchException {
        if (!isEnabled()) {
            return null;
        }
        try {
            IndexReader read = getReader();

            IndexSearcher searcher = new IndexSearcher(read);

            TopDocs results = LatLonPoint.nearest(searcher, AdrSearchEntry.POS, latitude, longitude, 1);
            ScoreDoc[] hits = results.scoreDocs;

            for (ScoreDoc sd : hits) {
                Document doc = searcher.doc(sd.doc);
                return new AdrSearchEntry(doc);

            }
            return null;
        } catch (IOException ex) {
            throw new SearchException("Error searching data", ex);
        }
    }

    private synchronized IndexReader getReader() throws IOException {
        if (reader == null) {
            reader = DirectoryReader.open(directory);
        }
        return reader;
    }

    public synchronized boolean isEnabled() {
        return enabled;
    }

    public boolean hasData() {
        if (!isEnabled()) {
            return false;
        }
        try {
            return getReader().numDocs() > 0;
        } catch (IOException ex) {
            LOG.error("Error checkng index status", ex);
            return false;
        }
    }

    @PreDestroy
    public synchronized void shutDown() {
        try {
            if (reader != null) {
                reader.close();
            }
            if (analyzer != null) {
                analyzer.close();
            }
        } catch (IOException ex) {
            LOG.error("Error during lucene shutdown", ex);
        }
    }

}
