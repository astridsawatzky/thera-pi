package opRgaf.rezept;

import static org.junit.Assert.*;

import org.junit.Test;

import core.Disziplin;

public class RezeptnummerTest {

    @Test
    public void disziplinenWerdenErkannt() throws Exception {

        for (Disziplin dis : Disziplin.values()) {
            assertSame(dis.toString(), dis, new Rezeptnummer(dis.toString() + "1").disziplin());
        }
    }

    @Test
    public void disziplinenVonStorniertenRechnungenWerdenErkannt() throws Exception {
        for (Disziplin dis : Disziplin.values()) {
            assertSame(dis.toString(), dis, new Rezeptnummer(dis.toString() + "1SundBemerkung").disziplin());
        }

    }

    @Test
    public void stornoBemerkungWirdgelesen() throws Exception {
        Rezeptnummer stornoReznr = new Rezeptnummer("ER1SundBemerkung");

        assertEquals(Disziplin.ER, stornoReznr.disziplin());
        assertTrue(stornoReznr.isStorniert());
        assertEquals("undBemerkung", stornoReznr.stornoVermerk());

    }

    @Test
    public void nichtstornierteLiestEmptyStringForBemerkung() throws Exception {
        Rezeptnummer stornoReznr = new Rezeptnummer("ER1");

        assertEquals(Disziplin.ER, stornoReznr.disziplin());
        assertFalse(stornoReznr.isStorniert());
        assertEquals("", stornoReznr.stornoVermerk());

    }

}
