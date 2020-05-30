package opRgaf;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXPanel;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import CommonTools.DatFunk;
import CommonTools.JRtaCheckBox;
import CommonTools.LeistungTools;
import CommonTools.OOTools;
import CommonTools.SqlInfo;
import ag.ion.bion.officelayer.application.OfficeApplicationException;
import ag.ion.bion.officelayer.document.DocumentDescriptor;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.document.IDocumentDescriptor;
import ag.ion.bion.officelayer.document.IDocumentService;
import ag.ion.bion.officelayer.text.ITextDocument;
import ag.ion.bion.officelayer.text.ITextField;
import ag.ion.bion.officelayer.text.ITextFieldService;
import ag.ion.bion.officelayer.text.TextException;
import ag.ion.noa.NOAException;
import office.OOService;

class AusfallRechnung extends JDialog implements WindowListener, ActionListener, KeyListener {
    private static final long serialVersionUID = 1L;

    private JRtaCheckBox[] leistung = { null, null, null, null, null };


    private JButton uebernahme;

    private JButton abbrechen;

    private String afrNummer;

    private String afrDatum;

    private Vector<String> vecaktrez = null;

    private Vector<String> patDaten = null;

    AusfallRechnung(Point pt, String pat_intern, String rez_nr, String rnummer, String rdatum) {

        super();
        afrNummer = rnummer;
        afrDatum = rdatum;
        setTitle("Kopie Ausfallrechnung erstellen");

        setSize(350, 270);
        setPreferredSize(new Dimension(350, 270));

        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        String test = SqlInfo.holeEinzelFeld("select id from verordn where rez_nr = '" + rez_nr + "' LIMIT 1");
        if (test.equals("")) {
            test = SqlInfo.holeEinzelFeld("select id from lza where rez_nr = '" + rez_nr + "' LIMIT 1");
            if (test.equals("")) {
                // this.dispose();
                // return;
            } else {
                vecaktrez = SqlInfo.holeSatz("lza", " * ", "id='" + test + "'", Arrays.asList(new String[] {}));
            }
        } else {
            vecaktrez = SqlInfo.holeSatz("verordn", " * ", "id='" + test + "'", Arrays.asList(new String[] {}));
        }

        patDaten = SqlInfo.holeSatz("pat5", " * ", "pat_intern='" + pat_intern + "'", Arrays.asList(new String[] {}));
        InitHashMaps.constructPatHMap(patDaten);
        this.add(getGebuehren());

        setName("Kopie - AusfallRechnung");
        setModal(true);
        this.setLocationRelativeTo(null);

        pack();

    }

    private JPanel getGebuehren() { // 1 2 3 4 5 6 7
        FormLayout lay = new FormLayout("10dlu,fill:0:grow(0.50),right:80dlu,10dlu,80dlu,fill:0:grow(0.50),10dlu",
                // 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15
                "15dlu,p,10dlu,p,2dlu,p,2dlu,p,2dlu,p, 10dlu, p,  20dlu, p ,20dlu");
        PanelBuilder pb = new PanelBuilder(lay);
        CellConstraints cc = new CellConstraints();
        try {
            pb.getPanel()
              .setOpaque(false);

            pb.addLabel("Bitte die Positionen auswählen die Sie berechnen wollen", cc.xyw(2, 2, 4));

            pb.addLabel("Heilmittel 1", cc.xy(3, 4));
            String lab = vecaktrez.get(48);
            leistung[0] = new JRtaCheckBox((lab.equals("") ? "----" : lab));
            leistung[0].setOpaque(false);
            if (!lab.equals("")) {
                leistung[0].setSelected(true);
            } else {
                leistung[0].setSelected(false);
                leistung[0].setEnabled(false);
            }
            pb.add(leistung[0], cc.xyw(5, 4, 2));

            pb.addLabel("Heilmittel 2", cc.xy(3, 6));
            lab = vecaktrez.get(49);
            leistung[1] = new JRtaCheckBox((lab.equals("") ? "----" : lab));
            leistung[1].setOpaque(false);
            if (!lab.equals("")) {

            } else {
                leistung[1].setSelected(false);
                leistung[1].setEnabled(false);
            }
            pb.add(leistung[1], cc.xyw(5, 6, 2));

            pb.addLabel("Heilmittel 3", cc.xy(3, 8));
            lab = vecaktrez.get(50);
            leistung[2] = new JRtaCheckBox((lab.equals("") ? "----" : lab));
            leistung[2].setOpaque(false);
            if (!lab.equals("")) {

            } else {
                leistung[2].setSelected(false);
                leistung[2].setEnabled(false);
            }
            pb.add(leistung[2], cc.xyw(5, 8, 2));

            pb.addLabel("Heilmittel 4", cc.xy(3, 10));
            lab = vecaktrez.get(51);
            leistung[3] = new JRtaCheckBox((lab.equals("") ? "----" : lab));
            leistung[3].setOpaque(false);
            if (!lab.equals("")) {

            } else {
                leistung[3].setSelected(false);
                leistung[3].setEnabled(false);
            }
            pb.add(leistung[3], cc.xyw(5, 10, 2));

            /*
             * pb.addLabel("Eintragen in Memo",cc.xy(3, 12)); leistung[4] = new
             * JRtaCheckBox("Fehldaten"); leistung[4].setOpaque(false);
             * leistung[4].setSelected(true); pb.add(leistung[4],cc.xyw(5, 12, 2));
             */

            uebernahme = new JButton("Kopie erstellen");
            uebernahme.setActionCommand("uebernahme");
            uebernahme.addActionListener(this);
            uebernahme.addKeyListener(this);
            pb.add(uebernahme, cc.xy(3, 14));

            abbrechen = new JButton("abbrechen");
            abbrechen.setActionCommand("abbrechen");
            abbrechen.addActionListener(this);
            abbrechen.addKeyListener(this);
            pb.add(abbrechen, cc.xy(5, 14));

            pb.getPanel()
              .validate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return pb.getPanel();
    }

    @Override
    public void windowClosed(WindowEvent arg0) {

    }

    @Override
    public void actionPerformed(ActionEvent arg0) {

        if (arg0.getActionCommand()
                .equals("uebernahme")) {
            macheAFRHmap();
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    try {
                        String url = OpRgaf.proghome + "vorlagen/" + OpRgaf.aktIK + "/AusfallRechnung.ott.Kopie.ott";
                        if(new File(url).exists()) {
                            starteAusfallRechnung(
                                    url);
                        } else {
                            JOptionPane.showMessageDialog(null, "Vorlage " + url + " konnte nicht gefunden werden");
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Fehler bei der Erstellung der Ausfallrechnung");
                    }
                    getInstance().dispose();
                    return null;
                }
            }.execute();
            /*
             * new SwingWorker<Void,Void>(){
             *
             * @Override protected Void doInBackground() throws Exception {
             * if(leistung[4].isSelected()){ macheMemoEintrag(); } return null; }
             * }.execute(); this.dispose();
             */
        }
        if (arg0.getActionCommand()
                .equals("abbrechen")) {
            this.dispose();
        }

    }

    private AusfallRechnung getInstance() {
        return this;
    }

    /** XXX: never used why ? */
    private void doBuchen() {
        StringBuffer buf = new StringBuffer();
        buf.append("insert into rgaffaktura set ");
        buf.append("rnr='" + afrNummer + "', ");
        buf.append("reznr='" + vecaktrez.get(1) + "', ");
        buf.append("pat_intern='" + vecaktrez.get(0) + "', ");
        buf.append("rgesamt='" + InitHashMaps.hmAdrAFRDaten.get("<AFRgesamt>")
                                                           .replace(",", ".")
                + "', ");
        buf.append("roffen='" + InitHashMaps.hmAdrAFRDaten.get("<AFRgesamt>")
                                                          .replace(",", ".")
                + "', ");
        buf.append("rdatum='" + DatFunk.sDatInSQL(DatFunk.sHeute()) + "'");
        SqlInfo.sqlAusfuehren(buf.toString());
    }

    private void macheAFRHmap() {
        String mappos = "";
        String mappreis = "";
        String mapkurz = "";
        String maplang = "";
        String[] inpos = { null, null };
        String spos = "";
        String sart = "";
        Double gesamt = new Double(0.00);
        int preisgruppe = 0;
        DecimalFormat df = new DecimalFormat("0.00");

        for (int i = 0; i < 4; i++) {
            mappos = "<AFRposition" + (i + 1) + ">";
            mappreis = "<AFRpreis" + (i + 1) + ">";
            mapkurz = "<AFRkurz" + (i + 1) + ">";
            maplang = "<AFRlang" + (i + 1) + ">";
            if (leistung[i].isSelected()) {
                Double preis = new Double(vecaktrez.get(18 + i));
                String s = df.format(preis);
                InitHashMaps.hmAdrAFRDaten.put(mappos, leistung[i].getText());
                InitHashMaps.hmAdrAFRDaten.put(mappreis, s);
                gesamt = gesamt + preis;

                spos = vecaktrez.get(8 + i);
                sart = vecaktrez.get(1);
                sart = sart.substring(0, 2);
                preisgruppe = Integer.parseInt(vecaktrez.get(41));
                inpos = LeistungTools.getLeistungRaw(sart, spos, preisgruppe);
                InitHashMaps.hmAdrAFRDaten.put(maplang, String.valueOf(inpos[0]));
                InitHashMaps.hmAdrAFRDaten.put(mapkurz, String.valueOf(inpos[1]));
                //// System.out.println(inpos[0]);
                //// System.out.println(inpos[1]);

            } else {
                spos = vecaktrez.get(8 + i);
                sart = vecaktrez.get(1);
                sart = sart.substring(0, 2);
                preisgruppe = Integer.parseInt(vecaktrez.get(41));
                if (spos.equals("0") || spos.equals("")) {
                    inpos[0] = "";
                    inpos[1] = "";
                } else {
                    inpos = LeistungTools.getLeistungRaw(sart, spos, preisgruppe);
                }
                InitHashMaps.hmAdrAFRDaten.put(mappos, leistung[i].getText());
                InitHashMaps.hmAdrAFRDaten.put(mappreis, "0,00");
                InitHashMaps.hmAdrAFRDaten.put(maplang, (!inpos[0].equals("") ? inpos[0] : "----"));
                InitHashMaps.hmAdrAFRDaten.put(mapkurz, (!inpos[1].equals("") ? inpos[1] : "----"));

            }

        }
        InitHashMaps.hmAdrAFRDaten.put("<AFRgesamt>", df.format(gesamt));

        InitHashMaps.hmAdrAFRDaten.put("<AFRnummer>", afrNummer);
        try {
            InitHashMaps.hmAdrAFRDaten.put("<AFRdatum>", DatFunk.sDatInDeutsch(afrDatum));
        } catch (Exception ex) {
        }
    }

    @Override
    public void keyPressed(KeyEvent event) {
        if (event.getKeyCode() == 10) {
            event.consume();
            if (((JComponent) event.getSource()).getName()
                                                .equals("uebernahme")) {
            }
            if (((JComponent) event.getSource()).getName()
                                                .equals("abbrechen")) {
                this.dispose();
            }

        }
        if (event.getKeyCode() == 27) {
            this.dispose();
        }
    }

    private static void starteAusfallRechnung(String url) {
        IDocumentService documentService ;

        try {
            documentService = new OOService().getOfficeapplication().getDocumentService();
        } catch (OfficeApplicationException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Fehler im OpenOffice-System - Ausfallrechnung kann nicht erstellt werden");
            return;
        }
        IDocumentDescriptor docdescript = new DocumentDescriptor();
        docdescript.setHidden(false);
        docdescript.setAsTemplate(true);
        IDocument document = null;
        try {
            document = documentService.loadDocument(url, docdescript);
        } catch (NOAException e) {

            e.printStackTrace();
        }
        ITextDocument textDocument = (ITextDocument) document;
        ITextFieldService textFieldService = textDocument.getTextFieldService();
        ITextField[] placeholders = null;
        try {
            placeholders = textFieldService.getPlaceholderFields();
        } catch (TextException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < placeholders.length; i++) {
            boolean schonersetzt = false;
            String placeholderDisplayText = placeholders[i].getDisplayText()
                                                           .toLowerCase();
            Set<Entry<String, String>> patdaten = InitHashMaps.hmAdrPDaten.entrySet();
            Iterator<Entry<String, String>> it = patdaten.iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> entry = it.next();
                if (entry.getKey()
                         .toLowerCase()
                         .equals(placeholderDisplayText)) {
                    placeholders[i].getTextRange()
                                   .setText((entry.getValue()));
                    schonersetzt = true;
                    break;
                }
            }
            Set<Entry<String, String>>  afrDaten = InitHashMaps.hmAdrAFRDaten.entrySet();
            Iterator<Entry<String, String>>   afrDatenIterator = afrDaten.iterator();
            while (afrDatenIterator.hasNext() && (!schonersetzt)) {
                Entry<String, String> entry =  afrDatenIterator.next();
                if ( entry.getKey().toLowerCase()
                                             .equals(placeholderDisplayText)) {
                    placeholders[i].getTextRange()
                                   .setText(entry.getValue());
                    schonersetzt = true;
                    break;
                }
            }
            if (!schonersetzt) {
                OOTools.loescheLeerenPlatzhalter(textDocument, placeholders[i]);
            }
        }

    }

    @Override
    public void windowActivated(WindowEvent arg0) {

    }

    @Override
    public void windowClosing(WindowEvent arg0) {

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

    @Override
    public void keyReleased(KeyEvent arg0) {

    }

    @Override
    public void keyTyped(KeyEvent arg0) {

    }
}

class AusfallRechnungHintergrund extends JXPanel {
    private static final long serialVersionUID = 1L;

    public AusfallRechnungHintergrund() {
        super();
    }
}
