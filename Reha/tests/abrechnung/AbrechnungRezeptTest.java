package abrechnung;

import org.junit.Test;

import CommonTools.SqlInfo;
import hauptFenster.UIFSplitPane;
import sql.DatenquellenFactory;
import systemEinstellungen.SystemConfig;

public class AbrechnungRezeptTest {

    @Test
    public final void testMacheEDIFACT() throws Exception {
        SystemConfig.AbrechnungParameter();
        AbrechnungRezept abrrez = new AbrechnungRezept(null, null) {

            @Override
            void keepDayTreeSize(UIFSplitPane sPane) {

            }
        };
        SqlInfo sql = new SqlInfo();
        sql.setConnection(new DatenquellenFactory("987654321").createConnection());
        abrrez.sucheRezept("ER1448");
        abrrez.macheEDIFACT();

    }

}
