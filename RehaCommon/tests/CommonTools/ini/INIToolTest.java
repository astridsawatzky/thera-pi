package CommonTools.ini;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

public class INIToolTest {

    @Test
    public void filesInIncontroleIniAreMeantForDatabase() throws Exception {

        String pfad = "./tests/resources/";
        INITool.init(pfad);

        assertEquals(2, INITool.anzahlInisInDB());
        assertTrue(Arrays.asList(INITool.dbInis).contains("color.ini"));
        assertTrue(Arrays.asList(INITool.dbInis).contains("james.ini"));
        assertFalse(Arrays.asList(INITool.dbInis).contains("michnicht.ini"));

    }

}
