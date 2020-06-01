package opRgaf;

import org.junit.Test;

import specs.ContractException;

public class KennungTest {

    @Test (expected = ContractException.class)
    public void testKennungStringTooManyCommas() throws Exception {
        new Kennung("a,b,c,d");
    }

    @Test (expected = ContractException.class)
    public void testKennungStringTooFewCommas() throws Exception {
        new Kennung("a,b");
    }

}
