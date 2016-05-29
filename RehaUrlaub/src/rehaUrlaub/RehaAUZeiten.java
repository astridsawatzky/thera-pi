package rehaUrlaub;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXPanel;






import ag.ion.bion.officelayer.application.OfficeApplicationException;
import ag.ion.bion.officelayer.document.DocumentDescriptor;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.document.IDocumentDescriptor;
import ag.ion.bion.officelayer.document.IDocumentService;
import ag.ion.bion.officelayer.spreadsheet.ISpreadsheetDocument;
import ag.ion.noa.NOAException;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.sun.star.beans.PropertyVetoException;
import com.sun.star.beans.UnknownPropertyException;
import com.sun.star.container.NoSuchElementException;
import com.sun.star.lang.IllegalArgumentException;
import com.sun.star.lang.IndexOutOfBoundsException;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.sheet.XSheetCellCursor;
import com.sun.star.sheet.XSpreadsheet;
import com.sun.star.sheet.XSpreadsheets;
import com.sun.star.uno.UnoRuntime;

import CommonTools.DatFunk;
import CommonTools.JRtaTextField;
import CommonTools.OOTools;
import CommonTools.SqlInfo;

public class RehaAUZeiten extends JXPanel{
	
	DecimalFormat dcf = new DecimalFormat("##########0.00");
	SimpleDateFormat datumsFormat = new SimpleDateFormat ("dd.MM.yyyy"); //Konv.
	int calcrow = 0;	
	ISpreadsheetDocument spreadsheetDocument = null;;
	IDocument document  = null;
	XSheetCellCursor cellCursor = null;
	String sheetName = null;
	
	
	RehaUrlaubTab eltern = null;
	Vector<Vector<String>> vecKalZeile = null;
	JRtaTextField[] tf = {null,null};
	JButton but = null;
	boolean mustbreak = false;
	JLabel infoLabel = new JLabel("");
	Vector<Object[]> auGesamt = new Vector<Object[]>();

	public RehaAUZeiten(RehaUrlaubTab eltern){
		super();
		this.eltern = eltern;
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				holeKalUser();
				return null;
			}
		}.execute();
		String x = "5dlu,50dlu,5dlu,100dlu,30dlu,200dlu";
		String y = "15dlu,p,5dlu,p,15dlu,p,15dlu";
		FormLayout lay = new FormLayout(x,y);
		CellConstraints cc = new CellConstraints();
		PanelBuilder pb = new PanelBuilder(lay);
		pb.addLabel("von Datum:",cc.xy(2,2));
		pb.add( (tf[0] = new JRtaTextField("DATUM",true)),cc.xy(4, 2));
		pb.addLabel("bis Datum:",cc.xy(2,4));
		pb.add( (tf[1] = new JRtaTextField("DATUM",true)),cc.xy(4, 4));
		
		pb.add( (but = new JButton("berechnen")),cc.xy(4, 6));
		but.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				mustbreak = false;
				new SwingWorker<Void,Void>(){
					@Override
					protected Void doInBackground() throws Exception {
						auGesamt.clear();
						startInvestigation();
						calcrow = 0;
						starteCalc();
						fuelleTabelle();
						return null;
					}
				}.execute();			
			}
		});
		
		pb.add(infoLabel,cc.xy(6,2,CellConstraints.FILL,CellConstraints.FILL));

		pb.getPanel().validate();
		setLayout(new BorderLayout());
		add(pb.getPanel(),BorderLayout.CENTER);
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run() {
				setzeFocus();
			}
		});
		
	}
	

	
	private void setzeFocus(){
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run() {
				tf[0].requestFocus();
			}
		});		
	}
	
	private void holeKalUser(){
		vecKalZeile = SqlInfo.holeFelder("select matchcode,kalzeile,astunden from kollegen2 order by matchcode");
		//System.out.println(vecKalZeile);
	}
	
	
	private void startInvestigation(){
		try{
			if(tf[0].getText().trim().length() != 10){
				JOptionPane.showMessageDialog(null,"Startdatum fehlt oder ist falsch");
				return;
			}
			if(tf[0].getText().trim().length() != 10){
				JOptionPane.showMessageDialog(null,"Enddatum fehlt oder ist falsch");
				return;
			}
			but.setEnabled(false);
			String kalzeile = "";
			int anzahlDates = 0;
			int x = 0;
			Vector<Vector<String>> kalDaten = null;
			boolean warkrank = false;
			boolean warda = false;
			double minanwesend = 0.00;
			double sollstunden = 0.00;
			
			
			
			for(int i = 0; i < vecKalZeile.size();i++){
				//Einzelner User
				Object[] auMonat = new Object[] {0.00,0.00,0.00,0.00,0.00,0.00,
						0.00,0.00,0.00,0.00,0.00,0.00,
						0.00,0.00,0.00,0.00,0.00,0.00,0.00};
				
				infoLabel.setText("hole Daten von Kalenderbenutzer "+vecKalZeile.get(i).get(0));
				kalzeile = nomalizeKalUser(vecKalZeile.get(i).get(1));
				kalDaten = SqlInfo.holeFelder("select * from flexkc where behandler = '"+kalzeile+"' and datum >='"+
				DatFunk.sDatInSQL(tf[0].getText())+"' and datum <= '"+DatFunk.sDatInSQL(tf[1].getText())+"' order by datum");
				sollstunden = Double.parseDouble(vecKalZeile.get(i).get(2))/5.00*60;
				String tag = "";
				
				for(int t = 0; t < kalDaten.size();t++){
					warkrank = false;
					warda = false;
					minanwesend = 0.00;	
					//Einzelner Tag
					infoLabel.setText("untersuche "+kalDaten.get(t).get(kalDaten.get(t).size()-2)+" von Kalenderbenutzer "+vecKalZeile.get(i).get(0));
					anzahlDates = Integer.parseInt(kalDaten.get(t).get(kalDaten.get(t).size()-6));
					tag = "";
					for(x = 0; x < (anzahlDates*5); x+=5){
						if(kalDaten.get(t).get(x).trim().equalsIgnoreCase("KRANK") && kalDaten.get(t).get(x+1).trim().equalsIgnoreCase("@FREI")){
							if(! tag.equals(kalDaten.get(t).get(kalDaten.get(t).size()-2).trim())){
								rechneKrank(auMonat,DatFunk.sDatInDeutsch(kalDaten.get(t).get(kalDaten.get(t).size()-2)),warda,minanwesend,sollstunden/2.0);
								tag = kalDaten.get(t).get(kalDaten.get(t).size()-2).trim();
							}
						}else if(!kalDaten.get(t).get(x+1).trim().equalsIgnoreCase("@FREI")){
							//Minuten addieren
							warda = true;
							minanwesend = minanwesend+Double.parseDouble(kalDaten.get(t).get(x+3).trim());
							
						}
					}
					//
				}
				auGesamt.add(auMonat.clone());
				if(mustbreak){
					break;
				}
			}
			but.setEnabled(true);
		}catch(Exception ex){
			ex.printStackTrace();
		}

		//System.out.println(kalDaten);
		
	}
	
	private void rechneKrank(Object[] au,String tag, boolean warda, Double minanwesend, Double halbertag ){
		String xmonat = tag.substring(3,5);
		int imonat = ( xmonat.startsWith("0") ? Integer.parseInt(xmonat.substring(1,2))-1 : Integer.parseInt(xmonat)-1);
		//Nur volle AU-Tage rechnen
		au[imonat] = ((Double)au[imonat])+1.00;
		// Wer halbe AU-Tage rechnen möchte muß die nächste Zeile verwenden
		//au[imonat] = ((Double)au[imonat])+(minanwesend >= halbertag ? 1.00 : 0.50);
		int wotag = DatFunk.TagDerWoche(tag);
		au[11+wotag] = ((Double)au[11+wotag])+1.00;
		
	}
	
	private String nomalizeKalUser(String string){
		String ret = null;
		try{
			if(Integer.parseInt(string) < 10){
				ret = "0"+string+"BEHANDLER";
			}else{
				ret = string+"BEHANDLER";
			}
		}catch(Exception ex){
			
		}
		return ret;
	}
	

	
	private void starteCalc() throws OfficeApplicationException, NOAException, NoSuchElementException, WrappedTargetException, UnknownPropertyException, PropertyVetoException, IllegalArgumentException{
		if(!RehaUrlaub.officeapplication.isActive()){
			RehaUrlaub.starteOfficeApplication();
		}
		IDocumentService documentService = RehaUrlaub.officeapplication.getDocumentService();
        IDocumentDescriptor docdescript = new DocumentDescriptor();
       	//docdescript.setHidden(true);
        docdescript.setAsTemplate(true);
		document = documentService.constructNewDocument(IDocument.CALC, docdescript);
		spreadsheetDocument = (ISpreadsheetDocument) document;
		OOTools.setzePapierFormatCalc((ISpreadsheetDocument) spreadsheetDocument, 21000, 29700);
		OOTools.setzeRaenderCalc((ISpreadsheetDocument) spreadsheetDocument, 1000,1000, 1000, 1000);
		sheetName= "Tabelle1";
		XSpreadsheets spreadsheets = spreadsheetDocument.getSpreadsheetDocument().getSheets();		
		XSpreadsheet spreadsheet1 = (XSpreadsheet)UnoRuntime.queryInterface(XSpreadsheet.class,spreadsheets.getByName(sheetName));
		cellCursor = spreadsheet1.createCursor();
	}
	private void fuelleTabelle() throws IndexOutOfBoundsException, UnknownPropertyException, PropertyVetoException, IllegalArgumentException, WrappedTargetException, NoSuchElementException{
		OOTools.doCellValue(cellCursor, 0, calcrow, "AU-Ermittlung vom "+tf[0].getText()+" bis "+tf[1].getText());
		calcrow++;
		calcrow++;
		String[] labs = {"Mitarbeiter","Jan.","Feb.","März","April","Mai","Juni","Juli","Aug.","Sept.","Okt.","Nov.","Dez.","Gesamt",
				"Mo.","Di.","Mi.","Do.","Fr.","Sa.","So."};
		OOTools.doColWidth(spreadsheetDocument, sheetName, 0, 0, 4550);
		OOTools.doColWidth(spreadsheetDocument, sheetName, 1, 12, 1500);
		OOTools.doColWidth(spreadsheetDocument, sheetName, 13, 13, 2270);
		OOTools.doColWidth(spreadsheetDocument, sheetName, 14, 21, 1500);
		OOTools.doColTextAlign(spreadsheetDocument, sheetName, 0, 0, 0);
		OOTools.doColTextAlign(spreadsheetDocument, sheetName, 1, 13, 2);
		OOTools.doColTextAlign(spreadsheetDocument, sheetName, 14, 21, 2);
		for(int i = 0;i < 14+7 ;i++){
			OOTools.doCellFontBold(cellCursor, i, calcrow);
			OOTools.doCellValue(cellCursor, i, calcrow, labs[i]);
			if(i> 0){
				
				OOTools.doColNumberFormat(spreadsheetDocument, sheetName, i, i, 2);
			}
			

		}
		boolean summe = false;
		for(int i = 0; i < auGesamt.size();i++){
			calcrow++;
			summe = false;
			OOTools.doCellValue(cellCursor, 0, calcrow, vecKalZeile.get(i).get(0));
			for(int x = 1; x < 13;x++){
				if(((Double)auGesamt.get(i)[x-1]) > 0.00){
					OOTools.doCellValue(cellCursor, x, calcrow, (Double) auGesamt.get(i)[x-1]);
					summe = true;
				}
				
			}
			if(summe){
				OOTools.doCellFormula(cellCursor, 13,calcrow, "=sum(B"+Integer.toString(calcrow+1)+":M"+Integer.toString(calcrow+1)+")");
				for(int x = 14; x < 21;x++){
					if(((Double)auGesamt.get(i)[x-2]) > 0.00){
						OOTools.doCellValue(cellCursor, x, calcrow, (Double) auGesamt.get(i)[x-2]);
					}	
				}				
			}
			
		}
	}

}
