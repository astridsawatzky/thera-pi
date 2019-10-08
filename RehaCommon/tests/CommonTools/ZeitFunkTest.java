package CommonTools;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ZeitFunkTest {
    @Test
    public void testName() throws Exception {

        assertEquals(330, ZeitFunk.ZeitDifferenzInMinuten("07:00", "12:30"));
        ;
        assertEquals(150, ZeitFunk.MinutenSeitMitternacht("02:30:00"));
        assertEquals("01:30:00", ZeitFunk.MinutenZuZeit(90));

    }

    @Test
    public void testZeitPlusMinuten() throws Exception {
        assertEquals("13:11:02", ZeitFunk.ZeitPlusMinuten("12:30:02", "41"));
        assertEquals("00:11", ZeitFunk.ZeitPlusMinuten("23:30", "41"));
    }

    @Test
    public void testZeitMinusMinuten() throws Exception {
        assertEquals("12:30", ZeitFunk.ZeitMinusMinuten("13:11", "41"));
        assertEquals("23:30", ZeitFunk.ZeitMinusMinuten("00:11", "41"));
    }

}
