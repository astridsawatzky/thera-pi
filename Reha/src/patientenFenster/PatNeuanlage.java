package patientenFenster;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.TerminalFactory;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingx.JXDialog;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thera_pi.common.image.JpegWriter;
import org.therapi.reha.patient.AktuelleRezepte;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.looks.windows.WindowsTabbedPaneUI;
import com.mysql.jdbc.PreparedStatement;

import CommonTools.DatFunk;
import CommonTools.ExUndHop;
import CommonTools.JCompTools;
import CommonTools.JRtaCheckBox;
import CommonTools.JRtaComboBox;
import CommonTools.JRtaTextField;
import CommonTools.SqlInfo;
import CommonTools.StringTools;
import CommonTools.ini.INITool;
import CommonTools.ini.Settings;
import environment.Path;
import events.PatStammEvent;
import events.PatStammEventClass;
import events.RehaTPEvent;
import events.RehaTPEventClass;
import events.RehaTPEventListener;
import hauptFenster.Reha;
import jxTableTools.TableTool;
import krankenKasse.KassenFormulare;
import oOorgTools.RehaOOTools;
import openMaps.CalcKilometer;
import openMaps.Distance;
import rechteTools.Rechte;
import stammDatenTools.ArztTools;
import stammDatenTools.ZuzahlTools;
import systemEinstellungen.SystemConfig;
import systemTools.ListenerTools;
import umfeld.Betriebsumfeld;

public class PatNeuanlage extends JXPanel implements RehaTPEventListener, ActionListener, KeyListener, FocusListener {

    private static final int TEXTFELD_INDEX_FUER_GEBURTSTAG = 11;
    /**
    *
    */
    private static final long serialVersionUID = -5089258058628709139L;
    /**
    *
    */

    public JRtaTextField[] jtf = { null, null, null, null, null, null, null, null, null, null, null, null, null, null,
            null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
            null, null, null, null, null }; // ,null,null,
                                            // null};
    JRtaCheckBox[] jcheck = { null, null, null, null, null, null, null, null, null, null, null };

    JXTable doclist = null;
    MyDocTableModel docmod = null;
    JButton delDocButton = null;
    JButton addDocButton = null;
    JButton chipButton = null;
    JButton speichernButton = null;
    JButton abbrechenButton = null;
    JButton hbRoute = null;
    JButton picVerwerfenButton = null;
    JButton picAufnehmenButton = null;
    JButton picBildDateiButton = null;

    JRtaComboBox cbanrede = null;

    // Lemmi 20110103: Merken der Originalwerte der eingelesenen Textfelder,
    // Combo- und Check-Boxen
    Vector<Object> originale = new Vector<Object>();

    String kassenid = "";
    String befreitdatum = "";
    String befreitbeginn = "";
    JLabel lblbild = null;
    boolean freizumstart = false;
    boolean freibeimspeichern = false;
    public FocusListener flis;
    public boolean feldergefuellt = false;

    JLabel kassenLab;
    JLabel arztLab;

    Font font = null;
    JScrollPane jscr = null;
    public List<String> xfelder = Arrays.asList(new String[] { "anrede", "n_name", "v_name", "strasse", "plz", "ort",
            "geboren", "telefonp", "telefong", "telefonm", "emaila", "kasse", "kv_nummer", "v_nummer", "kv_status",
            "bef_dat", "artz", "arzt_num", "therapeut", "abwanrede", "abwtitel", "abn_name", "abwv_name", "abwstrasse",
            "abwort", "akutdat", "termine1", "termine2", "kilometer", "heimbewohn", "jahrfrei", "bef_ab" });
    public List<String> checks = Arrays.asList(new String[] { "abwadress", "akutpat", "merk1", "merk2", "merk3",
            "merk4", "merk5", "merk6", "heimbewohn", "nobefr", "u18no" });
    // Achtung bei Feldgr��en �ber > 65 immer 2 abziehen wg. memofelder die
    // nicht eingelesen werden
    // 0 1 2 3 4 5 6 7 8 9 10 1112 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27
    // 28 29 30 31 32 33 34 35 36
    int[] fedits = { 0, 1, 2, 3, 4, 5, 6, 11, 7, 8, 9, 10, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26,
            27, 28, 29, 30, 31, 32, 33, 34, 35, 36 };
    int[] ffelder = { 0, 1, 2, 3, 21, 23, 24, 4, 18, 19, 20, 50, 13, 14, 16, 15, 31, 25, 26, 56, 6, 7, 8, 9, 10, 11, 12,
            46, 34, 36, 37, 48, 40, 65, 66, 67, 41 };
    int[] fchecks = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
    int[] ffelder2 = { 5, 33, 62, 61, 60, 59, 58, 57, 44, 68 };
    Vector<String> patDaten = null;
    ImageIcon hgicon;
    int icx, icy;
    AlphaComposite xac1 = null;
    AlphaComposite xac2 = null;
    String feldname = "";
    String globPat_intern = "";
    public static String arztbisher = "";
    public static String kassebisher = "";
    boolean inNeu = false;
    Vector<String> titel = new Vector<String>();
    Vector<String> formular = new Vector<String>();
    int iformular = -1;

    private RehaTPEventClass rtp = null;

    boolean editvoll = false;
    private JRtaTextField formularid = new JRtaTextField("NIX", false);

    boolean startMitBild = false;
    boolean updateBild = false;
    private static final Logger logger = LoggerFactory.getLogger(PatNeuanlage.class);

    public PatNeuanlage(Vector<String> vec, boolean neu, String sfeldname) {
        setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                setBackgroundPainter(Reha.instance.compoundPainter.get("PatNeuanlage"));
                return null;
            }
        }.execute();
        this.setDoubleBuffered(true);
        this.patDaten = vec;
        this.inNeu = neu;
        if (inNeu) {
            editvoll = Rechte.hatRecht(Rechte.Patient_anlegen, false);
        } else {
            editvoll = Rechte.hatRecht(Rechte.Patient_editvoll, false);
        }
        this.feldname = sfeldname;
        this.setBackground(Color.WHITE);
        this.setLayout(new BorderLayout());
        this.font = new Font("Tahome", Font.BOLD, 11);
        this.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        add(getButtonPanel(), BorderLayout.SOUTH);

        UIManager.put("TabbedPane.tabsOpaque", Boolean.FALSE);
        UIManager.put("TabbedPane.contentOpaque", Boolean.FALSE);

        JTabbedPane patTab = new JTabbedPane();
        try {
            patTab.setUI(new WindowsTabbedPaneUI());
        } catch (Exception ex) {
            // Kein KarstenLentsch LAF
        }
        // TabbedPaneUI tpUi = patTab.getUI();

        patTab.setOpaque(false);

        patTab.addTab("1 - Stammdaten", Tab1());
        patTab.addTab("2 - Zusätze", Tab2());
        patTab.addTab("3 - Sonstiges", Tab3());
        patTab.setMnemonicAt(0, '1');
        patTab.setMnemonicAt(1, '2');
        patTab.setMnemonicAt(2, '3');

        if (!editvoll) {
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    for (int i = 0; i < jtf.length; i++) {
                        if (jtf[i] != null) {
                            jtf[i].setEnabled(false);
                        }
                    }
                    for (int i = 0; i < jcheck.length; i++) {
                        if (jcheck[i] != null) {
                            jcheck[i].setEnabled(false);
                        }
                    }
                    doclist.setEnabled(false);
                    picVerwerfenButton.setEnabled(false);
                    picAufnehmenButton.setEnabled(false);
                    picBildDateiButton.setEnabled(false);
                    delDocButton.setEnabled(false);
                    addDocButton.setEnabled(false);
                    chipButton.setEnabled(false);
                    cbanrede.setEnabled(false);
                    jcheck[1].setEnabled(true);
                    jtf[7].setEnabled(true);
                    jtf[8].setEnabled(true);
                    jtf[9].setEnabled(true);
                    jtf[10].setEnabled(true);
                    jtf[19].setEnabled(true);
                    jtf[27].setEnabled(true);
                    jtf[28].setEnabled(true);
                    jtf[29].setEnabled(true);
                    jtf[30].setEnabled(true);

                    return null;
                }
            }.execute();

        }

        add(patTab, BorderLayout.CENTER);

        hgicon = Reha.rehaBackImg;
        icx = hgicon.getIconWidth() / 2;
        icy = hgicon.getIconHeight() / 2;
        xac1 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.07f);
        xac2 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f);

        rtp = new RehaTPEventClass();
        rtp.addRehaTPEventListener(this);
        this.addKeyListener(this);

        // ****************Checken ob Preisgruppen bedient
        // werden****************

        // nur für den ersten Focus setzen
        flis = new FocusListener() {

            @Override
            public void focusGained(FocusEvent arg0) {
                try {
                    if (((JComponent) arg0.getSource()).getName()
                                                       .equals("PatientenNeuanlage")) {
                        if (inNeu || feldname.equals("")) {
                            setzeFocus();
                        } else {
                            if (Reha.instance.patpanel.kid < 0 && feldname.equals("KASSE")) {
                                if (feldergefuellt) {
                                    // jtf[12].setText("?"+jtf[12].getText());
                                }
                            }
                            if (Reha.instance.patpanel.aid < 0 && feldname.equals("ARZT")) {
                                if (feldergefuellt) {
                                    // jtf[17].setText("?"+jtf[17].getText());
                                }
                            }
                            geheAufFeld(feldname);
                        }
                        removeFocusListener(this);
                    }
                } catch (java.lang.NullPointerException ex) {
                }
            }

            @Override
            public void focusLost(FocusEvent arg0) {
                // Do nothing
            }

        };
        this.addFocusListener(flis);

        new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground() throws Exception {
                for (int i = 0; i < ffelder.length; i++) {
                    jtf[ffelder[i]].addFocusListener(flis);
                }
                for (int i = 0; i < ffelder2.length; i++) {
                    jcheck[ffelder2[i]].addFocusListener(flis);
                }
                return null;
            }
        }.execute();

        if ((!this.inNeu)) {
            new Thread() {
                @Override
                public void run() {
                    fuelleFelder();
                    if (!feldname.equals("")) {
                        geheAufFeld(feldname);
                    }
                }
            }.start();
        } else {
            jtf[12].setText("?");
            jtf[17].setText("?");
        }

        UIManager.put("TabbedPane.tabsOpaque", Boolean.TRUE);
        UIManager.put("TabbedPane.contentOpaque", Boolean.TRUE);

        validate();
        repaint();

        // Lemmi 20110103: Merken der Originalwerte der eingelesenen Textfelder
        // SaveChangeStatus();
    }

    public void geheAufFeld(String feld) {
        // //System.out.println("Focus setzen auf "+feld);
        final String xfeld = feld;
        new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground() throws Exception {

                for (int i = 0; i < jtf.length; i++) {
                    if (jtf[i].getName() != null) {
                        if (jtf[i].getName()
                                  .trim()
                                  .toUpperCase()
                                  .equals(xfeld)) {
                            if (!jtf[i].hasFocus()) {
                                jtf[i].setCaretPosition(0);
                                jtf[i].requestFocusInWindow();
                            }
                            break;
                        }
                    }
                }
                return null;
            }

        }.execute();
        /*
         * SwingUtilities.invokeLater(new Runnable(){ public void run(){ } });
         */
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        if (hgicon != null) {
            g2d.setComposite(this.xac1);
            g2d.drawImage(hgicon.getImage(), (getWidth() / 2) - icx, (getHeight() / 2) - icy, null);
            g2d.setComposite(this.xac2);
        }
    }

    public void setzeFocus() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (!cbanrede.hasFocus()) {
                    cbanrede.requestFocusInWindow();
                }
            }
        });
    }

    public PatNeuanlage getInstance() {
        return this;
    }

    private void fuelleFelder() {
        // final String xfeld = this.feldname;

        new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground() throws Exception {

                List<String> nichtlesen = Arrays.asList(new String[] { "anamnese", "pat_text" });
                Vector<?> felder = SqlInfo.holeSatz("pat5", "*", "pat_intern='" + Reha.instance.patpanel.aktPatID + "'",
                        nichtlesen);
                int gros = felder.size();
                int anzahlf = fedits.length;
                int anzahlc = fchecks.length;
                if (gros > 0) {
                    String name = "";
                    for (int i = 0; i < anzahlf; i++) {
                        name = jtf[fedits[i]].getName()
                                             .trim();
                        if (name.contains("geboren") || name.contains("akutdat") || name.contains("akutbis")
                                || name.contains("bef_dat") || name.contains("bef_ab") || name.contains("er_dat")) {
                            String datum = String.valueOf(felder.get(ffelder[i]));
                            if (datum.trim()
                                     .length() > 0) {
                                // //System.out.println("Datum waere gewesen->"+datum+"
                                // Laenge->"+datum.trim().length());
                                jtf[fedits[i]].setText(DatFunk.sDatInDeutsch(datum));
                                jtf[fedits[i]].setValue(DatFunk.sDatInDeutsch(datum));
                            }

                        } else {
                            jtf[fedits[i]].setText((String) felder.get(ffelder[i]));
                        }
                    }
                }

                for (int i = 0; i < anzahlc; i++) {
                    jcheck[i].setSelected((felder.get(ffelder2[i])
                                                 .equals("F")
                            || felder.get(ffelder2[i])
                                     .equals("") ? false : true));
                }
                cbanrede.setSelectedItem(jtf[0].getText());

                /*
                 * if(!feldname.equals("")){ geheAufFeld(feldname); }
                 */
                arztbisher = jtf[17].getText();
                kassebisher = jtf[12].getText();
                kassenid = Reha.instance.patpanel.patDaten.get(68);
                befreitdatum = DatFunk.sDatInDeutsch(Reha.instance.patpanel.patDaten.get(31));
                freizumstart = (Reha.instance.patpanel.patDaten.get(30)
                                                               .equals("T") ? true : false);
                if (!jtf[35].getText()
                            .trim()
                            .equals("")) {
                    jcheck[10].setEnabled(true);
                }

                // System.out.println("Gehe auf Feld 1 -> "+getInstance().feldname);
                if (!"".equals(getInstance().feldname)) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            // System.out.println("Gehe auf Feld 2 -> "+getInstance().feldname);
                            feldergefuellt = true;
                            geheAufFeld(getInstance().feldname);
                        }
                    });
                }

                return null;
            }

        }.execute();

    }

    private void schreibeInDb() {
        if (jtf[2].getText()
                  .trim()
                  .equals("")
                || jtf[3].getText()
                         .trim()
                         .equals("")
                || jtf[4].getText()
                         .trim()
                         .equals("")
                || jtf[5].getText()
                         .trim()
                         .equals("")
                || jtf[6].getText()
                         .trim()
                         .equals("")
                || jtf[12].getText()
                          .trim()
                          .equals("")
                || jtf[11].getText()
                          .trim()
                          .equals(".  .")
                || jtf[17].getText()
                          .trim()
                          .equals("")) {
            JOptionPane.showMessageDialog(null,
                    "Die Daten des Patienten wurden unvollständig eingegeben!\n\nSpeichern ist nicht möglich.\n");
            cbanrede.requestFocus();
            return;
        }
        // int gros = felder.size();
        int anzahlf = fedits.length;
        int anzahlc = fchecks.length;
        int patintern = 0;
        String name = "";
        // String wert = "";
        String spatintern;
        StringBuffer buf = new StringBuffer();
        if (this.inNeu) {
            buf.append("insert into pat5 set ");
        } else {
            buf.append("update pat5 set ");
        }

        for (int i = 1; i < anzahlf; i++) {
            name = jtf[fedits[i]].getName()
                                 .trim();
            if (name.contains("geboren") || name.contains("akutdat") || name.contains("akutbis")
                    || name.contains("bef_dat") || name.contains("bef_ab") || name.contains("er_dat")) {
                if (jtf[fedits[i]].getText()
                                  .trim()
                                  .equals(".  .")) {
                    buf.append(name + "=NULL, ");
                    if (name.equals("bef_dat")) { // Wenn befreit bis testen ob
                                                  // er wert gr��er als heute
                        buf.append("befreit ='F', ");
                        freibeimspeichern = false;
                        // System.out.println("Patient ist -> nicht <- befreit!");
                    }
                } else {
                    try {
                        buf.append(name + "='" + DatFunk.sDatInSQL(jtf[fedits[i]].getText()) + "', ");
                        if (name.equals("bef_dat")) { // Wenn befreit bis testen
                                                      // ob er wert gr��er als
                                                      // heute
                            if (DatFunk.DatumsWert(jtf[fedits[i]].getText()) >= DatFunk.DatumsWert(DatFunk.sHeute())) {
                                buf.append("befreit ='T', ");
                                freibeimspeichern = true;
                                // System.out.println("Patient ist befreit!");
                            } else {
                                buf.append("befreit ='F', ");
                                freibeimspeichern = false;
                            }
                        }
                    } catch (java.lang.ArrayIndexOutOfBoundsException ex) {
                        buf.append(name + "='" + "*****Problem" + jtf[fedits[i]].getText() + "', ");
                    }
                }

            } else {
                buf.append(name + "='" + StringTools.Escaped(jtf[fedits[i]].getText()) + "', ");
            }
        }
        buf.append("anrede='" + ((String) cbanrede.getSelectedItem()).trim() + "'");
        for (int i = 0; i < anzahlc; i++) {
            name = jcheck[i].getName();
            buf.append(", " + name + "='" + (jcheck[i].isSelected() ? "T' " : "F' "));
        }

        if (!this.inNeu) {
            globPat_intern = Reha.instance.patpanel.aktPatID;
            buf.append(" where pat_intern='" + globPat_intern + "' LIMIT 1");
            spatintern = Reha.instance.patpanel.aktPatID;
            // Wenn Kasse veränderr wurde....
            if (!jtf[34].getText()
                        .trim()
                        .equals(kassenid)) {
                JOptionPane.showMessageDialog(null, "Achtung - Sie haben dem Patient eine neue Kasse zugewiesen.\n"
                        + "Eventuell ändert sich dadurch der Zuzahlungsstatus vorhandener Rezepte. Bitte prüfen!!!");
            }

            boolean doof = false;
            if (!(freizumstart == freibeimspeichern)) {
                if (((ZuzahlTools.getZuzahlRegel(jtf[34].getText()
                                                        .trim())) <= 0)) {
                    JOptionPane.showMessageDialog(null,
                            "Sie haben einen Kostenträger gwählt der keine Zuzahlung verlangt und\n"
                                    + "jetzt wollen Sie im Feld Zuzahlungsbefreiung rummurksen???????\n\nNa ja.....");
                    doof = true;
                }
                if (!doof) {
                    // hier wäre es optimal eine ZuzahlToolsFunktion zu
                    // haben.....
                    int anzahl = SqlInfo.zaehleSaetze("verordn",
                            "pat_intern='" + Reha.instance.patpanel.aktPatID + "' AND REZ_GEB='0.00'");
                    if (anzahl > 0) {
                        String meldung = "Dieser Patient hat -> " + anzahl + " laufende Rezepte <- ohne Abschluss\n"
                                + "Soll der veränderte Befreiungsstatus auf alle noch nicht(!) bezahlten Rezepte übertragen werden?";
                        int frage = JOptionPane.showConfirmDialog(null, meldung, "Achtung wichtige Benuterzanfrage",
                                JOptionPane.YES_NO_OPTION);
                        if (frage == JOptionPane.NO_OPTION) {
                            JOptionPane.showMessageDialog(null,
                                    "Dann eben nicht!\nVergessen Sie aber nicht den Befreiungsstatus der Rezepte von Hand zu ändern");
                        } else if (frage == JOptionPane.YES_OPTION) {
                            String pat_intern = Reha.instance.patpanel.aktPatID;
                            String geboren = DatFunk.sDatInDeutsch(Reha.instance.patpanel.patDaten.get(4));
                            String befreit = (freibeimspeichern ? "T" : "F");

                            String datum = (freibeimspeichern ? ""
                                    : jtf[16].getText()
                                             .trim());
                            ZuzahlTools.zzStatusEdit(pat_intern, geboren, "", befreit, jtf[34].getText()
                                                                                              .trim());
                        }
                    }
                }
            }
        } else {
            patintern = SqlInfo.erzeugeNummer("pat");
            if (patintern < 0) {
                JOptionPane.showMessageDialog(null,
                        "Fehler beim Bezug einer neuen Patientennummer\nNeustart des Programmes vermutlich erforderlich");
                return;
            }
            globPat_intern = Integer.toString(patintern);
            buf.append(",anl_datum='" + DatFunk.sDatInSQL(DatFunk.sHeute()) + "' ");
            buf.append(",pat_intern='" + globPat_intern + "'");
            spatintern = Integer.toString(patintern);
        }
        SqlInfo.sqlAusfuehren(buf.toString());
        if (updateBild) {
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    speichernPatBild((inNeu || !startMitBild), (ImageIcon) lblbild.getIcon(), globPat_intern);
                    return null;
                }

            }.execute();

        }

        ((JXDialog) this.getParent()
                        .getParent()
                        .getParent()
                        .getParent()
                        .getParent()).setVisible(false);

        String rez_num = "";
        if (AktuelleRezepte.tabelleaktrez.getRowCount() > 0) {
            int row = AktuelleRezepte.tabelleaktrez.getSelectedRow();
            if (row >= 0) {
                rez_num = AktuelleRezepte.tabelleaktrez.getValueAt(row, 0)
                                                       .toString()
                                                       .trim();
            }
        }
        final String xpatintern = spatintern;
        final String xrez = rez_num;
        new Thread() {
            @Override
            public void run() {
                Reha.instance.patpanel.getLogic()
                                      .arztListeSpeichernVector((Vector<?>) docmod.getDataVector()
                                                                                  .clone(),
                                              inNeu, String.valueOf(globPat_intern));
                finalise();
                ((JXDialog) getInstance().getParent()
                                         .getParent()
                                         .getParent()
                                         .getParent()
                                         .getParent()).dispose();
                String s1 = "#PATSUCHEN";
                String s2 = xpatintern;
                PatStammEvent pEvt = new PatStammEvent(getInstance());
                pEvt.setPatStammEvent("PatSuchen");
                pEvt.setDetails(s1, s2, xrez);
                PatStammEventClass.firePatStammEvent(pEvt);
                pEvt = null;

            }
        }.start();

    }

    private JXPanel getButtonPanel() {
        JXPanel but = new JXPanel(new BorderLayout());
        but.setOpaque(false);
        but.setDoubleBuffered(true);

        chipButton = new JButton("Chipkarte");
        chipButton.setPreferredSize(new Dimension(70, 20));
        chipButton.addActionListener(this);
        chipButton.setActionCommand("einlesen");
        chipButton.setName("einlesen");
        chipButton.addKeyListener(this);
        chipButton.setMnemonic(KeyEvent.VK_C);
        chipButton.setEnabled(true);

        speichernButton = new JButton("speichern");
        speichernButton.setPreferredSize(new Dimension(70, 20));
        speichernButton.addActionListener(this);
        speichernButton.setActionCommand("speichern");
        speichernButton.setName("speichern");
        speichernButton.addKeyListener(this);
        speichernButton.setMnemonic(KeyEvent.VK_S);

        abbrechenButton = new JButton("abbrechen");
        abbrechenButton.setPreferredSize(new Dimension(70, 20));
        abbrechenButton.addActionListener(this);
        abbrechenButton.setActionCommand("abbrechen");
        abbrechenButton.setName("abbrechen");
        abbrechenButton.addKeyListener(this);
        abbrechenButton.setMnemonic(KeyEvent.VK_A);

        // 1. 2. 3. 4. 5. 6. 7
        FormLayout lay = new FormLayout("fill:0:grow(0.50), 60dlu,15dlu, 60dlu, 15dlu,60dlu,fill:0:grow(0.50) ",
                // 1. 2. 3. 4. 5.
                "4dlu, p, 4dlu");
        PanelBuilder builder = new PanelBuilder(lay);
        builder.setDefaultDialogBorder();
        builder.getPanel()
               .setOpaque(false);

        CellConstraints cc = new CellConstraints();
        builder.add(chipButton, cc.xy(2, 2));
        builder.add(speichernButton, cc.xy(4, 2));
        builder.add(abbrechenButton, cc.xy(6, 2));

        but.add(builder.getPanel(), BorderLayout.CENTER);
        return but;
    }

    private JXPanel Tab1() {
        JXPanel tab1 = new JXPanel(new BorderLayout());
        tab1.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        tab1.setOpaque(false);
        tab1.setDoubleBuffered(true);
        tab1.add(getDatenPanel12(), BorderLayout.EAST);
        tab1.add(getDatenPanel11(), BorderLayout.WEST);
        tab1.validate();
        return tab1;
    }

    private JXPanel Tab2() {
        JXPanel tab2 = new JXPanel(new BorderLayout());
        tab2.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        tab2.setOpaque(false);
        tab2.setDoubleBuffered(true);
        tab2.add(getDatenPanel22(), BorderLayout.EAST);
        tab2.add(getDatenPanel21(), BorderLayout.WEST);
        tab2.validate();
        holeFormulare();
        return tab2;
    }

    private JXPanel Tab3() {
        JXPanel tab3 = new JXPanel(new BorderLayout());
        tab3.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        tab3.setOpaque(false);
        tab3.setDoubleBuffered(true);
        tab3.add(getDatenPanel32(), BorderLayout.EAST);
        tab3.add(getDatenPanel31(), BorderLayout.WEST);

        return tab3;
    }

    private JXPanel getDatenPanel12() {

        JXPanel pat12 = new JXPanel(new BorderLayout());
        pat12.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        pat12.setOpaque(false);
        pat12.setDoubleBuffered(true);

        jtf[12] = new JRtaTextField("GROSS", true);
        jtf[12].setName("kasse");
        jtf[12].addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent arg0) {
                if (arg0.getKeyChar() == '?') {
                    String suchkrit = jtf[12].getText()
                                             .replaceAll("\\?", "");
                    kassenAuswahl(new String[] { suchkrit, jtf[34].getText()
                                                                  .trim(),
                            jtf[34].getText() });
                }
            }
        });
        jtf[12].addFocusListener(this);

        jtf[13] = new JRtaTextField("ZAHLEN", true);
        jtf[13].setName("kv_nummer");

        jtf[14] = new JRtaTextField("GROSS", true);
        jtf[14].setName("v_nummer");

        jtf[15] = new JRtaTextField("GROSS", true);
        jtf[15].setName("kv_status");

        jtf[16] = new JRtaTextField("DATUM", true);
        jtf[16].setName("bef_dat"); // aus Kostentr�gerdatei/Karte einlesen?
        jtf[16].setFont(font);
        jtf[16].setForeground(Color.RED);

        jtf[36] = new JRtaTextField("DATUM", true);
        jtf[36].setName("bef_ab");
        jtf[36].setFont(font);
        jtf[36].setForeground(Color.RED);

        jtf[17] = new JRtaTextField("GROSS", true);
        jtf[17].setName("arzt");
        jtf[17].addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent arg0) {
                if (arg0.getKeyChar() == '?') {
                    arg0.consume();
                    String[] suchkrit = { jtf[17].getText()
                                                 .replaceAll("\\?", ""),
                            jtf[33].getText() };
                    arztAuswahl(suchkrit);
                }
            }

        });
        jtf[17].addFocusListener(this);

        jtf[18] = new JRtaTextField("ZAHLEN", true);
        jtf[18].setName("arzt_num");

        jtf[19] = new JRtaTextField("GROSS", true);
        jtf[19].setName("therapeut");

        jtf[33] = new JRtaTextField("ZAHLEN", true);
        jtf[33].setName("arztid");

        jtf[34] = new JRtaTextField("ZAHLEN", true);
        jtf[34].setName("kassenid");

        // 1. 2. 3. 4. 5. 6.
        FormLayout lay12 = new FormLayout("right:max(80dlu;p), 4dlu, 60dlu,right:max(60dlu;p), 4dlu, 60dlu",
                // 1. 2. 3. 4. 5. 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22
                // 23 24 25 26 27 28 29 30 31 32 33 34 35 36 37 38 39 40 41 42
                // 43 44 45 46 47 48 49 50 51 52 53 54 55 56 57 58 59 60 61 62
                // 63 64 65 66 67
                "p, 10dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 82dlu, p, 20dlu, p, 2dlu, p, 2dlu, p, 2dlu, p");

        PanelBuilder builder12 = new PanelBuilder(lay12);
        builder12.setDefaultDialogBorder();
        builder12.getPanel()
                 .setOpaque(false);
        CellConstraints cc12 = new CellConstraints();
        builder12.getPanel()
                 .setDoubleBuffered(true);

        builder12.addSeparator("Krankenversicherung", cc12.xyw(1, 1, 6));
        kassenLab = new JLabel("Kasse *)");
        kassenLab.setIcon(SystemConfig.hmSysIcons.get("kleinehilfe"));
        kassenLab.setHorizontalTextPosition(JLabel.LEFT);

        kassenLab.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent ev) {
                if (editvoll) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            String suchkrit = jtf[12].getText()
                                                     .replace("?", "");
                            kassenAuswahl(new String[] { suchkrit, jtf[34].getText()
                                                                          .trim(),
                                    jtf[34].getText() });
                        }
                    });
                }
            }
        });
        builder12.add(kassenLab, cc12.xy(1, 3));
        builder12.add(jtf[12], cc12.xyw(3, 3, 4));
        builder12.addLabel("Kassen-IK", cc12.xy(1, 5));
        builder12.add(jtf[13], cc12.xyw(3, 5, 4));
        builder12.addLabel("Vers-Nr.", cc12.xy(1, 7));
        builder12.add(jtf[14], cc12.xyw(3, 7, 4));
        builder12.addLabel("Status", cc12.xy(1, 9));
        builder12.add(jtf[15], cc12.xy(3, 9));
        builder12.addLabel("Befreit von", cc12.xy(1, 11));
        builder12.add(jtf[36], cc12.xy(3, 11));
        builder12.addLabel("bis ", cc12.xy(4, 11));
        builder12.add(jtf[16], cc12.xy(6, 11));

        builder12.addSeparator("Arzt / Therapeut", cc12.xyw(1, 23, 6));
        arztLab = new JLabel("Hausarzt *)");
        arztLab.setIcon(SystemConfig.hmSysIcons.get("kleinehilfe"));
        arztLab.setHorizontalTextPosition(JLabel.LEFT);
        arztLab.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent ev) {
                if (editvoll) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            String[] suchkrit = { jtf[17].getText()
                                                         .replace("?", ""),
                                    jtf[33].getText() };
                            arztAuswahl(suchkrit);
                        }
                    });
                }
            }
        });
        builder12.add(arztLab, cc12.xy(1, 25));
        builder12.add(jtf[17], cc12.xyw(3, 25, 4));
        builder12.addLabel("ArztNummer (LANR)", cc12.xy(1, 27));
        builder12.add(jtf[18], cc12.xyw(3, 27, 4));
        builder12.addLabel("Betreuer/Therapeut", cc12.xy(1, 29));
        builder12.add(jtf[19], cc12.xyw(3, 29, 4));

        JScrollPane jscrzusatz = JCompTools.getTransparentScrollPane(builder12.getPanel());
        jscrzusatz.getVerticalScrollBar()
                  .setUnitIncrement(15);
        jscrzusatz.getViewport()
                  .setOpaque(false);
        jscrzusatz.setBorder(null);
        jscrzusatz.setViewportBorder(null);
        jscrzusatz.validate();
        jscrzusatz.addKeyListener(this);
        pat12.add(jscrzusatz, BorderLayout.CENTER);
        pat12.validate();
        return pat12;
    }

    private void finalise() {
        for (int i = 0; i < jtf.length; i++) {
            if (jtf[i] != null) {
                ListenerTools.removeListeners(jtf[i]);
                jtf[i] = null;
            }

        }
        for (int i = 0; i < jcheck.length; i++) {
            if (jcheck[i] != null) {
                ListenerTools.removeListeners(jcheck[i]);
                jcheck[i] = null;
            }
        }
        ListenerTools.removeListeners(delDocButton);
        ListenerTools.removeListeners(addDocButton);
        ListenerTools.removeListeners(chipButton);
        ListenerTools.removeListeners(speichernButton);
        ListenerTools.removeListeners(abbrechenButton);
        xfelder = null;
        checks = null;
        doclist = null;
        docmod = null;
        if (rtp != null) {
            rtp.removeRehaTPEventListener(this);
            rtp = null;
        }
    }

    private JXPanel getDatenPanel21() {
        JXPanel pat21 = new JXPanel(new BorderLayout());
        pat21.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        pat21.setOpaque(false);
        pat21.setDoubleBuffered(true);

        jtf[20] = new JRtaTextField("GROSS", true);
        jtf[20].setName("abwanrede");

        jtf[21] = new JRtaTextField("GROSS", true);
        jtf[21].setName("abwtitel");

        jtf[22] = new JRtaTextField("GROSS", true);
        jtf[22].setName("abwn_name");

        jtf[23] = new JRtaTextField("GROSS", true);
        jtf[23].setName("abwv_name");

        jtf[24] = new JRtaTextField("GROSS", true);
        jtf[24].setName("abwstrasse");

        jtf[25] = new JRtaTextField("GROSS", true);
        jtf[25].setName("abwplz");

        jtf[26] = new JRtaTextField("GROSS", true);
        jtf[26].setName("abwort");

        jtf[31] = new JRtaTextField("ZAHLEN", true);
        jtf[31].setName("kilometer");

        jtf[32] = new JRtaTextField("DATUM", true);
        jtf[32].setName("er_dat");

        jtf[35] = new JRtaTextField("ZAHLEN", true);
        jtf[35].setName("jahrfrei");
        jtf[35].setEnabled(false);

        jcheck[0] = new JRtaCheckBox();
        jcheck[0].setOpaque(false);
        jcheck[0].setName("abwadress");

        jcheck[8] = new JRtaCheckBox();
        jcheck[8].setOpaque(false);
        jcheck[8].setName("heimbewohn");

        jcheck[10] = new JRtaCheckBox();
        jcheck[10].setOpaque(false);
        jcheck[10].setName("vorjahrfrei");
        jcheck[10].setActionCommand("vorjahrfrei");
        jcheck[10].addActionListener(this);

        jcheck[9] = new JRtaCheckBox();
        jcheck[9].setOpaque(false);
        jcheck[9].setName("u18ignore");
        jcheck[9].setActionCommand("u18ignore");
        jcheck[9].setEnabled(false);
        jcheck[9].addActionListener(this);

        FormLayout lay21 = new FormLayout("right:max(80dlu;p), 4dlu, 60dlu,right:max(60dlu;p), 4dlu, 60dlu",
                // 1. 2. 3. 4. 5. 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22
                // 23 24 25 26 27 28 29 30 31 32 33 34 35 36 37 38 39 40 41 42
                // 43 44 45 46 47 48 49 50 51 52 53 54 55 56 57 58 59 60 61 62
                // 63 64 65 66 67
                "p, 10dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 10dlu, p, 10dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p");

        PanelBuilder builder21 = new PanelBuilder(lay21);
        builder21.setDefaultDialogBorder();
        builder21.getPanel()
                 .setOpaque(false);
        CellConstraints cc21 = new CellConstraints();
        builder21.getPanel()
                 .setDoubleBuffered(true);

        builder21.addSeparator("Zusätze", cc21.xyw(1, 1, 6));
        builder21.addLabel("Kilometer bei HB", cc21.xy(1, 3));
        builder21.add(jtf[31], cc21.xy(3, 3));

        hbRoute = new JButton("Route");
        hbRoute.addActionListener(this);
        hbRoute.setName("route");
        hbRoute.setActionCommand("route");

        builder21.add(hbRoute, CC.xy(4, 3));

        builder21.addLabel("Heimbewohner", cc21.xy(1, 5));
        builder21.add(jcheck[8], cc21.xy(3, 5));
        builder21.addLabel("befreit im Vorjahr", cc21.xy(1, 7));
        builder21.add(jtf[35], cc21.xy(3, 7));
        builder21.addLabel("löschen?", cc21.xy(4, 7));
        builder21.add(jcheck[10], cc21.xy(6, 7));
        builder21.addLabel("U18-Regel ignorieren?", cc21.xy(1, 9));
        builder21.add(jcheck[9], cc21.xy(3, 9));
        builder21.addLabel("Vertrag unterz. am", cc21.xy(1, 11));
        builder21.add(jtf[32], cc21.xy(3, 11));

        builder21.addSeparator("abweichender Rechnungsempfänger/Versicherter", cc21.xyw(1, 13, 6));
        builder21.addLabel("verwenden", cc21.xy(1, 15));
        builder21.add(jcheck[0], cc21.xy(3, 15));
        builder21.addLabel("Anrede", cc21.xy(1, 17));
        builder21.add(jtf[20], cc21.xy(3, 17));
        builder21.addLabel("Titel", cc21.xy(4, 17));
        builder21.add(jtf[21], cc21.xy(6, 17));
        builder21.addLabel("Nachname", cc21.xy(1, 19));
        builder21.add(jtf[22], cc21.xyw(3, 19, 4));
        builder21.addLabel("Vorname", cc21.xy(1, 21));
        builder21.add(jtf[23], cc21.xyw(3, 21, 4));
        builder21.addLabel("Strasse, Nr.", cc21.xy(1, 23));
        builder21.add(jtf[24], cc21.xyw(3, 23, 4));
        builder21.addLabel("PLZ, Ort", cc21.xy(1, 25));
        builder21.add(jtf[25], cc21.xy(3, 25));
        builder21.add(jtf[26], cc21.xyw(4, 25, 3));

        JScrollPane jscrzusatz = JCompTools.getTransparentScrollPane(builder21.getPanel());
        jscrzusatz.getVerticalScrollBar()
                  .setUnitIncrement(15);
        jscrzusatz.getViewport()
                  .setOpaque(false);
        jscrzusatz.setBorder(null);
        jscrzusatz.setViewportBorder(null);
        jscrzusatz.validate();
        jscrzusatz.addKeyListener(this);
        pat21.add(jscrzusatz, BorderLayout.CENTER);
        pat21.validate();
        return pat21;
    }

    private JXPanel getDatenPanel22() {
        JXPanel pat22 = new JXPanel(new BorderLayout());
        pat22.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        pat22.setOpaque(false);
        pat22.setDoubleBuffered(true);

        jcheck[2] = new JRtaCheckBox(SystemConfig.vPatMerker.get(0));
        jcheck[2].setOpaque(false);
        jcheck[2].setName("merk1");
        jcheck[2].setToolTipText(SystemConfig.vPatMerker.get(0));

        jcheck[3] = new JRtaCheckBox(SystemConfig.vPatMerker.get(1));
        jcheck[3].setOpaque(false);
        jcheck[3].setName("merk2");
        jcheck[3].setToolTipText(SystemConfig.vPatMerker.get(1));

        jcheck[4] = new JRtaCheckBox(SystemConfig.vPatMerker.get(2));
        jcheck[4].setOpaque(false);
        jcheck[4].setName("merk3");
        jcheck[4].setToolTipText(SystemConfig.vPatMerker.get(2));

        jcheck[5] = new JRtaCheckBox(SystemConfig.vPatMerker.get(3));
        jcheck[5].setOpaque(false);
        jcheck[5].setName("merk4");
        jcheck[5].setToolTipText(SystemConfig.vPatMerker.get(3));

        jcheck[6] = new JRtaCheckBox(SystemConfig.vPatMerker.get(4));
        jcheck[6].setOpaque(false);
        jcheck[6].setName("merk5");
        jcheck[6].setToolTipText(SystemConfig.vPatMerker.get(4));

        jcheck[7] = new JRtaCheckBox(SystemConfig.vPatMerker.get(5));
        jcheck[7].setOpaque(false);
        jcheck[7].setName("merk6");
        jcheck[7].setToolTipText(SystemConfig.vPatMerker.get(5));

        // die Labeltexte merk2 bis merk7 aus Datenbank/SysINI einlesen?
        docmod = new MyDocTableModel();
        docmod.setColumnIdentifiers(new String[] { "LANR", "Nachname", "Strasse", "Ort", "BSNR", "" });
        doclist = new JXTable(docmod);
        doclist.getColumn(5)
               .setMinWidth(0);
        doclist.getColumn(5)
               .setMaxWidth(0);
        doclist.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {

                if (arg0.getClickCount() == 2 && arg0.getButton() == 1) {
                    final Point pt = arg0.getLocationOnScreen();
                    new SwingWorker<Void, Void>() {
                        @Override
                        protected Void doInBackground() throws Exception {
                            try {
                                arztInListeDoppelKlick(pt);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                            return null;
                        }

                    }.execute();
                }
            }

        });
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                doArztListe();
                return null;
            }
        }.execute();
        JScrollPane docscr = JCompTools.getTransparentScrollPane(doclist);
        docscr.validate();

        delDocButton = new JButton("entfernen");
        delDocButton.setPreferredSize(new Dimension(70, 20));
        delDocButton.addActionListener(this);
        delDocButton.setName("entfernen");
        delDocButton.setActionCommand("deldoc");
        delDocButton.addKeyListener(this);
        addDocButton = new JButton("hinzu");
        addDocButton.setPreferredSize(new Dimension(70, 20));
        addDocButton.addActionListener(this);
        addDocButton.setName("hinzu");
        addDocButton.setActionCommand("adddoc");
        addDocButton.addKeyListener(this);

        FormLayout lay22 = new FormLayout("right:max(80dlu;p), 4dlu, 60dlu,right:max(60dlu;p), 4dlu, 60dlu",
                // 1. 2. 3. 4. 5. 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22
                // 23 24 25 26 27 28 29 30 31 32 33 34 35 36 37 38 39 40 41 42
                // 43 44 45 46 47 48 49 50 51 52 53 54 55 56 57 58 59 60 61 62
                // 63 64 65 66 67
                "p, 10dlu, p, 2dlu, p, 2dlu, p, 42dlu, p, 11dlu, 90dlu, 5dlu, p");

        PanelBuilder builder22 = new PanelBuilder(lay22);
        builder22.setDefaultDialogBorder();
        builder22.getPanel()
                 .setOpaque(false);
        CellConstraints cc22 = new CellConstraints();
        builder22.getPanel()
                 .setDoubleBuffered(true);

        builder22.addSeparator("individuelle Merkmale", cc22.xyw(1, 1, 6));

        builder22.add(jcheck[2], cc22.xy(3, 3));
        builder22.add(jcheck[3], cc22.xy(6, 3));
        builder22.add(jcheck[4], cc22.xy(3, 5));
        builder22.add(jcheck[5], cc22.xy(6, 5));
        builder22.add(jcheck[6], cc22.xy(3, 7));
        builder22.add(jcheck[7], cc22.xy(6, 7));

        builder22.addSeparator("Ärzteliste des Patienten", cc22.xyw(1, 9, 6));
        builder22.add(docscr, cc22.xyw(1, 11, 6));
        builder22.addLabel("Arzt aufnehmen", cc22.xy(1, 13));
        builder22.add(addDocButton, cc22.xy(3, 13));
        builder22.addLabel("Arzt entfernen", cc22.xy(4, 13));
        builder22.add(delDocButton, cc22.xy(6, 13));

        JScrollPane jscrzusatz = JCompTools.getTransparentScrollPane(builder22.getPanel());
        jscrzusatz.getVerticalScrollBar()
                  .setUnitIncrement(15);
        jscrzusatz.getViewport()
                  .setOpaque(false);
        jscrzusatz.setBorder(null);
        jscrzusatz.setViewportBorder(null);
        jscrzusatz.validate();
        jscrzusatz.addKeyListener(this);
        pat22.add(jscrzusatz, BorderLayout.CENTER);
        pat22.validate();
        return pat22;
    }

    public void setNewPic(ImageIcon img) {
        lblbild.setText("");
        lblbild.setIcon(img);
        updateBild = true;
    }

    private JXPanel getDatenPanel31() {
        JXPanel pat31 = new JXPanel(new BorderLayout());
        // but.setBorder(null);
        pat31.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        pat31.setOpaque(false);
        pat31.setDoubleBuffered(true);

        picVerwerfenButton = new JButton("verwerfen");
        picVerwerfenButton.setPreferredSize(new Dimension(70, 20));
        picVerwerfenButton.addActionListener(this);
        picVerwerfenButton.setActionCommand("delpic");
        picVerwerfenButton.addKeyListener(this);
        picAufnehmenButton = new JButton("Aufnahme");
        picAufnehmenButton.setPreferredSize(new Dimension(70, 20));
        picAufnehmenButton.addActionListener(this);
        picAufnehmenButton.setActionCommand("addpic");
        picAufnehmenButton.addKeyListener(this);
        picBildDateiButton = new JButton("Bilddatei");
        picBildDateiButton.setPreferredSize(new Dimension(70, 20));
        picBildDateiButton.addActionListener(this);
        picBildDateiButton.setActionCommand("addjpg");
        picBildDateiButton.addKeyListener(this);

        FormLayout lay31 = new FormLayout("right:max(80dlu;p), 4dlu, 175px,right:max(60dlu;p), 4dlu, 60dlu",
                // 1. 2. 3. 4. 5. 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22
                // 23 24 25 26 27 28 29 30 31 32 33 34 35 36 37 38 39 40 41 42
                // 43 44 45 46 47 48 49 50 51 52 53 54 55 56 57 58 59 60 61 62
                // 63 64 65 66 67
                "p, 10dlu, 220px, 2dlu, p");

        PanelBuilder builder31 = new PanelBuilder(lay31);
        builder31.setDefaultDialogBorder();
        builder31.getPanel()
                 .setOpaque(false);
        CellConstraints cc31 = new CellConstraints();
        builder31.getPanel()
                 .setDoubleBuffered(true);

        builder31.addSeparator("Patientenfoto", cc31.xyw(1, 1, 6));
        lblbild = new JLabel();
        builder31.add(lblbild, cc31.xy(3, 3));
        builder31.addLabel("Bild aufnehmen", cc31.xy(1, 5));
        /****/
        JXPanel pan = new JXPanel();
        pan.setOpaque(false);
        FormLayout lay2 = new FormLayout("fill:0:grow(0.5),2px,fill:0:grow(0.5)", "p");
        pan.setLayout(lay2);
        CellConstraints cc2 = new CellConstraints();
        pan.add(picAufnehmenButton, cc2.xy(1, 1, CellConstraints.FILL, CellConstraints.DEFAULT));
        pan.add(picBildDateiButton, cc2.xy(3, 1, CellConstraints.FILL, CellConstraints.DEFAULT));
        builder31.add(pan, cc31.xy(3, 5));
        builder31.addLabel("Bild verwerfen", cc31.xy(4, 5));
        builder31.add(picVerwerfenButton, cc31.xy(6, 5));
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    if (!inNeu) {
                        BufferedImage img = holePatBild(Reha.instance.patpanel.aktPatID);
                        if (img == null) {
                            lblbild.setText("Kein Bild des Patienten vorhanden");
                        } else {
                            lblbild.setText("");
                            lblbild.setIcon(new ImageIcon(img));
                            startMitBild = true;
                        }
                    } else {
                        lblbild.setText("Kein Bild des Patienten vorhanden");
                        lblbild.setIcon(null);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return null;
            }
        }.execute();

        JScrollPane jscrzusatz = JCompTools.getTransparentScrollPane(builder31.getPanel());
        jscrzusatz.getVerticalScrollBar()
                  .setUnitIncrement(15);
        jscrzusatz.getViewport()
                  .setOpaque(false);
        jscrzusatz.setBorder(null);
        jscrzusatz.setViewportBorder(null);
        jscrzusatz.validate();
        jscrzusatz.addKeyListener(this);
        pat31.add(jscrzusatz, BorderLayout.CENTER);
        pat31.validate();
        return pat31;
    }

    private JXPanel getDatenPanel32() {
        JXPanel pat32 = new JXPanel(new BorderLayout());
        pat32.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        pat32.setOpaque(false);
        pat32.setDoubleBuffered(true);

        FormLayout lay32 = new FormLayout("right:max(80dlu;p), 4dlu, 60dlu,right:max(60dlu;p), 4dlu, 60dlu",
                // 1. 2. 3. 4. 5. 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22
                // 23 24 25 26 27 28 29 30 31 32 33 34 35 36 37 38 39 40 41 42
                // 43 44 45 46 47 48 49 50 51 52 53 54 55 56 57 58 59 60 61 62
                // 63 64 65 66 67
                "p, 10dlu, 160dlu, 2dlu, p");

        PanelBuilder builder32 = new PanelBuilder(lay32);
        builder32.setDefaultDialogBorder();
        builder32.getPanel()
                 .setOpaque(false);
        CellConstraints cc32 = new CellConstraints();
        builder32.getPanel()
                 .setDoubleBuffered(true);

        builder32.addLabel("Space for future Extensions", cc32.xyw(1, 3, 6));

        JScrollPane jscrzusatz = JCompTools.getTransparentScrollPane(builder32.getPanel());
        jscrzusatz.getVerticalScrollBar()
                  .setUnitIncrement(15);
        jscrzusatz.getViewport()
                  .setOpaque(false);
        jscrzusatz.setBorder(null);
        jscrzusatz.setViewportBorder(null);
        jscrzusatz.validate();
        jscrzusatz.addKeyListener(this);
        pat32.add(jscrzusatz, BorderLayout.CENTER);
        pat32.validate();
        return pat32;
    }

    private JXPanel getDatenPanel11() {
        JXPanel pat11 = new JXPanel(new BorderLayout());
        pat11.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        pat11.setOpaque(false);
        pat11.setDoubleBuffered(true);

        cbanrede = new JRtaComboBox(new String[] { "HERR", "FRAU" });
        cbanrede.addKeyListener(this);
        jtf[0] = new JRtaTextField("GROSS", true);
        jtf[0].setName("anrede");

        jtf[1] = new JRtaTextField("GROSS", true);
        jtf[1].setName("titel");

        jtf[2] = new JRtaTextField("GROSS", true);
        jtf[2].setName("n_name");
        jtf[2].setFont(font);
        jtf[2].setForeground(Color.RED);

        jtf[3] = new JRtaTextField("GROSS", true);
        jtf[3].setName("v_name");
        jtf[3].setFont(font);
        jtf[3].setForeground(Color.RED);

        jtf[4] = new JRtaTextField("GROSS", true);
        jtf[4].setName("strasse");

        jtf[5] = new JRtaTextField("ZAHLEN", true);
        jtf[5].setName("plz");

        jtf[6] = new JRtaTextField("GROSS", true);
        jtf[6].setName("ort");

        jtf[7] = new JRtaTextField("GROSS", true);
        jtf[7].setName("telefonp");

        jtf[8] = new JRtaTextField("GROSS", true);
        jtf[8].setName("telefong");

        jtf[9] = new JRtaTextField("GROSS", true);
        jtf[9].setName("telefonm");

        jtf[10] = new JRtaTextField("", true);
        jtf[10].setName("emaila");

        jtf[TEXTFELD_INDEX_FUER_GEBURTSTAG] = new JRtaTextField("DATUM", true);
        jtf[TEXTFELD_INDEX_FUER_GEBURTSTAG].setName("geboren");
        jtf[TEXTFELD_INDEX_FUER_GEBURTSTAG].setFont(font);
        jtf[TEXTFELD_INDEX_FUER_GEBURTSTAG].setForeground(Color.RED);

        jtf[TEXTFELD_INDEX_FUER_GEBURTSTAG].addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                JRtaTextField textField = (JRtaTextField) e.getSource();
                String inhalt = textField.getText();
                if (inhalt.replace(".", "")
                          .isEmpty()) {

                } else {
                    if (!inhalt.equals(textField.getValue())) {

                        LocalDate birthday = LocalDate.parse(inhalt, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                        long age = ChronoUnit.YEARS.between(birthday, LocalDate.now());
                        if (age > 100) {
                            JOptionPane.showMessageDialog(null, age + "! Das ist wirklich alt");
                        } else if (LocalDate.now()
                                            .isBefore(birthday)) {
                            JOptionPane.showMessageDialog(null, "Wird also geboren werden!");
                        }
                    }
                }
            }
        });

        jtf[27] = new JRtaTextField("DATUM", true);
        jtf[27].setName("akutbis");

        jtf[28] = new JRtaTextField("DATUM", true);
        jtf[28].setName("akutdat");

        jtf[29] = new JRtaTextField("GROSS", true);
        jtf[29].setName("termine1");

        jtf[30] = new JRtaTextField("GROSS", true);
        jtf[30].setName("termine2");

        jcheck[1] = new JRtaCheckBox();
        jcheck[1].setOpaque(false);
        jcheck[1].setName("akutpat");

        // 1. 2. 3. 4. 5. 6.
        FormLayout lay11 = new FormLayout("right:max(80dlu;p), 4dlu, 60dlu,right:max(60dlu;p), 4dlu, 60dlu",
                // 1. 2. 3. 4. 5. 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22
                // 23 24 25 26 27 28 29 30 31 32 33 34 35 36 37 38 39 40 41 42
                // 43 44 45 46 47 48 49 50 51 52 53 54 55 56 57 58 59 60 61 62
                // 63 64 65 66 67
                "p, 10dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 10dlu, p, 10dlu, p, 2dlu, p, 2dlu, p, 2dlu, p");
        PanelBuilder builder = new PanelBuilder(lay11);
        builder.setDefaultDialogBorder();
        builder.getPanel()
               .setOpaque(false);
        CellConstraints cc11 = new CellConstraints();
        builder.getPanel()
               .setDoubleBuffered(true);

        builder.addSeparator("Personendaten", cc11.xyw(1, 1, 6));
        builder.addLabel("Anrede", cc11.xy(1, 3));
        builder.add(cbanrede, cc11.xy(3, 3));
        // builder.add(jtf[0], cc11.xy(3,1));
        builder.addLabel("Titel", cc11.xy(4, 3));
        builder.add(jtf[1], cc11.xy(6, 3));
        builder.addLabel("Nachname *)", cc11.xy(1, 5));
        builder.add(jtf[2], cc11.xyw(3, 5, 4));
        builder.addLabel("Vorname *)", cc11.xy(1, 7));
        builder.add(jtf[3], cc11.xyw(3, 7, 4));
        builder.addLabel("Strasse, Nr. *)", cc11.xy(1, 9));
        builder.add(jtf[4], cc11.xyw(3, 9, 4));
        builder.addLabel("PLZ, Ort *)", cc11.xy(1, 11));
        builder.add(jtf[5], cc11.xy(3, 11));
        builder.add(jtf[6], cc11.xyw(4, 11, 3));
        builder.addLabel("Geburtstag *)", cc11.xy(1, 13));
        builder.add(jtf[TEXTFELD_INDEX_FUER_GEBURTSTAG], cc11.xy(3, 13));
        builder.addLabel("Telefon priv.", cc11.xy(1, 15));
        builder.add(jtf[7], cc11.xyw(3, 15, 4));
        builder.addLabel("Telefon gesch.", cc11.xy(1, 17));
        builder.add(jtf[8], cc11.xyw(3, 17, 4));
        builder.addLabel("Mobil", cc11.xy(1, 19));
        builder.add(jtf[9], cc11.xyw(3, 19, 4));
        builder.addLabel("Email", cc11.xy(1, 21));
        builder.add(jtf[10], cc11.xyw(3, 21, 4));

        builder.addSeparator("Plandaten", cc11.xyw(1, 23, 6));

        builder.addLabel("Akutpatient", cc11.xy(1, 25));
        builder.add(jcheck[1], cc11.xy(3, 25));

        builder.addLabel("akut von", cc11.xy(1, 27));
        builder.add(jtf[28], cc11.xy(3, 27));
        builder.addLabel("akut bis", cc11.xy(4, 27));
        builder.add(jtf[27], cc11.xy(6, 27));

        builder.addLabel("mögliche Termine 1", cc11.xy(1, 29));
        builder.add(jtf[29], cc11.xyw(3, 29, 4));
        builder.addLabel("mögliche Termine 2", cc11.xy(1, 31));
        builder.add(jtf[30], cc11.xyw(3, 31, 4));

        builder.getPanel()
               .addKeyListener(this);
        builder.getPanel()
               .addFocusListener(this);
        builder.getPanel()
               .validate();
        JScrollPane xjscr = JCompTools.getTransparentScrollPane(builder.getPanel());
        xjscr.getVerticalScrollBar()
             .setUnitIncrement(15);

        xjscr.validate();
        xjscr.addKeyListener(this);

        pat11.add(xjscr, BorderLayout.CENTER);
        pat11.validate();
        return pat11;
    }

    public static String getArztBisher() {
        return arztbisher;
    }

    public static String getKasseBisher() {
        return kassebisher;
    }

    private void doArztListe() {
        if (this.inNeu) {
            return;
        }
        String aerzte = Reha.instance.patpanel.patDaten.get(63);
        String[] einzelarzt = null;
        String[] arztdaten = null;
        Vector<?> arztvec = null;
        if (!aerzte.trim()
                   .equals("")) {
            einzelarzt = aerzte.split("\n");
            for (int i = 0; i < einzelarzt.length; i++) {
                arztdaten = einzelarzt[i].split("@");
                arztvec = SqlInfo.holeFelder("select arztnum,nachname,strasse,ort,bsnr,id  from arzt where id='"
                        + arztdaten[1] + "' LIMIT 1");
                if (arztvec.size() > 0) {
                    docmod.addRow((Vector<?>) arztvec.get(0));
                }
            }
            if (docmod.getRowCount() > 0) {
                doclist.setRowSelectionInterval(0, 0);
            }
        }
    }

    public void enableReaderButton() {
        if (SystemConfig.sReaderAktiv.equals("0")) {
            return;
        }
        chipButton.setEnabled(true);
        einlesen();
    }

    public void disableReaderButton() {
        if (SystemConfig.sReaderAktiv.equals("0")) {
            return;
        }
        chipButton.setEnabled(false);
    }

    private void einlesen() {

        if (SystemConfig.sReaderAktiv.equals("0")) {
            return;
        } else {
            CardTerminal terminal = TerminalFactory.getDefault()
                                                   .terminals()
                                                   .getTerminal(SystemConfig.sReaderName);
            try {
                Reha.instance.ocKVK.lesen(terminal);
            } catch (UnsatisfiedLinkError | Exception e) {
                logger.error("Probably library not found, shouldn't happen at all anymore", e);
            }
        }

        if (SystemConfig.hmKVKDaten.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Daten der Chipkarte konnten nicht gelesen werden");
            return;
        }
        System.out.println("Aufruf der KVK");
        if (!SystemConfig.hmKVKDaten.isEmpty()) {
            KVKRohDaten kvkr = new KVKRohDaten(this);
            kvkr.setModal(true);
            kvkr.setLocationRelativeTo(this);
            kvkr.setVisible(true);
            setzeFocus();
            kvkr = null;
        } else {
            String fehlertext = "Fehler beim einlesen der Versichertenkarte.\nBitte erneut einlesen\n\n";
            JOptionPane.showMessageDialog(null, fehlertext);
        }
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {

        String com = arg0.getActionCommand();
        switch (com) {
        case "einlesen":
            if (SystemConfig.sReaderAktiv.equals("0")) {
                return;
            }

            einlesen();

            break;
        case "speichern":
            schreibeInDb();
            break;
        case "abbrechen":
            finalise();
            ((JXDialog) this.getParent()
                            .getParent()
                            .getParent()
                            .getParent()
                            .getParent()).dispose();
            break;
        case "adddoc":
            arztInListeAuswahl();
            break;
        case "deldoc":
            int row = -1;
            if ((row = doclist.getSelectedRow()) < 0) {
                return;
            } else {
                TableTool.loescheRow(doclist, row);
            }
            break;
        case "u18ignore":
            break;
        case "vorjahrfrei":
            if (jcheck[10].isSelected()) {
                jtf[35].setText("");
            } else {
                jtf[35].setText(SystemConfig.vorJahr);
            }
            break;
        case "delpic":
            if (lblbild.getIcon() != null && !inNeu) {
                int frage = JOptionPane.showConfirmDialog(null, "Wollen Sie das Patientenfoto wirklich löschen?",
                        "Wichtige Benutzeranfrage", JOptionPane.YES_NO_OPTION);
                if (frage == JOptionPane.YES_OPTION) {
                    SqlInfo.sqlAusfuehren(
                            "delete from patbild where pat_intern = '" + Reha.instance.patpanel.aktPatID + "' LIMIT 1");
                } else {
                    return;
                }
            }
            lblbild.setIcon(null);
            lblbild.setText("Kein Bild des Patienten vorhanden");
            startMitBild = false;
            updateBild = false;
            return;
        case "addpic":
            if (SystemConfig.sWebCamActive.equals("0")) {
                JOptionPane.showMessageDialog(null,
                        "WebCam entweder nicht aktiviert (System-Initialisierung)\noder nicht angeschlossen!");
                return;
            }
            PatientenFoto foto = new PatientenFoto(null, "patBild", this);
            foto.setModal(true);
            foto.setLocationRelativeTo(null);
            foto.pack();
            foto.setVisible(true);
            foto = null;
            return;
        case "addjpg":
            bildLaden();
            break;
        case "route":
            calcKilometer();
            break;
        }

    }

    private void bildLaden() {

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("*.jpg", "jpg"));
        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                BufferedImage result = ImageIO.read(new File(file.getAbsolutePath()));

                ImageIcon img = new ImageIcon(result);
                img = new ImageIcon(img.getImage()
                                       .getScaledInstance(175, 220, Image.SCALE_SMOOTH));
                setNewPic(img);
            } catch (IOException ex) {
                System.out.println("Failed to save image!");
            }
        } else {
            System.out.println("No file choosen!");
        }
    }

    @Override
    public void keyPressed(KeyEvent arg0) {

        if (arg0.getKeyCode() == KeyEvent.VK_ENTER || arg0.getKeyCode() == KeyEvent.VK_UNDEFINED) {
            arg0.consume();
            try {
                if (((JComponent) arg0.getSource()).getName()
                                                   .equals("einlesen")) {
                    einlesen();
                    return;
                }
                if (((JComponent) arg0.getSource()).getName()
                                                   .equals("speichern")) {
                    schreibeInDb();
                    return;
                }
                if (((JComponent) arg0.getSource()).getName()
                                                   .equals("abbrechen")) {

                    ((JXDialog) this.getParent()
                                    .getParent()
                                    .getParent()
                                    .getParent()
                                    .getParent()).setVisible(false);
                    finalise();
                    ((JXDialog) this.getParent()
                                    .getParent()
                                    .getParent()
                                    .getParent()
                                    .getParent()).dispose();
                    return;
                }
            } catch (Exception ex) {

            }

        }
        if (arg0.getKeyCode() == KeyEvent.VK_ESCAPE) {

            ((JXDialog) this.getParent()
                            .getParent()
                            .getParent()
                            .getParent()
                            .getParent()).setVisible(false);
            finalise();
            ((JXDialog) this.getParent()
                            .getParent()
                            .getParent()
                            .getParent()
                            .getParent()).dispose();
        }

    }

    @Override
    public void keyReleased(KeyEvent arg0) {

        if (arg0.getKeyCode() == KeyEvent.VK_ENTER || arg0.getKeyCode() == KeyEvent.VK_UNDEFINED)
            arg0.consume();
    }

    @Override
    public void keyTyped(KeyEvent arg0) {

        if (arg0.getKeyCode() == KeyEvent.VK_ENTER || arg0.getKeyCode() == KeyEvent.VK_UNDEFINED)
            arg0.consume();
    }

    @Override
    public void focusGained(FocusEvent arg0) {

        if (((JComponent) arg0.getSource()).getName() != null) {
            if (((JComponent) arg0.getSource()).getName()
                                               .equals("arzt")) {
                if (testObDialog(jtf[17].getText())) {
                    String[] suchenach = null;
                    if (jtf[17].getText()
                               .trim()
                               .length() > 1) {
                        suchenach = new String[] { jtf[17].getText()
                                                          .trim()
                                                          .substring(1),
                                jtf[33].getText()
                                       .trim() };
                    } else {
                        suchenach = new String[] { "", "" };
                    }
                    arztAuswahl(suchenach);
                }
            } else if (((JComponent) arg0.getSource()).getName()
                                                      .equals("kasse")) {
                if (testObDialog(jtf[12].getText())) {
                    String[] suchenach = null;
                    if (jtf[12].getText()
                               .trim()
                               .length() > 1) {
                        suchenach = new String[] { jtf[12].getText()
                                                          .trim()
                                                          .substring(1),
                                jtf[34].getText()
                                       .trim() };
                    } else {
                        suchenach = new String[] { "", "" };
                    }
                    kassenAuswahl(suchenach);
                }
            }
        }
    }

    @Override
    public void focusLost(FocusEvent arg0) {

    }

    private void arztAuswahl(String[] suchenach) {
        jtf[19].requestFocus();
        ArztAuswahl awahl = new ArztAuswahl(null, "ArztAuswahl", suchenach,
                new JRtaTextField[] { jtf[17], jtf[18], jtf[33] }, jtf[17].getText()
                                                                          .trim());
        awahl.setModal(true);
        awahl.setLocationRelativeTo(this);
        awahl.setVisible(true);
        awahl.dispose();
        awahl = null;
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    aerzteOrganisieren(jtf[33].getText(), inNeu, docmod, doclist, true);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return null;
            }

        }.execute();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                jtf[19].requestFocus();
            }
        });
        if (jtf[17].getText()
                   .indexOf("?") >= 0) {
            String text = jtf[17].getText()
                                 .replace("?", "");
            jtf[17].setText(text);
        }

    }

    private void kassenAuswahl(String[] suchenach) {
        jtf[14].requestFocus();
        KassenAuswahl kwahl = new KassenAuswahl(null, "KassenAuswahl", suchenach,
                new JRtaTextField[] { jtf[12], jtf[13], jtf[34] }, jtf[12].getText()
                                                                          .trim());
        kwahl.setModal(true);
        kwahl.setLocationRelativeTo(this);
        kwahl.setVisible(true);
        kwahl.dispose();
        kwahl = null;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                jtf[14].requestFocus();
            }
        });
        if (jtf[12].getText()
                   .indexOf("?") >= 0) {
            String text = jtf[12].getText()
                                 .replace("?", "");
            jtf[12].setText(text);
        }
    }

    private void arztInListeAuswahl() {
        JRtaTextField[] tfaliste = { new JRtaTextField("nix", false), new JRtaTextField("nix", false),
                new JRtaTextField("nix", false) };
        ArztAuswahl awahl = new ArztAuswahl(null, "ArztAuswahl", new String[] { "", "" }, tfaliste, "");
        awahl.setModal(true);
        awahl.setLocationRelativeTo(this);
        awahl.setVisible(true);
        awahl.dispose();
        awahl = null;
        final JRtaTextField xtf = tfaliste[2];
        if (!xtf.getText()
                .equals("")) {
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    try {
                        aerzteOrganisieren(xtf.getText(), inNeu, docmod, doclist, false);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    return null;
                }

            }.execute();
        }
    }

    private void arztInListeDoppelKlick(Point klick) {
        int row = doclist.getSelectedRow();
        if (row >= 0) {
            formulareAuswerten(klick);
        }

    }

    public void holeFormulare() {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    Settings inif = INITool.openIni(
                            Path.Instance.getProghome() + "ini/" + Betriebsumfeld.getAktIK() + "/", "arzt.ini");
                    int forms = inif.getIntegerProperty("Formulare", "ArztFormulareAnzahl");
                    for (int i = 1; i <= forms; i++) {
                        titel.add(inif.getStringProperty("Formulare", "AFormularText" + i));
                        formular.add(inif.getStringProperty("Formulare", "AFormularName" + i));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return null;
            }

        }.execute();

    }

    public void formulareAuswerten(Point klick) {
        int row = doclist.getSelectedRow();
        if (row >= 0) {
            String sid = Integer.toString(Integer.parseInt((String) doclist.getValueAt(row, 5)));
            iformular = -1;
            KassenFormulare kf = new KassenFormulare(null, titel, formularid);
            Point pt = klick;
            kf.setLocation(pt.x - 100, pt.y + 25);
            kf.setModal(true);
            kf.setVisible(true);
            iformular = Integer.valueOf(formularid.getText());
            kf = null;
            final String xid = sid;
            if (iformular >= 0) {
                new SwingWorker<Void, Void>() {

                    @Override
                    protected Void doInBackground() throws Exception {
                        ArztTools.constructArztHMap(xid);
                        RehaOOTools.starteStandardFormular(Path.Instance.getProghome() + "vorlagen/"
                                + Betriebsumfeld.getAktIK() + "/" + formular.get(iformular), null, Reha.instance);
                        return null;
                    }
                }.execute();

            }

        } else {
            String mes = "Wenn man eine Kasse anschreiben möchte, empfiehlt es sich\n"
                    + "vorher die Kasse auszuwählen die man anschreiben möchte!!!\n\n"
                    + "Aber trösten Sie sich, unser Herrgott hat ein Herz für eine ganz spezielle Randgruppe.\n"
                    + "Sie dürfen also hoffen....\n\n";
            JOptionPane.showMessageDialog(null, mes);
            iformular = -1;
        }
    }

    public void aerzteOrganisieren(String aid, boolean neu, MyDocTableModel mod, JXTable tbl, boolean bloednachfragen) {
        // hier weitermachen
        // wenn neu dann Tabelle mit nur einem arzt
        // *******************/
        // wenn nicht neu und nicht in bisherigem aerzt.feld
        // nachfragen ob neu aufgenommen werden soll
        // wenn ja aufnehmen und in dbAbspeichern
        // wenn nicht zur�ck
        if (mod != null) {
            if (neu) {
                if (!inTableEnthalten(aid, mod)) {
                    // System.out.println("Neuanlage Pat. Arzt wird in Liste übernommen");
                    mod.setRowCount(0);
                    arztInTableAufnehmen(aid, mod);
                    tbl.validate();
                } else {
                    // System.out.println("Neuanlage Pat. Arzt bereits in der Liste enthalten");
                }
            } else { // in Patient �ndern
                if (!inTableEnthalten(aid, mod)) {
                    // System.out.println("Ändern Pat. Arzt wird in Liste übernommen");
                    if (bloednachfragen) {
                        int frage = JOptionPane.showConfirmDialog(null,
                                "Den gewählten Arzt in die Arztliste dieses Patienten aufnehmen?",
                                "Wichtige Benutzeranfrage", JOptionPane.YES_NO_OPTION);
                        if (frage == JOptionPane.YES_OPTION) {
                            arztInTableAufnehmen(aid, mod);
                            tbl.validate();
                        }
                    } else {
                        arztInTableAufnehmen(aid, mod);
                        tbl.validate();
                    }
                } else {
                    // System.out.println("Ändern Pat. Arzt bereits in der Liste enthalten");
                }
            }
        }
    }

    private void arztInTableAufnehmen(String aid, MyDocTableModel mod) {
        Vector<Vector<String>> vecx;
        if (mod != null) {
            vecx = SqlInfo.holeFelder(
                    "select arztnum,nachname,strasse,ort,bsnr,id  from arzt where id='" + aid + "' LIMIT 1");
            if (vecx.size() > 0) {
                mod.addRow(vecx.get(0));
            }
        } else {

        }
    }

    private boolean inTableEnthalten(String aid, MyDocTableModel mod) {
        boolean bret = false;
        for (int i = 0; i < mod.getRowCount(); i++) {
            if (((String) mod.getValueAt(i, 5)).equals(aid)) {
                bret = true;
                return bret;
            }
        }
        return bret;
    }

    private boolean testObDialog(String string) {
        if (string == null) {
            return false;
        }
        if (string.trim()
                  .length() == 0) {
            return false;
        }
        if ("?".equals(string.substring(0, 1))) {
            return true;
        }
        return false;
    }

    private void speichernPatBild(boolean neu, ImageIcon ico, String pat_intern) {

        String sqlString;
        if (neu) {
            sqlString = "Insert into patbild set bild = ? , vorschau = ? , pat_intern = ?";
        } else {
            sqlString = "Update patbild set bild = ? , vorschau = ?  where pat_intern = ?";
        }

        Image vorschau = ico.getImage()
                            .getScaledInstance(35, 44, Image.SCALE_SMOOTH);

        try (PreparedStatement ps = (PreparedStatement) Reha.instance.conn.prepareStatement(sqlString);) {

            ps.setBytes(1, JpegWriter.bufferedImageToByteArray(iconToBufferedImage(ico)));

            ps.setBytes(2, JpegWriter.bufferedImageToByteArray(imageToBufferedImage(vorschau)));
            ps.setString(3, pat_intern);
            ps.execute();
        } catch (SQLException | IOException e) {
            logger.error("Fehler beim Speichern des Patientenbilds", e);
        }
    }

    public static BufferedImage imageToBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }

    private BufferedImage iconToBufferedImage(ImageIcon icon) {
        BufferedImage bi = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics g = bi.createGraphics();
        icon.paintIcon(null, g, 0, 0);
        g.dispose();
        return bi;
    }

    private byte[] bufferedImageToByteArray(BufferedImage originalImage) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(originalImage, "jpg", baos);
            baos.flush();
            byte[] imageInByte = baos.toByteArray();
            baos.close();
            return imageInByte;
        } catch (IOException e) {
            logger.error("Cannot convert Image ", e);
            return new byte[0];
        }

    }

    public static BufferedImage holePatBild(String pat_intern) {
        String test = "select bild from patbild where pat_intern ='" + pat_intern + "'";
        Image bild = null;
        try (Statement stmt = Reha.instance.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_UPDATABLE); ResultSet rs = stmt.executeQuery(test)) {
            if (rs.next()) {
                bild = ImageIO.read(new ByteArrayInputStream(rs.getBytes("bild")));
            }
        } catch (SQLException | IOException e) {
            logger.error("Fehler beim Bezug des Bildes fuer pat_intern= " + pat_intern, e);
        }
        return (BufferedImage) bild;
    }

    class MyDocTableModel extends DefaultTableModel {
        /**
        *
        */
        private static final long serialVersionUID = 1L;

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 1) {
                return String.class;
            } else {
                return String.class;
            }
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            return false;
        }
    }

    public void aufraeumen() {
        for (int i = 0; i < jtf.length; i++) {
            jtf[i].listenerLoeschen();
            ListenerTools.removeListeners(jtf[i]);
        }
        for (int i = 0; i < jcheck.length; i++) {
            ListenerTools.removeListeners(jcheck[i]);
        }
        xfelder.clear();
        checks.clear();
        xfelder = null;
        checks = null;
        rtp.removeRehaTPEventListener(this);
        rtp = null;
    }

    private void calcKilometer() {

        if (patientenAdresseUnvollständig()) {
            JOptionPane.showMessageDialog(this, "Patientenadresse oder Firmenadresse sind unvollständig");
            return;
        }

        String patientAdr = jtf[4].getText() + ", " + jtf[5].getText() + " " + jtf[6].getText();

        String mandAdr = SystemConfig.hmFirmenDaten.get("Strasse") + ", " + SystemConfig.hmFirmenDaten.get("Plz") + " "
                + SystemConfig.hmFirmenDaten.get("Ort");

        try {

            CalcKilometer scout = new CalcKilometer();
            Distance distanz = scout.distanzZwischen(mandAdr, patientAdr);

            long kmGesamt = Math.round(distanz.getKilometer() * 2);
            int minuten = distanz.getDurationInMinutes() * 2;
            int copy = distanz.getMeter();

            Object[] options = { "Übernehmen", "Nö!" };
            int answer = JOptionPane.showOptionDialog(null,
                    "<html>openMaps hat für Hin- und Rückweg (gerundet) <b><u>" + kmGesamt
                            + " km</u></b> errechnet.<br>Fahrzeit: " + minuten + " min <br>" + copy + "</html>",
                    "openMaps sagt", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options,
                    options[0]);
            if (answer == JOptionPane.YES_OPTION) {
                jtf[31].setText(String.valueOf(kmGesamt));
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Fehler beim Bezug der Routenangaben");
        }

    }

    private boolean patientenAdresseUnvollständig() {
        return jtf[4].getText()
                     .trim()
                     .equals("")
                || jtf[5].getText()
                         .trim()
                         .equals("")
                || jtf[6].getText()
                         .trim()
                         .equals("")
                || SystemConfig.hmFirmenDaten.get("Strasse")
                                             .equals("")
                || SystemConfig.hmFirmenDaten.get("Plz")
                                             .equals("")
                || SystemConfig.hmFirmenDaten.get("Ort")
                                             .equals("");
    }

    @Override
    public void rehaTPEventOccurred(RehaTPEvent evt) {
        try {
            if (evt.getDetails()[0] != null) {
                if (evt.getDetails()[0].equals(this.getName())) {
                    ((JXDialog) this.getParent()
                                    .getParent()
                                    .getParent()
                                    .getParent()
                                    .getParent()).setVisible(false);
                    finalise();
                    ((JXDialog) this.getParent()
                                    .getParent()
                                    .getParent()
                                    .getParent()
                                    .getParent()).dispose();
                    // //System.out.println("****************Patient Neu/ändern -> Listener
                    // entfernt**************");
                }
            }
        } catch (NullPointerException ne) {
            // System.out.println("In PatNeuanlage" +evt);
        }

    }

}

class ArztListeSpeichern {
    public ArztListeSpeichern(Vector<Vector<String>> vec, boolean neu, String xpatintern) {
        if (vec.size() <= 0) {
            return;
        }
        String cmd = "update pat5 set aerzte = '";
        String aliste = "";
        for (int i = 0; i < vec.size(); i++) {
            aliste = aliste + "@" + (vec.get(i)
                                        .get(5))
                    + "@\n";
        }
        SqlInfo.aktualisiereSaetze("pat5", "aerzte='" + aliste + "'", "pat_intern='" + xpatintern + "'");
        new ExUndHop().setzeStatement(cmd);
        Reha.instance.patpanel.patDaten.set(63, aliste);
    }
}
