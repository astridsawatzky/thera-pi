package kurzAufrufe;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import com.sun.star.beans.PropertyVetoException;
import com.sun.star.beans.UnknownPropertyException;
import com.sun.star.container.NoSuchElementException;
import com.sun.star.lang.IllegalArgumentException;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.uno.UnoRuntime;

import CommonTools.DatFunk;
import CommonTools.SqlInfo;
import ag.ion.bion.officelayer.application.OfficeApplicationException;
import ag.ion.bion.officelayer.document.DocumentDescriptor;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.document.IDocumentService;
import ag.ion.bion.officelayer.internal.text.TextTableColumn;
import ag.ion.bion.officelayer.text.ITextDocument;
import ag.ion.bion.officelayer.text.ITextTable;
import ag.ion.bion.officelayer.text.ITextTableCellRange;
import ag.ion.bion.officelayer.text.TextException;
import ag.ion.noa.NOAException;
import hauptFenster.Reha;
import oOorgTools.OOTools;

public class KurzAufrufe {
    public static void starteFunktion(String funktion, Object obj1, Object obj2) {
        switch (funktion) {
        case "Akutliste":
            try {
                new AkutListe();
            } catch (TextException e) {

                e.printStackTrace();
            }
            break;
        case "Telefonliste":
            try {
                new TelefonListe(obj1);
            } catch (TextException e) {

                e.printStackTrace();
            }
            break;
        case "RettungsAnker":
            try {
                new F2RettungsAnker();
            } catch (TextException e) {
                e.printStackTrace();
            }
            break;
        }
    }
}
/*******************************************************************************************************/
class AkutListe{
	Vector<Vector<String>> vec = null;
	String felder = "therapeut,n_name,v_name,telefonp,telefong,telefonm,emaila,termine1,termine2,akutdat,akutbis";
	public AkutListe() throws TextException{
		vec = SqlInfo.holeSaetze("pat5", felder, "akutpat='T' order by therapeut", Arrays.asList(new String[] {}));
		int lang;
		if( (lang = vec.size()) > 0){
			IDocumentService documentService = null;
			try {
				if(!Reha.officeapplication.isActive()){
					Reha.starteOfficeApplication();
				}
				documentService = Reha.officeapplication.getDocumentService();
			} catch (OfficeApplicationException e) {
				System.out.println("Die Java-Access-Bridge ist zusammengebrochen...");
				e.printStackTrace();
			}
			IDocument document = null;
			try {
				DocumentDescriptor docdecript = new DocumentDescriptor();
				docdecript.setHidden(true);
				document = documentService.constructNewDocument(IDocument.WRITER, docdecript);

				//document = documentService.constructNewDocument(IDocument.WRITER, DocumentDescriptor.DEFAULT);
			} catch (NOAException e) {
				e.printStackTrace();
			}

			ITextDocument textDocument = (ITextDocument)document;
			/*
			IParagraph paragraph =
		  		textDocument.getTextService().getTextContentService().constructNewParagraph();
			*/
			try {
				OOTools.setzePapierFormat(textDocument, 21000, 29700);
			} catch (NoSuchElementException | WrappedTargetException | UnknownPropertyException | PropertyVetoException | IllegalArgumentException e1) {
				
				e1.printStackTrace();
			}
			/*
			 * Saichtext basteln und einsetzen
			 */
			ITextTable textTable = null;
			try {
				textTable = textDocument.getTextTableService().constructTextTable(lang+1, 5);

			} catch (TextException e) {
				e.printStackTrace();
			}
			try {

				textDocument.getTextService().getTextContentService().insertTextContent(textTable);
			} catch (TextException e) {
				e.printStackTrace();
			}
			try {
				textTable.getCell(0,0).getTextService().getText().setText("Behandler");
				textTable.getCell(1,0).getTextService().getText().setText("Patient");
				textTable.getCell(2,0).getTextService().getText().setText("Kontakt");
				textTable.getCell(3,0).getTextService().getText().setText("von / bis");
				textTable.getCell(4,0).getTextService().getText().setText("mögliche Termine");

			}
			  catch (TextException exception) {
			  	exception.printStackTrace();
			}
			  String text = "";
			  String test = "";
			for(int i = 0; i < lang;i++){
				  try {
					  /*
					  String felder = "akutbeh," +
					  		"n_name,v_name," +
					  		"telefonp,telefong,telefonm,emaila," +
					  		"termine1,"termine2";
					  		,akutdat,akutbis
					  */
					  text = (String) ((Vector<?>)vec.get(i)).get(0);
					  textTable.getCell(0,i+1).getTextService().getText().setText(text);

					  text = (String) ((Vector<?>)vec.get(i)).get(1)+"\r"+ (String)((Vector<?>)vec.get(i)).get(2);
					  textTable.getCell(1,i+1).getTextService().getText().setText(text);

					  text = "";
					  test = (String) ((Vector<?>)vec.get(i)).get(3);
					  text = text + (test.trim().equals("") ? "" : "p:"+test);
					  test = (String) ((Vector<?>)vec.get(i)).get(4);
					  text = text + (test.trim().equals("") ? "" : "\r"+"g:"+test);
					  test = (String) ((Vector<?>)vec.get(i)).get(5);
					  text = text + (test.trim().equals("") ? "" : "\r"+"m:"+test);
					  test = (String) ((Vector<?>)vec.get(i)).get(6);
					  text = text + (test.trim().equals("") ? "" : "\r"+"e:"+test);
					  textTable.getCell(2,i+1).getTextService().getText().setText(text);

					  text = "";
					  test = (String) ((Vector<?>)vec.get(i)).get(9);
					  test = (test.trim().equals("") ? "ab: " : "ab:  "+DatFunk.sDatInDeutsch(test));
					  text = text + test;
					  test = (String) ((Vector<?>)vec.get(i)).get(10);
					  test = (test.trim().equals("") ? "\rbis: " : "\rbis: "+DatFunk.sDatInDeutsch(test));
					  text = text + test;
					  textTable.getCell(3,i+1).getTextService().getText().setText(text);

					  text = "";
					  test = (String) ((Vector<?>)vec.get(i)).get(7);
					  text = text + (test.trim().equals("") ? "" : test);
					  test = (String) ((Vector<?>)vec.get(i)).get(8);
					  text = text + (test.trim().equals("") ? "" : "\r"+test);
					  textTable.getCell(4,i+1).getTextService().getText().setText(text);

					}
				  catch (TextException exception) {
				  	exception.printStackTrace();
					}
			}
			TextTableColumn[] tbc = (TextTableColumn[]) textTable.getColumns();
			tbc[0].setWidth((short) 1500);
			tbc[1].setWidth((short) 1500);
			tbc[2].setWidth((short) 1700);
			tbc[3].setWidth((short) 1500);
			//tbc[4].setWidth((short) 7920);
			//System.out.println("Es gibt insgesamt "+tbc.length+" Column");

			//int cols = textTable.getColumnCount();
			int rows = textTable.getRowCount();
			int rot = Color.RED.getRGB();
			int blau = Color.BLUE.getRGB();
			int magenta = Color.MAGENTA.getRGB();
			for (int i = 0; i < rows; i++) {
				textTable.getCell(0, i).getCharacterProperties().setFontName("Courier New");
				textTable.getCell(0, i).getCharacterProperties().setFontSize(10.f);
				textTable.getCell(0, i).getCharacterProperties().setFontBold((i==0 ? true : false));
				textTable.getCell(0, i).getCharacterProperties().setFontColor(magenta);
				textTable.getCell(1, i).getCharacterProperties().setFontName("Courier New");
				textTable.getCell(1, i).getCharacterProperties().setFontSize(10.f);
				textTable.getCell(1, i).getCharacterProperties().setFontColor(blau);
				textTable.getCell(1, i).getCharacterProperties().setFontBold((i==0 ? true : false));
				textTable.getCell(2, i).getCharacterProperties().setFontName("Courier New");
				textTable.getCell(2, i).getCharacterProperties().setFontSize(10.f);
				textTable.getCell(2, i).getCharacterProperties().setFontBold((i==0 ? true : false));
				textTable.getCell(3, i).getCharacterProperties().setFontName("Courier New");
				textTable.getCell(3, i).getCharacterProperties().setFontSize(10.f);
				textTable.getCell(3, i).getCharacterProperties().setFontBold((i==0 ? true : false));
				textTable.getCell(4, i).getCharacterProperties().setFontName("Courier New");
				textTable.getCell(4, i).getCharacterProperties().setFontSize(10.f);
				textTable.getCell(4, i).getCharacterProperties().setFontBold((i==0 ? true : false));
				textTable.getCell(4, i).getCharacterProperties().setFontColor(rot);
			}
			final IDocument xdoc = document;
			new SwingWorker<Void,Void>(){
				@Override
				protected Void doInBackground() throws Exception {
					xdoc.getFrame().getXFrame().getContainerWindow().setVisible(true);
					return null;
				}

			}.execute();
			vec.clear();
			vec = null;

		}
	}
}
/*******************************************************************************************************/
class TelefonListe{
	Vector<String[]> dvec = new Vector<String[]>();
	Object tfobj = null;
	public TelefonListe(Object obj) throws TextException{
		////System.out.println("�bergebene Parameter = "+obj);
		////System.out.println("Einzelner Termin "+((String) ((Vector)((ArrayList) obj).get(0)).get(1)) );
		tfobj = obj;
		for(int i = 0; i <((Vector<?>)((ArrayList<?>) obj).get(0)).size(); i++){
			if(((String) ((Vector<?>)((ArrayList<?>) obj).get(1)).get(i)).equals("@FREI")){
				continue;
			}
			if(((String) ((Vector<?>)((ArrayList<?>) obj).get(0)).get(i)).equals("") &&
					((String) ((Vector<?>)((ArrayList<?>) obj).get(1)).get(i)).equals("")){
				continue;
			}

			if(((String) ((Vector<?>)((ArrayList<?>) obj).get(1)).get(i)).equals("")){
				String daten[] = {"","","","",""};
				daten[0] = ((String) ((Vector<?>)((ArrayList<?>) tfobj).get(2)).get(i)).substring(0,5)+ " Uhr";
				daten[1] = ((String) ((Vector<?>)((ArrayList<?>) tfobj).get(0)).get(i))+"\r\rKeine Zuordnung möglich!";
				daten[2] = "keine RezNr.";
				daten[4] = "???";
				dvec.add(daten);
				continue;
			}

			testeReznr(((String) ((Vector<?>)((ArrayList<?>) obj).get(1)).get(i)),i);
		}
		if(dvec.size()==0){
			JOptionPane.showMessageDialog(null, "Für die aktuelle Terminspalte kann kein Patient zugeordnet werden");
			return;
		}else{
			druckeTelefonListe();
		}
	}
	private void testeReznr(String xreznr,int zaehler){
		String[] daten = {"","","","",""};
		String reznr = xreznr;
		int ind = reznr.indexOf("\\");
		if(ind >= 0){
			reznr = reznr.substring(0,ind);
		}
		List<String> nichtlesen = Arrays.asList(new String[] {});
		Vector<String> vec1 = SqlInfo.holeSatz("verordn", "pat_intern", "rez_nr='"+reznr+"'",nichtlesen );
		if(vec1.size() == 0){
			daten[0] = ((String) ((Vector<?>)((ArrayList<?>) tfobj).get(2)).get(zaehler)).substring(0,5)+ " Uhr";
			daten[1] = ((String) ((Vector<?>)((ArrayList<?>) tfobj).get(0)).get(zaehler))+"\r\rKeine Zuordnung möglich, falsche Rezeptnummer???";
			daten[2] = xreznr;
			daten[4] = "???";
			dvec.add(daten);
			return;
		}
		String felder = "n_name,v_name,telefonp,telefong,telefonm,emaila,akutpat";
		//List<String> nichtlesen = Arrays.asList(new String[] {});
		Vector<String> vec2 = SqlInfo.holeSatz("pat5", felder, "pat_intern='"+vec1.get(0)+"'",nichtlesen );
		if(vec2.size() == 0){
			daten[0] = ((String) ((Vector<?>)((ArrayList<?>) tfobj).get(2)).get(zaehler)).substring(0,5)+ "Uhr";
			daten[1] = ((String) ((Vector<?>)((ArrayList<?>) tfobj).get(0)).get(zaehler))+"\r\rKeine Zuordnung möglich, Patient nicht gefunden";
			daten[2] = xreznr;
			daten[4] = "???";
			//dvec.add(daten.clone());
			dvec.add(daten);
			return;
		}
		daten[0] =  ((String) ((Vector<?>)((ArrayList<?>) tfobj).get(2)).get(zaehler)).substring(0,5)+ " Uhr";
		daten[1] = vec2.get(0)+", "+vec2.get(1);
		daten[2] = xreznr;
		String telefon = ( vec2.get(2).trim().length() > 0  ? "p:"+(vec2.get(2)) : "" );
		telefon = telefon + ( vec2.get(3).trim().length() > 0 ? "\r"+"g:"+(vec2.get(3)) : "" );
		telefon = telefon + ( vec2.get(4).trim().length() > 0 ? "\r"+"m:"+(vec2.get(4)) : "" );
		telefon = telefon + ( vec2.get(5).trim().length() > 0 ? "\r"+"e:"+(vec2.get(5)) : "" );
		daten[3] = telefon;
		daten[4] = ( vec2.get(6).equals("T") ? "JA!!!!" : "nein");
		dvec.add(daten);
	}
	private void druckeTelefonListe() throws TextException{
		int lang;
		if( (lang = dvec.size()) > 0){
			IDocumentService documentService = null;
			try {
				documentService = Reha.officeapplication.getDocumentService();
			} catch (OfficeApplicationException e) {
				e.printStackTrace();
			}
			IDocument document = null;
			try {
				DocumentDescriptor docdecript = new DocumentDescriptor();
				docdecript.setHidden(true);
				document = documentService.constructNewDocument(IDocument.WRITER, docdecript);

				//document = documentService.constructNewDocument(IDocument.WRITER, DocumentDescriptor.DEFAULT);
			} catch (NOAException e) {
				e.printStackTrace();
			}

			ITextDocument textDocument = (ITextDocument)document;
			/*
			IParagraph paragraph =
		  		textDocument.getTextService().getTextContentService().constructNewParagraph();
			*/
			try {
				OOTools.setzePapierFormat(textDocument, 21000, 29700);
			} catch (NoSuchElementException e1) {
				
				e1.printStackTrace();
			} catch (WrappedTargetException e1) {
				
				e1.printStackTrace();
			} catch (UnknownPropertyException e1) {
				
				e1.printStackTrace();
			} catch (PropertyVetoException e1) {
				
				e1.printStackTrace();
			} catch (IllegalArgumentException e1) {
				
				e1.printStackTrace();
			}
			/*
			 * Saichtext basteln und einsetzen
			 */
			ITextTable textTable = null;
			try {
				textTable = textDocument.getTextTableService().constructTextTable(lang+1, 6);

			} catch (TextException e) {
				e.printStackTrace();
			}
			try {

				textDocument.getTextService().getTextContentService().insertTextContent(textTable);
			} catch (TextException e) {
				e.printStackTrace();
			}
			try {
				textTable.getCell(0,0).getTextService().getText().setText("Beh.Beginn");
				textTable.getCell(1,0).getTextService().getText().setText("Patient");
				textTable.getCell(2,0).getTextService().getText().setText("Rezeptnr.");
				textTable.getCell(3,0).getTextService().getText().setText("Kontakt");
				textTable.getCell(4,0).getTextService().getText().setText("Akutpatient");
				textTable.getCell(5,0).getTextService().getText().setText("Bemerkung");
			}
			  catch (TextException exception) {
			  	exception.printStackTrace();
			}
			  //String text = "";
			  //String test = "";
			for(int i = 0; i < lang;i++){
				  try {
					  textTable.getCell(0,i+1).getTextService().getText().setText((((String[])((Vector<?>)dvec).get(i))[0]) );
					  textTable.getCell(1,i+1).getTextService().getText().setText((((String[])((Vector<?>)dvec).get(i))[1]) );
					  textTable.getCell(2,i+1).getTextService().getText().setText((((String[])((Vector<?>)dvec).get(i))[2]) );
					  textTable.getCell(3,i+1).getTextService().getText().setText((((String[])((Vector<?>)dvec).get(i))[3]) );
					  textTable.getCell(4,i+1).getTextService().getText().setText((((String[])((Vector<?>)dvec).get(i))[4]) );
					  textTable.getCell(5,i+1).getTextService().getText().setText("" );
					}
				  catch (TextException exception) {
				  	exception.printStackTrace();
					}
			}
			TextTableColumn[] tbc = (TextTableColumn[]) textTable.getColumns();
			tbc[0].setWidth((short) 1500);
			tbc[1].setWidth((short) 1500);
			tbc[2].setWidth((short) 1500);
			tbc[3].setWidth((short) 1500);
			tbc[4].setWidth((short) 1500);
			//tbc[4].setWidth((short) 7920);
			//System.out.println("Es gibt insgesamt "+tbc.length+" Column");

			//int cols = textTable.getColumnCount();
			int rows = textTable.getRowCount();
			int rot = Color.RED.getRGB();
			int blau = Color.BLUE.getRGB();
			int magenta = Color.MAGENTA.getRGB();
			textTable.getCell(5, 0).getCharacterProperties().setFontName("Courier New");
			textTable.getCell(5, 0).getCharacterProperties().setFontSize(10.f);
			textTable.getCell(5, 0).getCharacterProperties().setFontBold((true));
			textTable.getCell(5, 0).getCharacterProperties().setFontColor(rot);
			for (int i = 0; i < rows; i++) {
				textTable.getCell(0, i).getCharacterProperties().setFontName("Courier New");
				textTable.getCell(0, i).getCharacterProperties().setFontSize(10.f);
				textTable.getCell(0, i).getCharacterProperties().setFontBold((i==0 ? true : false));
				textTable.getCell(0, i).getCharacterProperties().setFontColor(magenta);
				textTable.getCell(1, i).getCharacterProperties().setFontName("Courier New");
				textTable.getCell(1, i).getCharacterProperties().setFontSize(10.f);
				textTable.getCell(1, i).getCharacterProperties().setFontColor(blau);
				textTable.getCell(1, i).getCharacterProperties().setFontBold((i==0 ? true : false));
				textTable.getCell(2, i).getCharacterProperties().setFontName("Courier New");
				textTable.getCell(2, i).getCharacterProperties().setFontSize(10.f);
				textTable.getCell(2, i).getCharacterProperties().setFontBold((i==0 ? true : false));
				textTable.getCell(3, i).getCharacterProperties().setFontName("Courier New");
				textTable.getCell(3, i).getCharacterProperties().setFontSize(10.f);
				textTable.getCell(3, i).getCharacterProperties().setFontBold((i==0 ? true : false));
				textTable.getCell(4, i).getCharacterProperties().setFontName("Courier New");
				textTable.getCell(4, i).getCharacterProperties().setFontSize(10.f);
				textTable.getCell(4, i).getCharacterProperties().setFontBold((i==0 ? true : false));
				textTable.getCell(4, i).getCharacterProperties().setFontColor(rot);

			}

			document.getFrame().getXFrame().getContainerWindow().setVisible(true);
			dvec.clear();
			dvec = null;
		}

	}
}

class F2RettungsAnker{
	public F2RettungsAnker() throws TextException{
		IDocumentService documentService = null;
		try {
			if(!Reha.officeapplication.isActive()){
				Reha.starteOfficeApplication();
			}
			documentService = Reha.officeapplication.getDocumentService();
		} catch (OfficeApplicationException e) {
			e.printStackTrace();
		}
		IDocument document = null;
		try {
			DocumentDescriptor docdecript = new DocumentDescriptor();
			docdecript.setHidden(true);
			document = documentService.constructNewDocument(IDocument.WRITER, docdecript);

			//document = documentService.constructNewDocument(IDocument.WRITER, DocumentDescriptor.DEFAULT);
		} catch (NOAException e) {
			e.printStackTrace();
		}

		ITextDocument textDocument = (ITextDocument)document;
		/*
		IParagraph paragraph =
	  		textDocument.getTextService().getTextContentService().constructNewParagraph();
		*/
		try {
			OOTools.setzePapierFormat(textDocument, 21000, 29700);
			OOTools.setzeRaender(textDocument, 500, 500, 500, 500);
		} catch (NoSuchElementException e1) {
			
			e1.printStackTrace();
		} catch (WrappedTargetException e1) {
			
			e1.printStackTrace();
		} catch (UnknownPropertyException e1) {
			
			e1.printStackTrace();
		} catch (PropertyVetoException e1) {
			
			e1.printStackTrace();
		} catch (IllegalArgumentException e1) {
			
			e1.printStackTrace();
		}
		/*
		 * Saichtext basteln und einsetzen
		 */
		ITextTable textTable = null;
		try {
			textTable = textDocument.getTextTableService().constructTextTable(Reha.terminLookup.size()+1, 5);


		} catch (TextException e) {
			e.printStackTrace();
		}

		try {

			textDocument.getTextService().getTextContentService().insertTextContent(textTable);

		} catch (TextException e) {
			e.printStackTrace();
		}
		try {
			textTable.getCell(0,0).getTextService().getText().setText("lfNr.");
			textTable.getCell(1,0).getTextService().getText().setText("durchgeführt von/am/um");
			textTable.getCell(2,0).getTextService().getText().setText("was/wo/wann");
			textTable.getCell(3,0).getTextService().getText().setText("Termindaten vor Aktion");
			textTable.getCell(4,0).getTextService().getText().setText("Termindaten nach Aktion");
			for(int i = 1; i < Reha.terminLookup.size()+1;i++){
				textTable.getCell(0,i).getTextService().getText().setText(Integer.toString(i));
				textTable.getCell(1,i).getTextService().getText().setText(
						Reha.terminLookup.get(i-1).get(0).get(4)+"\r"+
						Reha.terminLookup.get(i-1).get(0).get(3)+" - "+
						Reha.terminLookup.get(i-1).get(0).get(2)
						);
				textTable.getCell(2,i).getTextService().getText().setText(
						"Funktion = "+Reha.terminLookup.get(i-1).get(0).get(5)+"\r"+
						"Spalte von = "+Reha.terminLookup.get(i-1).get(0).get(0)+"\r"+
						"Kalendertag = "+Reha.terminLookup.get(i-1).get(0).get(1)
						);
				textTable.getCell(3,i).getTextService().getText().setText(
						" Name: "+Reha.terminLookup.get(i-1).get(1).get(0)+"\r"+
						"RezNr: "+Reha.terminLookup.get(i-1).get(1).get(1)+"\r"+
						"Start: "+Reha.terminLookup.get(i-1).get(1).get(2)+"\r"+
						" Ende: "+Reha.terminLookup.get(i-1).get(1).get(4)+"\r"+
						"Dauer: "+Reha.terminLookup.get(i-1).get(1).get(3)
						);
				textTable.getCell(4,i).getTextService().getText().setText(
						" Name: "+Reha.terminLookup.get(i-1).get(2).get(0)+"\r"+
						"RezNr: "+Reha.terminLookup.get(i-1).get(2).get(1)+"\r"+
						"Start: "+Reha.terminLookup.get(i-1).get(2).get(2)+"\r"+
						" Ende: "+Reha.terminLookup.get(i-1).get(2).get(4)+"\r"+
						"Dauer: "+Reha.terminLookup.get(i-1).get(2).get(3)
						);

			}
			//textTable.getCell(4,1).getTextService().getText().setText(Reha.terminLookup.toString());

		}
		  catch (TextException exception) {
		  	exception.printStackTrace();
		}

		TextTableColumn[] tbc = (TextTableColumn[]) textTable.getColumns();
		/*
		int width = 0;
		for(int i = 0; i < tbc.length;i++){
			width += (int)tbc[i].getWidth();
		}
		System.out.println(width);
		*/
		tbc[0].setWidth((short) 500);
		tbc[1].setWidth((short) 2375);
		tbc[2].setWidth((short) 2375);
		tbc[3].setWidth((short) 2375);
		tbc[4].setWidth((short) 2375);

		int rows = textTable.getRowCount();
		int rot = Color.RED.getRGB();
		int blau = Color.BLUE.getRGB();
		int magenta = Color.MAGENTA.getRGB();
		int irgendwas = new Color(231,120,23).getRGB();
		for (int i = 0; i < rows; i++) {
			textTable.getCell(0, i).getCharacterProperties().setFontName("Courier New");
			textTable.getCell(0, i).getCharacterProperties().setFontSize(9.f);
			textTable.getCell(0, i).getCharacterProperties().setFontBold((i==0 ? true : false));
			textTable.getCell(0, i).getCharacterProperties().setFontColor(magenta);
			textTable.getCell(1, i).getCharacterProperties().setFontName("Courier New");
			textTable.getCell(1, i).getCharacterProperties().setFontSize(9.f);
			textTable.getCell(1, i).getCharacterProperties().setFontColor(blau);
			textTable.getCell(1, i).getCharacterProperties().setFontBold((i==0 ? true : false));
			textTable.getCell(2, i).getCharacterProperties().setFontName("Courier New");
			textTable.getCell(2, i).getCharacterProperties().setFontSize(9.f);
			textTable.getCell(2, i).getCharacterProperties().setFontBold((i==0 ? true : false));
			textTable.getCell(3, i).getCharacterProperties().setFontName("Courier New");
			textTable.getCell(3, i).getCharacterProperties().setFontSize(9.f);
			textTable.getCell(3, i).getCharacterProperties().setFontBold((i==0 ? true : false));
			textTable.getCell(3, i).getCharacterProperties().setFontColor(irgendwas);
			textTable.getCell(4, i).getCharacterProperties().setFontName("Courier New");
			textTable.getCell(4, i).getCharacterProperties().setFontSize(9.f);
			textTable.getCell(4, i).getCharacterProperties().setFontBold((i==0 ? true : false));
			textTable.getCell(4, i).getCharacterProperties().setFontColor(rot);
		}

		final IDocument xdoc = document;
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				xdoc.getFrame().getXFrame().getContainerWindow().setVisible(true);
				return null;
			}

		}.execute();
	}
	public void propSet(ITextTableCellRange range){
		try {
			com.sun.star.beans.XPropertySet xPropSet = null;
			Object aColumnObj = null;
			aColumnObj = range.getCell(0,0);
			xPropSet = UnoRuntime.queryInterface(com.sun.star.beans.XPropertySet.class, aColumnObj);
			xPropSet.setPropertyValue("HoriJustify", 0);
		} catch (UnknownPropertyException e) {
			e.printStackTrace();
		} catch (PropertyVetoException e) {
			
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			
			e.printStackTrace();
		} catch (WrappedTargetException e) {
			
			e.printStackTrace();
		} catch (TextException e) {
			
			e.printStackTrace();
		}
	}
}

