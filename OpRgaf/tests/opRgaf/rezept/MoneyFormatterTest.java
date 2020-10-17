package opRgaf.rezept;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MoneyFormatterTest {

    @Test
    public void value2string() throws Exception {
        MoneyFormatter mf = new MoneyFormatter();
        assertEquals(new Money("1205.00"), mf.stringToValue("1.205,00"));
        assertEquals(new Money("1205.00"), mf.stringToValue("1205,00"));
        assertEquals(new Money("1205.00"), mf.stringToValue("1205.00"));
        assertEquals(new Money("1205.00"), mf.stringToValue("1.205,00"));

    }

}
