package rehaUrlaub.Tools;

import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingx.JXTable;

public class TableTool {

    public static void setzeRow(JXTable table) {

    }

    public static int loescheRow(JXTable table, int row) {
        int ret = -1;
        int currow = table.getSelectedRow();
        int countrow = table.getRowCount() - 1;
        if (currow == -1) {
            return -1;
        }
        ((DefaultTableModel) table.getModel()).removeRow(currow);

        if (countrow > currow) {
            table.setRowSelectionInterval(currow, currow);
            return table.getSelectedRow();
        } else if (countrow == 0) {
            return -1;

        } else {
            table.setRowSelectionInterval(countrow - 1, countrow - 1);
            return table.getSelectedRow();
        }
    }

    public static int loescheRowAusModel(JXTable table, int row) {
        int ret = -1;
        int currow = table.convertRowIndexToModel(table.getSelectedRow());
        int countrow = table.getRowCount() - 1;
        if (currow == -1) {
            return -1;
        }

        ((DefaultTableModel) table.getModel()).removeRow(currow);

        if (countrow > currow) {
            table.setRowSelectionInterval(currow, currow);
            return table.getSelectedRow();
        } else if (countrow == 0) {
            return -1;

        } else {
            table.setRowSelectionInterval(countrow - 1, countrow - 1);
            return table.getSelectedRow();
        }
    }

}
