package opRgaf;

import javax.swing.JTable;
import javax.swing.RowFilter;

final class OffenePostenJTable extends JTable {

    OffenePostenJTable(OffenePostenTableModel dm) {
        super(dm);
        setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

    }

    void setFilter(RowFilter<OffenePostenTableModel, Integer> filter) {
      setRowSorter(((OffenePostenTableModel) getModel()).setFilter(filter));

    }
}
