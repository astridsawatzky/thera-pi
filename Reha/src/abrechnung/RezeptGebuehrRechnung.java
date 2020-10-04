package abrechnung;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.jdesktop.swingx.JXDialog;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTitledPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.therapi.reha.patient.AktuelleRezepte;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import CommonTools.DatFunk;
import CommonTools.JRtaTextField;
import CommonTools.SqlInfo;
import ag.ion.bion.officelayer.application.OfficeApplicationException;
import ag.ion.bion.officelayer.document.DocumentDescriptor;
import ag.ion.bion.officelayer.document.DocumentException;
import ag.ion.bion.officelayer.document.IDocumentDescriptor;
import ag.ion.bion.officelayer.document.IDocumentService;
import ag.ion.bion.officelayer.text.ITextDocument;
import ag.ion.bion.officelayer.text.ITextField;
import ag.ion.bion.officelayer.text.ITextFieldService;
import ag.ion.bion.officelayer.text.ITextTable;
import ag.ion.bion.officelayer.text.TextException;
import ag.ion.noa.NOAException;
import ag.ion.noa.internal.printing.PrintProperties;
import dialoge.DragWin;
import dialoge.PinPanel;
import environment.Path;
import events.RehaTPEvent;
import events.RehaTPEventClass;
import events.RehaTPEventListener;
import gui.Cursors;
import hauptFenster.Reha;
import oOorgTools.OOTools;
import stammDatenTools.ZuzahlTools.ZZStat;
import systemEinstellungen.SystemConfig;

public class RezeptGebuehrRechnung extends JXDialog
        implements FocusListener, ActionListener, MouseListener, KeyListener, RehaTPEventListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(RezeptGebuehrRechnung.class);
    private static final long serialVersionUID = 7491942845791659861L;
    private JXTitledPanel jtp = null;
    private MouseAdapter mymouse = null;
    private PinPanel pinPanel = null;
    private JXPanel content = null;
    private RehaTPEventClass rtp = null;

    private JRtaTextField[] tfs = { null, null, null, null, null };
    private Map<String,String> hmRezgeb = null;
    DecimalFormat dcf = new DecimalFormat("#########0.00");
    String rgnrNummer;
    boolean buchen;

    public RezeptGebuehrRechnung(JXFrame owner, String titel, int rueckgabe,Map<String,String> hmRezgeb,
            boolean auchbuchen) {
        super(owner, (JComponent) Reha.getThisFrame()
                                      .getGlassPane());
        this.setUndecorated(true);
        this.setName("RezgebDlg");
        this.hmRezgeb =  hmRezgeb;
        this.buchen = auchbuchen;
        this.jtp = new JXTitledPanel();
        this.jtp.setName("RezgebDlg");
        this.mymouse = new DragWin(this);
        this.jtp.addMouseListener(mymouse);
        this.jtp.addMouseMotionListener(mymouse);
        this.jtp.setContentContainer(getContent());
        this.jtp.setTitleForeground(Color.WHITE);
        this.jtp.setTitle(titel);
        this.pinPanel = new PinPanel();
        this.pinPanel.getGruen()
                     .setVisible(false);
        this.pinPanel.setName("RezgebDlg");
        this.jtp.setRightDecoration(this.pinPanel);
        this.setContentPane(jtp);
        this.setModal(true);
        this.setResizable(false);
        this.rtp = new RehaTPEventClass();
        this.rtp.addRehaTPEventListener(this);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (buchen) {
                    setzeFelderMitBuchung();
                } else {
                    setzeFelderOhneBuchung();
                }

            }
        });
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                setzeFocus();
            }
        });

    }

    private void setzeFocus() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                tfs[3].requestFocus();
            }
        });
    }

    private void setzeFelderMitBuchung() {
        tfs[0].setText(hmRezgeb.get("<rgreznum>"));
        tfs[1].setText(hmRezgeb.get("<rgdatum>"));
        tfs[2].setText(hmRezgeb.get("<rgbetrag>"));
        tfs[3].setText(hmRezgeb.get("<rgpauschale>"));
        tfs[4].setText(hmRezgeb.get("<rgbehandlung>"));
    }

    private void setzeFelderOhneBuchung() {
        try {
            String cmd = "select reznr,rdatum,rgbetrag,rpbetrag from rgaffaktura where reznr='"
                    + hmRezgeb.get("<rgreznum>") + "' and rnr like 'RGR-%' LIMIT 1";
            Vector<Vector<String>> vec = SqlInfo.holeFelder(cmd);
            if (vec.size() <= 0) {
                JOptionPane.showMessageDialog(null,
                        "Diese Rezeptgebührrechnung ist nicht in der Tabelle rgaffaktura erfaßt");
                return;
            }

            tfs[0].setText(vec.get(0)
                              .get(0));
            tfs[1].setText(DatFunk.sDatInDeutsch(vec.get(0)
                                                    .get(1)));
            tfs[2].setText(vec.get(0)
                              .get(2));
            tfs[3].setText(vec.get(0)
                              .get(3));
            tfs[4].setText(hmRezgeb.get("<rgbehandlung>"));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Fehler beim Bezug der Daten für Rezeptgebührrechnung");
        }
    }

    private JXPanel getContent() {
        content = new JXPanel(new BorderLayout());
        content.add(getFields(), BorderLayout.CENTER);
        content.add(getButtons(), BorderLayout.SOUTH);
        content.addKeyListener(this);
        return content;
    }

    private JXPanel getFields() {
        JXPanel pan = new JXPanel();
        // 1 2 3 4 5 6 7
        FormLayout lay = new FormLayout("5dlu,fill:0:grow(0.5),right:max(40dlu;p),5dlu,60dlu,fill:0:grow(0.5),5dlu",
                // 1 2 3 4 5 6 7 8 9 10 11 12 13
                "5dlu,fill:0:grow(0.5),p,3dlu,p,3dlu,p,3dlu,p,3dlu, p,fill:0:grow(0.5),5dlu");
        pan.setLayout(lay);
        CellConstraints cc = new CellConstraints();
        pan.setOpaque(false);
        JLabel lab = new JLabel("Rezeptnummer");
        pan.add(lab, cc.xy(3, 3));
        tfs[0] = new JRtaTextField("GROSS", true);
        pan.add(tfs[0], cc.xy(5, 3));
        lab = new JLabel("Rezeptdatum");
        pan.add(lab, cc.xy(3, 5));
        tfs[1] = new JRtaTextField("DATUM", true);
        pan.add(tfs[1], cc.xy(5, 5));
        lab = new JLabel("Rezeptgebühr");
        lab.setForeground(Color.RED);
        pan.add(lab, cc.xy(3, 7));
        tfs[2] = new JRtaTextField("FL", true, "6.2", "RECHTS");
        tfs[2].setupFormat(2);
        tfs[2].setDValueFromS("0,00");
        pan.add(tfs[2], cc.xy(5, 7));
        lab = new JLabel("Bearbeitungsgebühr");
        lab.setForeground(Color.RED);
        pan.add(lab, cc.xy(3, 9));
        tfs[3] = new JRtaTextField("FL", true, "6.2", "RECHTS");
        tfs[3].setDValueFromS("0,00");
        tfs[3].setupFormat(2);
        pan.add(tfs[3], cc.xy(5, 9));
        lab = new JLabel("Behandlungen");
        pan.add(lab, cc.xy(3, 11));
        tfs[4] = new JRtaTextField("NIX", true);
        pan.add(tfs[4], cc.xy(5, 11));
        return pan;
    }

    private JXPanel getButtons() {
        JXPanel pan = new JXPanel();
        pan.setOpaque(false);
        FormLayout lay = new FormLayout("5dlu,fill:0:grow(0.5),50dlu,10dlu,50dlu,fill:0:grow(0.5),5dlu",
                // 1 2 3 4 5 6 7 8 9 10 11 12
                "5dlu,fill:0:grow(0.5),p,fill:0:grow(0.5),5dlu");
        pan.setLayout(lay);
        CellConstraints cc = new CellConstraints();
        JButton okButton = macheBut("Ok", "ok", e -> doRgRechnungPrepare());
        pan.add(okButton, cc.xy(3, 3));
        JButton abbrechenButton = macheBut("abbrechen", "abbrechen", e -> fensterSchliessen("dieses"));
        pan.add((abbrechenButton), cc.xy(5, 3));
        return pan;
    }

    private JButton macheBut(String titel, String cmd, ActionListener listener) {
        JButton but = new JButton(titel);
        but.setName(cmd);
        but.setActionCommand(cmd);
        but.addActionListener(listener);
        return but;
    }

    private void doRgRechnungPrepare() {
        double rezgeb = Double.parseDouble(tfs[2].getText()
                                                 .replace(",", "."))
                + Double.parseDouble(tfs[3].getText()
                                           .replace(",", "."));
        hmRezgeb.put("<rggesamt>", dcf.format(rezgeb));
        hmRezgeb.put("<rgbetrag>", dcf.format(Double.parseDouble(tfs[2].getText()
                                                                       .replace(",", "."))));
        hmRezgeb.put("<rgpauschale>", dcf.format(Double.parseDouble(tfs[3].getText()
                                                                          .replace(",", "."))));
        if (this.buchen) {
            hmRezgeb.put("<rgnr>", "RGR-" + Integer.toString(SqlInfo.erzeugeNummer("rgrnr")));
        } else {
            hmRezgeb.put("<rgnr>", SqlInfo.holeEinzelFeld("select rnr from rgaffaktura where reznr='"
                    + hmRezgeb.get("<rgreznum>") + "' and rnr like 'RGR-%' LIMIT 1"));
        }
        hmRezgeb.put("<rgbehandlung>", tfs[4].getText()
                                             .trim());
        String url = Path.Instance.getProghome() + "vorlagen/" + Reha.getAktIK() + "/RezeptgebuehrRechnung.ott";
        try {
            officeStarten(url);
        } catch (OfficeApplicationException e) {
            e.printStackTrace();
        } catch (NOAException e) {
            e.printStackTrace();
        } catch (TextException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        if (this.buchen) {
            buchungStarten();
            AktuelleRezepte.setZuzahlImageActRow(ZZStat.ZUZAHLRGR, hmRezgeb.get("<rgreznum>"));
        } else {
            buchungUpdaten();
        }
        fensterSchliessen("dieses");
    }

    private void buchungStarten() {

        StringBuffer buf = new StringBuffer();
        buf.append("insert into rgaffaktura set ");
        buf.append("rnr='" + hmRezgeb.get("<rgnr>") + "', ");
        buf.append("reznr='" + hmRezgeb.get("<rgreznum>") + "', ");
        buf.append("pat_intern='" + hmRezgeb.get("<rgpatintern>") + "', ");
        buf.append("rgesamt='" + hmRezgeb.get("<rggesamt>")
                                         .replace(",", ".")
                + "', ");
        buf.append("roffen='" + hmRezgeb.get("<rggesamt>")
                                        .replace(",", ".")
                + "', ");
        buf.append("rgbetrag='" + hmRezgeb.get("<rgbetrag>")
                                          .replace(",", ".")
                + "', ");
        buf.append("rpbetrag='" + hmRezgeb.get("<rgpauschale>")
                                          .replace(",", ".")
                + "', ");
        buf.append("rdatum='" + DatFunk.sDatInSQL(DatFunk.sHeute()) + "',");
        buf.append("ik='" + Reha.getAktIK() + "'");
        SqlInfo.sqlAusfuehren(buf.toString());

    }

    private void buchungUpdaten() {
        // Lemmi Doku: Schreibt die RGR-Buchung in die Datei "rgaffaktura"
        StringBuffer buf = new StringBuffer();
        buf.append("update rgaffaktura set ");
        buf.append("rnr='" + hmRezgeb.get("<rgnr>") + "', ");
        buf.append("reznr='" + hmRezgeb.get("<rgreznum>") + "', ");
        buf.append("pat_intern='" + hmRezgeb.get("<rgpatintern>") + "', ");
        buf.append("rgesamt='" + hmRezgeb.get("<rggesamt>")
                                         .replace(",", ".")
                + "', ");
        buf.append("roffen='" + hmRezgeb.get("<rggesamt>")
                                        .replace(",", ".")
                + "', ");
        buf.append("rgbetrag='" + hmRezgeb.get("<rgbetrag>")
                                          .replace(",", ".")
                + "', ");
        buf.append("rpbetrag='" + hmRezgeb.get("<rgpauschale>")
                                          .replace(",", ".")
                + "', ");
        buf.append("rdatum='" + DatFunk.sDatInSQL(DatFunk.sHeute()) + "',");
        buf.append("ik='" + Reha.getAktIK() + "'");
        buf.append(" where rnr='" + hmRezgeb.get("<rgnr>") + "' LIMIT 1");
        SqlInfo.sqlAusfuehren(buf.toString());
    }

    private synchronized void officeStarten(String url)
            throws OfficeApplicationException, NOAException, TextException, DocumentException {
        IDocumentService documentService = null;
        Reha.getThisFrame()
            .setCursor(Cursors.wartenCursor);


        documentService = Reha.officeapplication.getDocumentService();

        IDocumentDescriptor docdescript = new DocumentDescriptor();
        docdescript.setHidden(true);
        docdescript.setAsTemplate(true);
        final ITextDocument textDocument = (ITextDocument) documentService.loadDocument(url, docdescript);
        /**********************/
        OOTools.druckerSetzen(textDocument, SystemConfig.hmAbrechnung.get("hmgkvrechnungdrucker"));
        /**********************/
        ITextFieldService textFieldService = textDocument.getTextFieldService();
        ITextField[] placeholders = textFieldService.getPlaceholderFields();
        String placeholderDisplayText = "";

        Map<String, String> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        map.putAll(hmRezgeb);



        for (ITextField placeHolder : placeholders) {
            placeholderDisplayText = placeHolder.getDisplayText()
                                                .toLowerCase();
            placeHolder.getTextRange()
                       .setText(map.get(placeholderDisplayText));
        }
        try {
            ITextTable[] textTables = textDocument.getTextTableService()
                                                  .getTextTables();
            for (ITextTable iTextTable : textTables) {
                if ("Tabelle-RGR".equals(iTextTable.getName())) {

                    ITextTable textTable = textDocument.getTextTableService()
                                                       .getTextTable("Tabelle-RGR");
                    int anzpos = Integer.parseInt(hmRezgeb.get("<rganzpos>"));
                    for (int position = 1; position <= anzpos; position++) {
                        setCellText(textTable,0, position, "rglangtext");
                        setCellText(textTable,1, position , "rganzahl");
                        setCellText(textTable,2, position, "rggesamt");

                        textTable.addRow(1);
                    }

                }
            }
        } catch (Exception e) {

            LOGGER.error("Fehler in DetailTabelle: ", e);
        }
        boolean immerInOfficeOeffnen = "1".equals(SystemConfig.hmAbrechnung.get("hmallinoffice"));
        if (immerInOfficeOeffnen) {
            textDocument.getFrame()
                        .getXFrame()
                        .getContainerWindow()
                        .setVisible(true);

        } else {
            PrintProperties printprop = new PrintProperties(anzahlKopien());
            textDocument.getPrintService()
                        .print(printprop);

            if (textDocument.isOpen()) {
                textDocument.close();
            }
        }

    }

    private void setCellText(ITextTable textTable, int column  , int position, String string) throws TextException {
        textTable.getCell(column, position)
                 .getTextService()
                 .getText()
                 .setText(hmRezgeb.get("<"
                         + string + String.valueOf(position) + ">"));
    }

    private short anzahlKopien() {
        String anzahlKopien = SystemConfig.hmAbrechnung.get("rgrdruckanzahl");
        int exemplare = 2;
        if (anzahlKopien != null) {

            exemplare = Integer.parseInt(anzahlKopien);
        }
        return (short) exemplare;
    }

    @Override
    public void focusGained(FocusEvent arg0) {

    }

    @Override
    public void focusLost(FocusEvent arg0) {

    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        String cmd = arg0.getActionCommand();
        if (cmd.equals("abbrechen")) {
            fensterSchliessen("dieses");
        } else {
            doRgRechnungPrepare();
        }

    }

    @Override
    public void mouseClicked(MouseEvent arg0) {

    }

    @Override
    public void mouseEntered(MouseEvent arg0) {

    }

    @Override
    public void mouseExited(MouseEvent arg0) {

    }

    @Override
    public void mousePressed(MouseEvent arg0) {

    }

    @Override
    public void mouseReleased(MouseEvent arg0) {

    }

    @Override
    public void keyPressed(KeyEvent arg0) {
        if (arg0.getKeyCode() == KeyEvent.VK_ESCAPE) {
            fensterSchliessen("dieses");
            return;
        }
        if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
            if (((JComponent) arg0.getSource()) instanceof JButton) {
                if (((JComponent) arg0.getSource()).getName()
                                                   .equals("abbrechen")) {
                    fensterSchliessen("dieses");
                    return;
                } else if (((JComponent) arg0.getSource()).getName()
                                                          .equals("ok")) {
                    doRgRechnungPrepare();
                }
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent arg0) {

    }

    @Override
    public void keyTyped(KeyEvent arg0) {

    }

    @Override
    public void rehaTPEventOccurred(RehaTPEvent evt) {
        fensterSchliessen("dieses");

    }

    public void fensterSchliessen(String welches) {
        this.jtp.removeMouseListener(this.mymouse);
        this.jtp.removeMouseMotionListener(this.mymouse);
        this.content.removeKeyListener(this);
        this.mymouse = null;
        if (this.rtp != null) {
            this.rtp.removeRehaTPEventListener(this);
            this.rtp = null;
        }
        this.pinPanel = null;
        setVisible(false);
        this.dispose();
    }

}
