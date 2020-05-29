package update;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Arrays;

import sql.DatenquellenFactory;

public class VerkaufsTabellenUpdate extends Update {



    @Override
    protected boolean postCondition(DatenquellenFactory dq) {

        return !tableStructureIsOk(dq);
    }

    @Override
    protected void execute(DatenquellenFactory dq) {

        try (Connection conn = dq.createConnection(); Statement statement = conn.createStatement();) {
            statement.addBatch("ALTER TABLE verkartikel\n"
                    + "        MODIFY COLUMN `preis` decimal(10,2) NOT NULL DEFAULT '0.00',\n"
                    + "        MODIFY COLUMN `mwst` decimal(10,2) NOT NULL DEFAULT '0.00',\n"
                    + "        MODIFY COLUMN `lagerstand` decimal(10,2) NOT NULL DEFAULT '0.00',\n"
                    + "        MODIFY COLUMN `einkaufspreis` decimal(10,2) NOT NULL DEFAULT '0.00';");
            statement.addBatch("ALTER TABLE verkfaktura\n"
                    + "        MODIFY COLUMN `art_einzelpreis` decimal(10,2) NOT NULL DEFAULT '0.00',\n"
                    + "        MODIFY COLUMN `art_mwst` decimal(10,2) NOT NULL DEFAULT '0.00',\n"
                    + "        MODIFY COLUMN `anzahl` decimal(10,2) NOT NULL DEFAULT '0.00';");
            statement.addBatch("ALTER TABLE verkliste\n"
                    + "        MODIFY COLUMN `v_betrag` decimal(10,2) NOT NULL DEFAULT '0.00',\n"
                    + "        MODIFY COLUMN `v_mwst7` decimal(10,2) NOT NULL DEFAULT '0.00',\n"
                    + "        MODIFY COLUMN `v_mwst19` decimal(10,2) NOT NULL DEFAULT '0.00',\n"
                    + "        MODIFY COLUMN `v_offen` decimal(10,2) NOT NULL DEFAULT '0.00';");
            int[] result = statement.executeBatch();

            System.out.println( "verkauftabellen update returned: " + Arrays.toString(result ));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected boolean preCondition(DatenquellenFactory dq) {
       return !tableStructureIsOk(dq);
    }

    private boolean tableStructureIsOk(DatenquellenFactory dq) {

        boolean itsOK = true;
        try (Connection conn = dq.createConnection();
                Statement statement = conn.createStatement();
                ResultSet rs = statement.executeQuery("select  v_offen,\n" + "    v_betrag,\n" + "    v_mwst7,\n"
                        + "    v_mwst19,\n" + "    art_einzelpreis,\n" + "    art_mwst,\n" + "    anzahl\n"
                        + "    preis,\n" + "    mwst, \n" + "    lagerstand,\n"
                        + "    einkaufspreis from  verkliste,verkfaktura, verkartikel WHERE 0=1;")) {
            ResultSetMetaData rsMetaData = rs.getMetaData();
            for (int i = 1; i <= rsMetaData.getColumnCount(); i++) {

                itsOK = itsOK && rsMetaData.getColumnType(i) == Types.DECIMAL;

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return itsOK;

    }

    public static void main(String[] args) {

        VerkaufsTabellenUpdate vu = new VerkaufsTabellenUpdate();
        DueUpdates du = new DueUpdates(new DatenquellenFactory("123456789"));
        du.add(vu);
        du.execute();

    }

}
