package reha301;

import java.awt.Cursor;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.jdesktop.swingworker.SwingWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import CommonTools.INIFile;
import CommonTools.SqlInfo;
import CommonTools.StartOOApplication;
import ag.ion.bion.officelayer.application.IOfficeApplication;
import ag.ion.bion.officelayer.application.OfficeApplicationException;
import io.RehaIOMessages;
import logging.Logging;
import sql.DatenquellenFactory;

public class Reha301 implements WindowListener {
    public static boolean DbOk;
    JFrame jFrame;
    public static JFrame thisFrame = null;
    public Connection conn;
    public static Reha301 thisClass;

    public static IOfficeApplication officeapplication;

    public String dieseMaschine = null;
    /*
     * public static String dbIpAndName = null; public static String dbUser = null;
     * public static String dbPassword = null;
     *
     */
    public final Cursor wartenCursor = new Cursor(Cursor.WAIT_CURSOR);
    public final Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);
    public final Cursor kreuzCursor = new Cursor(Cursor.CROSSHAIR_CURSOR);
    public final Cursor cmove = new Cursor(Cursor.MOVE_CURSOR); // @jve:decl-index=0:
    public final Cursor cnsize = new Cursor(Cursor.N_RESIZE_CURSOR); // @jve:decl-index=0:
    public final Cursor cnwsize = new Cursor(Cursor.NW_RESIZE_CURSOR); // @jve:decl-index=0:
    public final Cursor cnesize = new Cursor(Cursor.NE_RESIZE_CURSOR); // @jve:decl-index=0:
    public final Cursor cswsize = new Cursor(Cursor.SW_RESIZE_CURSOR); // @jve:decl-index=0:
    public final Cursor cwsize = new Cursor(Cursor.W_RESIZE_CURSOR); // @jve:decl-index=0:
    public final Cursor csesize = new Cursor(Cursor.SE_RESIZE_CURSOR); // @jve:decl-index=0:
    public final Cursor cssize = new Cursor(Cursor.S_RESIZE_CURSOR); // @jve:decl-index=0:
    public final Cursor cesize = new Cursor(Cursor.E_RESIZE_CURSOR); // @jve:decl-index=0:
    public final Cursor cdefault = new Cursor(Cursor.DEFAULT_CURSOR); // @jve:decl-index=0:


    public static String officeProgrammPfad = "C:/Program Files (x86)/LibreOffice 3";
    public static String officeNativePfad = "C:/RehaVerwaltung/Libraries/lib/openofficeorg/";
    public static String progHome = "C:/RehaVerwaltung/";
    public static String aktIK = "510841109";
    public static String hmRechnungPrivat = "C:/RehaVerwaltung/vorlagen/HMRechnungPrivatKopie.ott";
    public static String hmRechnungKasse = "C:/RehaVerwaltung/vorlagen/HMRechnungPrivatKopie.ott";
    public static String rhRechnungPrivat = "C:/RehaVerwaltung/vorlagen/HMRechnungPrivatKopie.ott";
    public static String rhRechnungKasse = "C:/RehaVerwaltung/vorlagen/HMRechnungPrivatKopie.ott";

    public static boolean nachrichtfuerRezept = false;
    public static String argsRezeptnummer = "";

    public static String inbox = "//192.168.2.3/programme/data301/540840108/inbox/"; // C:/OODokumente/RehaVerwaltung/Dokumentation/301-er/";
    public static String outbox = "//192.168.2.3/programme/data301/540840108/outbox/";

    public static int rehaPort = -1;

    public static int xport = 7000;
    public static boolean xportOk = false;
    public RehaReverseServer rehaReverseServer = null;

    public boolean isLibreOffice;

    public SqlInfo sqlInfo = null;

    public static boolean testcase = false;
    private static Logger logger = LoggerFactory.getLogger(Reha301.class);

    public static void main(String[] args) {
        new Logging("reha301");
        Reha301 application = new Reha301();
        application.getInstance();
        application.getInstance().sqlInfo = new SqlInfo();
        if (args.length > 0 || testcase) {
            if (!testcase) {
                // System.out.println("hole daten aus INI-Datei "+args[0]);
                INIFile inif = new INIFile(args[0] + "ini/" + args[1] + "/rehajava.ini");

                officeProgrammPfad = inif.getStringProperty("OpenOffice.org", "OfficePfad");
                officeNativePfad = inif.getStringProperty("OpenOffice.org", "OfficeNativePfad");
                progHome = args[0];
                aktIK = args[1];
                INIFile ini301 = new INIFile(args[0] + "ini/" + args[1] + "/dta301.ini");
                inbox = ini301.getStringProperty("DatenPfade301", "inbox");
                outbox = ini301.getStringProperty("DatenPfade301", "outbox");
                if (args.length == 3) {
                    try {
                        rehaPort = Integer.parseInt(args[2]);


                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null,
                                "Fehler im Modul Reha301, kann den IO-Port nicht ermitteln");
                    }
                }
            }

            try {
                application.  conn = new DatenquellenFactory(aktIK).createConnection();

                application.sqlInfo.setConnection(application.  conn);
                Reha301.DbOk = true;
            } catch (SQLException e1) {
                logger.error("Connection konnte nicht erstellt werden fuer " + aktIK,e1);
            }
            application.getJFrame();
            new SocketClient().setzeRehaNachricht(rehaPort, "Reha301#" + RehaIOMessages.IS_STARTET);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } else {


            JOptionPane.showMessageDialog(null,
                    "Keine Datenbankparameter übergeben!\nReha-Sql kann nicht gestartet werden");
            System.exit(0);

        }

    }

    /********************/

    public JFrame getJFrame() {
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
        jFrame = new JFrame() {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void setVisible(final boolean visible) {

                if (getState() != JFrame.NORMAL) {
                    setState(JFrame.NORMAL);
                }

                if (visible) {
                    // setDisposed(false);
                }
                if (!visible || !isVisible()) {
                    super.setVisible(visible);
                }

                if (visible) {
                    int state = super.getExtendedState();
                    state &= ~JFrame.ICONIFIED;
                    super.setExtendedState(state);
                    super.setAlwaysOnTop(true);
                    super.toFront();
                    super.requestFocus();
                    super.setAlwaysOnTop(false);
                }
            }

            @Override
            public void toFront() {
                super.setVisible(true);
                int state = super.getExtendedState();
                state &= ~JFrame.ICONIFIED;
                super.setExtendedState(state);
                super.setAlwaysOnTop(true);
                super.toFront();
                super.requestFocus();
                super.setAlwaysOnTop(false);
            }
        };
        sqlInfo.setFrame(jFrame);
        jFrame.addWindowListener(this);
        jFrame.setSize(1000, 550);
        jFrame.setTitle("Thera-Pi  §301-er  [IK: " + aktIK + "] ");
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setLocationRelativeTo(null);
        jFrame.getContentPane()
              .add(new Reha301Tab());
        jFrame.setIconImage(Toolkit.getDefaultToolkit()
                                   .getImage(Reha301.progHome + "icons/abr301.png"));
        jFrame.setVisible(true);
        thisFrame = jFrame;
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    while (Reha301.xport < 7050 && Reha301.xportOk == false) {
                        try {
                            Thread.sleep(50);
                            rehaReverseServer = new RehaReverseServer(Reha301.xport);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (Reha301.xportOk) {
                        new SocketClient().setzeRehaNachricht(rehaPort,
                                "AppName#" + "Reha301#" + Integer.toString(Reha301.xport));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return null;
            }

        }.execute();

        return jFrame;
    }

    /********************/

    public Reha301 getInstance() {
        thisClass = this;
        return this;
    }

    /*******************/

    public void starteDB() {


        DatenbankStarten dbstart = new DatenbankStarten();
        dbstart.run();

    }

    /*******************/

    public static void stoppeDB() {
        try {
            Reha301.thisClass.conn.close();
            Reha301.thisClass.conn = null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**********************************************************
     *
     */
    final class DatenbankStarten implements Runnable {
        private void StarteDB() {

            try {

                conn = new DatenquellenFactory(aktIK).createConnection();
                Reha301.thisClass.sqlInfo.setConnection(conn);
                Reha301.DbOk = true;
            } catch (final SQLException ex) {
                ex.printStackTrace();
                // System.out.println("SQLException: " + ex.getMessage());
                // System.out.println("SQLState: " + ex.getSQLState());
                // System.out.println("VendorError: " + ex.getErrorCode());
                Reha301.DbOk = false;

            }
            return;
        }

        @Override
        public void run() {
            StarteDB();
        }

    }

    /*****************************************************************
     *
     */
    /**********************************************************
     *
     */

    @Override
    public void windowActivated(WindowEvent arg0) {
    }

    @Override
    public void windowClosed(WindowEvent arg0) {
        if (Reha301.thisClass.conn != null) {
            try {
                Reha301.thisClass.conn.close();
                // System.out.println("Datenbankverbindung wurde geschlossen");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (rehaReverseServer != null) {
            try {
                rehaReverseServer.serv.close();
                // System.out.println("RehaIO-SocketServer geschlossen");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.exit(0);
    }

    @Override
    public void windowClosing(WindowEvent arg0) {
        if (Reha301.thisClass.conn != null) {
            try {
                Reha301.thisClass.conn.close();
                // System.out.println("Datenbankverbindung wurde geschlossen");
                new SocketClient().setzeRehaNachricht(rehaPort, "Reha301#" + RehaIOMessages.IS_FINISHED);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (rehaReverseServer != null) {
            try {
                rehaReverseServer.serv.close();
                // System.out.println("RehaIO-SocketServer geschlossen");
            } catch (IOException e) {
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

    /***************************/

    public static void starteOfficeApplication() {

        try {
            officeapplication = new StartOOApplication(Reha301.officeProgrammPfad, Reha301.officeNativePfad).start(
                    false);
            System.out.println("OpenOffice ist gestartet und Active =" + officeapplication.isActive());
        } catch (OfficeApplicationException e) {
            e.printStackTrace();
        }

    }

}
