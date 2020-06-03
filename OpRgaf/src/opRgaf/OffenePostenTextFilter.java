package opRgaf;

final class OffenePostenTextFilter extends OffenePostenAbstractRowFilter {



    public OffenePostenTextFilter(int ColumnIdx) {
        columnIndex = ColumnIdx;
    }

    @Override
    public boolean include(Entry<? extends OffenePostenTableModel, ? extends Integer> entry) {
        System.out.println(getFiltertext());
        return ((String) entry.getValue(columnIndex)).toLowerCase().contains(getFiltertext().toLowerCase());
    }
}
