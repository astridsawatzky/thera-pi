package opRgaf;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
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

class OffenePostenBuchen extends JXPanel implements TableModelListener {
    private static final long serialVersionUID = -7883557713071422132L;

    private JRtaTextField suchen;

    private JButton merkenBtn;
    private JButton ausbuchenBtn;
    private JButton kopieButton;

    private OffenePostenComboBox combo;

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

        this.iniOpRgAf = iniOpRgAf;
        opListe = offenePostenListe;
        startKeyListener();
        setLayout(new BorderLayout());
        add(this.getContent(), BorderLayout.CENTER);

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
            OffenePostenTableModel.RGNR, "rgr", false);
    static final OffenePostenSchaltbarerTextFilter afrTypefilter = new OffenePostenSchaltbarerTextFilter(
            OffenePostenTableModel.RGNR, "afr", false);
    static final OffenePostenSchaltbarerTextFilter vrTypefilter = new OffenePostenSchaltbarerTextFilter(
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

        builder.add(suchen, cc.xy(++colCnt, rowCnt, CellConstraints.FILL, CellConstraints.DEFAULT)); // 8,2

        // Auswahl RGR/AFR/Verkauf
        colCnt += 2;
        selPan = new OffenePostenCHKBX();

        builder.add(selPan.getPanel(),
                cc.xywh(++colCnt, rowCnt - 1, 5, 3, CellConstraints.LEFT, CellConstraints.DEFAULT)); // 10..15,1..3
        // Ende Auswahl

        merkenBtn = new JButton("merken");
        merkenBtn.setToolTipText("h\u00e4lt die Suchergebnisse in der Anzeige fest");
        merkenBtn.addActionListener(e -> merken());

        merkenBtn.setMnemonic(KeyEvent.VK_S);

        builder.add(merkenBtn, cc.xy(17, rowCnt));

        modelNeu = new OffenePostenTableModel(opListe);
        modelNeu.addTableModelListener(this);
        tab = new OffenePostenJTable(modelNeu);
        verknuepfe(tab, selPan);
        verküpfen(tab, suchen, combo);
        tab.getSelectionModel()
           .addListSelectionListener(new OPListSelectionHandler());
        ;
        selPan.initSelection(iniOpRgAf.getIncRG(), iniOpRgAf.getIncAR(), iniOpRgAf.getIncVK());
        combo.setSelectedIndex(0);
        tab.sorter.sort();

        JScrollPane jscr = JCompTools.getTransparentScrollPane(tab);
        rowCnt += 2;
        builder.add(jscr, cc.xyw(2, rowCnt, 17)); // 2,4
//**********************

        rowCnt += 2; // 6
        colCnt = 4;
        kopieButton = new JButton("Rechnungskopie");
        kopieButton.addActionListener(e -> kopieren());
        builder.add(kopieButton, cc.xy(colCnt, rowCnt)); // 4,6
        colCnt = 11;
        builder.addLabel("Geldeingang:", cc.xy(colCnt, rowCnt, CellConstraints.RIGHT, CellConstraints.TOP)); // 12,6

        ++colCnt;
        geldeingangTf = new JFormattedTextField(new DefaultFormatterFactory(new MoneyFormatter(), new MoneyFormatter(), new MoneyFormatter()),new Money());

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
//**********************

        rowCnt += 2;
        colCnt = 1;
        builder.add(new JSeparator(SwingConstants.HORIZONTAL), cc.xyw(colCnt, rowCnt++, 19));

        sumPan = new OffenePostenSummenPanel();
        builder.add(sumPan.getPanel(), cc.xyw(colCnt, rowCnt, 2, CellConstraints.LEFT, CellConstraints.TOP)); // 2,2

        sumPan.setValGesamtOffen(calcGesamtOffen(opListe));

        return builder.getPanel();
    }

    private List<OffenePosten> ausbuchen() {

        List<OffenePosten> opl = selectedOffenePosten();
        Money eingang;
        try {

            eingang = (Money) geldeingangTf.getValue();
        } catch (Exception e1) {
            logger.error("Fehler beim Ausbuchen",e1);
            eingang = new Money();
        }
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

    void sucheRezept(String rezept) { // Einstieg für RehaReverseServer (z.B. RGR-Kopie aus Historie)
        suchen.setText(rezept);
        combo.setSelectedItem("Rezeptnummer =");

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

    private static void verküpfen(OffenePostenJTable opJTable, JTextField eingabeFeld,
            OffenePostenComboBox opComboBox) {
        opComboBox.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
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
