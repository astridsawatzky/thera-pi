package hauptFenster;

import java.sql.DriverManager;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import org.thera_pi.updater.Version;

import CommonTools.ExUndHop;
import systemEinstellungen.SystemConfig;

final class DbNachladen implements Runnable {
    @Override
    public void run() {
        final String sDB = "SQL";
        final Reha obj = Reha.instance;
        if (Reha.instance.conn != null) {
            try {
                Reha.instance.conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        try {
            if (sDB == "SQL") {
                new SocketClient().setzeInitStand("Datenbank initialisieren und öffnen");
                obj.conn = DriverManager.getConnection(SystemConfig.vDatenBank.get(0)
                                                                              .get(1)
                        + "?jdbcCompliantTruncation=false",
                        SystemConfig.vDatenBank.get(0)
                                               .get(3),
                        SystemConfig.vDatenBank.get(0)
                                               .get(4));
            }
            int nurmaschine = SystemConfig.dieseMaschine.toString()
                                                        .lastIndexOf("/");
            new ExUndHop().setzeStatement("delete from flexlock where maschine like '%"
                    + SystemConfig.dieseMaschine.toString()
                                                .substring(0, nurmaschine)
                    + "%'");
            if (obj.dbLabel != null) {
                String db = SystemConfig.vDatenBank.get(0)
                                                   .get(1)
                                                   .replace("jdbc:mysql://", "");
                db = db.substring(0, db.indexOf("/"));
                obj.dbLabel.setText(Version.aktuelleVersion + db);
            }
            obj.sqlInfo.setConnection(obj.conn);
            Reha.DbOk = true;

        } catch (final SQLException ex) {
            Reha.DbOk = false;
            Reha.nachladenDB = -1;
            Reha.nachladenDB = JOptionPane.showConfirmDialog(Reha.getThisFrame(),
                    "Die Datenbank konnte nicht gestartet werden, erneuter Versuch?", "Wichtige Benuterzinfo",
                    JOptionPane.YES_NO_OPTION);

            while (Reha.nachladenDB < 0) {
                try {
                    Thread.sleep(25);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (Reha.nachladenDB == JOptionPane.YES_OPTION) {
                new Thread(new DbNachladen()).start();
            }
            return;
        }
        return;
    }
}