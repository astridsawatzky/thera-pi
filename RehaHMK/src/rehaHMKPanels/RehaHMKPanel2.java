package rehaHMKPanels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
//import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.painter.Painter;
import org.slf4j.LoggerFactory;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
//import com.sun.image.codec.jpeg.ImageFormatException;
//import com.sun.image.codec.jpeg.JPEGCodec;
//import com.sun.image.codec.jpeg.JPEGEncodeParam;
//import com.sun.image.codec.jpeg.JPEGImageEncoder;
//import java.awt.image.*;
//import java.io.*;
import javax.imageio.*;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;

import com.sun.imageio.plugins.jpeg.JPEGImageWriter;
import com.sun.star.beans.XPropertySet;
import com.sun.star.container.XNameContainer;
import com.sun.star.lang.XMultiServiceFactory;
import com.sun.star.text.TextContentAnchorType;
import com.sun.star.text.XText;
import com.sun.star.text.XTextContent;
import com.sun.star.text.XTextCursor;
import com.sun.star.text.XTextRange;
import com.sun.star.uno.AnyConverter;
import com.sun.star.uno.RuntimeException;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.util.XRefreshable;

import CommonTools.ButtonTools;
import CommonTools.DatFunk;
import CommonTools.JCompTools;
import CommonTools.JRtaCheckBox;
import CommonTools.JRtaComboBox;
import CommonTools.JRtaRadioButton;
import CommonTools.JRtaTextField;
import CommonTools.SqlInfo;
import CommonTools.StringTools;
import CommonTools.ini.INIFile;
import CommonTools.ini.Settings;
import rehaHMK.RehaIO.SocketClient;
import rehaHMK.Tools.ArztTools;
import ag.ion.bion.officelayer.application.OfficeApplicationException;
import ag.ion.bion.officelayer.document.DocumentDescriptor;
import ag.ion.bion.officelayer.document.DocumentException;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.document.IDocumentDescriptor;
import ag.ion.bion.officelayer.document.IDocumentService;
import ag.ion.bion.officelayer.text.IText;
import ag.ion.bion.officelayer.text.ITextDocument;
import ag.ion.bion.officelayer.text.ITextField;
import ag.ion.bion.officelayer.text.ITextFieldService;
import ag.ion.bion.officelayer.text.ITextRange;
import ag.ion.bion.officelayer.text.ITextTable;
import ag.ion.bion.officelayer.text.IViewCursor;
import ag.ion.bion.officelayer.text.TextException;
import ag.ion.noa.NOAException;
import ag.ion.noa.graphic.GraphicInfo;
import ag.ion.noa.search.ISearchDescriptor;
import ag.ion.noa.search.ISearchResult;
import ag.ion.noa.search.SearchDescriptor;
import rehaHMK.dialoge.ArztAuswahl;
import environment.Path;
import gui.Cursors;
import io.RehaIOMessages;
import rehaHMK.CompoundPainters;
import rehaHMK.RehaHMK;
import rehaHMK.RehaHMKTab;
import uk.co.mmscomputing.device.scanner.Scanner;
import uk.co.mmscomputing.device.scanner.ScannerIOException;
import uk.co.mmscomputing.device.scanner.ScannerIOMetadata;
import uk.co.mmscomputing.device.scanner.ScannerListener;

public class RehaHMKPanel2 extends JXPanel implements ScannerListener {
    protected static String progHome = Path.Instance.getProghome();
    private static final long serialVersionUID = 1L;

    RehaHMKTab eltern;
    Scanner scanner;
    JPanel content;
    int ckERST_VO;
    int ckFOLGE_VO = 1;
    int ckADR_VO = 2;
    int ckGRUPPEN_TH = 3;
    int ckREZEPT_DAT = 4;
    int ckBEHANDL_BEGIN = 5;
    int ckHB_JANEIN = 6;
    int ckTB_JANEIN = 7;
    int ckINDI_SCHL = 8;
    int ckMAX_ANZAHL = 9;
    int ckFREQU_WO = 10;
    int ckBEH_DAUER = 11;
    int ckHEIL_MITTEL = 12;
    int ckLEIT_SYMPTOMATIK = 13;
    int ckDIAG_NOSE = 14;
    int ckBEGRUEND_ADR = 15;
    int ckSONSTIGER_GRUND = 16;
    int tfsREZEPT_DAT;
    int tfsBEHANDL_BEGIN = 1;
    int tfsINDI_SCHL = 2;
    int tfsMAX_ANZAHL = 3;
    int tfsFREQU_WO = 4;
    int tfsBEH_DAUER = 5;
    int tfsHEIL_MITTEL = 6;
    int tfsLEIT_SYMPTOMATIK = 7;
    int tfsDIAG_NOSE = 8;
    int tfsBEGRUEND_ADR = 9;
    int tfsSONSTIGER_GRUND = 10;
    int rbHB_JA;
    int rbHB_NEIN = 1;
    int rbTB_JA = 2;
    int rbTB_NEIN = 3;
    int xversatz;

    JRtaCheckBox[] chbox = new JRtaCheckBox[20];
    JRtaTextField[] tfs = new JRtaTextField[11];
    JRtaRadioButton[] rbuts = new JRtaRadioButton[4];
    ButtonGroup hbgroup = new ButtonGroup();
    ButtonGroup tbgroup = new ButtonGroup();
    JTextArea[] tas = new JTextArea[4];
    JButton[] buts = new JButton[4];
    JRtaComboBox scanners;
    JRtaComboBox scanformat;
    JRtaCheckBox scandialog;
    boolean scannerok;
    Settings inifile;
    ActionListener al;
    IDocument document;
    ITextDocument textDocument;
    boolean sourceenabled;
    JEditorPane htmlpane;
    StringBuffer arztbuf = new StringBuffer();
    JRtaTextField reznummer;

    int[][] kreuzpos = new int[][] { new int[2], { 3, 0 }, { 0, 2 }, { 3, 2 }, { 0, 5 }, { 5, 5 }, { 0, 8 }, { 8, 8 },
            { 0, 11 }, { 4, 11 }, { 0, 14 }, { 4, 14 }, { 0, 17 }, { 0, 21 }, { 0, 25 }, { 0, 29 }, { 0, 33 } };

    int[][] textpos = new int[][] { { 3, 11 }, { 7, 11 }, { 3, 14 }, { 7, 14 }, { 2, 18 }, { 2, 22 }, { 2, 26 },
            { 2, 30 }, { 2, 34 } };

    String arztkorrektur = "";

    public RehaHMKPanel2(RehaHMKTab xeltern) {
        this.eltern = xeltern;
        setOpaque(false);
        setBackgroundPainter((Painter) CompoundPainters.CP);
        setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        setLayout(new BorderLayout());
        new SwingWorker<Void, Void>() {
            protected Void doInBackground() throws Exception {
                try {
                    UIManager.put("Separator.foreground", new Color(231, 120, 23));
                    try {
                        RehaHMKPanel2.this.scanStarten();
                        RehaHMKPanel2.this.scannerok = true;
                    } catch (Exception ex2) {
                        RehaHMKPanel2.this.scannerok = false;
                    }
                    RehaHMKPanel2.this.activateListener();

                    inifile = new INIFile(progHome + "ini/" + RehaHMK.aktIK + "/hmrmodul.ini");
                    RehaHMKPanel2.this.add(RehaHMKPanel2.this.getContent(), "Center");
                    RehaHMKPanel2.this.add((Component) RehaHMKPanel2.this.getScannerSaich(), "South");
                    RehaHMKPanel2.this.validate();
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            RehaHMKPanel2.this.reznummer.requestFocus();
                        }
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    private JXPanel getScannerSaich() {
        String x = "15dlu,95dlu,15dlu,95dlu,15dlu,0dlu,15dlu,fill:0:grow(1.0),15dlu,95dlu,15dlu,95dlu,15dlu";
        String y = "15dlu,p,15dlu,p,5dlu,p,15dlu";
        FormLayout lay = new FormLayout(x, y);
        CellConstraints cc = new CellConstraints();
        PanelBuilder pb = new PanelBuilder(lay);
        pb.getPanel()
          .setOpaque(false);
        pb.getPanel()
          .validate();
        pb.addSeparator("", cc.xyw(1, 2, 12, CellConstraints.DEFAULT, CellConstraints.TOP));
        boolean mustsave = false;
        FormLayout lay2 = new FormLayout("fill:0:grow(0.33),2dlu,fill:0:grow(0.66)", "p");
        CellConstraints cc2 = new CellConstraints();
        PanelBuilder pb2 = new PanelBuilder(lay2);
        pb2.getPanel()
           .setOpaque(false);
        JLabel xlab = new JLabel("RezeptNr.");
        xlab.setOpaque(false);
        pb2.add(xlab, cc2.xy(1, 1));
        pb2.add((Component) (this.reznummer = new JRtaTextField("GROSS", true)), cc.xy(3, 1));
        pb2.getPanel()
           .validate();
        pb.add(pb2.getPanel(), cc.xy(2, 4, CellConstraints.FILL, CellConstraints.FILL));
        this.reznummer.setText("");
        pb.add(this.buts[0] = ButtonTools.macheButton("Formular erzeugen", "ooformular", this.al), cc.xy(2, 6));
        String sdummy = null;
        if (this.scannerok) {
            try {
                pb.addLabel("Scanner auswählen", cc.xy(12, 4));
                pb.add((Component) (this.scanners = new JRtaComboBox(this.scanner.getDeviceNames())), cc.xy(12, 6));
                sdummy = this.inifile.getStringProperty("HMRModul", "Scanner");
                if (sdummy == null) {
                    this.inifile.setStringProperty("HMRModul", "Scanner", this.scanners.getSelectedItem()
                                                                                       .toString(),
                            null);
                    this.inifile.setIntegerProperty("HMRModul", "XVersatz", 0, null);
                    mustsave = true;
                } else {
                    this.scanners.setSelectedItem(sdummy);
                    sdummy = this.inifile.getStringProperty("HMRModul", "XVersatz");
                    if (sdummy == null) {
                        this.inifile.setStringProperty("HMRModul", "XVersatz", "0", null);
                        mustsave = true;
                    } else {
                        this.xversatz = this.inifile.getIntegerProperty("HMRModul", "XVersatz");
                    }
                }
                this.scanners.setActionCommand("scannerwahl");
                this.scanners.addActionListener(this.al);
            } catch (ScannerIOException | NullPointerException nullPointerException) {
            }
        }
        this.scanformat = new JRtaComboBox(new String[] { "DIN A5", "DIN A4" });
        sdummy = this.inifile.getStringProperty("HMRModul", "Scanformat");
        if (sdummy == null) {
            this.inifile.setStringProperty("HMRModul", "Scanformat", this.scanformat.getSelectedItem()
                                                                                    .toString(),
                    null);
            mustsave = true;
        } else {
            this.scanformat.setSelectedItem(sdummy);
        }
        this.scanformat.setActionCommand("scanformat");
        this.scanformat.addActionListener(this.al);
        this.scanformat.addActionListener(this.al);
        pb.add(this.buts[2] = ButtonTools.macheButton("Arzt suchen", "arztadresse", this.al), cc.xy(10, 4));
        pb.add(this.buts[3] = ButtonTools.macheButton("finde Arzt über RezNr.", "arztreznr", this.al), cc.xy(10, 6));
        if (mustsave) {
            this.inifile.save();
        }
        this.htmlpane = new JEditorPane();
        this.htmlpane.setContentType("text/html");
        this.htmlpane.setEditable(false);
        this.htmlpane.setOpaque(false);
        JScrollPane scr = JCompTools.getTransparentScrollPane(this.htmlpane);
        scr.validate();
        pb.add(scr, cc.xywh(4, 2, 5, 6));
        JXPanel pane = new JXPanel(new BorderLayout());
        pane.setOpaque(false);
        pane.add(pb.getPanel(), "Center");
        return pane;
    }

    private JScrollPane getContent() {
        String x = "5dlu,25dlu,fill:0:grow(0.5),5dlu,fill:0:grow(0.5),5dlu";
        String y = "5dlu,p,5dlu,p,2dlu,p,10dlu,p, 5dlu,p, 10dlu,p,5dlu,p,10dlu,p,5dlu,p,10dlu,p,5dlu,p,2dlu,p,5dlu,p,2dlu,p,5dlu,p,2dlu,p,5dlu";
        FormLayout lay = new FormLayout(x, y);
        CellConstraints cc = new CellConstraints();
        PanelBuilder pb = new PanelBuilder(lay);
        pb.getPanel()
          .setOpaque(false);
        pb.addSeparator("Änderung der Rezeptart", cc.xyw(2, 2, 4));
        pb.add((Component) (this.chbox[this.ckERST_VO] = new JRtaCheckBox("Erstverordnung statt Folgeverordnung")),
                cc.xy(3, 4));
        pb.add((Component) (this.chbox[this.ckFOLGE_VO] = new JRtaCheckBox("Folgeverordnung statt Erstverordnung")),
                cc.xy(5, 4));
        pb.add((Component) (this.chbox[this.ckADR_VO] = new JRtaCheckBox("Verordnung außerhalb des Regelfalles")),
                cc.xy(3, 6));
        pb.add((Component) (this.chbox[this.ckGRUPPEN_TH] = new JRtaCheckBox("Gruppentherapie")), cc.xy(5, 6));
        pb.addSeparator("Änderung Rezeptdatum / spätester Behandlungsbeginn", cc.xyw(2, 8, 4));
        JXPanel pan = getNewForm("p,10dlu,40dlu,fill:0:grow(0.5)", "p");
        CellConstraints cc2 = new CellConstraints();
        pan.add((Component) (this.chbox[this.ckREZEPT_DAT] = new JRtaCheckBox("Rezeptdatum ändern in")), cc2.xy(1, 1));
        pan.add((Component) (this.tfs[this.tfsREZEPT_DAT] = new JRtaTextField("DATUM", true)), cc2.xy(3, 1));
        pb.add((Component) pan, cc.xy(3, 10));
        pan = getNewForm("p,10dlu,40dlu,fill:0:grow(0.5)", "p");
        pan.add((Component) (this.chbox[this.ckBEHANDL_BEGIN] = new JRtaCheckBox("spätester Beh.Beginn ändern in")),
                cc2.xy(1, 1));
        pan.add((Component) (this.tfs[this.tfsBEHANDL_BEGIN] = new JRtaTextField("DATUM", true)), cc2.xy(3, 1));
        pb.add((Component) pan, cc.xy(5, 10));
        pb.addSeparator("Hausbesuch / Therapiebericht", cc.xyw(2, 12, 4));
        pan = getNewForm("p,10dlu,40dlu,fill:0:grow(0.5)", "p,2dlu,p");
        cc2 = new CellConstraints();
        pan.add((Component) (this.chbox[this.ckHB_JANEIN] = new JRtaCheckBox(
                "Hausbesuch JA/NEIN                      ")),
                cc2.xywh(1, 1, 1, 3, CellConstraints.DEFAULT, CellConstraints.CENTER));
        pan.add((Component) (this.rbuts[this.rbHB_JA] = new JRtaRadioButton("Ja")), cc2.xy(3, 1));
        pan.add((Component) (this.rbuts[this.rbHB_NEIN] = new JRtaRadioButton("Nein")), cc2.xy(3, 3));
        pb.add((Component) pan, cc.xy(3, 14));
        pan = getNewForm("p,10dlu,40dlu,fill:0:grow(0.5)", "p,2dlu,p");
        pan.add((Component) (this.chbox[this.ckTB_JANEIN] = new JRtaCheckBox(
                "Therapiebericht JA/NEIN                 ")),
                cc2.xywh(1, 1, 1, 3, CellConstraints.DEFAULT, CellConstraints.CENTER));
        pan.add((Component) (this.rbuts[this.rbTB_JA] = new JRtaRadioButton("Ja")), cc2.xy(3, 1));
        pan.add((Component) (this.rbuts[this.rbTB_NEIN] = new JRtaRadioButton("Nein")), cc2.xy(3, 3));
        pb.add((Component) pan, cc.xy(5, 14));
        int i;
        for (i = 0; i < 2; i++) {
            if (i == 1) {
                this.rbuts[i].setSelected(true);
                this.rbuts[i + 2].setSelected(true);
            }
            this.rbuts[i].setOpaque(false);
            this.rbuts[i + 2].setOpaque(false);
            this.rbuts[i].setEnabled(false);
            this.rbuts[i + 2].setEnabled(false);
            this.hbgroup.add((AbstractButton) this.rbuts[i]);
            this.tbgroup.add((AbstractButton) this.rbuts[i + 2]);
        }
        pb.addSeparator("Indikationsschlüssel / Verordnungsmenge / Behandlungsfrequenz / Behandlungsdauer",
                cc.xyw(2, 16, 4));
        pan = getNewForm("p,10dlu,40dlu,fill:0:grow(0.5)", "p,5dlu,p");
        cc2 = new CellConstraints();
        pan.add((Component) (this.chbox[this.ckINDI_SCHL] = new JRtaCheckBox("Indikationsschlüssel ändern in")),
                cc2.xy(1, 1));
        pan.add((Component) (this.tfs[this.tfsINDI_SCHL] = new JRtaTextField("nix", true)), cc2.xy(3, 1));
        pan.add((Component) (this.chbox[this.ckFREQU_WO] = new JRtaCheckBox("Anzahl Behandlungen pro Woche")),
                cc2.xy(1, 3));
        pan.add((Component) (this.tfs[this.tfsFREQU_WO] = new JRtaTextField("nix", true)), cc2.xy(3, 3));
        pb.add((Component) pan, cc.xy(3, 18));
        pan = getNewForm("p,10dlu,40dlu,fill:0:grow(0.5)", "p,5dlu,p");
        pan.add((Component) (this.chbox[this.ckMAX_ANZAHL] = new JRtaCheckBox("Anzahl der Behandlungen lt. HMR")),
                cc2.xy(1, 1));
        pan.add((Component) (this.tfs[this.tfsMAX_ANZAHL] = new JRtaTextField("ZAHLEN", true)), cc2.xy(3, 1));
        pan.add((Component) (this.chbox[this.ckBEH_DAUER] = new JRtaCheckBox("Dauer der Behandlugen in Minuten")),
                cc2.xy(1, 3));
        pan.add((Component) (this.tfs[this.tfsBEH_DAUER] = new JRtaTextField("nix", true)), cc2.xy(3, 3));
        pb.add((Component) pan, cc.xy(5, 18));
        pb.addSeparator("Heilmittel, Leitsymptomatik und mehr....", cc.xyw(2, 20, 4));
        pb.add((Component) (this.chbox[this.ckHEIL_MITTEL] = new JRtaCheckBox("Heilmittel / HM-Kombination ändern in")),
                cc.xy(3, 22));
        pb.add((Component) (this.tfs[this.tfsHEIL_MITTEL] = new JRtaTextField("nix", true)), cc.xy(3, 24));
        pb.add((Component) (this.chbox[this.ckLEIT_SYMPTOMATIK] = new JRtaCheckBox("Leitsymptomatik gemäß HMR")),
                cc.xy(5, 22));
        pb.add((Component) (this.tfs[this.tfsLEIT_SYMPTOMATIK] = new JRtaTextField("nix", true)), cc.xy(5, 24));
        pb.add((Component) (this.chbox[this.ckDIAG_NOSE] = new JRtaCheckBox("Diagnose gemäß HMR")), cc.xy(3, 26));
        pb.add((Component) (this.tfs[this.tfsDIAG_NOSE] = new JRtaTextField("nix", true)), cc.xy(3, 28));
        pb.add((Component) (this.chbox[this.ckBEGRUEND_ADR] = new JRtaCheckBox(
                "medizinische Begründung für außerhalb d.Regelfalles")), cc.xy(5, 26));
        pb.add((Component) (this.tfs[this.tfsBEGRUEND_ADR] = new JRtaTextField("nix", true)), cc.xy(5, 28));
        pb.add((Component) (this.chbox[this.ckSONSTIGER_GRUND] = new JRtaCheckBox("Sonstige Änderungen")),
                cc.xy(3, 30));
        pb.add((Component) (this.tfs[this.tfsSONSTIGER_GRUND] = new JRtaTextField("nix", true)), cc.xyw(3, 32, 3));
        for (i = 0; i < 11; i++) {
            this.tfs[i].setEnabled(false);
        }
        for (i = 0; i < 17; i++) {
            this.chbox[i].setActionCommand("cbox-" + i);
            this.chbox[i].addActionListener(this.al);
        }
        pb.getPanel()
          .validate();
        this.content = pb.getPanel();
        JScrollPane scrpane = JCompTools.getTransparentScrollPane(this.content);
        scrpane.getVerticalScrollBar()
               .setUnitIncrement(15);
        scrpane.validate();
        return scrpane;
    }

    private JXPanel getNewForm(String x, String y) {
        FormLayout lay = new FormLayout(x, y);
        JXPanel pan = new JXPanel();
        pan.setOpaque(false);
        pan.setLayout((LayoutManager) lay);
        return pan;
    }

    private JRtaCheckBox createBox(String text, Font font, Color fcolor, String command, ActionListener al) {
        JRtaCheckBox box = new JRtaCheckBox(text);
        box.setFont(font);
        box.setForeground(fcolor);
        if (al != null) {
            box.addActionListener(al);
        }
        return box;
    }

    private void scanStarten() {
        try {
            if (this.scanner == null) {
                this.scanner = Scanner.getDevice();
            }
            String[] arrayOfString = this.scanner.getDeviceNames();
        } catch (ScannerIOException e2) {
            e2.printStackTrace();
        }
    }

    private void activateListener() {
        this.al = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String cmd = e.getActionCommand();
                if (cmd.contains("-")) {
                    int zahl = Integer.parseInt(cmd.split("-")[1]);
                    if (zahl < 4) {
                        if (RehaHMKPanel2.this.chbox[zahl].isSelected()) {
                            for (int i = 0; i < 4; i++) {
                                if (i != zahl) {
                                    RehaHMKPanel2.this.chbox[i].setSelected(false);
                                }
                            }
                        }
                    } else {
                        switch (zahl) {
                        case 4:
                            if (RehaHMKPanel2.this.chbox[4].isSelected()) {
                                RehaHMKPanel2.this.tfs[0].setEnabled(true);
                                RehaHMKPanel2.this.tfs[0].requestFocus();
                            } else {
                                RehaHMKPanel2.this.tfs[0].setText("  .  .    ");
                                RehaHMKPanel2.this.tfs[0].setEnabled(false);
                            }
                            break;
                        case 5:
                            if (RehaHMKPanel2.this.chbox[5].isSelected()) {
                                RehaHMKPanel2.this.tfs[1].setEnabled(true);
                                RehaHMKPanel2.this.tfs[1].requestFocus();
                            } else {
                                RehaHMKPanel2.this.tfs[1].setText("  .  .    ");
                                RehaHMKPanel2.this.tfs[1].setEnabled(false);
                            }
                            break;
                        case 6:
                            if (RehaHMKPanel2.this.chbox[6].isSelected()) {
                                RehaHMKPanel2.this.rbuts[0].setEnabled(true);
                                RehaHMKPanel2.this.rbuts[1].setEnabled(true);
                                RehaHMKPanel2.this.rbuts[0].requestFocus();
                            } else {
                                RehaHMKPanel2.this.rbuts[1].setSelected(true);
                                RehaHMKPanel2.this.rbuts[0].setEnabled(false);
                                RehaHMKPanel2.this.rbuts[1].setEnabled(false);
                            }
                            break;
                        case 7:
                            if (RehaHMKPanel2.this.chbox[7].isSelected()) {
                                RehaHMKPanel2.this.rbuts[2].setEnabled(true);
                                RehaHMKPanel2.this.rbuts[3].setEnabled(true);
                                RehaHMKPanel2.this.rbuts[2].requestFocus();
                            } else {
                                RehaHMKPanel2.this.rbuts[3].setSelected(true);
                                RehaHMKPanel2.this.rbuts[2].setEnabled(false);
                                RehaHMKPanel2.this.rbuts[3].setEnabled(false);
                            }
                            break;
                        case 8:
                            if (RehaHMKPanel2.this.chbox[8].isSelected()) {
                                RehaHMKPanel2.this.tfs[2].setEnabled(true);
                                RehaHMKPanel2.this.tfs[2].requestFocus();
                            } else {
                                RehaHMKPanel2.this.tfs[2].setText("");
                                RehaHMKPanel2.this.tfs[2].setEnabled(false);
                            }
                            break;
                        case 9:
                            if (RehaHMKPanel2.this.chbox[9].isSelected()) {
                                RehaHMKPanel2.this.tfs[3].setEnabled(true);
                                RehaHMKPanel2.this.tfs[3].requestFocus();
                            } else {
                                RehaHMKPanel2.this.tfs[3].setText("");
                                RehaHMKPanel2.this.tfs[3].setEnabled(false);
                            }
                            break;
                        case 10:
                            if (RehaHMKPanel2.this.chbox[10].isSelected()) {
                                RehaHMKPanel2.this.tfs[4].setEnabled(true);
                                RehaHMKPanel2.this.tfs[4].requestFocus();
                            } else {
                                RehaHMKPanel2.this.tfs[4].setText("");
                                RehaHMKPanel2.this.tfs[4].setEnabled(false);
                            }
                            break;
                        case 11:
                            if (RehaHMKPanel2.this.chbox[11].isSelected()) {
                                RehaHMKPanel2.this.tfs[5].setEnabled(true);
                                RehaHMKPanel2.this.tfs[5].requestFocus();
                            } else {
                                RehaHMKPanel2.this.tfs[5].setText("");
                                RehaHMKPanel2.this.tfs[5].setEnabled(false);
                            }
                            break;
                        case 12:
                            if (RehaHMKPanel2.this.chbox[12].isSelected()) {
                                RehaHMKPanel2.this.tfs[6].setEnabled(true);
                                RehaHMKPanel2.this.tfs[6].requestFocus();
                            } else {
                                RehaHMKPanel2.this.tfs[6].setText("");
                                RehaHMKPanel2.this.tfs[6].setEnabled(false);
                            }
                            break;
                        case 13:
                            if (RehaHMKPanel2.this.chbox[13].isSelected()) {
                                RehaHMKPanel2.this.tfs[7].setEnabled(true);
                                RehaHMKPanel2.this.tfs[7].requestFocus();
                            } else {
                                RehaHMKPanel2.this.tfs[7].setText("");
                                RehaHMKPanel2.this.tfs[7].setEnabled(false);
                            }
                            break;
                        case 14:
                            if (RehaHMKPanel2.this.chbox[14].isSelected()) {
                                RehaHMKPanel2.this.tfs[8].setEnabled(true);
                                RehaHMKPanel2.this.tfs[8].requestFocus();
                            } else {
                                RehaHMKPanel2.this.tfs[8].setText("");
                                RehaHMKPanel2.this.tfs[8].setEnabled(false);
                            }
                            break;
                        case 15:
                            if (RehaHMKPanel2.this.chbox[15].isSelected()) {
                                RehaHMKPanel2.this.tfs[9].setEnabled(true);
                                RehaHMKPanel2.this.tfs[9].requestFocus();
                            } else {
                                RehaHMKPanel2.this.tfs[9].setText("");
                                RehaHMKPanel2.this.tfs[9].setEnabled(false);
                            }
                            break;
                        case 16:
                            if (RehaHMKPanel2.this.chbox[16].isSelected()) {
                                RehaHMKPanel2.this.tfs[10].setEnabled(true);
                                RehaHMKPanel2.this.tfs[10].requestFocus();
                            } else {
                                RehaHMKPanel2.this.tfs[10].setText("");
                                RehaHMKPanel2.this.tfs[10].setEnabled(false);
                            }
                            break;
                        }
                    }
                } else if ("scannerwahl".equals(cmd)) {
                    RehaHMKPanel2.this.doIniFile("HMRModul", "Scanner", RehaHMKPanel2.this.scanners.getSelectedItem()
                                                                                                   .toString());
                } else if ("scandialog".equals(cmd)) {
                    RehaHMKPanel2.this.doIniFile("HMRModul", "Scandialog",
                            RehaHMKPanel2.this.scandialog.isSelected() ? "1" : "0");
                } else if ("scanformat".equals(cmd)) {
                    RehaHMKPanel2.this.doIniFile("HMRModul", "Scanformat",
                            RehaHMKPanel2.this.scanformat.getSelectedItem()
                                                         .toString());
                } else if ("ooformular".equals(cmd)) {
                    if (RehaHMKPanel2.this.RezNumOk()) {
                        RehaHMKPanel2.this.doOOFormular();
                    }
                } else if ("scannen".equals(cmd)) {
                    if (RehaHMKPanel2.this.scannerok && RehaHMKPanel2.this.RezNumOk()) {
                        RehaHMKPanel2.this.doScannen();
                    }
                } else if ("arztadresse".equals(cmd)) {
                    RehaHMKPanel2.this.doArztAdresse();
                } else if ("arztreznr".equals(cmd)) {
                    RehaHMKPanel2.this.doArztReznum();
                }
            }
        };
    }

    private boolean RezNumOk() {
        String rezNr = this.reznummer.getText()
                                     .trim();
        if ("".equals(rezNr)) {
            JOptionPane.showMessageDialog(null, "Ohne die Eingabe der Rezeptnummer ist dieser Vorgang nicht möglich");
            this.reznummer.requestFocus();
            return false;
        }
        if ("".equals(this.arztkorrektur)) {
            JOptionPane.showMessageDialog(null, "Ohne die Eingabe eines Arztes ist dieser Vorgang nicht möglich");
            this.reznummer.requestFocus();
            return false;
        }
        String patintern = SqlInfo.holeEinzelFeld(
                "select t2.pat_intern from verordn as t1 join pat5 as t2 on (t1.pat_intern = t2.pat_intern) where t1.rez_nr = '"
                        + rezNr + "' LIMIT 1");
        if ("".equals(patintern)) {
            JOptionPane.showMessageDialog(null,
                    "Ungültige Rezeptnummer, kein Patient zugeordnet, Scanvorgang nicht möglich");
            return false;
        }
        int frage = JOptionPane.showConfirmDialog(null, "Soll die Rezeptkorrektur im Patientenstamm vermerkt werden?",
                "Wichtige Benutzeranfrage", 0);
        if (frage == 0) {
            String cmd = "select anamnese from pat5 where pat_intern = '" + patintern + "' LIMIT 1";
            String anamnese = SqlInfo.holeEinzelFeld(cmd);
            String string = "Korrekturfax für " + rezNr + ", Arzt: " + this.arztkorrektur + ", User: " + RehaHMK.aktUser
                    + ", " + DatFunk.sHeute() + ".\n";
            SqlInfo.sqlAusfuehren("update pat5 set anamnese='" + StringTools.Escaped(String.valueOf(string) + anamnese)
                    + "' where pat_intern = '" + patintern + "' LIMIT 1");
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    new SocketClient().setzeRehaNachricht(RehaHMK.rehaReversePort,
                            "RehaHMK#" + RehaIOMessages.MUST_REZFIND + "#" + rezNr);
                }
            });
        }
        return true;
    }

    private void doArztAdresse() {
        JRtaTextField tf1 = new JRtaTextField("nix", false);
        JRtaTextField tf2 = new JRtaTextField("nix", false);
        JRtaTextField tf3 = new JRtaTextField("nix", false);
        ArztAuswahl awahl = new ArztAuswahl(null, "ArztAuswahl", new String[] { "", "" },
                new JRtaTextField[] { tf1, tf2, tf3 }, "");
        awahl.setModal(true);
        awahl.setLocationRelativeTo((Component) this);
        awahl.setVisible(true);
        awahl.dispose();
        awahl = null;
        if (!"".equals(tf3.getText())) {
            regleHTML(tf3.getText());
        } else {
            this.htmlpane.setText("");
            RehaHMK.hmAdrADaten = new HashMap<>();
        }
    }

    private void doArztReznum() {
        Object reznum = JOptionPane.showInputDialog((Component) null, "Bitte geben Sie die Rezeptnummer ein");
        if (reznum == null || "".equals(reznum.toString())) {
            this.htmlpane.setText("");
            RehaHMK.hmAdrADaten = new HashMap<>();
            return;
        }
        String test = SqlInfo.holeEinzelFeld("select arztid from verordn where rez_nr = '" + reznum + "' LIMIT 1");
        if ("".equals(test)) {
            test = SqlInfo.holeEinzelFeld("select arztid from lza where rez_nr = '" + reznum + "' LIMIT 1");
            if ("".equals(test)) {
                JOptionPane.showMessageDialog(null,
                        "Die Rezeptnummer ist weder im aktuellen Rezeptstamm noch in der Historie vorhanden!");
                this.htmlpane.setText("");
                RehaHMK.hmAdrADaten = new HashMap<>();
            } else {
                JOptionPane.showMessageDialog(null, "Zur Info: Das angegebene Rezept ist bereits in der Historie!");
                this.reznummer.setText(reznum.toString());
                regleHTML(test);
            }
        } else {
            this.reznummer.setText(reznum.toString());
            regleHTML(test);
        }
    }

    private void regleHTML(String arztid) {
        ArztTools.constructArztHMap(arztid);
        this.arztbuf.setLength(0);
        this.arztbuf.trimToSize();
        this.arztbuf.append("<html><body><br><font face='Arial, Helvetica'>");
        this.arztbuf.append("Klinik: ")
                    .append("".equals((String) RehaHMK.hmAdrADaten.get("<Aklinik>")) ? "keine Klinik!!!"
                            : (String) RehaHMK.hmAdrADaten.get("<Aklinik>"))
                    .append("<br>");
        this.arztbuf.append("Anrede: ")
                    .append((String) RehaHMK.hmAdrADaten.get("<Aadr1>"))
                    .append(" ")
                    .append((String) RehaHMK.hmAdrADaten.get("<Aadr2>"))
                    .append("<br>");
        this.arztbuf.append("Fax: ")
                    .append("".equals((String) RehaHMK.hmAdrADaten.get("<Afax>")) ? "keine Faxnummer vorhanden!!!"
                            : (String) RehaHMK.hmAdrADaten.get("<Afax>"));
        this.arztbuf.append("<br>Briefanrede: ")
                    .append((String) RehaHMK.hmAdrADaten.get("<Aadr5>"));
        this.arztbuf.append("</font></body></html>");
        this.htmlpane.setText(this.arztbuf.toString());
        this.arztkorrektur = RehaHMK.hmAdrADaten.get("<Aadr2>");
    }

    private void doIniFile(String sektion, String property, String value) {
        this.inifile.setStringProperty(sektion, property, value, null);
        this.inifile.save();
    }

    private void doOOFormular() {
        if (!RehaHMK.officeapplication.isPresent()) {
            return;
        }
        try {
            IDocumentService documentService;
            RehaHMK.thisFrame.setCursor(Cursors.wartenCursor);
            documentService = RehaHMK.officeapplication.get()
                                                       .getDocumentService();
            DocumentDescriptor documentDescriptor = new DocumentDescriptor();
            documentDescriptor.setHidden(true);
            documentDescriptor.setAsTemplate(true);
            IDocument document;
            String url = scanformat.getSelectedIndex() == 0
                    ? progHome + "vorlagen/" + RehaHMK.aktIK + "/Rezeptkorrektur_A5-Rezepte.ott"
                    : progHome + "vorlagen/" + RehaHMK.aktIK + "/Rezeptkorrektur_A4-Rezepte.ott";

            document = documentService.loadDocument(url, (IDocumentDescriptor) documentDescriptor);
            this.textDocument = (ITextDocument) document;
            ITextFieldService textFieldService = this.textDocument.getTextFieldService();
            ITextField[] placeholders;
            placeholders = textFieldService.getPlaceholderFields();
            try {
                String placeholderDisplayText = null;
                for (int i = 0; i < placeholders.length; i++) {
                    boolean schonersetzt = false;
                    try {
                        placeholderDisplayText = placeholders[i].getDisplayText()
                                                                .toLowerCase();
                    } catch (RuntimeException ex) {
                        ex.printStackTrace();
                    }
                    Set<?> entries = RehaHMK.hmAdrADaten.entrySet();
                    Iterator<?> it = entries.iterator();
                    while (it.hasNext()) {
                        Map.Entry<?, ?> entry = (Map.Entry<?, ?>) it.next();
                        if (entry.getKey()
                                 .toString()
                                 .toLowerCase()
                                 .equals(placeholderDisplayText)) {
                            if ("".equals(entry.getValue()
                                               .toString()
                                               .trim())) {
                                placeholders[i].getTextRange()
                                               .setText("");
                            } else {
                                placeholders[i].getTextRange()
                                               .setText(entry.getValue()
                                                             .toString());
                            }
                            schonersetzt = true;
                            break;
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            ITextTable textTable = null;
            try {
                textTable = this.textDocument.getTextTableService()
                                             .getTextTable("Tabelle1");
            } catch (TextException e) {
                e.printStackTrace();
            }
            if (textTable == null) {
                JOptionPane.showMessageDialog(null, "Kann Tabelle nicht finden");
            } else {
                try {
                    for (int i2 = 0; i2 < this.kreuzpos.length; i2++) {
                        textTable.getCell(this.kreuzpos[i2][0], this.kreuzpos[i2][1])
                                 .getTextService()
                                 .getText()
                                 .setText(this.chbox[i2].isSelected() ? "X" : "");
                        if (i2 >= 8) {
                            textTable.getCell(this.textpos[i2 - 8][0], this.textpos[i2 - 8][1])
                                     .getTextService()
                                     .getText()
                                     .setText(this.tfs[i2 - 8 + 2].getText());
                        }
                    }
                    if (this.chbox[4].isSelected() && !".  .".equals(this.tfs[0].getText()
                                                                                .trim())) {
                        textTable.getCell(4, 5)
                                 .getTextService()
                                 .getText()
                                 .setText(this.tfs[0].getText());
                    }
                    if (this.chbox[5].isSelected() && !".  .".equals(this.tfs[1].getText()
                                                                                .trim())) {
                        textTable.getCell(9, 5)
                                 .getTextService()
                                 .getText()
                                 .setText(this.tfs[1].getText());
                    }
                    if (this.chbox[6].isSelected()) {
                        if (this.rbuts[0].isSelected()) {
                            textTable.getCell(3, 8)
                                     .getTextService()
                                     .getText()
                                     .setText("X");
                        } else {
                            textTable.getCell(5, 8)
                                     .getTextService()
                                     .getText()
                                     .setText("X");
                        }
                    }
                    if (this.chbox[7].isSelected()) {
                        if (this.rbuts[2].isSelected()) {
                            textTable.getCell(11, 8)
                                     .getTextService()
                                     .getText()
                                     .setText("X");
                        } else {
                            textTable.getCell(13, 8)
                                     .getTextService()
                                     .getText()
                                     .setText("X");
                        }
                    }
                } catch (TextException e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(50L);
                    this.textDocument.getFrame()
                                     .getXFrame()
                                     .getContainerWindow()
                                     .setFocus();
                    doRefresh(this.textDocument);
                    this.textDocument.getFrame()
                                     .getXFrame()
                                     .getContainerWindow()
                                     .setFocus();
                    Thread.sleep(100L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    textTable = this.textDocument.getTextTableService()
                                                 .getTextTable("Tabelle1");
                } catch (TextException e) {
                    e.printStackTrace();
                }
                sucheNachPlatzhalter((ITextDocument) document);
            }
            if (this.scannerok) {
                int frage = JOptionPane.showConfirmDialog(null, "Wollen Sie im Anschluß ein Rezept einscannen",
                        "Benutzeranfrage", 0);
                if (frage == 0) {
                    try {
                        doScannen();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Scanner nicht angeschlossen oder nicht eingeschaltet");
                    }
                } else {
                    new SwingWorker<Void, Void>() {
                        protected Void doInBackground() throws Exception {
                            RehaHMKPanel2.this.textDocument.getFrame()
                                                           .getXFrame()
                                                           .getContainerWindow()
                                                           .setVisible(true);
                            return null;
                        }
                    }.execute();
                }
            } else {
                new SwingWorker<Void, Void>() {
                    protected Void doInBackground() throws Exception {
                        RehaHMKPanel2.this.textDocument.getFrame()
                                                       .getXFrame()
                                                       .getContainerWindow()
                                                       .setVisible(true);
                        return null;
                    }
                }.execute();
            }
        } catch (OfficeApplicationException | NOAException | TextException e) {
            LoggerFactory.getLogger(RehaHMK.class)
                         .error("something bad happens here", e);
        }
        RehaHMK.thisFrame.setCursor(Cursors.normalCursor);
    }

    private static void doRefresh(ITextDocument document) {
        XRefreshable refresh;
        refresh = UnoRuntime.queryInterface(XRefreshable.class, document.getXTextDocument());
        refresh.refresh();
        try {
            Thread.sleep(50L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static boolean sucheNachPlatzhalter(ITextDocument document) {
        OutputStream out = null;
        try {
            document.getPersistenceService()
                    .store(out);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        doRefresh(document);
        IViewCursor viewCursor = document.getViewCursorService()
                                         .getViewCursor();
        viewCursor.getPageCursor()
                  .jumpToStartOfPage();
        document.getTextService()
                .getCursorService()
                .getTextCursor()
                .gotoStart(true);
        IText text = document.getTextService()
                             .getText();
        String stext = text.getText();
        int start = 0;
        int vars = 0;
        boolean noendfound = false;
        while ((start = stext.indexOf('^')) >= 0) {
            noendfound = true;
            for (int i = 1; i < 150; i++) {
                if ("^".equals(stext.substring(start + i, start + i + 1))) {
                    String dummy = stext.substring(start, start + i + 1);
                    String sanweisung = dummy.replace("^", "");
                    Object ret = JOptionPane.showInputDialog(null,
                            "<html>Bitte Wert eingeben f--><b> " + sanweisung + " </b> &nbsp; </html>",
                            "Platzhalter gefunden", 1);
                    if (ret == null) {
                        return true;
                    }
                    sucheErsetze(document, dummy, ((String) ret).trim(), false);
                    stext = text.getText();
                    noendfound = false;
                    vars++;
                    break;
                }
            }
            if (noendfound) {
                JOptionPane.showMessageDialog(null,
                        "Der Baustein ist fehlerhaft, eine deshalb nicht m\n\nVermutete Ursache des Fehlers: es wurde ein Start-/Endezeichen '^' fVariable vergessen\n");
                return false;
            }
        }
        return true;
    }

    private static void sucheErsetze(ITextDocument document, String suchenach, String ersetzemit, boolean alle) {
        SearchDescriptor searchDescriptor = new SearchDescriptor(suchenach);
        searchDescriptor.setIsCaseSensitive(true);
        ISearchResult searchResult = null;
        if (alle) {
            searchResult = document.getSearchService()
                                   .findAll((ISearchDescriptor) searchDescriptor);
        } else {
            searchResult = document.getSearchService()
                                   .findFirst((ISearchDescriptor) searchDescriptor);
        }
        if (!searchResult.isEmpty()) {
            ITextRange[] textRanges = searchResult.getTextRanges();
            for (int resultIndex = 0; resultIndex < textRanges.length; resultIndex++) {
                textRanges[resultIndex].setText(ersetzemit);
            }
        }
    }

    private void doScannen() {
        if (!this.scannerok) {
            return;
        }
        try {
            if (this.textDocument == null) {
                JOptionPane.showMessageDialog(null, "Erzeugen Sie zuerst das Formular");
                return;
            }
            this.scanner.addListener(this);
            this.scanner.select(this.scanners.getSelectedItem()
                                             .toString());
            this.sourceenabled = false;
            this.scanner.acquire();
        } catch (ScannerIOException e1) {
            e1.printStackTrace();
        }
    }

    public void update(ScannerIOMetadata.Type type, ScannerIOMetadata metadata) {
        if (!ScannerIOMetadata.NEGOTIATE.equals(type)) {
            if (ScannerIOMetadata.STATECHANGE.equals(type)) {
                if ("Source Manager Open".equals(metadata.getStateStr()) && this.sourceenabled) {
                    new SwingWorker<Void, Void>() {
                        protected Void doInBackground() throws Exception {
                            RehaHMKPanel2.this.textDocument.getFrame()
                                                           .getXFrame()
                                                           .getContainerWindow()
                                                           .setVisible(true);
                            return null;
                        }
                    }.execute();
                } else if ("Source Enabled".equals(metadata.getStateStr())) {
                    this.sourceenabled = true;
                }
            } else if (type.equals(ScannerIOMetadata.EXCEPTION)) {
                new SwingWorker<Void, Void>() {
                    protected Void doInBackground() throws Exception {
                        RehaHMKPanel2.this.textDocument.getFrame()
                                                       .getXFrame()
                                                       .getContainerWindow()
                                                       .setVisible(true);
                        return null;
                    }
                }.execute();
            } else if (ScannerIOMetadata.ACQUIRED.equals(type)) {
                this.scanner.removeListener(this);
                // File file = new File(String.valueOf(progHome) + "temp/" + RehaHMK.aktIK +
                // "/rezkorrekt.jpg");
                try {
                    saveScanToTempJpegFile(metadata);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
                GraphicInfo graphicInfo;
                String imagePath = new File(
                        String.valueOf(progHome) + "temp/" + RehaHMK.aktIK + "/rezkorrekt.jpg").getAbsolutePath();
                graphicInfo = new GraphicInfo(imagePath, 200, true, 200, true, (short) 1, (short) 1,
                        TextContentAnchorType.AT_PAGE);
                XMultiServiceFactory multiServiceFactory = UnoRuntime.queryInterface(XMultiServiceFactory.class,
                        this.textDocument.getXTextDocument());
                XText xText = this.textDocument.getXTextDocument()
                                               .getText();
                XTextCursor xTextCursor = xText.createTextCursor();
                embedGraphic(graphicInfo, multiServiceFactory, xTextCursor);
                new SwingWorker<Void, Void>() {
                    protected Void doInBackground() throws Exception {
                        RehaHMKPanel2.this.textDocument.getFrame()
                                                       .getXFrame()
                                                       .getContainerWindow()
                                                       .setVisible(true);
                        return null;
                    }
                }.execute();
                this.sourceenabled = false;
                this.reznummer.setText("");
                this.arztkorrektur = "";
                this.htmlpane.setText("");
                RehaHMK.hmAdrADaten = new HashMap<>();
            }
        }
    }

    private void saveScanToTempJpegFile(ScannerIOMetadata metadata)
            throws FileNotFoundException, IOException, InterruptedException {
        FileOutputStream fout = new FileOutputStream(
                new File(String.valueOf(progHome) + "temp/" + RehaHMK.aktIK + "/rezkorrekt.jpg"));
        // ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageOutputStream os = ImageIO.createImageOutputStream(fout);

        // JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(os);
        JPEGImageWriter imageWriter = (JPEGImageWriter) ImageIO.getImageWritersBySuffix("jpeg")
                                                               .next();
        imageWriter.setOutput(os);

        // JPEGEncodeParam param =
        // encoder.getDefaultJPEGEncodeParam(metadata.getImage());
        JPEGImageWriteParam param = (JPEGImageWriteParam) imageWriter.getDefaultWriteParam();

        // param.setQuality(1.0F, false);
        // encoder.setJPEGEncodeParam(param);
        param.setCompressionMode(JPEGImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(1.0F);

        // encoder.encode(metadata.getImage());
        IIOMetadata imageMetaData = imageWriter.getDefaultImageMetadata(new ImageTypeSpecifier(metadata.getImage()),
                null);
        imageWriter.write(imageMetaData, new IIOImage(metadata.getImage(), null, null), null);

        os.close();
        // fout.write(os.toByteArray());
        fout.flush();
        fout.close();
        imageWriter.dispose();
        Thread.sleep(150L);
    }

    private void embedGraphic(GraphicInfo grProps, XMultiServiceFactory xMSF, XTextCursor xCursor) {
        XNameContainer xBitmapContainer = null;
        XText xText = xCursor.getText();
        XTextContent xImage = null;
        String internalURL = null;
        String url = null;
        try {
            xBitmapContainer = UnoRuntime.queryInterface(XNameContainer.class,
                    xMSF.createInstance("com.sun.star.drawing.BitmapTable"));
            xImage = UnoRuntime.queryInterface(XTextContent.class,
                    xMSF.createInstance("com.sun.star.text.TextGraphicObject"));
            XPropertySet xProps = UnoRuntime.queryInterface(XPropertySet.class, xImage);
            url = "file:///" + progHome + "temp/" + RehaHMK.aktIK + "/rezkorrekt.jpg";
            xBitmapContainer.insertByName("someID", url);
            internalURL = AnyConverter.toString(xBitmapContainer.getByName("someID"));
            xProps.setPropertyValue("AnchorType", TextContentAnchorType.AT_PAGE);
            xProps.setPropertyValue("GraphicURL", internalURL);
            xProps.setPropertyValue("Width", Integer.valueOf(14850));
            xProps.setPropertyValue("Height", Integer.valueOf(21000));
            xProps.setPropertyValue("TextWrap", Integer.valueOf(1));
            xProps.setPropertyValue("HoriOrientRelation", Integer.valueOf(7));
            xProps.setPropertyValue("HoriOrient", Integer.valueOf(0));
            xProps.setPropertyValue("HoriOrientPosition", Integer.valueOf(15210 + this.xversatz));
            xProps.setPropertyValue("VertOrientRelation", Integer.valueOf(7));
            xProps.setPropertyValue("VertOrient", Integer.valueOf(0));
            xProps.setPropertyValue("VertOrientPosition", Integer.valueOf(609));
            xProps.setPropertyValue("Width", Integer.valueOf(14000));
            xProps.setPropertyValue("Height", Integer.valueOf(19798));
            xText.insertTextContent((XTextRange) xCursor, xImage, false);
            xBitmapContainer.removeByName("someID");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
