package terminKalender;

import static org.junit.Assert.*;

import java.time.Duration;
import java.time.LocalTime;

import org.junit.Test;

public class TerminDTOTest {

    @Test
    public void emptyterminekommenalsconstantezurueck() throws Exception {
        assertSame(Termin.EMPTY, new TerminDTO(null, null, null, null, 0).toTermin());

    }

    @Test
    public void testName() throws Exception {
        Termin termin = new TerminDTO("bezeichnung", "notiz", "00:01", "23:05", 40).toTermin();
        assertEquals(termin.start, LocalTime.of(0, 1));
        assertEquals(termin.ende, LocalTime.of(23, 5));
        assertEquals(termin.dauer, Duration.ofMinutes(40));
    }

}
