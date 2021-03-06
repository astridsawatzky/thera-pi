package hauptFenster;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

import javax.swing.JOptionPane;

import org.thera_pi.updater.Version;

import CommonTools.ExUndHop;
import systemEinstellungen.SystemConfig;
import systemEinstellungen.config.Datenbank;

final class DbNachladen implements Runnable {
    @Override
    public void run() {
        final String sDB = "SQL";
        final Reha rehaInstance = Reha.instance;
        if (Reha.instance.conn != null) {
            try {
                Reha.instance.conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        try {
            Datenbank datenbank=new Datenbank();
            if (sDB == "SQL") {
                new SocketClient().setzeInitStand("Datenbank initialisieren und öffnen");
                rehaInstance.conn = DriverManager.getConnection(datenbank.jdbcDB()
                        + "?jdbcCompliantTruncation=false&zeroDateTimeBehavior=convertToNull&autoReconnect=true",
                        datenbank. user(),
                        datenbank. password());
            }
            int nurmaschine = SystemConfig.dieseMaschine.toString()
                                                        .lastIndexOf("/");
            new ExUndHop().setzeStatement("delete from flexlock where maschine like '%"
                    + SystemConfig.dieseMaschine.toString()
                                                .substring(0, nurmaschine)
                    + "%'");
            if (rehaInstance.dbLabel != null) {
                String db = datenbank.jdbcDB()
                                                   .replace("jdbc:mysql://", "");
                db = db.substring(0, db.indexOf("/"));
                new Version();
                rehaInstance.dbLabel.setText(new Version().getReleaseDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "-DB=" + db);
            }
            rehaInstance.sqlInfo.setConnection(rehaInstance.conn);
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
