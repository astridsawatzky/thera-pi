package rehaBillEdit;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.jdesktop.swingworker.SwingWorker;

import CommonTools.ini.INIFile;
import CommonTools.ini.Settings;
import ag.ion.bion.officelayer.application.IOfficeApplication;
import ag.ion.bion.officelayer.application.OfficeApplicationException;
import ag.ion.bion.officelayer.application.OfficeApplicationRuntime;
import ag.ion.bion.officelayer.document.DocumentException;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.event.ITerminateEvent;
import ag.ion.bion.officelayer.event.VetoTerminateListener;
import crypt.Verschluesseln;

public class RehaBillEdit implements WindowListener {
    private static boolean DbOk;
    private JFrame jFrame;
    public static JFrame thisFrame;
    public Connection conn;
    public static RehaBillEdit thisClass;

    static IOfficeApplication officeapplication;

                private static String dbIpAndName = "jdbc:mysql://192.168.2.2:3306/rtadaten";
    private static String dbUser = "rtauser";
    private static String dbPassword = "rtacurie";
    private static String officeProgrammPfad = "C:/Programme/OpenOffice.org 3";
    private static String officeNativePfad = "C:/RehaVerwaltung/Libraries/lib/openofficeorg/";
    static String progHome = "C:/RehaVerwaltung/";
    static String aktIK = "510841109";
    static String hmRechnungPrivat = "C:/RehaVerwaltung/vorlagen/HMRechnungPrivatKopie.ott";

        static String rhRechnungKasse = "C:/RehaVerwaltung/vorlagen/HMRechnungPrivatKopie.ott";

    static HashMap<String, String> hmAbrechnung = new HashMap<>();
    static HashMap<String, String> hmFirmenDaten;
    public static HashMap<String, String> hmAdrPDaten = new HashMap<>();

    public static boolean testcase;

    public static void main(String[] args) {
        RehaBillEdit application = new RehaBillEdit();
        application.getInstance();

        if (args.length > 0 || testcase) {
            if (!testcase) {
                System.out.println("hole daten aus INI-Datei " + args[0]);
                Settings inif = new INIFile(args[0] + "ini/" + args[1] + "/rehajava.ini");
                dbIpAndName = inif.getStringProperty("DatenBank", "DBKontakt1");
                dbUser = inif.getStringProperty("DatenBank", "DBBenutzer1");
                String pw = inif.getStringProperty("DatenBank", "DBPasswort1");
                String decrypted = null;
                if (pw != null) {
                    Verschluesseln man = Verschluesseln.getInstance();
                    decrypted = man.decrypt(pw);
                } else {
                    decrypted = "";
                }
                dbPassword = decrypted;
                inif = new INIFile(args[0] + "ini/" + args[1] + "/fremdprog.ini");
                officeProgrammPfad = inif.getStringProperty("OpenOffice.org", "OfficePfad");
                officeNativePfad = inif.getStringProperty("OpenOffice.org", "OfficeNativePfad");

                inif = new INIFile(args[0] + "ini/" + args[1] + "/abrechnung.ini");
                String rechnung = inif.getStringProperty("HMPRIRechnung", "Pformular");
                rechnung = rechnung.replace(".ott", "");
                rechnung = rechnung + "Kopie.ott";
                hmRechnungPrivat = rechnung;
                rhRechnungKasse = inif.getStringProperty("RehaDRVRechnung", "RehaDRVformular");
                progHome = args[0];
                aktIK = args[1];
            }
            AbrechnungParameter(progHome);
            FirmenDaten(progHome);

            final RehaBillEdit xapplication = application;
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    xapplication.starteDB();
                    long zeit = System.currentTimeMillis();
                    while (!DbOk) {
                        try {
                            Thread.sleep(20);
                            if (System.currentTimeMillis() - zeit > 5000) {
                                System.exit(0);
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (!DbOk) {
                        JOptionPane.showMessageDialog(null,
                                "Datenbank konnte nicht geöffnet werden!\nReha-Statistik kann nicht gestartet werden");
                    }
                    starteOfficeApplication();
                    return null;
                }

            }.execute();
            application.getJFrame();
        } else {
            JOptionPane.showMessageDialog(null,
                    "Keine Datenbankparameter übergeben!\nReha-Statistik kann nicht gestartet werden");
            System.exit(0);
        }
    }

    public JFrame getJFrame() {
        try {
            UIManager.setLookAndFeel("com.jgoodies.looks.plastic.PlasticXPLookAndFeel");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        thisClass = this;
        jFrame = new JFrame();
        jFrame.addWindowListener(this);
        jFrame.setSize(1000, 500);
        jFrame.setTitle("Thera-Pi  Rechnungen korrigieren / Duplikate erstellen  [IK: " + aktIK + "] " + "[Server-IP: "
                + dbIpAndName + "]");
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setLocationRelativeTo(null);
        jFrame.getContentPane()
              .add(new RehaBillEditTab());
        jFrame.setVisible(true);
        thisFrame = jFrame;
        return jFrame;
    }

    public RehaBillEdit getInstance() {
        thisClass = this;
        return this;
    }

    private void starteDB() {
        DatenbankStarten dbstart = new DatenbankStarten();
        dbstart.run();
    }

        private final class DatenbankStarten implements Runnable {
        private void StarteDB() {
            final RehaBillEdit obj = thisClass;

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
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                e.printStackTrace();
                System.out.println(sDB + "Treiberfehler: " + e.getMessage());
                DbOk = false;
                return;
            }
            try {
                obj.conn = DriverManager.getConnection(dbIpAndName, dbUser, dbPassword);
                DbOk = true;
                System.out.println("Datenbankkontakt hergestellt");
            } catch (final SQLException ex) {
                System.out.println("SQLException: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("VendorError: " + ex.getErrorCode());
                DbOk = false;
            }
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
        if (thisClass.conn != null) {
            try {
                thisClass.conn.close();
                System.out.println("Datenbankverbindung wurde geschlossen");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        System.exit(0);
    }

    @Override
    public void windowClosing(WindowEvent arg0) {
        if (thisClass.conn != null) {
            try {
                thisClass.conn.close();
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

    private static void FirmenDaten(String proghome) {
        String[] stitel = { "Ik", "Ikbezeichnung", "Firma1", "Firma2", "Anrede", "Nachname", "Vorname", "Strasse",
                "Plz", "Ort", "Telefon", "Telefax", "Email", "Internet", "Bank", "Blz", "Kto", "Steuernummer", "Hrb",
                "Logodatei", "Zusatz1", "Zusatz2", "Zusatz3", "Zusatz4", "Bundesland" };
        hmFirmenDaten = new HashMap<String, String>();
        Settings inif = new INIFile(proghome + "ini/" + RehaBillEdit.aktIK + "/firmen.ini");
        for (int i = 0; i < stitel.length; i++) {
            hmFirmenDaten.put(stitel[i], inif.getStringProperty("Firma", stitel[i]));
        }
    }

    private static void AbrechnungParameter(String proghome) {
        hmAbrechnung.clear();
        /******** Heilmittelabrechnung ********/
        Settings inif = new INIFile(proghome + "ini/" + aktIK + "/abrechnung.ini");
        hmAbrechnung.put("hmgkvformular", inif.getStringProperty("HMGKVRechnung", "Rformular"));
        hmAbrechnung.put("hmgkvrechnungdrucker", inif.getStringProperty("HMGKVRechnung", "Rdrucker"));
        hmAbrechnung.put("hmgkvtaxierdrucker", inif.getStringProperty("HMGKVRechnung", "Tdrucker"));
        hmAbrechnung.put("hmgkvbegleitzettel", inif.getStringProperty("HMGKVRechnung", "Begleitzettel"));
        hmAbrechnung.put("hmgkvrauchdrucken", inif.getStringProperty("HMGKVRechnung", "Rauchdrucken"));
        hmAbrechnung.put("hmgkvrexemplare", inif.getStringProperty("HMGKVRechnung", "Rexemplare"));

        hmAbrechnung.put("hmpriformular", inif.getStringProperty("HMPRIRechnung", "Pformular"));
        hmAbrechnung.put("hmpridrucker", inif.getStringProperty("HMPRIRechnung", "Pdrucker"));
        hmAbrechnung.put("hmpriexemplare", inif.getStringProperty("HMPRIRechnung", "Pexemplare"));

        hmAbrechnung.put("hmbgeformular", inif.getStringProperty("HMBGERechnung", "Bformular"));
        hmAbrechnung.put("hmbgedrucker", inif.getStringProperty("HMBGERechnung", "Bdrucker"));
        hmAbrechnung.put("hmbgeexemplare", inif.getStringProperty("HMBGERechnung", "Bexemplare"));
        /* Rehaabrechnung */
        hmAbrechnung.put("rehagkvformular", inif.getStringProperty("RehaGKVRechnung", "RehaGKVformular"));
        hmAbrechnung.put("rehagkvdrucker", inif.getStringProperty("RehaGKVRechnung", "RehaGKVdrucker"));
        hmAbrechnung.put("rehagkvexemplare", inif.getStringProperty("RehaGKVRechnung", "RehaGKVexemplare"));
        hmAbrechnung.put("rehagkvik", inif.getStringProperty("RehaGKVRechnung", "RehaGKVik"));

        hmAbrechnung.put("rehadrvformular", inif.getStringProperty("RehaDRVRechnung", "RehaDRVformular"));
        hmAbrechnung.put("rehadrvdrucker", inif.getStringProperty("RehaDRVRechnung", "RehaDRVdrucker"));
        hmAbrechnung.put("rehadrvexemplare", inif.getStringProperty("RehaDRVRechnung", "RehaDRVexemplare"));
        hmAbrechnung.put("rehadrvik", inif.getStringProperty("RehaDRVRechnung", "RehaDRVik"));

        hmAbrechnung.put("rehapriformular", inif.getStringProperty("RehaPRIRechnung", "RehaPRIformular"));
        hmAbrechnung.put("rehapridrucker", inif.getStringProperty("RehaPRIRechnung", "RehaPRIdrucker"));
        hmAbrechnung.put("rehapriexemplare", inif.getStringProperty("RehaPRIRechnung", "RehaPRIexemplare"));
        hmAbrechnung.put("rehapriik", inif.getStringProperty("RehaPRIRechnung", "RehaPRIik"));

        hmAbrechnung.put("hmallinoffice", inif.getStringProperty("GemeinsameParameter", "InOfficeStarten"));
        if (System.getProperty("os.name")
                  .contains("Windows")) {
        } else if (System.getProperty("os.name")
                         .contains("Linux")) {
        } else if (System.getProperty("os.name")
                         .contains("String für MaxOSX????")) {
        }
        /*
         * org.thera_pi.nebraska.gui.utils.Verschluesseln man =
         * org.thera_pi.nebraska.gui.utils.Verschluesseln.getInstance();
         * man.init(org.thera_pi.nebraska.gui.utils.Verschluesseln.getPassword().
         * toCharArray(), man.getSalt(), man.getIterations()); try{ inif = new
         * INIFile(INI_FILE); String pw = null; String decrypted = null;
         * hmAbrechnung.put("hmkeystorepw", ""); int anzahl =
         * inif.getIntegerProperty("KeyStores", "KeyStoreAnzahl"); for(int i = 0; i <
         * anzahl;i++){ if(inif.getStringProperty("KeyStores",
         * "KeyStoreAlias"+Integer.toString(i+1)).trim().equals("IK"+Reha.aktIK)){ pw =
         * inif.getStringProperty("KeyStores", "KeyStorePw"+Integer.toString(i+1));
         * decrypted = man.decrypt(pw); hmAbrechnung.put("hmkeystorepw", decrypted);
         * break; } }
         *
         * }catch(Exception ex){ JOptionPane.showMessageDialog(
         * null,"Zertifikatsdatenbank nicht vorhanden oder fehlerhaft.\nAbrechnung nach § 302 kann nicht durchgeführt werden."
         * ); }
         */
    }

    private static void starteOfficeApplication() {
        final String OPEN_OFFICE_ORG_PATH = officeProgrammPfad;

        try {
            // System.out.println("**********Open-Office wird gestartet***************");
            String path = OPEN_OFFICE_ORG_PATH;
            Map<String, String> config = new HashMap<>();
            config.put(IOfficeApplication.APPLICATION_HOME_KEY, path);
            config.put(IOfficeApplication.APPLICATION_TYPE_KEY, IOfficeApplication.LOCAL_APPLICATION);
            System.setProperty(IOfficeApplication.NOA_NATIVE_LIB_PATH, officeNativePfad);
            officeapplication = OfficeApplicationRuntime.getApplication(config);
            officeapplication.activate();
            officeapplication.getDesktopService()
                             .addTerminateListener(new VetoTerminateListener() {
                                 @Override
                                 public void queryTermination(ITerminateEvent terminateEvent) {
                                     super.queryTermination(terminateEvent);
                                     try {
                                         IDocument[] docs = officeapplication.getDocumentService()
                                                                             .getCurrentDocuments();
                                         if (docs.length == 1) {
                                             docs[0].close();
                                             // System.out.println("Letztes Dokument wurde geschlossen");
                                         }
                                     } catch (DocumentException | OfficeApplicationException e) {
                                         e.printStackTrace();
                                     }
                                 }
                             });
        } catch (OfficeApplicationException e) {
            e.printStackTrace();
        }
    }
}
