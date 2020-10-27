package terminKalender;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class KalenderPanelTest {

    @Test
    public void extractsSingleLetterAfterBackslash() throws Exception {

        assertEquals("H", new KalenderPanel().extractColLetter("\\H"));
        assertEquals("H", new KalenderPanel().extractColLetter("\\H_"));

    }

    @Test
    public void codeCanBeAnywhereInTheString() throws Exception {
        assertEquals("H", new KalenderPanel().extractColLetter("abc\\H_#++lasdölj"));

    }

    @Test
    public void codenotcontained() throws Exception {
        assertEquals("", new KalenderPanel().extractColLetter("\\"));

    }

}
