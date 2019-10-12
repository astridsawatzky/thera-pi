package hauptFenster;

import java.sql.DriverManager;
import java.sql.SQLException;

import systemEinstellungen.SystemConfig;

/*******************************************/
final class HilfeDatenbankStarten implements Runnable {

    void StarteDB() {

    }

    @Override
    public void run() {
        final Reha obj = Reha.instance;

        // final String sDB = "SQL";
        if (obj.hilfeConn != null) {
            try {
                obj.hilfeConn.close();
            } catch (final SQLException e) {
            }
        }
        try {
            Class.forName(SystemConfig.hmHilfeServer.get("HilfeDBTreiber"))
                 .newInstance();
            Reha.HilfeDbOk = true;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            Reha.instance.hilfeConn = DriverManager.getConnection(SystemConfig.hmHilfeServer.get("HilfeDBLogin"),
                    SystemConfig.hmHilfeServer.get("HilfeDBUser"), SystemConfig.hmHilfeServer.get("HilfeDBPassword"));
        } catch (final SQLException ex) {
            Reha.HilfeDbOk = false;
            return;
        }
        return;
    }
}