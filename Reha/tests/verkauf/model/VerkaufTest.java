package verkauf.model;

import static org.junit.Assert.*;

import org.junit.Test;

import verkauf.MwSTSatz;

public class VerkaufTest {
    private final class Testartikel extends ArtikelVerkauf {
        @Override
        void update() {
        }
    }

    @Test
    public void verkaufmwstentwirdkorrektberechnet() throws Exception {
        Verkauf verk = new Verkauf();

        ArtikelVerkauf posten = new Testartikel();
        posten.setMwst(16);
        posten.setPreis(116);
        posten.setAnzahl(1);
        verk.fuegeArtikelHinzu(posten );
        assertEquals(MwSTSatz.now().vollerSatz(), verk.getBetrag19() , 0.001);
        ArtikelVerkauf posten2 = new Testartikel();
        posten2.setMwst(5);
        posten2.setPreis(105);
        posten2.setAnzahl(1);
        verk.fuegeArtikelHinzu(posten2);
        assertEquals(MwSTSatz.now().verminderterSatz(), verk.getBetrag7() , 0.001);

        verk.fuegeArtikelHinzu(posten2);

        assertEquals(MwSTSatz.now().verminderterSatz()*2, verk.getBetrag7() , 0.001);

    }






}
