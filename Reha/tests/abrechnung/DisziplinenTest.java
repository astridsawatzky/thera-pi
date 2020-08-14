package abrechnung;

import static org.junit.Assert.assertEquals;

import java.util.Vector;

import org.junit.Test;

import CommonTools.JRtaComboBox;

public class DisziplinenTest {
    @Test
    public void testDisziplinen() throws Exception {
        Vector<Vector<String>> rezeptKlassenAktiv = new Vector<>();
        String initRezeptKlasse = null;
        boolean mitRs = true;
        new Disziplinen(rezeptKlassenAktiv, initRezeptKlasse, mitRs);
        // Just don't blow up to make us happy.
    }

    @Test
    public void testGetComboBox() throws Exception {
        Vector<Vector<String>> rezeptKlassenAktiv = new Vector<>();
        String initRezeptKlasse = null;
        boolean mitRs = true;
        JRtaComboBox cb = new Disziplinen(rezeptKlassenAktiv, initRezeptKlasse, mitRs).getComboBox();
        assertEquals(0, cb.getSelectedIndex());
        JRtaComboBox cb2 = new Disziplinen(rezeptKlassenAktiv, "Logopädie-Rezept", mitRs).getComboBox();
        assertEquals(3, cb2.getSelectedIndex());
    }

    @Test
    public void testGetDisziKurzFromRK() throws Exception {
        Vector<Vector<String>> rezeptKlassenAktiv = new Vector<>();
        String initRezeptKlasse = null;
        boolean mitRs = true;
        Disziplinen diszis = new Disziplinen(rezeptKlassenAktiv, initRezeptKlasse, mitRs);
        assertEquals("Physio", diszis.getDisziKurzFromRK("KG"));
        assertEquals("Massage", diszis.getDisziKurzFromRK("MA"));
        assertEquals("Ergo", diszis.getDisziKurzFromRK("ER"));
        assertEquals("Logo", diszis.getDisziKurzFromRK("LO"));
        assertEquals("Podo", diszis.getDisziKurzFromRK("PO"));
        assertEquals("Rsport", diszis.getDisziKurzFromRK("RS"));
        assertEquals("Ftrain", diszis.getDisziKurzFromRK("FT"));
    }

    @Test
    public void testGetCurrDisziKurz() throws Exception {
        Vector<Vector<String>> rezeptKlassenAktiv = new Vector<>();
        String initRezeptKlasse = null;
        boolean mitRs = true;
        Disziplinen diszis = new Disziplinen(rezeptKlassenAktiv, initRezeptKlasse, mitRs);
        assertEquals("Physio", diszis.getCurrDisziKurz());

        Vector<String> logo = new Vector<>();
        logo.add("Logopädie-Rezept");
        logo.add("LO");
        rezeptKlassenAktiv.add(logo);
        Disziplinen diszis2 = new Disziplinen(rezeptKlassenAktiv, initRezeptKlasse, mitRs);
        assertEquals("weird behaviour to return a class that isnt even active","Physio", diszis2.getCurrDisziKurz());
        Disziplinen diszis3 = new Disziplinen(rezeptKlassenAktiv, "Logopädie-Rezept", mitRs);
        assertEquals("Logo", diszis3.getCurrDisziKurz());
    }
}
