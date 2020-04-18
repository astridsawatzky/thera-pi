package hauptFenster;

import java.awt.Dimension;

import javax.smartcardio.CardException;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.jdesktop.swingx.JXFrame;

import com.sun.star.uno.Exception;

import CommonTools.ini.INIFile;
import CommonTools.ini.INITool;
import egk.EgkReader;
import environment.Path;
import oOorgTools.OOTools;
import ocf.OcKVK;
import systemEinstellungen.SystemConfig;

final class ErsterLogin implements Runnable {
    private void Login() {
        new Thread() {
            @Override
            public void run() {
                Reha.starteOfficeApplication();
                OOTools.ooOrgAnmelden();
            }
        }.start();
        ProgLoader.PasswortDialog();
    }

    @Override
    public void run() {
        Login();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Reha.getThisFrame()
                    .setMinimumSize(new Dimension(800, 600));
                Reha.getThisFrame()
                    .setPreferredSize(new Dimension(800, 600));
                Reha.getThisFrame()
                    .setExtendedState(JXFrame.MAXIMIZED_BOTH);
                Reha.getThisFrame()
                    .setVisible(true);
                INIFile inif = INITool.openIni(Path.Instance.getProghome() + "ini/" + Reha.getAktIK() + "/",
                        "rehajava.ini");
                if (inif.getIntegerProperty("HauptFenster", "Divider1") != null) {
                    Reha.instance.jSplitLR.setDividerLocation(
                            (Reha.divider1 = inif.getIntegerProperty("HauptFenster", "Divider1")));
                    Reha.instance.jSplitRechtsOU.setDividerLocation(
                            (Reha.divider2 = inif.getIntegerProperty("HauptFenster", "Divider2")));
                    // System.out.println("Divider gesetzt");
                    // System.out.println("Divider 1 = "+inif.getIntegerProperty("HauptFenster",
                    // "Divider1"));
                    // System.out.println("Divider 2 = "+inif.getIntegerProperty("HauptFenster",
                    // "Divider2")+"\n\n");
                    Reha.dividerOk = true;
                    // Hier mußt noch eine funktion getSichtbar() entwickelt werden
                    // diese ersetzt die nächste Zeile
                    // System.out.println("Sichtbar Variante = "+Reha.instance.getSichtbar());
                } else {
                    // System.out.println("Divider-Angaben sind noch null");
                    Reha.instance.setDivider(5);
                }

                Reha.getThisFrame()
                    .getRootPane()
                    .validate();
                Reha.isStarted = true;

                Reha.getThisFrame()
                    .setVisible(true);

                if (Reha.dividerOk) {
                    Reha.instance.vollsichtbar = Reha.instance.getSichtbar();
                    if (!SystemConfig.desktopHorizontal) {
                        Reha.instance.jSplitRechtsOU.setDividerLocation((Reha.divider2));
                    }
                    // System.out.println("Wert für Vollsichtbar = "+Reha.instance.vollsichtbar);
                }

                // Reha.thisFrame.pack();
                new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        try {

                            // SCR335
                            // ctpcsc31kv

                            if (SystemConfig.sReaderAktiv.equals("1")) {
                                try {

                                    System.out.println("Aktiviere Reader: " + SystemConfig.sReaderName + "\n"
                                            + "CT-API Bibliothek: " + SystemConfig.sReaderCtApiLib);

                                    Reha.instance.ocKVK = new OcKVK();

                                    EgkReader target = new EgkReader(SystemConfig.sReaderName);
                                    target.addCardListener(Reha.instance.ocKVK);
                                    Thread egk = new Thread(target);

                                    egk.setDaemon(true);
                                    egk.setName("EGK");
                                    egk.start();

                                } catch (CardException e) {
                                    disableReader("Fehlerstufe rc = -2 oder -4  = Karte wird nicht unterstützt\n"
                                            + e.getMessage());
                                } catch (ClassNotFoundException e) {
                                    disableReader("Fehlerstufe rc = -1 = CT-API läßt sich nicht initialisieren\n"
                                            + e.getMessage());
                                } catch (java.lang.Exception e) {
                                    if (e.getMessage()
                                         .contains("property file")) {
                                        disableReader("Anderweitiger Fehler\n"
                                                + "Die Datei opencard.properties befindet sich nicht im Java-Verzeichnis ../lib."
                                                + "Das Kartenlesegerät kann nicht verwendet werden.");
                                    } else {
                                        disableReader("Anderweitiger Fehler\n" + e.getMessage());
                                    }
                                }

                            }
                        } catch (NullPointerException ex) {
                            ex.printStackTrace();
                        }
                        return null;
                    }
                }.execute();
            }
        });
    }

    private void disableReader(String error) {
        SystemConfig.sReaderAktiv = "0";
        Reha.instance.ocKVK = null;
        JOptionPane.showMessageDialog(null, error);
    }
}