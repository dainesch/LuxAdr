package lu.dainesch.luxadrservice.input;

import java.io.FileInputStream;
import java.io.InputStream;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GeoParserTest {

    private static final Logger LOG = LoggerFactory.getLogger(GeoParserTest.class);

    public GeoParserTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @Test
    public void testParseLocalite() throws Exception {

        try (InputStream in = new FileInputStream("../LuxAdrAdmin/src/main/resources/demodata/addresses.geojson");
                GeoParser parser = new GeoParser(in)) {

            while (parser.hasNext()) {
                LOG.info(parser.next().toString());
                //parser.next();
            }
            LOG.info("Read "+parser.getCount());

        }

    }

}
