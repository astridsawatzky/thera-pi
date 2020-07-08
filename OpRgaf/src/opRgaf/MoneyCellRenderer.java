package opRgaf;

import java.awt.Color;
import java.awt.Component;
import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import opRgaf.rezept.Money;
import sun.swing.DefaultLookup;

public class MoneyCellRenderer extends DefaultTableCellRenderer {

    private static final NumberFormat currencyFormat = NumberFormat.getNumberInstance(Locale.GERMANY);
    static {
        currencyFormat.setMinimumFractionDigits(2);
        currencyFormat.setGroupingUsed(true);

    }

    @Override
    public Component getTableCellRendererComponent(final JTable table, final Object value, boolean isSelected,
            boolean hasFocus, int row, int column) {

        if (value instanceof Money) {
            setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            Money moneyValue = (Money) value;

            setText(currencyFormat.format(moneyValue.getValue()));
        }
        if (isSelected) {
            super.setForeground(table.getSelectionForeground());
            super.setBackground(table.getSelectionBackground());
        } else {

            super.setForeground(table.getForeground());
            super.setBackground(table.getBackground());
        }
        return this;

    }

}
