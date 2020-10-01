package systemEinstellungen.abrechnung.formulare;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;

import org.junit.Test;

import CommonTools.ini.INIFile;
import CommonTools.ini.Settings;


public class parameterMapperTest {


    @Test
    public void readSettings() throws Exception {
        Settings setting=   new INIFile(new ByteArrayInputStream(TestInis.abrechnungini.getBytes()), "testgkvsetting");

     GKVAbrechnungsParameter result = new parameterMapper() .readSettings(setting);

     assertEquals(         true ,result.askBefore302Mail);
     assertEquals(         true ,result.direktAusdruck);

     assertFalse(result.gkv.isPrinterEinstellungsAusVorlage());
     assertTrue(result.gkv.begleitzettelOnly);
     assertEquals("HMRechnungGKV.ott", result.gkv.template);
     assertEquals("gkvdrucker", result.gkv.printer.getName());

     assertFalse(result.bg.isPrinterEinstellungsAusVorlage());
     assertEquals(4,result.bg.numberOfPrintOuts);
     assertEquals("HMRechnungBG.ott", result.bg.template);
     assertEquals("BGEDrucker", result.bg.printer.getName());


     assertFalse(result.privat.isPrinterEinstellungsAusVorlage());
     assertEquals(2,result.privat.numberOfPrintOuts);
     assertEquals("HMRechnungPrivat.ott", result.privat.template);
     assertEquals("privatdrucker", result.privat.printer.getName());


     assertFalse(result.taxierung.isPrinterEinstellungsAusVorlage());
     assertEquals(1,result.taxierung.numberOfPrintOuts);
     assertEquals("", result.taxierung.template);
     assertEquals("taxdrucker", result.taxierung.printer.getName());

    }


}
