package opRgaf;

import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.table.TableRowSorter;

final class OffenePostenJTable extends JTable {
    TableRowSorter   <OffenePostenTableModel> sorter;
    OffenePostenJTable(OffenePostenTableModel dm) {
        super(dm);
        setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        sorter  = new TableRowSorter<>((OffenePostenTableModel)getModel());
        
    }

    void setFilter(RowFilter<OffenePostenTableModel, Integer> filter) {

            sorter.setRowFilter(filter);

        setRowSorter(sorter);
        sorter.sort();



    }


}
