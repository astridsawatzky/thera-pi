package opRgaf;

import static org.junit.Assert.assertFalse;

import java.time.LocalDate;
import java.util.List;

import org.junit.Test;

import core.Disziplin;
import mandant.Mandant;
import opRgaf.rezept.Money;
import opRgaf.rezept.Rezeptnummer;

public class OffenePostenDTOTest {
    Mandant m = new mandant.Mandant("123456789", "blabla");
    @Test
    public void testall() throws Exception {
       List<OffenePosten> result = new OffenePostenDTO(m.ik()).all();
       assertFalse(result.isEmpty());
    }



    @Test
    public void crud() throws Exception {
        OffenePosten op = new OffenePosten();
        op.rezNummer = new Rezeptnummer(Disziplin.FT, 1);
        op.gesamtBetrag = new Money(20);
        op.offen=op.gesamtBetrag;
        op.rgBetrag= new Money(30);
        op.bearbeitungsGebuehr=new Money(10.00);
        op.rgDatum = LocalDate.now();
        op.kennung = new Kennung("a,b,1.1.2012");

      assertFalse(  new OffenePostenDTO(m.ik()).generatePayment(op).isEmpty());





    }

}
