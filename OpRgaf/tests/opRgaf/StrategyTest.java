package opRgaf;

import static org.junit.Assert.*;

import org.junit.Test;

public class StrategyTest {

    public static final Integer eins = 1;
    public static final Integer zwei = 2;
    public static final Integer drei = 3;
    public static final Integer nochmaldrei = 3;

    @Test
    public void testGleich() throws Exception {
        assertTrue("eins = eins", Strategy.gleich.compare(eins, eins));
        assertTrue("drei = nochmaldrei", Strategy.gleich.compare(drei, nochmaldrei));
        assertFalse("eins = drei", Strategy.gleich.compare(eins, drei));
    }

    @Test
    public void testkleiner() throws Exception {
        assertTrue("eins < zwei", Strategy.kleiner.compare(eins, zwei));
        assertFalse("eins < eins", Strategy.kleiner.compare(eins, eins));
        assertFalse("zwei < eins", Strategy.kleiner.compare(zwei, eins));

    }

    @Test
    public void testkleinerOderGleich() throws Exception {
        assertTrue("eins <= zwei", Strategy.kleinerOderGleich.compare(eins, zwei));
        assertTrue("eins <= eins", Strategy.kleinerOderGleich.compare(eins, eins));
        assertFalse("zwei <= eins", Strategy.kleinerOderGleich.compare(zwei, eins));

    }

    @Test
    public void testgroesser() throws Exception {
        assertFalse("eins > zwei", Strategy.groesser.compare(eins, zwei));
        assertFalse("eins > eins", Strategy.groesser.compare(eins, eins));
        assertTrue("zwei > eins", Strategy.groesser.compare(zwei, eins));

    }

    @Test
    public void testgroesserOderGleich() throws Exception {
        assertFalse("eins >= zwei", Strategy.groesserOderGleich.compare(eins, zwei));
        assertTrue("eins >= eins", Strategy.groesserOderGleich.compare(eins, eins));
        assertTrue("zwei >= eins", Strategy.groesserOderGleich.compare(zwei, eins));

    }
    @Test
    public void testUnleich() throws Exception {
        assertFalse("eins = eins", Strategy.ungleich.compare(eins, eins));
        assertFalse("drei = nochmaldrei", Strategy.ungleich.compare(drei, nochmaldrei));
        assertTrue("eins = drei", Strategy.ungleich.compare(eins, drei));
    }

  

}
