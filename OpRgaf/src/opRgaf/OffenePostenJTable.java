package opRgaf;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.table.TableRowSorter;

final class OffenePostenJTable extends JTable {
    private final class Everythingisfine extends RowFilter<OffenePostenTableModel, Integer> {
        @Override
        public boolean include(Entry<? extends OffenePostenTableModel, ? extends Integer> entry) {
            return true;
        }
    }

    TableRowSorter   <OffenePostenTableModel> sorter;
    private RowFilter<OffenePostenTableModel, Integer> contentfilter = new Everythingisfine();
    private RowFilter<OffenePostenTableModel, Integer> typefilter = new Everythingisfine();



    OffenePostenJTable(OffenePostenTableModel dm) {
        super(dm);
        setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        sorter  = new TableRowSorter<>((OffenePostenTableModel)getModel());

    }

    void setFilter(RowFilter<OffenePostenTableModel, Integer> filter) {

        contentfilter=filter;

        sorter.setRowFilter(RowFilter.andFilter(Arrays.asList(filter,typefilter)));

        setRowSorter(sorter);
        sorter.sort();



    }

    void setTypeFilter(RowFilter<OffenePostenTableModel, Integer> filter) {

        typefilter=filter;

        sorter.setRowFilter(RowFilter.andFilter(Arrays.asList(contentfilter,filter)));

        setRowSorter(sorter);
        sorter.sort();
    }





}
