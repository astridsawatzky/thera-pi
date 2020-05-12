package terminKalender;

import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import mitarbeiter.Mitarbeiter;

public class KollegenTest {

    @Test
    public void kollegenAreSortedByMatchcode() throws Exception {

        Mitarbeiter ma_a = new Mitarbeiter();
        ma_a.setMatchcode("aa");
        ma_a.setKalzeile(1);
        ma_a.setAbteilung("abteil");
        ma_a.setNicht_zeig(false);
        Kollegen kol_a = Kollegen.of(ma_a);
        Mitarbeiter ma_grossA = new Mitarbeiter();
        ma_grossA.setMatchcode("Ab");
        ma_grossA.setKalzeile(1);
        ma_grossA.setAbteilung("abteil");
        ma_grossA.setNicht_zeig(false);
        Kollegen grossA =Kollegen.of(ma_grossA);
        Mitarbeiter ma_b = new Mitarbeiter();
        ma_b.setMatchcode("b");
        ma_b.setKalzeile(1);
        ma_b.setAbteilung("abteil");
        ma_b.setNicht_zeig(false);
        Kollegen b = Kollegen.of(ma_b);

        assertThat(kol_a, lessThan(grossA));
        assertThat(kol_a, lessThan(b));
        assertThat(grossA, lessThan(b));
    }

    @Test
    public void kollegenWithSameMatchcodeAreComparedByReihe() throws Exception {
        Mitarbeiter ma_a1 = new Mitarbeiter();
        ma_a1.setMatchcode("a");
        ma_a1.setKalzeile(1);
        ma_a1.setAbteilung("abteil");
        ma_a1.setNicht_zeig(false);
        Mitarbeiter ma_a2 = new Mitarbeiter();
        ma_a2.setMatchcode("a");
        ma_a2.setKalzeile(2);
        ma_a2.setAbteilung("abteil");
        ma_a2.setNicht_zeig(false);
        Kollegen a1 = Kollegen.of(ma_a1);
        Kollegen a2 = Kollegen.of(ma_a2);

        assertThat(a1, lessThan(a2));

    }

}
