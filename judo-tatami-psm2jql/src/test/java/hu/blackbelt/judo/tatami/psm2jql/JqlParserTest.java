package hu.blackbelt.judo.tatami.psm2jql;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
        jqlParser.parse("self.quantity * self.unitPrice * (1 - self.discount)");
    }
}
