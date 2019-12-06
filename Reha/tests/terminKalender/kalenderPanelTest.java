package terminKalender;

import static org.junit.Assert.*;

import org.junit.Test;

public class kalenderPanelTest {

    @Test
    public void extractsSingleLetterAfterBackslash() throws Exception {

        assertEquals("H", new kalenderPanel().extractColLetter("\\H"));
        assertEquals("H", new kalenderPanel().extractColLetter("\\H_"));

    }

    @Test
    public void codeCanBeAnywhereInTheString() throws Exception {
        assertEquals("H", new kalenderPanel().extractColLetter("abc\\H_#++lasdölj"));

    }

    @Test
    public void codenotcontained() throws Exception {
        assertEquals("", new kalenderPanel().extractColLetter("\\"));

    }

}
