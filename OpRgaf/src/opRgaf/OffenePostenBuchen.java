package opRgaf;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.Vector;
import java.util.stream.Collectors;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import CommonTools.ButtonTools;
import CommonTools.DatFunk;
import CommonTools.DateTableCellEditor;
import CommonTools.JCompTools;
import CommonTools.JRtaCheckBox;
import CommonTools.JRtaTextField;
import CommonTools.SqlInfo;
import CommonTools.StringTools;
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
import environment.Path;
import io.RehaIOMessages;
import mandant.IK;
import office.OOService;
import opRgaf.RehaIO.SocketClient;
import opRgaf.rezept.Money;

class OffenePostenBuchen extends JXPanel implements TableModelListener, RgAfVk_IfCallBack {
    private static final long serialVersionUID = -7883557713071422132L;

    private JRtaTextField suchen;

    private JButton merkenBtn;
    private JButton ausbuchenBtn;
    private JButton kopieButton;

    private OffenePostenComboBox combo;

    private KeyListener kl;

    private ActionListener al;

    private OffenePostenTableModel modelNeu;

    private OffenePostenJTable tab;

    private JRtaCheckBox bar;
    private boolean barWasSelected;

    private DecimalFormat dcf = new DecimalFormat("###0.00");

    private HashMap<String, String> hmRezgeb = new HashMap<>();

    private OffenePostenSummenPanel sumPan;
    private OffenePostenCHKBX selPan;

    private OpRgAfIni iniOpRgAf;

    private IK ik;

    private JRtaTextField geldeingangTf;

    private Logger logger = LoggerFactory.getLogger(OffenePostenBuchen.class);

    OffenePostenBuchen(OpRgaf opRgaf, IK ik) {

        this.iniOpRgAf = opRgaf.iniOpRgAf;
        this.ik = ik;
        startKeyListener();
        startActionListener();
        setLayout(new BorderLayout());
        add(getContent(), BorderLayout.CENTER);
        setzeFocus();
        tab.sorter.sort();
    }

    void setzeFocus() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                suchen.requestFocus();
            }
        });
    }

    static final OffenePostenSchaltbarerTextFilter rgrTypefilter = new OffenePostenSchaltbarerTextFilter(
            OffenePostenTableModel.RGNR, "rgr", true);
    static final OffenePostenSchaltbarerTextFilter afrTypefilter = new OffenePostenSchaltbarerTextFilter(
            OffenePostenTableModel.RGNR, "afr", false);
    static final OffenePostenSchaltbarerTextFilter vrTypefilter = new OffenePostenSchaltbarerTextFilter(
            OffenePostenTableModel.RGNR, "vr", false);

    private List<OffenePosten> opListe;

    private static void verknuepfe(OffenePostenJTable opJTable, OffenePostenCHKBX select3ChkBx) {

        List<OffenePostenSchaltbarerTextFilter> filters = Arrays.asList(rgrTypefilter, afrTypefilter, vrTypefilter);
        opJTable.setTypeFilter(RowFilter.orFilter(filters));

        select3ChkBx.addOListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                System.out.println(e);
                rgrTypefilter.set(e.getStateChange() == ItemEvent.SELECTED);
                opJTable.sorter.sort();

            }
        });
        select3ChkBx.addMListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                System.out.println(e);
                afrTypefilter.set(e.getStateChange() == ItemEvent.SELECTED);
                opJTable.sorter.sort();
            }
        });
        select3ChkBx.addUListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                System.out.println(e);
                vrTypefilter.set(e.getStateChange() == ItemEvent.SELECTED);
                opJTable.sorter.sort();
            }
        });
    }

    private JPanel getContent() {
        // 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19
        String xwerte = "10dlu,50dlu,2dlu,90dlu,10dlu,p,2dlu,70dlu:g,40dlu,5dlu,50dlu,5dlu,50dlu,5dlu,50dlu,5dlu,50dlu,10dlu,p";
        // 1 2 3 4 5 6 7 8 9 10 11
        String ywerte = "15dlu,p,15dlu,160dlu:g,8dlu,p,10dlu,2dlu,p,8dlu,0dlu";
        FormLayout lay = new FormLayout(xwerte, ywerte);
        PanelBuilder builder = new PanelBuilder(lay);

        builder.getPanel()
               .setOpaque(false);
        CellConstraints cc = new CellConstraints();

        int colCnt = 2, rowCnt = 2;

        builder.addLabel("Suchkriterium", cc.xy(colCnt++, rowCnt)); // 2,2

        combo = new OffenePostenComboBox();
        builder.add(combo, cc.xy(++colCnt, rowCnt)); // 4,2

        ++colCnt;
        builder.addLabel("finde:", cc.xy(++colCnt, rowCnt)); // 6,2

        ++colCnt;
        suchen = new JRtaTextField("nix", true);
        suchen.setName("suchen");
        suchen.addKeyListener(kl);
        builder.add(suchen, cc.xy(++colCnt, rowCnt, CellConstraints.FILL, CellConstraints.DEFAULT)); // 8,2

        // Auswahl RGR/AFR/Verkauf
        colCnt += 2;
        selPan = new OffenePostenCHKBX("suche in  ", "Rezeptgebührenrechnungen", "Ausfallrechnungen",
                "Verkaufsrechnungen");

        selPan.initSelection(iniOpRgAf.getIncRG(), iniOpRgAf.getIncAR(), iniOpRgAf.getIncVK());

        builder.add(selPan.getPanel(),
                cc.xywh(++colCnt, rowCnt - 1, 5, 3, CellConstraints.LEFT, CellConstraints.DEFAULT)); // 10..15,1..3
        // Ende Auswahl

        merkenBtn = new JButton("merken");
        merkenBtn.setToolTipText("hält die suchergebnisse in der Anzeige fest");
        merkenBtn.addActionListener(e -> merken());

        merkenBtn.setMnemonic(KeyEvent.VK_S);

        builder.add(merkenBtn, cc.xy(17, rowCnt));
        opListe = new OffenePostenDTO(ik).all();
        modelNeu = new OffenePostenTableModel(opListe);
        tab = new OffenePostenJTable(modelNeu);
        verknuepfe(tab, selPan);
        verküpfen(tab, suchen, combo);
        combo.setSelectedIndex(0);
        tab.sorter.sort();
        // tab.setHorizontalScrollEnabled(true);

        DateTableCellEditor tble = new DateTableCellEditor();
//TODO:CellRenderer
        // tab.getColumn(1)

//           .setCellRenderer(new MitteRenderer());
//
//        tab.getColumn(2)
//           .setCellEditor(tble);
//
//        tab.getColumn(3)
//           .setCellRenderer(new DoubleTableCellRenderer());
//        tab.getColumn(3)
//           .setCellEditor(new DblCellEditor());
//
//        tab.getColumn(4)
//           .setCellRenderer(new DoubleTableCellRenderer());
//        tab.getColumn(4)
//           .setCellEditor(new DblCellEditor());
//
//        tab.getColumn(5)
//           .setCellRenderer(new DoubleTableCellRenderer());
//        tab.getColumn(5)
//           .setCellEditor(new DblCellEditor());
//
//        tab.getColumn(6)
//           .setCellEditor(tble);
//        tab.getColumn(7)
//           .setCellEditor(tble);
//        tab.getColumn(8)
//           .setCellEditor(tble);
//        tab.getColumn(10)
//           .setMinWidth(80);
//        tab.getColumn(11)
//           .setMaxWidth(50);
        tab.getSelectionModel()
           .addListSelectionListener(new OPListSelectionHandler());
        // tab.setHighlighters(HighlighterFactory.createSimpleStriping(HighlighterFactory.CLASSIC_LINE_PRINTER));

        JScrollPane jscr = JCompTools.getTransparentScrollPane(tab);
        rowCnt += 2;
        builder.add(jscr, cc.xyw(2, rowCnt, 17)); // 2,4
//**********************

        rowCnt += 2; // 6
        colCnt = 4;
        kopieButton = ButtonTools.macheButton("Rechnungskopie", "kopie", al);
        builder.add(kopieButton, cc.xy(colCnt, rowCnt)); // 4,6
        colCnt = 11;
        builder.addLabel("Geldeingang:", cc.xy(colCnt, rowCnt, CellConstraints.RIGHT, CellConstraints.TOP)); // 12,6

        ++colCnt;
        geldeingangTf = new JRtaTextField("F", true, "6.2", "");
        geldeingangTf.setHorizontalAlignment(SwingConstants.RIGHT);
        geldeingangTf.setText("0,00");
        geldeingangTf.setName("offen");
        geldeingangTf.addKeyListener(kl);
        builder.add(geldeingangTf, cc.xy(++colCnt, rowCnt)); // 14,6

        ++colCnt;
        bar = (JRtaCheckBox) builder.add(new JRtaCheckBox("bar in Kasse"), cc.xy(++colCnt, rowCnt));
        if ("Kasse".equals(iniOpRgAf.getWohinBuchen())) {
            bar.setSelected(true);
        }

        ausbuchenBtn = ButtonTools.macheButton("ausbuchen", "ausbuchen", al);
        builder.add(ausbuchenBtn, cc.xy(17, 6));
        ausbuchenBtn.setMnemonic(KeyEvent.VK_A);
//**********************

        rowCnt += 2;
        colCnt = 1;
        builder.add(new JSeparator(SwingConstants.HORIZONTAL), cc.xyw(colCnt, rowCnt++, 19));

        sumPan = new OffenePostenSummenPanel();
        builder.add(sumPan.getPanel(), cc.xyw(colCnt, rowCnt, 2, CellConstraints.LEFT, CellConstraints.TOP)); // 2,2

        sumPan.setValGesamtOffen(calcGesamtOffen(opListe));

        return builder.getPanel();
    }

    private Object merken() {
        sumPan.setValAnzahlSaetze(tab.getRowCount());
        sumPan.setValSuchOffen(calcFilteredOffen());
        sumPan.setValSuchGesamt(calcFilteredRGSum());
        return null;
    }



    private Money calcGesamtOffen(List<OffenePosten> list) {
        Optional<Money> result = list.stream()
                                     .map(OffenePosten::getOffen)
                                     .filter(o -> o.isMoreThan(Money.ZERO))
                                     .collect(Collectors.reducing((a, b) -> a.add(b)));

        return result.orElseGet(Money::new);

    }



    private  Money calcFilteredOffen() {
        Money result = new Money();
        for (int i = 0; i < tab.getRowCount(); i++) {
            result = result.add((Money) tab.getValueAt(tab.convertRowIndexToModel(i), OffenePostenTableModel.OFFEN));
            ;
        }
        return result;
    }

    private Money calcFilteredRGSum() {
        Money result = new Money();

        for (int i = 0; i < tab.getRowCount(); i++) {
            result = result.add(
                    (Money) tab.getValueAt(tab.convertRowIndexToModel(i), OffenePostenTableModel.GESAMTBETRAG));
            ;
        }
        return result;
    }

    private void startKeyListener() {
        kl = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent arg0) {
                if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
                    arg0.consume();
                    if ("suchen".equals(((JComponent) arg0.getSource()).getName())) {
                        sucheEinleiten();
                    } else if ("offen".equals(((JComponent) arg0.getSource()).getName())) {
                        setzeFocus();
                    }
                }
            }
        };
    }

    private void startActionListener() {
        al = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                String cmd = arg0.getActionCommand();
                if ("ausbuchen".equals(cmd)) {
                    modelNeu.removeTableModelListener(OffenePostenBuchen.this);
                    doAusbuchen();
                    modelNeu.addTableModelListener(OffenePostenBuchen.this);
                    setzeFocus();
                    return;
                }
                if ("kopie".equals(cmd)) {
                    doKopie();
                    setzeFocus();
                    return;
                }

            }
        };
    }

    private void doKopie() {
        if (modelNeu.getRowCount() <= 0) {
            return;
        }
        final String rnr = tab.getValueAt(tab.getSelectedRow(), 1)
                              .toString();
        if (rnr.startsWith("AFR")) {
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    try {
                        // System.out.println("in Ausfallrechnung");
                        String id = tab.getValueAt(tab.getSelectedRow(), 11)
                                       .toString();
                        String rez_nr = SqlInfo.holeEinzelFeld(
                                "select reznr from rgaffaktura where id='" + id + "' LIMIT 1");
                        String pat_intern = SqlInfo.holeEinzelFeld(
                                "select pat_intern from rgaffaktura where id='" + id + "' LIMIT 1");
                        String rdatum = SqlInfo.holeEinzelFeld(
                                "select rdatum from rgaffaktura where id='" + id + "' LIMIT 1");
                        AusfallRechnung ausfall = new AusfallRechnung(kopieButton.getLocationOnScreen(), pat_intern,
                                rez_nr, rnr, rdatum, OffenePostenBuchen.this.ik);
                        ausfall.setModal(true);
                        ausfall.setLocationRelativeTo(null);
                        ausfall.toFront();
                        ausfall.setVisible(true);
                        ausfall = null;
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    return null;
                }
            }.execute();
            return;
        }
        if (rnr.startsWith("RGR")) {
            doRezeptgebKopie();
        }
    }

    private void setzeBezahlBetrag(final int i) {
        geldeingangTf.setText(
                dcf.format(modelNeu.getValueAt(tab.convertRowIndexToModel(i), OffenePostenTableModel.OFFEN)));
    }

    private void sucheEinleiten() {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    setzeFocus();
                    modelNeu.removeTableModelListener(OffenePostenBuchen.this);
                    doSuchen();
                    modelNeu.addTableModelListener(OffenePostenBuchen.this);
                    suchen.setEnabled(true);
                    ausbuchenBtn.setEnabled(true);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Fehler beim einlesen der Datensätze");
                    setzeFocus();
                    suchen.setEnabled(true);
                    ausbuchenBtn.setEnabled(true);
                }
                setzeFocus();
                return null;
            }
        }.execute();
    }

    private void doAusbuchen() {
        int row = tab.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(null, "Keine Rechnung zum Ausbuchen ausgewählt");
            return;
        }
        BigDecimal nochoffen = BigDecimal.valueOf(
                (Double) modelNeu.getValueAt(tab.convertRowIndexToModel(row), OffenePostenTableModel.OFFEN));
        BigDecimal eingang = BigDecimal.valueOf(Double.parseDouble(geldeingangTf.getText()
                                                                                .replace(",", ".")));
        BigDecimal restbetrag = nochoffen.subtract(eingang);


        if (nochoffen.compareTo(BigDecimal.valueOf(Double.parseDouble("0.0"))) == 0) {
            JOptionPane.showMessageDialog(null, "Diese Rechnung ist bereits auf bezahlt gesetzt");
            return;
        }

        String cmd = "";
        String rgaf_reznum = modelNeu.getValueAt(tab.convertRowIndexToModel(row), OffenePostenTableModel.REZNUMMER)
                                     .toString();
        String rgaf_rechnum = modelNeu.getValueAt(tab.convertRowIndexToModel(row), OffenePostenTableModel.RGNR)
                                      .toString();

        if (bar.isSelected()) {
            String ktext = rgaf_rechnum + ","
                    + modelNeu.getValueAt(tab.convertRowIndexToModel(row), OffenePostenTableModel.KENNUNG);
            // Name, Vorname, Geburtstag (soweit 35 Zeichen reichen)
            if (ktext.length() > 35) {
                ktext = ktext.substring(0, 34);
            }
            cmd = "insert into kasse set einnahme='" + dcf.format(eingang)
                                                          .replace(",", ".")
                    + "', datum='" + DatFunk.sDatInSQL(DatFunk.sHeute()) + "', ktext='" + ktext + "'," + "rez_nr='"
                    + rgaf_reznum + "'";
            SqlInfo.sqlAusfuehren(cmd);
        }
        modelNeu.setValueAt(new Date(), tab.convertRowIndexToModel(row), OffenePostenTableModel.BEZAHLTAM); // not your
                                                                                                            // business
        modelNeu.setValueAt(restbetrag.doubleValue(), tab.convertRowIndexToModel(row), OffenePostenTableModel.OFFEN);

        if (rgaf_rechnum.startsWith("RGR-")) { // Rezept bezahlt setzen
            SqlInfo.sqlAusfuehren(
                    "update verordn set zzstatus='1', rez_bez='T' where rez_nr = '" + rgaf_reznum + "' LIMIT 1"); // zz:
                                                                                                                  // 1-ok
            SqlInfo.sqlAusfuehren(
                    "update lza set zzstatus='1', rez_bez='T' where rez_nr = '" + rgaf_reznum + "' LIMIT 1");
        }

        int id = (Integer) modelNeu.getValueAt(tab.convertRowIndexToModel(row), OffenePostenTableModel.TABELLENID);
        if (rgaf_rechnum.startsWith("RGR-") || rgaf_rechnum.startsWith("AFR-")) { // aus rgaffaktura ausbuchen
            cmd = "update rgaffaktura set roffen='" + dcf.format(restbetrag)
                                                         .replace(",", ".")
                    + "', rbezdatum='" + DatFunk.sDatInSQL(DatFunk.sHeute()) + "' where id ='" + id + "' LIMIT 1";
        }
        if (rgaf_rechnum.startsWith("VR-")) { // aus verkliste ausbuchen
            cmd = "update verkliste set v_offen='" + dcf.format(restbetrag)
                                                        .replace(",", ".")
                    + "', v_bezahldatum='" + DatFunk.sDatInSQL(DatFunk.sHeute()) + "' where verklisteID ='" + id
                    + "' LIMIT 1";
        }
        SqlInfo.sqlAusfuehren(cmd);
        geldeingangTf.setText("0,00");
    }

    void sucheRezept(String rezept) { // Einstieg für RehaReverseServer (z.B. RGR-Kopie aus Historie)
        suchen.setText(rezept);
        combo.setSelectedItem("Rezeptnummer =");

        doSuchen();

    }

    private void doSuchen() {

        tab.sorter.sort();
    }

    private void adjustColumns() {
        /* ausgewaehlte Spalten dem Inhalt anpassen */
        int columns2adjust[] = { 0, 4, 7, 8, 10 }; // Name,Vorname,Geburtstag, Offen, 1.Mahnung, 2.Mahnung, RezeptNr.
        for (int col : columns2adjust) {
//            tab.packColumn(col, 5);
        }
    }

    private class OPListSelectionHandler implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            ListSelectionModel lsm = (ListSelectionModel) e.getSource();
            boolean isAdjusting = e.getValueIsAdjusting();
            if (isAdjusting) {
                return;
            }
            if (lsm.isSelectionEmpty()) {

            } else {
                int minIndex = lsm.getMinSelectionIndex();
                int maxIndex = lsm.getMaxSelectionIndex();
                for (int i = minIndex; i <= maxIndex; i++) {
                    if (lsm.isSelectedIndex(i)) {
                        setzeBezahlBetrag(i);
                        String id = tab.getValueAt(i, 11)
                                       .toString();
                        String rnr = tab.getValueAt(i, 1)
                                        .toString();
                        String rez_nr = SqlInfo.holeEinzelFeld(
                                "select reznr from rgaffaktura where id='" + id + "' LIMIT 1");
                        String pat_intern = SqlInfo.holeEinzelFeld(
                                "select pat_intern from rgaffaktura where id='" + id + "' LIMIT 1");
                        new SocketClient().setzeRehaNachricht(OpRgaf.rehaReversePort,
                                "OpRgaf#" + RehaIOMessages.MUST_PATANDREZFIND + "#" + pat_intern + "#" + rez_nr);
                        // System.out.println("Satz "+i);

                        if (rnr.startsWith("VR-")) { // test ob VR -> bar ausbuchen enabled/disabled
                            if (!iniOpRgAf.getVrCashAllowed()) {
                                bar.setEnabled(false);
                                bar.setToolTipText("not allowed for VR (see System-Init)");
                                if (bar.isSelected()) { // falls 'bar in Kasse' gewählt war -> merken
                                    bar.setSelected(false);
                                    barWasSelected = true;
                                }
                            }
                        } else {
                            bar.setEnabled(true);
                            bar.setToolTipText("");
                            if (barWasSelected) { // Status 'bar in Kasse' wieder herstellen
                                bar.setSelected(true);
                                barWasSelected = false;
                            }
                        }
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void tableChanged(TableModelEvent arg0) {
        if (arg0.getType() == TableModelEvent.INSERT) {
            System.out.println("Insert");
            return;
        }
        if (arg0.getType() == TableModelEvent.UPDATE) {
            try {
                int col = arg0.getColumn();
                int row = arg0.getFirstRow();
                String value = "";
                String id = Integer.toString((Integer) modelNeu.getValueAt(row, 11));
                if (modelNeu.getColumnClass(col) == Boolean.class) {
                    value = modelNeu.getValueAt(row, col) == Boolean.FALSE ? "F" : "T";
                } else if (modelNeu.getColumnClass(col) == Date.class) {
                    if (modelNeu.getValueAt(row, col) == null) {
                        value = "1900-01-01";
                    } else {
                        String test = modelNeu.getValueAt(row, col)
                                              .toString();
                        if (".  .".equals(test.trim())) {
                            value = null;
                        } else if (test.contains(".")) {
                            value = DatFunk.sDatInSQL(test);
                            if (value.equals("    -  -  ")) {
                                value = null;
                            }
                        } else if (test.equals("    -  -  ")) {
                            value = null;
                        } else {
                            value = test;
                        }
                    }
                } else if (modelNeu.getColumnClass(col) == Double.class) {
                    value = dcf.format(modelNeu.getValueAt(row, col))
                               .replace(",", ".");
                } else if (modelNeu.getColumnClass(col) == String.class) {
                    value = modelNeu.getValueAt(row, col)
                                    .toString();
                }
                String rnr = (String) modelNeu.getValueAt(row, 1);
                if (rnr.startsWith("VR-")) { // test ob VR -> Änderung in 'verkliste' schreiben
                    HashMap<String, String> hmMap2VerkListe = new HashMap<>();
                    hmMap2VerkListe.put("rbezdatum", "v_bezahldatum"); // der Fluch der verbogenen Spaltennamen
                    hmMap2VerkListe.put("roffen", "v_offen");
                    hmMap2VerkListe.put("rgesamt", "v_betrag");
                    if (hmMap2VerkListe.containsKey(modelNeu.getColumnName(col))) { // Ändern dieser Spalte ist erlaubt
                        String cmd = "update verkliste set " + hmMap2VerkListe.get(modelNeu.getColumnName(col)) + " ="
                                + (value != null ? "'" + value + "'" : "null") + " where verklisteID='" + id
                                + "' LIMIT 1";
                        System.out.println(cmd);
                        SqlInfo.sqlAusfuehren(cmd);
                    } else {
                        new SwingWorker<Void, Void>() {
                            /** Andere 'rückgängig' machen (= Suche neu ausführen). */
                            @Override // eleganter wäre nur das geänderte Feld neu einzulesen ...
                            protected Void doInBackground() throws Exception {
                                try {
                                    doSuchen();
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                                return null;
                            }
                        }.execute();

                        JOptionPane.showMessageDialog(null, "Diese Änderung ist in Verkaufsrechnungen nicht möglich!");
                    }
                } else {
                    String cmd = "update rgaffaktura set " + modelNeu.getColumnName(col) + "="
                            + (value != null ? "'" + value + "'" : "null") + " where id='" + id + "' LIMIT 1";
                    // System.out.println(cmd);
                    SqlInfo.sqlAusfuehren(cmd);
                    geldeingangTf.setText(dcf.format(
                            modelNeu.getValueAt(tab.convertRowIndexToModel(row), OffenePostenTableModel.OFFEN)));
                }
            } catch (Exception ex) {
                Logger logger = LoggerFactory.getLogger(OffenePostenBuchen.class);
                logger.error("Fehler ind der Dateneingabe", ex);
                JOptionPane.showMessageDialog(null, "Fehler in der Dateneingabe");
            }
        }
    }

    private void doRezeptgebKopie() {
        if (modelNeu.getRowCount() <= 0) {
            return;
        }
        String db = "";
        String id = tab.getValueAt(tab.getSelectedRow(), 11)
                       .toString();
        String rgnr = tab.getValueAt(tab.getSelectedRow(), 1)
                         .toString();
        String rez_nr = SqlInfo.holeEinzelFeld("select reznr from rgaffaktura where id='" + id + "' LIMIT 1");
        String pat_intern = SqlInfo.holeEinzelFeld("select pat_intern from rgaffaktura where id='" + id + "' LIMIT 1");
        String rdatum = SqlInfo.holeEinzelFeld("select rdatum from rgaffaktura where id='" + id + "' LIMIT 1");
        String rezgeb = SqlInfo.holeEinzelFeld("select rgbetrag from rgaffaktura where id='" + id + "' LIMIT 1");
        String pauschale = SqlInfo.holeEinzelFeld("select rpbetrag from rgaffaktura where id='" + id + "' LIMIT 1");
        String gesamt = SqlInfo.holeEinzelFeld("select rgesamt from rgaffaktura where id='" + id + "' LIMIT 1");
        // System.out.println("Rezeptnummer = "+rez_nr);
        new InitHashMaps();

        String test = SqlInfo.holeEinzelFeld("select id from verordn where rez_nr = '" + rez_nr + "' LIMIT 1");
        Vector<String> vecaktrez = null;
        if ("".equals(test)) {
            test = SqlInfo.holeEinzelFeld("select id from lza where rez_nr = '" + rez_nr + "' LIMIT 1");
            if (!"".equals(test)) {
                vecaktrez = SqlInfo.holeSatz("lza",
                        " anzahl1,kuerzel1,kuerzel2," + "kuerzel3,kuerzel4,kuerzel5,kuerzel6 ", "id='" + test + "'",
                        Arrays.asList(new String[] {}));
                db = "lza";
            }
        } else {
            vecaktrez = SqlInfo.holeSatz("verordn",
                    " anzahl1,kuerzel1,kuerzel2," + "kuerzel3,kuerzel4,kuerzel5,kuerzel6 ", "id='" + test + "'",
                    Arrays.asList(new String[] {}));
            db = "verordn";
        }
        if (vecaktrez != null) {
            String behandlungen = vecaktrez.get(0) + "*" + (!"".equals(vecaktrez.get(1)
                                                                                .trim()) ? vecaktrez.get(1) : "")
                    + (!"".equals(vecaktrez.get(2)
                                           .trim()) ? "," + vecaktrez.get(2) : "")
                    + (!"".equals(vecaktrez.get(3)
                                           .trim()) ? "," + vecaktrez.get(3) : "")
                    + (!"".equals(vecaktrez.get(4)
                                           .trim()) ? "," + vecaktrez.get(4) : "")
                    + (!"".equals(vecaktrez.get(5)
                                           .trim()) ? "," + vecaktrez.get(5) : "")
                    + (!"".equals(vecaktrez.get(6)
                                           .trim()) ? "," + vecaktrez.get(6) : "");

            String cmd = "select abwadress,id from pat5 where pat_intern='" + pat_intern + "' LIMIT 1";
            Vector<Vector<String>> adrvec = SqlInfo.holeFelder(cmd);
            String[] adressParams = null;
            String patid = adrvec.get(0)
                                 .get(1);
            PatientenAdressen patientenAdressen = new PatientenAdressen(patid);
            if ("T".equals(adrvec.get(0)
                                 .get(0))) {
                adressParams = patientenAdressen.holeAbweichendeAdresse(patid);
            } else {
                adressParams = patientenAdressen.getAdressParams(patid);
            }

            hmRezgeb.put("<rgreznum>", rez_nr);
            hmRezgeb.put("<rgbehandlung>", behandlungen);
            hmRezgeb.put("<rgdatum>", DatFunk.sDatInDeutsch(
                    SqlInfo.holeEinzelFeld("select rez_datum from " + db + " where rez_nr='" + rez_nr + "' LIMIT 1")));
            hmRezgeb.put("<rgbetrag>", rezgeb.replace(".", ","));
            hmRezgeb.put("<rgpauschale>", pauschale.replace(".", ","));
            hmRezgeb.put("<rggesamt>", gesamt.replace(".", ","));
            hmRezgeb.put("<rganrede>", adressParams[0]);
            hmRezgeb.put("<rgname>", adressParams[1]);
            hmRezgeb.put("<rgstrasse>", adressParams[2]);
            hmRezgeb.put("<rgort>", adressParams[3]);
            hmRezgeb.put("<rgbanrede>", adressParams[4]);
            hmRezgeb.put("<rgorigdatum>", DatFunk.sDatInDeutsch(rdatum));
            hmRezgeb.put("<rgnr>", rgnr);

            hmRezgeb.put("<rgpatnname>", StringTools.EGross(
                    SqlInfo.holeEinzelFeld("select n_name from pat5 where pat_intern='" + pat_intern + "' LIMIT 1")));
            hmRezgeb.put("<rgpatvname>", StringTools.EGross(
                    SqlInfo.holeEinzelFeld("select v_name from pat5 where pat_intern='" + pat_intern + "' LIMIT 1")));
            hmRezgeb.put("<rgpatgeboren>", DatFunk.sDatInDeutsch(
                    SqlInfo.holeEinzelFeld("select geboren from pat5 where pat_intern='" + pat_intern + "' LIMIT 1")));

            // System.out.println(hmRezgeb);
            String url = Path.Instance.getProghome() + "vorlagen/" + ik.digitString()
                    + "/RezeptgebuehrRechnung.ott.Kopie.ott";
            try {
                officeStarten(url);
            } catch (OfficeApplicationException | NOAException e) {
                e.printStackTrace();
            } catch (TextException e) {
                e.printStackTrace();
            }
        } else {
            logger.error("das Rezept: " + rez_nr
                    + " konnte nicht gefunden werden, daher kann keine Rechnungskopie erstellt werden. ");
        }
    }

    private void officeStarten(String url) throws OfficeApplicationException, NOAException, TextException {
        IDocumentService documentService;
        //// System.out.println("Starte Datei -> "+url);

        documentService = new OOService().getOfficeapplication()
                                         .getDocumentService();

        IDocumentDescriptor docdescript = new DocumentDescriptor();
        docdescript.setHidden(true);
        docdescript.setAsTemplate(true);
        IDocument document;

        document = documentService.loadDocument(url, docdescript);
        ITextDocument textDocument = (ITextDocument) document;
        // OOTools.druckerSetzen(textDocument,
        // SystemConfig.hmAbrechnung.get("hmgkvrechnungdrucker"));
        ITextFieldService textFieldService = textDocument.getTextFieldService();
        ITextField[] placeholders;

        placeholders = textFieldService.getPlaceholderFields();
        String placeholderDisplayText = "";

        for (int i = 0; i < placeholders.length; i++) {
            placeholderDisplayText = placeholders[i].getDisplayText()
                                                    .toLowerCase();
            Set<Entry<String, String>> entries = hmRezgeb.entrySet();
            Iterator<Entry<String, String>> it = entries.iterator();
            while (it.hasNext()) {
                Entry<String, String> entry = it.next();
                if (((String) entry.getKey()).toLowerCase()
                                             .equals(placeholderDisplayText)) {
                    placeholders[i].getTextRange()
                                   .setText((String) entry.getValue());

                    break;
                }
            }
        }
        textDocument.getFrame()
                    .getXFrame()
                    .getContainerWindow()
                    .setVisible(true);
    }

    @Override
    public void useRGR(boolean rgr) {
        iniOpRgAf.setIncRG(rgr);
        calcGesamtOffen(opListe);
    }

    @Override
    public void useAFR(boolean afr) {
        iniOpRgAf.setIncAR(afr);
        calcGesamtOffen(opListe);
    }

    @Override
    public void useVKR(boolean vkr) {
        iniOpRgAf.setIncVK(vkr);
        calcGesamtOffen(opListe);
    }

    private static void verküpfen(OffenePostenJTable opJTable, JTextField eingabeFeld,
            OffenePostenComboBox opComboBox) {
        opComboBox.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                System.out.println(e);
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    CBModel selectedItem = (CBModel) opComboBox.getSelectedItem();
                    if (selectedItem != null) {
                        OffenePostenAbstractRowFilter filter = selectedItem.filter;
                        opJTable.setFilter(filter);
                        opJTable.sorter.sort();

                    }
                }
            }
        });
        eingabeFeld.getDocument()
                   .addDocumentListener(new DocumentListener() {

                       @Override
                       public void removeUpdate(DocumentEvent e) {
                           update(e);

                       }

                       @Override
                       public void insertUpdate(DocumentEvent e) {
                           update(e);
                       }

                       @Override
                       public void changedUpdate(DocumentEvent e) {
                           update(e);
                       }

                       private void update(DocumentEvent e) {

                           OffenePostenAbstractRowFilter filter = ((CBModel) opComboBox.getSelectedItem()).filter;
                           if (filter != null) {
                               filter.setFiltertext(eingabeFeld.getText());
                               opJTable.sorter.sort();
                           }

                       }
                   });
    }
}
