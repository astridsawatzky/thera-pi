package opRgaf;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.text.DefaultFormatterFactory;

import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.JXPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import CommonTools.JCompTools;
import CommonTools.JRtaCheckBox;
import CommonTools.JRtaTextField;
import io.RehaIOMessages;
import mandant.IK;
import opRgaf.OffenePosten.Type;
import opRgaf.RehaIO.SocketClient;
import opRgaf.rezept.Money;
import opRgaf.rezept.MoneyFormatter;

class OffenePostenBuchen extends JXPanel implements OPRGAFGui, TableModelListener {
    private static final long serialVersionUID = -7883557713071422132L;

    private JRtaTextField suchen;

    private JButton ausbuchenBtn;
    private JButton kopieButton;

    private OffenePostenComboBox suchkriterienCombo;

    private KeyListener kl;

    private OffenePostenTableModel modelNeu;

    private OffenePostenJTable tab;

    private JRtaCheckBox bar;
    private boolean barWasSelected;

    private OffenePostenSummenPanel sumPan;
    private OffenePostenCHKBX selPan;

    private OpRgAfIni iniOpRgAf;

    private JFormattedTextField geldeingangTf;

    private Logger logger = LoggerFactory.getLogger(OffenePostenBuchen.class);

    OffenePostenBuchen(OpRgAfIni iniOpRgAf, IK ik, List<OffenePosten> offenePostenListe) {
        InputMap inputmap = getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        KeyStroke keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_F12, 0);
        inputmap.put(keyStroke, keyStroke.toString());
        getActionMap().put(keyStroke.toString(),

                new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        ausbuchen();

                    }
                });

        this.iniOpRgAf = iniOpRgAf;
        opListe = offenePostenListe;
        startKeyListener();
        setLayout(new BorderLayout());
        add(this.getContent(), BorderLayout.CENTER);

        setzeFocus();
        tab.sorter.sort();
    }

    public void setzeFocus() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                suchen.requestFocus();
            }
        });
    }

    private static final OffenePostenSchaltbarerTextFilter rgrTypefilter = new OffenePostenSchaltbarerTextFilter(
            OffenePostenTableModel.RGNR, "rgr", false);
    private static final OffenePostenSchaltbarerTextFilter afrTypefilter = new OffenePostenSchaltbarerTextFilter(
            OffenePostenTableModel.RGNR, "afr", false);
    private static final OffenePostenSchaltbarerTextFilter vrTypefilter = new OffenePostenSchaltbarerTextFilter(
            OffenePostenTableModel.RGNR, "vr", false);

    private List<OffenePosten> opListe;

    private ActionListener kopierenListener = e -> {
        // if not set do nothing but don't NPE
    };

    private ActionListener ausbuchenListener = e -> {
        // if not set do nothing but don't NPE
    };

    private ActionListener paymentUpdateListener = e -> {
        // if not set do nothing but don't NPE
    };

    private ActionListener teilzahlenListener = e -> {
        // if not set do nothing but don't NPE
    };

    private LocalDate aktuellesBuchungsDatum = LocalDate.now();

    private void verknuepfe(OffenePostenJTable opJTable, OffenePostenCHKBX select3ChkBx) {

        List<OffenePostenSchaltbarerTextFilter> filters = Arrays.asList(rgrTypefilter, afrTypefilter, vrTypefilter);
        opJTable.setTypeFilter(RowFilter.orFilter(filters));

        select3ChkBx.addOListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                rgrTypefilter.set(e.getStateChange() == ItemEvent.SELECTED);
                opJTable.sorter.sort();
                calcGesamtOffen(opListe);
            }
        });
        select3ChkBx.addMListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                afrTypefilter.set(e.getStateChange() == ItemEvent.SELECTED);
                opJTable.sorter.sort();
                calcGesamtOffen(opListe);
            }
        });
        select3ChkBx.addUListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                vrTypefilter.set(e.getStateChange() == ItemEvent.SELECTED);
                opJTable.sorter.sort();
                calcGesamtOffen(opListe);
            }
        });
    }

    private JPanel getContent() {
        // 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19
        String xwerte = "10dlu," + "50dlu," + "2dlu," + "90dlu," + "10dlu," + "p," + "2dlu," + "60dlu:g,"
                + "50dlu,5dlu,50dlu,5dlu,50dlu,5dlu,50dlu,5dlu,60dlu,10dlu,p";
        // 1 2 3 4 5 6 7 8 9 10 11
        String reihe1 = "15dlu";
        String reihe2 = "p";
        String reihe3 = "15dlu";
        String reihe4 = "160dlu:g";
        String reihe5 = "p";
        String reihe6 = "p";
        String reihe7 = "10dlu";
        String reihe8 = "2dlu";
        String reihe9 = "p";
        String reihe10 = "8dlu";
        String reihe11 = "0dlu";
        String ywerte = reihe1 + "," + reihe2 + "," + reihe3 + "," + reihe4 + "," + reihe5 + "," + reihe6 + "," + reihe7
                + "," + reihe8 + "," + reihe9 + "," + reihe10 + "," + reihe11;
        FormLayout lay = new FormLayout(xwerte, ywerte);
        PanelBuilder builder = new PanelBuilder(lay);

        builder.getPanel()
               .setOpaque(false);
        CellConstraints cc = new CellConstraints();

        int colCnt = 2, rowCnt = 2;

        builder.addLabel("Suchkriterium", cc.xy(colCnt++, rowCnt)); // 2,2

        suchkriterienCombo = new OffenePostenComboBox(iniOpRgAf.getVorauswahlSuchkriterium());
        suchkriterienCombo.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                iniOpRgAf.setVorauswahl(suchkriterienCombo.getSelectedIndex());

            }
        });
        builder.add(suchkriterienCombo, cc.xy(++colCnt, rowCnt)); // 4,2

        ++colCnt;
        builder.addLabel("finde:", cc.xy(++colCnt, rowCnt)); // 6,2

        ++colCnt;
        suchen = new JRtaTextField("nix", true);

        builder.add(suchen, cc.xy(++colCnt, rowCnt, CellConstraints.FILL, CellConstraints.DEFAULT)); // 8,2

        JCheckBox offenOnly = new JCheckBox("nur offen", true);
        builder.add(offenOnly, cc.xy(9, rowCnt, CellConstraints.FILL, CellConstraints.DEFAULT)); // 9,2
        offenOnly.setToolTipText("wenn ausgewählt, wird nur in offenen gesucht sonst in allen");

        // Auswahl RGR/AFR/Verkauf
        colCnt += 3;
        selPan = new OffenePostenCHKBX();

        builder.add(selPan.getPanel(),
                cc.xywh(++colCnt, rowCnt - 1, 5, 3, CellConstraints.LEFT, CellConstraints.DEFAULT)); // 10..15,1..3
        // Ende Auswahl


        modelNeu = new OffenePostenTableModel(opListe);
        modelNeu.addTableModelListener(this);
        tab = new OffenePostenJTable(modelNeu);
        offenOnly.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                boolean isSelected = offenOnly.isSelected();
                if (isSelected) {
                    tab.enableOffenFilter();
                } else {
                    tab.disableOffenFilter();
                }

            }
        });
        tab.enableOffenFilter();
        verknuepfe(tab, selPan);
        verknuepfen(tab, suchen, suchkriterienCombo);
        tab.getSelectionModel()
           .addListSelectionListener(new OPListSelectionHandler());
        ;
        selPan.initSelection(iniOpRgAf.getIncRG(), iniOpRgAf.getIncAR(), iniOpRgAf.getIncVK());

        selPan.addOListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                iniOpRgAf.setIncRG(e.getStateChange() == ItemEvent.SELECTED);

            }
        });
        selPan.addMListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                iniOpRgAf.setIncAR(e.getStateChange() == ItemEvent.SELECTED);

            }
        });
        selPan.addUListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {

                iniOpRgAf.setIncVK(e.getStateChange() == ItemEvent.SELECTED);
            }
        });
        tab.sorter.sort();

        JScrollPane jscr = JCompTools.getTransparentScrollPane(tab);
        rowCnt += 2;
        builder.add(jscr, cc.xyw(2, rowCnt, 17)); // 2,4

        rowCnt += 2; // 6
        colCnt = 4;
        kopieButton = new JButton("Rechnungskopie");
        kopieButton.addActionListener(e -> kopieren());
        builder.add(kopieButton, cc.xy(colCnt, rowCnt)); // 4,6
        colCnt = 11;
        builder.addLabel("Geldeingang:", cc.xy(colCnt, rowCnt, CellConstraints.RIGHT, CellConstraints.TOP)); // 12,6

        ++colCnt;
        geldeingangTf = new JFormattedTextField(
                new DefaultFormatterFactory(new MoneyFormatter(), new MoneyFormatter(), new MoneyFormatter()),
                new Money());

        geldeingangTf.setHorizontalAlignment(SwingConstants.RIGHT);
        geldeingangTf.setName("offen");
        geldeingangTf.addKeyListener(kl);
        builder.add(geldeingangTf, cc.xy(++colCnt, rowCnt)); // 14,6

        ++colCnt;
        bar = (JRtaCheckBox) builder.add(new JRtaCheckBox("bar in Kasse"), cc.xy(++colCnt, rowCnt));
        if ("Kasse".equals(iniOpRgAf.getWohinBuchen())) {
            bar.setSelected(true);
        }

        ausbuchenBtn = new JButton("ausbuchen");
        ausbuchenBtn.addActionListener(e -> ausbuchen());
        builder.add(ausbuchenBtn, cc.xy(17, 6));
        ausbuchenBtn.setMnemonic(KeyEvent.VK_A);
        java.util.Date selected = new java.util.Date();
        JXDatePicker buchungsDatum = new JXDatePicker(selected, Locale.GERMAN);
        builder.add(buchungsDatum, cc.xy(17, 5));

        buchungsDatum.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                setAktuellesBuchungsDatum(buchungsDatum.getDate()
                                                       .toInstant()
                                                       .atZone(ZoneId.systemDefault())
                                                       .toLocalDate());

            }

        });
//**********************

        rowCnt += 2;
        colCnt = 1;
        builder.add(new JSeparator(SwingConstants.HORIZONTAL), cc.xyw(colCnt, rowCnt++, 19));

        sumPan = new OffenePostenSummenPanel(createMerkenButton());
        builder.add(sumPan.getPanel(), cc.xyw(colCnt, rowCnt, 2, CellConstraints.LEFT, CellConstraints.TOP)); // 2,2

        sumPan.setValGesamtOffen(calcGesamtOffen(opListe));

        return builder.getPanel();
    }

    private void setAktuellesBuchungsDatum(LocalDate localDate) {
        this.aktuellesBuchungsDatum = localDate;
    }

    private JButton createMerkenButton() {
        JButton merkenBtn = new JButton("Ergebnisse aktualisieren");
        merkenBtn.setToolTipText("h\u00e4lt die Suchergebnisse in der Anzeige fest");
        merkenBtn.addActionListener(e -> merken());

        merkenBtn.setMnemonic(KeyEvent.VK_S);
        return merkenBtn;
    }

    private List<OffenePosten> ausbuchen() {

        List<OffenePosten> opl = selectedOffenePosten();
        Money eingang;
        try {

            eingang = (Money) geldeingangTf.getValue();
        } catch (Exception e1) {
            logger.error("Fehler beim Ausbuchen", e1);
            eingang = new Money();
        }
        opl.forEach((o) -> o.bezahltAm = aktuellesBuchungsDatum);
        if (opl.size() == 1) {
            if (!opl.get(0).offen.hasSameValue(eingang)) {
                Payment paid = new Payment(opl.get(0), eingang);
                ActionEvent e = new ActionEvent(paid, ActionEvent.ACTION_PERFORMED, bar.isSelected() ? "bar" : "unbar");
                teilzahlenListener.actionPerformed(e);
            } else {
                ActionEvent e = new ActionEvent(opl, ActionEvent.ACTION_PERFORMED, bar.isSelected() ? "bar" : "unbar");
                ausbuchenListener.actionPerformed(e);
            }

        } else {
            ActionEvent e = new ActionEvent(opl, ActionEvent.ACTION_PERFORMED, bar.isSelected() ? "bar" : "unbar");
            ausbuchenListener.actionPerformed(e);
        }

        return opl;
    }

    public void datachanged() {
        modelNeu.fireTableDataChanged();

        sumPan.setValGesamtOffen(calcGesamtOffen(opListe));

    }

    private List<OffenePosten> kopieren() {
        List<OffenePosten> opl = selectedOffenePosten();
        ActionEvent e = new ActionEvent(opl, ActionEvent.ACTION_PERFORMED, "kopieren");
        kopierenListener.actionPerformed(e);
        return opl;
    }

    private List<OffenePosten> selectedOffenePosten() {
        int[] selections = tab.getSelectedRows();
        List<OffenePosten> opl = new LinkedList<>();
        for (int i : selections) {
            opl.add(modelNeu.getValue(tab.convertRowIndexToModel(i)));

        }
        return opl;
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

    private Money calcFilteredOffen() {
        Money result = new Money();
        for (int i = 0; i < tab.getRowCount(); i++) {
            Money offen = (Money) tab.getValueAt(i, OffenePostenTableModel.OFFEN);
            if (offen.isMoreThan(Money.ZERO)) {
                result = result.add(offen);
            }
            ;
        }
        return result;
    }

    private Money calcFilteredRGSum() {
        Money result = new Money();

        for (int i = 0; i < tab.getRowCount(); i++) {
            result = result.add((Money) tab.getValueAt(i, OffenePostenTableModel.GESAMTBETRAG));
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
                    if ("offen".equals(((JComponent) arg0.getSource()).getName())) {
                        setzeFocus();
                    }
                }
            }
        };
    }

    private void setzeBezahlBetrag(final int i) {
        geldeingangTf.setValue((Money) tab.getValueAt(i, OffenePostenTableModel.OFFEN));
    }

    public void sucheRezept(String rezept) { // Einstieg für RehaReverseServer (z.B. RGR-Kopie aus Historie)
        suchen.setText(rezept);
        suchkriterienCombo.setSelectedItem(OffenePostenComboBox.REZNUMMER_ENTHAELT);

        doSuchen();

    }

    private void doSuchen() {

        tab.sorter.sort();
    }

    private class OPListSelectionHandler implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (e.getValueIsAdjusting()) {
                return;
            }

            ListSelectionModel lsm = (ListSelectionModel) e.getSource();

            if (!lsm.isSelectionEmpty()) {
                OffenePosten leadOP = modelNeu.getValue(tab.convertRowIndexToModel(lsm.getLeadSelectionIndex()));
                ;
                String rez_nr = leadOP.rezNummer.rezeptNummer();

                int pat_intern = leadOP.patid;
                new SocketClient().setzeRehaNachricht(OpRgaf.rehaReversePort,
                        "OpRgaf#" + RehaIOMessages.MUST_PATANDREZFIND + "#" + pat_intern + "#" + rez_nr);
                int minIndex = lsm.getMinSelectionIndex();
                int maxIndex = lsm.getMaxSelectionIndex();
                for (int i = minIndex; i <= maxIndex; i++) {
                    if (lsm.isSelectedIndex(i)) {
                        setzeBezahlBetrag(i);
                        OffenePosten op = modelNeu.getValue(tab.convertRowIndexToModel(i));

                        if (op.type == Type.VR) { // test ob VR -> bar ausbuchen enabled/disabled
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
            return;
        }
        if (arg0.getType() == TableModelEvent.UPDATE) {

            int row = arg0.getFirstRow();

            OffenePosten op = modelNeu.getValue(row);
            ActionEvent payment = new ActionEvent(op, ActionEvent.ACTION_PERFORMED, "bezahlen");
            paymentUpdateListener.actionPerformed(payment);
        }
    }

    private static void verknuepfen(OffenePostenJTable opJTable, JTextField eingabeFeld,
            OffenePostenComboBox opComboBox) {
        opComboBox.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    applycombofilter(opJTable, eingabeFeld, opComboBox);
                }
            }

        });
        applycombofilter(opJTable, eingabeFeld, opComboBox);
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

                           updateTableView(opJTable, eingabeFeld, opComboBox);

                       }

                   });
    }

    private static void updateTableView(OffenePostenJTable opJTable, JTextField eingabeFeld,
            OffenePostenComboBox opComboBox) {
        OffenePostenAbstractRowFilter filter = ((CBModel) opComboBox.getSelectedItem()).filter;
        if (filter != null) {
            filter.setFiltertext(eingabeFeld.getText());
            opJTable.sorter.sort();
        }
    }

    private static void applycombofilter(OffenePostenJTable opJTable, JTextField eingabeFeld,
            OffenePostenComboBox opComboBox) {
        CBModel selectedItem = (CBModel) opComboBox.getSelectedItem();
        if (selectedItem != null) {
            OffenePostenAbstractRowFilter filter = selectedItem.filter;
            opJTable.setContentFilter(filter);
            if (filter != null) {
                filter.setFiltertext(eingabeFeld.getText());
            }
            opJTable.sorter.sort();

        }
    }

    public void addKopierenListener(ActionListener listener) {
        kopierenListener = listener;

    }

    public void addAusbuchenListener(ActionListener listener) {
        ausbuchenListener = listener;

    }

    public void addTeilzahlenListener(ActionListener listener) {
        teilzahlenListener = listener;

    }

    public void addPaymentUpdateListener(ActionListener paymentListener) {
        paymentUpdateListener = paymentListener;

    }

}
