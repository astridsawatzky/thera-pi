package terminKalender;


import hauptFenster.Reha;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXHeader;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTitledPanel;

import rehaContainer.RehaTP;
import systemEinstellungen.SystemConfig;
import CommonTools.StringTools;
import CommonTools.SqlInfo;
import CommonTools.ZeitFunk;
import ag.ion.bion.officelayer.application.OfficeApplicationException;
import ag.ion.bion.officelayer.document.DocumentDescriptor;
import ag.ion.bion.officelayer.document.DocumentException;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.document.IDocumentDescriptor;
import ag.ion.bion.officelayer.document.IDocumentService;
import ag.ion.bion.officelayer.filter.PDFFilter;
import ag.ion.bion.officelayer.text.ITextDocument;
import ag.ion.bion.officelayer.text.ITextField;
import ag.ion.bion.officelayer.text.ITextFieldService;
import ag.ion.bion.officelayer.text.ITextTable;
import ag.ion.bion.officelayer.text.TextException;
import ag.ion.noa.NOAException;
import ag.ion.noa.printing.IPrinter;
import dialoge.PinPanel;
import dialoge.RehaSmartDialog;
import emailHandling.EmailSendenExtern;
import events.RehaTPEvent;
import events.RehaTPEventClass;
import events.RehaTPEventListener;




public class DruckFenster extends RehaSmartDialog implements ActionListener, KeyListener, WindowListener,RehaTPEventListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3482074172384055074L;
	//private int setOben;

	private RehaTPEventClass rtp = null;
	//private JXPanel jp1 = null;
	private static ArrayList<String[]> termine = new ArrayList<String[]>();
	private static JXPanel jtp = null;
	private static String dieserName = "";
	private static JXTable pliste = null;
	private static JXTitledPanel  jp;
	public static int seiten = 1;
	public static int OOoFertig = -1;
	private static JButton jb1 = null;
	private static JButton jb2 = null;	
	private static JButton jb3 = null;
	private static JButton jb4 = null;
	public static DruckFenster thisClass;
	
	public Vector<Vector<String>> termineVec = new Vector<Vector<String>>(); 
	public DruckFenster(JXFrame owner,ArrayList<String[]> terminVergabe){
		//super(frame, titlePanel());
		super(owner,"DruckerListe");
		dieserName = "DruckerListe";
		setName(dieserName);
		getSmartTitledPanel().setName(dieserName);
		//this.termine = terminVergabe;

		macheTerminVec(terminVergabe);
		
		this.setModal(true);
		this.setUndecorated(true);
		this.setContentPanel(titlePanel() );
		DruckFenster.jtp.setLayout(new BorderLayout());
		DruckFenster.jtp.add(terminInfo(),BorderLayout.NORTH);
		DruckFenster.jtp.add(terminListe(),BorderLayout.CENTER);
		DruckFenster.jtp.add(buttonPanel(),BorderLayout.SOUTH);		
		PinPanel pinPanel = new PinPanel();
		pinPanel.getGruen().setVisible(false);
		pinPanel.setName(dieserName);
		pinPanel.setzeName(dieserName);
		setPinPanel(pinPanel);
		this.addKeyListener(this);
		rtp = new RehaTPEventClass();
		rtp.addRehaTPEventListener((RehaTPEventListener) this);
		SwingUtilities.invokeLater(new Runnable(){
	        public  void run()
           	   {
	        	if(pliste.getRowCount()>0){
	        		pliste.setEnabled(true);
	        		pliste.requestFocusInWindow();
	        		pliste.changeSelection(0,0,true,true);
	        		pliste.setRowSelectionInterval(0, 0);
	        		//pliste.getModel().setSelectedItem(0,0);

	        	}else{
	       			pliste.requestFocusInWindow();	        		
	        	}
	           	}
           	});
		thisClass = this;
	}		    
/*******************************************************/	
	@SuppressWarnings("unchecked")
	private void macheTerminVec(ArrayList<String[]>  termine){
		Vector<String> dummyTermin = new Vector<String>();
		for(int i = 0; i < termine.size();i++){
			dummyTermin.clear();
			for(int i2 = 0; i2 < termine.get(i).length;i2++){
				dummyTermin.add(termine.get(i)[i2]);
			}
			termineVec.add((Vector<String>)dummyTermin.clone());
		}
		if(dummyTermin.size() > 0){
			@SuppressWarnings("rawtypes")
			Comparator<Vector> comparator = new Comparator<Vector>() {
				@Override
				public int compare(Vector o1, Vector o2) {
					// TODO Auto-generated method stub
					String s1 = (String)o1.get(3);
					String s2 = (String)o2.get(3);
					return s1.compareTo(s2);
				}
			};
			Collections.sort(termineVec,comparator);
			
		}
		////System.out.println(termineVec);
	}
	
	public void cursorWait(boolean ein){
		if(!ein){
			this.setCursor(Reha.thisClass.normalCursor);
		}else{
			this.setCursor(Reha.thisClass.wartenCursor);
		}
	}
	public void setFocusTabelle(){
	SwingUtilities.invokeLater(new Runnable(){
        public  void run()
       	   {
        	if(pliste.getRowCount()>0){
        		pliste.setEnabled(true);
        		pliste.requestFocusInWindow();
        		pliste.changeSelection(0,0,true,true);
        		pliste.setRowSelectionInterval(0, 0);
        		//pliste.getModel().setSelectedItem(0,0);

        	}else{
       			pliste.requestFocusInWindow();	        		
        	}
           	}
       	});
	}

	/*********************************************************/
	
public void FensterSchliessen(String welches){
	if(rtp != null){
		rtp.removeRehaTPEventListener((RehaTPEventListener) this);		
	}
	this.dispose();
}

private JXPanel terminInfo(){
	JXPanel jpganz = new JXPanel(new BorderLayout());
	jpganz.setBorder(null);
	jpganz.setBackground(Color.WHITE);
    String ss = "icons/header-image.png";
    JXHeader header = new JXHeader("Die Druckerliste....",
            "....kann zwar 'Roogle' nicht ersetzen, für die schnelle Vergabe von Terminen und die Erstellung eines Terminplanes\n" +
            "für den Patienten ist diese Funktion jedoch bestens geeignet. Sie starten die Terminliste indem Sie auf dem gewünschten\n" +
            "Termin die Tastenkombination >>Strg+Einfg<< durchführen. Wenn Sie nun weitere Termine mit der Kombination >>Shift+Einfg<<\n"+
            "im Termikalender eintragen, wird jeder (so eingetragene) Termin der Liste hinzugefügt.\n\n" +
            "Sie schließen dieses Fenster über den roten Punkt rechts oben, oder mit der Taste >>ESC<<.",
            new ImageIcon(ss));
    jpganz.add(header,BorderLayout.NORTH);

	
	JXPanel jtinfo = new JXPanel();

	//jtinfo.setLayout(null);
	jtinfo.setBorder(null);
	if(DruckFenster.termine.size() > 0){
		jtinfo.setPreferredSize(new Dimension(600,40));
		String anzahlTermine = Integer.toString(DruckFenster.termine.size());
		String nameTermine = DruckFenster.termine.get(0)[8];		
		String nummerTermine = DruckFenster.termine.get(0)[9];
		getSmartTitledPanel().setTitle(anzahlTermine+ "  Termin(e) in der Druckerliste");
		JXLabel jl = new JXLabel("Termineintrag");
		jtinfo.add(jl);
		jl.setLocation(25,15);
		jl = new JXLabel(nameTermine);
		jl.setForeground(Color.BLUE);
		jtinfo.add(jl);
		jl.setLocation(75,15);
		jl = new JXLabel("Rez.Nummer");
		jtinfo.add(jl);
		jl.setLocation(25,15);
		jl = new JXLabel(nummerTermine);
		jl.setForeground(Color.BLUE);
		jtinfo.add(jl);
		jl.setLocation(75,15);
		jtinfo.setVisible(true);
		jtinfo.validate();
	}else{
		getSmartTitledPanel().setTitle("Keine(!!) Termine in der Druckerliste");
	}
	jpganz.add(jtinfo);
	return jpganz;
}

private JXPanel buttonPanel(){
	JXPanel bpanel = new JXPanel(new GridLayout(1,4));
	bpanel.setPreferredSize(new Dimension(0,25));
	jb1 = new JButton("Termine drucken");
	jb1.setMnemonic('T');
	jb1.setPreferredSize(new Dimension(30,15));
	jb1.addActionListener(this);
	jb1.addKeyListener(this);
	bpanel.add(jb1);
	jb2 = new JButton("Email senden");
	jb2.setMnemonic('E');
	jb2.setPreferredSize(new Dimension(30,15));
	jb2.addActionListener(this);
	jb2.addKeyListener(this);	
	bpanel.add(jb2);
	jb3 = new JButton("Termin löschen");
	jb3.setPreferredSize(new Dimension(30,15));
	jb3.addActionListener(this);
	jb3.addKeyListener(this);	
	bpanel.add(jb3);
	jb4 = new JButton("Liste leeren");
	jb4.setPreferredSize(new Dimension(30,15));
	jb4.addActionListener(this);
	jb4.addKeyListener(this);
	bpanel.add(jb4);

	return bpanel;
}
private DruckFenster getInstance(){
	return this;
}
private JXPanel titlePanel(){
	jp = new RehaTP(0);
	jp.setName(dieserName);
	jtp = (JXPanel) jp.getContentContainer();
	jtp.setSize(new Dimension(200,200));
	jtp.setVisible(true);
	jtp.addKeyListener(getInstance());
	return jtp;
}

private JXPanel terminListe(){
	JXPanel jpliste = new JXPanel(new BorderLayout());
	jpliste.setBorder(null);
	MyTerminTableModel myTable = new MyTerminTableModel();
	//TerminTableModel myTable = new TerminTableModel();
	String[] column = {"Tag","Datum","Uhrzeit","","Dauer","Therapeut","","","Termininhaber","Rez.Nr.",""};
	myTable.setColumnIdentifiers(column);
	//myTable.columnNames = column;
	for(int i = 0;i<termineVec.size();i++){
		myTable.addRow(termineVec.get(i));
	}
	
	
	//myTable.data = (ArrayList<String[]>) termine;
	////System.out.println("Klasse von Column 2 = "+myTable.getColumnClass(1));
	//jxTable.setModel(tblDataModel);
	pliste = new JXTable();
	pliste.addKeyListener(this);
	pliste.setModel(myTable);
	//pliste.getColumn(1).setMinWidth(0);	
	pliste.getColumn(0).setMaxWidth(80);
	pliste.getColumn(1).setMaxWidth(100);
	pliste.getColumn(2).setMaxWidth(80);	
	
	pliste.getColumn(3).setMinWidth(0);
	pliste.getColumn(3).setMaxWidth(0); //SQL-Datum
	pliste.getColumn(4).setMinWidth(40);
	pliste.getColumn(4).setMaxWidth(40); //Dauer
	pliste.getColumn(6).setMinWidth(0);
	pliste.getColumn(6).setMaxWidth(0); //Behandler
	pliste.getColumn(7).setMinWidth(0);
	pliste.getColumn(7).setMaxWidth(0); //Block
	pliste.getColumn(8).setMinWidth(180);
	pliste.getColumn(8).setMaxWidth(200); //Name
	pliste.getColumn(9).setMinWidth(80);
	pliste.getColumn(9).setMaxWidth(80); //Rez.Nr.	
	pliste.getColumn(10).setMinWidth(0);
	pliste.getColumn(10).setMaxWidth(0); //Datenvector				
	pliste.setEditable(false);
	//pliste.setSortable(true);
	//SortOrder setSort = SortOrder.ASCENDING;
	//pliste.setSortOrder(3,(SortOrder) setSort);
	pliste.setSelectionMode(0);	
	
	pliste.validate();
	pliste.setVisible(true);
	JScrollPane jscr = new JScrollPane();
	jscr.setViewportView(pliste);
	jpliste.add(jscr,BorderLayout.CENTER);
	jpliste.setVisible(true);


	return jpliste;
}

public String dieserName(){
	return this.getName();
}

public void rehaTPEventOccurred(RehaTPEvent evt) {
	// TODO Auto-generated method stub
	////System.out.println("****************das darf doch nicht wahr sein in DruckFenster**************");
	//String ss =  this.getName();
	////System.out.println("Durckerlistenfenster "+this.getName()+" Eltern "+ss);
	try{
		//if (evt.getDetails()[0].equals(ss) && evt.getDetails()[1]=="ROT"){
			FensterSchliessen(evt.getDetails()[0]);
			rtp.removeRehaTPEventListener((RehaTPEventListener) this);
		//}	
	}catch(NullPointerException ne){
		////System.out.println("In DruckFenster" +evt);
	}


}

@Override
public void actionPerformed(ActionEvent arg0) {
	String cmd = arg0.getActionCommand();
	for(int i = 0; i< 1;i++){
		if(cmd.equals("Termine drucken")){
			if(pliste.getRowCount()> 0){
				//PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
				//DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PRINTABLE;
				//PrintService   prservDflt = PrintServiceLookup.lookupDefaultPrintService();
				////System.out.println("Default Printer = "+prservDflt);
				//PrintService[] prservices = PrintServiceLookup.lookupPrintServices( flavor, aset );
				////System.out.println("Printer prservices = "+Arrays.asList(prservices));
				////System.out.println("Printer aset = "+Arrays.asList(aset));
				jb1.setEnabled(false);
				jb2.setEnabled(false);
				jb3.setEnabled(false);
				jb4.setEnabled(false);	
				cursorWait(true);
				bestueckeOOo xbestueckeOOo = new bestueckeOOo();
				xbestueckeOOo.DruckenOderEmail("Drucken");
			}
			break;
		}
		if(cmd.equals("Email senden")){
			if(pliste.getRowCount()> 0){
				try{
					jb1.setEnabled(false);
					jb2.setEnabled(false);
					jb3.setEnabled(false);
					jb4.setEnabled(false);			
					cursorWait(true);
					new Thread(new sendeTermine()).start();
				}catch(Exception ex){
					JOptionPane.showMessageDialog(null,"Fehler in der Funktion Terminplan per Email senden.\nFehlertext:"+ex.getLocalizedMessage());
				}
			}
			break;
		}
		if(cmd.equals("Termin löschen")){
			int mc = JOptionPane.QUESTION_MESSAGE;
			int bc = JOptionPane.YES_NO_CANCEL_OPTION;
			if(pliste.getRowCount()<=0){
				return;
			}
			String anfrage = "Wollen Sie den Termin nur in dieser Druckerliste löschen?\n\n"+
				"Ja  =   nur in der Druckerliste löschen\n\n"+
				"Nein  =   in Druckerliste und(!) im Terminkalender\n\n"+
				"Abbrechen  =   weder noch und Tschüß...\n\n\n";
			int ch = JOptionPane.showConfirmDialog (null,anfrage , "Termin löschen aber wie?", bc, mc);
			if(ch == JOptionPane.CANCEL_OPTION){
				return;
			}
			
			////System.out.println("Selektierte Reihe="+pliste.getSelectedRow());
			int reihen = pliste.getRowCount();
			int selected = pliste.getSelectedRow();
			if(selected >=0){
				String tagundstart = DatFunk.sDatInSQL(pliste.getValueAt(selected, 1).toString())+
				pliste.getValueAt(selected, 2).toString();
				String altdauer = pliste.getValueAt(selected, 4).toString();
				String altbehandler = pliste.getValueAt(selected, 5).toString();
				String altname = pliste.getValueAt(selected, 8).toString();
				String altrezept = pliste.getValueAt(selected, 9).toString();
				////System.out.println("TerminAusmustern = "+tagundstart+"/"+altdauer+"/"+altbehandler+"/"+altname+"/"+altrezept);
				Reha.thisClass.terminpanel.terminAusmustern(tagundstart,altdauer,altbehandler,altname,altrezept);				
			}
			int reihenselekt = pliste.getSelectedRow();
			int realindex = pliste.convertRowIndexToModel(reihenselekt);
			////System.out.println("******Tats�chlicher Index = "+realindex);

			if(ch == JOptionPane.NO_OPTION){
				boolean geklappt;
				geklappt = satzSperrenUndLoeschen(realindex);
				if(geklappt){
					termine.remove(realindex);
					
					//terminAusmustern(tagundstart,altdauer,altbehandler,altname,altrezept);					
				}else{
					JOptionPane.showMessageDialog(null,"Die Kalenderspalte ist momentan gesperrt und kann deshalb nicht gelöscht werden!");
					return;
				}
			}else{
				JOptionPane.showMessageDialog(null,"Bitte denken Sie daran:\n"+
						"Sie drucken einen Terminplan - haben zuvor einen Termin aus dem Terminplan gelöscht\n"+
						"aber der Termin steht immer noch im Terminkalender - bitte keinen Ausfall produzieren");
			}
			//System.out.println("Realindex = "+realindex);			
			if(reihen > 0){
				getSmartTitledPanel().setTitle(Integer.toString(reihen-1)+ "  Termin(e) in der Druckerliste");
				//xxx
				pliste.setEditable(true);
				int select = pliste.getSelectedRow();
				if(select >= 0){
					((MyTerminTableModel)pliste.getModel()).removeRow(select);
					pliste.validate();
					pliste.repaint();
				}
				
			}
			break;
		}
		if(cmd.equals("Liste leeren")){
			((MyTerminTableModel)pliste.getModel()).setRowCount(0);
			pliste.validate();
			pliste.repaint();
			break;
		}

	}
	
}
@Override
public void keyPressed(KeyEvent arg0) {
	if(arg0.getKeyCode() == 27){
		rtp.removeRehaTPEventListener((RehaTPEventListener) this);
		rtp = null;
		FensterSchliessen(null);
	}
	// TODO Auto-generated method stub
	
}
@Override
public void keyReleased(KeyEvent arg0) {
	// TODO Auto-generated method stub
	
}
@Override
public void keyTyped(KeyEvent arg0) {
	// TODO Auto-generated method stub
	if(arg0.getKeyCode() == 27){
		rtp.removeRehaTPEventListener((RehaTPEventListener) this);
		rtp = null;
		FensterSchliessen(null);
	}	
}

public static JXTable getTable(){
	return pliste;
}
public Vector<Vector<String>> getTermine(){
	return termineVec;
}
public static void buttonsEinschalten(){
	jb1.setEnabled(true);
	jb2.setEnabled(true);
	jb3.setEnabled(true);
	jb4.setEnabled(true);			
}

private boolean satzSperrenUndLoeschen(int realindex){
	boolean ret=false;
	Reha.thisClass.terminpanel.setUpdateVerbot(true);
	String behandlernum = (termine.get(realindex)[6].length()==1 ? "0"+termine.get(realindex)[6]+"BEHANDLER" : termine.get(realindex)[6]+"BEHANDLER");
	String sdatum = termine.get(realindex)[1];
	String sstmt = "select * from flexlock where sperre = '"+behandlernum+sdatum+"'";
	String isstmt = "insert into flexlock set sperre = '"+behandlernum+sdatum+"'";
	int iblock = Integer.valueOf(termine.get(realindex)[7]);
	String neustmt = "update flexkc set T"+(iblock+1)+
			"='', N"+(iblock+1)+
			" = '' where DATUM = '"+termine.get(realindex)[3].substring(0,10)+"' AND BEHANDLER = '"+
			behandlernum+"'";
	String sentsprerr = "delete from flexlock where sperre = '"+behandlernum+sdatum+"'";
	String[] befehle = {null,null,null,null};
	befehle[0] = sstmt;
	befehle[1] = isstmt;
	befehle[2] = neustmt;	
	befehle[3] = sentsprerr;	
	////System.out.println("Behfehl 1 = "+befehle[0]);
	////System.out.println("Behfehl 2 = "+befehle[1]);
	////System.out.println("Behfehl 3 = "+befehle[2]);
	////System.out.println("Behfehl 4 = "+befehle[3]);	
	ret = new druckListeSperren().schongesperrt(befehle);
	if (ret){
		SwingUtilities.invokeLater(new Runnable(){
	        public  void run()
           	   {
	        	Reha.thisClass.terminpanel.aktualisieren();
           	   }
		}); 
	}
	//mm
	Reha.thisClass.terminpanel.setUpdateVerbot(false);
	return ret;
}

final class bestueckeOOo extends Thread implements Runnable{
JXTable jtable = null; 
Vector<Vector<String>> oOTermine = null;
String aktion = "";
String exporturl = "";
public void DruckenOderEmail(String aktion){
	this.aktion = aktion;
	start();
}
public void run(){
	String[] tabName = null; 
	jtable = DruckFenster.getTable();
	
	oOTermine = getTermine();
	
	if(oOTermine.size()==0){
		JOptionPane.showMessageDialog (null, "In der Terminliste sind keine Termine vorhanden.\n"+
				"Nicht vorhandene Termine könne nur sehr schwer (in diesem Fall gar nicht) ausgedrucket werden...\n\n"+
				"Oh Herr schmeiß Hirn ra.....");
				DruckFenster.OOoFertig = 0;
				DruckFenster.buttonsEinschalten();
				DruckFenster.thisClass.cursorWait(false);
				return;
	}

	try {
		String url = Reha.proghome+"vorlagen/"+Reha.aktIK+"/"+SystemConfig.oTerminListe.NameTemplate;
		//String url = Reha.proghome+"vorlagen/"+SystemConfig.oTerminListe.NameTemplate; 
		////System.out.println("***************URL = "+url+"****************");
		String terminDrucker = SystemConfig.oTerminListe.NameTerminDrucker;
		//String terminDrucker = SystemConfig.oTerminListe.NameTerminDrucker;
		int anzahl = oOTermine.size();
		int AnzahlTabellen = SystemConfig.oTerminListe.AnzahlTerminTabellen;
		int maxTermineProTabelle = SystemConfig.oTerminListe.AnzahlTermineProTabelle;
		int maxTermineProSeite = AnzahlTabellen * maxTermineProTabelle;
		//int spaltenProtabelle = SystemConfig.oTerminListe.AnzahlSpaltenProTabellen;
		Vector<String> spaltenNamen = SystemConfig.oTerminListe.NamenSpalten;
		int ipatdrucken = SystemConfig.oTerminListe.PatNameDrucken;
		int iheader = SystemConfig.oTerminListe.MitUeberschrift;
		boolean lendlos = SystemConfig.oTerminListe.EndlosDruck;
		//String patplatzhalter = SystemConfig.oTerminListe.PatNamenPlatzhalter;
		////System.out.println("Platzhalter = "+patplatzhalter);
		//
		
/*******************************/
		String patname = (oOTermine.get(0).get(8).indexOf("?")>=0 ? oOTermine.get(0).get(8).substring(1).trim() : oOTermine.get(0).get(8).trim());
		String rez = (oOTermine.get(0).get(9).trim().equals("") ? "" : " - "+oOTermine.get(0).get(9).trim());
        patname = patname+rez;
        
		IDocumentService documentService = Reha.officeapplication.getDocumentService();
        IDocumentDescriptor docdescript = new DocumentDescriptor();
        docdescript.setHidden(true);
        docdescript.setAsTemplate(true);
		
		ITextTable[] tbl = null;        

		IDocument document = documentService.loadDocument(url,docdescript);
		ITextDocument textDocument = (ITextDocument)document;
		
		/**********************/
		tbl = textDocument.getTextTableService().getTextTables();

		if(tbl.length != AnzahlTabellen){
			JOptionPane.showMessageDialog (null, "Anzahl Tabellen stimmt nicht mit der Vorlagen.ini überein.\nDruck nicht möglich");
			textDocument.close();
			DruckFenster.thisClass.cursorWait(false);
			return;
		}
		tabName = new String[AnzahlTabellen];
		int x = 0;
		for(int i=AnzahlTabellen;i>0;i--){
			tabName[x] = tbl[(tbl.length-1)-x].getName(); 
			x++;
		}
		/*********************/
		

		//Aktuellen Drucker ermitteln
		String druckerName = textDocument.getPrintService().getActivePrinter().getName();
		//Wenn nicht gleich wie in der INI angegeben -> Drucker wechseln
		IPrinter iprint = null;
		if(! druckerName.equals(terminDrucker)){
			iprint = (IPrinter) textDocument.getPrintService().createPrinter(terminDrucker);
			textDocument.getPrintService().setActivePrinter(iprint);
		}
		//Jetzt den Platzhalter ^Name^ suchen
//		SearchDescriptor searchDescriptor = null;
//		ISearchResult searchResult = null;
		if(ipatdrucken  > 0){
		      ITextFieldService textFieldService = textDocument.getTextFieldService();
		      ITextField[] placeholders = null;
				try {
					placeholders = textFieldService.getPlaceholderFields();
				} catch (TextException e) {
					JOptionPane.showMessageDialog(null,"Fehler bei der Erstellung des Terminplanes\nFehlermeldung: "+e.getMessage());
					e.printStackTrace();
				}
				for (int i = 0; i < placeholders.length; i++) {
					String placeholderDisplayText = placeholders[i].getDisplayText();
					////System.out.println("Platzhalter-Name = "+placeholderDisplayText);
					if(placeholderDisplayText.equals("<^Name^>")){
						placeholders[i].getTextRange().setText(patname);
					}	
				}
		      
		}
		// Ab hier die Tabelle best�cken
		//..........


	
		//int anzahl = (oOTermine.size() > 17 ? 17 : oOTermine.size()) ;
		//int zeile = 0;
		//int startTabelle = 0;
		int aktTabelle = 0;
		int aktTermin = -1;
		int aktTerminInTabelle = -1;
		String druckDatum = "";
		ITextTable textTable = textDocument.getTextTableService().getTextTable(tabName[aktTabelle]);
		//int aktseiten = 0;
		while(true){
			aktTerminInTabelle = aktTerminInTabelle+1;
			aktTermin = aktTermin+1;
			
			if(aktTermin >= anzahl){
				break;
			}
			
			/***********Wenn die Spalte voll ist und die aktuelle Tabelle nicht die letzte ist*/
			if(!lendlos){
				if(aktTerminInTabelle >= maxTermineProTabelle && aktTabelle < AnzahlTabellen-1  ){
					aktTabelle = aktTabelle+1;
					textTable = textDocument.getTextTableService().getTextTable(tabName[aktTabelle]);
					aktTerminInTabelle = 0;
					////System.out.println("Spaltenwechsel nach Spalte"+aktTabelle);
				}
			
				/************Wenn die aktuelle Seite voll ist******************/
				if(aktTermin >= maxTermineProSeite && aktTerminInTabelle==maxTermineProTabelle){

					textDocument.getViewCursorService().getViewCursor().getPageCursor().jumpToEndOfPage();
					try {
						textDocument.getViewCursorService().getViewCursor().getTextCursorFromEnd().insertPageBreak();
						textDocument.getViewCursorService().getViewCursor().getTextCursorFromEnd().insertDocument(url) ;
					} catch (NOAException e) {
						// TODO Auto-generated catch block
						JOptionPane.showMessageDialog(null,"Fehler bei der Erstellung des Terminplanes\nFehlermeldung: "+e.getMessage());
						e.printStackTrace();
					}
					tbl = textDocument.getTextTableService().getTextTables();
					x = 0;
					for(int i=AnzahlTabellen;i>0;i--){
						tabName[x] = tbl[(tbl.length-1)-x].getName(); 
						////System.out.println(tabName[x]);
						x++;
					}
					
					if(ipatdrucken  > 0){
					      ITextFieldService textFieldService = textDocument.getTextFieldService();
					      ITextField[] placeholders = null;
							try {
								placeholders = textFieldService.getPlaceholderFields();
							} catch (TextException e) {
								// TODO Auto-generated catch block
								JOptionPane.showMessageDialog(null,"Fehler bei der Erstellung des Terminplanes\nFehlermeldung: "+e.getMessage());
								e.printStackTrace();
							}
							for (int i = 0; i < placeholders.length; i++) {
								String placeholderDisplayText = placeholders[i].getDisplayText();
								////System.out.println("Platzhalter-Name = "+placeholderDisplayText);
								if(placeholderDisplayText.equals("<^Name^>")){
									placeholders[i].getTextRange().setText(patname);
								}	
							}

					}
					aktTabelle = 0;
					aktTerminInTabelle = 0;

					try {
						textTable = textDocument.getTextTableService().getTextTable(tabName[aktTabelle]);
					} catch (TextException e) {
						// TODO Auto-generated catch block
						JOptionPane.showMessageDialog(null,"Fehler bei der Erstellung des Terminplanes\nFehlermeldung: "+e.getMessage());
						e.printStackTrace();
					}
					////System.out.println("textTable gesetzt*************");
					////System.out.println("Druck wird fortgesetzt bei Termin Nr.:"+aktTermin);
				}
			}
			/********************/
			if(!lendlos){
				if(spaltenNamen.contains("Wochentag")){
					int zelle = spaltenNamen.indexOf("Wochentag");
					druckDatum = jtable.getStringAt(aktTermin,0);
					if(aktTerminInTabelle > 0){
						if(! druckDatum.equals(jtable.getStringAt(aktTermin-1,0))){
							textTable.getCell(zelle,aktTerminInTabelle+iheader).getTextService().getText().setText(druckDatum.substring(0,2) );					
						}
					}else{
						textTable.getCell(zelle,aktTerminInTabelle+iheader).getTextService().getText().setText(druckDatum.substring(0,2) );
					}
				}
				if(spaltenNamen.indexOf("Datum") > 0){
					int zelle = spaltenNamen.indexOf("Datum");
					textTable.getCell(zelle,aktTerminInTabelle+iheader).getTextService().getText().setText(jtable.getStringAt(aktTermin,1) );
				}
				if(spaltenNamen.indexOf("Uhrzeit") > 0){
					int zelle = spaltenNamen.indexOf("Uhrzeit");
					textTable.getCell(zelle,aktTerminInTabelle+iheader).getTextService().getText().setText(jtable.getStringAt(aktTermin,2).substring(0,5) );
				}
				if(spaltenNamen.indexOf("Behandler") > 0){
					int zelle = spaltenNamen.indexOf("Behandler");						
					textTable.getCell(zelle,aktTerminInTabelle+iheader).getTextService().getText().setText(jtable.getStringAt(aktTermin,5) );
				}
				
			}else{
				/**** hier die Endlosdruckfunktion ****/
				if(aktTermin > 0){
					textTable.addRow(1);
				}
				if(spaltenNamen.contains("Wochentag")){
					int zelle = spaltenNamen.indexOf("Wochentag");
					druckDatum = jtable.getStringAt(aktTermin,0);
					if(aktTermin > 0){
						if(! druckDatum.equals(jtable.getStringAt(aktTermin-1,0))){
							//textTable.getCell(zelle,aktTermin+iheader).getCharacterProperties().setFontSize(9.f);
							textTable.getCell(zelle,aktTermin+iheader).getTextService().getText().setText(druckDatum.substring(0,2) );					
						}
					}else{
						//textTable.getCell(zelle,aktTermin+iheader).getCharacterProperties().setFontSize(9.f);
						textTable.getCell(zelle,aktTermin+iheader).getTextService().getText().setText(druckDatum.substring(0,2) );
					}
				}
				if(spaltenNamen.indexOf("Datum") > 0){
					int zelle = spaltenNamen.indexOf("Datum");
					//textTable.getCell(zelle,aktTermin+iheader).getCharacterProperties().setFontSize(9.f);
					textTable.getCell(zelle,aktTermin+iheader).getTextService().getText().setText(jtable.getStringAt(aktTermin,1) );
				}
				if(spaltenNamen.indexOf("Uhrzeit") > 0){
					int zelle = spaltenNamen.indexOf("Uhrzeit");
					//textTable.getCell(zelle,aktTermin+iheader).getCharacterProperties().setFontSize(9.f);
					textTable.getCell(zelle,aktTermin+iheader).getTextService().getText().setText(jtable.getStringAt(aktTermin,2).substring(0,5) );
				}
				if(spaltenNamen.indexOf("Behandler") > 0){
					int zelle = spaltenNamen.indexOf("Behandler");						
					//textTable.getCell(zelle,aktTermin+iheader).getCharacterProperties().setFontSize(9.f);
					textTable.getCell(zelle,aktTermin+iheader).getTextService().getText().setText(jtable.getStringAt(aktTermin,5) );
				}
				
			}

			/********************/
		}
		// Jetzt das fertige Dokument drucken, bzw. als PDF aufbereiten;
		if (this.aktion == "Drucken"){
			if(SystemConfig.oTerminListe.DirektDruck){
				textDocument.print();
				textDocument.close();
				DruckFenster.thisClass.cursorWait(false);
				JOptionPane.showMessageDialog (null, "Die Terminliste wurde aufbereitet und ausgedruckt\n");
			}else{
				DruckFenster.thisClass.cursorWait(false);
				document.getFrame().getXFrame().getContainerWindow().setVisible(true);	
			}

		}else{
			try{
				exporturl = Reha.proghome+"temp/"+Reha.aktIK+"/Terminplan.pdf";
				File f = new File(exporturl);
				if(f.exists()){
					f.delete();
				}
				textDocument.getPersistenceService().export(exporturl, new PDFFilter());
				//Thread.sleep(50);
			}catch(Exception ex){
				JOptionPane.showMessageDialog(null,"Fehler bei der Erstellung der PDF-Datei (Termine)");
			}
			textDocument.close();
		}		
		// Anschlie�end die Vorlagendatei schlie�en

		/*
		try{
			while (Reha.officeapplication.getDocumentService().getCurrentDocuments()[0] != null){
				Reha.officeapplication.getDocumentService().getCurrentDocuments()[0].close();
				//System.out.println("Fenster geschlossen");
			}
		}catch(java.lang.ArrayIndexOutOfBoundsException ex){}	
		documentService.dispose();
		*/
		//Reha.officeapplication.getDesktopService().dispose();
	} catch (OfficeApplicationException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		DruckFenster.OOoFertig = 0;
		DruckFenster.buttonsEinschalten();
		JOptionPane.showMessageDialog(null,"Fehler bei der Erstellung des Terminplanes\nFehlermeldung: "+e.getMessage());
		return;
	} catch (DocumentException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		DruckFenster.OOoFertig = 0;
		DruckFenster.buttonsEinschalten();
		JOptionPane.showMessageDialog(null,"Fehler bei der Erstellung des Terminplanes\nFehlermeldung: "+e.getMessage());
		return;
	} catch (NOAException e) {
		// TODO Auto-generated catch block
		DruckFenster.OOoFertig = 0;
		DruckFenster.buttonsEinschalten();
		e.printStackTrace();
		JOptionPane.showMessageDialog(null,"Fehler bei der Erstellung des Terminplanes\nFehlermeldung: "+e.getMessage());
		return;
	} catch (TextException e) {
		// TODO Auto-generated catch block
		DruckFenster.OOoFertig = 0;
		e.printStackTrace();
		DruckFenster.buttonsEinschalten();
		JOptionPane.showMessageDialog(null,"Fehler bei der Erstellung des Terminplanes\nFehlermeldung: "+e.getMessage());
		return;
	}
	DruckFenster.OOoFertig = 1;
	if (this.aktion == "Drucken"){
		DruckFenster.buttonsEinschalten();
	}	
	return;

	}
}
final class sendeTermine extends Thread implements Runnable{
	Vector<Vector<String>> oOTermine = null;
	String str = "";
	String pat_intern = "";
	String emailaddy = "";

	public void run(){
		oOTermine = getTermine();
		if(oOTermine.size()==0){
			JOptionPane.showMessageDialog (null, "In der Terminliste sind keine Termine vorhanden.\n"+
					"Nicht vorhandene Termine können nur sehr schwer (in diesem Fall gar nicht) per Email versandt werden...\n\n"+
					"Oh Herr schmeiß Hirn ra.....");
			DruckFenster.buttonsEinschalten();
			cursorWait(false);
			return;
		}
		if(oOTermine.get(0).get(9).equals("")){
			emailaddy = JOptionPane.showInputDialog (null, "Bitte geben Sie eine gültige Email-Adresse ein");
			try{
				if(emailaddy.equals("")){
					DruckFenster.buttonsEinschalten();
					JOptionPane.showMessageDialog (null, "Emailadresse ungültig - Abbruchposition 1");
					cursorWait(false);
					return;
				}
			}catch(java.lang.NullPointerException ex){
				JOptionPane.showMessageDialog (null, "Emailadresse ungültig - Abbruchposition 2");
				cursorWait(false);
				DruckFenster.buttonsEinschalten();
				return;
			}
		}else{
			pat_intern = holeAusDB("select PAT_INTERN from verordn where REZ_NR ='"+StringTools.richteNummer(oOTermine.get(0).get(9))+"'");
			if(pat_intern.equals("")){
				emailaddy = JOptionPane.showInputDialog (null, "Bitte geben Sie eine gültige Email-Adresse ein");
				try{
					if(emailaddy.equals("")){
						DruckFenster.buttonsEinschalten();
						JOptionPane.showMessageDialog (null, "Emailadresse ungültig - Abbruchposition 3");
						cursorWait(false);
						return;
					}
				}catch(java.lang.NullPointerException ex){
					DruckFenster.buttonsEinschalten();
					JOptionPane.showMessageDialog (null, "Emailadresse ungültig - Abbruchposition 4");
					cursorWait(false);
					return;
				}
			}else{
				emailaddy = holeAusDB("select EMAILA from pat5 where PAT_INTERN ='"+pat_intern+"'");
				if(emailaddy.equals("")){
					emailaddy = JOptionPane.showInputDialog (null, "Bitte geben Sie eine gültige Email-Adresse ein");
					try{
						if(emailaddy.equals("")){
						DruckFenster.buttonsEinschalten();
						JOptionPane.showMessageDialog (null, "Emailadresse ungültig - Abbruchposition 5");
						cursorWait(false);
						return;
						}
					}catch(java.lang.NullPointerException ex){
						DruckFenster.buttonsEinschalten();
						JOptionPane.showMessageDialog (null, "Emailadresse ungültig - Abbruchposition 6");
						cursorWait(false);
						return;
					}
				}
			}
		}
		try{
			File f = new File(Reha.proghome+"temp/"+Reha.aktIK+"/Terminplan.pdf"); 
			if(f.exists()){
				f.delete();
			}

			int frage = JOptionPane.showConfirmDialog(null,"Terminplan versenden an:\n\nEmailadresse = "+emailaddy+"\nPatient = "+
					StringTools.EGross(SqlInfo.holeEinzelFeld("select n_name from pat5 where pat_intern='"+pat_intern+"' LIMIT 1"))+", "+
					StringTools.EGross(SqlInfo.holeEinzelFeld("select v_name from pat5 where pat_intern='"+pat_intern+"' LIMIT 1"))+"\n"+
					"Rezept = "+StringTools.richteNummer(oOTermine.get(0).get(9))+"\n",
					"Benutzeranfrage",
					JOptionPane.YES_NO_OPTION);
					if(frage != JOptionPane.YES_OPTION){
						DruckFenster.buttonsEinschalten();
						cursorWait(false);
						return;
					}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	
		bestueckeOOo xbestueckeOOo = new bestueckeOOo();
		xbestueckeOOo.DruckenOderEmail("Email");
		while(DruckFenster.OOoFertig < 0){
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		boolean success = doIcalExport();
		//hier der Einstieg für den iCal-Export;
		//iCal-Kopf
		//durch die Tabelle iterieren
		//iCal-Ende
		//feddisch
		if(DruckFenster.OOoFertig == 0){
			DruckFenster.buttonsEinschalten();
			return;
		}
		ArrayList<String[]> attachments = new ArrayList<String[]>();
		String[] anhang = {null,null};//zwei weitere Elemente für iCal-Datei erforderlich

		anhang[0] = Reha.proghome+"temp/"+Reha.aktIK+"/Terminplan.pdf";
		anhang[1] = "Terminplan.pdf";
		attachments.add(anhang.clone());
		if(success){
			attachments.add(new String[] {Reha.proghome+"temp/"+Reha.aktIK+"/iCal-TherapieTermine.ics","iCal-TherapieTermine.ics"});
		}
		File f = new File(anhang[0]);
		long zeit = System.currentTimeMillis();
		while(!f.exists()){
			if(System.currentTimeMillis()-zeit > 2000){
				break;
			}
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			f = new File(anhang[0]);
			
		}
		if(!f.exists()){
			JOptionPane.showMessageDialog (null, "PDF-Emailanhang konnte nicht erzeugt werden, Aktion wird abgebrochen");
			DruckFenster.buttonsEinschalten();
			return;
		}

		String username = SystemConfig.hmEmailExtern.get("Username");
		String password = SystemConfig.hmEmailExtern.get("Password");
		String senderAddress =SystemConfig.hmEmailExtern.get("SenderAdresse");
		String secure = SystemConfig.hmEmailExtern.get("SmtpSecure");
		String useport = SystemConfig.hmEmailExtern.get("SmtpPort");
		////System.out.println("Empf�ngeradresse = "+emailaddy);
		String recipientsAddress = emailaddy+","+SystemConfig.hmEmailExtern.get("SenderAdresse");
		String subject = "Ihre Behandlungstermine";
		boolean authx = (SystemConfig.hmEmailExtern.get("SmtpAuth").equals("0") ? false : true);
		boolean bestaetigen = (SystemConfig.hmEmailExtern.get("Bestaetigen").equals("0") ? false : true);

		String text = "";
		/*********/
		 File file = new File(Reha.proghome+"vorlagen/"+Reha.aktIK+"/EmailTerminliste.txt");
	      try {
	         // FileReader zum Lesen aus Datei
	         FileReader fr = new FileReader(file);
	         // Der String, der am Ende ausgegeben wird
	         String gelesen;
	         // char-Array als Puffer fuer das Lesen. Die
	         // Laenge ergibt sich aus der Groesse der Datei
	         char[] temp = new char[(int) file.length()];
	         // Lesevorgang
	         fr.read(temp);
	         // Umwandlung des char-Arrays in einen String
	         gelesen = String.valueOf(temp);
	         text = gelesen;
	         //Ausgabe des Strings
	         ////System.out.println(gelesen);
	         // Ressourcen freigeben
	         fr.close();
	      } catch (FileNotFoundException e1) {
	         // die Datei existiert nicht
	         System.err.println("Datei nicht gefunden: ");
	      } catch (IOException e2) {
	         // andere IOExceptions abfangen.
	         e2.printStackTrace();
	      }
		/*********/
	      if (text.equals("")){
	    	  text = "Sehr geehrte Damen und Herren,\n"+
					"im Dateianhang finden Sie die PDF-Datei mit den von Ihnen gewünschten Behandlungsterminen.\n"+
	    			"Die Termine der ebenfalls beigelegten ICS-Datei, können in die elektronischen Kalender der meisten\n"+
					"mobilen Systemen wie Smartphone oder Tablet importiert werden.\n\n"+
					"Termine die Sie nicht einhalten bzw. wahrnehmen können, müssenen 24 Stunden vorher\n"+
					"abgesagt werden.\n\nIhr Planungs-Team vom RTA";
	      }
		String smtpHost = SystemConfig.hmEmailExtern.get("SmtpHost");
		
		EmailSendenExtern oMail = new EmailSendenExtern();
		try{
		oMail.sendMail(smtpHost, username, password, senderAddress, recipientsAddress, subject, text,attachments,authx,bestaetigen,secure,useport);
		DruckFenster.thisClass.cursorWait(false);
		JOptionPane.showMessageDialog (null, "Die Terminliste wurde aufbereitet und per Email versandt\n");
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				DruckFenster.jtp.requestFocus();
			}
		});
		}catch(Exception e){
			JOptionPane.showMessageDialog (null, "Emailversand der Terminliste fehlgeschlagen!!!!\n");
			e.printStackTrace( );
		}
		f.delete();
		DruckFenster.buttonsEinschalten();
	}
	/*****************************************************************************************************/
	private boolean doIcalExport(){
		boolean success = false;
		try{
			boolean datewarning = (Boolean)SystemConfig.hmIcalSettings.get("warnen");
			List<String> x0diszis = Arrays.asList(new String[] {"KG","MA","ER","LO","RH","PO","FT","RS"});
			List<String> x1diszis = Arrays.asList(new String[] {"Physiotherapie","Massage/LD","Ergotherapie","Logopädie","Reha","Podologie","Funktionstraining","Rehasport"});
			Vector<Vector<String>> icalVec = new Vector<Vector<String>>();
			Vector<String> icalDummy = new Vector<String>();
			String xtitel;
			int xindex;
			String endzeit = "";
			int lang = pliste.getRowCount(); 
			for(int i = 0; i < lang;i++){
				icalDummy.clear();
				//Datum
				icalDummy.add(DatFunk.sDatInSQL(pliste.getValueAt(i, 1).toString()).replace("-", ""));
				//Beginn
				icalDummy.add(pliste.getValueAt(i, 2).toString().replace(":", ""));
				//Ende
				/*
				System.out.println(pliste.getValueAt(i, 1).toString());
				System.out.println(pliste.getValueAt(i, 2).toString());
				System.out.println(pliste.getValueAt(i, 4).toString());
				System.out.println(pliste.getValueAt(i, 9).toString());
				*/
				endzeit = ZeitFunk.ZeitPlusMinuten(pliste.getValueAt(i, 2).toString(), pliste.getValueAt(i, 4).toString());
				icalDummy.add(endzeit.replace(":", "")+"00");
				//Terminart (Physio, Logo etc.)
				if( (xtitel=pliste.getValueAt(i, 9).toString()).trim().length() >= 2){
					if( (xindex=x0diszis.indexOf((String)xtitel.substring(0,2))) < 0 ){
						icalDummy.add("Therapie-Termin");
					}else{
						icalDummy.add((String)x1diszis.get(xindex));
					}
				}else{
					icalDummy.add("Therapie-Termin");
				}
				//Terminbeschreibung
				icalDummy.add(((String)SystemConfig.hmIcalSettings.get("beschreibung")).replace("\n","CRLF"));
				
				icalVec.add((Vector)icalDummy.clone());
			}
			StringBuffer buf = new StringBuffer();
			buf.append(ICalGenerator.macheKopf());
			for(int i = 0;i<icalVec.size();i++){
				buf.append(ICalGenerator.macheVevent(icalVec.get(i).get(0), icalVec.get(i).get(1), icalVec.get(i).get(2), icalVec.get(i).get(3), icalVec.get(i).get(4),datewarning));
			}
			buf.append(ICalGenerator.macheEnd());
			FileOutputStream outputFile = new  FileOutputStream(Reha.proghome+"temp/"+Reha.aktIK+"/iCal-TherapieTermine.ics");
            //OutputStreamWriter out = new OutputStreamWriter(outputFile, "ISO-8859-1"); 
            OutputStreamWriter out = new OutputStreamWriter(outputFile, "UTF8");
			BufferedWriter bw = null;
			bw = new BufferedWriter(out);
			bw.write(buf.toString());
			bw.flush();
			bw.close();
			out.close();
			outputFile.close();			
			return true;
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return success;
	}
	
	
	/*****************************************************************************************************/
	private String holeAusDB(String exStatement){
		Statement stmt = null;
		ResultSet rs = null;
		String sergebnis = "";
		try {
			stmt = (Statement) Reha.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE );
			try{
				rs = (ResultSet) stmt.executeQuery(exStatement);		
				while(rs.next()){
					sergebnis = (rs.getString(1) == null ? "" : rs.getString(1));
				}
			}catch(SQLException ev){
        		//System.out.println("SQLException: " + ev.getMessage());
        		//System.out.println("SQLState: " + ev.getSQLState());
        		//System.out.println("VendorError: " + ev.getErrorCode());
			}	

		}catch(SQLException ex) {
			//System.out.println("von stmt -SQLState: " + ex.getSQLState());
		}

		finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException sqlEx) { // ignore }
					rs = null;
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException sqlEx) { // ignore }
					stmt = null;
				}
			}
			
		}
		return sergebnis;
	}
	 
	
}
/*******************************************/
class MyTerminTableModel extends DefaultTableModel{
	   /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Class<?> getColumnClass(int columnIndex) {
		   if(columnIndex==0){return String.class;}
		   else{return String.class;}
	}

	public boolean isCellEditable(int row, int col) {
		 	return true;
	}
	public Object getValueAt(int rowIndex, int columnIndex) {
		String theData = (String) ((Vector<?>)getDataVector().get(rowIndex)).get(columnIndex); 
		Object result = null;
		result = theData;
		return result;
	}
}
/******************************************/
}
/******************************************/


final class druckListeSperren{
	public boolean schongesperrt(String[] exStatement){
		Statement stmt = null;
		ResultSet rs = null;
		String sergebnis = "";
		//boolean gesperrt = false;
		try {
			stmt = (Statement) Reha.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE );
			try{
				rs = (ResultSet) stmt.executeQuery(exStatement[0]);		
				while(rs.next()){
					sergebnis = (rs.getString(1) == null ? "" : rs.getString(1));
				}
				
				//System.out.println("Befehl ausgef�hrt"+exStatement[0]);
			}catch(SQLException ev){
        		//System.out.println("SQLException: " + ev.getMessage());
        		//System.out.println("SQLState: " + ev.getSQLState());
        		//System.out.println("VendorError: " + ev.getErrorCode());
			}	

		}catch(SQLException ex) {
			//System.out.println("von stmt -SQLState: " + ex.getSQLState());
		}

		finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException sqlEx) { // ignore }
					rs = null;
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException sqlEx) { // ignore }
					stmt = null;
				}
			}
		}
		
		//System.out.println("Ergebnis = "+sergebnis);
		if(sergebnis.trim().equals("")){
			//System.out.println("Befehl ausgef�hrt");
			sperren(exStatement[1]);
			//System.out.println("Befehl ausgef�hrt"+exStatement[1]);
			sperren(exStatement[2]);			
			//System.out.println("Befehl ausgef�hrt"+exStatement[2]);
			sperren(exStatement[3]);
			//System.out.println("Befehl ausgef�hrt"+exStatement[3]);			
			return true;
		}else{
			return false;
		}
		 
	}
	private boolean sperren(String exStatement){
		Statement stmt = null;
		ResultSet rs = null;
		boolean boolergebnis = false;
		try {
			stmt = (Statement) Reha.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE );
			try{
				boolergebnis = stmt.execute(exStatement);		
				
			}catch(SQLException ev){
        		//System.out.println("SQLException: " + ev.getMessage());
        		//System.out.println("SQLState: " + ev.getSQLState());
        		//System.out.println("VendorError: " + ev.getErrorCode());
			}	

		}catch(SQLException ex) {
			//System.out.println("von stmt -SQLState: " + ex.getSQLState());
		}

		finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException sqlEx) { // ignore }
					rs = null;
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException sqlEx) { // ignore }
					stmt = null;
				}
			}

		}
		return boolergebnis;
	}
	
}
