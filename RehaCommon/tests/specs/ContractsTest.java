package specs;

import org.junit.Test;

public class ContractsTest {


    @Test
    public void contractsmetarenice() throws Exception {
        Contracts.require(true, "Das ist wahr");

        Contracts.require(2>1,"ja zwei sollte immer gr\u00f6\u00dfer sein als eins");
    }

    @Test (expected = ContractException.class)
    public void contractsNotMetThrowUp() throws Exception {
        Contracts.require(false, "das ist falsch");
    }

}
