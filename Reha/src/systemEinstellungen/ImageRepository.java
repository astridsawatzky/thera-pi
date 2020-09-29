package systemEinstellungen;

import java.awt.Image;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.ImageIcon;

import CommonTools.ini.INIFile;
import CommonTools.ini.INITool;
import CommonTools.ini.Settings;
import environment.Path;
import hauptFenster.Reha;

public class ImageRepository {

    ConcurrentHashMap<String, ImageIcon> map = new ConcurrentHashMap<String, ImageIcon>();
    private static String[] bilder = new String[] { "neu", "edit", "delete", "print", "save", "find", "stop",
            "zuzahlfrei", "zuzahlok", "zuzahlRGR", "zuzahlnichtok", "nichtgesperrt", "rezeptgebuehr",
            "rezeptgebuehrrechnung", "ausfallrechnung", "arztbericht", "privatrechnung", "sort", "historieumsatz",
            "historietage", "historieinfo", "keinerezepte", "hausbesuch", "historie", "kvkarte", "ooowriter", "ooocalc",
            "oooimpress", "openoffice", "barcode", "info", "scanner", "email", "sms", "tools", "links", "rechts",
            "abbruch", "pdf", "euro", "einzeltage", "info2", "bild", "patbild", "bunker", "camera", "oofiles",
            "kleinehilfe", "achtung", "vorschau", "patstamm", "arztstamm", "kassenstamm", "drvlogo", "personen16",
            "forward", "wecker16", "mond", "roogle", "scannergross", "rot", "gruen", "inaktiv", "buttonrot",
            "buttongruen", "statusoffen", "statuszu", "statusset", "abschliessen", "bombe", "bomb24", "openoffice26",
            "tporgklein", "information", "undo", "redo", "abrdreizwei", "abriv", "att", "close", "confirm", "copy",
            "cut", "day", "dayselect", "down", "left", "minimize", "paste", "patsearch", "quicksearch", "refresh",
            "right", "search", "tellist", "termin", "upw", "week", "abrdreieins", "ebcheck", "hbmehrere",
            "verkaufArtikel", "verkaufLieferant", "verkaufTuten", "patnachrichten", "ocr", "BarKasse" };

    public static void SystemIconsInit() {
        Settings inif = INITool.openIni(Path.Instance.getProghome() + "ini/" + Reha.getAktIK() + "/", "icons.ini");
        Settings iniFallBack = INITool.openIniFallback(Path.Instance.getProghome() + "defaults/ini/", "icons.ini"); // lokale
                                                                                                                   // ini
                                                                                                                   // (falls
                                                                                                                   // aktive
                                                                                                                   // ini
                                                                                                                   // in
                                                                                                                   // DB
                                                                                                                   // abgelegt
                                                                                                                   // ist)
        Settings iniDefault = new INIFile(Path.Instance.getProghome() + "defaults/ini/icons.ini"); // ini im
                                                                                                  // defaults-Pfad, die
                                                                                                  // ist immer da


        int xscale = 0;
        int yscale = 0;
        for (int i = 0; i < bilder.length; i++) {
            Settings use_ini = inif;
            xscale = SystemConfig.testIntIni(use_ini, "Icons", bilder[i] + "ScaleX");
            if (xscale == 0) { // nicht in ini; nur '-1' u. Werte >0 sind 'Treffer'
                if (iniFallBack != null) { // aktive ini liegt in der DB
                    use_ini = iniFallBack; // lokale Kopie checken
                    xscale = SystemConfig.testIntIni(use_ini, "Icons", bilder[i] + "ScaleX");
                }
                if (xscale == 0) {
                    use_ini = iniDefault; // letzterVersuch: default-ini
                    xscale = SystemConfig.testIntIni(use_ini, "Icons", bilder[i] + "ScaleX");
                }
            }
            if (xscale != 0) {
                if (use_ini != inif) {
                    System.out.println("found " + bilder[i] + " in " + use_ini.getFileName());
                }
                yscale = use_ini.getIntegerProperty("Icons", bilder[i] + "ScaleY");
            } else {
                System.out.println("Fehler!!!!!!!!! bei Bild: " + bilder[i]
                        + ". Fehler->Bilddatei existiert nicht, oder ist nicht in icons.ini vermerkt");
            }

            if ((xscale > 0) && (yscale > 0)) {
                Image ico  = new ImageIcon(Path.Instance.getProghome() + "icons/"
                        + use_ini.getStringProperty("Icons", bilder[i])).getImage()
                                                                        .getScaledInstance(xscale, yscale,
                                                                                Image.SCALE_SMOOTH);
                SystemConfig.hmSysIcons.put(bilder[i], new ImageIcon(ico));
            } else {
                SystemConfig.hmSysIcons.put(bilder[i], new ImageIcon(
                        Path.Instance.getProghome() + "icons/" + use_ini.getStringProperty("Icons", bilder[i])));
            }
            /*
             * Wenn das Icon im default-Pfad gefunden wurde, sollte es hier in die icons.ini
             * vom IK (egal ob file oder in DB) übernommen werden: INITool.addIcon(String
             * key, String file, int xDim, int yDim); INITool.addIcon("Icons", "dummy.png",
             * xscale, yscale); public Integer getIntegerProperty(String pstrSection, String
             * pstrProp) public void setIntegerProperty(String pstrSection, String pstrProp,
             * int pintVal, String pstrComments)
             *
             * public String getStringProperty(String pstrSection, String pstrProp) public
             * void setStringProperty(String pstrSection, String pstrProp, String pstrVal,
             * String pstrComments)
             *
             * inif.setStringProperty("Icons", bilder[i], "", "");
             * inif.setIntegerProperty("Icons", bilder[i]+"ScaleX", xscale, "")
             * inif.setIntegerProperty("Icons", bilder[i]+"ScaleY", yscale, "")
             */
        }
        // Reha.instance.copyLabel.setDropTarget(true);
        //// System.out.println("System-Icons wurden geladen");
        werkzeugeIcon();
        noaccessIcon();
    }

    private void put(String name, ImageIcon imageIcon) {
        map.put(name, imageIcon);

    }

    public ImageIcon get(String name) {
        return map.get(name);
    }

    public static ImageIcon paypalIcon() {
        return new ImageIcon(new ImageIcon(Path.Instance.getProghome() + "icons/pp_cc_mark_37x23.jpg").getImage()
                                                                                       .getScaledInstance(24, 24,
                                                                                               Image.SCALE_SMOOTH));
    }

    private static void werkzeugeIcon() {

        if (SystemConfig.hmSysIcons.get("werkzeuge") == null) {
            SystemConfig.hmSysIcons.put("werkzeuge", new ImageIcon(Path.Instance.getProghome() + "icons/werkzeug.gif"));
        }
    }

    private static void noaccessIcon() {
        if (SystemConfig.hmSysIcons.get("noaccess") == null) {
            SystemConfig.hmSysIcons.put("noaccess",
                    new ImageIcon(Path.Instance.getProghome() + "icons/" + "noaccess.gif"));
        }
    }

    public void add(String string, ImageIcon image) {
        SystemConfig.hmSysIcons.put(string, image);

    }
}
