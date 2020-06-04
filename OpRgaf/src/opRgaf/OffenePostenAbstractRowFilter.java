package opRgaf;

import javax.swing.RowFilter;

public abstract class OffenePostenAbstractRowFilter extends RowFilter<OffenePostenTableModel, Integer> {

    protected int columnIndex;
    protected String filtertext = "";

    public OffenePostenAbstractRowFilter() {
        super();
    }

    public String getFiltertext() {
        return filtertext;
    }

    public void setFiltertext(String filtertext) {
        this.filtertext = filtertext;
    }

    @Override
    public boolean include(Entry<? extends OffenePostenTableModel, ? extends Integer> entry) {
        return validate( entry.getValue(columnIndex));

    }

    protected abstract boolean validate(Object object) ;
}
