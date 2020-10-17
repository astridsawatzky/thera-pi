package rehaBillEdit;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.AbstractAction;
import javax.swing.DefaultCellEditor;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;

import CommonTools.DatFunk;

class DateTableCellEditor extends DefaultCellEditor implements KeyListener {
    private static final long serialVersionUID = -2165072750853498692L;

    private JFormattedTextField ftf;

        public DateTableCellEditor() {
        super(new JFormattedTextField());
        ftf = (JFormattedTextField) getComponent();
        ftf.setDocument(new DateFieldDocument(ftf, false));
        ftf.setInputVerifier(new DateInputVerifier(ftf));
        ftf.addKeyListener(this);
        ftf.getInputMap()
           .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "check");
        ftf.getInputMap()
           .put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "check");
        ftf.getInputMap()
           .put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "check");
        ftf.getInputMap()
           .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "escape");

        ftf.getActionMap()
           .put("check", new AbstractAction() {
               private static final long serialVersionUID = 5552202939398900769L;

               @Override
               public void actionPerformed(ActionEvent e) {
                   //// System.out.println("Verify in ActionEvent =
                   //// "+ftf.getInputVerifier().verify(ftf));
                   if ((!ftf.getInputVerifier()
                            .verify(ftf))
                           || (ftf.getText()
                                  .trim()
                                  .length() == 7)
                           || (ftf.getText()
                                  .trim()
                                  .length() == 9)) {
                       //// System.out.println("Verifyer in ActionEvent "+ftf.getText());
                       ftf.setText("  .  .    ");
                       ftf.setCaretPosition(0);
                       ftf.postActionEvent();
                   } else {
                       if (!testeDatum(ftf)) {

                       }
                       ftf.postActionEvent();
                   }
               }
           });
        ftf.getActionMap()
           .put("escape", new AbstractAction() {
               private static final long serialVersionUID = -4211848212093072907L;

               @Override
               public void actionPerformed(ActionEvent e) {
                   cancelCellEditing();
                   // ftf.commitEdit(); //so use it.
                   ftf.postActionEvent(); // stop editing
               }
           });
    }

        private Character placeholder;

    /**
     * Set an Empty Character for delete the Input. If Empty Character is null, a
     * valid value need to input.
     *
     * @param c Character
     */
    public void setPlaceholder(final Character c) {
        this.placeholder = c;
    }

    /**
     * Return the char for delete the input or null if delete not allowed.
     *
     * @return Character
     */
    public Character getPlaceHolder() {
        return this.placeholder;
    }

    private boolean testeDatum(JFormattedTextField txf) {
        boolean ret = true;
        String jahr = txf.getText()
                         .substring(6);
        if (jahr.length() == 4 && jahr.subSequence(0, 1)
                .equals("0")) {
            return false;
         }
        return ret;
    }

    /** Override to invoke setValue on the formatted text field. */
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        JFormattedTextField ftf = (JFormattedTextField) super.getTableCellEditorComponent(table, value, isSelected, row,
                column);

        String insstr = "  .  .    ";
        if (value == null) {
            insstr = "  .  .    ";
        } else if (value.toString()
                        .trim()
                        .equals("")) {
            insstr = "  .  .    ";
        } else {
            try {
                insstr = DatFunk.sDatInDeutsch(value.toString());
            } catch (Exception ex) {
                insstr = "  .  .    ";
            }
        }
        ftf.setText(insstr);
        ftf.requestFocus();
        ftf.setCaretColor(Color.BLACK);
        ftf.requestFocus();
        ftf.setSelectionStart(0);
        ftf.setSelectionEnd(0);
        ftf.selectAll();
        // ftf.setSelectionEnd(insstr.length()-1);
        ftf.setCaretPosition(0);
        // System.out.println("Caret gesetzt auf ->"+ftf.getCaretPosition());

        return ftf;
    }

    /** Override to ensure that the value remains an Integer. */
    @Override
    public Object getCellEditorValue() {
        JFormattedTextField ftf = (JFormattedTextField) getComponent();
        // Object o = ftf.getValue();
        // return o.toString();
        return ftf.getText();
        /*
         * if (o instanceof Integer) { return o; } else if (o instanceof Number) {
         * return new Integer(((Number)o).intValue()); } else { if (DEBUG) {
         * //System.out.println("getCellEditorValue: o isn't a Number"); } try { return
         * integerFormat.parseObject(o.toString()); } catch (ParseException exc) {
         * System.err.println("getCellEditorValue: can't parse o: " + o); return null; }
         * }
         */
    }

    /**
     * Override to check whether the edit is valid,
     * setting the value if it is and complaining if
     * it isn't. If it's OK for the editor to go
     * away, we need to invoke the superclass's version
     * of this method so that everything gets cleaned up.
     */
    @Override
    public boolean stopCellEditing() {
        JFormattedTextField ftf = (JFormattedTextField) getComponent();
        //// System.out.println("Verify in stopCell =
        //// "+ftf.getInputVerifier().verify(ftf));
        if (((!ftf.getInputVerifier()
                 .verify(ftf))
                || (ftf.getText()
                       .trim()
                       .length() == 7)
                || (ftf.getText()
                       .trim()
                       .length() == 9)) || !testeDatum(ftf)) {
         //// System.out.println("stopCellEditing "+ftf.getText());
         ftf.setText("  .  .    ");
         ftf.setCaretPosition(0);
         return false;
      }
        /*
         * if (ftf.isEditValid()) { try { ftf.commitEdit(); } catch
         * (java.text.ParseException exc) { }
         *
         * } else { //text is invalid
         * //System.out.println("Verify = "+ftf.getInputVerifier().verify(ftf));
         * //System.out.println("Ung�ltige Eingabe ---------> "+ftf.getText()); if
         * (!userSaysRevert()) { //user wants to edit return false; //don't let the
         * editor go away } }
         */
        if (ftf.getText()
               .trim()
               .equals("")
                || ftf.getText()
                      .trim()
                      .equals(".  .")
                || ftf.getText()
                      .trim()
                      .length() != 10) {

        } else if (DatFunk.JahreDifferenz(DatFunk.sHeute(), ftf.getText()) >= 120
                || DatFunk.JahreDifferenz(DatFunk.sHeute(), ftf.getText()) <= -120) {
            JOptionPane.showMessageDialog(null, "Der eingebene Datumswert ist zwar ein kalendarisch korrektes Datum,\n"
                    + "trotzdem würde ich an Ihrer Stelle das Datum noch einmal prüfen.....");
        }
        fireEditingStopped();
        return super.stopCellEditing();
    }

    @Override
    public void keyPressed(KeyEvent arg0) {
        //// System.out.println("in DateTableCellEditor "+arg0.getKeyCode());
        if (arg0.getKeyCode() == KeyEvent.VK_DELETE) {
            ((JFormattedTextField) arg0.getSource()).setText("  .  .    ");
            ((JFormattedTextField) arg0.getSource()).setCaretPosition(0);
        }
        if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
            // fireEditingStopped();
        }
        // System.out.println("Caret gesetzt auf ->"+ftf.getCaretPosition());
    }

    @Override
    public void keyReleased(KeyEvent arg0) {
    }

    @Override
    public void keyTyped(KeyEvent arg0) {
    }
}
