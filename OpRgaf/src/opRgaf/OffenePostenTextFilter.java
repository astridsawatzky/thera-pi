package opRgaf;

class OffenePostenTextFilter extends OffenePostenAbstractRowFilter {



    public OffenePostenTextFilter(int ColumnIdx) {
        columnIndex = ColumnIdx;
    }
    public OffenePostenTextFilter(int ColumnIdx,String text) {
        columnIndex = ColumnIdx;
        filtertext=text;
    }



    @Override
    protected boolean validate(Object object) {
        return ((String)object).toLowerCase().contains(getFiltertext().toLowerCase());
    }


}
