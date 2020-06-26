package verkauf;

import static org.junit.Assert.*;

import java.time.LocalDate;

import org.junit.Test;

public class MwSTFristTest {

    @Test
    public void mwstVollIs16inJulyAndDecember2020() throws Exception {
        assertEquals(16, MwSTFrist.of(LocalDate.of(2020, 7, 1))
                                  .vollerSatz());
        assertEquals(16, MwSTFrist.of(LocalDate.of(2020, 8, 21))
                                  .vollerSatz());
        assertEquals(16, MwSTFrist.of(LocalDate.of(2020, 12, 31))
                                  .vollerSatz());
    }

    @Test
    public void mwstVollIs19beforeJuly2020() throws Exception {
        assertEquals(19, MwSTFrist.of(LocalDate.of(2020, 6, 30))
                                  .vollerSatz());
        LocalDate einfuehrung19 = LocalDate.of(2007, 1, 1);
        assertEquals(19, MwSTFrist.of(einfuehrung19)
                                  .vollerSatz());
    }

    @Test
    public void mwstVollIs19afterDec2020() throws Exception {
        assertEquals(19, MwSTFrist.of(LocalDate.of(2021, 1, 1))
                                  .vollerSatz());
    }

    @Test
    public void mwstVermindertIs5inJulyAndDecember2020() throws Exception {
        assertEquals(5, MwSTFrist.of(LocalDate.of(2020, 7, 1))
                                 .verminderterSatz());
        assertEquals(5, MwSTFrist.of(LocalDate.of(2020, 8, 21))
                                 .verminderterSatz());
        assertEquals(5, MwSTFrist.of(LocalDate.of(2020, 12, 31))
                                 .verminderterSatz());
    }

    @Test
    public void mwstVermindertIs7beforeJuly2020() throws Exception {
        assertEquals(7, MwSTFrist.of(LocalDate.of(2020, 6, 30))
                                 .verminderterSatz());
        LocalDate einfuehrung7 = LocalDate.of(2007, 1, 1);
        assertEquals(7, MwSTFrist.of(einfuehrung7)
                                 .verminderterSatz());
    }

    @Test
    public void mwstVermindertIs7afterDec2020() throws Exception {
        assertEquals(7, MwSTFrist.of(LocalDate.of(2021, 1, 1))
                                 .verminderterSatz());
    }
}
