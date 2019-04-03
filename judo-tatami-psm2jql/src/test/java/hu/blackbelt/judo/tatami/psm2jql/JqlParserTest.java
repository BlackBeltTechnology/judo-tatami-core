package hu.blackbelt.judo.tatami.psm2jql;

import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

@Slf4j
public class JqlParserTest {

    private JqlParser jqlParser;

    @Before
    public void setUp() throws Exception {
        jqlParser = new JqlParser();
    }

    @After
    public void tearDown() {
        jqlParser = null;
    }

    @Test
    public void testParser() throws Exception {

        long st = System.currentTimeMillis();
        for (int i = 0; i<10000; i++) {
            jqlParser.parse("self.quantity" + Integer.toString(i) + " * self.unitPrice * (1 - self.discount)");
        }
        log.info("10000 expression parsing: " + Long.toString(System.currentTimeMillis() - st));
    }
}
