package textBausteine;

import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import CommonTools.ini.INIFile;
import CommonTools.ini.Settings;
import crypt.Verschluesseln;
import logging.Logging;
import sqlTools.SystemEinstellungen;

public class textbaus implements WindowListener {
    /*
     * Ab hier Deklaration der Klassenobjekte und -Variablen
     *
     */
    JFrame jFrame;
    public static textbaus thisClass = null;
    public static boolean DbOk;
    public Connection conn = null;
    public String dbIpAndName = "jdbc:mysql://192.168.2.2:3306/dbf";
    public String dbUser = "entwickler";
    public String dbPassword = "entwickler";

    /*
     *
     * Ende Deklaration
     *
     */
    public static void main(String[] args) {
        new Logging("tbedit");
        System.out.println(" Name des Betriebssystems: " + System.getProperty("os.name"));
        System.out.println("      Benutzerverzeichnis: " + java.lang.System.getProperty("user.dir")
                                                                           .replaceAll("\\\\", "/"));
        System.out.println("Installierte Java-Version: " + java.lang.System.getProperty("java.version"));
        System.out.println("         Java-Verzeichnis: " + java.lang.System.getProperty("java.home")
                                                                           .replaceAll("\\\\", "/"));
        textbaus lbaust = new textbaus();
        lbaust.getJFrame(args);
    }

    public JFrame getJFrame(String[] args) {
        try {
            UIManager.setLookAndFeel("com.jgoodies.looks.plastic.PlasticXPLookAndFeel");
        } catch (ClassNotFoundException e) {

            e.printStackTrace();
        } catch (InstantiationException e) {

            e.printStackTrace();
        } catch (IllegalAccessException e) {

            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {

            e.printStackTrace();
        }

        thisClass = this;
        if (args.length > 0) {
            System.out.println("Starte mit Startparameter 1 = " + args[0]);
            System.out.println("Starte mit Startparameter 2 = " + args[1]);
            Settings ini = new INIFile(args[0]);
            dbIpAndName = ini.getStringProperty("DatenBank", "DBKontakt1");
            dbUser = ini.getStringProperty("DatenBank", "DBBenutzer1");
            String pw = ini.getStringProperty("DatenBank", "DBPasswort1");
            String decrypted = null;
            if (pw != null) {
                Verschluesseln man = Verschluesseln.getInstance();
                decrypted = man.decrypt(pw);
            } else {
                decrypted = new String("");
            }
            dbPassword = decrypted.toString();

        }
        DatenbankStarten dbstart = new DatenbankStarten();
        dbstart.run();
        SystemEinstellungen.ladeGelenke(args);
        jFrame = new JFrame();

        jFrame.addWindowListener(this);
        jFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        jFrame.setSize(630, 543);
        jFrame.setPreferredSize(new Dimension(630, 543));
        jFrame.setTitle("Textbausteine für Therapiebericht erstellen / ändern / löschen");
        jFrame.setLocationRelativeTo(null);

        jFrame.getContentPane()
              .add(new testbauoberflaeche());
        jFrame.pack();
        jFrame.setVisible(true);

        return jFrame;
    }

    @Override
    public void windowActivated(WindowEvent arg0) {

    }

    @Override
    public void windowClosed(WindowEvent arg0) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void windowClosing(WindowEvent arg0) {

    }

    @Override
    public void windowDeactivated(WindowEvent arg0) {

    }

    @Override
    public void windowDeiconified(WindowEvent arg0) {

    }

    @Override
    public void windowIconified(WindowEvent arg0) {

    }

    @Override
    public void windowOpened(WindowEvent arg0) {

    }
}

/*************************/
final class DatenbankStarten implements Runnable {
    private void StarteDB() {
        final textbaus obj = textbaus.thisClass;

        final String sDB = "SQL";
        if (obj.conn != null) {
            try {
                obj.conn.close();
            } catch (final SQLException e) {
            }
        }
        try {
            Class.forName("com.mysql.jdbc.Driver")
                 .newInstance();

        } catch (final Exception e) {
            System.out.println(sDB + "Treiberfehler: " + e.getMessage());
            textbaus.DbOk = false;
            return;
        }
        try {
            obj.conn = DriverManager.getConnection(obj.dbIpAndName + "?jdbcCompliantTruncation=false", obj.dbUser,
                    obj.dbPassword);
            textbaus.DbOk = true;
            System.out.println("Datenbankkontakt hergestellt");
        } catch (final SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            textbaus.DbOk = false;

        }
        return;
    }

    @Override
    public void run() {
        StarteDB();
    }
}
