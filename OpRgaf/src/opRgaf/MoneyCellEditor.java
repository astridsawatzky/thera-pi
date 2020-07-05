package opRgaf;

import java.awt.Component;
import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.DefaultCellEditor;
import javax.swing.JFormattedTextField;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.text.NumberFormatter;

import opRgaf.rezept.Money;

public class MoneyCellEditor extends DefaultCellEditor {

    public MoneyCellEditor() {
        super(new JFormattedTextField());
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        JFormattedTextField editor = (JFormattedTextField) super.getTableCellEditorComponent(table, value, isSelected,
                row, column);
        ((JFormattedTextField) editor).selectAll();
        if (value instanceof Number) {
            Locale myLocale = Locale.getDefault();

            NumberFormat numberFormatB = NumberFormat.getInstance(myLocale);
            numberFormatB.setMaximumFractionDigits(2);
            numberFormatB.setMinimumFractionDigits(2);
            numberFormatB.setMinimumIntegerDigits(1);

            editor.setFormatterFactory(
                    new javax.swing.text.DefaultFormatterFactory(new NumberFormatter(numberFormatB)));

            editor.setHorizontalAlignment(SwingConstants.RIGHT);
            editor.setValue(value);
        }
        return editor;
    }

    @Override
    public boolean stopCellEditing() {
        try {
            // try to get the value
            this.getCellEditorValue();
            return super.stopCellEditing();
        } catch (Exception ex) {
            return false;
        }

    }

    @Override
    public Object getCellEditorValue() {
        // get content of textField
        String str = (String) super.getCellEditorValue();
        if (str == null) {
            return Money.ZERO;
        }

        if (str.length() == 0) {
            return Money.ZERO;
        }

        return new Money(str);

    }

}
