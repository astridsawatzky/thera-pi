package therapi.abrechnung.split;

import static org.junit.Assert.*;

import org.junit.Test;

import core.Disziplin;
import rezept.Money;
import rezept.Rezeptnummer;

public class SplittingTest {
    @Test
    public  void ohneSplittingWirddiePauschaleunveraendertzurueckgegeben() {
        Splitting hacke= new Splitting ();
        Rezeptnummer nr = new Rezeptnummer(Disziplin.ER, 14225 );
        Money pausch = new Money(5.0);
        assertEquals(pausch,  hacke.berechnePauschale(nr, pausch));
    }

    @Test
    public void beiSplitterRezeptenistkeinePauschalefaellig() throws Exception {
        new Splitting().add(new Rezeptnummer(Disziplin.ER, 14226 ));

        Splitting hacke= new Splitting ();

        Money pausch = new Money(5.0);
        assertEquals(Money.ZERO,  hacke.berechnePauschale(new Rezeptnummer(Disziplin.ER, 14226 ), pausch));
    }
}
