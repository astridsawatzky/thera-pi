package opRgaf;

import javax.swing.RowFilter;
import javax.swing.RowFilter.Entry;

final class OffenePostenRowFilter extends RowFilter<OffenePostenTableModel, Integer> {
    @Override
    public boolean include(Entry<? extends OffenePostenTableModel, ? extends Integer> entry) {
        return ((String) entry.getValue(OffenePostenTableModel.KENNUNG)).toLowerCase().contains(getFiltertext());
    }
    private String filtertext = "abd";
    public String getFiltertext() {
        return filtertext;
    }

    public void setFiltertext(String filtertext) {
        this.filtertext = filtertext;
    }
}