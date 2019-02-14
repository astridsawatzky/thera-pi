package arztBaustein;

import javax.swing.table.DefaultTableModel;

class MyBausteinTableModel extends DefaultTableModel {
    private static final long serialVersionUID = 1L;

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == 1 || columnIndex == 5) {
            return String.class;
        } else {
            return String.class;
        }
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        if (col == 0) {
            return false;
        } else if (col == 3) {
            return false;
        } else if (col == 7) {
            return false;
        } else if (col == 11) {
            return false;
        } else {
            return false;
        }
    }

}