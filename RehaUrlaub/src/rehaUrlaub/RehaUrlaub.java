package rehaUrlaub;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;
import org.jdesktop.swingworker.SwingWorker;

import CommonTools.SqlInfo;
import CommonTools.ini.INIFile;
import CommonTools.ini.Settings;
import ag.ion.bion.officelayer.application.IOfficeApplication;
import ag.ion.bion.officelayer.application.OfficeApplicationException;
import ag.ion.bion.officelayer.spreadsheet.ISpreadsheetDocument;
import logging.Logging;
import office.OOService;
import sql.DatenquellenFactory;

public class RehaUrlaub implements WindowListener {

    private DatenquellenFactory datenquellenFactory;
    public static boolean DbOk = false;
    JFrame jFrame;
    public static JFrame thisFrame = null;
    public Connection conn;
    public static RehaUrlaub thisClass;

    public static IOfficeApplication officeapplication;
    public static boolean officeOk = false;

    public String dieseMaschine = null;

    public final Cursor wartenCursor = new Cursor(Cursor.WAIT_CURSOR);
    public final Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);


    public static String progHome;
    public static String aktIK;
    public static String urlaubsDateiVorlage = "UrlaubUndStunden.ods";
    public boolean isLibreOffice;

    public static ISpreadsheetDocument spreadsheetDocument;

    public static boolean testcase = false;
    SqlInfo sqlInfo = null;

    public static void main(String[] args) throws InvalidFileFormatException, IOException {
        if (args.length < 2) {
            return;
        }

         progHome= args[0];
         aktIK = args[1];




        new Logging("urlaub");
        
        
        try {
            Ini rehjavaini = new Ini(new File(progHome + "ini/" + aktIK + "/rehajava.ini"));
            OOService officeService = new OOService(rehjavaini);
            officeapplication =officeService.start();
            officeOk=true;
        } catch (IOException | OfficeApplicationException e1) {

            officeOk=false;
        }




        RehaUrlaub application = new RehaUrlaub();
        application.sqlInfo = new SqlInfo();
        try {

            if (args.length > 0 || testcase) {
                    System.out.println("hole daten aus INI-Datei " + args[0]);
                    Settings rehjavaini = new INIFile(args[0] + "ini/" + args[1] + "/rehajava.ini");


                    INIFile abrechnungini = new INIFile(args[0] + "ini/" + args[1] + "/abrechnung.ini");
                    String rechnung = abrechnungini.getStringProperty("HMPRIRechnung", "Pformular");
                    rechnung = rechnung.replace(".ott", "");
                    rechnung = rechnung + "Kopie.ott";



                final RehaUrlaub xapplication = application;
                new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws java.lang.Exception {
                        xapplication.starteDB();
                        long zeit = System.currentTimeMillis();
                        while (!DbOk) {
                            try {
                                Thread.sleep(20);
                                if (System.currentTimeMillis() - zeit > 10000) {
                                    JOptionPane.showMessageDialog(null, "TimeOut-für Datenbank erreicht");
                                    System.exit(0);
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        return null;
                    }

                }.execute();
                long zeit = System.currentTimeMillis();
                while (!DbOk) {
                    try {
                        Thread.sleep(20);
                        if (System.currentTimeMillis() - zeit > 7000) {
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                application.getJFrame();
            } else {
                JOptionPane.showMessageDialog(null,
                        "Keine Datenbankparameter übergeben!\nReha-Statistik kann nicht gestartet werden");
                System.exit(0);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
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
        jFrame.setCursor(RehaUrlaub.thisClass.wartenCursor);
        jFrame.addWindowListener(this);
        jFrame.setSize(1000, 500);
        jFrame.setPreferredSize(new Dimension(1000, 500));
        jFrame.setTitle("Thera-Pi  Urlaub- / Überstundenverwaltung  [IK: " + aktIK + "] ");
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setLocationRelativeTo(null);
        RehaUrlaubTab urlaubTab = new RehaUrlaubTab();
        jFrame.getContentPane()
              .add(urlaubTab);
        jFrame.pack();
        // jFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        // urlaubTab.starteOOFrame();
        jFrame.setVisible(true);

        thisFrame = jFrame;
        jFrame.setCursor(RehaUrlaub.thisClass.normalCursor);
        return jFrame;
    }

    /********************/

    public RehaUrlaub getInstance() {
        thisClass = this;
        return this;
    }

    /*******************/

    public void starteDB() {
        DatenbankStarten dbstart = new DatenbankStarten(this);
        dbstart.run();
    }

    /*******************/

    public static void stoppeDB() {
        try {
            RehaUrlaub.thisClass.conn.close();
            RehaUrlaub.thisClass.conn = null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**********************************************************
     *
     */
    final class DatenbankStarten implements Runnable {

        private RehaUrlaub urlaub;

        public DatenbankStarten(RehaUrlaub urlaub) {
            this.urlaub = urlaub;
        }

        private void StarteDB() {
            ;

            try {

                urlaub.datenquellenFactory = new DatenquellenFactory(aktIK);
                urlaub.conn = datenquellenFactory.createConnection();
                sqlInfo.setConnection(urlaub.conn);
                RehaUrlaub.DbOk = true;
                System.out.println("Datenbankkontakt hergestellt");
            } catch (final SQLException ex) {
                System.out.println("SQLException: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("VendorError: " + ex.getErrorCode());
                RehaUrlaub.DbOk = false;

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

    @Override
    public void windowActivated(WindowEvent arg0) {
    }

    @Override
    public void windowClosed(WindowEvent arg0) {
        if (RehaUrlaub.thisClass.conn != null) {
            try {
                RehaUrlaub.thisClass.conn.close();
                System.out.println("Datenbankverbindung wurde geschlossen");
                if (RehaUrlaub.spreadsheetDocument != null) {
                    RehaUrlaub.spreadsheetDocument.close();
                    RehaUrlaub.spreadsheetDocument = null;

                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        System.exit(0);
    }

    @Override
    public void windowClosing(WindowEvent arg0) {
        if (RehaUrlaub.thisClass.conn != null) {
            try {
                RehaUrlaub.thisClass.conn.close();
                System.out.println("Datenbankverbindung wurde geschlossen");
                if (RehaUrlaub.spreadsheetDocument != null) {
                    RehaUrlaub.spreadsheetDocument.close();
                    RehaUrlaub.spreadsheetDocument = null;
                }
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

   

}
