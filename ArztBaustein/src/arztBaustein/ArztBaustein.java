package arztBaustein;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.Connection;
import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.jdesktop.swingx.JXFrame;

import CommonTools.INIFile;
import CommonTools.StartOOApplication;
import ag.ion.bion.officelayer.application.IOfficeApplication;
import ag.ion.bion.officelayer.application.OfficeApplicationException;
import logging.Logging;
import mandant.IK;
import sql.DatenquellenFactory;

public class ArztBaustein {

    private IOfficeApplication officeapplication;
    Connection conn = null;

    JXFrame jFrame = null;

    ArztBausteinPanel arztbausteinpanel = null;

    public ArztBaustein(Connection connection, IOfficeApplication officeapplication) {
        conn = connection;
        this.officeapplication = officeapplication;
    }

    public static void main(String[] args) throws SQLException {
        new Logging("arztbaustein");
        String proghome = args[0];
        IK ik = new IK(args[1]);

        String OpenOfficePfad = "C:/Program Files (x86)/OpenOffice.org 3";
        String OpenOfficeNativePfad = "C:/RehaVerwaltung/Libraries/lib/openofficeorg";

        if (args.length > 0) {
            System.out.println("hole daten aus INI-Datei " + proghome);
            INIFile ini = new INIFile(proghome + "ini/" + ik.digitString() + "/rehajava.ini");

            OpenOfficePfad = ini.getStringProperty("OpenOffice.org", "OfficePfad");
            OpenOfficeNativePfad = ini.getStringProperty("OpenOffice.org", "OfficeNativePfad");
        }

        IOfficeApplication officeapp = starteOfficeApplication(OpenOfficePfad, OpenOfficeNativePfad);

        start(ik, officeapp);

    }

    public static void start(IK ik, IOfficeApplication officeapplication) throws SQLException {

        Connection connection = new DatenquellenFactory().with(ik).createConnection();
        ArztBaustein arztbaustein = new ArztBaustein(connection, officeapplication);
        arztbaustein.getJFrame();
    }

    public JXFrame getJFrame() {
        if (jFrame == null) {
            jFrame = new JXFrame();

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

            jFrame.addWindowListener(new WindowAdapter() {
                public void windowClosed(WindowEvent arg0) {
                    closeconnection();
                }
            });
            jFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            jFrame.setPreferredSize(new Dimension(1024, 800));

            jFrame.setTitle("Bausteine für ärztlichen Entlassbericht anlegen / ändern");

            jFrame.getContentPane()
                  .setPreferredSize(new Dimension(1024, 800));
            jFrame.getContentPane()
                  .setLayout(new GridLayout());
            jFrame.getContentPane()
                  .add(arztbausteinpanel = new ArztBausteinPanel(this, this.officeapplication));
            WindowListener wl = new ArztBausteinWindowlistener(arztbausteinpanel);
            jFrame.addWindowListener(wl);
            jFrame.setVisible(true);

            jFrame.pack();

        }
        return jFrame;
    }

    public void closeconnection() {
        if (conn != null) {
            try {
                conn.close();
                System.out.println("Datenbankverbindung wurde geschlossen-2");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static IOfficeApplication starteOfficeApplication(String ooPath, String ooNativePath) {
        IOfficeApplication application = null;
        try {
            application = new StartOOApplication(ooPath, ooNativePath).start();
            System.out.println("OpenOffice ist gestartet und Active =" + application.isActive());
        } catch (OfficeApplicationException e1) {
            e1.printStackTrace();
        }
        return application;

    }

}
