package roogle;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.looks.windows.WindowsTabbedPaneUI;

import CommonTools.DatFunk;
import CommonTools.JRtaCheckBox;
import CommonTools.JRtaRadioButton;
import CommonTools.JRtaTextField;
import dialoge.DragWin;
import dialoge.PinPanel;
import dialoge.RehaSmartDialog;
import events.RehaTPEvent;
import events.RehaTPEventClass;
import hauptFenster.Reha;
import systemEinstellungen.SystemConfig;
import systemTools.ListenerTools;
import systemTools.WinNum;
import terminKalender.KollegenListe;

public class RoogleFenster extends RehaSmartDialog
        implements TableModelListener, FocusListener, ActionListener, ComponentListener, ChangeListener {


    private static final long serialVersionUID = 1L;
    // public static RoogleFenster thisClass;
    private String eigenName = null;
    private RehaTPEventClass rtp = null;
    private JXPanel jcc = null;
    private JTabbedPane tabbedPane = null;
    private JTabbedPane wahltabbedPane = null;
    private JXPanel wahl1 = null;
    private JXPanel wahl2 = null;
    private JXPanel wahl3 = null;
    private JXPanel wahl4 = null;
    private JXPanel tp1 = null;
    private SuchenSeite tp2 = null;
    private JXPanel jpLinks = null;
    private JXPanel jpRechts = null;
    private JScrollPane ptc = null;
    private JXTable jxTable = null;
    private JXTable jxGruppen = null;
    private AbstractTableModel tblDataModel = null;
    private MyRoogleTable1 myTable = null;
    private JRtaCheckBox[] gruppenCheck = { null, null, null, null, null };
    private JComboBox[] gruppenCombo = { null, null, null, null, null };
    private JRtaCheckBox[] zeitraumCheck = { null, null };
    public JRtaTextField[] zeitraumEdit = { null, null };
    public JRtaCheckBox[] tageCheck = { null, null, null, null, null, null, null };
    public JRtaCheckBox[] uhrselectCheck = { null, null, null, null, null };
    private JRtaTextField[] uhrselectEdit = { null, null, null, null, null, null, null, null };
    private JRtaCheckBox[] schichtCheck = { null, null };
    private JRtaRadioButton[] schichtRadio = { null, null, null, null };
    private JRtaTextField[] schichtEdit = { null, null, null, null };
    private ButtonGroup[] schichtGruppe = { null, null };
    private Object[][] kollegenWahl = null;
    private int gewaehlt = 0;
    public static boolean gedropt = false;
    public static String[] sldrops = { null, null, null };
    public static boolean schicht = false;
    public static boolean select = false;
    public String[] kollegenAbteilung = null;
    public boolean[] kollegenSuchen = new boolean[KollegenListe.maxKalZeile + 1];
    private MouseAdapter mymouse = null;
    private JScrollPane js;
    private JScrollPane js2;
    private JScrollPane js3;
    private JScrollPane js4;
    private Logger logger = LoggerFactory.getLogger(RoogleFenster.class);

    public RoogleFenster(JXFrame owner, String drops) {
        super(owner, "Roogle");
        setPreferredSize(new Dimension(300, 300));
        if (!(drops == null)) {
            gedropt = true;
            String[] termdrops = drops.split("°");
            sldrops = new String[] { termdrops[1], termdrops[2], termdrops[3] };

            sldrops[2] = sldrops[2].toUpperCase();
            sldrops[2] = sldrops[2].replaceAll(" MIN.", "");
        } else {
            gedropt = false;
            sldrops = new String[] { null, null, null };
        }
        eigenName = "Roogle" + WinNum.NeueNummer();
        this.setName("Roogle" + WinNum.NeueNummer());

        getSmartTitledPanel().setName(eigenName);
        this.getParent()
            .setName(eigenName);

        this.setModal(true);

        this.setUndecorated(true);

        /** jcc ist die Haupt-JXPanel **/
        this.addFocusListener(this);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        jcc = new JXPanel(new GridLayout(1, 1));
        jcc.setDoubleBuffered(true);
        jcc.setName("contentPanel");
        jcc.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        jcc.addKeyListener(this);
        jcc.addFocusListener(this);

        /** darauf muss eine Tabbedpane gelegt werden (Suchkriterium und suche) ****/
        macheTabedPane1(jcc);
        /** die Tabedpane1 muss mit einer JXPanel belegt werden **/

        /**
         * diese wird unterteilt in links=JXTable und rechts ist neue TabedPane fuer
         * diverse Einstellungen
         **/

        /*****************/
        /*****************/

        this.setContentPanel(jcc);

        getSmartTitledPanel().setTitle("[ R u : g l ] - Die Terminsuchmaschine");
        getSmartTitledPanel().getContentContainer()
                             .setName(eigenName);
        mymouse = new DragWin(this);
        getSmartTitledPanel().addMouseListener(mymouse);
        getSmartTitledPanel().addMouseMotionListener(mymouse);
        pinPanel = new PinPanel();
        pinPanel.getGruen()
                .setVisible(false);
        pinPanel.setName(eigenName);
        pinPanel.setzeName(eigenName);

        setPinPanel(pinPanel);

        rtp = new RehaTPEventClass();
        rtp.addRehaTPEventListener(this);

        this.setName(eigenName);

        this.addWindowListener(this);
        this.addKeyListener(this);

        tabbedPane.setSelectedComponent(tp1);

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                KeyStroke stroke = KeyStroke.getKeyStroke(85, KeyEvent.ALT_MASK);
                jcc.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                   .put(stroke, "doSuchen");
                jcc.getActionMap()
                   .put("doSuchen", new RoogleAction());
                stroke = KeyStroke.getKeyStroke(73, KeyEvent.ALT_MASK);
                jcc.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                   .put(stroke, "doEinstellungen");
                jcc.getActionMap()
                   .put("doEinstellungen", new RoogleAction());
                stroke = KeyStroke.getKeyStroke(70, KeyEvent.ALT_MASK);
                jcc.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                   .put(stroke, "doFocusTable");
                jcc.getActionMap()
                   .put("doFocusTable", new RoogleAction());
                stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
                jcc.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                   .put(stroke, "doSchliessen");
                jcc.getActionMap()
                   .put("doSchliessen", new RoogleAction());
                if (Reha.instance.terminpanel != null) {
                    Reha.instance.terminpanel.setUpdateVerbot(true);
                }


            }
        });
        this.validate();
        setTableSelection(jxTable, 0, 0);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                erstFocus();
            }
        });
    }

    private void erstFocus() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                jxTable.requestFocus();
            }
        });
    }

    public RoogleFenster getInstance() {
        return this;
    }

    public void doSchliessen() {
        this.setVisible(false);
        this.dispose();
    }

    private void macheDrop() {
        if (sldrops[1].indexOf("KG") >= 0) {
            gruppenCombo[0].setSelectedItem(sldrops[2]);
            return;
        }
        if (sldrops[1].indexOf("MA") >= 0) {
            gruppenCombo[1].setSelectedItem(sldrops[2]);
            return;
        }
        if (sldrops[1].indexOf("ER") >= 0) {
            gruppenCombo[2].setSelectedItem(sldrops[2]);
            return;
        }
        if (sldrops[1].indexOf("LO") >= 0) {
            gruppenCombo[3].setSelectedItem(sldrops[2]);
            return;
        }
    }

    private void macheTabedPane1(JXPanel haupt) {
        tabbedPane = new JTabbedPane();
        tabbedPane.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        try {
            tabbedPane.setUI(new WindowsTabbedPaneUI());
        } catch (Exception ex) {
            logger .error("konnte TabbedPaneUI nicht setzen", ex);
        }
        tabbedPane.addKeyListener(this);
        tabbedPane.addChangeListener(this);

        tp1 = new JXPanel(new GridLayout(1, 2));
        tp1.setName("RoogleSeite1");
        tp1.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        tp1.setBackground(Color.WHITE);
        tp1.setDoubleBuffered(true);
        tp1.addKeyListener(this);
        tp1.addFocusListener(this);
        jpLinks = new JXPanel(new GridLayout(1, 1));
        jpLinks.setDoubleBuffered(true);
        jpLinks.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        jpLinks.setBackground(Color.WHITE);

        tblDataModel = new DefaultTableModel();
        jxTable = new JXTable(tblDataModel);
        jxTable.setDoubleBuffered(true);
        jxTable.setEditable(true);
        jxTable.addFocusListener(this);
        new Thread() {
            @Override
            public void run() {
                TabelleSetzen(0);
                jxTable.validate();
            }
        }.start();

        ptc = new JScrollPane();
        ptc.setViewportBorder(null);
        ptc.setViewportView(jxTable);
        ptc.revalidate();
        jpLinks.add(ptc);
        tp1.add(jpLinks);
        jpRechts = new JXPanel(new BorderLayout());
        jpRechts.setDoubleBuffered(true);
        jpRechts.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        jpRechts.setBackground(Color.WHITE);
        JXLabel imglab = new JXLabel();
        imglab.setIcon(SystemConfig.hmSysIcons.get("roogle"));
        JXPanel dummy = new JXPanel();
        dummy.setBackground(Color.WHITE);
        dummy.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        dummy.add(imglab);
        jpRechts.add(dummy, BorderLayout.NORTH);
        wahltabbedPane = new JTabbedPane();
        wahltabbedPane.setUI(new WindowsTabbedPaneUI());

        wahl1 = new JXPanel(new BorderLayout());
        js = new JScrollPane();
        js.setBorder(null);
        js.setDoubleBuffered(true);
        js.setViewportView(wahl1());
        js.validate();
        wahl1.add(js, BorderLayout.CENTER);
        wahl1.validate();

        wahltabbedPane.addTab("Gruppenwahl", SystemConfig.hmSysIcons.get("personen16"), wahl1, "");

        wahl2 = new JXPanel(new BorderLayout());
        js2 = new JScrollPane();
        js2.setBorder(null);
        js2.setDoubleBuffered(true);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                js2.setViewportView(wahl2());
                js2.validate();
                wahl2.add(js2, BorderLayout.CENTER);
            }
        });
        wahltabbedPane.addTab("individueller Zeitraum", SystemConfig.hmSysIcons.get("forward"), wahl2, "");

        wahl3 = new JXPanel(new BorderLayout());
        js3 = new JScrollPane();
        js3.setBorder(null);
        js3.setDoubleBuffered(true);

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                js3.setViewportView(wahl3());
                js3.validate();
                wahl3.add(js3, BorderLayout.CENTER);
            }
        });
        wahltabbedPane.addTab("selektive Uhrzeiten", SystemConfig.hmSysIcons.get("wecker16"), wahl3, "");

        wahl4 = new JXPanel();
        js4 = new JScrollPane();
        js4.setBorder(null);
        js4.setDoubleBuffered(true);

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                js4.setViewportView(wahl4());
                js4.validate();
                wahl4.add(js4, BorderLayout.CENTER);
            }
        });
        wahltabbedPane.addTab("Schichtarbeiter planen", SystemConfig.hmSysIcons.get("mond"), wahl4, "");

        jpRechts.add(wahltabbedPane, BorderLayout.CENTER);
        tp1.add(jpRechts);

        tabbedPane.addTab("Einstellungen für die Suche", SystemConfig.hmSysIcons.get("tools"), tp1,
                "Einstellungen für den Suchlauf");
        tp2 = new SuchenSeite(this);
        tp2.setName("RoogleSeite2");
        tp2.setDoubleBuffered(true);
        tp2.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        tp2.setBackground(Color.WHITE);
        tp2.addKeyListener(this);
        tp2.addFocusListener(this);

        tabbedPane.addTab("Termine suchen und Überschreiben", SystemConfig.hmSysIcons.get("find"), tp2,
                "Termine suchen und Überschreiben");

        tabbedPane.setMnemonicAt(0, 'i');
        tabbedPane.setMnemonicAt(1, 'u');
        tabbedPane.addFocusListener(this);
        tp1.addFocusListener(this);
        tp2.addFocusListener(this);
        haupt.add(tabbedPane);

    }

    public void setStartFocus() {
        setTableSelection(jxTable, 0, 0);
    }

    class RoogleAction extends AbstractAction {
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand()
                 .equals("i")) {
                tabbedPane.setSelectedIndex(0);
                return;
            }
            if (e.getActionCommand()
                 .equals("u")) {
                tabbedPane.setSelectedIndex(1);
                return;
            }
            if (e.getActionCommand()
                 .equals("f")) {
                setTableSelection(jxTable, 0, 0);
                return;
            }
            if ((e.getActionCommand()
                  .toCharArray()[0]) == 27) {
                setVisible(false);
                dispose();
            }
        }
    }

    private void setTableSelection(JXTable table, int row, int col) {
        table.requestFocus();
        try {
            table.addRowSelectionInterval(row, col);
            table.addColumnSelectionInterval(row, col);
        } catch (java.lang.IllegalArgumentException e) {
            logger.error("could not add rowselection to table",e);
        }
    }

    private void TabelleSetzen(int wert) {

        String[] column = { "suchen", "Kollege", "Abteilung", "DBZeile", "Zeigen" };
        int i, size;
        size = KollegenListe.vKKollegen.size() - 1;

        Object[][] dataVector = new Object[size][5];

        kollegenWahl = new Object[size][6];
        kollegenAbteilung = new String[KollegenListe.maxKalZeile + 1];

        for (i = 1; i <= size; i++) {
            dataVector[i - 1][0] = Boolean.valueOf(false);
            dataVector[i - 1][1] = KollegenListe.getMatchcode(i);
            dataVector[i - 1][2] = KollegenListe.getAbteilung(i);
            dataVector[i - 1][3] = Integer.toString(KollegenListe.getDBZeile(i));
            dataVector[i - 1][4] = KollegenListe.getZeigen(i);
            kollegenAbteilung[Integer.parseInt((String) dataVector[i - 1][3])] = (String) dataVector[i - 1][2];


            kollegenWahl[i - 1][0] = Integer.parseInt((String) dataVector[i - 1][3]); // dbzeile
            kollegenWahl[i - 1][1] = Boolean.valueOf(false);// gewaehlt
            kollegenWahl[i - 1][2] = dataVector[i - 1][2];// Abreilung
            kollegenWahl[i - 1][3] = dataVector[i - 1][1];// Matchcode
            kollegenWahl[i - 1][4] = i; // Bezug zu vKKollegen
            kollegenWahl[i - 1][5] = Integer.parseInt((String) dataVector[i - 1][3]); // getDBZeile


        }

        myTable = new MyRoogleTable1();

        myTable.columnNames = column;
        myTable.data = dataVector;
        myTable.addTableModelListener(this);
        jxTable.setModel(myTable);

        jxTable.setHighlighters(HighlighterFactory.createSimpleStriping());
        jxTable.getColumn(0)
               .setMinWidth(50);
        jxTable.getColumn(0)
               .setMaxWidth(50);
        jxTable.getColumn(2)
               .setMaxWidth(150);
        jxTable.getColumn(3)
               .setMinWidth(0);
        jxTable.getColumn(3)
               .setMaxWidth(0);
        jxTable.getColumn(4)
               .setMinWidth(0);
        jxTable.getColumn(4)
               .setMaxWidth(0);

        jxTable.setColumnControlVisible(true);

        jxTable.setEditable(true);
        jxTable.setSortable(false);
        jxTable.validate();
        jxTable.setName("RoogleListe1");
        jxTable.addMouseListener(this);
    }

    private void allesMark(boolean mark) {
        int bis = jxGruppen.getRowCount();
        int i = 0;
        for (i = 0; i < bis; i++) {
            jxGruppen.setValueAt(Boolean.valueOf(mark), i, 0);
        }
        for (i = 0; i < 5; i++) {
            gruppenCheck[i].setSelected(mark);
        }
        bis = jxTable.getRowCount();
        for (i = 0; i < bis; i++) {
            jxTable.setValueAt(Boolean.valueOf(mark), i, 0);
        }
    }

    private void gruppenHandeln(ArrayList<String> arr, boolean ein) {
        int lang = jxTable.getRowCount();
        int i = 0;
        for (i = 0; i < lang; i++) {
            if (arr.contains(jxTable.getValueAt(i, 1))) {
                jxTable.setValueAt(Boolean.valueOf(ein), i, 0);
            }
        }
    }

    private void testeMarker() {

        int r, c;
        r = jxTable.getSelectedRow();
        c = 0;
        if (jxTable.getValueAt(r, c)
                   .toString()
                   .equals("true")) {
            jxTable.setValueAt(Boolean.valueOf(false), r, c);
        } else {
            jxTable.setValueAt(Boolean.valueOf(true), r, c);
        }
    }

    private void testeGruppenMarker() {

        int r, c;
        r = jxGruppen.getSelectedRow();
        c = 0;
        if (jxGruppen.getValueAt(r, c)
                     .toString()
                     .equals("true")) {
            jxGruppen.setValueAt(Boolean.valueOf(false), r, c);
        } else {
            jxGruppen.setValueAt(Boolean.valueOf(true), r, c);
        }
    }

    private void setzeActionListener(AbstractButton abut) {
        abut.addActionListener(this);
    }

    private void setzeKeyListener(AbstractButton abut) {
        abut.addKeyListener(this);
    }

    private JPanel wahl1() {

        String spalten = "20dlu,p,45dlu,p";
        String reihen = "10dlu,5dlu,p,2dlu,p,2dlu,p,2dlu,p,2dlu,p,5dlu,10dlu,5dlu,50dlu,5dlu,10dlu,5dlu,p";
        FormLayout lay = new FormLayout(spalten, reihen);
        PanelBuilder builder = new PanelBuilder(lay);
        builder.setDefaultDialogBorder();
        CellConstraints cc = new CellConstraints();
        builder.addSeparator("Mehrere Mitarbeiter in Suchlauf einbeziehen", cc.xyw(1, 1, 4));
        JRtaCheckBox jb = new JRtaCheckBox("Alle Krankengymnasten wählen");
        jb.setName("KG");
        jb.setMnemonic(KeyEvent.VK_K);
        setzeActionListener(jb);
        gruppenCheck[0] = jb;
        builder.add(jb, cc.xy(2, 3));
        jb = new JRtaCheckBox("Alle Masseure wählen");
        jb.setName("MA");
        jb.setMnemonic(KeyEvent.VK_M);
        setzeActionListener(jb);
        gruppenCheck[1] = jb;
        builder.add(jb, cc.xy(2, 5));
        jb = new JRtaCheckBox("Alle Ergotherapeuten wählen");
        jb.setName("ER");
        jb.setMnemonic(KeyEvent.VK_E);
        setzeActionListener(jb);
        gruppenCheck[2] = jb;
        builder.add(jb, cc.xy(2, 7));
        jb = new JRtaCheckBox("Alle Logopäden wählen");
        jb.setName("LO");
        jb.setMnemonic(KeyEvent.VK_L);
        setzeActionListener(jb);
        gruppenCheck[3] = jb;
        builder.add(jb, cc.xy(2, 9));
        jb = new JRtaCheckBox("Alle Sporttherapeuten wählen");
        jb.setName("SP");
        jb.setMnemonic(KeyEvent.VK_O);
        setzeActionListener(jb);
        gruppenCheck[4] = jb;
        builder.add(jb, cc.xy(2, 11));

        String[] xstring = { "10", "15", "20", "25", "30", "35", "40", "45", "50", "55", "60", "65", "70", "75", "80",
                "85", "90", "95", "100", "105", "110", "115", "120" };
        JComboBox<String> jc = null;
        try {
            jc = new JComboBox<>(xstring);
        } catch (Exception ex) {

        }
        jc.setSelectedItem(SystemConfig.RoogleZeiten.get("KG"));
        jc.setName("CKG");
        gruppenCombo[0] = jc;
        builder.add(jc, cc.xy(4, 3));

        jc = new JComboBox<>(xstring);
        jc.setSelectedItem(SystemConfig.RoogleZeiten.get("MA"));
        jc.setName("CMA");
        gruppenCombo[1] = jc;
        builder.add(jc, cc.xy(4, 5));

        jc = new JComboBox<>(xstring);
        jc.setSelectedItem(SystemConfig.RoogleZeiten.get("ER"));
        jc.setName("CER");
        gruppenCombo[2] = jc;
        builder.add(jc, cc.xy(4, 7));

        jc = new JComboBox<>(xstring);
        jc.setSelectedItem(SystemConfig.RoogleZeiten.get("LO"));
        jc.setName("CLO");
        gruppenCombo[3] = jc;
        builder.add(jc, cc.xy(4, 9));

        jc = new JComboBox<>(xstring);
        jc.setSelectedItem(SystemConfig.RoogleZeiten.get("SP"));
        jc.setName("CSP");
        gruppenCombo[4] = jc;
        builder.add(jc, cc.xy(4, 11));

        if (gedropt) {
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    macheDrop();
                    return null;
                }
            }.execute();
        }

        builder.addSeparator("Selbstdefinierte Gruppen einbeziehen", cc.xyw(1, 13, 4));

        jxGruppen = new JXTable(new DefaultTableModel());
        jxGruppen.setDoubleBuffered(true);
        new Thread() {
            @Override
            public void run() {
                TabelleGruppenSetzen();
                jxGruppen.validate();
            }
        }.start();
        JScrollPane js = new JScrollPane();
        js.setDoubleBuffered(true);
        js.setViewportView(jxGruppen);
        builder.add(js, cc.xywh(2, 15, 3, 1));

        builder.addSeparator("Mehr geht nicht....", cc.xyw(1, 17, 4));

        JXPanel dummy = new JXPanel(new GridLayout(1, 1));
        dummy.setDoubleBuffered(true);
        JXButton jbut = new JXButton("alles markieren");
        jbut.setName("allesmark");
        setzeActionListener(jbut);
        setzeKeyListener(jbut);
        dummy.add(jbut, BorderLayout.WEST);

        jbut = new JXButton("Markierungen löschen");
        jbut.setName("allesentmark");
        setzeActionListener(jbut);
        setzeKeyListener(jbut);

        dummy.add(jbut, BorderLayout.EAST);

        builder.add(dummy, cc.xyw(2, 19, 3));
        return builder.getPanel();
    }

    /****************************************/
    private JPanel wahl2() {
        SwingWorker<JPanel, Void> worker = new SwingWorker<JPanel, Void>() {
            @Override
            public JPanel doInBackground() {

                String spalten = "20dlu,p,35dlu,p";
                // 1. 2. 3. 4. 5. 6. 7. 8. 9. 10. 11. 12. 13. 14. 15. 16. 17. 18. 19. 20. 21.
                // 22. 23.
                String reihen = "10dlu,5dlu,p,2dlu,p,2dlu,p,5dlu,p,5dlu, p, 2dlu, p, 2dlu, p ,2dlu, p ,2dlu, p , 2dlu, p, 2dlu , p";
                FormLayout lay = new FormLayout(spalten, reihen);
                PanelBuilder builder = new PanelBuilder(lay);
                builder.setDefaultDialogBorder();
                CellConstraints cc = new CellConstraints();
                builder.addSeparator("Suche-Zeitraum manuell festlegen", cc.xyw(1, 1, 4));
                JRtaCheckBox jb = new JRtaCheckBox("Suche starten mit Datum...");
                zeitraumCheck[0] = jb;
                zeitraumCheck[0].setName("sucheab");
                setzeActionListener(zeitraumCheck[0]);
                builder.add(zeitraumCheck[0], cc.xy(2, 3));
                jb = new JRtaCheckBox("Suche beenden bei Datum...");
                zeitraumCheck[1] = jb;
                zeitraumCheck[1].setName("suchebis");
                setzeActionListener(zeitraumCheck[1]);
                builder.add(zeitraumCheck[1], cc.xy(2, 5));
                JRtaTextField tf = new JRtaTextField("DATUM", false);
                zeitraumEdit[0] = tf;
                zeitraumEdit[0].setName("sucheabdatum");
                zeitraumEdit[0].setText(DatFunk.sHeute());
                zeitraumEdit[0].setEnabled(false);
                builder.add(zeitraumEdit[0], cc.xy(4, 3));
                tf = new JRtaTextField("DATUM", false);
                zeitraumEdit[1] = tf;
                zeitraumEdit[1].setName("suchebisdatum");
                zeitraumEdit[1].setText(DatFunk.sDatPlusTage(DatFunk.sHeute(), SystemConfig.RoogleZeitraum));
                zeitraumEdit[1].setEnabled(false);
                builder.add(zeitraumEdit[1], cc.xy(4, 5));
                builder.addSeparator("Wochentage ein- / ausschließen (Geschwindigkeit)", cc.xyw(1, 9, 4));

                jb = new JRtaCheckBox("Montag");
                tageCheck[0] = jb;
                tageCheck[0].setName("montag");
                tageCheck[0].setSelected(SystemConfig.RoogleTage[0]);
                builder.add(tageCheck[0], cc.xy(2, 11));
                jb = new JRtaCheckBox("Dienstag");
                tageCheck[1] = jb;
                tageCheck[1].setName("dienstag");
                tageCheck[1].setSelected(SystemConfig.RoogleTage[1]);
                builder.add(tageCheck[1], cc.xy(2, 13));
                jb = new JRtaCheckBox("Mittwoch");
                tageCheck[2] = jb;
                tageCheck[2].setName("mittwoch");
                tageCheck[2].setSelected(SystemConfig.RoogleTage[2]);
                builder.add(tageCheck[2], cc.xy(2, 15));
                jb = new JRtaCheckBox("Donnerstag");
                tageCheck[3] = jb;
                tageCheck[3].setName("donnerstag");
                tageCheck[3].setSelected(SystemConfig.RoogleTage[3]);
                builder.add(tageCheck[3], cc.xy(2, 17));
                jb = new JRtaCheckBox("Freitag");
                tageCheck[4] = jb;
                tageCheck[4].setName("freitag");
                tageCheck[4].setSelected(SystemConfig.RoogleTage[4]);
                builder.add(tageCheck[4], cc.xy(2, 19));
                jb = new JRtaCheckBox("Samstag");
                tageCheck[5] = jb;
                tageCheck[5].setName("samstag");
                tageCheck[5].setSelected(SystemConfig.RoogleTage[5]);
                builder.add(tageCheck[5], cc.xy(2, 21));
                jb = new JRtaCheckBox("Sonntag");
                tageCheck[6] = jb;
                tageCheck[6].setName("sonntag");
                tageCheck[6].setSelected(SystemConfig.RoogleTage[6]);
                builder.add(tageCheck[6], cc.xy(2, 23));

                return builder.getPanel();
            }
        };
        worker.execute();
        try {
            return worker.get();
        } catch (InterruptedException e) {

            e.printStackTrace();
        } catch (ExecutionException e) {

            e.printStackTrace();
        }
        return new JPanel();
    }

    /****************************************/
    private JPanel wahl3() {
        SwingWorker<JPanel, Void> worker = new SwingWorker<JPanel, Void>() {
            @Override
            public JPanel doInBackground() {

                // 1. 2. 3. 4. 5. 6. 7. 8.
                String spalten = "20dlu,p,20dlu,15dlu,2dlu,15dlu,2dlu,p";
                // 1. 2. 3. 4. 5. 6. 7. 8. 9. 10. 11. 12. 13. 14. 15. 16. 17. 18. 19. 20. 21.
                // 22. 23.
                String reihen = "10dlu,5dlu,p,2dlu,p,20dlu,p,5dlu,p,2dlu, p, 2dlu, p, 2dlu, p ,2dlu, p ,2dlu, p , 2dlu, p, 2dlu , p";
                FormLayout lay = new FormLayout(spalten, reihen);
                PanelBuilder builder = new PanelBuilder(lay);
                builder.setDefaultDialogBorder();
                CellConstraints cc = new CellConstraints();
                builder.addSeparator("Suche freie Termine innerhalb dieser Uhrzeiten", cc.xyw(1, 1, 8));

                JRtaCheckBox jb = new JRtaCheckBox("Frei Termine suchen ab");
                uhrselectCheck[0] = jb;
                uhrselectCheck[0].setName("uhr1");
                setzeActionListener(uhrselectCheck[0]);
                builder.add(uhrselectCheck[0], cc.xy(2, 3));
                JRtaTextField tf = new JRtaTextField("STUNDEN", true);
                uhrselectEdit[0] = tf;
                uhrselectEdit[0].setText("00");
                builder.add(uhrselectEdit[0], cc.xy(4, 3));
                tf = new JRtaTextField("MINUTEN", true);
                uhrselectEdit[1] = tf;
                uhrselectEdit[1].setText("00");
                builder.add(uhrselectEdit[1], cc.xy(6, 3));
                builder.addLabel("Uhr", cc.xy(8, 3));

                jb = new JRtaCheckBox("Freie Termine suchen bis spätestens");
                uhrselectCheck[1] = jb;
                uhrselectCheck[1].setName("uhr2");
                setzeActionListener(uhrselectCheck[1]);
                builder.add(uhrselectCheck[1], cc.xy(2, 5));
                tf = new JRtaTextField("STUNDEN", true);
                uhrselectEdit[2] = tf;
                uhrselectEdit[2].setText("00");
                builder.add(uhrselectEdit[2], cc.xy(4, 5));
                tf = new JRtaTextField("MINUTEN", true);
                uhrselectEdit[3] = tf;
                uhrselectEdit[3].setText("00");
                builder.add(uhrselectEdit[3], cc.xy(6, 5));
                builder.addLabel("Uhr", cc.xy(8, 5));

                builder.addSeparator("Oder(!) suche freie Termine der 2-ten Bedingung", cc.xyw(1, 7, 8));

                jb = new JRtaCheckBox("Frei Termine suchen ab");
                uhrselectCheck[2] = jb;
                uhrselectCheck[2].setName("uhr3");
                setzeActionListener(uhrselectCheck[2]);
                builder.add(uhrselectCheck[2], cc.xy(2, 9));
                tf = new JRtaTextField("STUNDEN", true);
                uhrselectEdit[4] = tf;
                uhrselectEdit[4].setText("00");
                builder.add(uhrselectEdit[4], cc.xy(4, 9));
                tf = new JRtaTextField("MINUTEN", true);
                uhrselectEdit[5] = tf;
                uhrselectEdit[5].setText("00");
                builder.add(uhrselectEdit[5], cc.xy(6, 9));
                builder.addLabel("Uhr", cc.xy(8, 9));

                jb = new JRtaCheckBox("Freie Termine suchen bis spätestens");
                uhrselectCheck[3] = jb;
                uhrselectCheck[3].setName("uhr4");
                setzeActionListener(uhrselectCheck[3]);
                builder.add(uhrselectCheck[3], cc.xy(2, 11));
                tf = new JRtaTextField("STUNDEN", true);
                uhrselectEdit[6] = tf;
                uhrselectEdit[6].setText("00");
                builder.add(uhrselectEdit[6], cc.xy(4, 11));
                tf = new JRtaTextField("MINUTEN", true);
                uhrselectEdit[7] = tf;
                uhrselectEdit[7].setText("00");
                builder.add(uhrselectEdit[7], cc.xy(6, 11));
                builder.addLabel("Uhr", cc.xy(8, 11));
                for (int i = 0; i < 8; i++) {
                    uhrselectEdit[i].setEnabled(false);
                }

                builder.getPanel()
                       .validate();
                return builder.getPanel();
            }
        };
        worker.execute();
        try {
            return worker.get();
        } catch (InterruptedException e) {

            e.printStackTrace();
        } catch (ExecutionException e) {

            e.printStackTrace();
        }
        return new JPanel();
    }

    /****************************************/
    private JPanel wahl4() {
        SwingWorker<JPanel, Void> worker = new SwingWorker<JPanel, Void>() {
            @Override
            public JPanel doInBackground() {
                String spalten = "20dlu,p,20dlu,15dlu,2dlu,15dlu,2dlu,p";
                // 1. 2. 3. 4. 5. 6. 7. 8. 9. 10. 11. 12. 13. 14. 15. 16. 17. 18. 19. 20. 21.
                // 22. 23.
                String reihen = "10dlu,5dlu,p,5dlu,p,2dlu,p, 10dlu, p,   10dlu,  p,  5dlu, p ,5dlu, p ,2dlu, p , 2dlu, p, 2dlu , p";
                FormLayout lay = new FormLayout(spalten, reihen);
                PanelBuilder builder = new PanelBuilder(lay);
                builder.setDefaultDialogBorder();
                CellConstraints cc = new CellConstraints();
                builder.addSeparator("Gerade Kalenderwochen akivieren", cc.xyw(1, 1, 8));

                JRtaCheckBox jb = new JRtaCheckBox("Geraden Kalenderwochen einplanen");
                schichtCheck[0] = jb;
                schichtCheck[0].setName("schichtcheck1");
                setzeActionListener(schichtCheck[0]);
                builder.add(schichtCheck[0], cc.xy(2, 3));

                schichtGruppe[0] = new ButtonGroup();

                JRtaRadioButton rb = new JRtaRadioButton("Nur freie Termine suchen vor..");
                schichtRadio[0] = rb;
                schichtRadio[0].setName("schichtradio1");
                setzeActionListener(schichtRadio[0]);
                schichtGruppe[0].add(schichtRadio[0]);
                builder.add(schichtRadio[0], cc.xy(2, 5));

                JRtaTextField tf = new JRtaTextField("STUNDEN", true);
                schichtEdit[0] = tf;
                schichtEdit[0].setText("00");
                builder.add(schichtEdit[0], cc.xywh(4, 5, 1, 3));
                tf = new JRtaTextField("MINUTEN", true);
                schichtEdit[1] = tf;
                schichtEdit[1].setText("00");
                builder.add(schichtEdit[1], cc.xywh(6, 5, 1, 3));

                rb = new JRtaRadioButton("Nur freie Termine suchen nach..");
                schichtRadio[1] = rb;
                schichtRadio[1].setName("schichtradio2");
                setzeActionListener(schichtRadio[1]);
                schichtGruppe[0].add(schichtRadio[1]);
                builder.add(schichtRadio[1], cc.xy(2, 7));

                builder.addLabel("Uhr", cc.xywh(8, 5, 1, 3));

                builder.addSeparator("Ungerade Kalenderwochen akivieren", cc.xyw(1, 11, 8));

                jb = new JRtaCheckBox("Ungeraden Kalenderwochen einplanen");
                schichtCheck[1] = jb;
                schichtCheck[1].setName("schichtcheck2");
                setzeActionListener(schichtCheck[1]);
                builder.add(schichtCheck[1], cc.xy(2, 13));

                schichtGruppe[1] = new ButtonGroup();

                rb = new JRtaRadioButton("Nur freie Termine suchen vor..");
                schichtRadio[2] = rb;
                schichtRadio[2].setName("schichtradio3");
                setzeActionListener(schichtRadio[2]);
                schichtGruppe[1].add(schichtRadio[2]);
                builder.add(schichtRadio[2], cc.xy(2, 15));

                tf = new JRtaTextField("STUNDEN", true);
                schichtEdit[2] = tf;
                schichtEdit[2].setText("00");
                builder.add(schichtEdit[2], cc.xywh(4, 15, 1, 3));
                tf = new JRtaTextField("MINUTEN", true);
                schichtEdit[3] = tf;
                schichtEdit[3].setText("00");
                builder.add(schichtEdit[3], cc.xywh(6, 15, 1, 3));

                rb = new JRtaRadioButton("Nur freie Termine suchen nach..");
                schichtRadio[3] = rb;
                schichtRadio[3].setName("schichtradio4");
                setzeActionListener(schichtRadio[3]);
                schichtGruppe[1].add(schichtRadio[3]);
                builder.add(schichtRadio[3], cc.xy(2, 17));

                for (int i = 0; i < 4; i++) {
                    schichtRadio[i].setEnabled(false);
                    schichtEdit[i].setEnabled(false);
                }
                builder.addLabel("Uhr", cc.xywh(8, 15, 1, 3));

                return builder.getPanel();
            }
        };
        worker.execute();
        try {
            return worker.get();
        } catch (InterruptedException e) {

            e.printStackTrace();
        } catch (ExecutionException e) {

            e.printStackTrace();
        }

        return new JPanel();
    }

    /****************************************/
    private void TabelleGruppenSetzen() {

        String[] column = { "Gr.wählen", "Gruppenname" };
        Object[][] dataVector;
        int i;
        int lang = SystemConfig.aRoogleGruppen.size();
        if (lang > 0) {
            dataVector = new Object[lang][2];
            for (i = 0; i < lang; i++) {
                dataVector[i][0] = Boolean.valueOf(false);
                dataVector[i][1] = SystemConfig.aRoogleGruppen.get(i)
                                                              .get(0)
                                                              .get(0);
            }
        } else {
            dataVector = null;
        }
        MyRoogleGruppe myGTable = new MyRoogleGruppe();
        myGTable.columnNames = column;
        myGTable.data = dataVector;
        myGTable.addTableModelListener(this);
        jxGruppen.setName("GruppenTable");
        jxGruppen.setModel(myGTable);
        jxGruppen.getColumn(0)
                 .setMinWidth(55);
        jxGruppen.getColumn(0)
                 .setMaxWidth(55);
        jxGruppen.setHighlighters(HighlighterFactory.createSimpleStriping());
        jxGruppen.setEditable(true);
        jxGruppen.setSortable(false);
        jxGruppen.validate();
        jxGruppen.addMouseListener(this);
    }

    /****************************************/
    private void checkBoxHandling(String name, JRtaCheckBox jcom, boolean selected) {
        int anzahl = jxTable.getRowCount();
        int i = 0;
        for (i = 0; i < anzahl; i++) {
            if (jxTable.getValueAt(i, 2)
                       .equals(name)) {
                if (selected && jxTable.getValueAt(i, 4)
                                       .toString()
                                       .equals("F")) {
                    jxTable.setValueAt(Boolean.valueOf(true), i, 0);
                } else {
                    jxTable.setValueAt(Boolean.valueOf(false), i, 0);
                }
            }
        }
    }

    /****************************************/
    public void FensterSchliessen(String welches) {
        this.dispose();
    }

    @Override
    public void componentHidden(ComponentEvent arg0) {

    }

    @Override
    public void componentMoved(ComponentEvent arg0) {

    }

    @Override
    public void componentResized(ComponentEvent arg0) {

    }

    @Override
    public void componentShown(ComponentEvent arg0) {

    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        JComponent jcomp = (JComponent) arg0.getSource();
        for (int i = 0; i < 1; i++) {
            String cname = jcomp.getName();
            if (cname.equals("KG")) {
                checkBoxHandling(cname, gruppenCheck[0], gruppenCheck[0].isSelected());
                break;
            }
            if (cname.equals("MA")) {
                checkBoxHandling(cname, gruppenCheck[1], gruppenCheck[1].isSelected());
                break;
            }
            if (cname.equals("ER")) {
                checkBoxHandling(cname, gruppenCheck[2], gruppenCheck[2].isSelected());
                break;
            }
            if (cname.equals("LO")) {
                checkBoxHandling(cname, gruppenCheck[3], gruppenCheck[3].isSelected());
                break;
            }
            if (cname.equals("SP")) {
                checkBoxHandling(cname, gruppenCheck[4], gruppenCheck[4].isSelected());
                break;
            }
            if (cname.equals("allesmark")) {
                new Thread() {
                    @Override
                    public void run() {
                        allesMark(true);
                    }
                }.start();
                break;
            }
            if (cname.equals("allesentmark")) {
                new Thread() {
                    @Override
                    public void run() {
                        allesMark(false);
                    }
                }.start();
                break;
            }
            if (cname.equals("sucheab")) {
                if (zeitraumCheck[0].isSelected()) {
                    zeitraumEdit[0].setEnabled(true);
                    zeitraumEdit[0].requestFocus();
                } else {
                    zeitraumEdit[0].setEnabled(false);
                }
                break;
            }
            if (cname.equals("suchebis")) {
                if (zeitraumCheck[1].isSelected()) {
                    zeitraumEdit[1].setEnabled(true);
                    zeitraumEdit[1].requestFocus();
                } else {
                    zeitraumEdit[1].setEnabled(false);
                }
                break;
            }
            if (cname.contains("uhr")) {
                if (schichtCheck[0].isSelected() || schichtCheck[1].isSelected()) {
                    JOptionPane.showMessageDialog(null, "Was wollen Sie eigentlich?\n\n"
                            + "Wollen Sie jetzt selektive Uhrzeiten angeben oder Schichtarbeiter? \n\n"
                            + "Die Software nimmt Ihnen an dieser Stelle die Entscheidung ab:\nDie Einstellungen für Schichtarbeiter planen werden zurückgesetzt!!!\n\n");
                    for (int j = 0; j < 4; j++) {
                        if (j < 2) {
                            schichtCheck[j].setSelected(false);
                        }
                        schichtEdit[j].setText("00");
                        schichtEdit[j].setEnabled(false);
                        schichtRadio[j].setEnabled(false);
                    }
                }
                select = true;
                schicht = false;

                if (cname.equals("uhr1") && uhrselectCheck[0].isSelected()) {
                    uhrselectEdit[0].setEnabled(true);
                    uhrselectEdit[1].setEnabled(true);
                    uhrselectEdit[0].requestFocus();
                }
                if ((cname.equals("uhr1")) && (!uhrselectCheck[0].isSelected())) {
                    if ((uhrselectCheck[2].isSelected() || uhrselectCheck[3].isSelected())) {
                        JOptionPane.showMessageDialog(null,
                                "Diese Auswahl macht nur dann Sinn, wenn Sie im unteren Bereich\n"
                                        + "beide Häkchen entfernt haben!");
                        uhrselectCheck[2].setSelected(false);
                        uhrselectCheck[3].setSelected(false);
                        uhrselectEdit[4].setText("00");
                        uhrselectEdit[5].setText("00");
                        uhrselectEdit[6].setText("00");
                        uhrselectEdit[7].setText("00");
                        uhrselectEdit[4].setEnabled(false);
                        uhrselectEdit[5].setEnabled(false);
                        uhrselectEdit[6].setEnabled(false);
                        uhrselectEdit[7].setEnabled(false);
                        uhrselectCheck[0].requestFocus();
                    }
                    uhrselectEdit[0].setText("00");
                    uhrselectEdit[1].setText("00");
                    uhrselectEdit[0].setEnabled(false);
                    uhrselectEdit[1].setEnabled(false);
                }
                if (cname.equals("uhr2") && uhrselectCheck[1].isSelected()) {
                    uhrselectEdit[2].setEnabled(true);
                    uhrselectEdit[3].setEnabled(true);
                    uhrselectEdit[2].requestFocus();
                }
                if ((cname.equals("uhr2")) && (!uhrselectCheck[1].isSelected())) {
                    if ((uhrselectCheck[2].isSelected() || uhrselectCheck[3].isSelected())) {
                        JOptionPane.showMessageDialog(null,
                                "Diese Auswahl macht nur dann Sinn, wenn Sie im unteren Bereich\n"
                                        + "beide Häkchen entfernt haben!");
                        uhrselectCheck[2].setSelected(false);
                        uhrselectCheck[3].setSelected(false);
                        uhrselectEdit[4].setText("00");
                        uhrselectEdit[5].setText("00");
                        uhrselectEdit[6].setText("00");
                        uhrselectEdit[7].setText("00");
                        uhrselectEdit[4].setEnabled(false);
                        uhrselectEdit[5].setEnabled(false);
                        uhrselectEdit[6].setEnabled(false);
                        uhrselectEdit[7].setEnabled(false);
                        uhrselectCheck[1].requestFocus();
                    }
                    uhrselectEdit[3].setText("00");
                    uhrselectEdit[2].setEnabled(false);
                    uhrselectEdit[3].setEnabled(false);
                }
                if (cname.equals("uhr3") && uhrselectCheck[2].isSelected()) {
                    if (!(uhrselectCheck[0].isSelected() && uhrselectCheck[1].isSelected())) {
                        JOptionPane.showMessageDialog(null,
                                "Diese Auswahl macht nur dann Sinn, wenn Sie im oberen Bereich\n"
                                        + "beide Häkchen gesetzt haben!");
                        uhrselectCheck[2].setSelected(false);
                        uhrselectCheck[1].requestFocus();
                        return;
                    }
                    uhrselectEdit[4].setEnabled(true);
                    uhrselectEdit[5].setEnabled(true);
                    uhrselectEdit[4].requestFocus();
                }
                if ((cname.equals("uhr3")) && (!uhrselectCheck[2].isSelected())) {
                    uhrselectEdit[4].setText("00");
                    uhrselectEdit[5].setText("00");
                    uhrselectEdit[4].setEnabled(false);
                    uhrselectEdit[5].setEnabled(false);
                }
                if (cname.equals("uhr4") && uhrselectCheck[3].isSelected()) {
                    if (!(uhrselectCheck[0].isSelected() && uhrselectCheck[1].isSelected())) {
                        JOptionPane.showMessageDialog(null,
                                "Diese Auswahl macht nur dann Sinn, wenn Sie im oberen Bereich\n"
                                        + "beide Häkchen gesetzt haben!");
                        uhrselectCheck[3].setSelected(false);
                        uhrselectCheck[1].requestFocus();
                        return;
                    }
                    uhrselectEdit[6].setEnabled(true);
                    uhrselectEdit[7].setEnabled(true);
                    uhrselectEdit[6].requestFocus();
                }
                if ((cname.equals("uhr4")) && (!uhrselectCheck[3].isSelected())) {
                    uhrselectEdit[6].setText("00");
                    uhrselectEdit[7].setText("00");
                    uhrselectEdit[6].setEnabled(false);
                    uhrselectEdit[7].setEnabled(false);
                }
                if ((!uhrselectCheck[0].isSelected()) && (!uhrselectCheck[1].isSelected())
                        && (!uhrselectCheck[2].isSelected()) && (!uhrselectCheck[3].isSelected())) {
                    select = false;
                }

                break;
            }
            if (cname.equals("schichtcheck1") || cname.equals("schichtcheck2")) {
                if (uhrselectCheck[0].isSelected() || uhrselectCheck[1].isSelected() || uhrselectCheck[2].isSelected()
                        || uhrselectCheck[3].isSelected()) {
                    JOptionPane.showMessageDialog(null, "Was wollen Sie eigentlich?\n\n"
                            + "Wollen Sie jetzt Schichtarbeiter planen, oder wollen Sie selektiv nach bestimmten\n"
                            + "Uhrzeiten suchen??\n\n"
                            + "Die Software nimmt Ihnen an dieser Stelle die Entscheidung ab:\nDie Einstellungen für selektive Uhrzeiten werden ausgeschaltet!!!\n\n");
                    for (int j = 0; j < 8; j++) {
                        if (j < 4) {
                            uhrselectCheck[j].setSelected(false);
                        }
                        uhrselectEdit[j].setText("00");
                        uhrselectEdit[j].setEnabled(false);
                    }

                }
            }
            select = false;
            schicht = true;
            if ((cname.equals("schichtcheck1")) && (schichtCheck[0].isSelected())) {

                schichtRadio[0].setEnabled(true);
                schichtRadio[1].setEnabled(true);
                schichtEdit[0].setEnabled(true);
                schichtEdit[1].setEnabled(true);
                schichtRadio[0].setSelected(true);
                schichtEdit[0].requestFocus();
            }
            if ((cname.equals("schichtcheck1")) && (!schichtCheck[0].isSelected())) {
                schichtRadio[0].setEnabled(false);
                schichtRadio[1].setEnabled(false);
                schichtEdit[0].setText("00");
                schichtEdit[1].setText("00");
                schichtEdit[0].setEnabled(false);
                schichtEdit[1].setEnabled(false);
            }
            if ((cname.equals("schichtcheck2")) && (schichtCheck[1].isSelected())) {
                schichtRadio[2].setEnabled(true);
                schichtRadio[3].setEnabled(true);
                schichtEdit[2].setEnabled(true);
                schichtEdit[3].setEnabled(true);
                schichtRadio[2].setSelected(true);
                schichtEdit[2].requestFocus();
            }
            if ((cname.equals("schichtcheck2")) && (!schichtCheck[1].isSelected())) {
                schichtRadio[2].setEnabled(false);
                schichtRadio[3].setEnabled(false);
                schichtEdit[2].setText("00");
                schichtEdit[3].setText("00");
                schichtEdit[2].setEnabled(false);
                schichtEdit[3].setEnabled(false);
            }
            if ((!schichtCheck[0].isSelected()) && (!schichtCheck[1].isSelected())) {
                schicht = false;
            }

        }

    }

    @Override
    public void windowActivated(WindowEvent arg0) {

    }

    @Override
    public void windowClosed(WindowEvent arg0) {

        if (rtp != null) {
            rtp.removeRehaTPEventListener(this);
            rtp = null;
            ListenerTools.removeListeners(this);
        }
        if (tp2 != null) {
            if (!tp2.getZeit()
                    .equals("")) {
                EntsperreSatz es = new EntsperreSatz();
                es.setzeEltern(tp2);
                es.start();
            }
        } else {
            return;
        }
        getSmartTitledPanel().removeMouseListener(mymouse);
        getSmartTitledPanel().removeMouseMotionListener(mymouse);
        mymouse = null;

        if (tp2 != null) {
            ListenerTools.removeListeners(tp2);
            tp2.sucheDaten.clear();
            tp2.sucheDaten = null;
            tp2.vecWahl.clear();
            tp2.vecWahl = null;
            tp2.sucheKollegen = null;
            tp2.hZeiten = null;
            tp2.selbstGesperrt = null;
            ListenerTools.removeListeners(tp2.jxSucheTable);
            tp2.dtblm.getDataVector()
                     .clear();
            tp2.dtblm = null;
            tp2.jxSucheTable = null;
            tp2 = null;
        }
        if (tp1 != null) {
            ListenerTools.removeListeners(tp1);
            ListenerTools.removeListeners(this);
            jxTable = null;
            jxGruppen = null;
            tblDataModel = null;
            myTable = null;
            tp1 = null;
        }
        pinPanel = null;
        Reha.instance.progLoader.loescheRoogle();

        if (Reha.instance.terminpanel != null) {
            new Thread() {
                @Override
                public void run() {
                    Reha.instance.terminpanel.aktualisieren();
                    Reha.instance.terminpanel.setUpdateVerbot(false);
                }
            }.start();
        }
    }

    @Override
    public void windowClosing(WindowEvent arg0) {

        rtp.removeRehaTPEventListener(this);

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
    public void keyPressed(KeyEvent arg0) {
        if (arg0.getKeyCode() == KeyEvent.VK_ESCAPE) {
            arg0.consume();
            rtp.removeRehaTPEventListener(this);
            FensterSchliessen(null);
        }
        if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
            arg0.consume();
        }
    }

    @Override
    public void rehaTPEventOccurred(RehaTPEvent evt) {

        String ss = this.getName();
        try {
            if (evt.getDetails()[0].equals(ss) && evt.getDetails()[1] == "ROT") {
                rtp.removeRehaTPEventListener(this);
                rtp = null;
                FensterSchliessen(evt.getDetails()[0]);

            }
        } catch (NullPointerException ne) {
            logger.error("Fehler beim schließen des Fensters", ne);
        }
    }

    @Override
    public void mouseClicked(MouseEvent arg0) {
        JComponent jcom = (JComponent) arg0.getSource();

        try {
            if (jcom.getName()
                    .equals("RoogleListe1")) {
                if (arg0.getClickCount() == 2) {
                    testeMarker();
                }
            }
            if (jcom.getName()
                    .equals("GruppenTable")) {
                if (arg0.getClickCount() == 2) {
                    testeGruppenMarker();
                }
            }
        } catch (NullPointerException ne) {
        }
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
        setClicks(-1, -1);

    }

    @Override
    public void tableChanged(TableModelEvent arg0) {

        /*
         * //System.out.println("Art der Aenderung : "+arg0.getType());
         * //System.out.println("Aenderung in Zeile: "+arg0.getFirstRow());
         * //System.out.println("Aenderung in Spalte: "+arg0.getColumn());
         * //System.out.println("Source: "+arg0.getSource());
         * //System.out.println("Klasse: "+arg0.getSource().getClass().getSimpleName().
         * toString() );
         */
        if (arg0.getSource()
                .getClass()
                .getSimpleName()
                .toString()
                .equals("MyRoogleGruppe")) {
             //System.out.println("Art der Aenderung : "+arg0.getType());
            //System.out.println("Aenderung in Zeile: "+arg0.getFirstRow());
             //System.out.println("Aenderung in Spalte: "+arg0.getColumn());
            if (arg0.getType() == TableModelEvent.UPDATE) {
                //// System.out.println("Celle wurde updated");
            }

            String[] sSet = SystemConfig.aRoogleGruppen.get(arg0.getFirstRow())
                                                       .get(1)
                                                       .get(0);
            ArrayList<String> arr = new ArrayList<String>();
            for (int i = 0; i < sSet.length; i++) {
                arr.add(sSet[i]);
            }
            gruppenHandeln(arr, (jxGruppen.getValueAt(arg0.getFirstRow(), arg0.getColumn())
                                          .toString()
                                          .equals("true") ? true : false));
        } else {
            if (arg0.getType() == TableModelEvent.UPDATE) {
                kollegenWahl[arg0.getFirstRow()][1] = jxTable.getValueAt(arg0.getFirstRow(), 0);

            }
        }
    }

    @Override
    public void focusGained(FocusEvent arg0) {


    }

    @Override
    public void focusLost(FocusEvent arg0) {

    }

    @Override
    public void stateChanged(ChangeEvent arg0) {

        JTabbedPane pane = (JTabbedPane) arg0.getSource();
        int sel = pane.getSelectedIndex();

        if (sel == 1) {
            gewaehlt = 0;
            tp2.datumEinstellen();
            tp2.tageEinstellen();
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    int i;
                    int lang = jxTable.getRowCount();

                    tp2.setKollegenEinstellen(kollegenWahl);

                    HashMap<String, Integer> hm = new HashMap<String, Integer>();
                    hm.put("KG", Integer.parseInt((String) gruppenCombo[0].getSelectedItem()));
                    hm.put("MA", Integer.parseInt((String) gruppenCombo[1].getSelectedItem()));
                    hm.put("ER", Integer.parseInt((String) gruppenCombo[2].getSelectedItem()));
                    hm.put("LO", Integer.parseInt((String) gruppenCombo[3].getSelectedItem()));
                    hm.put("SP", Integer.parseInt((String) gruppenCombo[4].getSelectedItem()));
                    hm.put("GR", 15);

                    tp2.setKollegenZeiten(hm);

                    tp2.setKollegenAbteilung(kollegenAbteilung);

                    if ((!schichtCheck[0].isSelected()) && (!schichtCheck[1].isSelected())) {
                        schicht = false;
                    }
                    if (schicht) {
                        tp2.schichtWal[0] = schichtCheck[0].isSelected();
                        tp2.schichtWal[1] = schichtCheck[1].isSelected();
                        tp2.schichtVor[0] = (schichtRadio[0].isSelected() ? true : false);
                        tp2.schichtVor[1] = (schichtRadio[2].isSelected() ? true : false);
                        tp2.schichtUhr[0] = schichtEdit[0].getText()
                                                          .trim()
                                + ":" + schichtEdit[1].getText()
                                                      .trim()
                                + ":00";
                        tp2.schichtUhr[1] = schichtEdit[2].getText()
                                                          .trim()
                                + ":" + schichtEdit[3].getText()
                                                      .trim()
                                + ":00";
                    }

                    if ((!uhrselectCheck[0].isSelected()) && (!uhrselectCheck[1].isSelected())
                            && (!uhrselectCheck[2].isSelected()) && (!uhrselectCheck[3].isSelected())) {
                        select = false;
                    }
                    if (select) {
                        tp2.selectUhr[0] = uhrselectEdit[0].getText() + ":" + uhrselectEdit[1].getText() + ":00";
                        tp2.selectUhr[1] = uhrselectEdit[2].getText() + ":" + uhrselectEdit[3].getText() + ":00";
                        tp2.selectUhr[2] = uhrselectEdit[4].getText() + ":" + uhrselectEdit[5].getText() + ":00";
                        tp2.selectUhr[3] = uhrselectEdit[6].getText() + ":" + uhrselectEdit[7].getText() + ":00";
                        tp2.selectWal[0] = uhrselectCheck[0].isSelected();
                        tp2.selectWal[1] = uhrselectCheck[1].isSelected();
                        tp2.selectWal[2] = uhrselectCheck[2].isSelected();
                        tp2.selectWal[3] = uhrselectCheck[3].isSelected();
                        int fehler = 0;
                        if (tp2.selectWal[0] && tp2.selectUhr[0].equals("00:00:00")) {
                            fehler++;
                        }
                        if (tp2.selectWal[1] && tp2.selectUhr[1].equals("00:00:00")) {
                            fehler++;
                        }
                        if (tp2.selectWal[2] && tp2.selectUhr[2].equals("00:00:00")) {
                            fehler++;
                        }
                        if (tp2.selectWal[3] && tp2.selectUhr[3].equals("00:00:00")) {
                            fehler++;
                        }
                        if (fehler != 0) {
                            JOptionPane.showMessageDialog(tp1, "Sie haben fehlerhafte Zeitangaben gemacht!\n"
                                    + "Registerseite 'selektive Uhrzeiten'");
                            tabbedPane.setSelectedIndex(0);
                            return;
                        }

                    }

                    SuchenSeite.schicht = schicht;
                    SuchenSeite.selektiv = select;
                    for (i = 0; i < lang; i++) {
                        Boolean rangImkalenderSuchen = (Boolean) jxTable.getValueAt(i, 0);
                        if (rangImkalenderSuchen) {
                            gewaehlt = gewaehlt + 1;
                            kollegenSuchen[Integer.valueOf((String) jxTable.getValueAt(i, 3))] = true;
                        } else {
                            kollegenSuchen[Integer.valueOf((String) jxTable.getValueAt(i, 3))] = false;
                        }

                    }
                    if (gewaehlt == 0) {
                        JOptionPane.showMessageDialog(tp1,
                                "Nur völlige Spezialisten wollen zwar suchen - sagen aber nicht wo(!) sie suchen wollen.\n"
                                        + "Am besten suchen - 'diese Spezialisten' - dann unter einer Lampe - dann ist's wenigstens recht hell bei der Suche....");
                        tabbedPane.setSelectedIndex(0);
                    } else {
                        tp2.setGewaehlt(gewaehlt);
                    }
                    tp2.setKollegenSuchen(kollegenSuchen);
                }
            });
        }
        if (sel == 0) {
            gewaehlt = 0;
        }

    }

    @Override
    public void keyReleased(KeyEvent arg0) {

    }

    @Override
    public void keyTyped(KeyEvent arg0) {

    }

}

class MyRoogleTable1 extends AbstractTableModel {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public Object[] values = { Boolean.valueOf(false), "", "" };
    public String[] columnNames = { "", "", "", "", "" };

    public Object[][] data = { { (boolean) Boolean.valueOf(false), "", "", "", "" } };

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public int getRowCount() {
        return data.length;
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
    public Object getValueAt(int row, int col) {
        return data[row][col];
    }

    /*
     * JTable uses this method to determine the default renderer/ editor for each
     * cell. If we didn't implement this method, then the last column would contain
     * text ("true"/"false"), rather than a check box.
     */
    @Override
    public Class<?> getColumnClass(int c) {
        Object valueAt = getValueAt(0, c);
        return valueAt!=null ? valueAt.getClass() : String.class;
    }

    /*
     * Don't need to implement this method unless your table's editable.
     */
    @Override
    public boolean isCellEditable(int row, int col) {
        // Note that the data/cell address is constant,
        // no matter where the cell appears onscreen.
        if (col > 0) {
            return false;
        } else {
            return true;
        }
    }

    /*
     * Don't need to implement this method unless your table's data can change.
     */
    @Override
    public void setValueAt(Object value, int row, int col) {


        data[row][col] = value;
        fireTableCellUpdated(row, col);

    }
}

/******************************************/
class MyRoogleGruppe extends AbstractTableModel {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public Object[] values = { Boolean.valueOf(false), "", "" };
    public String[] columnNames = { "", "", "" };

    public Object[][] data = { { (boolean) Boolean.valueOf(false), "", "" } };

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public int getRowCount() {
        return data.length;
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
    public Object getValueAt(int row, int col) {
        return data[row][col];
    }

    /*
     * JTable uses this method to determine the default renderer/ editor for each
     * cell. If we didn't implement this method, then the last column would contain
     * text ("true"/"false"), rather than a check box.
     */
    @Override
    public Class<?> getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    /*
     * Don't need to implement this method unless your table's editable.
     */
    @Override
    public boolean isCellEditable(int row, int col) {
        if (col > 0) {
            return false;
        } else {
            return true;
        }
    }

    /*
     * Don't need to implement this method unless your table's data can change.
     */
    @Override
    public void setValueAt(Object value, int row, int col) {

        data[row][col] = value;
        fireTableCellUpdated(row, col);

    }


}
