package CommonTools;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.junit.Test;

public class DatFunkTest extends DatFunk {

    @Test
    public void testSDatInDeutsch() {
        assertEquals("  .  .    ", DatFunk.sDatInDeutsch(null));
        assertEquals("21.03.2012", DatFunk.sDatInDeutsch("2012-03-21"));
        assertEquals("1.1.2012", DatFunk.sDatInDeutsch("2012-1-1"));

    }

    @Test
    public void testSDatInSQL() {
        assertEquals("2022-05-01", DatFunk.sDatInSQL("1.5.2022"));
    }

    @Test
    public void testSHeute() {
        LocalDate date = LocalDate.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        assertEquals(date.format(dtf), DatFunk.sHeute());
    }

    @Test
    public void testSDatPlusTage() {
        assertEquals("02.03.2011", DatFunk.sDatPlusTage("28.02.2011", 2));
        assertEquals("01.03.2012", DatFunk.sDatPlusTage("28.02.2012", 2));
        assertEquals("01.03.2000", DatFunk.sDatPlusTage("28.02.2000", 2));
    }

    @Test
    public void testWochenTag() {
        assertEquals("Montag", DatFunk.WochenTag("28.02.2011"));
        assertEquals("Sonntag", DatFunk.WochenTag("06.02.2011"));
    }

    @Test
    public void testTagDerWoche() {
        assertEquals(1, DatFunk.TagDerWoche("28.02.2011"));
        assertEquals(7, DatFunk.TagDerWoche("6.02.2011"));

    }

    @Test
    public void testTageDifferenz() {
        assertEquals(365, DatFunk.TageDifferenz("28.02.2011", "28.02.2012"));
        assertEquals(366, DatFunk.TageDifferenz("28.02.2012", "28.02.2013"));
        assertEquals(365, DatFunk.TageDifferenz("28.02.2013", "28.02.2014"));

        assertEquals(-8, DatFunk.TageDifferenz("28.02.2013", "20.02.2013"));

    }

    @Test
    public void testWocheErster() {
        assertEquals("28.02.2011", DatFunk.WocheErster("28.02.2011"));
        assertEquals("27.02.2012", DatFunk.WocheErster("28.02.2012"));

        assertEquals("25.02.2013", DatFunk.WocheErster("28.02.2013"));
    }

    @Test
    public void testWocheLetzter() {
        assertEquals("06.03.2011", DatFunk.WocheLetzter("28.02.2011"));
        assertEquals("04.03.2012", DatFunk.WocheLetzter("28.02.2012"));

        assertEquals("03.03.2013", DatFunk.WocheLetzter("28.02.2013"));

    }

    @Test
    public void testKalenderWoche() {
        assertEquals(9, DatFunk.KalenderWoche("28.02.2011"));
        assertEquals(9, DatFunk.KalenderWoche("28.02.2012"));

        assertEquals(9, DatFunk.KalenderWoche("28.02.2013"));
        assertEquals(13, DatFunk.KalenderWoche("28.03.2013"));
        assertEquals(17, DatFunk.KalenderWoche("28.04.2013"));

        assertEquals(22, DatFunk.KalenderWoche("28.05.2013"));

    }

    @Test
    public void testDatumsWert() {
        assertEquals("28.05.2013", DatFunk.WertInDatum(DatFunk.DatumsWert("2013-05-28")));

        assertEquals("28.05.2013", DatFunk.WertInDatum(DatFunk.DatumsWert("28.05.2013")));

    }

    @Test
    public void testWertInDatum() {
        assertEquals("01.01.1970", DatFunk.WertInDatum(1L));
    }

    @Test
    public void testGeradeWoche() {
        assertEquals(false, DatFunk.GeradeWoche("28.02.2013"));
        assertEquals(false, DatFunk.GeradeWoche("28.03.2013"));
        assertEquals(false, DatFunk.GeradeWoche("28.04.2013"));

        assertEquals(true, DatFunk.GeradeWoche("28.05.2013"));
    }

    @Test
    public void testUnter18() {
        assertEquals(false, DatFunk.Unter18("20.02.2018", "19.02.2000"));
        assertEquals(true, DatFunk.Unter18("20.02.2018", "21.02.2000"));
        assertEquals(false, DatFunk.Unter18("20.02.2018", "20.02.2000"));
    }

    @Test
    public void testSchaltjahr() {
        assertEquals(true, DatFunk.Schaltjahr(2000));
        assertEquals(false, DatFunk.Schaltjahr(2001));
        assertEquals(true, DatFunk.Schaltjahr(2004));
        assertEquals(false, DatFunk.Schaltjahr(2005));
        assertEquals(false, DatFunk.Schaltjahr(2100));
    }

    @Test
    public void testJahreDifferenz() {
        assertEquals(18, DatFunk.JahreDifferenz("20.02.2018", "19.02.2000"));
        assertEquals(18, DatFunk.JahreDifferenz("20.02.2018", "20.02.2000"));
        assertEquals(17, DatFunk.JahreDifferenz("20.02.2018", "21.02.2000"));
        assertEquals(18, DatFunk.JahreDifferenz("20.02.2018", "21.02.1999"));
        assertEquals(0, DatFunk.JahreDifferenz("01.01.2018", "31.12.2017"));
        assertEquals(0, DatFunk.JahreDifferenz("31.12.2017", "01.01.2018"));

    }

}
