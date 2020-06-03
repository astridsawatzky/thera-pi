package opRgaf;

final class OffenePostenTextFilter extends OffenePostenAbstractRowFilter {



    public OffenePostenTextFilter(int ColumnIdx) {
        columnIndex = ColumnIdx;
    }



    @Override
    protected boolean validate(Object object) {
        return ((String)object).toLowerCase().contains(getFiltertext().toLowerCase());
    }


}
