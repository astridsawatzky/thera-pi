package jxTableTools;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;

public class CheckRenderer extends JCheckBox implements TableCellRenderer {
    /**
     * 
     */
    private static final long serialVersionUID = 1154825134666384570L;
    Border unselectedBorder = null;
    Border selectedBorder = null;
    boolean isBordered = true;

    public CheckRenderer(boolean isBordered) {
        this.isBordered = isBordered;
        setOpaque(true); // MUST do this for background to show up.
        setVerticalAlignment(SwingConstants.CENTER);
        setHorizontalAlignment(SwingConstants.CENTER);

    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
            int row, int column) {

        if (isSelected) {
            setBackground(table.getSelectionBackground());
        } else {
            setBackground(table.getBackground());
        }
        if (isBordered) {
            if (isSelected) {
                if (selectedBorder == null) {
                    selectedBorder = BorderFactory.createMatteBorder(2, 5, 2, 5, table.getSelectionBackground());
                }
                setBorder(selectedBorder);
            } else {
                if (unselectedBorder == null) {
                    unselectedBorder = BorderFactory.createMatteBorder(2, 5, 2, 5, table.getBackground());
                }
                setBorder(unselectedBorder);
            }
        }

        return this;
    }
}
