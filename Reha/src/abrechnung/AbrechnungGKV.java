package abrechnung;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thera_pi.nebraska.crypto.NebraskaCryptoException;
import org.thera_pi.nebraska.crypto.NebraskaEncryptor;
import org.thera_pi.nebraska.crypto.NebraskaFileException;
import org.thera_pi.nebraska.crypto.NebraskaKeystore;
import org.thera_pi.nebraska.crypto.NebraskaNotInitializedException;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.mysql.jdbc.PreparedStatement;

import CommonTools.DatFunk;
import CommonTools.ExUndHop;
import CommonTools.JCompTools;
import CommonTools.JRtaCheckBox;
import CommonTools.JRtaComboBox;
import CommonTools.JRtaRadioButton;
import CommonTools.SqlInfo;
import CommonTools.StringTools;
import commonData.RezFromDB;
import dialoge.InfoDialog;
import dialoge.InfoDialogVOinArbeit;
import emailHandling.EmailSendenExtern;
import environment.Path;
import events.PatStammEvent;
import events.PatStammEventClass;
import gui.Cursors;
import hauptFenster.AktiveFenster;
import hauptFenster.Reha;
import hauptFenster.UIFSplitPane;
import mandant.IK;
import office.OOService;
import rehaInternalFrame.JAbrechnungInternal;
import stammDatenTools.RezTools;
import suchen.PatMitAbgebrochenenVOs;
import suchen.PatMitVollenVOs;
import systemEinstellungen.SystemConfig;
import systemEinstellungen.SystemPreislisten;
import umfeld.Betriebsumfeld;

public class AbrechnungGKV extends JXPanel {

    private static final long serialVersionUID = -3580427603080353812L;
    private JAbrechnungInternal jry;
    private UIFSplitPane jSplitLR = null;

    final String plus = "+";
    final String EOL = "'" + System.getProperty("line.separator");
    final String SOZ = "?";
    public String abzurechnendeKassenID = "";
    String ik_kasse, ik_kostent, ik_nutzer, ik_physika, ik_papier, ik_email, ik_preisgruppe;
    String name_kostent;
    String aktEsol;
    String aktDfue;
    String aktRechnung;
    String aktDisziplin = "";

    boolean annahmeAdresseOk = false;
    /******* Controls für die linke Seite *********/
    ButtonGroup bg = new ButtonGroup();
    JRtaRadioButton[] rbLinks = { null, null, null, null };
    JButton[] butLinks = { null, null, null, null };
    public JRtaComboBox cmbDiszi = null;
    JXTree treeKasse = null;
    File f;
    FileWriter fw;
    BufferedWriter bw;
    AbrechnungDlg abrDlg = null;

    public JXTTreeNode rootKasse;
    public KassenTreeModel treeModelKasse;
    public JXTTreeNode aktuellerKnoten;
    public static JXTTreeNode aktuellerKassenKnoten;
    public int kontrollierteRezepte;

    public StringBuffer positionenBuf = new StringBuffer();
    public StringBuffer unbBuf = new StringBuffer();
    public StringBuffer unzBuf = new StringBuffer();
    public StringBuffer gesamtBuf = new StringBuffer();
    public StringBuffer auftragsBuf = new StringBuffer();
    public StringBuffer buf = new StringBuffer();
    public StringBuffer htmlBuf = new StringBuffer();
    public StringBuffer rechnungBuf = new StringBuffer();
    public StringBuffer historieBuf = new StringBuffer();
    public int positionenAnzahl = 0;
    public String abrDateiName = "";
    JEditorPane htmlPane = null;

    Double[] preis00 = { 0.00, 0.00, 0.00 };
    Double[] preis11 = { 0.00, 0.00, 0.00 };
    Double[] preis31 = { 0.00, 0.00, 0.00 };
    Double[] preis51 = { 0.00, 0.00, 0.00 };

    Double[] kassenUmsatz = { 0.00, 0.00 };
    DecimalFormat dfx = new DecimalFormat("0.00");

    Vector<String> existiertschon = new Vector<String>();
    Vector<String> customIconList = new Vector<String>();
    Vector<String> lateKtList = new Vector<String>();
    Vector<String> lateVOList = new Vector<String>();
    int toggleIcons;
    Vector<Vector<String>> kassenIKs = new Vector<Vector<String>>();
    /******* Controls für die rechte Seite *********/
    AbrechnungRezept abrRez = null;
    AbrechnungDrucken abrDruck = null;
    Vector<String> abgerechneteRezepte = new Vector<String>();
    Vector<String> abgerechnetePatienten = new Vector<String>();
    Vector<Vector<String>> preisVector = null;
    HashMap<String, String> hmAnnahme = null;
    HashMap<String, String> hmKostentraeger = new HashMap<String, String>();
    int abrechnungRezepte = 0;
    public String aktuellerPat = "";

    String rlistekasse;
    String rlisteesol;

    JRtaCheckBox soll302 = null;
    HashMap<String, String> hmAlternativeKasse = new HashMap<String, String>();
    JButton alternativeKK = null;

    public String abrechnungsModus = "abrechnung302";
    final String ABR_MODE_302 = "abrechnung302";
    final String ABR_MODE_IV = "abrechnungIV";
    public String SlgaVersion = null;
    public String SllaVersion = null;
    public boolean zuzahlModusDefault = true;

    public static String zertifikatVon = SystemConfig.hmAbrechnung.get("hmkeystoreusecertof");
    public static String originalTitel = "";
    public static boolean lOwnCert = (SystemConfig.hmAbrechnung.get("hmkeystoreusecertof")
                                                               .equals(SystemConfig.hmAbrechnung.get("hmkeystorealias"))
                                                                       ? true
                                                                       : false);

    final String sucheFertige = "SELECT t1.name1,t1.ikktraeger,t1.ikkasse,t1.id,t2.ik_papier FROM fertige AS t1 LEFT JOIN kass_adr AS t2 ON t1.ikkasse = t2.ik_kasse ";

    public static boolean directCall = false;

    public Disziplinen disziSelect = null;
    private Connection connection;
    protected InfoDialog infoDlg;
    private RezFromDB RezFromDB;
    KeyStore keyStore = null;
    static int noCertFound = 0xfff; // max. 0x448 (3 Jahre) gültig
    OwnCertState myCert = null;
    private List<String> volleVOs;
    private List<String> abgebrocheneVOs;

    public AbrechnungGKV(JAbrechnungInternal xjry, Connection connection) {
        super();
        this.setJry(xjry);
        setLayout(new BorderLayout());
        if (disziSelect == null) {
            disziSelect = new Disziplinen();
        }
        cmbDiszi = disziSelect.getComboBox();

        jSplitLR = UIFSplitPane.createStrippedSplitPane(JSplitPane.HORIZONTAL_SPLIT, getLeft(), getRight(connection));
        jSplitLR.setDividerSize(7);
        jSplitLR.setDividerBorderVisible(true);
        jSplitLR.setName("BrowserSplitLinksRechts");
        jSplitLR.setOneTouchExpandable(true);
        jSplitLR.setDividerLocation(230);
        add(jSplitLR, BorderLayout.CENTER);
        mandantenCheck();
        SlgaVersion = "13";
        SllaVersion = "13";

        keyStore = new KeyStore();
        myCert = new OwnCertState();
        /*
         * Änderung im Ablauf: hier wird nur das (eigene) Zertifikat geprüft, das für
         * die Abrechnung zum Einsatz kommt Keine pauschale Prüfung aller Zertifikate im
         * Keystore mehr. Prüfung der Zertifikate der Datenannahmestellen erfolgt nach
         * Bedarf, wenn eine Kasse im Kassentree ausgewählt wird. (warum soll das
         * abgelaufene Zert einer Kasse, für die gar keine Rezepte existieren, die
         * Kassenabrechnung blockieren?)
         */
        SystemConfig.certState = keyStore.checkOwnCert(zertifikatVon/* "IK"+Reha.aktIK */);

        originalTitel = this.jry.getTitel();
        setEncryptTitle();
        disziSelect.setCurrTypeOfVO(SystemConfig.initRezeptKlasse);
        jry.setAbrRezInstance(abrRez); // JAbrechnungInternal mitteilen, welche Instanz cleanup() enthält
        RezFromDB = new RezFromDB();
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                volleVOs = new PatMitVollenVOs(new IK(Betriebsumfeld.getAktIK())).getVoList();
                abgebrocheneVOs = new PatMitAbgebrochenenVOs(new IK(Betriebsumfeld.getAktIK())).getVoList();
                return null;
            }

        }.execute();
    }

    public void setEncryptTitle() {
        this.jry.setzeTitel(originalTitel + " [Abrechnung für IK: " + Betriebsumfeld.getAktIK() + " - Zertifikat von IK: "
                + zertifikatVon.replace("IK", "") + "]");
        this.jry.repaint();
    }

    /**********
     *
     * Linke Seite
     */
    private void mandantenCheck() {
        if (SystemConfig.hmFirmenDaten.get("Steuernummer")
                                      .trim()
                                      .equals("")) {
            String msg = "Achtung(!!!!!!) für diesen Mandant wurde keine Steuernummer angegeben!\n\n"
                    + "Eine Abrechnung ohne Steuernummer wird von der GKV nicht(!!!!) akzeptiert\n\"+"
                    + "Schließen Sie die Abrechnung starten Sie die System-Initialisierung -> Mandanten Datenbank -> Firmenangaben...\n"
                    + "und tragen Sie hier im Feld Steuernummer die vom Finanzamt vergebene Steuernummer ein.";
            JOptionPane.showMessageDialog(null, msg);
        }
        if (SystemConfig.hmFirmenDaten.get("Ikbezeichnung")
                                      .trim()
                                      .equals("")) {
            String msg = "Achtung(!!!!!!) für diesen Mandant wurde keine Firmenbezeichnung angegeben!\n\n"
                    + "Schließen Sie die Abrechnung starten Sie die System-Initialisierung -> Mandanten Datenbank -> Firmenangaben...\n"
                    + "und tragen Sie hier im Feld 'Mandanten-Bezeichnung' eine Kurzfassung Ihres Firmennamens ein (max 30 Zeichen)";
            JOptionPane.showMessageDialog(null, msg);
        }
        if (SystemConfig.hmFirmenDaten.get("Ikbezeichnung")
                                      .length() > 30) {
            String msg = "Achtung(!!!!!!) für diesen Mandant wurde eine zu lange Firmenbezeichnung angegeben.\n\n"
                    + "Schließen Sie die Abrechnung starten Sie die System-Initialisierung -> Mandanten Datenbank -> Firmenangaben...\n"
                    + "und tragen Sie hier im Feld 'Mandanten-Bezeichnung' eine Kurzfassung Ihres Firmennamens ein (max 30 Zeichen)";
            JOptionPane.showMessageDialog(null, msg);
        }
    }

    private JScrollPane getLeft() {
        FormLayout lay = new FormLayout("5dlu,fill:0:grow(1.0),5dlu",
                // 1 2 3 4 5 6 7 8 9 10 11
                "5dlu,p,5dlu,p,15dlu,p,20dlu,p,15dlu,fill:0:grow(1.0),5dlu");
        PanelBuilder pb = new PanelBuilder(lay);
        CellConstraints cc = new CellConstraints();
        pb.getPanel()
          .setBackground(Color.WHITE);
        pb.addLabel("Heilmittel auswählen", cc.xy(2, 2));

        cmbDiszi.setActionCommand("einlesen");
        pb.add(cmbDiszi, cc.xy(2, 4));

        rootKasse = new JXTTreeNode(new KnotenObjekt("Abrechnung für Kasse...", "", false, "", ""), true);
        treeModelKasse = new KassenTreeModel(rootKasse);

        treeKasse = new JXTree(treeModelKasse);
        treeKasse.setModel(treeModelKasse);
        treeKasse.setName("kassentree");
        treeKasse.getSelectionModel()
                 .addTreeSelectionListener(treeSelectionListener);
        treeKasse.setCellRenderer(new MyRenderer(SystemConfig.hmSysIcons.get("zuzahlok")));
        treeKasse.addMouseListener(mouseListener);

        treeKasse.addKeyListener( keyListener);

        JScrollPane jscrk = JCompTools.getTransparentScrollPane(treeKasse);
        jscrk.validate();
        pb.add(jscrk, cc.xy(2, 6));
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                Reha.getThisFrame()
                    .setCursor(Cursors.wartenCursor);
                return null;
            }

        }.execute();

        htmlPane = new JEditorPane();
        htmlPane.setContentType("text/html");
        htmlPane.setEditable(false);
        htmlPane.setOpaque(false);
        jscrk = JCompTools.getTransparentScrollPane(htmlPane);
        jscrk.validate();
        pb.add(jscrk, cc.xy(2, 10));

        pb.getPanel()
          .validate();

        JScrollPane jscr = JCompTools.getTransparentScrollPane(pb.getPanel());
        jscr.validate();
        cmbDiszi.addActionListener(actionListener);

        return jscr;
    }

    private JXPanel getRight(Connection connection) {
        this.abrRez = new AbrechnungRezept(this, connection);
        this.abrRez.setRechtsAufNull();
        return abrRez;
    }

    public boolean getTageDrucken() {
        return this.abrRez.getTageDrucken();
    }

    public void setJry(JAbrechnungInternal jry) {
        this.jry = jry;
    }

    public JAbrechnungInternal getJry() {
        return jry;
    }

    public JXTTreeNode getaktuellerKassenKnoten() {
        return aktuellerKassenKnoten;
    }

    ActionListener actionListener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent arg0) {
            String cmd = arg0.getActionCommand();
            if (cmd.equals("einlesen")) {
                aktDisziplin = disziSelect.getCurrDisziKurz();
                if (abrRez.rezeptSichtbar) {
                    abrRez.setRechtsAufNull();
                    aktuellerPat = "";
                }
                jry.setzeTitel(originalTitel + " [Abrechnung für IK: " + Betriebsumfeld.getAktIK() + " - Zertifikat von IK: "
                        + zertifikatVon.replace("IK", "") + "] [Disziplin: " + aktDisziplin + "]");
                doEinlesen(null, null);
            }
            if (cmd.equals("alternativeadresse")) {

            }
        }
    };


    public void einlesenErneuern(String neueReznr) {
        directCall = false;
        aktDisziplin = disziSelect.getCurrDisziKurz();
        if (abrRez.rezeptSichtbar) {
            abrRez.setRechtsAufNull();
            aktuellerPat = "";
        }
        if (neueReznr != null) { // Rezept zum Baum hinzufügen
            if (!aktDisziplin.equals(RezTools.getDisziplinFromRezNr(neueReznr))) {
                doEinlesen(null, neueReznr); // andere Disziplin -> Kassenbaum neu aufbauen
            } else {
                directCall = true; // in Baum der akt. Disziplin einsortieren
                final int xindex = doEinlesenEinzeln(neueReznr);
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            treeKasse.clearSelection();
                            treeKasse.setSelectionInterval(xindex, xindex);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });
            }
        } else { // Rezept aus Baum entfernen
            doEinlesen(null, neueReznr);
        }

    }

    public int doEinlesenEinzeln(String neueReznr) {
        // das Gleiche, mit Papierannahmestelle:
        String cmd = sucheFertige + "WHERE rez_nr='" + neueReznr + "' Limit 1";
        Vector<Vector<String>> vecKassen = SqlInfo.holeFelder(cmd);
        treeKasse.setEnabled(true);
        String ktraeger = vecKassen.get(0)
                                   .get(1)
                                   .trim();
        String ikkasse = vecKassen.get(0)
                                  .get(2)
                                  .trim();
        String ikpapier = vecKassen.get(0)
                                   .get(4)
                                   .trim();
        String kas = getKassenName(ikkasse);

        int usesSameIkPapier = 0;
        int aeste = rootKasse.getChildCount();
        int aktuellerAst = 0;
        boolean neuerKnoten = true;
        JXTTreeNode node = null;
        int treeindex = 0;
        for (int i = 0; i < aeste; i++) {
            if (((JXTTreeNode) rootKasse.getChildAt(i)).knotenObjekt.ktraeger.equals(ktraeger)) {
                neuerKnoten = false;
                node = ((JXTTreeNode) rootKasse.getChildAt(i));
                aktuellerAst = i;
                break;
            } else if (((JXTTreeNode) rootKasse.getChildAt(i)).knotenObjekt.getIkPap()
                                                                           .equals(ikpapier)) {
                usesSameIkPapier = i;
            }
        }
        if (neuerKnoten) {
            if (usesSameIkPapier != 0) {
                node = astEinhaengen(kas, ktraeger, ikkasse, ikpapier, ++usesSameIkPapier);
            } else {
                node = astAnhaengen(kas, ktraeger, ikkasse, ikpapier);
            }
        }

        cmd = "select rez_nr,pat_intern,ediok,ikkasse from fertige where rez_nr='" + neueReznr + "' Limit 1";
        vecKassen = SqlInfo.holeFelder(cmd);
        JXTTreeNode meinitem = null;
        for (int i = 0; i < vecKassen.size(); i++) {
            try {
                cmd = "select n_name from pat5 where pat_intern='" + vecKassen.get(i)
                                                                              .get(1)
                        + "' LIMIT 1";

                String name = SqlInfo.holeFelder(cmd)
                                     .get(0)
                                     .get(0);
                cmd = "select preisgruppe from verordn where rez_nr='" + vecKassen.get(i)
                                                                                  .get(0)
                        + "' LIMIT 1";
                String preisgr = SqlInfo.holeEinzelFeld(cmd);

                KnotenObjekt rezeptknoten = new KnotenObjekt(vecKassen.get(i)
                                                                      .get(0)
                        + "-" + name,
                        vecKassen.get(i)
                                 .get(0),
                        (vecKassen.get(i)
                                  .get(2)
                                  .equals("T") ? true : false),
                        vecKassen.get(i)
                                 .get(3),
                        preisgr);
                rezeptknoten.ktraeger = ktraeger;
                rezeptknoten.pat_intern = vecKassen.get(i)
                                                   .get(1);
                meinitem = new JXTTreeNode(rezeptknoten, true);

                treeModelKasse.insertNodeInto(meinitem, node, node.getChildCount());
                treeKasse.validate();

                aeste = rootKasse.getChildCount();
                treeKasse.updateUI();
                treeKasse.expandPath(new TreePath(node.getPath()));
                treeKasse.scrollPathToVisible(new TreePath(meinitem.getPath()));
                for (int i2 = 0; i2 < aeste; i2++) {
                    if (treeKasse.isCollapsed(new TreePath(((JXTTreeNode) rootKasse.getChildAt(i2)).getPath()))) {
                        treeindex += 1;
                    } else {
                        treeindex += ((JXTTreeNode) rootKasse.getChildAt(i2)).getChildCount() + 1;
                    }
                    if (((JXTTreeNode) rootKasse.getChildAt(i2)).knotenObjekt.ktraeger.equals(ktraeger)) {
                        break;
                    }
                }

                treeKasse.expandPath(new TreePath(node));

                treeKasse.setSelectionPath(new TreePath(meinitem));
                treeKasse.setSelectionInterval(treeindex, treeindex);
                if (treeKasse.getSelectionPath() != null) {
                    if (!SystemConfig.hmAbrechnung.get("autoOk302")
                                                  .equals("0")) {
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                abrRez.actionAbschluss();
                            }
                        });
                    }
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }
        return treeindex;
    }

    private String getKassenName(String ikkasse) {
        String cmd = "select kassen_nam1, kassen_nam2 from kass_adr where ik_kasse = " + ikkasse;
        Vector<Vector<String>> holeFelder = SqlInfo.holeFelder(cmd);
        if(holeFelder.isEmpty()) {
            Logger logger = LoggerFactory.getLogger(AbrechnungGKV.class);
            logger.error("kassennamen nicht gefunden für KassenIK " + ikkasse);
            return "_UNBEKANNT_";

        }
        Vector<String> vecKassenName = holeFelder.get(0);
        String kname = vecKassenName.get(0)
                                    .trim();
        String kname2 = vecKassenName.get(1)
                                    .trim();
        // etwas Kosmetik:
        String tst1 = kname.toUpperCase();
        String tst2 = kname2.toUpperCase();
        if (kname.contains("Die Gesundheitskasse")) {
            kname = kname.replace("Die Gesundheitskasse", " ") + kname2;
        } else if (tst1.endsWith("-")) {
            kname = kname + kname2;
        } else if (tst1.contentEquals("BETRIEBSKRANKENKASSE")
                || tst1.contentEquals("BKK LANDESVERBAND")
                || tst1.endsWith(" DER")
                || tst1.endsWith(" DES")
                || tst2.startsWith("KRANKENKASSE")
                || tst2.startsWith("BETRIEBSKRANKENKASSE")
                || tst2.startsWith("UND ")) {
            kname = kname + " " + kname2;
        }

        kname = kname.replace("Betriebskrankenkasse", "BKK");
        kname = kname.replace("Berufsgenossenschaft", "BG");
        return kname;
    }

    /*********
     * Einlesen der abrechnungsdaten
     */
    public void doEinlesen(JXTTreeNode aktKassenNode, String neueReznr) {
        directCall = false;
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    new SwingWorker<Void, Void>() {
                        @Override
                        protected Void doInBackground() throws Exception {
                            Reha.instance.progressStarten(true);
                            return null;
                        }
                    }.execute();
                    existiertschon.clear();
                    customIconList.clear();
                    lateKtList.clear();
                    lateVOList.clear();
                    String dsz = disziSelect.getCurrRezClass();

                    String cmd = sucheFertige + "WHERE rezklasse='" + dsz
                            + "' GROUP by ikktraeger ORDER BY t2.ik_papier, t1.name1, t1.ikktraeger, t1.id";

                    Vector<Vector<String>> vecKassen = SqlInfo.holeFelder(cmd);

                    kassenBaumLoeschen();
                    if (vecKassen.size() <= 0) {
                        Reha.instance.progressStarten(false);
                        return null;
                    }
                    treeKasse.setEnabled(true);
                    String kassenName = vecKassen.get(0)
                                                 .get(0)
                                                 .trim()
                                                 .toUpperCase();
                    String ktraeger = vecKassen.get(0)
                                               .get(1)
                                               .trim();
                    String ikkasse = vecKassen.get(0)
                                              .get(2)
                                              .trim();
                    String ikpapier = vecKassen.get(0)
                                               .get(4)
                                               .trim();
                    if (!ikkasse.isEmpty()) {
                        kassenName = getKassenName(ikkasse);
                    }
                    existiertschon.add(ktraeger);

                    int aeste = 0;
                    KnotenObjekt newNode = astAnhaengen(kassenName, ktraeger, ikkasse, ikpapier).getObject();
                    rezepteAnhaengen(aeste);
                    aeste++;

                    for (int i = 0; i < vecKassen.size(); i++) {
                        ktraeger = vecKassen.get(i)
                                            .get(1)
                                            .trim();
                        if (!existiertschon.contains(ktraeger)) {
                            ikkasse = vecKassen.get(i)
                                               .get(2);
                            ikpapier = vecKassen.get(i)
                                                .get(4)
                                                .trim();
                            kassenName = getKassenName(ikkasse);
                            existiertschon.add(ktraeger);
                            astAnhaengen(kassenName, ktraeger, ikkasse, ikpapier);
                            rezepteAnhaengen(aeste);
                            aeste++;

                        }
                    }
                    kassenIconsNeuAnzeigen();

                    treeKasse.validate();
                    treeKasse.setRootVisible(true);

                    treeKasse.expandRow(0);

                    treeKasse.repaint();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                Reha.getThisFrame()
                    .setCursor(Cursors.cdefault);
                Reha.instance.progressStarten(false);
                return null;
            }
        }.execute();
    }

    private void rezepteAnhaengen(int knoten) {
        String ktraeger = ((JXTTreeNode) rootKasse.getChildAt(knoten)).knotenObjekt.ktraeger;
        String dsz = disziSelect.getCurrRezClass();
        String cmd = "select rez_nr,pat_intern,ediok,ikkasse from fertige where rezklasse='" + dsz
                + "' AND ikktraeger='" + ktraeger + "' AND fertige.neueversion LIKE 'F' ORDER BY id,pat_intern";
        System.out.println(cmd);

        Vector<Vector<String>> vecRezepte = SqlInfo.holeFelder(cmd);

        JXTTreeNode node = (JXTTreeNode) rootKasse.getChildAt(knoten);

        JXTTreeNode meinitem = null;
        for (int i = 0; i < vecRezepte.size(); i++) {
            try {
                cmd = "select n_name from pat5 where pat_intern='" + vecRezepte.get(i)
                                                                               .get(1)
                        + "' LIMIT 1";

                String thisRezNr = vecRezepte.get(i)
                                             .get(0);
                String thisPatInt = vecRezepte.get(i)
                                              .get(1);
                String name = SqlInfo.holeEinzelFeld(cmd);
                cmd = "select preisgruppe from verordn where rez_nr='" + thisRezNr + "' LIMIT 1";
                String preisgr = SqlInfo.holeEinzelFeld(cmd);

                KnotenObjekt rezeptknoten = new KnotenObjekt(thisRezNr + "-" + name, vecRezepte.get(i)
                                                                                               .get(0),
                        (vecRezepte.get(i)
                                   .get(2)
                                   .equals("T") ? true : false),
                        vecRezepte.get(i)
                                  .get(3),
                        preisgr);
                rezeptknoten.ktraeger = ktraeger;
                rezeptknoten.pat_intern = thisPatInt;
                meinitem = new JXTTreeNode(rezeptknoten, true);

                treeModelKasse.insertNodeInto(meinitem, node, node.getChildCount());
                treeKasse.validate();

                if (RezTools.isLate(thisRezNr)) {
                    lateVOList.add(thisRezNr); // letzte Behandlung ist > 10 Monate her -> Kasse u. Rezept rot markieren
                    if (lateKtList.contains(ktraeger)) {
                    } else {
                        lateKtList.add(ktraeger);
                    }
                }
            } catch (Exception ex) {

            }
        }

    }

    private JXTTreeNode astEinhaengen(String ast, String ktraeger, String ikkasse, String ikpapier, int usesSameIkPap) {
        KnotenObjekt knoten = new KnotenObjekt(ast, "", false, "", "");
        knoten.ktraeger = ktraeger;
        knoten.ikkasse = ikkasse;
        knoten.setIkPap(ikpapier);
        String cmd = "select ik_nutzer from kass_adr where ik_kasse='" + ikkasse + "' Limit 1";
        knoten.setIkNutzer(SqlInfo.holeEinzelFeld(cmd));
        JXTTreeNode node = new JXTTreeNode(knoten, true);
        treeModelKasse.insertNodeInto(node, rootKasse, usesSameIkPap);
        treeKasse.validate();
        return (node);
    }

    private JXTTreeNode astAnhaengen(String ast, String ktraeger, String ikkasse, String ikpapier) {
        return (astEinhaengen(ast, ktraeger, ikkasse, ikpapier, rootKasse.getChildCount()));

    }

    private void kassenBaumLoeschen() {
        try {

            while ((rootKasse.getChildCount()) > 0) {
                treeModelKasse.removeNodeFromParent(
                        (JXTTreeNode) ((JXTTreeNode) treeModelKasse.getRoot()).getChildAt(0));
            }
            treeKasse.validate();
            treeKasse.repaint();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /*******************************************/
    private void doKassenTreeAuswerten(KnotenObjekt node) {
        // Rezept ausgewählt
        setCursor(Cursors.wartenCursor);
        Reha.instance.progressStarten(true);
        try {
            if (!this.abrRez.setNewRez(node.rez_num, node.fertig, aktDisziplin)) {
                Reha.instance.progressStarten(false);
                setCursor(Cursors.normalCursor);
                JOptionPane.showMessageDialog(null, "Rezept konnte nicht ausgewertet werden");
                return;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        setCursor(Cursors.normalCursor);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Reha.instance.progressStarten(false);
            }
        });

        return;

    }

    /*******************************************/
    public void loescheKnoten() {
        // rezept aus fertige löschen
        // Verschluß des Rezeptes aufheben
        // Knoten löschen
        // wenn einziger Knoten den kassenKnoten löschen
        String rez_nr = this.aktuellerKnoten.knotenObjekt.rez_num;
        String cmd = "delete from fertige where rez_nr='" + rez_nr + "' LIMIT 1";
        SqlInfo.sqlAusfuehren(cmd);
        cmd = "update verordn set abschluss='F' where rez_nr='" + rez_nr + "' LIMIT 1";
        SqlInfo.sqlAusfuehren(cmd);
        try {
            treeModelKasse.removeNodeFromParent(this.aktuellerKnoten);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Kritische Situation bei Aktion aufschließen des Rezeptes");
        }
        this.aktuellerKassenKnoten.getNextNode();
        if (!removeKassenNode(this.aktuellerKassenKnoten)) {
            this.rechneKasse(this.aktuellerKassenKnoten);
        }
        this.abrRez.setRechtsAufNull();
    }

    private boolean removeKassenNode(JXTTreeNode aktKassNode) {
        if (aktKassNode.getChildCount() > 0) {
            return Boolean.FALSE;
        }
        JXTTreeNode nodeWithSameIK = sameIkPap(aktKassNode);
        JXTTreeNode prevKNode = getPrevKassenKnoten(aktKassNode);
        JXTTreeNode nextKNode = getNextKassenKnoten(aktKassNode);
        treeModelKasse.removeNodeFromParent(aktKassNode);
        if (nodeWithSameIK != null) { // Nachfolger o. Vorgänger hat gleiches IKpapier -> icons können bleiben
            aktKassNode = nodeWithSameIK; // ... wird aktueller Knoten
            int aktNodeIdx = 1 + treeModelKasse.getIndexOfChild(rootKasse, aktKassNode);

            treeKasse.repaint(); // Anzeige aktualisieren
        } else if (prevKNode!=null && nextKNode!=null) {
            kassenIconsNeuAnzeigen();
        }
        return Boolean.TRUE;
    }

    private JXTTreeNode sameIkPap(JXTTreeNode aktNode) {


        JXTTreeNode prevKNode = getPrevKassenKnoten(aktNode);
        JXTTreeNode nextKNode = getNextKassenKnoten(aktNode);
        if (nextKNode != null) {
            if (nextKNode.knotenObjekt.getIkPap()
                                      .equals(aktNode.knotenObjekt.getIkPap())) { // nächster Knoten hat gleiches
                                                                                  // IKpapier
                return nextKNode;
            }
        }
        if (prevKNode != null) {
            if (prevKNode.knotenObjekt.getIkPap()
                                      .equals(aktNode.knotenObjekt.getIkPap())) { // Vorläuferknoten hat gleiches
                                                                                  // IKpapier
                return prevKNode;
            }
        }
        return null;
    }

    private JXTTreeNode getPrevKassenKnoten(JXTTreeNode aktNode) {
        return (JXTTreeNode) aktNode.getPreviousSibling();
    }

    private JXTTreeNode getNextKassenKnoten(JXTTreeNode aktNode) {
        return (JXTTreeNode) aktNode.getNextSibling();
    }

    private void kassenIconsNeuAnzeigen() {
        JXTTreeNode rootNode = (JXTTreeNode) treeModelKasse.getRoot();
        JXTTreeNode aktKasse = (JXTTreeNode) rootNode.getChildAt(0);
        KnotenObjekt knAktKasse = aktKasse.getObject();

        if (customIconList.contains(knAktKasse.ktraeger)) {
            toggleIcons = 1;
        } else {
            toggleIcons = 0;
        }
        customIconList.clear();

        KeepIkPap myIkPap = new KeepIkPap(knAktKasse.ikpapier);
        while (aktKasse != null) {
            knAktKasse = aktKasse.getObject();
            if (myIkPap.newIkPap(knAktKasse.ikpapier)) {
                toggleIcons = (++toggleIcons) & 1;
            }
            ;
            if (toggleIcons == 1) {
                customIconList.add(knAktKasse.ktraeger);
            }
            aktKasse = (JXTTreeNode) aktKasse.getNextSibling();
        }

        treeKasse.validate();
        treeKasse.repaint();
    }
TreeSelectionListener treeSelectionListener = new TreeSelectionListener() {


    @Override
    public void valueChanged(TreeSelectionEvent arg0) {
        TreePath tp = treeKasse.getSelectionPath();
        kontrollierteRezepte = 0;
        if (tp == null) {

            return;
        }
        JXTTreeNode node = (JXTTreeNode) tp.getLastPathComponent();
        String rez_nr = node.knotenObjekt.rez_num;
        if (!rez_nr.trim()
                   .equals("")) { // Knoten enthält ein Rezept
            aktuellerKnoten = node;
            doKassenTreeAuswerten(node.knotenObjekt);
            aktuellerPat = node.knotenObjekt.pat_intern;
            if (aktuellerKassenKnoten != (JXTTreeNode) aktuellerKnoten.getParent()) {
                // VO gehört zu einem anderen Kassenknoten
                aktuellerKassenKnoten = (JXTTreeNode) aktuellerKnoten.getParent();
                if (myCert.isValid()) { // solange eigenes Zert. nicht OK, kann nicht abgerechnet werden
                    SystemConfig.certState = keyStore.checkCertKT(getaktuellerKassenKnoten().knotenObjekt);
                }
            }
            int pgr = -1;
            if (!node.knotenObjekt.preisgruppe.trim()
                                              .equals("")) {
                pgr = Integer.parseInt(node.knotenObjekt.preisgruppe.trim());
                zuzahlModusDefault = (SystemPreislisten.hmZuzahlModus.get(disziSelect.getCurrDisziKurz())
                                                                     .get(pgr - 1) == 1 ? true : false);
            }
            if (pgr < 0) {
                JOptionPane.showMessageDialog(null,
                        "Achtung Preisgruppe kann nicht ermittelt werden!\nBitte dieses Rezept nicht abrechnen!");
            }
        } else { // Knoten ist ein Kassenknoten
            abrRez.setRechtsAufNull();
            aktuellerKnoten = node;
            if (aktuellerKnoten.getParent() != null) {
                if (((JXTTreeNode) aktuellerKnoten.getParent()).isRoot()) {
                    ////// System.out.println("Aktueller Knoten ist ein Kassenknoten");
                    if (aktuellerKassenKnoten != aktuellerKnoten) {
                        aktuellerKassenKnoten = aktuellerKnoten;
                        if (myCert.isValid()) {
                            SystemConfig.certState = keyStore.checkCertKT(getaktuellerKassenKnoten().knotenObjekt);
                        }
                    }
                }
            } else {
                aktuellerKassenKnoten = null;
            }
            aktuellerPat = "";

        }
        kassenUmsatz[0] = 0.00;
        kassenUmsatz[1] = 0.00;
        if (aktuellerKassenKnoten != null) {
            rechneKasse(aktuellerKassenKnoten);
        }

    }

};
    public void setKassenUmsatzNeu() {
        kassenUmsatz[0] = 0.00;
        kassenUmsatz[1] = 0.00;
        if (aktuellerKassenKnoten != null) {
            rechneKasse(aktuellerKassenKnoten);
        }
    }

    public String getAbrechnungKasse() {
        return aktuellerKnoten.knotenObjekt.ktraeger;
    }

    public void rechneKasse(JXTTreeNode aktKasse) {
        kontrollierteRezepte = 0;
        final JXTTreeNode xaktKasse = aktKasse;
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    int lang = xaktKasse.getChildCount();
                    Reha.instance.progressStarten(true);
                    setCursor(Cursors.wartenCursor);
                    for (int i = 0; i < lang; i++) {
                        if (((JXTTreeNode) xaktKasse.getChildAt(i)).knotenObjekt.fertig) {
                            kontrollierteRezepte++;
                            holeUmsaetze(((JXTTreeNode) xaktKasse.getChildAt(i)).knotenObjekt.rez_num);
                        }
                    }
                    setHtmlLinksUnten(lang, kontrollierteRezepte);
                    Reha.instance.progressStarten(false);
                   setCursor(Cursors.cdefault);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    public void setHtmlLinksUnten(int gesamt, int gut) {
        htmlBuf.setLength(0);
        htmlBuf.trimToSize();
        htmlBuf.append("<html><head>");
        htmlBuf.append("<STYLE TYPE=\"text/css\">");
        htmlBuf.append("<!--");
        htmlBuf.append("A{text-decoration:none;background-color:transparent;border:none}");
        htmlBuf.append("TD{font-family: Tahoma; font-size: 11pt; padding-left:5px;padding-right:30px}");
        htmlBuf.append(".spalte1{color:#0000FF;}");
        htmlBuf.append(".spalte2{color:#FF0000;}");
        htmlBuf.append(".spalte3{color:#333333;}");
        htmlBuf.append("--->");
        htmlBuf.append("</STYLE>");
        htmlBuf.append("</head>");
        htmlBuf.append("<div style=margin-left:0px;>");
        htmlBuf.append("<font face=\"Tahoma\"><style=margin-left=0px;>");
        htmlBuf.append("<br>");
        htmlBuf.append("<table>");
        htmlBuf.append("<tr><td>fertige Rezepte:</td>");
        htmlBuf.append("<td class=\"spalte1\" align=\"right\"><b>" + gesamt + "</b></td></tr>");
        htmlBuf.append("<tr><td>abrechnungsfähig:</td>");
        htmlBuf.append(
                (gesamt != gut ? "<td class=\"spalte2\" align=\"right\">" : "<td class=\"spalte1\" align=\"right\">")
                        + "<b>" + gut + "</b></td></tr>");
        htmlBuf.append("<tr><td>Umsatz:</td>");
        htmlBuf.append("<td class=\"spalte1\" align=\"right\">" + dfx.format(kassenUmsatz[0]) + "</td></tr>");
        htmlBuf.append("<tr><td>enth. Rezeptgeb.: </td>");
        htmlBuf.append("<td class=\"spalte1\" align=\"right\">" + dfx.format(kassenUmsatz[1]) + "</td></tr>");
        htmlBuf.append("</table>");
        htmlBuf.append("</div>");
        htmlBuf.append("</html>");
        htmlPane.setText(htmlBuf.toString());
    }

    public void holeUmsaetze(String rez_nr) {
        buf.setLength(0);
        buf.trimToSize();
        try {
            buf.append(SqlInfo.holeFelder("select edifact from fertige where rez_nr='" + rez_nr + "'")
                              .get(0)
                              .get(0));
        } catch (Exception ex) {
        }
        if (buf.length() <= 0) {
            JOptionPane.showMessageDialog(null, "Kassenumsatz für Rezept + " + rez_nr
                    + " kann nicht abgeholt werden. Modul holeUmsatz() (Edifact)");
        }
        String[] zeilen = buf.toString()
                             .split("\n");
        String[] positionen = zeilen[0].split(":");
        // PG=1:PATINTERN=16961:REZNUM=KG57747:GESAMT=102,30:REZGEB=20,26:REZANTEIL=10,26:REZPAUSCHL=10,00:KASSENID=116
        kassenUmsatz[0] = kassenUmsatz[0] + Double.valueOf(positionen[3].split("=")[1].replace(",", "."));
        kassenUmsatz[1] = kassenUmsatz[1] + Double.valueOf(positionen[4].split("=")[1].replace(",", "."));
    }

    /**************************************************/

    public void starteAbrechnung() {
        try {
            hmKostentraeger.clear();
            if (!new OOService().getOfficeapplication().isActive()) {
                try {
                    Reha.starteOfficeApplication();
                    if (!new OOService().getOfficeapplication().isActive()) {
                        doDlgAbort();
                        JOptionPane.showMessageDialog(null,
                                "Das OpenOffice-System reagiert nicht korrekt!\nAbrechnung wird nicht gestartet");
                        return;
                    }
                } catch (Exception ex) {
                    doDlgAbort();
                    JOptionPane.showMessageDialog(null,
                            "Das OpenOffice-System reagiert nicht korrekt!\nAbrechnung wird nicht gestartet");
                    return;

                }
            }
            if (aktuellerKassenKnoten == null) {
                abrDlg.setVisible(false);
                abrDlg.dispose();
                abrDlg = null;
                Reha.instance.progressStarten(false);
                JOptionPane.showMessageDialog(null, "Keine Kasse für die Abrechnung ausgewählt!");
                return;
            }
            if (kontrollierteRezepte <= 0) {
                abrDlg.setVisible(false);
                abrDlg.dispose();
                abrDlg = null;
                Reha.instance.progressStarten(false);
                JOptionPane.showMessageDialog(null,
                        "Für die ausgewählte Kasse sind keine Rezepte zur Abrechnung freigegeben!");
                return;

            }

            abgerechneteRezepte.clear();
            abgerechnetePatienten.clear();
            abrechnungRezepte = 0;
            preis00 = setzePreiseAufNull(preis00);
            preis11 = setzePreiseAufNull(preis11);
            preis31 = setzePreiseAufNull(preis31);
            preis51 = setzePreiseAufNull(preis51);
            positionenBuf.setLength(0);
            positionenBuf.trimToSize();
            unbBuf.setLength(0);
            unbBuf.trimToSize();
            unzBuf.setLength(0);
            unzBuf.trimToSize();
            gesamtBuf.setLength(0);
            gesamtBuf.trimToSize();
            auftragsBuf.setLength(0);
            auftragsBuf.trimToSize();
            positionenAnzahl = 0;
            abrDateiName = "";
            annahmeAdresseOk = false;
            /**********************************/

            abzurechnendeKassenID = getAktKTraeger();
            String preisgr = getPreisgruppenKuerzel(aktDisziplin);
            String cmd = "select ik_kasse,ik_kostent,ik_nutzer,ik_physika,ik_papier," + preisgr
                    + " from kass_adr where ik_kasse='" + abzurechnendeKassenID + "' LIMIT 1";
            kassenIKs.clear();
            kassenIKs = SqlInfo.holeFelder(cmd);
            if (kassenIKs.size() <= 0) {
                Reha.instance.progressStarten(false);
                abrDlg.setVisible(false);
                abrDlg.dispose();
                abrDlg = null;
                JOptionPane.showMessageDialog(null, "Fehler - Daten der Krankenkasse konnten nicht ermittelt werden");
                return;
            }

            ik_kasse = kassenIKs.get(0)
                                .get(0);
            ik_kostent = kassenIKs.get(0)
                                  .get(1);
            ik_nutzer = kassenIKs.get(0)
                                 .get(2);
            ik_physika = kassenIKs.get(0)
                                  .get(3);
            ik_papier = kassenIKs.get(0)
                                 .get(4);
            ik_email = SqlInfo.holeEinzelFeld("select email from ktraeger where ikkasse='" + ik_physika + "' LIMIT 1");


            if (abrechnungsModus.equals(ABR_MODE_302)) {
                if (ik_email.equals("")) {
                    ik_email = SqlInfo.holeEinzelFeld(
                            "select email1 from kass_adr where ik_kasse='" + ik_physika + "' LIMIT 1");
                }
                if (ik_email.equals("")) {
                    ik_email = SqlInfo.holeEinzelFeld(
                            "select email from ktraeger where ikkasse='" + ik_kasse + "' LIMIT 1");
                }
                if (ik_email.equals("")) {
                    ik_email = SqlInfo.holeEinzelFeld(
                            "select email from ktraeger where ikkasse='" + ik_kostent + "' LIMIT 1");
                }
                if (ik_email.equals("")) {
                    ik_email = SqlInfo.holeEinzelFeld(
                            "select email from ktraeger where ikkasse='" + ik_physika + "' LIMIT 1");
                }

                if (ik_email.trim()
                            .equals("")) {
                    JOptionPane.showMessageDialog(null,
                            "Emailadresse ist weder der Datenannahmestelle noch dem Kostenträger\noder der Kasse selbst zugeordnet."
                                    + "\n\n Bitte geben Sie die Emailadresse von Hand ein");
                    Object ret = JOptionPane.showInputDialog(null,
                            "Geben Sie bitte die Emailadresse der Datenannahmestelle ein", "");
                    if (ret == null) {
                        Reha.instance.progressStarten(false);
                        abrDlg.setVisible(false);
                        abrDlg.dispose();
                        abrDlg = null;
                        JOptionPane.showMessageDialog(null, "Fehler - keine Emailadresse eingegeben");
                        return;
                    }
                    ik_email = String.valueOf(ret);
                }
            }
            preisVector = RezTools.holePreisVector(disziSelect.getCurrRezClass(), Integer.parseInt(kassenIKs.get(0)
                                                                                                            .get(5))
                    - 1);
            name_kostent = holeNameKostentraeger();

            String test = "";
            if (abrechnungsModus.equals(ABR_MODE_302)) {
                test = "IK der Krankenkasse: " + ik_kasse + "\n" + "IK des Kostenträgers: " + ik_kostent + "\n"
                        + "IK des Nutzer mit Entschlüsselungsbefungnis: " + ik_nutzer + "\n"
                        + "IK der Datenannahmestelle: " + ik_physika + "\n" + "IK der Papierannahmestelle: " + ik_papier
                        + "\n" + "Emailadresse der Datenannahmestelle: " + ik_email + "\n" + "Name des Kostenträgers: "
                        + name_kostent;

                /************************************************/
                hmKostentraeger.put("name1", String.valueOf(name_kostent));
                /************************************************/
            } else {
                test = "IK der Krankenkasse: " + ik_kasse + "\n" + "Keine Abrechnung nach § 302!!!\n\n"
                        + "Rechnungsanschrift:\n" + hmAlternativeKasse.get("<Ivnam1>") + "\n"
                        + hmAlternativeKasse.get("<Ivnam2>") + "\n" + hmAlternativeKasse.get("<Ivstrasse>") + "\n"
                        + hmAlternativeKasse.get("<Ivplz>") + " " + hmAlternativeKasse.get("<Ivort>");
                hmKostentraeger.put("name1", String.valueOf(hmAlternativeKasse.get("<Ivnam1>")));
            }
            int anfrage = JOptionPane.showConfirmDialog(null, test, "Die Abrechnung mit diesen Parametern starten?",
                    JOptionPane.YES_NO_OPTION);
            if (anfrage != JOptionPane.YES_OPTION) {
                try {
                    doDlgAbort();
                } catch (Exception ex) {
                }
                return;
            }
            /*********** hier erst die Nummer erzeugen **************/
            aktRechnung = Integer.toString(SqlInfo.erzeugeNummer("rnr"));
            if (abrechnungsModus.equals(ABR_MODE_302)) {
                aktEsol = StringTools.fuelleMitZeichen(Integer.toString(ExUndHop.erzeugeNummerMitMax("esol", 999)), "0",
                        true, 3);
            }
            /************************************************/
            hmKostentraeger.put("aktesol", String.valueOf(aktEsol));
            /************************************************/
            aktDfue = StringTools.fuelleMitZeichen(Integer.toString(ExUndHop.erzeugeNummerMitMax("dfue", 99999)), "0",
                    true, 5);
            if (aktRechnung.equals("-1")) {
                Reha.instance.progressStarten(false);
                abrDlg.setVisible(false);
                abrDlg.dispose();
                abrDlg = null;
                JOptionPane.showMessageDialog(null, "Fehler - Rechnungsnummer kann nicht bezogen werden");
                return;
            }
            /*****************************************/
            if (abrechnungsModus.equals(ABR_MODE_302)) {
                if (ik_email.trim()
                            .equals("")) {
                    JOptionPane.showMessageDialog(null, "Dieser Kasse ist keine Emailadresse zugewiesen\n"
                            + "Abrechnung nach §302 ist nicht möglich!");
                    cancelRechnung(aktRechnung);
                    return;
                }
                if (ik_papier.trim()
                             .equals("")) {
                    JOptionPane.showMessageDialog(null, "Dieser Kasse ist keine Papierannahmestelle zugewiesen\n"
                            + "Abrechnung nach §302 ist nicht möglich!");
                    cancelRechnung(aktRechnung);
                    return;
                }
                if (ik_nutzer.trim()
                             .equals("")) {
                    JOptionPane.showMessageDialog(null,
                            "Dieser Kasse ist kein Nutzer mit Entschlüsselungsbefugnis zugewiesen\n"
                                    + "Abrechnung nach §302 ist nicht möglich!");
                    cancelRechnung(aktRechnung);
                    return;
                }
                if (ik_physika.trim()
                              .equals("")) {
                    JOptionPane.showMessageDialog(null,
                            "Dieser Kasse ist keine Empfänger der Abrechnungsdaten zugewiesen\n"
                                    + "Abrechnung nach §302 ist nicht möglich!");
                    cancelRechnung(aktRechnung);
                    return;
                }
                hmAnnahme = holeAdresseAnnahmestelle(true);
                annahmeAdresseOk = true;
            } else {
                hmAnnahme = holeAdresseAnnahmestelle(false);
                annahmeAdresseOk = true;
            }

            /********
             *
             *
             */
            abrDlg.setVisible(true);

            holeEdifact();
            macheKopfDaten();
            macheEndeDaten();


            gesamtBuf.append(unbBuf.toString());
            gesamtBuf.append(positionenBuf.toString());
            gesamtBuf.append(unzBuf.toString());
            abrDlg.setzeLabel("übertrage EDIFACT in Datenbank");

            if (Reha.vollbetrieb && abrechnungsModus.equals(ABR_MODE_302)) {
                PreparedStatement ps = null;
                try {
                    ps = (PreparedStatement) Reha.instance.conn.prepareStatement(
                            "insert into edifact (r_nummer, r_datum,r_edifact) VALUES (?,?,?)");
                    ps.setString(1, aktRechnung);
                    ps.setString(2, DatFunk.sDatInSQL(DatFunk.sHeute()));
                    ps.setBytes(3, gesamtBuf.toString()
                                            .getBytes());
                    ps.executeUpdate();
                    ps.close();
                    ps = null;
                } catch (SQLException e1) {
                    e1.printStackTrace();
                } finally {
                    if (ps != null) {
                        try {
                            ps.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        ps = null;
                    }
                }
            }

            if (abrechnungsModus.equals(ABR_MODE_302)) {
                try {

                    f = new File(Path.Instance.getProghome() + "edifact/" + Betriebsumfeld.getAktIK() + "/" + "esol0" + aktEsol
                            + ".org");
                    fw = new FileWriter(f);
                    bw = new BufferedWriter(fw);
                    bw.write(gesamtBuf.toString());
                    bw.flush();

                    bw.close();
                    fw.close();

                    abrDlg.setzeLabel("Rechnungsdatei verschlüsseln");
                    int originalSize = Integer.parseInt(Long.toString(f.length()));
                    int encryptedSize = originalSize;
                    String skeystore = SystemConfig.hmAbrechnung.get("hmkeystorefile");
                    File fkeystore = new File(skeystore);
                    if (!fkeystore.exists()) {
                        abrDlg.setzeLabel("Rechnungsdatei verschlüsseln - fehlgeschlagen!!!");
                        String message = "<html>Auf Ihrem System ist keine (ITSG) Zertifikatsdatenbank vorhanden.<br>"
                                + "Eine Verschlüsselung gemäß §302 SGB V kann daher nicht durchgeführt werden.<br><br>"
                                + "Melden Sie sich im Forum <a href='http://www.thera-pi.org'>www.Thera-Pi.org</a> und fragen Sie nach dem<br>Verschlüsseler <b>'Nebraska'</b></html>";
                        Reha.instance.progressStarten(false);
                        JOptionPane.showMessageDialog(null, message);

                    } else {

                        encryptedSize = doVerschluesseln();

                    }

                    if (encryptedSize < 0) {
                        JOptionPane.showMessageDialog(null, "Es ist ein Fehler in der Verschlüsselung aufgetreten!");
                        Reha.instance.progressStarten(false);
                        abrDlg.setVisible(false);
                        abrDlg.dispose();
                        abrDlg = null;
                        return;
                    }

                    doAuftragsDatei(originalSize, encryptedSize);

                    f = new File(Path.Instance.getProghome() + "edifact/" + Betriebsumfeld.getAktIK() + "/" + "esol0" + aktEsol
                            + ".auf");
                    fw = new FileWriter(f);
                    bw = new BufferedWriter(fw);
                    bw.write(auftragsBuf.toString());
                    bw.close();
                    fw.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                abrDlg.setzeLabel("erstelle Email an: " + ik_email);

                try {
                    if ("1".equals(SystemConfig.hmAbrechnung.get("hmaskforemail"))) {
                        int frage = JOptionPane.showConfirmDialog(null, "Wollen Sie die Abrechnungsdatei esol0"
                                + aktEsol + " an die Adresse\n" + "--> " + ik_email + " <-- versenden?",
                                "Benutzeranfrage", JOptionPane.YES_NO_OPTION);
                        if (frage == JOptionPane.YES_OPTION) {
                            doEmail();
                        }
                    } else {
                        doEmail();
                    }

                } catch (Exception ex) {
                    String meldung = "Die Dateien " + "esol0" + aktEsol + ".auf und " + "esol0" + aktEsol
                            + " sollten an die" + "Adresse " + ik_email + " gesendet werden.\n\n"
                            + "Versand ist fehlgeschlagen, bitte von Hand erneut senden";
                    JOptionPane.showMessageDialog(null, meldung);
                }

            }

            /*********************************************/

            abrDlg.setzeLabel("übertrage Rezepte in Historie");
            if (Reha.vollbetrieb) {
                doUebertragen();
                abrDlg.setzeLabel("organisiere Abrechnungsprogramm");
            }
            loescheFertigeRezepteAusKassenNode();
            Reha.instance.progressStarten(false);

            abrDlg.setVisible(false);
            abrDlg.dispose();
            abrDlg = null;
        } catch (Exception ex) {
            if (abrDlg != null) {
                Reha.instance.progressStarten(false);
                abrDlg.setVisible(false);
                abrDlg.dispose();
                abrDlg = null;
            }
            JOptionPane.showMessageDialog(null, "Fehler beim Abrechnungsvorgang:\n" + ex.getMessage());
            ex.printStackTrace();
        }
        this.abrRez.setRechtsAufNull();
    }

    /**
     * Abbruch Rechnungserstellung; bereits erzeugte Rechnungsnummer 'zurückgeben'
     * (McM 16-05)
     */
    private void cancelRechnung(String aktRnr) {
        if (aktRnr != null) {
            SqlInfo.sqlAusfuehren(
                    "update nummern set rnr='" + aktRnr + "' where mandant='" + Betriebsumfeld.getAktIK() + "' LIMIT 1");
        }
        doDlgAbort();
    }

    /********************************************************************/
    private void doDlgAbort() {
        if (abrDlg != null) {
            Reha.instance.progressStarten(false);
            abrDlg.setVisible(false);
            abrDlg.dispose();
            abrDlg = null;
        }
    }

    private void doEmail() {
        try {
            //// System.out.println("Erstelle Emailparameter.....");
            String smtphost = SystemConfig.hmEmailExtern.get("SmtpHost");
            // String pophost = SystemConfig.hmEmailExtern.get("Pop3Host");
            String authent = SystemConfig.hmEmailExtern.get("SmtpAuth");
            String benutzer = SystemConfig.hmEmailExtern.get("Username");
            String pass1 = SystemConfig.hmEmailExtern.get("Password");
            String sender = SystemConfig.hmEmailExtern.get("SenderAdresse");
            String secure = SystemConfig.hmEmailExtern.get("SmtpSecure");
            String useport = SystemConfig.hmEmailExtern.get("SmtpPort");
            String recipient = ik_email + "," + SystemConfig.hmEmailExtern.get("SenderAdresse");
            String text = "";
            boolean authx = (authent.equals("0") ? false : true);
            boolean bestaetigen = false;
            String[] encodedDat = {
                    Path.Instance.getProghome() + "edifact/" + Betriebsumfeld.getAktIK() + "/" + "esol0" + aktEsol,
                    "esol0" + aktEsol };
            String[] aufDat = {
                    Path.Instance.getProghome() + "edifact/" + Betriebsumfeld.getAktIK() + "/" + "esol0" + aktEsol + ".auf",
                    "esol0" + aktEsol + ".auf" };
            ArrayList<String[]> attachments = new ArrayList<String[]>();
            attachments.add(encodedDat);
            attachments.add(aufDat);
            EmailSendenExtern oMail = new EmailSendenExtern();
            try {
                oMail.sendMail(smtphost, benutzer, pass1, sender, recipient,
                        zertifikatVon.replace("IK", "")/* Reha.aktIK */, text, attachments, authx, bestaetigen, secure,
                        useport);
                oMail = null;

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Emailversand fehlgeschlagen\n\n" + "Mögliche Ursachen:\n"
                        + "- falsche Angaben zu Ihrem Emailpostfach und/oder dem Provider\n"
                        + "- Sie haben keinen Kontakt zum Internet" + "\n\nFehlertext:" + e.getLocalizedMessage());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Emailversand fehlgeschlagen\n\n" + "Mögliche Ursachen:\n"
                    + "- falsche Angaben zu Ihrem Emailpostfach und/oder dem Provider\n"
                    + "- Sie haben keinen Kontakt zum Internet - Fehlermeldung:\n\n" + ex.getLocalizedMessage());
        }
    }

    /********************************************************************/
    private void loescheFertigeRezepteAusKassenNode() {
        try {
            int lang = aktuellerKassenKnoten.getChildCount();
            JXTTreeNode node;
            for (int i = (lang - 1); i >= 0; i--) {
                node = (JXTTreeNode) aktuellerKassenKnoten.getChildAt(i);
                if (node.knotenObjekt.fertig) {
                    treeModelKasse.removeNodeFromParent(node);
                }
            }
            removeKassenNode(AbrechnungGKV.aktuellerKassenKnoten);
            treeKasse.validate();
            this.treeKasse.repaint();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /***************************************************************/
    private void doUebertragen() {
        try {
            String aktiverPatient = "";
            JComponent patient = AktiveFenster.getFensterAlle("PatientenVerwaltung");
            if (patient != null) {
                aktiverPatient = Reha.instance.patpanel.aktPatID;
            }

            Vector<String> feldNamen = SqlInfo.holeFeldNamen("verordn", true, Arrays.asList(new String[] { "id" }));

            rechnungBuf.setLength(0);
            rechnungBuf.trimToSize();
            rechnungBuf.append("select ");

            int rezepte = 0;
            int rezeptFelder = 0;
            for (int i = 0; i < feldNamen.size(); i++) {
                if (i > 0) {
                    rechnungBuf.append("," + feldNamen.get(i));
                } else {
                    rechnungBuf.append(feldNamen.get(i));
                }
            }
            rechnungBuf.append(" from verordn where rez_nr='");
            Vector<Vector<String>> vec = null;
            rezepte = abgerechneteRezepte.size();
            for (int i2 = 0; i2 < rezepte; i2++) {
                abrDlg.setzeLabel("übertrage Rezepte in Historie, übertrage Rezept: " + abgerechneteRezepte.get(i2));
                vec = SqlInfo.holeFelder(rechnungBuf.toString() + abgerechneteRezepte.get(i2) + "'");
                rezeptFelder = vec.get(0)
                                  .size();
                historieBuf.setLength(0);
                historieBuf.trimToSize();
                historieBuf.append("insert into lza set ");
                for (int i3 = 0; i3 < rezeptFelder; i3++) {
                    if (!vec.get(0)
                            .get(i3)
                            .equals("")) {
                        if (i3 > 0) {
                            historieBuf.append("," + feldNamen.get(i3) + "='" + StringTools.Escaped(vec.get(0)
                                                                                                       .get(i3))
                                    + "'");
                        } else {
                            historieBuf.append(feldNamen.get(i3) + "='" + StringTools.Escaped(vec.get(0)
                                                                                                 .get(i3))
                                    + "'");
                        }
                    }
                }

                SqlInfo.sqlAusfuehren(historieBuf.toString());

                /***
                 *
                 * In der Echtfunktion muß das Loeschen in der rezept-Datenbank eingeschaltet
                 * werden und das sofortige Löschen aus der Historie auschgeschaltet werden
                 *
                 */

                String delrez = String.valueOf(abgerechneteRezepte.get(i2));
                SqlInfo.sqlAusfuehren("delete from fertige where rez_nr='" + delrez + "' LIMIT 1");
                SqlInfo.sqlAusfuehren("delete from verordn where rez_nr='" + delrez + "' LIMIT 1");
                SqlInfo.sqlAusfuehren("delete from volle where rez_nr='" + delrez + "'");
                if (aktiverPatient.equals(abgerechnetePatienten.get(i2))) {
                    posteAktualisierung(aktiverPatient.toString());
                }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void posteAktualisierung(String patid) {
        final String xpatid = patid;
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                String s1 = String.valueOf("#PATSUCHEN");
                String s2 = xpatid;
                PatStammEvent pEvt = new PatStammEvent(this);
                pEvt.setPatStammEvent("PatSuchen");
                pEvt.setDetails(s1, s2, "");
                PatStammEventClass.firePatStammEvent(pEvt);
                return null;
            }

        }.execute();
    }

    private int doVerschluesseln() {
        try {
            String keystore = SystemConfig.hmAbrechnung.get("hmkeystorefile");

            NebraskaKeystore store = new NebraskaKeystore(keystore, SystemConfig.hmAbrechnung.get("hmkeystorepw"),
                    "123456", zertifikatVon.replace("IK", ""));

            NebraskaEncryptor encryptor = store.getEncryptor(ik_nutzer);
            String inFile = Path.Instance.getProghome() + "edifact/" + Betriebsumfeld.getAktIK() + "/" + "esol0" + aktEsol
                    + ".org";
            long size = encryptor.encrypt(inFile, inFile.replace(".org", ""));
            return Integer.parseInt(Long.toString(size));
        } catch (NebraskaCryptoException e) {
            e.printStackTrace();
        } catch (NebraskaFileException e) {
            e.printStackTrace();
        } catch (NebraskaNotInitializedException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /***************************************************************/
    private void doAuftragsDatei(int originalSize, int encryptedSize) {
        auftragsBuf.append("500000" + "01" + "00000348" + "000");
        auftragsBuf.append("ESOL0" + aktEsol);
        auftragsBuf.append("     ");
        auftragsBuf.append(
                StringTools.fuelleMitZeichen(zertifikatVon.replace("IK", "")/* Reha.aktIK */, " ", false, 15));
        auftragsBuf.append(
                StringTools.fuelleMitZeichen(zertifikatVon.replace("IK", "")/* Reha.aktIK */, " ", false, 15));
        auftragsBuf.append(StringTools.fuelleMitZeichen(ik_nutzer, " ", false, 15));
        auftragsBuf.append(StringTools.fuelleMitZeichen(ik_physika, " ", false, 15));
        auftragsBuf.append("000000");
        auftragsBuf.append("000000");
        auftragsBuf.append(abrDateiName);
        auftragsBuf.append(getEdiDatumFromDeutsch(DatFunk.sHeute()) + getEdiTimeString(true));
        auftragsBuf.append(StringTools.fuelleMitZeichen("0", "0", false, 14));
        auftragsBuf.append(StringTools.fuelleMitZeichen("0", "0", false, 14));
        auftragsBuf.append(StringTools.fuelleMitZeichen("0", "0", false, 14));
        auftragsBuf.append("000000");
        auftragsBuf.append("0");
        auftragsBuf.append(StringTools.fuelleMitZeichen(Integer.toString(originalSize), "0", true, 12));
        auftragsBuf.append(StringTools.fuelleMitZeichen(Integer.toString(encryptedSize), "0", true, 12));
        auftragsBuf.append("I800");
        auftragsBuf.append("0303");
        auftragsBuf.append("   ");
        auftragsBuf.append(StringTools.fuelleMitZeichen("0", "0", true, 5));
        auftragsBuf.append(StringTools.fuelleMitZeichen("0", "0", true, 8));
        auftragsBuf.append("0");
        auftragsBuf.append("00");
        auftragsBuf.append("0");
        auftragsBuf.append(StringTools.fuelleMitZeichen("0", "0", true, 10));
        auftragsBuf.append(StringTools.fuelleMitZeichen("0", "0", true, 6));
        auftragsBuf.append(StringTools.fuelleMitZeichen(" ", " ", true, 28));
        auftragsBuf.append(StringTools.fuelleMitZeichen(" ", " ", true, 44));
        auftragsBuf.append(StringTools.fuelleMitZeichen(" ", " ", true, 30));
        rlisteesol = String.valueOf(aktEsol); // aktEsol.toString();
        rlistekasse = String.valueOf(getAbrechnungKasse());
    }

    /*************************************************/
    private void macheEndeDaten() {
        String zeilenzahl = StringTools.fuelleMitZeichen(Integer.toString(positionenAnzahl + 4), "0", true, 6);
        unzBuf.append("UNT" + plus + zeilenzahl + plus + "00002" + EOL);
        unzBuf.append("UNZ" + plus + "000002" + plus + aktDfue + EOL);
    }

    /***************************************************************/

    private void macheKopfDaten() {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                if (!annahmeAdresseOk) {
                    long zeit = System.currentTimeMillis();
                    while (!annahmeAdresseOk) {
                        Thread.sleep(50);
                        if (System.currentTimeMillis() - zeit > 5000) {
                            JOptionPane.showMessageDialog(null,
                                    "Adresse der Annahmestelle konnte nicht ermittelt werden");
                            break;
                        }
                    }
                }

                if (abrDruck != null) {
                    abrDruck.setIKundRnr(ik_papier, aktRechnung, hmAnnahme);
                    abrDruck = null;
                } else {
                    JOptionPane.showMessageDialog(null, "Fehler im Rechnungsdruck - Fehler = abrDruck==null");
                }
                try {
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null,
                            "Fehler im Modul Rezept übertragen und Rechnung anlegen\n" + ex.getMessage());
                }
                if (abrechnungsModus.equals(ABR_MODE_302)) {
                    try {
                        Thread.sleep(100);
                        new BegleitzettelDrucken(AbrechnungGKV.this, abrechnungRezepte, ik_kostent, name_kostent, hmAnnahme,
                                aktRechnung, Path.Instance.getProghome() + "vorlagen/" + Betriebsumfeld.getAktIK() + "/"
                                        + SystemConfig.hmAbrechnung.get("hmgkvbegleitzettel"));
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null,
                                "Fehler im Modul BegleitzettlDrucken - Fehler-Exception: ex\n" + ex.getMessage());
                    }
                }
                return null;
            }
        }.execute();
        String sgruppe = null;
        if (this.aktDisziplin.equals("Rsport")) {
            sgruppe = "H";
        } else if (this.aktDisziplin.equals("Ftrain")) {
            sgruppe = "I";
        } else {
            sgruppe = "B";
        }

        unbBuf.append("UNB+UNOC:3+" + zertifikatVon.replace("IK", "") + plus + ik_nutzer + plus);
        unbBuf.append(getEdiDatumFromDeutsch(DatFunk.sHeute()) + ":" + getEdiTimeString(false) + plus);
        unbBuf.append(aktDfue + plus + sgruppe + plus);
        abrDateiName = "SL" + zertifikatVon.replace("IK", "")
                                           .substring(2, 8)
                + "S" + getEdiMonat();
        unbBuf.append(abrDateiName + plus);
        unbBuf.append("2" + EOL);

        unbBuf.append("UNH+00001+SLGA:" + SlgaVersion + ":0:0" + EOL);
        unbBuf.append("FKT+01" + plus + plus + Betriebsumfeld.getAktIK() + plus + ik_kostent + plus + ik_kasse + plus
                + zertifikatVon.replace("IK", "") + EOL);
        unbBuf.append("REC" + plus + aktRechnung + ":0" + plus + getEdiDatumFromDeutsch(DatFunk.sHeute()) + plus
                + (lOwnCert ? "1" : "2") + EOL);
        unbBuf.append("UST" + plus + SystemConfig.hmFirmenDaten.get("Steuernummer") + plus + "J" + EOL);
        unbBuf.append("GES" + plus + "00" + plus + dfx.format(preis00[0]) + plus + dfx.format(preis00[1]) + plus
                + dfx.format(preis00[2]) + EOL);
        unbBuf.append("GES" + plus + "11" + plus + dfx.format(preis11[0]) + plus + dfx.format(preis11[1]) + plus
                + dfx.format(preis11[2]) + EOL);
        unbBuf.append("GES" + plus + "31" + plus + dfx.format(preis31[0]) + plus + dfx.format(preis31[1]) + plus
                + dfx.format(preis31[2]) + EOL);
        unbBuf.append("GES" + plus + "51" + plus + dfx.format(preis51[0]) + plus + dfx.format(preis51[1]) + plus
                + dfx.format(preis51[2]) + EOL);
        unbBuf.append("NAM" + plus + (abrRez.hochKomma(SystemConfig.hmFirmenDaten.get("Ikbezeichnung"))
                                            .length() > 30
                                                    ? abrRez.hochKomma(SystemConfig.hmFirmenDaten.get("Ikbezeichnung"))
                                                            .substring(0, 30)
                                                    : abrRez.hochKomma(SystemConfig.hmFirmenDaten.get("Ikbezeichnung")))
                + plus + abrRez.hochKomma(SystemConfig.hmFirmenDaten.get("Anrede"))
                               .trim()
                + " " + (abrRez.hochKomma(SystemConfig.hmFirmenDaten.get("Nachname"))
                               .trim()
                               .length() > 25 ? abrRez.hochKomma(SystemConfig.hmFirmenDaten.get("Nachname"))
                                                      .trim()
                                                      .substring(0, 25)
                                       : abrRez.hochKomma(SystemConfig.hmFirmenDaten.get("Nachname"))
                                               .trim())
                + plus + SystemConfig.hmFirmenDaten.get("Telefon") + EOL);
        unbBuf.append("UNT+000010+00001" + EOL);
        unbBuf.append("UNH+00002+SLLA:" + SllaVersion + ":0:0" + EOL);
        unbBuf.append("FKT+01" + plus + plus + Betriebsumfeld.getAktIK() + plus + ik_kostent + plus + ik_kasse + EOL);
        unbBuf.append("REC" + plus + aktRechnung + ":0" + plus + getEdiDatumFromDeutsch(DatFunk.sHeute()) + plus
                + (lOwnCert ? "1" : "2") + EOL);
        getEdiTimeString(false);
    }

    /***************************************************************/



    public AbrechnungRezept getInstanceAbrechnungRezept() {
        return abrRez;
    }

    /***************************************************************/

    private String holeNameKostentraeger() {
        Vector<Vector<String>> vec = SqlInfo.holeFelder(
                "select name1 from ktraeger where ikkasse ='" + ik_kostent + "' LIMIT 1");
        if (vec.size() == 0) {
            return "";
        }
        return vec.get(0)
                  .get(0);
    }

    /***************************************************************/

    private HashMap<String, String> holeAdresseAnnahmestelle(boolean nach302) {
        HashMap<String, String> hmAdresse = new HashMap<String, String>();
        String[] hmKeys = { "<gkv1>", "<gkv2>", "<gkv3>", "<gkv4>", "<gkv5>", "<gkv6>" };
        if (nach302) {
            Vector<Vector<String>> vec = SqlInfo.holeFelder(
                    "select kassen_nam1,kassen_nam2,strasse,plz,ort from kass_adr where ik_kasse ='" + ik_papier
                            + "' LIMIT 1");
            if (vec.size() == 0) {
                vec = SqlInfo.holeFelder("select name1,name2,adresse3,adresse1,adresse2 from ktraeger where ikkasse ='"
                        + ik_papier + "' LIMIT 1");
                if (vec.size() == 0) {
                    for (int i = 0; i < hmKeys.length - 1; i++) {
                        hmAdresse.put(hmKeys[i], "");
                    }
                    hmAdresse.put(hmKeys[5], aktRechnung);
                    JOptionPane.showMessageDialog(null,
                            "Achtung Daten für die Papierannahmestelle konnt nicht ermittelt werden (vermutlich wieder mal AOK....)!\n\nBitte die Daten von Hand auf den Ausdrucken eintragen");
                    return hmAdresse;
                }
            }
            hmAdresse.put(hmKeys[0], vec.get(0)
                                        .get(0));
            hmAdresse.put(hmKeys[1], vec.get(0)
                                        .get(1));
            hmAdresse.put(hmKeys[2], vec.get(0)
                                        .get(2));
            hmAdresse.put(hmKeys[3], vec.get(0)
                                        .get(3)
                    + " " + vec.get(0)
                               .get(4));
            hmAdresse.put(hmKeys[4], "");
            hmAdresse.put(hmKeys[5], aktRechnung);
            return hmAdresse;
        } else {
            hmAdresse.put(hmKeys[0], hmAlternativeKasse.get("<Ivnam1>"));
            hmAdresse.put(hmKeys[1], hmAlternativeKasse.get("<Ivnam2>"));
            hmAdresse.put(hmKeys[2], hmAlternativeKasse.get("<Ivstrasse>"));
            hmAdresse.put(hmKeys[3], hmAlternativeKasse.get("<Ivplz>") + " " + hmAlternativeKasse.get("<Ivort>"));
            hmAdresse.put(hmKeys[4], "");
            hmAdresse.put(hmKeys[5], aktRechnung);
        }
        return hmAdresse;
    }

    /***************************************************************/

    private Double[] setzePreiseAufNull(Double[] preis) {
        preis[0] = 0.00;
        preis[1] = 0.00;
        preis[2] = 0.00;
        return preis;
    }

    private String getEdiMonat() {
        String tag = DatFunk.sHeute();
        return tag.substring(3, 5);
    }

    private String getEdiDatumFromDeutsch(String deutschDat) {
        if (deutschDat.trim()
                      .length() < 10) {
            return "";
        }
        return deutschDat.substring(6) + deutschDat.substring(3, 5) + deutschDat.substring(0, 2);
    }

    private String getEdiTimeString(boolean mitsekunden) {
        Date date = new Date();
        String[] datesplit = date.toString()
                                 .split(" ");
        if (mitsekunden) {
            return datesplit[3].substring(0, 2) + datesplit[3].substring(3, 5) + datesplit[3].substring(6, 8);
        }
        return datesplit[3].substring(0, 2) + datesplit[3].substring(3, 5);
    }

    /***************************************************************/

    private void holeEdifact() {
        try {
            if (SystemConfig.hmAbrechnung.get("hmgkvrauchdrucken")
                                         .equals("1")) {
                abrDruck = new AbrechnungDrucken(this, Path.Instance.getProghome() + "vorlagen/" + Betriebsumfeld.getAktIK() + "/"
                        + SystemConfig.hmAbrechnung.get("hmgkvformular"));
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Fehler im Modul Rechnungdruck in AbrechnungGKV.java");
            abrDruck = null;
            e.printStackTrace();
        }
        int lang = aktuellerKassenKnoten.getChildCount();
        JXTTreeNode node;
        Vector<Vector<String>> vec;
        for (int i = 0; i < lang; i++) {
            node = (JXTTreeNode) aktuellerKassenKnoten.getChildAt(i);
            if (node.knotenObjekt.fertig) {
                vec = SqlInfo.holeFelder(
                        "select edifact from fertige where rez_nr='" + node.knotenObjekt.rez_num + "'");
                try {
                    if (!annahmeAdresseOk) {
                        long zeit = System.currentTimeMillis();
                        while (!annahmeAdresseOk) {
                            Thread.sleep(50);
                            if (System.currentTimeMillis() - zeit > 5000) {
                                JOptionPane.showMessageDialog(null,
                                        "Adresse der Annahmestelle konnte nicht ermittelt werden");
                                break;
                            }
                        }
                    }
                    Thread.sleep(75);
                    abgerechneteRezepte.add(node.knotenObjekt.rez_num);
                    abgerechnetePatienten.add(node.knotenObjekt.pat_intern);
                    // hier den Edifact-Code analysieren und die Rechnungsdatei erstellen;
                    try {
                        analysierenEdifact(vec.get(0)
                                              .get(0),
                                node.knotenObjekt.rez_num);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null,
                                "Unbekannter Fehler bei Edifact analysierenEdifact()\n" + ex.getLocalizedMessage());

                    }
                    try {
                        anhaengenEdifact(vec.get(0)
                                            .get(0));
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null,
                                "Unbekannter Fehler bei Edifact anhaengenEdifact()\n" + ex.getLocalizedMessage());
                    }

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Unbekannter Fehler bei Edifact anhängen");
                }
            }
        }
        if (abgerechneteRezepte.size() > 0) {
            /************** Hier den offenen Posten anlegen ***************/
            abrDlg.setzeLabel("Offene Posten anlegen für Rechnung Nr.: " + aktRechnung);
            if (Reha.vollbetrieb) {
                anlegenOP();
            }

        }
    }

    /***************************************************************/

    private void anlegenOP() {
        /************************************************/
        rechnungBuf.setLength(0);
        rechnungBuf.trimToSize();
        rechnungBuf.append("insert into rliste set ");
        rechnungBuf.append("r_nummer='" + aktRechnung + "', ");
        rechnungBuf.append("r_datum='" + DatFunk.sDatInSQL(DatFunk.sHeute()) + "', ");
        rechnungBuf.append(
                "r_kasse='" + hmKostentraeger.get("name1") + ", " + "esol0" + hmKostentraeger.get("aktesol") + "', ");
        rechnungBuf.append("r_klasse='" + disziSelect.getCurrRezClass() + "', ");
        rechnungBuf.append("r_betrag='" + dfx.format(preis00[0])
                                             .replace(",", ".")
                + "', ");
        rechnungBuf.append("r_offen='" + dfx.format(preis00[0])
                                            .replace(",", ".")
                + "', ");
        rechnungBuf.append("r_zuzahl='" + dfx.format(preis00[2])
                                             .replace(",", ".")
                + "', ");
        rechnungBuf.append("ikktraeger='" + ik_kostent + "',");
        rechnungBuf.append("ik='" + Betriebsumfeld.getAktIK() + "'");
        SqlInfo.sqlAusfuehren(rechnungBuf.toString());
    }

    /***************************************************************/

    private void analysierenEdifact(String edifact, String rez_num) {
        Vector<String> position = new Vector<String>();
        Vector<BigDecimal> anzahl = new Vector<BigDecimal>();
        Vector<BigDecimal> preis = new Vector<BigDecimal>();
        Vector<BigDecimal> einzelpreis = new Vector<BigDecimal>();
        Vector<BigDecimal> einzelzuzahlung = new Vector<BigDecimal>();
        Vector<BigDecimal> rezgeb = new Vector<BigDecimal>();
        Vector<BigDecimal> abrtage = new Vector<BigDecimal>();
        BigDecimal bdAnzahl = null;
        BigDecimal einzelPreisTest = null;
        String[] zeilen = edifact.split("\n");
        boolean preisUmstellung = false;
        boolean zuzahlUmstellung = false;
        String[] woerter;
        String dummy;
        int pos = 0;
        int zugabe = 0;
        for (int i = 0; i < zeilen.length; i++) {
            if (zeilen[i].startsWith("EHE+") || zeilen[i].startsWith("ENF++")) {
                if (zeilen[i].startsWith("ENF++")) {
                    zugabe = 1;
                }
                woerter = zeilen[i].split("\\+");
                if (!position.contains(woerter[2 + zugabe])) {
                    position.add(woerter[2 + zugabe]);
                    bdAnzahl = BigDecimal.valueOf(Double.valueOf(woerter[3 + zugabe].replace(",", ".")));
                    anzahl.add(bdAnzahl);
                    abrtage.add(BigDecimal.valueOf(Double.valueOf("1.00")));
                    preis.add(BigDecimal.valueOf(Double.valueOf(woerter[4 + zugabe].replace(",", ".")))
                                        .multiply(bdAnzahl));
                    if (woerter.length == (7 + zugabe)) {
                        // Einstieg2 für Kilometer
                        dummy = woerter[6 + zugabe].replace("'", "")
                                                   .replace(",", ".");
                        if (zuzahlModusDefault) {
                            rezgeb.add(BigDecimal.valueOf(Double.valueOf(dummy)));
                            einzelzuzahlung.add(BigDecimal.valueOf(Double.valueOf(dummy)));
                        } else {
                            rezgeb.add(BigDecimal.valueOf(Double.valueOf(dummy))
                                                 .multiply(bdAnzahl));
                            einzelzuzahlung.add(BigDecimal.valueOf(Double.valueOf(dummy))
                                                          .multiply(bdAnzahl));
                        }
                    } else {
                        rezgeb.add(BigDecimal.valueOf(Double.valueOf("0.00")));
                        einzelzuzahlung.add(BigDecimal.valueOf(Double.valueOf("0.00")));
                    }

                    einzelpreis.add(BigDecimal.valueOf(Double.valueOf(woerter[4 + zugabe].replace(",", "."))));

                } else {
                    pos = position.indexOf(woerter[2 + zugabe]);
                    einzelPreisTest = BigDecimal.valueOf(Double.valueOf(woerter[4 + zugabe].replace(",", ".")));
                    if (!einzelPreisTest.equals(einzelpreis.get(pos))) {
                        preisUmstellung = true;
                    }
                    bdAnzahl = BigDecimal.valueOf(Double.valueOf(woerter[3 + zugabe].replace(",", ".")));
                    anzahl.set(pos, anzahl.get(pos)
                                          .add(BigDecimal.valueOf(
                                                  Double.valueOf(woerter[3 + zugabe].replace(",", ".")))));
                    preis.set(pos, preis.get(pos)
                                        .add(BigDecimal.valueOf(Double.valueOf(woerter[4 + zugabe].replace(",", ".")))
                                                       .multiply(bdAnzahl)));
                    abrtage.set(pos, abrtage.get(pos)
                                            .add(BigDecimal.valueOf(Double.valueOf("1.00"))));
                    if (woerter.length == (7 + zugabe)) {
                        // Einstieg3 für Kilometer
                        dummy = woerter[6 + zugabe].replace("'", "")
                                                   .replace(",", ".");
                        if (zuzahlModusDefault) {
                            rezgeb.set(pos, rezgeb.get(pos)
                                                  .add(BigDecimal.valueOf(Double.valueOf(dummy))));
                            if (!BigDecimal.valueOf(Double.valueOf(dummy))
                                           .equals(einzelzuzahlung.get(pos))) {
                                zuzahlUmstellung = true;
                            }
                        } else {
                            rezgeb.set(pos, rezgeb.get(pos)
                                                  .add(BigDecimal.valueOf(Double.valueOf(dummy))
                                                                 .multiply(bdAnzahl)));
                            if (!BigDecimal.valueOf(Double.valueOf(dummy))
                                           .multiply(bdAnzahl)
                                           .equals(einzelzuzahlung.get(pos))) {
                                zuzahlUmstellung = true;
                            }
                        }
                    } else {
                        rezgeb.set(pos, rezgeb.get(pos)
                                              .add(BigDecimal.valueOf(Double.valueOf("0.00"))));
                        if (!BigDecimal.valueOf(Double.valueOf("0.00"))
                                       .equals(einzelzuzahlung.get(pos))) {
                            zuzahlUmstellung = true;
                        }
                    }
                }
            }
            try {
                Thread.sleep(15);
            } catch (InterruptedException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Fehler in Thread.sleep(25) analysierenEdifact\n" + e.getMessage());
            }
        }
        String[] splits = zeilen[0].split(":");

        try {
            abrechnungRezepte++;
            if (abrDruck != null) {
                abrDruck.setDaten(splits[9].split("=")[1], splits[10].split("=")[1], splits[2].split("=")[1], position,
                        anzahl, abrtage, einzelpreis, preis, rezgeb,
                        (splits[6].split("=")[1].equals("10,00") ? true : false));
            }

            if (Reha.vollbetrieb) {
                schreibeInRechnungDB(splits, position, anzahl, abrtage, einzelpreis, preis, rezgeb, preisUmstellung,
                        zuzahlUmstellung);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***************************************************************/

    private void schreibeInRechnungDB(String[] kopf, Vector<String> positionen, Vector<BigDecimal> anzahl,
            Vector<BigDecimal> abrtage, Vector<BigDecimal> einzelpreis, Vector<BigDecimal> preis,
            Vector<BigDecimal> rezgeb, boolean preisUmstellung, boolean zuzahlUmstellung) {
        try {
            if (hmAnnahme.get("<gkv1>")
                         .trim()
                         .equals("")) {
                holeAdresseAnnahmestelle(true);
            }
        } catch (Exception ex) {
            holeAdresseAnnahmestelle(true);
        }
        abrDlg.setzeLabel("Rechnungssatz erstellen für Rezept: " + kopf[2].split("=")[1]);
        String cmdKopf = "insert into faktura set ";
        for (int i = 0; i < positionen.size(); i++) {
            rechnungBuf.setLength(0);
            rechnungBuf.trimToSize();
            rechnungBuf.append(cmdKopf);
            if (i == 0) {
                rechnungBuf.append("kassen_nam='" + hmAnnahme.get("<gkv1>") + "', ");
                rechnungBuf.append("kassen_na2='" + hmAnnahme.get("<gkv2>") + "', ");
                rechnungBuf.append("strasse='" + hmAnnahme.get("<gkv3>") + "', ");
                try {
                    rechnungBuf.append("plz='" + hmAnnahme.get("<gkv4>")
                                                          .split(" ")[0]
                            + "', ");
                    rechnungBuf.append("ort='" + hmAnnahme.get("<gkv4>")
                                                          .split(" ")[1]
                            + "', ");
                    String patName = StringTools.Escaped(kopf[9].split("=")[1]);
                    rechnungBuf.append("name='" + patName + "', ");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Fehler in der Adressaufbereitung - Tabelle=Faktura");
                    ex.printStackTrace();
                }
            }
            rechnungBuf.append("lfnr='" + Integer.toString(i) + "', ");
            rechnungBuf.append("status='" + kopf[10].split("=")[1] + "', ");
            rechnungBuf.append("pos_kas='" + positionen.get(i) + "', ");
            rechnungBuf.append(
                    "pos_int='" + RezTools.getIDFromPos(positionen.get(i), kopf[0].split("=")[1], preisVector) + "', ");
            rechnungBuf.append("anzahl='" + Integer.toString(anzahl.get(i)
                                                                   .intValue())
                    + "', ");
            rechnungBuf.append("anzahltage='" + Integer.toString(abrtage.get(i)
                                                                        .intValue())
                    + "', ");
            rechnungBuf.append("preis='" + dfx.format(einzelpreis.get(i)
                                                                 .doubleValue())
                                              .replace(",", ".")
                    + "', ");
            rechnungBuf.append("gesamt='" + dfx.format(preis.get(i)
                                                            .doubleValue())
                                               .replace(",", ".")
                    + "', ");
            rechnungBuf.append("zzbetrag='" + dfx.format(rezgeb.get(i)
                                                               .doubleValue())
                                                 .replace(",", ".")
                    + "', ");
            rechnungBuf.append("netto='" + dfx.format((preis.get(i)
                                                            .subtract(rezgeb.get(i))).doubleValue())
                                              .replace(",", ".")
                    + "', ");
            rechnungBuf.append("pauschale='" + kopf[6].split("=")[1].replace(",", ".") + "', ");
            rechnungBuf.append("rez_nr='" + kopf[2].split("=")[1] + "', ");
            if (!anzahl.get(i)
                       .equals(abrtage.get(i))) {
                rechnungBuf.append("kilometer='" + dfx.format((anzahl.get(i)
                                                                     .divide(abrtage.get(i))
                                                                     .doubleValue()))
                                                      .replace(",", ".")
                        + "', ");
            }
            rechnungBuf.append("rezeptart='0', ");
            rechnungBuf.append("pat_intern='" + kopf[1].split("=")[1] + "', ");
            rechnungBuf.append("rnummer='" + aktRechnung + "', ");
            rechnungBuf.append("kassid='" + kopf[7].split("=")[1] + "', ");
            rechnungBuf.append("arztid='" + kopf[8].split("=")[1] + "', ");
            rechnungBuf.append("zzindex='" + kopf[12].split("=")[1] + "', ");
            rechnungBuf.append("preisdiff='" + (preisUmstellung ? "T" : "F") + "', ");
            rechnungBuf.append("zuzahldiff='" + (zuzahlUmstellung ? "T" : "F") + "', ");
            rechnungBuf.append("disziplin='" + kopf[2].split("=")[1].subSequence(0, 2) + "', ");
            rechnungBuf.append("rdatum='" + DatFunk.sDatInSQL(DatFunk.sHeute()) + "',");
            rechnungBuf.append("ik='" + Betriebsumfeld.getAktIK() + "'");
            SqlInfo.sqlAusfuehren(rechnungBuf.toString());
        }

    }

    /*************************************************/
    private void anhaengenEdifact(String string) {
        String[] edi = string.split("\n");
        // String[] preise = edi[0].split(":");
        String status = "";
        try {
            status = edi[4].split("\\+")[2];
        } catch (Exception ex) {
            status = "10001";
        }
        for (int i = 4; i < edi.length; i++) {
            positionenBuf.append(edi[i] + System.getProperty("line.separator"));
            positionenAnzahl++;
        }
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (status.startsWith("1")) {
            preis11 = addierePreise(preis11, edi[edi.length - 1]);
        } else if (status.startsWith("3")) {
            preis31 = addierePreise(preis31, edi[edi.length - 1]);
        } else if (status.startsWith("5")) {
            preis51 = addierePreise(preis51, edi[edi.length - 1]);
        }
    }

    /*************************************************/
    private Double[] addierePreise(Double[] preis, String zeile) {
        String[] zahlen = zeile.split("\\+");
        Double brutto = Double.parseDouble(zahlen[1].replace(",", "."));
        Double zuzahl = Double.parseDouble(zahlen[2].replace(",", "."));
        preis[1] = preis[1] + brutto;
        preis[2] = preis[2] + zuzahl;
        preis[0] = preis[0] + (brutto - zuzahl);
        preis00[0] = preis00[0] + (brutto - zuzahl);
        preis00[1] = preis00[1] + (brutto);
        preis00[2] = preis00[2] + (zuzahl);
        return preis;
    }

    /*************************************************/
    public boolean isRezeptSelected() {
        if (treeKasse.getSelectionCount() <= 0) {
            return false;
        }
        TreePath path = treeKasse.getSelectionPath();
        return (path.getPathCount() >= 3);
    }

    String getAktKTraeger() {
        TreePath path = treeKasse.getSelectionPath();
        JXTTreeNode node = (JXTTreeNode) path.getLastPathComponent();
        return ((KnotenObjekt) node.getUserObject()).ikkasse;
    }

    public void setRezeptOk(boolean ok) {

        treeKasse.getSelectionCount();
        TreePath path = treeKasse.getSelectionPath();
        JXTTreeNode node = (JXTTreeNode) path.getLastPathComponent();
        ((KnotenObjekt) node.getUserObject()).fertig = ok;
        if (ok) {
            kontrollierteRezepte++;
        } else {
            kontrollierteRezepte--;
        }
        treeKasse.repaint();
    }

    public String getPreisgruppenKuerzel(String disziplin) { // nach 'Disziplinen' verschieben?
        if (disziplin.equals("Physio")) {
            return "pgkg";
        } else if (disziplin.equals("Massage")) {
            return "pgma";
        } else if (disziplin.equals("Ergo")) {
            return "pger";
        } else if (disziplin.equals("Logo")) {
            return "pglo";
        } else if (disziplin.equals("Reha")) {
            return "pgrh";
        } else if (disziplin.equals("Podo")) {
            return "pgpo";
        } else if (disziplin.equals("Rsport")) {
            return "pgrs";
        } else if (disziplin.equals("Ftrain")) {
            return "pgft";
        } else {
            return "pgkg";
        }
    }

    /***************************************/

    private static class JXTTreeNode extends DefaultMutableTreeNode {
        /**
         *
         */
        private static final long serialVersionUID = 2195590211796817012L;

        private KnotenObjekt knotenObjekt = null;

        public JXTTreeNode(KnotenObjekt obj, boolean enabled) {
            super();
            this.knotenObjekt = obj;
            if (obj != null) {
                this.setUserObject(obj);
            }
        }

        public KnotenObjekt getObject() {
            return knotenObjekt;
        }
    }

    /***************************************/
    class KnotenObjekt {
        public String titel;
        public boolean fertig;
        public String rez_num;
        public String ktraeger;
        public String pat_intern;
        public String entschluessel;
        public String ikkasse;
        public String preisgruppe;
        public String ohnepauschale;
        public boolean langfrist;
        public String langfristaz;
        private String ikpapier;
        private String ikNutzer;

        public KnotenObjekt(String titel, String rez_num, boolean fertig, String ikkasse, String preisgruppe) {
            this.titel = titel;
            this.fertig = fertig;
            this.rez_num = rez_num;
            this.ikkasse = ikkasse;
            this.preisgruppe = preisgruppe;
        }

        public void setIkPap(String ikpapier) {
            this.ikpapier = ikpapier;
        }

        public String getIkPap() {
            return this.ikpapier;
        }

        public void setIkNutzer(String iknutzer) {
            this.ikNutzer = iknutzer;
        }

        public String getIkNutzer() {
            return this.ikNutzer;
        }
    }

    /*************************************/
    private class KassenTreeModel extends DefaultTreeModel {
        /**
        *
        */
        private static final long serialVersionUID = 6391618556224740611L;

        public KassenTreeModel(JXTTreeNode node) {
            super(node);
        }

        public Object getValueAt(Object node, int column) {
            JXTTreeNode jXnode = (JXTTreeNode) node;

            KnotenObjekt o = null;
            o = (KnotenObjekt) jXnode.getUserObject();
            switch (column) {
            case 0:
                return o.titel;
            case 1:
                return o.fertig;

            }
            return jXnode.getObject().titel;
        }

        public int getColumnCount() {
            return 3;
        }

        public void setValueAt(Object value, Object node, int column) {
            JXTTreeNode jXnode = (JXTTreeNode) node;
            KnotenObjekt o;
            o = jXnode.getObject();
            switch (column) {
            case 0:
                o.titel = ((String) value);
                break;
            case 1:
                o.fertig = ((Boolean) value);
                break;
            }
        }

        public Class<?> getColumnClass(int column) {
            switch (column) {
            case 0:
                return String.class;
            case 1:
                return Boolean.class;
            }
            return Object.class;
        }
    }

    /*****************************************/
    private class KeyStore {
        /**
         * Zertifikatshandling
         */
        NebraskaKeystore keyStore = null;
        Vector<X509Certificate> certs = null;

        public void keyStore() throws NebraskaCryptoException, NebraskaFileException {
            this.init();
        }

        /**********
         *
         * Keystore öffnen
         */
        public void init() throws NebraskaCryptoException, NebraskaFileException {
            if (this.keyStore == null) { // (evtl. zusätzlich Überwachung des Dateidatums um auf Aktualisierung
                                         // reagieren zu können)
                String keyStoreLoc = SystemConfig.hmAbrechnung.get("hmkeystorefile"); // Reha.proghome+"keystore/"+Reha.aktIK+"/"+Reha.aktIK+".p12";
                this.keyStore = new NebraskaKeystore(keyStoreLoc, SystemConfig.hmAbrechnung.get("hmkeystorepw"),
                        "123456", SystemConfig.hmAbrechnung.get("hmkeystoreusecertof")
                                                           .replace("IK", ""));
                this.certs = keyStore.getAllCerts();
            }
        }

        /**********
         *
         * Restlaufzeit eines Zertifikates ermitteln
         *
         * @param alias String - IK des gesuchten Zertifikates
         * @return int Anz. Tage, wie lange das Zert noch gültig ist oder noCertFound,
         *         falls kein Zertifikat gefunden wurde.
         */
        public int getCertDaysValid(String alias) {
            try {
                this.init();
                String[] dn = null;
                String ik;
                long tage;
                for (int i = 0; i < certs.size(); i++) {
                    dn = certs.get(i)
                              .getSubjectDN()
                              .toString()
                              .split(",");
                    if (dn.length == 5) {
                        ik = (String) dn[3].split("=")[1];
                        if (ik.equals(alias)) { // gesuchtes Zertifikat gefunden
                            Date verfall = certs.get(i)
                                                .getNotAfter();
                            tage = Instant.now()
                                          .until(verfall.toInstant(), ChronoUnit.DAYS);
                            return (int) tage;
                        }
                    }
                }
            } catch (Exception ex) {
                return noCertFound;
            }
            return noCertFound;
        }

        /**********
         *
         * Eigenes Zertifikat auf Gültigkeit prüfen
         *
         * @param alias String - IK des eigenen Zertifikates
         * @return SystemConfig.certState
         */
        public int checkOwnCert(String alias) {
            abrRez.sperreAbrechnung(); // Abrechnung bleibt gesperrt bis Zertifikat geprüft ist
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    int daysLeft = getCertDaysValid(alias);
                    if (daysLeft <= 0) {
                        JOptionPane.showMessageDialog(null,
                                "Ihr Zertifikat ist abgelaufen.\nEine Verschlüsselung mit diesem Zertifikat ist nicht mehr möglich. Die Abrechnung ist gesperrt.");
                        SystemConfig.certState = SystemConfig.certIsExpired;
                    } else if (daysLeft <= 30) {
                        JOptionPane.showMessageDialog(null,
                                "Achtung!!!\nIhr Zertifikat läuft in " + Long.toString(daysLeft)
                                        + " Tage(n) ab.\nBitte rechtzeitig neues Zertifikat beantragen!");
                        SystemConfig.certState = SystemConfig.certWillExpire;
                        abrRez.erlaubeAbrechnung();
                    } else if (daysLeft == noCertFound) {
                        JOptionPane.showMessageDialog(null, "Kein Zertifikat für IK" + alias
                                + " gefunden!.\nVerschlüsselung und damit die 302-er Abrechnung ist nicht möglich.");
                        SystemConfig.certState = SystemConfig.certNotFound;
                    } else {
                        SystemConfig.certState = SystemConfig.certOK;
                        abrRez.erlaubeAbrechnung();
                    }
                    myCert.setState(SystemConfig.certState);
                    return null;
                }
            }.execute();
            return SystemConfig.certNotFound;
        }

        /**********
         *
         * Zertifikat einer Kasse (bzw. von deren Datenannahmestelle) auf Gültigkeit
         * prüfen
         *
         * @param currNode KnotenObjekt - Knoten im Kassenbaum
         * @return SystemConfig.certState
         */
        public int checkCertKT(KnotenObjekt currNode) {
            abrRez.sperreAbrechnung(); // Abrechnung bleibt gesperrt bis Zertifikat dieser Kasse geprüft ist
            new SwingWorker<Void, Void>() {
                String txtKasse = currNode.titel;
                String ikNutzer = "IK" + currNode.getIkNutzer();

                @Override
                protected Void doInBackground() throws Exception {
                    int daysLeft = getCertDaysValid(ikNutzer);
                    if (daysLeft <= 0) {
                        JOptionPane.showMessageDialog(null, "Das für " + txtKasse
                                + " zuständige Zertifikat im Keystore ist abgelaufen.\nVerschlüsselung und damit die 302-er Abrechnung wird daher gesperrt.");
                        SystemConfig.certState = SystemConfig.certIsExpired;
                    } else if (daysLeft == noCertFound) {
                        JOptionPane.showMessageDialog(null, "Kein für " + txtKasse
                                + " zuständiges Zertifikat im Keystore gefunden!.\nVerschlüsselung und damit die 302-er Abrechnung ist nicht möglich");
                        SystemConfig.certState = SystemConfig.certNotFound;
                    } else {
                        SystemConfig.certState = SystemConfig.certOK;
                        abrRez.erlaubeAbrechnung();
                    }
                    return null;
                }
            }.execute();
            return SystemConfig.certState;
        }

    }

    /*****************************************/
    private class KeepIkPap {
        /**
         * Vergleich, ob sich IK_Papier (bei Wechsel der Kasse) geändert hat
         */
        String lastIkPap;

        public KeepIkPap(String ik) {
            lastIkPap = ik;
        }

        public boolean newIkPap(String ik) {
            if (lastIkPap.equals(ik)) {
                return false;
            } else {
                lastIkPap = ik;
                return true;
            }
        }
    }

    /*****************************************/
    private class OwnCertState {
        /**
         * merkt sich den Zustand des eigenen Zertifikates
         */
        private boolean ownCertIsValid = false;

        public boolean isValid() {
            return ownCertIsValid;
        }

        public void setState(int state) {
            if ((state == SystemConfig.certOK) || (state == SystemConfig.certWillExpire)) {
                ownCertIsValid = true;
            } else {
                ownCertIsValid = false;
            }
        }
    }

    /*****************************************/
    private class MyRenderer extends DefaultTreeCellRenderer {
        /**
         *
         */
        private static final long serialVersionUID = 2333990367290526356L;
        Icon fertigIcon;

        public MyRenderer(Icon icon) {
            fertigIcon = icon;
        }

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
                boolean leaf, int row, boolean hasFocus) {

            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
            KnotenObjekt o = ((JXTTreeNode) value).knotenObjekt;
            this.setText(o.titel);
            if (leaf && istFertig(value)) {
                setIcon(fertigIcon);
                setToolTipText("Verordnung " + o.rez_num + " kann dirket abgerechnet werden.");
            } else {
                this.setText(o.titel);
            }
            if (!leaf) {
                // set Icon according to (change of) ik_papier;
                if (customIconList.contains(o.ktraeger)) {
                    setIcon(getDisabledIcon());
                } else {
                    setIcon(getIcon());
                }
                if (lateKtList.contains(o.ktraeger)) {
                    this.setForeground(Color.red); // Kasse sollte zeitnah abgerechnet werden (enthält VO nahe am MHD).
                }
            } else {
                if (lateVOList.contains(o.rez_num)) {
                    this.setForeground(Color.red); // VO nahe am MHD - umgehend abrechnen!
                }
            }
            return this;
        }

    }

    protected boolean istFertig(Object value) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        KnotenObjekt fertig = (KnotenObjekt) (node.getUserObject());
        return fertig.fertig ;
    }

    MouseListener mouseListener = new MouseAdapter() {



        @Override
        public void mousePressed(MouseEvent e) {
            if (e.getButton() == 3) { // Rechtsklick auf Rezept im Tree
                TreePath tp = treeKasse.getSelectionPath();

                if (tp == null) {
                    return;
                }
                JXTTreeNode node = (JXTTreeNode) tp.getLastPathComponent();
                String rez_nr = node.knotenObjekt.rez_num;
                if (!rez_nr.trim()
                           .equals("")) {
                    if (node.knotenObjekt.fertig) {
                        String msg = "<html>Achtung Sie editieren im Anschluß den EDIFACT-Code!<br>"
                                + "Wenn Sie den Code in unzulässiger Weise manipulieren<br>wird <b>der gesamte Abrechnungslauf unbrauchbar</b><br><br>"
                                + "<b>Rufen Sie diese Funktion nur dann auf wenn Sie genau wissen was Sie tun!!!</b><br><br>"
                                + "Soll die Funktion jetzt aufgerufen werden?<br></html>";
                        int frage = JOptionPane.showConfirmDialog(null, msg, "Achtung wichtige Benutzeranfrage",
                                JOptionPane.YES_NO_OPTION);
                        if (frage != JOptionPane.YES_OPTION) {
                            return;
                        }


                        EditEdifact editEdifact = new EditEdifact(Reha.getThisFrame(), "EDIFACT - editieren",
                                rez_nr.trim());
                        editEdifact.getContentPane()
                                   .setPreferredSize(new Dimension(600, 500));
                        editEdifact.setLocation(e.getXOnScreen() - 50, e.getYOnScreen() - 50);
                        editEdifact.pack();
                        editEdifact.setVisible(true);
                        abrRez.setNewRez(rez_nr, node.knotenObjekt.fertig, aktDisziplin);
                    } else {
                        JOptionPane.showMessageDialog(null,
                                "Abrechnungsdaten im Edifact-Format kann nur\nbei bereits markierten Rezepten manipuliert werden!");
                    }

                }
            }

        }


    };
    KeyListener keyListener = new KeyAdapter() {

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_F1) {
                TreePath tp = treeKasse.getSelectionPath();
                if (tp == null) {
                    return;
                }
                if (infoDlg != null) {
                    return;
                }

                String ikKasse = getaktuellerKassenKnoten().knotenObjekt.ikkasse;
                String kassenName = getaktuellerKassenKnoten().knotenObjekt.titel;

                Vector<Vector<String>> vecInArbeit = RezFromDB.getPendingVO(ikKasse);
                if (vecInArbeit.size() >= 0) {
                    infoDlg = new InfoDialogVOinArbeit(kassenName, vecInArbeit, volleVOs, abgebrocheneVOs,
                            connection);
                    infoDlg.pack();
                    infoDlg.setLocationRelativeTo(null);
                    infoDlg.setVisible(true);
                    infoDlg = null;
                }
            }
        }
    };
}
