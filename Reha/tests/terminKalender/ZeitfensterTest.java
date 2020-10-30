package terminKalender;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import static java.time.temporal.ChronoUnit.*;
import org.junit.Test;

public class ZeitfensterTest {

    public static void main(String[] args) {
    Zeitfenster zf = new Zeitfenster(new Block("bob", "ER0815", "09:20", "73", "10:33", "5"));
    zf.showAndWait(200,200);
    }


    @Test
    public void mitternachtmorgensundabends() throws Exception {
        LocalTime morgens = LocalTime.of(0, 0);
        LocalTime _0_15 = LocalTime.of(0,15);
        assertEquals(_0_15, morgens.plus(15,MINUTES));
        LocalTime _0_30 = LocalTime.of(0,30);
        assertEquals(_0_30, morgens.plus(15,MINUTES).plus(15,MINUTES));
        assertEquals(30, morgens.until(_0_30, MINUTES));
        assertEquals("24 hours later is the same time", 0, morgens.until(morgens.plus(24,ChronoUnit.HOURS), ChronoUnit.HOURS));
        assertEquals("24 hours later is the same time", 0, morgens.until(morgens.plus(24*60,ChronoUnit.MINUTES), ChronoUnit.MINUTES));

        assertEquals(LocalDateTime.of(LocalDate.of(2020, 10, 1), LocalTime.MIDNIGHT),LocalDateTime.of(LocalDate.of(2020, 9, 30), LocalTime.MIDNIGHT).plus(24,HOURS) );
    }

}
