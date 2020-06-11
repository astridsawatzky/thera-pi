package opRgaf;

import java.awt.Component;
import java.time.LocalDate;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import opRgaf.CommonTools.DateTimeFormatters;

public class DateRenderer extends DefaultTableCellRenderer {



        @Override
        public Component getTableCellRendererComponent(final JTable table, final Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (value instanceof LocalDate) {
                setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
                LocalDate dateValue = (LocalDate) value;
                setText(dateValue.format(DateTimeFormatters.ddMMYYYYmitPunkt));
            }
            return this;

        }


}
