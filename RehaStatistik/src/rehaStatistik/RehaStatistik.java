package rehaStatistik;

import java.awt.Cursor;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.jdesktop.swingworker.SwingWorker;

import CommonTools.SqlInfo;
import rehaStatistik.Tools.SystemPreislisten;
import ag.ion.bion.officelayer.application.IOfficeApplication;
import logging.Logging;
import office.OOTools;
import sql.DatenquellenFactory;

public class RehaStatistik implements WindowListener {

    /**
     * @param args
     */
    public static boolean DbOk;
    JFrame jFrame;
    public static JFrame thisFrame = null;
    public Connection conn;
    public static RehaStatistik thisClass;

    public static Optional<IOfficeApplication> officeapplication;

    public String dieseMaschine = null;

    public final Cursor wartenCursor = new Cursor(Cursor.WAIT_CURSOR);
    public final Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);



    public static String proghome = "C:/RehaVerwaltung/";
    public static String aktIK;

    public static boolean testcase = false;
    public SqlInfo sqlInfo;

    public static void main(String[] args) {

        new Logging("statistik");
        RehaStatistik application = new RehaStatistik();
        application.getInstance();
        application.getInstance().sqlInfo = new SqlInfo();
        if (args.length > 0 || testcase) {


         officeapplication = OOTools.initOffice(proghome, aktIK);

        System.out.println("hole daten aus INI-Datei " + proghome);
        }

        final RehaStatistik xapplication = application;
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws java.lang.Exception {
                xapplication.starteDB();
                long zeit = System.currentTimeMillis();
                while (!DbOk) {
                    try {
                        Thread.sleep(20);
                        if (System.currentTimeMillis() - zeit > 10000) {
                            System.exit(0);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (!DbOk) {
                    JOptionPane.showMessageDialog(null,
                            "Datenbank konnte nicht geöffnet werden!\nReha-Statistik kann nicht gestartet werden");
                    System.exit(0);
                }
                SystemPreislisten.ladePreise("Reha");
                // System.out.println(SystemPreislisten.hmPreise.get("Reha"));
                // System.out.println(SystemPreislisten.hmPreisGruppen.get("Reha"));
                return null;
            }

        }.execute();
        application.getJFrame();

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
        jFrame.setSize(500, 500);
        jFrame.setTitle("Thera-Pi Modul:Reha-Statistik");
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setLocationRelativeTo(null);
        jFrame.getContentPane()
              .add(new StatistikPanel(RehaStatistik.officeapplication));
        jFrame.setVisible(true);
        thisFrame = jFrame;
        return jFrame;
    }

    /********************/

    public RehaStatistik getInstance() {
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
            if (RehaStatistik.thisClass.conn != null) {
                RehaStatistik.thisClass.conn.close();
                RehaStatistik.thisClass.conn = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**********************************************************
     *
     */
    final class DatenbankStarten implements Runnable {
        private void StarteDB() {
            final RehaStatistik obj = RehaStatistik.thisClass;


            try {

                obj.conn = new DatenquellenFactory(aktIK).createConnection();
                sqlInfo.setConnection(obj.conn);
                RehaStatistik.DbOk = true;
                System.out.println("Datenbankkontakt hergestellt");
            } catch (final SQLException ex) {
                System.out.println("SQLException: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("VendorError: " + ex.getErrorCode());
                RehaStatistik.DbOk = false;

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
        if (RehaStatistik.thisClass.conn != null) {
            try {
                RehaStatistik.thisClass.conn.close();
                System.out.println("Datenbankverbindung wurde geschlossen");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        System.exit(0);
    }

    @Override
    public void windowClosing(WindowEvent arg0) {
        if (RehaStatistik.thisClass.conn != null) {
            try {
                RehaStatistik.thisClass.conn.close();
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
