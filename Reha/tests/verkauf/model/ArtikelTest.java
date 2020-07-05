package verkauf.model;

import static org.junit.Assert.*;

import org.junit.Test;

import verkauf.MwSt;

public class ArtikelTest {

    @Test
    public void artikelsetMwst16ResultsinVoll() throws Exception {
        Artikel a = new Artikel() {
            @Override
            void update() {
            }
        };
        a.setMwst(16.0);
        assertEquals(MwSt.voll, a.getMwst());


    }
    @Test
    public void artikelsetMwst5ResultsInVermindert() throws Exception {
        Artikel a = new Artikel() {
            @Override
            void update() {
            }
        };
        a.setMwst(5.0);
        assertEquals(MwSt.vermindert, a.getMwst());


    }
    @Test
    public void artikelConvert19und7und0ToMwSt() throws Exception {
        Artikel a = new Artikel() {
            @Override
            void update() {
            }
        };
        a.setMwst(7.0);
        assertEquals(MwSt.vermindert, a.getMwst());
        a.setMwst(19.0);
        assertEquals(MwSt.voll, a.getMwst());

        a.setMwst(0);
        assertEquals(MwSt.frei, a.getMwst());


    }
}
