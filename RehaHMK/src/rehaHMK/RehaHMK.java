package rehaHMK;

import CommonTools.Colors;
import CommonTools.SqlInfo;
import CommonTools.ini.INIFile;
import CommonTools.ini.Settings;
import rehaHMK.RehaIO.RehaReverseServer;
import rehaHMK.RehaIO.SocketClient;
import ag.ion.bion.officelayer.application.IOfficeApplication;
import ag.ion.bion.officelayer.application.OfficeApplicationException;
import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;
import environment.Path;
import environment.SWTJarLoader;
import io.RehaIOMessages;
import logging.Logging;
import office.OOService;
import sql.DatenquellenFactory;

import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import CommonTools.SqlInfo;
import CommonTools.ini.INIFile;
import ag.ion.bion.officelayer.application.IOfficeApplication;
import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;
import environment.Path;
import environment.SWTJarLoader;
import io.RehaIOMessages;
import logging.Logging;
import office.OOTools;
import rehaHMK.RehaIO.RehaReverseServer;
import rehaHMK.RehaIO.SocketClient;
import sql.DatenquellenFactory;

public class RehaHMK implements WindowListener {


    private JFrame jFrame;

    public static JFrame thisFrame;

    private Connection conn;
    private static RehaHMK thisClass;
    private SqlInfo sqlInfo;

    public static int xport = -1;

    private RehaReverseServer rehaReverseServer;
    public static int rehaReversePort = 6000;

    public static String aktIK;
    public static HashMap<String, Integer> pgReferenz = new HashMap<>();
    public static HashMap<String, ImageIcon> icons = new HashMap<>();
    public static HashMap<String, String> hmAdrADaten = new HashMap<>();
    public static Optional<IOfficeApplication> officeapplication;

    public static String[] arztGruppen;
    public static String hmkURL;
    public static String aktUser = "unbekannt";
    private static String proghome;
private static final    Logger LOGGER = LoggerFactory.getLogger(RehaHMK.class);

    public static void main(String[] args) throws InterruptedException, ExecutionException, TimeoutException {
        new Logging("hmk");
        try {
            SwingUtilities.invokeAndWait(new SWTJarLoader());
        } catch (InvocationTargetException | InterruptedException e1) {
            e1.printStackTrace();
        }
        RehaHMK application = new RehaHMK();

        application.getInstance();
        application.getInstance().sqlInfo = new SqlInfo();
        if (args.length <= 0) {
            JOptionPane.showMessageDialog(null,
                    "Keine Datenbankparameter übergeben\nReha-Sql kann nicht gestartet werden");
            return;
        }
        System.out.println("hole daten aus INI-Datei " + args[0]);
        Settings inif = new INIFile(args[0] + "ini/" + args[1] + "/rehajava.ini");


        proghome = Path.Instance.getProghome();
        aktIK = args[1];

        Settings hmrinif = new INIFile(proghome + "ini/" + RehaHMK.aktIK + "/hmrmodul.ini");
        hmkURL = hmrinif.getStringProperty("HMRModul", "HMKUrl");

        if (args.length >= 3) {
            rehaReversePort = Integer.parseInt(args[2]);
        }

        NativeInterface.initialize();
        JWebBrowser.useXULRunnerRuntime();
        NativeInterface.open();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (!NativeInterface.isEventPumpRunning()) {
                    NativeInterface.runEventPump();
                }
            }
        }).start();


        ExecutorService laufbursche = Executors.newFixedThreadPool(2);
        Future<Boolean> dbstartErfolgreich = laufbursche.submit(new Callable<Boolean>() {

            @Override
            public Boolean call() throws Exception {
                try {
                    application.conn = new DatenquellenFactory(aktIK).createConnection();
                    application.sqlInfo.setConnection(application.conn);

                    LOGGER.info("Datenbankkontakt hergestellt");
                    return true;
                } catch (final SQLException ex) {
                    LOGGER.info("Datenbankkontakt fehlgeschlagen",ex);
                    return false;
                }
            }
        });

        if (dbstartErfolgreich.get(30, TimeUnit.SECONDS)) {

            fuelleReferenz();
            application.getJFrame();
        } else {
            JOptionPane.showMessageDialog(null,

                    "Datenbank konnte nicht geöffnet werden!\nTimeout nach 10 Sekunden Wartezeit!\nReha-Kassenbuch kann nicht gestartet werden");
            return;
        }
    }



    private static void fuelleReferenz() {
        String[] diszis = { "Physio", "Massage", "Ergo", "Logo", "Podo" }; // <- ToDo: umstellen!
        INIFile inif = new INIFile(proghome + "ini/" + aktIK + "/pgreferenz.ini");
        for (int i = 0; i < diszis.length; i++) {
            pgReferenz.put(diszis[i], inif.getIntegerProperty("HMR_ReferenzPreisGruppe", diszis[i]));
        }
        icons.put("browser", new ImageIcon(proghome + "icons/internet-web-browser.png"));
        icons.put("key", new ImageIcon(proghome + "icons/entry_pk.gif"));
        icons.put("lupe", new ImageIcon(proghome + "icons/mag.png"));
        icons.put("erde", new ImageIcon(proghome + "icons/earth.gif"));
        icons.put("inaktiv", new ImageIcon(proghome + "icons/inaktiv.png"));
        icons.put("green", new ImageIcon(proghome + "icons/green.png"));
        icons.put("rot", new ImageIcon(proghome + "icons/red.png"));
        Image ico = new ImageIcon(proghome + "icons/blitz.png").getImage()
                                                                 .getScaledInstance(26, 26, Image.SCALE_SMOOTH);
        icons.put("blitz", new ImageIcon(ico));
        icons.put("strauss", new ImageIcon(proghome + "icons/strauss_150.png"));
    }

    public JFrame getJFrame() {
        try {
            UIManager.setLookAndFeel("com.jgoodies.looks.plastic.PlasticXPLookAndFeel");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        thisClass = this;
        this.jFrame = new JFrame() {
            private static final long serialVersionUID = 1L;

            public void setVisible(boolean visible) {
                if (getState() != 0) {
                    setState(0);
                }
                if (!visible || !isVisible()) {
                    super.setVisible(visible);
                }
                if (visible) {
                    int state = getExtendedState();
                    state &= ~JFrame.ICONIFIED;
                    setExtendedState(state);
                    setAlwaysOnTop(true);
                    super.toFront();
                    requestFocus();
                    setAlwaysOnTop(false);
                }
            }

            public void toFront() {
                super.setVisible(true);
                int state = getExtendedState();
                state &= ~JFrame.ICONIFIED;
                setExtendedState(state);
                setAlwaysOnTop(true);
                super.toFront();
                requestFocus();
                setAlwaysOnTop(false);
            }
        };
        try {
            this.rehaReverseServer = new RehaReverseServer(7000);
        } catch (Exception ex) {
            this.rehaReverseServer = null;
        }
        this.sqlInfo.setFrame(this.jFrame);
        this.jFrame.addWindowListener(this);
        this.jFrame.setSize(1020, 675);
        this.jFrame.setTitle(
                "Thera-Pi  Heilmittelkatalog  [IK: " + aktIK + "] " + "[Server-IP: " + "dbIpAndName" + "]"); // FIXME:
                                                                                                             // info
                                                                                                             // dpipandname
        this.jFrame.setDefaultCloseOperation(3);
        this.jFrame.setLocationRelativeTo((Component) null);
        this.jFrame.getContentPane()
                   .add((Component) new RehaHMKTab());
        this.jFrame.setVisible(true);
        thisFrame = this.jFrame;
        try {
            new SocketClient().setzeRehaNachricht(rehaReversePort, "AppName#RehaHMK#" + xport);
            new SocketClient().setzeRehaNachricht(rehaReversePort, "RehaHMK#" + RehaIOMessages.IS_STARTET);
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    new SocketClient().setzeRehaNachricht(rehaReversePort,
                            "RehaHMK#" + RehaIOMessages.NEED_AKTUSER);
                }
            });
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Fehler in der Socketkommunikation");
        }
        return this.jFrame;
    }

    public RehaHMK getInstance() {
        thisClass = this;
        return this;
    }

    public void windowOpened(WindowEvent e) {
    }

    public void windowClosing(WindowEvent e) {
        if (thisClass.conn != null) {
            try {
                thisClass.conn.close();
                System.out.println("Datenbankverbindung wurde geschlossen");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        if (thisClass.rehaReverseServer != null) {
            try {
                new SocketClient().setzeRehaNachricht(rehaReversePort, "RehaHMK#" + RehaIOMessages.IS_FINISHED);
                this.rehaReverseServer.serv.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        System.exit(0);
    }

    public void windowClosed(WindowEvent e) {
    }

    public void windowIconified(WindowEvent e) {
    }

    public void windowDeiconified(WindowEvent e) {
    }

    public void windowActivated(WindowEvent e) {
    }

    public void windowDeactivated(WindowEvent e) {
    }
}
