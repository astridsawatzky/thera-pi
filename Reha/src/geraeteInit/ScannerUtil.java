package geraeteInit;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import org.jdesktop.swingx.JXPanel;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import CommonTools.JRtaCheckBox;
import CommonTools.JRtaComboBox;
import CommonTools.ini.INITool;
import CommonTools.ini.Settings;
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
import dialoge.PinPanel;
import dialoge.RehaSmartDialog;
import environment.Path;
import events.RehaTPEvent;
import events.RehaTPEventClass;
import hauptFenster.Reha;
import office.OOService;
import office.OOTools;
import systemEinstellungen.SystemConfig;
import uk.co.mmscomputing.device.scanner.Scanner;
import uk.co.mmscomputing.device.scanner.ScannerIOException;
import umfeld.Betriebsumfeld;

public class ScannerUtil extends RehaSmartDialog implements ActionListener {
    /**
     *
     */
    private static final long serialVersionUID = 7878966572279505811L;

    public JRtaCheckBox[] leistung = { null, null, null, null };

    private RehaTPEventClass rtp = null;
    private ScannerUtilHintergrund rgb;

    public JButton uebernahme;
    public JButton abbrechen;

    public JRtaComboBox[] jcmbscan = { null, null, null, null, null };
    public JRtaCheckBox[] jcbscan = { null, null, null, null, null };
    Scanner scanner;

    public ScannerUtil(Point pt) {
        super(null, "ScannerUtil");

        pinPanel = new PinPanel();
        pinPanel.setName("ScannerUtil");
        pinPanel.getGruen()
                .setVisible(false);
        setPinPanel(pinPanel);
        getSmartTitledPanel().setTitle("Scanner-Einstellung");

        setSize(400, 350);
        setPreferredSize(new Dimension(400, 350));
        getSmartTitledPanel().setPreferredSize(new Dimension(400, 350));
        setPinPanel(pinPanel);
        rgb = new ScannerUtilHintergrund();
        rgb.setLayout(new BorderLayout());

        new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground() throws Exception {
                rgb.setBackgroundPainter(Reha.instance.compoundPainter.get("ScannerUtil"));
                return null;
            }

        }.execute();
        rgb.add(getGebuehren(), BorderLayout.CENTER);

        getSmartTitledPanel().setContentContainer(rgb);
        getSmartTitledPanel().getContentContainer()
                             .setName("ScannerUtil");
        setName("ScannerUtil");
        setModal(true);
        Point lpt = new Point(pt.x - 150, pt.y + 30);
        setLocation(lpt);

        rtp = new RehaTPEventClass();
        rtp.addRehaTPEventListener(this);

        new SwingWorker<String, String>() {
            @Override
            protected String doInBackground() throws Exception {
                scanner = Scanner.getDevice();
                try {
                    String[] names = scanner.getDeviceNames();
                    for (int i = 0; i < names.length; i++) {
                        jcmbscan[0].addItem(names[i]);
                    }
                    if (!scanner.getSelectedDeviceName()
                                .equals(SystemConfig.sDokuScanner)) {
                        jcmbscan[0].setSelectedItem(scanner.getSelectedDeviceName());
                    } else {
                        jcmbscan[0].setSelectedItem(SystemConfig.sDokuScanner);
                    }

                } catch (ScannerIOException e2) {
                    e2.printStackTrace();
                }
                return null;
            }

        }.execute();

        pack();

    }

    /****************************************************/

    private JPanel getGebuehren() { // 1 2 3 4 5 6 7
        FormLayout lay = new FormLayout("40dlu, right:max(70dlu;p),5dlu,p,fill:0:grow(1.00)",
                // 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16
                "30dlu,p,3dlu, p,3dlu, p, 3dlu, p, 10dlu, p,  10dlu, p,  20dlu,40dlu,fill:0:grow(1.00),40dlu ");
        PanelBuilder pb = new PanelBuilder(lay);
        CellConstraints cc = new CellConstraints();
        pb.getPanel()
          .setOpaque(false);

        pb.addLabel("installierte Geräte", cc.xy(2, 2));
        jcmbscan[0] = new JRtaComboBox();
        pb.add(jcmbscan[0], cc.xy(4, 2));

        pb.addLabel("Scanmodus", cc.xy(2, 4));
        jcmbscan[1] = new JRtaComboBox(new String[] { "Schwarz/Weiß", "Graustufen", "Farbe" });
        jcmbscan[1].setSelectedItem(SystemConfig.hmDokuScanner.get("farben"));
        pb.add(jcmbscan[1], cc.xy(4, 4));

        pb.addLabel("Auflösung", cc.xy(2, 6));
        jcmbscan[2] = new JRtaComboBox(new String[] { "50dpi", "75dpi", "100dpi", "150dpi", "200dpi", "300dpi" });
        jcmbscan[2].setSelectedItem(SystemConfig.hmDokuScanner.get("aufloesung") + "dpi");
        pb.add(jcmbscan[2], cc.xy(4, 6));

        pb.addLabel("Seitenformat", cc.xy(2, 8));
        // jcmbscan[3] = new JRtaComboBox(new String[]{"Din A6","Din A6-quer","Din
        // A5","Din A5-quer","Din A4","Din A4-quer"});
        jcmbscan[3] = new JRtaComboBox(new String[] { "Din A6", "Din A5", "Din A4", "angepasst" });
        jcmbscan[3].setSelectedItem(SystemConfig.hmDokuScanner.get("seiten"));
        pb.add(jcmbscan[3], cc.xy(4, 8));

        pb.addLabel("Scandialog", cc.xy(2, 10));
        jcbscan[0] = new JRtaCheckBox("verwenden");
        jcbscan[0].setOpaque(false);
        jcbscan[0].setSelected((SystemConfig.hmDokuScanner.get("dialog")
                                                          .equals("1") ? true : false));
        pb.add(jcbscan[0], cc.xy(4, 10));

        pb.addLabel("Einstellungen als", cc.xy(2, 12));
        jcbscan[1] = new JRtaCheckBox("Standard verwenden");
        jcbscan[1].setOpaque(false);
        pb.add(jcbscan[1], cc.xy(4, 12));

        JXPanel jpan = new JXPanel();
        // jpan.setBackground(Color.RED);
        jpan.setOpaque(false);
        pb.add(jpan, cc.xywh(1, 14, 5, 2));

        FormLayout lay2 = new FormLayout("fill:0:grow(0.33),80dlu,fill:0:grow(0.33),80dlu,fill:0:grow(0.33)",
                "fill:0:grow(0.50),p,fill:0:grow(0.50)");
        CellConstraints cc2 = new CellConstraints();
        PanelBuilder pb2 = new PanelBuilder(lay2);
        pb2.getPanel()
           .setOpaque(false);
        uebernahme = new JButton("übernehmen");
        uebernahme.setActionCommand("uebernahme");
        uebernahme.addActionListener(this);
        uebernahme.addKeyListener(this);
        abbrechen = new JButton("abbrechen");
        abbrechen.setActionCommand("abbrechen");
        abbrechen.addActionListener(this);
        abbrechen.addKeyListener(this);
        pb2.add(uebernahme, cc2.xy(2, 2));
        pb2.add(abbrechen, cc2.xy(4, 2));
        pb.add(pb2.getPanel(), cc.xyw(1, 14, 5));

        pb.getPanel()
          .validate();
        return pb.getPanel();
    }

    /****************************************************/

    @Override
    public void rehaTPEventOccurred(RehaTPEvent evt) {
        try {
            if (evt.getDetails()[0] != null) {
                if (evt.getDetails()[0].equals(this.getName())) {
                    this.setVisible(false);
                    rtp.removeRehaTPEventListener(this);
                    rtp = null;
                    uebernahme.removeActionListener(this);
                    abbrechen.removeActionListener(this);
                    rgb = null;
                    this.dispose();
                    super.dispose();
                    // System.out.println("****************Scanner-Util -> Listener
                    // entfernt**************");
                }
            }
        } catch (NullPointerException ne) {
            // System.out.println("In PatNeuanlage" +evt);
        }
    }

    @Override
    public void windowClosed(WindowEvent arg0) {
        if (rtp != null) {
            this.setVisible(false);
            rtp.removeRehaTPEventListener(this);
            rtp = null;
            pinPanel = null;
            dispose();
            super.dispose();
            // System.out.println("****************Scanner-Util -> Listener entfernt
            // (Closed)**********");
        }
        scanner = null;

    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        if (arg0.getActionCommand()
                .equals("uebernahme")) {
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {

                    return null;
                }
            }.execute();
            doSpeichernScanner();
            this.dispose();
            /********
             *
             * Hier noch schnell buchen entwickeln und feddisch...
             *
             */
        }
        if (arg0.getActionCommand()
                .equals("abbrechen")) {
            this.dispose();
        }

    }

    private void doSpeichernScanner() {
        String item = "";
        if (jcbscan[1].isSelected()) {
            Settings inif = INITool.openIni(Path.Instance.getProghome() + "ini/" + Betriebsumfeld.getAktIK() + "/", "geraete.ini");

            item = (String) jcmbscan[0].getSelectedItem();
            SystemConfig.sDokuScanner = item;
            inif.setStringProperty("DokumentenScanner", "DokumentenScannerName", item, null);

            item = (String) jcmbscan[1].getSelectedItem();
            SystemConfig.hmDokuScanner.put("farben", item);
            inif.setStringProperty("DokumentenScanner", "DokumentenScannerFarben", item, null);

            item = (String) jcmbscan[2].getSelectedItem();
            SystemConfig.hmDokuScanner.put("aufloesung", item.replaceAll("dpi", ""));
            inif.setStringProperty("DokumentenScanner", "DokumentenScannerAufloesung", item.replaceAll("dpi", ""),
                    null);

            item = (String) jcmbscan[3].getSelectedItem();
            inif.setStringProperty("DokumentenScanner", "DokumentenScannerSeiten", item, null);
            SystemConfig.hmDokuScanner.put("seiten", item);

            item = (jcbscan[0].isSelected() ? "1" : "0");
            SystemConfig.hmDokuScanner.put("dialog", item);
            inif.setStringProperty("DokumentenScanner", "DokumentenScannerDialog", item, null);
            INITool.saveIni(inif);
        } else {
            item = (String) jcmbscan[0].getSelectedItem();
            SystemConfig.sDokuScanner = item;

            item = (String) jcmbscan[1].getSelectedItem();
            SystemConfig.hmDokuScanner.put("farben", item);

            item = (String) jcmbscan[2].getSelectedItem();
            SystemConfig.hmDokuScanner.put("aufloesung", item.replaceAll("dpi", ""));

            item = (String) jcmbscan[3].getSelectedItem();
            SystemConfig.hmDokuScanner.put("seiten", item);

            item = (jcbscan[0].isSelected() ? "1" : "0");
            SystemConfig.hmDokuScanner.put("dialog", item);
        }
        // this.dispose();

    }

    @Override
    public void keyPressed(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.VK_ENTER) {
            event.consume();
            if (((JComponent) event.getSource()).getName()
                                                .equals("uebernahme")) {
                // doUebernahme();
            }
            if (((JComponent) event.getSource()).getName()
                                                .equals("abbrechen")) {
                this.dispose();
            }

            // System.out.println("Return Gedrückt");
        }
    }

    public static void starteAusfallRechnung(String url) {
        IDocumentService documentService = null;
        // System.out.println("Starte Datei -> "+url);
        try {
            documentService = new OOService().getOfficeapplication().getDocumentService();
        } catch (OfficeApplicationException e) {
            e.printStackTrace();
        }
        IDocumentDescriptor docdescript = new DocumentDescriptor();
        docdescript.setHidden(false);
        docdescript.setAsTemplate(true);
        IDocument document = null;
        // ITextTable[] tbl = null;
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
            // boolean loeschen = false;
            boolean schonersetzt = false;
            String placeholderDisplayText = placeholders[i].getDisplayText()
                                                           .toLowerCase();
            //// System.out.println(placeholderDisplayText);
            /*****************/
            Set<?> entries = SystemConfig.hmAdrPDaten.entrySet();
            Iterator<?> it = entries.iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                if (((String) entry.getKey()).toLowerCase()
                                             .equals(placeholderDisplayText)) {
                    placeholders[i].getTextRange()
                                   .setText(((String) entry.getValue()));
                    schonersetzt = true;
                    break;
                }
            }
            /*****************/
            entries = SystemConfig.hmAdrAFRDaten.entrySet();
            it = entries.iterator();
            while (it.hasNext() && (!schonersetzt)) {
                Map.Entry entry = (Map.Entry) it.next();
                if (((String) entry.getKey()).toLowerCase()
                                             .equals(placeholderDisplayText)) {
                    placeholders[i].getTextRange()
                                   .setText(((String) entry.getValue()));
                    schonersetzt = true;
                    break;
                }
            }
            if (!schonersetzt) {
                OOTools.loescheLeerenPlatzhalter(textDocument, placeholders[i]);
            }
            /*****************/
        }

    }

}

class ScannerUtilHintergrund extends JXPanel {
    /**
     *
     */
    private static final long serialVersionUID = -2862600734998377499L;
    ImageIcon hgicon;
    int icx, icy;
    AlphaComposite xac1 = null;
    AlphaComposite xac2 = null;

    public ScannerUtilHintergrund() {
        super();
        hgicon = SystemConfig.hmSysIcons.get("scannergross");
        icx = hgicon.getIconWidth() / 2;
        icy = hgicon.getIconHeight() / 2;
        xac1 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.15f);
        xac2 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f);

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
}
