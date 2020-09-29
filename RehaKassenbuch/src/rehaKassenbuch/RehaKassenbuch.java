package rehaKassenbuch;

import java.awt.Cursor;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.jdesktop.swingworker.SwingWorker;

import CommonTools.SqlInfo;
import CommonTools.ini.INIFile;
import CommonTools.ini.Settings;
import ag.ion.bion.officelayer.application.IOfficeApplication;
import ag.ion.bion.officelayer.application.OfficeApplicationException;
import crypt.Verschluesseln;
import logging.Logging;
import office.OOService;
import sql.DatenquellenFactory;

public class RehaKassenbuch implements WindowListener {

    /**
     * @param args
     */
    /**
     * @param args
     */
    public static boolean DbOk;
    JFrame jFrame;
    public static JFrame thisFrame = null;
    public Connection conn;
    public static RehaKassenbuch thisClass;

    public static IOfficeApplication officeapplication;

    public String dieseMaschine = null;

    public final Cursor wartenCursor = new Cursor(Cursor.WAIT_CURSOR);
    public final Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);


    public static String officeProgrammPfad = "C:/Programme/OpenOffice.org 3";
    public static String officeNativePfad = "C:/RehaVerwaltung/Libraries/lib/openofficeorg/";
    public static String progHome ;
    public static String aktIK ;
    /*
     * public static String hmRechnungPrivat =
     * "C:/RehaVerwaltung/vorlagen/HMRechnungPrivatKopie.ott"; public static String
     * hmRechnungKasse = "C:/RehaVerwaltung/vorlagen/HMRechnungPrivatKopie.ott";
     * public static String rhRechnungPrivat =
     * "C:/RehaVerwaltung/vorlagen/HMRechnungPrivatKopie.ott"; public static String
     * rhRechnungKasse = "C:/RehaVerwaltung/vorlagen/HMRechnungPrivatKopie.ott";
     */
    public static boolean testcase = false;
    public boolean isLibreOffice;

    public SqlInfo sqlInfo;

    public static void main(String[] args) {
        new Logging("kasse");
        RehaKassenbuch application = new RehaKassenbuch();
        application.getInstance();
        application.sqlInfo = new SqlInfo();

        if (args.length > 0 || testcase) {
            if (!testcase) {
                System.out.println("hole daten aus INI-Datei " + args[0]);
                Settings inif = new INIFile(args[0] + "ini/" + args[1] + "/rehajava.ini");

                officeProgrammPfad = inif.getStringProperty("OpenOffice.org", "OfficePfad");
                officeNativePfad = inif.getStringProperty("OpenOffice.org", "OfficeNativePfad");
                progHome = args[0];
                aktIK = args[1];
            }

            final RehaKassenbuch xapplication = application;
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws java.lang.Exception {
                    xapplication.starteDB();
                    long zeit = System.currentTimeMillis();
                    while (!DbOk) {
                        try {
                            Thread.sleep(20);
                            if (System.currentTimeMillis() - zeit > 10000) {
                                JOptionPane.showMessageDialog(null,
                                        "Datenbank konnte nicht geöffnet werden!\nTimeout nach 10 Sekunden Wartezeit!\nReha-Kassenbuch kann nicht gestartet werden");
                                break;
                                // System.exit(0);
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (!DbOk) {

                        JOptionPane.showMessageDialog(null,
                                "Datenbank konnte nicht geöffnet werden!\nReha-Kassenbuch wird beendet");
                        System.exit(0);

                    }
                    RehaKassenbuch.starteOfficeApplication();
                    return null;
                }

            }.execute();
            application.getJFrame();
        } else {
            JOptionPane.showMessageDialog(null,
                    "Keine Datenbankparameter übergeben!\nReha-Kassenbuch kann nicht gestartet werden");
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
        jFrame = new JFrame();
        sqlInfo.setFrame(jFrame);
        jFrame.addWindowListener(this);
        jFrame.setSize(1000, 500);
        jFrame.setTitle("Thera-Pi  Kassenbuch erstellen / bearbeiten  [IK: " + aktIK + "] " );
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

    public void starteDB() {
        new Thread() {
            @Override
            public void run() {
                DatenbankStarten dbstart = new DatenbankStarten();
                dbstart.run();
            }
        }.start();
    }

    /*******************/

    public static void stoppeDB() {
        try {
            RehaKassenbuch.thisClass.conn.close();
            RehaKassenbuch.thisClass.conn = null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**********************************************************
     *
     */
    final class DatenbankStarten implements Runnable {
        private void StarteDB() {
            final RehaKassenbuch obj = RehaKassenbuch.thisClass;

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
            } catch (InstantiationException e) {
                e.printStackTrace();
                System.out.println(sDB + "Treiberfehler: " + e.getMessage());
                RehaKassenbuch.DbOk = false;
                return;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                System.out.println(sDB + "Treiberfehler: " + e.getMessage());
                RehaKassenbuch.DbOk = false;
                return;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                System.out.println(sDB + "Treiberfehler: " + e.getMessage());
                RehaKassenbuch.DbOk = false;
                return;
            }
            try {

                obj.conn = new DatenquellenFactory(aktIK).createConnection();
                sqlInfo.setConnection(obj.conn);
                RehaKassenbuch.DbOk = true;
                System.out.println("Datenbankkontakt hergestellt");
            } catch (final SQLException ex) {
                System.out.println("SQLException: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("VendorError: " + ex.getErrorCode());
                RehaKassenbuch.DbOk = false;

            }
            return;
        }

        @Override
        public void run() {
            StarteDB();
        }

    }



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

    /***************************/

    public static void starteOfficeApplication() {
        try {
        	new OOService().start( RehaKassenbuch.officeNativePfad, RehaKassenbuch.officeProgrammPfad);
            officeapplication = new OOService().getOfficeapplication();
        } catch (OfficeApplicationException e1) {
            e1.printStackTrace();
        } catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

}
