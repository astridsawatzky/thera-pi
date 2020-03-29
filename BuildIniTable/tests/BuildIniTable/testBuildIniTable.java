package BuildIniTable;

import org.junit.Test;
import static org.junit.Assert.*;
import BuildIniTable.ProcessPanel;
// For hand-coded test-setup:
import java.util.Vector;
// For test-ini driven tests:
import CommonTools.INIFile;


public class testBuildIniTable {

    public BuildIniTable buildIniTable = new BuildIniTable();
    public ProcessPanel procPanel;
    
    @Test
    public void testSetGetPfadzurmandini() {
        buildIniTable.setPfadzurmandini("test");
        assertEquals("test", buildIniTable.getPfadzurmandini());
    }
    
    @Test
    public void testMandIniExist() {
        assertEquals(true, BuildIniTable.mandIniExist("./tests/resources/ini/mandanten.ini"));
        assertEquals(false, BuildIniTable.mandIniExist("./tests/resources/ini/keinemand.ini"));
    }
    
    @Test
    public void testMandantTesten() {
        // Simple test only, since non-existing mandanten.ini will invoke file-chooser
        this.buildIniTable.setPfadzurmandini("./tests/resources/ini/mandanten.ini");
        assertEquals("./tests/resources/ini/mandanten.ini", this.buildIniTable.mandantTesten());
    }
    
    // ProgressPanel tests
    @Test
    public void testPPGetIniList() {
        /* 
         * getIniList actually does 2 things:
         *   - it populates the PP Vector holding the names of inis
         *   - it populates the check-box-table to choose inis from
         */
        this.buildIniTable.thisClass = this.buildIniTable;
        this.buildIniTable.thisClass.pfadzurini = "./tests/resources/ini";
        
        // Use test-mandanten.ini to setup test-data:
        /*
        this.buildIniTable.thisClass.setPfadzurmandini("./tests/resources/ini/mandanten.ini");
        INIFile ini = new INIFile("./tests/resources/ini/mandanten.ini");
        this.buildIniTable.anzahlmandanten = Integer.parseInt(ini.getStringProperty(
                                                     "TheraPiMandanten", "AnzahlMandanten"));
        for (int i = 0; i < this.buildIniTable.anzahlmandanten; i++) {
            this.buildIniTable.mandantIkvec.add(ini.getStringProperty("TheraPiMandanten", "MAND-IK" + Integer.toString(i + 1)));
            this.buildIniTable.mandantNamevec.add(ini.getStringProperty("TheraPiMandanten", "MAND-NAME" + Integer.toString(i + 1)));
        }
        System.out.println("mandantIkvec: " + this.buildIniTable.mandantIkvec);
        System.out.println("mandantNamevec: " + this.buildIniTable.mandantNamevec);
        */
        
        // Hand-coded Mandant-data:
        this.buildIniTable.anzahlmandanten = 1;
        this.buildIniTable.mandantIkvec.add("123456789");
        this.buildIniTable.mandantNamevec.add("Test Mandant 1");
        
        ProcessPanel pan = new ProcessPanel("UnitTest");
        this.procPanel = pan;
        pan.tabmod = pan.new MyIniTableModel();
        pan.inivec.clear();
        assertEquals(0, pan.inivec.size()); 
        pan.getIniList();
        assertTrue("Check size of ProgressPanel.inivec >0 after calling getIniList",
                             pan.inivec.size() > 0); // Unit test
        assertEquals("test.ini", pan.inivec.get(0)); // Integration test
    }
}
