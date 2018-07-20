package lu.dainesch.luxadrservice.input;

import java.io.FileInputStream;
import java.io.InputStream;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FixedParserTest {

    private static final Logger LOG = LoggerFactory.getLogger(FixedParserTest.class);

    public FixedParserTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @Test
    public void testParseLocalite() throws Exception {

        try (InputStream in = new FileInputStream("../LuxAdrAdmin/src/main/resources/demodata/LOCALITE");
                FixedParser parser = new FixedParser(in, 5, 40, 40, 2, 1, 10, 1, 10, 2, 1, 2, 1)) {

            while (parser.hasNext()) {
                LOG.info(parser.next().toString());
            }

        }

    }

}
