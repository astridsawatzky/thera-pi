package systemEinstellungen.abrechnung.formulare;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.ini4j.Ini;
import org.junit.Test;

import CommonTools.ini.INIFile;


public class parameterMapperTest {


    @Test
    public void readSettings() throws Exception {
        INIFile setting=   new INIFile(new ByteArrayInputStream(TestInis.initialString.getBytes()), "testgkvsetting");

     GKVAbrechnungsParameter result = new parameterMapper() .readSettings(setting);

     assertEquals(         true ,result.askBefore302Mail);
     assertEquals(         true ,result.direktAusdruck);

     assertFalse(result.gkv.isPrinterEinstellungsAusVorlage());
     assertTrue(result.gkv.begleitzettelOnly);
     assertEquals("HMRechnungGKV.ott", result.gkv.template);
     assertEquals("KONICA MINOLTA bizhub C25(DE:28:3C)", result.gkv.printer.getName());
    }


}
