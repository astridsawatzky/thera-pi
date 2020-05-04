package terminKalender;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.*;

import org.junit.Test;

public class KollegenTest {

    @Test
    public void kollegenAreSortedByMatchcode() throws Exception {
        Kollegen a = new Kollegen("a", "name", 1, "abteil", "zeigen");
        Kollegen grossA = new Kollegen("A", "name", 1, "abteil", "zeigen");

        Kollegen b = new Kollegen("b", "name", 1, "abteil", "zeigen");


        assertThat(a, lessThan(b));
        assertThat(grossA, lessThan(b));
        assertThat(a, greaterThan(grossA));
    }

    @Test
    public void kollegenWithSameMatchcodeAreComparedByReihe() throws Exception {
        Kollegen a1 = new Kollegen("a", "name", 1, "abteil", "zeigen");
        Kollegen a2 = new Kollegen("a", "name", 2, "abteil", "zeigen");

        assertThat(a1, lessThan(a2));

    }

}
