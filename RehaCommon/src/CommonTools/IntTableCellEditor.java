package CommonTools;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellEditor;

public class IntTableCellEditor extends AbstractCellEditor implements KeyListener, TableCellEditor {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /**
     * 
     */

    private JComponent component = new JRtaTextField("ZAHLEN", true);
    private boolean mitMaus = false;

    public IntTableCellEditor() {
        component.addKeyListener(this);
        ((JRtaTextField) component).listenerLoeschen();
    }

    @Override
    public Object getCellEditorValue() {
        String foo;
        try {
            foo = ((JFormattedTextField) component).getText()
                                                   .replace(".", "")
                                                   .trim();
            if (foo.length() == 0) {
                foo = "0";
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            foo = "0";
        }
        Integer i_spent_hours_on_this = Integer.valueOf(foo);
        return i_spent_hours_on_this;
    }

    // This method is called when a cell value is edited by the user.
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        // Configure the component with the specified value
        if (!mitMaus) {
            ((JFormattedTextField) component).setText(String.valueOf(value));
            ((JFormattedTextField) component).selectAll();
            ((JFormattedTextField) component).setHorizontalAlignment(SwingConstants.RIGHT);

        } else {
            final String xvalue = String.valueOf(value);
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    ((JFormattedTextField) component).setText(String.valueOf(xvalue)
                                                                    .replace(".", ""));
                    ((JFormattedTextField) component).selectAll();
                    ((JFormattedTextField) component).setHorizontalAlignment(SwingConstants.RIGHT);
                    ((JFormattedTextField) component).setCaretPosition(0);
                }
            });

        }

        // Return the configured component
        //// System.out.println("I've been Called!!");
        return component;
    }

    @Override
    public boolean isCellEditable(EventObject evt) {
        if (evt instanceof MouseEvent) {
            if (((MouseEvent) evt).getClickCount() == 2) {
                ((MouseEvent) evt).consume();
                mitMaus = true;
                return true;
            }
        } else {
            mitMaus = false;
            return true;
        }
        return false;
    }

    @Override
    public void keyPressed(KeyEvent arg0) {

        if (mitMaus && arg0.getKeyCode() == KeyEvent.VK_ENTER) {
            //// System.out.println("in Maus + Return gedrückt");
            this.fireEditingStopped();
        }
        if (arg0.getKeyCode() == KeyEvent.VK_ESCAPE) {
            //// System.out.println("in Maus + Return gedrückt");
            this.fireEditingCanceled();
        }

    }

    @Override
    public void keyReleased(KeyEvent arg0) {

    }

    @Override
    public void keyTyped(KeyEvent arg0) {

    }

}