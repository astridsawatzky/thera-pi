package opRgaf;

import javax.swing.RowFilter;

public abstract class OffenePostenAbstractRowFilter extends RowFilter<OffenePostenTableModel, Integer> {

    protected int columnIndex;
    private String filtertext = "";

    public OffenePostenAbstractRowFilter() {
        super();
    }

    public String getFiltertext() {
        return filtertext;
    }

    public void setFiltertext(String filtertext) {
        this.filtertext = filtertext;
    }

}
