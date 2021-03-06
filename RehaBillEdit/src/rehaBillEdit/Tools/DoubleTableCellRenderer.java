package rehaBillEdit.Tools;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class DoubleTableCellRenderer extends DefaultTableCellRenderer
{
    private static final long serialVersionUID = -1029644753226393604L;

    @Override
    public Component getTableCellRendererComponent(final JTable table, final Object value, boolean isSelected,
            boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        if (value instanceof Double) {
            Double d = (Double) value;
            setText(String.format("%.2f", d));
        } else {
            setText(value.toString());
        }
        return this;
    }
}
