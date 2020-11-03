package rezept;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.BeforeClass;
import org.junit.Test;

import mandant.IK;

public class RezeptTest {
    private static RezeptDto rezDto;


    @BeforeClass
    public static void initForAllTests() {
        try {
            rezDto = new RezeptDto(new IK("123456789"));
        } catch (Exception e) {
            fail("Need running DB connection for these tests");
        }

    }

    @Test
    public void rezConstructor() {
        Rezept rez = new Rezept();
        assertTrue(rez.getRezNr() == null);
    }

    @Test
    public void rezCompareCopied() {
        Rezept rez = rezDto.byRezeptNr("ER1").orElse(new Rezept());
        // make sure we really got a rezept:
        assertTrue(rez.getRezNr() != null && !rez.getRezNr().isEmpty());
        // a deep-copy of Rezept should be equal to its original
        // (Well, this actually is arguable - it *may* contain other RezNr & RezID
        //      - would we still like them to be same?)
        assertTrue(rez.equals(new Rezept(rez)));
        assertTrue(rez.hashCode() == (new Rezept(rez)).hashCode());
        assertFalse(rez.equals(new Rezept()));
    }

    @Test
    public void rezAnzahlTermine() {
        Rezept rez = rezDto.byRezeptNr("ER1").orElse(new Rezept());
        // make sure we really got a rezept:
        assertTrue(rez.getRezNr() != null && !rez.getRezNr().isEmpty());
        assertTrue("Test-Rezept hat ein paar Termine", rez.AnzahlTermineInRezept() > 0);
        rez.setTermine(null);
        assertTrue("Nach dem Loeschen mit null sollten keine mehr drin sein", rez.AnzahlTermineInRezept() == 0);
        rez.setTermine("");
        assertTrue("Nach dem Loeschen mit leer-String sollten keine mehr drin sein", rez.AnzahlTermineInRezept() == 0);
        rez.setTermine("10.03.2020@KA@@54110,54002@2020-03-10");
        assertTrue("Nach dem Setzen sollte genau 1'er drin sein", rez.AnzahlTermineInRezept() == 1);
        assertTrue("Gesetzter Termin sollte dem gesetztem Wert entsprechen",
                    "10.03.2020@KA@@54110,54002@2020-03-10".equals(rez.getTermine()));
    }



}
