package rehaBillEdit;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.JFormattedTextField;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;

class DateFieldDocument extends javax.swing.text.PlainDocument {
    private static final long serialVersionUID = 1L;
    /*** Attribute
     * private static final String JAHR = "0123456789";// Erlaubte Ziffern Jahr
     * Erlaubte Ziffern Tag 10er.
     */
    private static final String DREI = "0123";
    /** Erlaubte Zeichen Monat 10er. */
    private static final String MONAT = "01";
    /** Calender fuers init. */
    private Calendar initDate = new GregorianCalendar();
    /** Voreingestellter String. */
    private String initString;
    /** Position vor dem Trenner. */
    private static int trenner1 = 2, trenner2 = 5;
    /** F�r Referenz auf das TextFeld. */
    private JTextField textComponent;
    /** Caret Position bei Trennern. */
    private int newOffset;
    private boolean init;
    /** Konv. */
    private SimpleDateFormat datumsFormat = new SimpleDateFormat("dd.MM.yyyy");
    /*** Attribute Ende

     * **** Konstruktor 1.
     */
    public DateFieldDocument(JFormattedTextField textComponent, boolean datumHeute) {
        this.textComponent = textComponent; // Hiermit wird jetzt gearbeitet
        initDate.setTime(new Date()); // Kalender auf heute
        initString = datumsFormat.format(initDate.getTime()); // Nach String

        try { // Jetzt den Inhalt mit dem Datum
            if (datumHeute) {
                insertString(0, initString, null); // initialisieren
            } else {
                insertString(0, "  .  .    ", null); // initialisieren
            }
            this.init = true;
        } catch (Exception KonstrEx) {
            KonstrEx.printStackTrace();
        }
        //// System.out.println("In Exception 1 KonstrEX - Zeichen = ");
    }

        /*** �berschreiben Insert-Methode. */
    @Override
    public void insertString(int offset, String zeichen, AttributeSet attributeSet) throws BadLocationException {
        //// System.out.println("In insert String - Zeichen = "+zeichen);
        if (zeichen.equals(initString) || "  .  .    ".equals(zeichen)) { // Wenn initString oder leeres Datum, gleich
                                                                          // rein
            if (zeichen.equals("  .  .    ")) {
                if (!this.init) {
                    super.insertString(0, "  .  .    ", attributeSet);
                } else {
                    super.remove(0, 10);
                    super.insertString(0, "  .  .    ", attributeSet);
                }
            } else if (!this.init) {
                super.insertString(0, zeichen, attributeSet);
            } else {
                super.remove(0, 10);
                super.insertString(0, zeichen, attributeSet);
            }
            // super.insertString(offset, zeichen, attributeSet);
        } else if (zeichen.length() == 10) { // Wenn komplettes Datum, und
            if ("  .  .    ".equals(zeichen)) { // richtig, dann rein
            }
            super.remove(0, 10);
            super.insertString(0, zeichen, attributeSet);
        } else if (zeichen.length() == 1) { // Wenn nicht, nur Einzelzeichen
            try { // annehmen
                Integer.parseInt(zeichen);
            } catch (Exception NumEx) { // Kein Integer?
                return; // Keine Verarbeitung!
            }
            if (offset == 0 && DREI.indexOf(String.valueOf(zeichen.charAt(0))) == -1) {
                // Toolkit.getDefaultToolkit().beep();
                return;
            }
            if (offset == 1 && textComponent.getText()
                             .substring(0, 1)
                             .equals("3")) {
               int tag = new Integer(zeichen).intValue();
               if (tag > 1) {
                  // Toolkit.getDefaultToolkit().beep();
                  return;
               }
            }
            if (offset == 1 && textComponent.getText()
                             .substring(0, 1)
                             .equals("0")) {
               int tag = new Integer(zeichen).intValue();
               if (tag == 0) {
                  // Toolkit.getDefaultToolkit().beep();
                  return;
               }
            }
            if ((offset == 2 && MONAT.indexOf(String.valueOf(zeichen.charAt(0))) == -1) || (offset == 3 && MONAT.indexOf(String.valueOf(zeichen.charAt(0))) == -1)) {
                // Toolkit.getDefaultToolkit().beep();
                return;
            }
            if (offset == 4 && textComponent.getText()
                             .substring(3, 4)
                             .equals("1")) {
               int monat = new Integer(zeichen).intValue();
               if (monat > 2) {
                  // Toolkit.getDefaultToolkit().beep();
                  return;
               }
            }
            if (offset == 4 && textComponent.getText()
                             .substring(3, 4)
                             .equals("0")) {
               int monat = new Integer(zeichen).intValue();
               if (monat == 0) {
                  // Toolkit.getDefaultToolkit().beep();
                  return;
               }
            }

            newOffset = offset;
            if (atSeparator(offset)) { // Wenn am trenner, dann den offset
                newOffset++; // vor dem einf�gen um 1 verschieben
                textComponent.setCaretPosition(newOffset);
            }
            super.remove(newOffset, 1); // Aktuelles zeichen entfernen
            super.insertString(newOffset, zeichen, attributeSet); // Neues einf�gen
        }
    }
    /*** �berschreiben Insert Ende

     * **** �berschreiben Remove.
     */
    @Override
    public void remove(int offset, int length) throws BadLocationException {
        if (atSeparator(offset)) {
            textComponent.setCaretPosition(offset - 1);
        } else {
            textComponent.setCaretPosition(offset);
        }
    }
    /*** �berschreiben Remove Ende

     * **** Hilfsmethode f�r die Punkte zwischen den Feldern.
     */
    private boolean atSeparator(int offset) {
        return offset == trenner1 || offset == trenner2;
    }
    // **** Hilfsmethode Ende
}
