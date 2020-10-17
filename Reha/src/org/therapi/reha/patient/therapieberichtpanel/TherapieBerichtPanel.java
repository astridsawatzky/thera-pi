package org.therapi.reha.patient.therapieberichtpanel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.table.AbstractTableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.therapi.reha.patient.Berichte.BerHist;

public class TherapieBerichtPanel {

    public static final Logger lOGGER = LoggerFactory.getLogger(TherapieBerichtPanel.class);
    private TherapieBerichtTableModel  dm = new TherapieBerichtTableModel();
    private JTable view = new JTable(dm);

    public Component getPanel() {

        JPanel jPanel = new JPanel(new BorderLayout());


        JToolBar toolPanel = new JToolBar();


        JButton add = new JButton(nuaber);
        add.setHideActionText(true);

        toolPanel.add(add);
        toolPanel.add(new JButton(nunicht));

        jPanel.add(toolPanel, BorderLayout.NORTH);
        jPanel.add(new JScrollPane(view), BorderLayout.CENTER);
        return jPanel;
    }

    AbstractAction nuaber = new Nuaber("nuaber", 2, KeyEvent.VK_A, (e) -> print(String.valueOf( view.getSelectedRow())),
            new ImageIcon(), new ImageIcon());

    AbstractAction nunicht = new Nuaber("nunicht", 2, KeyEvent.VK_N, (e) -> print("nunicht"),
            new ImageIcon(), new ImageIcon());


    public void setData(List<BerHist> berichte) {
        dm.setData(berichte);

    }

    boolean print(String what) {
        System.out.println(what);
        return false;
    }

    private final class Nuaber extends AbstractAction {

        private Consumer<ActionEvent> funct;

        public Nuaber(String titel, int mnemonicposition, int keyEvent, ImageIcon smallIcon, ImageIcon largeIcon) {
            putValue(Action.NAME, titel);
            putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, mnemonicposition);
            putValue(Action.SMALL_ICON, smallIcon);
            putValue(Action.LARGE_ICON_KEY, largeIcon);
            putValue(MNEMONIC_KEY, keyEvent);

        }

        public Nuaber(String titel, int mnemonicposition, int keyEvent, Consumer<ActionEvent> doit, ImageIcon smallIcon, ImageIcon largeIcon) {
            this(titel, mnemonicposition, keyEvent, smallIcon, largeIcon);
            funct = doit;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println(e.getSource());
            System.out.println(e.getModifiers());
            funct.accept(e);
        }

    }

    private final class TherapieBerichtTableModel extends AbstractTableModel {

        private static final int ID_COL = 0;
        private static final int TITEL_COL = 1;
        private static final int VERFASSER_COL = 2;
        private static final int ERSTELL_COL = 3;
        private static final int EMPFAENGER_COL = 4;
        private static final int EDIT_COL = 5;
        private static final String ID = "Id";
        private static final String TITEL = "Titel";
        private static final String VERFASSER = "Verfasser";
        private static final String ERSTELLT = "erstellt";
        private static final String EMPFAENGER = "Empfänger";
        private static final String EDIT = "geändert am";
        private List<BerHist> liste = new ArrayList<>();
        private List<String> columns = Arrays.asList(new String[] { ID, TITEL, VERFASSER, ERSTELLT, EMPFAENGER, EDIT });

        @Override
        public String getColumnName(int column) {
            return columns.get(column);
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            BerHist history = liste.get(rowIndex);

            switch (columnIndex) {
            case ID_COL:
                return history.getBerichtId();
            case TITEL_COL:
                return history.getBerTitel();
            case VERFASSER_COL:
                return history.getVerfasser();
            case ERSTELL_COL:
                return history.getErstellDat();
            case EMPFAENGER_COL:
                return history.getEmpfaenger();
            case EDIT_COL:
                return history.getEditDat();

            default:
                lOGGER.error("unknown column index: " + columnIndex);
                return "";
            }

        }



        @Override
        public int getRowCount() {
            return liste.size();
        }

        @Override
        public int getColumnCount() {

            return columns.size();
        }

        public void empty() {

            liste.clear();
        }

        public void addAll(List<BerHist> berichte) {
            liste.addAll(berichte);
            fireTableDataChanged();

        }

        private void setData(List<BerHist> berichte) {
            empty();
            addAll(berichte);
        }
    }

}
