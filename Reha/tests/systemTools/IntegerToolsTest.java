package systemTools;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class IntegerToolsTest {

    @Test
    public void nullValueReturns0() throws Exception {

        assertEquals(0, IntegerTools.trailNullAndRetInt(null));

    }

    @Test
    public void emptyStringsreturn0() throws Exception {

        assertEquals(0, IntegerTools.trailNullAndRetInt(""));

    }

    @Test
    public void trailing0AreTrimmed() throws Exception {

        assertEquals(123, IntegerTools.trailNullAndRetInt("0000000000123"));
    }

    
    //einzelne Ziffern werden geschluckt
    @Test
    public void singlenumbersAreTrimmrdTo0() throws Exception {

        assertEquals(0, IntegerTools.trailNullAndRetInt("0000000000003"));

    }

    //alles nullen wirft Exception
    @Test(expected = NumberFormatException.class)
    public void repeatet0ValuesReturn0() throws Exception {

        assertEquals(0, IntegerTools.trailNullAndRetInt("0000000000000"));

    }

}
