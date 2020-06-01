package opRgaf;

import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import CommonTools.SqlInfo;
import CommonTools.ini.INIFile;
import ag.ion.bion.officelayer.application.OfficeApplicationException;
import environment.Path;
import io.RehaIOMessages;
import logging.Logging;
import office.OOService;
import opRgaf.RehaIO.RehaReverseServer;
import opRgaf.RehaIO.SocketClient;
import sql.DatenquellenFactory;

public class OpRgaf implements WindowListener {

    private JFrame jFrame;
    private static JFrame thisFrame;
    Connection conn;

    String aktIK = "510841109";


    private OpRgafTab otab;

    public static int xport = -1;

    private RehaReverseServer rehaReverseServer;
    static int rehaReversePort = -1;

    private SqlInfo sqlInfo;
    OpRgAfIni iniOpRgAf;

    public static void main(String[] args) {
        new Logging("oprgaf");
        if (args.length <= 0 && !false) {
            JOptionPane.showMessageDialog(null,
                    "Keine Datenbankparameter übergeben!\nReha-opRGaf kann nicht gestartet werden");
        } else {
            OpRgaf application = new OpRgaf();
            application.sqlInfo = new SqlInfo();

            System.out.println("hole daten aus INI-Datei " + args[0]);
            INIFile inif = new INIFile(args[0] + "ini/" + args[1] + "/rehajava.ini");

            inif = new INIFile(args[0] + "ini/" + args[1] + "/rehajava.ini");
            String officeProgrammPfad = inif.getStringProperty("OpenOffice.org", "OfficePfad");
            String officeNativePfad = inif.getStringProperty("OpenOffice.org", "OfficeNativePfad");
            try {
                new OOService().start(officeNativePfad, officeProgrammPfad);
            } catch (FileNotFoundException | OfficeApplicationException e) {
                e.printStackTrace();
            }
            application.aktIK = args[1];
            try {
                application.sqlInfo.setConnection(new DatenquellenFactory(application.aktIK).createConnection());
            } catch (SQLException e) {
                e.printStackTrace();
            }
            application.iniOpRgAf = new OpRgAfIni(args[0], "ini/", args[1], "oprgaf.ini");

            if (args.length >= 3) {
                rehaReversePort = Integer.parseInt(args[2]);
            }
            application.StarteDB();

            application.getJFrame();
        }
    }

    public JFrame getJFrame() {
        try {
            UIManager.setLookAndFeel("com.jgoodies.looks.plastic.PlasticXPLookAndFeel");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        jFrame = new JFrame() {

            private static final long serialVersionUID = 1L;

            @Override
            public void setVisible(final boolean visible) {
                if (getState() != JFrame.NORMAL) {
                    setState(JFrame.NORMAL);
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

        new InitHashMaps();

        try {
            rehaReverseServer = new RehaReverseServer(7000);
            rehaReverseServer.register(this);
        } catch (Exception ex) {
            rehaReverseServer = null;
        }
        sqlInfo.setFrame(jFrame);
        jFrame.addWindowListener(this);
        jFrame.setSize(1000, 675);
        jFrame.setTitle("Thera-Pi  Rezeptgebühr-/Ausfall-/Verkaufsrechnungen ausbuchen u. Mahnwesen  [IK: " + aktIK
                + "] " + "[Server-IP: " + "dbIpAndName" + "]"); // FIXME: dpipandname
        jFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        jFrame.setLocationRelativeTo(null);
        otab = new OpRgafTab(this);
        otab.setFirstFocus();

        jFrame.getContentPane()
              .add(otab);

        jFrame.setIconImage(Toolkit.getDefaultToolkit()
                                   .getImage(Path.Instance.getProghome() + File.separator + "icons" + File.separator
                                           + "Guldiner_I.png"));
        jFrame.setVisible(true);
        thisFrame = jFrame;
        informRehaMainAboutStart();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                otab.opRgafPanel.setzeFocus();
            }
        });
        return jFrame;
    }

    private void informRehaMainAboutStart() {
        try {
            new SocketClient().setzeRehaNachricht(rehaReversePort, "AppName#OpRgaf#" + xport);
            new SocketClient().setzeRehaNachricht(rehaReversePort, "OpRgaf#" + RehaIOMessages.IS_STARTET);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Fehler in der Socketkommunikation");
        }
    }

    public OpRgaf getInstance() {
        return this;
    }

    private boolean StarteDB() {

        try {
            conn = new DatenquellenFactory(aktIK).createConnection();
            sqlInfo.setConnection(conn);
            System.out.println("Datenbankkontakt hergestellt");
            return true;
        } catch (final SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            return false;
        }
    }

    @Override
    public void windowActivated(WindowEvent arg0) {
    }

    @Override
    public void windowClosed(WindowEvent arg0) {
        if (conn != null) {
            try {
                conn.close();
                System.out.println("Datenbankverbindung wurde geschlossen");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void windowClosing(WindowEvent arg0) {
        iniOpRgAf.saveLastSelection();
        if (conn != null) {
            try {
                conn.close();
                System.out.println("Datenbankverbindung wurde geschlossen");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (rehaReverseServer != null) {
            try {
                new SocketClient().setzeRehaNachricht(rehaReversePort, "OpRgaf#" + RehaIOMessages.IS_FINISHED);
                rehaReverseServer.serv.close();
                System.out.println("ReverseServer geschlossen");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
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




    public void show() {
        thisFrame.setVisible(true);

    }

    public void sucheRezept(String string) {
        otab.sucheRezept(string);

    }
}
