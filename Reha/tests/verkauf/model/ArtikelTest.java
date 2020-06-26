package verkauf.model;

import static org.junit.Assert.*;

import org.junit.Test;

public class ArtikelTest {

    @Test
    public void artikelMwst16wird19() throws Exception {
        Artikel a = new Artikel() {
            @Override
            void update() {
            }
        };
        a.setMwst(16.0);
        assertEquals(19, a.getMwst(),0.001);


    }
    @Test
    public void artikelMwst5wird7() throws Exception {
        Artikel a = new Artikel() {
            @Override
            void update() {
            }
        };
        a.setMwst(5.0);
        assertEquals(7, a.getMwst(),0.001);


    }
    @Test
    public void artikelMwst19und7bleiben() throws Exception {
        Artikel a = new Artikel() {
            @Override
            void update() {
            }
        };
        a.setMwst(7.0);
        assertEquals(7, a.getMwst(),0.001);
        a.setMwst(19.0);
        assertEquals(19, a.getMwst(),0.001);

        a.setMwst(0);
        assertEquals(0, a.getMwst(),0.001);


    }
}
