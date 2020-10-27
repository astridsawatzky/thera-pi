package rehaKassenbuch;

import java.awt.Cursor;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import CommonTools.SqlInfo;
import ag.ion.bion.officelayer.application.IOfficeApplication;
import ch.qos.logback.classic.util.ContextInitializer;
import gui.LaF;
import office.OOTools;
import sql.DatenquellenFactory;
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J;

public class RehaKassenbuch implements WindowListener {

    public boolean DbOk;
    JFrame jFrame;
    public static JFrame thisFrame = null;
    public Connection conn;
    public static RehaKassenbuch thisClass;

    public static Optional<IOfficeApplication> officeapplication;

    public String dieseMaschine = null;

    public final Cursor wartenCursor = new Cursor(Cursor.WAIT_CURSOR);
    public final Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);

    public static String progHome;
    public static String aktIK;

    public boolean isLibreOffice;

    public SqlInfo sqlInfo;

    public static void main(String[] args) throws InterruptedException, ExecutionException, TimeoutException {

        System.setProperty(ContextInitializer.CONFIG_FILE_PROPERTY, "./logs/conf/kasse.xml");

        SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();

        if (args.length <= 0 && !false) {
            JOptionPane.showMessageDialog(null,
                    "Keine Datenbankparameter übergeben!\nReha-Kassenbuch kann nicht gestartet werden");
            return;
        }

        progHome = args[0];
        aktIK = args[1];

    final    RehaKassenbuch application = new RehaKassenbuch();
        application.getInstance();
        application.sqlInfo = new SqlInfo();
        officeapplication = OOTools.initOffice(progHome, aktIK);
        System.out.println("hole daten aus INI-Datei " + progHome);

        ExecutorService laufbursche = Executors.newFixedThreadPool(2);
        Future<Boolean> dbstartErfolgreich = laufbursche.submit(new Callable<Boolean>() {

            @Override
            public Boolean call() throws Exception {
                try {

                    application.conn = new DatenquellenFactory(aktIK).createConnection();
                    application.sqlInfo.setConnection(RehaKassenbuch.thisClass.conn);
                    System.out.println("Datenbankkontakt hergestellt");
                    return true;
                } catch (final SQLException ex) {
                    System.out.println("SQLException: " + ex.getMessage());
                    System.out.println("SQLState: " + ex.getSQLState());
                    System.out.println("VendorError: " + ex.getErrorCode());
                    return false;

                }

            }
        });
        
        
        if (dbstartErfolgreich.get(30, TimeUnit.SECONDS)) {
            application. DbOk = true;
        } else {
            JOptionPane.showMessageDialog(null,

                    "Datenbank konnte nicht geöffnet werden!\nTimeout nach 10 Sekunden Wartezeit!\nReha-Kassenbuch kann nicht gestartet werden");
            return;
        }
        application.getJFrame();

    }


    public JFrame getJFrame() {

        LaF.setPlastic();

        thisClass = this;
        jFrame = new JFrame();
        sqlInfo.setFrame(jFrame);
        jFrame.addWindowListener(this);
        jFrame.setSize(1000, 500);
        jFrame.setTitle("Thera-Pi  Kassenbuch erstellen / bearbeiten  [IK: " + aktIK + "] ");
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setLocationRelativeTo(null);
        RehaKassenbuchTab kbtab = new RehaKassenbuchTab();
        jFrame.getContentPane()
              .add(kbtab);
        kbtab.setHeader(0);
        jFrame.setVisible(true);
        thisFrame = jFrame;
        return jFrame;
    }

    /********************/

    public RehaKassenbuch getInstance() {
        thisClass = this;
        return this;
    }

    /*******************/



    @Override
    public void windowActivated(WindowEvent arg0) {
    }

    @Override
    public void windowClosed(WindowEvent arg0) {
        if (RehaKassenbuch.thisClass.conn != null) {
            try {
                RehaKassenbuch.thisClass.conn.close();
                System.out.println("Datenbankverbindung wurde geschlossen");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        System.exit(0);
    }

    @Override
    public void windowClosing(WindowEvent arg0) {
        if (RehaKassenbuch.thisClass.conn != null) {
            try {
                RehaKassenbuch.thisClass.conn.close();
                System.out.println("Datenbankverbindung wurde geschlossen");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        System.exit(0);
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
