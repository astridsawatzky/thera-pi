package opRgaf;

import javax.swing.DefaultCellEditor;
import javax.swing.JFormattedTextField;
import javax.swing.text.DefaultFormatterFactory;

import opRgaf.rezept.Money;
import opRgaf.rezept.MoneyFormatter;

public class MoneyCellEditor extends DefaultCellEditor {

    public MoneyCellEditor() {
        super(new JFormattedTextField(
                new DefaultFormatterFactory(new MoneyFormatter(), new MoneyFormatter(), new MoneyFormatter()),
                new Money()));
    }

    @Override
    public Object getCellEditorValue() {
        return ((JFormattedTextField) editorComponent).getValue();
    }
}
