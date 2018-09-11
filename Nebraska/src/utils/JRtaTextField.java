package utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.JFormattedTextField;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;

import CommonTools.JRtaTextField;




/*************************************************/
class NurZahlenDocument extends javax.swing.text.PlainDocument
{
/**
	 * 
	 */
	private static final long serialVersionUID = -29699654036665632L;
	private JTextField textField;
	private String text;

	public NurZahlenDocument(JFormattedTextField tf)
	{
		textField = tf;
	}

	@Override
    public void insertString(int offs, String str, javax.swing.text.AttributeSet a)
	throws javax.swing.text.BadLocationException
	{
		try
		{
			super.insertString(offs, str, a);
			text = textField.getText();
			if ((text.length() == 1) & (text.equals("-")))
				return;
			Long.parseLong(text);
		}
		catch (NumberFormatException e)
		{
			super.remove(offs, 1);
		}
	}
}
/*************************************************/

class NurNormalDocument extends javax.swing.text.PlainDocument
{
/**
	 * 
	 */
	private static final long serialVersionUID = -1708532746033381872L;
	private JTextField textField;
	private String text;

	public NurNormalDocument(JFormattedTextField tf)
	{
		textField = tf;
	}

	@Override
    public void insertString(int offs, String str, javax.swing.text.AttributeSet a)
	throws javax.swing.text.BadLocationException
	{
		try
		{
			text = textField.getText().trim();
			if(text.length() > 0){
				if(!str.substring(offs,1).equals(" ")){
					super.insertString(offs,StringTools.EGross(str), a);					
				}else{
					super.insertString(offs,str, a);
				}
				
			}else{
				super.insertString(offs,str, a);
			}

			return;
			//Integer.parseInt(text);
		}
		catch (NumberFormatException e)
		{
			super.remove(offs, 1);
			//Toolkit.getDefaultToolkit().beep();
		}

	}
}

/*************************************************/

class NurGrossDocument extends javax.swing.text.PlainDocument
{
/**
	 * 
	 */
	private static final long serialVersionUID = -1708532746033381872L;
	private JTextField textField;
	public NurGrossDocument(JFormattedTextField tf)
	{
		textField = tf;
	}

	@Override
    public void insertString(int offs, String str, javax.swing.text.AttributeSet a)
	throws javax.swing.text.BadLocationException
	{
		try
		{
			super.insertString(offs,JRtaTextField.toRtaUpper(str), a);
			
			return;
		}
		catch (NumberFormatException e)
		{
			super.remove(offs, 1);
		}

	}
}

/*************************************************/

class NurKleinDocument extends javax.swing.text.PlainDocument
{
/**
	 * 
	 */
	private static final long serialVersionUID = -1708532746033381872L;
	private JTextField textField;
	public NurKleinDocument(JFormattedTextField tf)
	{
		textField = tf;
	}

	@Override
    public void insertString(int offs, String str, javax.swing.text.AttributeSet a)
	throws javax.swing.text.BadLocationException
	{
		try
		{
			super.insertString(offs,str.toLowerCase(), a);
			return;
		}
		catch (NumberFormatException e)
		{
			super.remove(offs, 1);
		}

	}
}
/*************************************************/

class NurStundenDocument extends javax.swing.text.PlainDocument
{
/**
	 * 
	 */
	private static final long serialVersionUID = -29699654036665632L;
	private JTextField textField;
	private String text;

	public NurStundenDocument(JFormattedTextField tf)
	{
		textField = tf;
	}

	@Override
    public void insertString(int offs, String str, javax.swing.text.AttributeSet a)
	throws javax.swing.text.BadLocationException
	{
		try
		{
			super.insertString(offs, str, a);
			text = textField.getText();
			if (Integer.valueOf(text) > 24){
				super.remove(offs, 1);
				return;
			}
			if ((text.length() == 1) & (text.equals("-")))
				return;
			Integer.parseInt(text);
		}
		catch (NumberFormatException e)
		{
			super.remove(offs, 1);
			//Toolkit.getDefaultToolkit().beep();
		}
	}
}
/*************************************************/
class NurMinutenDocument extends javax.swing.text.PlainDocument
{
/**
	 * 
	 */
	private static final long serialVersionUID = -29699654036665632L;
	private JTextField textField;
	private String text;

	public NurMinutenDocument(JFormattedTextField tf)
	{
		textField = tf;
	}

	@Override
    public void insertString(int offs, String str, javax.swing.text.AttributeSet a)
	throws javax.swing.text.BadLocationException
	{
		try
		{
			super.insertString(offs, str, a);
			text = textField.getText();
			if (Integer.valueOf(text) > 59){
				super.remove(offs, 1);
				return;
			}
			if ((text.length() == 1) & (text.equals("-")))
				return;
			Integer.parseInt(text);
		}
		catch (NumberFormatException e)
		{
			super.remove(offs, 1);
		}
	}
}
/*************************************************/

/*****************************************************************/
class DateFieldDocument extends javax.swing.text.PlainDocument {
	 // **** Attribute
	 private static final String DREI  = "0123";// Erlaubte Ziffern Tag 10er
	 private static final String MONAT = "01";  // Erlaubte Zeichen Monat 10er
	 private Calendar initDate = new GregorianCalendar(); // Calender fuers init
	 private String initString;                 // Voreingestellter String
	 private static int trenner1 = 2, trenner2 = 5;  // Position vor dem Trenner
	 private JFormattedTextField textComponent;      // Für Referenz auf das TextFeld
	 private int newOffset;                     // Caret Position bei Trennern
	 private boolean init = false;
	 SimpleDateFormat datumsFormat = new SimpleDateFormat ("dd.MM.yyyy"); //Konv.
	 // **** Attribute Ende

	 // **** Konstruktor 1
	 public DateFieldDocument(JFormattedTextField textComponent,boolean datumHeute) { 
	  this.textComponent = textComponent;       // Hiermit wird jetzt gearbeitet
	  initDate.setTime(new Date());             // Kalender auf heute
	  initString = datumsFormat.format(initDate.getTime()); // Nach String
	  
	  try {                                     // Jetzt den Inhalt mit dem Datum
		  if(datumHeute){
		  insertString(0, initString, null);       // initialisieren
		  this.init = true;
		  } else{
		  insertString(0, "  .  .    ", null);       // initialisieren
		  this.init = true;
		  }
	  }
	  catch(Exception KonstrEx) { KonstrEx.printStackTrace(); }
	 }
	 // **** Konstruktor 1 Ende
	 // **** Konstruktor 2
	 public DateFieldDocument(JRtaTextField textComponent, Calendar givenDate){ 
	  this.textComponent = textComponent;       // Hiermit wird jetzt gearbeitet
	  initDate=givenDate;                       // Kalender auf Parameter
	  initString = datumsFormat.format(initDate.getTime()); // Nach String
	  try {                                     // Jetzt den Inhalt mit dem Datum
	   insertString(0, initString, null);       // initialisieren
	  }
	  catch(Exception KonstrEx) { KonstrEx.printStackTrace(); }
	 }
	 // **** Konstruktor 2 Ende

	 // **** Überschreiben Insert-Methode
	 @Override
    public void insertString(int offset, String zeichen, 
	       AttributeSet attributeSet) 
	       throws BadLocationException {
	  if(zeichen.equals(initString) || zeichen.equals("  .  .    ")) { // Wenn initString oder leeres Datum, gleich rein
		  if (zeichen.equals("  .  .    ") ){
			  if(!this.init){
				  super.insertString(0, "  .  .    ", attributeSet);
			  }else{
				  super.remove(0, 10);
				  super.insertString(0, "  .  .    ", attributeSet);
			  }
		  }else{
			  if(!this.init){
				  super.insertString(0, zeichen, attributeSet);
			  }else{
				  super.remove(0, 10);
				  super.insertString(0, zeichen, attributeSet);
			  }
			  //super.insertString(offset, zeichen, attributeSet);			  
		  }

	  }
	  else if(zeichen.length()==10) {           // Wenn komplettes Datum, und
		  if (zeichen.equals("  .  .    ")) {        // richtig, dann rein
			  super.remove(0, 10);
			  super.insertString(0, zeichen, attributeSet);
		  }else{
			  super.remove(0, 10);
			  super.insertString(0, zeichen, attributeSet);		   
		  }
	  }
	  else if(zeichen.length()==1) {            // Wenn nicht, nur Einzelzeichen
	   try {                                    // annehmen
	    Integer.parseInt(zeichen);
	   }
	   catch(Exception NumEx) {                 // Kein Integer?
	    return;                                 // Keine Verarbeitung!
	   }
	   if(offset==0) {                          // Tage auf 10 20 30 prüfen
	    if( DREI.indexOf( String.valueOf(zeichen.charAt(0) ) ) == -1 ) {
	     //Toolkit.getDefaultToolkit().beep();
	     return;
	    }
	   }
	   if(offset==1) {                          // Tage 32-39 unterbinden
	    if(textComponent.getText().substring(0, 1).equals("3")) {
	     int tag = Integer.valueOf(zeichen).intValue();
	     if(tag>1) {
	      //Toolkit.getDefaultToolkit().beep();
	      return;
	     }
	    }
	   }
	   if(offset==1) {                          // Tag 00 unterbinden
	    if(textComponent.getText().substring(0, 1).equals("0")) {
	     int tag = Integer.valueOf(zeichen).intValue();
	     if(tag==0) {
	      //Toolkit.getDefaultToolkit().beep();
	      return;
	     }
	    }
	   }
	   if(offset==2) {                         // Monate auf 0x-1x prüfen
	                                           // (Caret links vom Trenner)
	    if( MONAT.indexOf( String.valueOf(zeichen.charAt(0) ) ) == -1 ) {
	     //Toolkit.getDefaultToolkit().beep();
	     return;
	    }
	   }
	   if(offset==3) {                         // Monate auf 0x-1x prüfen
	                                           // (Caret rechts vom Trenner)
	    if( MONAT.indexOf( String.valueOf(zeichen.charAt(0) ) ) == -1 ) {
	     //Toolkit.getDefaultToolkit().beep();
	     return;
	    }
	   }
	   if(offset==4) {                         // Monate 13-19 unterbinden
	    if(textComponent.getText().substring(3, 4).equals("1")) {
	     int monat = Integer.valueOf(zeichen).intValue();
	     if(monat>2) {
	      //Toolkit.getDefaultToolkit().beep();
	      return;
	     }
	    }
	   }
	   if(offset==4) {                         // Monat 00 unterbinden
	         if(textComponent.getText().substring(3, 4).equals("0")) {
	     int monat = Integer.valueOf(zeichen).intValue();
	     if(monat==0) {
	      //Toolkit.getDefaultToolkit().beep();
	      return;
	     }
	    }
	   }



	   newOffset = offset;
	   if(atSeparator(offset)) {             // Wenn am trenner, dann den offset
	    newOffset++;                         // vor dem einfügen um 1 verschieben
	    textComponent.setCaretPosition(newOffset);
	   }
	   super.remove(newOffset, 1);           // Aktuelles zeichen entfernen
	   super.insertString(newOffset, zeichen, attributeSet);    // Neues einfügen
	  }
	 }
	 // **** Überschreiben Insert Ende

	 // **** Überschreiben Remove
	 @Override
    public void remove(int offset, int length) 
	       throws BadLocationException {
	  if(atSeparator(offset)) 
	   textComponent.setCaretPosition(offset-1);
	  else
	   textComponent.setCaretPosition(offset);
	 }
	 // **** Überschreiben Remove Ende

	 // **** Hilfsmethode für die Punkte zwischen den Feldern
	 private boolean atSeparator(int offset) {
	  return offset == trenner1 || offset == trenner2;
	 }
	 // **** Hilfsmethode Ende
	}

