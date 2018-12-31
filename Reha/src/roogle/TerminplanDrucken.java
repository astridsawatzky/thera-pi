package roogle;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

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
import emailHandling.EmailSendenExtern;
import environment.Path;
import hauptFenster.Reha;
import systemEinstellungen.SystemConfig;





public class TerminplanDrucken extends Thread {
private Vector<TermObjekt> termindat = null;
private boolean ldrucken;
private boolean ldirektsenden;
private boolean lendlos;
private String patient;
private String rezept;
public int  seiten = 1;
TerminplanDrucken thisDruck = null;
String[] tabName;
static String exporturl = "";
SuchenSeite eltern;
	public void init(Vector<TermObjekt> termdat,boolean drucken,String patName,String rezNr,SuchenSeite xeltern,boolean ldirektsenden){
		this.termindat = termdat;
		this.ldrucken = drucken;
		this.patient = patName;
		this.rezept = rezNr;
		this.ldirektsenden = ldirektsenden;
		eltern = xeltern;
		thisDruck = this;
		start();
	}

	@Override
    public synchronized void run() {
			String url = Path.Instance.getProghome()+"vorlagen/"+Reha.getAktIK()+"/"+SystemConfig.oTerminListe.NameTemplate;
			String terminDrucker = SystemConfig.oTerminListe.NameTerminDrucker;
			int anzahl = termindat.size();
			int AnzahlTabellen = SystemConfig.oTerminListe.AnzahlTerminTabellen;
			lendlos = SystemConfig.oTerminListe.EndlosDruck;
			int maxTermineProTabelle = SystemConfig.oTerminListe.AnzahlTermineProTabelle;
			int maxTermineProSeite = AnzahlTabellen * maxTermineProTabelle;
			//int spaltenProtabelle = SystemConfig.oTerminListe.AnzahlSpaltenProTabellen;
			Vector<String> spaltenNamen = SystemConfig.oTerminListe.NamenSpalten;
			int ipatdrucken = SystemConfig.oTerminListe.PatNameDrucken;
			int iheader = SystemConfig.oTerminListe.MitUeberschrift;
			//String patplatzhalter = SystemConfig.oTerminListe.PatNamenPlatzhalter;

			//int anzahl = oOTermine.size();
			String patname = (patient.indexOf("?")>=0 ? patient.substring(1).trim() : patient.trim());
			String rez = (rezept.trim().equals("") ? "" : " - "+rezept.trim());
	        patname = patname+rez;

	        /**********/
	        if(ldirektsenden){
		        eltern.getFortschritt().setStringPainted(true);
		        //eltern.getFortschritt().setIndeterminate(true);
		        eltern.setFortschrittZeigen(true);
		        eltern.setFortschrittRang(0, Long.valueOf(Integer.toString(termindat.size())));
		        eltern.setFortschrittSetzen(0);
		        //eltern.setFortschrittZeigen(true);
	        }
	        /**********/

	        IDocumentService documentService = null;
			if(!Reha.officeapplication.isActive()){
				Reha.starteOfficeApplication();
			}
			try{
				documentService = Reha.officeapplication.getDocumentService();

			} catch (OfficeApplicationException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null,"Fehler im OpenOffice-System, Terminplan kann nicht gedruckt werden");
			}

	        IDocumentDescriptor docdescript = new DocumentDescriptor();
	        docdescript.setHidden(true);
	        docdescript.setAsTemplate(true);
			IDocument document = null;
			ITextTable[] tbl = null;

			try {
				document = documentService.loadDocument(url,docdescript);

			} catch (NOAException e) {
				
				e.printStackTrace();
			}
			/**********************/
			ITextDocument textDocument = (ITextDocument)document;
			tbl = textDocument.getTextTableService().getTextTables();

			if(tbl.length != AnzahlTabellen){
				JOptionPane.showMessageDialog (null, "Anzahl Tabellen stimmt nicht mit der Vorlagen.ini überein.\nDruck nicht m�glich");
				textDocument.close();
		        if(ldirektsenden){
		        	eltern.cursorWait(false);
		        }
				return;
			}
			tabName = new String[AnzahlTabellen];
			int x = 0;
			for(int i=AnzahlTabellen;i>0;i--){
				tabName[x] = tbl[(tbl.length-1)-x].getName();
				////System.out.println(tabName[x]);
				x++;
			}
			/*********************/

			//Aktuellen Drucker ermitteln
			String druckerName = null;
			try {
				druckerName = textDocument.getPrintService().getActivePrinter().getName();
			} catch (NOAException e) {
				
				e.printStackTrace();
			}
			//Wenn nicht gleich wie in der INI angegeben -> Drucker wechseln
			IPrinter iprint = null;
			if(! druckerName.equals(terminDrucker)){
				try {
					iprint = textDocument.getPrintService().createPrinter(terminDrucker);
				} catch (NOAException e) {
					
					e.printStackTrace();
				}
				try {
					textDocument.getPrintService().setActivePrinter(iprint);
				} catch (NOAException e) {
					
					e.printStackTrace();
				}
			}
			//Jetzt den Platzhalter ^Name^ suchen
			//SearchDescriptor searchDescriptor = null;
			//ISearchResult searchResult = null;
			if(ipatdrucken  > 0){
		      ITextFieldService textFieldService = textDocument.getTextFieldService();
		      ITextField[] placeholders = null;
				try {
					placeholders = textFieldService.getPlaceholderFields();
				} catch (TextException e) {
					
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
			/********************************************/
			//int zeile = 0;
			//int startTabelle = 0;
			int aktTabelle = 0;
			int aktTermin = -1;
			int aktTerminInTabelle = -1;
			String druckDatum = "";
			ITextTable textTable = null;
			try {
				textTable = textDocument.getTextTableService().getTextTable(tabName[aktTabelle]);
			} catch (TextException e) {
				e.printStackTrace();
			}
			int zaehler = 0;
			while(true){

				aktTerminInTabelle = aktTerminInTabelle+1;
				aktTermin = aktTermin+1;
		        if(ldirektsenden){
		        	eltern.setFortschrittSetzen(aktTermin);
		        }
				if(aktTermin >= anzahl){
					break;
				}
				if(!lendlos){
					/***********Wenn die Spalte voll ist und die aktuelle Tabelle nicht die letzte ist*/
					if(aktTerminInTabelle >= maxTermineProTabelle && aktTabelle < AnzahlTabellen-1  ){
						aktTabelle = aktTabelle+1;
						try {
							textTable = textDocument.getTextTableService().getTextTable(tabName[aktTabelle]);
						} catch (TextException e) {
							e.printStackTrace();
						}
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
							e.printStackTrace();
						}
						tbl = textDocument.getTextTableService().getTextTables();
						x = 0;
						for(int i=AnzahlTabellen;i>0;i--){
							tabName[x] = tbl[(tbl.length-1)-x].getName();
							x++;
						}

						if(ipatdrucken  > 0){
						      ITextFieldService textFieldService = textDocument.getTextFieldService();
						      ITextField[] placeholders = null;
								try {
									placeholders = textFieldService.getPlaceholderFields();
								} catch (TextException e) {
									e.printStackTrace();
								}
								for (int i = 0; i < placeholders.length; i++) {
									String placeholderDisplayText = placeholders[i].getDisplayText();
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
							
							e.printStackTrace();
						}
					}
				}
				/********************/
				//**************/Hier die Zellen*************//
				if(!lendlos){
					if(spaltenNamen.contains("Wochentag")){
						int zelle = spaltenNamen.indexOf("Wochentag");

						druckDatum = termindat.get(aktTermin).tag;
						if(aktTerminInTabelle > 0){
							if(! druckDatum.equals(termindat.get(aktTermin-1).tag)){
								try {
									textTable.getCell(zelle,aktTerminInTabelle+iheader).getTextService().getText().setText(druckDatum.substring(0,2) );
								} catch (TextException e) {
									e.printStackTrace();
								}
							}
						}else{
							try {
								textTable.getCell(zelle,aktTerminInTabelle+iheader).getTextService().getText().setText(druckDatum.substring(0,2) );
							} catch (TextException e) {
								e.printStackTrace();
							}
						}
					}
					try {
						if(spaltenNamen.indexOf("Datum") > 0){
							int zelle = spaltenNamen.indexOf("Datum");
							textTable.getCell(zelle,aktTerminInTabelle+iheader).getTextService().getText().setText(druckDatum.substring(3) );
						}
						if(spaltenNamen.indexOf("Uhrzeit") > 0){
							int zelle = spaltenNamen.indexOf("Uhrzeit");
							textTable.getCell(zelle,aktTerminInTabelle+iheader).getTextService().getText().setText(termindat.get(aktTermin).beginn);
						}
						if(spaltenNamen.indexOf("Behandler") > 0){
							int zelle = spaltenNamen.indexOf("Behandler");
							textTable.getCell(zelle,aktTerminInTabelle+iheader).getTextService().getText().setText(termindat.get(aktTermin).termtext);
						}

					} catch (TextException e) {
						e.printStackTrace();
					}
				}else{
					if(aktTermin > 0){
						try {
							textTable.addRow(1);
						} catch (TextException e) {
						}
					}
					if(spaltenNamen.contains("Wochentag")){
						int zelle = spaltenNamen.indexOf("Wochentag");

						druckDatum = termindat.get(aktTermin).tag;
						if(aktTermin > 0){
							if(! druckDatum.equals(termindat.get(aktTermin-1).tag)){
								try {
									textTable.getCell(zelle,aktTermin+iheader).getTextService().getText().setText(druckDatum.substring(0,2) );
								} catch (TextException e) {
									e.printStackTrace();
								}
							}
						}else{
							try {
								textTable.getCell(zelle,aktTermin+iheader).getTextService().getText().setText(druckDatum.substring(0,2) );
							} catch (TextException e) {
								e.printStackTrace();
							}
						}
					}
					try {
						if(spaltenNamen.indexOf("Datum") > 0){
							int zelle = spaltenNamen.indexOf("Datum");
							textTable.getCell(zelle,aktTermin+iheader).getTextService().getText().setText(druckDatum.substring(3) );
						}
						if(spaltenNamen.indexOf("Uhrzeit") > 0){
							int zelle = spaltenNamen.indexOf("Uhrzeit");
							textTable.getCell(zelle,aktTermin+iheader).getTextService().getText().setText(termindat.get(aktTermin).beginn);
						}
						if(spaltenNamen.indexOf("Behandler") > 0){
							int zelle = spaltenNamen.indexOf("Behandler");
							textTable.getCell(zelle,aktTermin+iheader).getTextService().getText().setText(termindat.get(aktTermin).termtext);
						}

					} catch (TextException e) {
						e.printStackTrace();
					}

				}

				/********************/
				try {
					zaehler+=1;
					if(zaehler >= 50){
						Thread.sleep(25);
						zaehler=0;
					}

				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			// Jetzt das fertige Dokument drucken, bzw. als PDF aufbereiten;

			/********************************************/
			if (ldrucken){

				try {
						final ITextDocument xdoc = textDocument;
						if(SystemConfig.oTerminListe.DirektDruck){
							//System.out.println("vor print()");
							xdoc.print();
							Thread.sleep(150);
							//System.out.println("vor close()");
							xdoc.close();
							//System.out.println("nach close()");
							//eltern.setFortschrittSetzen(termindat.size()+(termindat.size()/20));
							/*
							new SwingWorker<Void,Void>(){
								@Override
								protected Void doInBackground()
										throws Exception {
									System.out.println("vor print()");
									try {
										xdoc.print();
										//textDocument.print();
										Thread.sleep(150);
									}catch (InterruptedException e) {
										e.printStackTrace();
									} catch(NullPointerException ex){
										ex.printStackTrace();
									}
									System.out.println("vor close()");
									xdoc.close();
									//textDocument.close();
									return null;
								}
							}.execute();
							*/
							if(ldirektsenden){
								eltern.setFortschrittSetzen(termindat.size());
								eltern.setFortschrittZeigen(false);
						        eltern.getFortschritt().setStringPainted(true);
								eltern.cursorWait(false);
							}
							this.termindat = null;
						}else{
							if(ldirektsenden){
								eltern.cursorWait(false);
								eltern.setFortschrittZeigen(false);
						        eltern.getFortschritt().setStringPainted(true);
							}
							this.termindat = null;
							document.getFrame().getXFrame().getContainerWindow().setVisible(true);

						}

				} catch (Exception e) {
					
					e.printStackTrace();
				}
			}else{

				exporturl = Path.Instance.getProghome()+"temp/"+Reha.getAktIK()+"/Terminplan.pdf";
				File f = new File(exporturl);
				if(f.exists()){
					f.delete();
				}
				try {
					Thread.sleep(50);
					textDocument.getPersistenceService().export(exporturl, new PDFFilter());
				} catch (DocumentException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, "Fehler bei der Aufbereitung des Terminplanes als PDF für Emailversand\nFehler: "+e.getMessage());
				} catch (InterruptedException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, "Fehler bei der Aufbereitung des Terminplanes als PDF für Emailversand\nFehler: "+e.getMessage());
				}
			}
			// Anschließend die Vorlagendatei schließen
			//textDocument.close();
			if(!ldrucken){
				final ITextDocument xdoc = textDocument;
				new SwingWorker<Void,Void>(){
					@Override
					protected Void doInBackground()
							throws Exception {
						try {
								Thread.sleep(50);
								xdoc.close();
								File f = new File(exporturl);
								if(!f.exists()){
									JOptionPane.showMessageDialog(null, "Fehler bei der Aufbereitung des Terminplanes als PDF Datei existiert nicht");
									return null;
								}
								if(ldirektsenden){
									sendeEmail();
								}
							} catch (InterruptedException e) {
								e.printStackTrace();
								JOptionPane.showMessageDialog(null, "Fehler beim Senden und Schließen des Terminplanes\nFehler: "+e.getMessage());
							} catch(Exception ex){
								JOptionPane.showMessageDialog(null, "Fehler beim Senden und Schließen des Terminplanes\nFehler: "+ex.getMessage());
							}
						//textDocument.close();

						return null;
					}
				}.execute();
				if(ldirektsenden){
					eltern.setFortschrittZeigen(false);
			        eltern.getFortschritt().setStringPainted(true);
				}
				this.termindat = null;
			}
			if(ldirektsenden){
				eltern.cursorWait(false);
			}
		}

	private void sendeEmail(){
		String emailaddy=null,pat_intern=null;
		if(this.rezept.trim().equals("")){
			emailaddy = JOptionPane.showInputDialog (null, "Bitte geben Sie eine gültige Email-Adresse ein");
			try{
				if(emailaddy.equals("")){
					return;
				}
			}catch(java.lang.NullPointerException ex){
				return;
			}
		}else{
			String trailing = null;
			if(this.rezept.trim().contains("\\")){
				trailing = this.rezept.substring(0,this.rezept.indexOf("\\"));
			}else{
				trailing = this.rezept.trim();
			}
			pat_intern = holeAusDB("select PAT_INTERN from verordn where REZ_NR ='"+trailing+"'");
			if(pat_intern.equals("")){
				emailaddy = JOptionPane.showInputDialog (null, "Bitte geben Sie eine gültige Email-Adresse ein");
				try{
					if(emailaddy.equals("")){
						return;
					}
				}catch(java.lang.NullPointerException ex){
					return;
				}
			}else{
				emailaddy = holeAusDB("select EMAILA from pat5 where PAT_INTERN ='"+pat_intern+"'");
				if(emailaddy.equals("")){
					emailaddy = JOptionPane.showInputDialog(null,"Bitte geben Sie eine gültige Email-Adresse ein" , emailaddy);
					try{
						if(emailaddy.equals("")){
						return;
						}
					}catch(java.lang.NullPointerException ex){
						return;
					}
				}else{
					emailaddy = JOptionPane.showInputDialog(null,"Soll diese Emailadresse verwendet werden?" , emailaddy);
				}
			}
		}
		/*****************bis hierher lediglich Emailadressen gewurschtel**************************/
		String[] anhang = {null,null};
		anhang[0] = Path.Instance.getProghome()+"temp/"+Reha.getAktIK()+"/Terminplan.pdf";
		anhang[1] = "Terminplan.pdf";

		File f = new File(anhang[0]);
		long zeit = System.currentTimeMillis();
		while(!f.exists()){
			if(System.currentTimeMillis()-zeit > 2000){
				break;
			}
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			f = new File(anhang[0]);

		}
		if(!f.exists()){
			JOptionPane.showMessageDialog (null, "PDF-Emailanhang konnte nicht erzeugt werden, Aktion wird abgebrochen");
			return;
		}

		ArrayList<String[]> attachments = new ArrayList<String[]>();


		attachments.add(anhang.clone());

		String username = SystemConfig.hmEmailExtern.get("Username");
		String password = SystemConfig.hmEmailExtern.get("Password");
		String senderAddress =SystemConfig.hmEmailExtern.get("SenderAdresse");
		String secure = SystemConfig.hmEmailExtern.get("SmtpSecure");
		String useport = SystemConfig.hmEmailExtern.get("SmtpPort");
		String recipientsAddress = emailaddy;
		String subject = "Ihre Behandlungstermine";
		boolean authx = (SystemConfig.hmEmailExtern.get("SmtpAuth").equals("0") ? false : true);
		boolean bestaetigen = (SystemConfig.hmEmailExtern.get("Bestaetigen").equals("0") ? false : true);
		String text = "";
		/*********/
		 File file = new File(Path.Instance.getProghome()+"vorlagen/"+Reha.getAktIK()+"/EmailTerminliste.txt");
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
					"im Dateianhang finden Sie die von Ihnen gewünschten Behandlungstermine.\n\n"+
					"Termine die Sie nicht einhalten bzw. wahrnehmen können, müssen 24 Stunden vorher\n"+
					"abgesagt werden.\n\nIhr Planungs-Team vom RTA";
	      }
		String smtpHost = SystemConfig.hmEmailExtern.get("SmtpHost");

		EmailSendenExtern oMail = new EmailSendenExtern();
		try{
			oMail.sendMail(smtpHost, username, password, senderAddress, recipientsAddress, subject, text,attachments,authx,bestaetigen,secure,useport);
			oMail = null;
			if(ldirektsenden){
				eltern.cursorWait(false);
			}

			f = new File(anhang[0]);
			if(f.exists()){
				f.delete();
				JOptionPane.showMessageDialog (null, "Die Terminliste wurde aufbereitet und per Email versandt\n");
			}else{
				JOptionPane.showMessageDialog (null, "Die Terminliste konnte nicht als PDF aufbereitet werden\n");
			}

		}catch(Exception e){
			if(ldirektsenden){
				eltern.cursorWait(false);
			}
			JOptionPane.showMessageDialog (null, "Der Emailversand der Terminliste ist fehlgeschlagen!!!!!\n");
			e.printStackTrace( );
		}




	}
	private String holeAusDB(String exStatement){
		Statement stmt = null;
		ResultSet rs = null;
		String sergebnis = "";
		try {
			stmt = Reha.instance.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE );
			try{
				rs = stmt.executeQuery(exStatement);
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


