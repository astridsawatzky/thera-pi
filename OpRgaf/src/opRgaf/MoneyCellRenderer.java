package opRgaf;

import java.awt.Component;
import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import opRgaf.rezept.Money;

public class MoneyCellRenderer extends DefaultTableCellRenderer {

    private static final NumberFormat currencyFormat = NumberFormat.getNumberInstance(Locale.GERMANY);
    static {
        currencyFormat.setMinimumFractionDigits(2);
        currencyFormat.setGroupingUsed(true);

    }

    @Override
    public Component getTableCellRendererComponent(final JTable table, final Object value, boolean isSelected,
            boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if (value instanceof Money) {
            setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            Money moneyValue = (Money) value;

            setText(currencyFormat.format(moneyValue.getValue()));
        }
        return this;

    }

}