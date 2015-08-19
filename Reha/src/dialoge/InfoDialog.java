package dialoge;

import hauptFenster.Reha;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.text.DecimalFormat;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JXPanel;

import stammDatenTools.RezTools;
import systemEinstellungen.SystemConfig;
import systemEinstellungen.SystemPreislisten;
import utils.DatFunk;
import CommonTools.StringTools;
import CommonTools.JCompTools;
import CommonTools.SqlInfo;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class InfoDialog extends JDialog implements WindowListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2903001967749371315L;
	private JLabel textlab;
	private JLabel bildlab;
	private KeyListener kl;
	Font font = new Font("Arial",Font.PLAIN,12);
	
	private String arg1 = null;
	private String infoArt = null;
	JEditorPane htmlPane1 = null;
	JEditorPane htmlPane2 = null;
	
	Vector <Vector<String>> vecResult = null;
	Vector<String> tage = null;
	boolean historie = false;
	boolean notfound = false;
	String endhinweis = "";
	JScrollPane scr1 = null;
	JScrollPane scr2 = null;
	
	String last12Wo = null;
	
	String disziplin = "";
	int tagebreak = 0;
	
	DecimalFormat df = new DecimalFormat( "0.00" );
	
	public InfoDialog(String arg1,String infoArt,Vector<Vector<String>> data) {
		super();
		setUndecorated(true);
		setModal(true);
		this.arg1 = arg1;
		this.infoArt = infoArt;
		activateListener();
		if(this.infoArt.equals("terminInfo")){
			this.setContentPane(getTerminInfoContent());
		}else if(this.infoArt.equals("offenRGAF")){
			this.setContentPane(getOffeneRechnungenInfoContent(data));
		}else{	
			this.setContentPane(getContent());	
		}
		
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.addKeyListener(kl);
		validate();
	}
	
	public JXPanel getTerminInfoContent(){
		JXPanel jpan = new JXPanel();
		jpan.addKeyListener(kl);
		//jpan.setPreferredSize(new Dimension(400,100));
		jpan.setBackground(Color.WHITE);
		jpan.setBorder(BorderFactory.createLineBorder(Color.BLACK));

		FormLayout lay = new FormLayout("5dlu,fill:0:grow(0.5),p,fill:0:grow(0.5),5dlu",
				"5dlu,p,5dlu,p,p,max(350dlu;p),0dlu,100dlu,5dlu");
		jpan.setLayout(lay);
		CellConstraints cc = new CellConstraints();
		bildlab = new JLabel(" ");
		bildlab.setIcon(SystemConfig.hmSysIcons.get("tporgklein"));
		jpan.add(bildlab,cc.xy(3, 2));
		htmlPane1 = new JEditorPane(/*initialURL*/);
        htmlPane1.setContentType("text/html");
        htmlPane1.setEditable(false);
        htmlPane1.setOpaque(false);
        htmlPane1.addKeyListener(kl);
        //htmlPane.addHyperlinkListener(this);
        scr1 = JCompTools.getTransparentScrollPane(htmlPane1);
        scr1.validate();	
        jpan.add(scr1,cc.xywh(2,4,2, 3));

		htmlPane2 = new JEditorPane(/*initialURL*/);
        htmlPane2.setContentType("text/html");
        htmlPane2.setEditable(false);
        htmlPane2.setOpaque(false);
        htmlPane2.addKeyListener(kl);
        scr2 = JCompTools.getTransparentScrollPane(htmlPane2);
        scr2.validate();	
        jpan.add(scr2,cc.xyw(2,8,2));
        
        holeTerminInfo();
		jpan.validate();
		return jpan;
	}
	
	public JXPanel getOffeneRechnungenInfoContent(Vector<Vector<String>> vdata){
		JXPanel jpan = new JXPanel();
		jpan.addKeyListener(kl);
		//jpan.setPreferredSize(new Dimension(400,100));
		jpan.setBackground(Color.WHITE);
		jpan.setBorder(BorderFactory.createLineBorder(Color.BLACK));

		FormLayout lay = new FormLayout("5dlu,fill:0:grow(0.5),p,fill:0:grow(0.5),5dlu",
				"5dlu,p,5dlu,p,p,250dlu,5dlu,150dlu:g,5dlu");
		jpan.setLayout(lay);
		CellConstraints cc = new CellConstraints();
		bildlab = new JLabel(" ");
		bildlab.setIcon(SystemConfig.hmSysIcons.get("tporgklein"));
		jpan.add(bildlab,cc.xy(3, 2));
		htmlPane1 = new JEditorPane(/*initialURL*/);
        htmlPane1.setContentType("text/html");
        htmlPane1.setEditable(false);
        htmlPane1.setOpaque(false);
        htmlPane1.addKeyListener(kl);
        //htmlPane.addHyperlinkListener(this);
        scr1 = JCompTools.getTransparentScrollPane(htmlPane1);
        scr1.validate();	
        jpan.add(scr1,cc.xywh(2,4,2, 3));

		htmlPane2 = new JEditorPane(/*initialURL*/);
        htmlPane2.setContentType("text/html");
        htmlPane2.setEditable(false);
        htmlPane2.setOpaque(false);
        htmlPane2.addKeyListener(kl);
        scr2 = JCompTools.getTransparentScrollPane(htmlPane2);
        scr2.validate();	
        jpan.add(scr2,cc.xyw(2,8,2));		

		holeOffeneRechnungen(vdata);
		scr1.validate();
		scr2.validate();
		jpan.revalidate();
		return jpan;
	}	

	private void activateListener(){
		kl = new KeyListener(){
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if(e.getKeyCode()==KeyEvent.VK_F1 && (!e.isControlDown()) && (!e.isShiftDown())){
					dispose();
				}
				
			}
			
		};
	}
	public JXPanel getContent(){
	JXPanel jpan = new JXPanel();
	jpan.addKeyListener(kl);
	jpan.setPreferredSize(new Dimension(400,100));
	jpan.setBackground(Color.WHITE);
	jpan.setBorder(BorderFactory.createLineBorder(Color.BLACK));

	FormLayout lay = new FormLayout("fill:0:grow(0.5),p,fill:0:grow(0.5)",
			"fill:0:grow(0.25),p,15dlu,p,fill:0:grow(0.75)");
	jpan.setLayout(lay);
	CellConstraints cc = new CellConstraints();
	bildlab = new JLabel(" ");
	bildlab.setIcon(SystemConfig.hmSysIcons.get("tporgklein"));
	jpan.add(bildlab,cc.xy(2, 2));
	textlab = new JLabel(" ");
	textlab.setFont(font);
	textlab.setForeground(Color.BLUE);
	jpan.add(textlab,cc.xy(2, 4,CellConstraints.CENTER,CellConstraints.CENTER));
	jpan.validate();
	int tagebeginn = 0;
	int tagebreak = 0;
	return jpan;
	
	}
	public void setzeLabel(String labelText){
		textlab.setText(labelText);
		textlab.getParent().validate();
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	@Override
	public void windowOpened(WindowEvent e) {
	}
	@Override
	public void windowClosing(WindowEvent e) {
	}
	@Override
	public void windowClosed(WindowEvent e) {
	}
	@Override
	public void windowIconified(WindowEvent e) {
	}
	@Override
	public void windowDeiconified(WindowEvent e) {
	}
	@Override
	public void windowActivated(WindowEvent e) {
	}
	@Override
	public void windowDeactivated(WindowEvent e) {
	}
	
	private void holeTerminInfo(){
		//vecResult = SqlInfo.holeFelder("select lastdate,termine from verordn where rez_nr = '"+macheNummer(this.arg1)+"' LIMIT 1");
		vecResult = SqlInfo.holeFelder("select t1.lastdate,t1.termine,t2.n_name,t2.v_name,t1.kuerzel1,t1.anzahl1,t1.kuerzel2,t1.anzahl2,t1.kuerzel3,t1.anzahl3,t1.kuerzel4,t1.anzahl4,ktraeger,rezeptart,zzstatus,preisgruppe,rez_datum,arztbericht,berid from verordn as t1 join pat5 as t2 on(t2.pat_intern = t1.pat_intern) where t1.rez_nr = '"+macheNummer(this.arg1)+"' LIMIT 1");
		if(vecResult.size()<=0){
			this.historie=true;
			vecResult = SqlInfo.holeFelder("select t1.lastdate,t1.termine,t2.n_name,t2.v_name,t1.kuerzel1,t1.anzahl1,t1.kuerzel2,t1.anzahl2,t1.kuerzel3,t1.anzahl3,t1.kuerzel4,t1.anzahl4,ktraeger,rezeptart,zzstatus,preisgruppe,rez_datum,arztbericht,berid from lza as t1 join pat5 as t2 on(t2.pat_intern = t1.pat_intern) where t1.rez_nr = '"+macheNummer(this.arg1)+"' LIMIT 1");
			if(vecResult.size() > 0){
				JOptionPane.showMessageDialog(null,"Dieses Rezept ist bereits in der Historie - also abgerechnet!"+"\nRezeptnummer: "+this.arg1);				
			}else{
				JOptionPane.showMessageDialog(null,"Dieses Rezept ist weder im aktuellen Rezeptstamm noch in der Historie - also gelöscht!"+"\nRezeptnummer: "+this.arg1);
			}
			SwingUtilities.invokeLater(new Runnable(){
				public void run(){
					dispose();
				}
			});
			return;
		}
		
		try{
			//int frist = (Integer)((Vector<?>)SystemPreislisten.hmFristen.get(disziplin).get((erstebehandlung ? 0 : 2))).get(preisgruppe);
			disziplin = StringTools.getDisziplin(this.arg1);
			
			tagebreak = (Integer)((Vector<?>)SystemPreislisten.hmFristen.get(disziplin).get(2)).get(Integer.parseInt(vecResult.get(0).get(15))-1 );

		}catch(Exception ex){
			tagebreak = 28;
			ex.printStackTrace();
		}
		
		fuelleHTML();
	}
	
	private void fuelleHTML(){
		try{
			String shtml = ladehead()+
			getMittelTeil()+
			ladeend();
			htmlPane1.setText(shtml);
			scr1.revalidate();
			htmlPane1.validate();
			SwingUtilities.invokeLater(new Runnable(){
				public void run(){
					if(scr1.isVisible()){
						scr1.getVerticalScrollBar().setValue(0);
						//scr1.getViewport().scrollRectToVisible(new Rectangle(0,0));
					}
				}
			});
			/**************/
			shtml = ladehead()+
					getErgebnisTeil();
					ladeend();
			htmlPane2.setText(shtml);
			scr2.revalidate();
			htmlPane2.validate();
			SwingUtilities.invokeLater(new Runnable(){
				public void run(){
					if(scr2.isVisible()){
						scr2.getVerticalScrollBar().setValue(0);
						//scr1.getViewport().scrollRectToVisible(new Rectangle(0,0));
					}
				}
			});
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	public static String macheNummer(String string){
		if(string.indexOf("\\") >= 0){
			return string.substring(0,string.indexOf("\\"));
		}
		return string;
	}
	/***************************************************/
	public String getErgebnisTeil(){
		StringBuffer  ergebnis = new StringBuffer();
		if(tage.size() == Integer.parseInt(vecResult.get(0).get(5))){
			ergebnis.append("<span "+getSpanStyle("12","#ff0000")+"<b>Das Rezept ist voll</b></span>\n");
		}else if(tage.size() > 0 && tage.size() < Integer.parseInt(vecResult.get(0).get(5))){
			String slastdate = DatFunk.sDatPlusTage(tage.get(tage.size()-1), tagebreak);
			String lastdate = DatFunk.WochenTag(slastdate)+" "+slastdate;
			long diff = 0;
			if( (diff = DatFunk.TageDifferenz(slastdate, DatFunk.sHeute())) > 0 ){
				ergebnis.append("<span "+getSpanStyle("14","#ff0000")+"<b>"+lastdate+"</b></span><br>"+"<span "+getSpanStyle("10","")+"Maximale Unterbrechung verpasst "+lastdate+"!! ("+Integer.toString(tagebreak)+" Tage)</span>\n");	
			}else{
				ergebnis.append("<span "+getSpanStyle("14","#008000")+"<b>"+lastdate+"</b></span><br>"+"<span "+getSpanStyle("10","")+"nächster Termin spätestens am " +lastdate+" ("+Integer.toString(tagebreak)+" Tage)</span>\n");
			}
			
		}else if(tage.size() == 0){
			long diff = 0;
			if((diff = DatFunk.TageDifferenz(DatFunk.sDatInDeutsch(vecResult.get(0).get(0)), DatFunk.sHeute())) > 0){
				ergebnis.append("<span "+getSpanStyle("14","#ff0000")+"<b>"+DatFunk.sDatInDeutsch(vecResult.get(0).get(0))+"</b></span><br>"+"<span "+getSpanStyle("10","")+"Achtung Behandlungsbeginn heute überschreitet<br>den spätestesn Behandlungsbeginn<br>um "+Long.toString(diff)+" Tage!!</span>\n");
			}else{
				ergebnis.append("<span "+getSpanStyle("14","#008000")+"<b>"+DatFunk.sDatInDeutsch(vecResult.get(0).get(0))+"</b></span><br>"+"<span "+getSpanStyle("10","")+"Behandlung noch nicht begonnen stand heute<br>spätester Behandlungsbeginn<br>in "+Long.toString(diff)+" Tage(n)!!</span>\n");				
			}
		}
		//jetzt den Saich mit der 12-Wochenfrist für ADR
		if(vecResult.get(0).get(13).equals("2") && tage.size() > 0){
			String lastdate = DatFunk.sDatPlusTage(tage.get(0), (12*7)-1);
			long diff = DatFunk.TageDifferenz(lastdate, DatFunk.sHeute());
			if(diff > 0){
				ergebnis.append("<span "+getSpanStyle("14","#ff0000")+"<br><br><b>"+DatFunk.WochenTag(lastdate)+" "+lastdate+" VO-AdR</b></span><br>"+"<span "+getSpanStyle("10","")+"letzter Behandlungstermin spätestens am "+lastdate+" (12 Wochenfrist)</span>\n");	
			}else{
				ergebnis.append("<span "+getSpanStyle("14","#008000")+"<br><br><b>"+DatFunk.WochenTag(lastdate)+" "+lastdate+" VO-AdR</b></span><br>"+"<span "+getSpanStyle("10","")+"letzter Behandlungstermin spätestens am  "+lastdate+" (12 Wochenfrist)</span>\n");
			}
		}else if(!vecResult.get(0).get(13).equals("2") && Integer.parseInt(vecResult.get(0).get(5)) >= 10){
			String lastdate = DatFunk.sDatPlusTage(tage.get(0), (12*7)-1);
			long diff = DatFunk.TageDifferenz(lastdate, DatFunk.sHeute());
			if(diff > 0){
				ergebnis.append("<span "+getSpanStyle("14","#ff0000")+"<br><br><b>"+DatFunk.WochenTag(lastdate)+" "+lastdate+"</b></span><br>"+"<span "+getSpanStyle("10","")+"letzter Behandlungstermin spätestens am "+lastdate+" (12 Wochenfrist)</span>\n");	
			}else{
				ergebnis.append("<span "+getSpanStyle("14","#008000")+"<br><br><b>"+DatFunk.WochenTag(lastdate)+" "+lastdate+"</b></span><br>"+"<span "+getSpanStyle("10","")+"letzter Behandlungstermin spätestens am "+lastdate+" (12 Wochenfrist)</span>\n");
			}
		}
		
		return ergebnis.toString();
	}	
	/***************************************************/
	public String getMittelTeil(){
		StringBuffer  mitte = new StringBuffer();
		boolean zuzahl = (vecResult.get(0).get(14).equals("0") ? false : true);
		tage =  RezTools.holeEinzelTermineAusRezept("",vecResult.get(0).get(1)) ;
		Object[] otest = {null,null};
		/**********************/
		if(tage.size() > 0){
			last12Wo = DatFunk.sDatPlusTage(tage.get(0), (12*7)-1);	
		}else{
			last12Wo = DatFunk.sDatPlusTage(DatFunk.sDatInDeutsch(vecResult.get(0).get(0)), (12*7)-1);
		}
		
		//System.out.println("12 Wochenfrist läuft ab am "+last12Wo);
		/*********************/
		
		mitte.append("<span "+getSpanStyle("12","")+StringTools.EGross(vecResult.get(0).get(2))+", "+StringTools.EGross(vecResult.get(0).get(3))+"<br>Rezeptnummer: "+this.arg1+
				"<br>"+StringTools.EGross(vecResult.get(0).get(12))+"</span>"+"<br><br>\n");
		mitte.append("<span "+getSpanStyle("10","")+getPositionen()+"</span>"+"<br><br>\n");
		
		if(!zuzahl){
			mitte.append("<span "+getSpanStyle("10","")+"Rezeptgebühr: </span><span "+getSpanStyle("10","#008000")+ "<b>nicht erforderlich oder befreit</b>"+"</span>"+"<br>\n");
		}else{
			if(vecResult.get(0).get(14).equals("1")){
				mitte.append("<span "+getSpanStyle("10","")+"Rezeptgebühr: </span><span "+getSpanStyle("10","#008000")+ "<b>bezahlt</b>"+"</span>"+"<br>\n");
			}else if(vecResult.get(0).get(14).equals("2")){
				mitte.append("<span "+getSpanStyle("10","")+"Rezeptgebühr: </span><span "+getSpanStyle("10","#ff0000")+ "<b>nicht bezahlt</b>"+"</span>"+"<br>\n");
			}
				
		}
		if(vecResult.get(0).get(17).equals("T")){
			if(vecResult.get(0).get(18).equals("") || vecResult.get(0).get(18).equals("-1") || vecResult.get(0).get(18).equals("0") ){
				mitte.append("<span "+getSpanStyle("10","")+"Arztbericht: </span><span "+getSpanStyle("10","#ff0000")+ "<b>noch nicht erstellt</b>"+"</span>"+"<br>\n");
			}else{
				mitte.append("<span "+getSpanStyle("10","")+"Arztbericht: </span><span "+getSpanStyle("10","#008000")+ "<b>bereits angelegt</b>"+"</span>"+"<br>\n");
			}
		}
		mitte.append("<span "+getSpanStyle("10","")+"Bislang durchgeführt: <b>"+Integer.toString(tage.size())+" von "+vecResult.get(0).get(5)+"</b></span>"+"<br>\n");
		mitte.append("<span "+getSpanStyle("10","")+"Rezeptdatum: <b>"+DatFunk.sDatInDeutsch(vecResult.get(0).get(16))+"</b></span>"+"<br>\n");
		mitte.append("<span "+getSpanStyle("10","")+"Spätester Behandlungsbeginn: <b>"+DatFunk.sDatInDeutsch(vecResult.get(0).get(0))+"</b></span>"+"<br><br>\n");	
		
		int pghmr = Integer.parseInt(vecResult.get(0).get(15));
		String disziplin = StringTools.getDisziplin(this.arg1);

		if(SystemPreislisten.hmHMRAbrechnung.get(disziplin).get(pghmr-1) < 1){
			mitte.append("<span "+getSpanStyle("14","#ff0000")+"<b>Keine HMR-Prüfung erforderlich</b>"+"</span>"+"<br><br>\n");
		}
		mitte.append("<table width='100%'>\n");
		/***********************************************************************************/
		long diff = 0;
		long diff1 = 0;
		
		for(int i = 0; i < tage.size();i++){
			mitte.append("<tr>\n");
			mitte.append("<td class='itemkleinodd'>"+Integer.toString(i+1)+"</td>\n");
			mitte.append("<td class='itemkleinodd'>"+tage.get(i)+"</td>\n");
			
			if(i==0){
				//zuerst testen ob vor dem Rezeptdatum begonnen wurde
				if( (diff1=DatFunk.TageDifferenz(DatFunk.sDatInDeutsch(vecResult.get(0).get(16)), tage.get(i))) < 0){
					mitte.append("<td><img src='file:///"+Reha.proghome+"icons/nichtok.gif"+"'>"+" < Rezeptdatum<br>"+Long.toString(diff1)+" Tage"+"</td>\n");
				}else{
					if( (diff = DatFunk.TageDifferenz(DatFunk.sDatInDeutsch(vecResult.get(0).get(0)), tage.get(i))) > 0){
						mitte.append("<td><img src='file:///"+Reha.proghome+"icons/nichtok.gif"+"'>"+" > "+Long.toString(diff)+" Tage"+"</td>\n");
						otest = Wochen12Test(last12Wo,tage.get(i));
						if( ((Boolean)otest[0]) == (Boolean) true){
							mitte.append("<td><img src='file:///"+Reha.proghome+"icons/nichtok.gif"+"'>"+/*otest[1].toString()*/""+" 12 Wo."+"</td>\n");
						}else{
							mitte.append("<td><img src='file:///"+Reha.proghome+"icons/ok.gif"+"'>"+/*otest[1].toString()*/""+" 12 Wo."+"</td>\n");
						}
					}else{
						mitte.append("<td><img src='file:///"+Reha.proghome+"icons/ok.gif"+"'>"+" <= "+Long.toString(diff)+" Tage"+"</td>\n");
						otest = Wochen12Test(last12Wo,tage.get(i));
						if( ((Boolean)otest[0]) == (Boolean) true){
							mitte.append("<td><img src='file:///"+Reha.proghome+"icons/nichtok.gif"+"'>"+/*otest[1].toString()*/""+" 12 Wo."+"</td>\n");
						}else{
							mitte.append("<td><img src='file:///"+Reha.proghome+"icons/ok.gif"+"'>"+/*otest[1].toString()*/""+" 12 Wo."+"</td>\n");
						}
					}
				}
			}else if(i > 0 ){
				if( (diff = DatFunk.TageDifferenz(tage.get(i-1), tage.get(i))) > tagebreak){
					mitte.append("<td><img src='file:///"+Reha.proghome+"icons/nichtok.gif"+"'>"+" > "+Long.toString(diff)+" Tage"+"</td>\n");
					otest = Wochen12Test(last12Wo,tage.get(i));
					if( ((Boolean)otest[0]) == (Boolean) true){
						mitte.append("<td><img src='file:///"+Reha.proghome+"icons/nichtok.gif"+"'>"+/*otest[1].toString()*/""+" 12 Wo."+"</td>\n");
					}else{
						mitte.append("<td><img src='file:///"+Reha.proghome+"icons/ok.gif"+"'>"+/*otest[1].toString()*/""+" 12 Wo."+"</td>\n");
					}

				}else{
					mitte.append("<td><img src='file:///"+Reha.proghome+"icons/ok.gif"+"'>"+" <= "+Long.toString(diff)+" Tage"+"</td>\n");
					otest = Wochen12Test(last12Wo,tage.get(i));
					if( ((Boolean)otest[0]) == (Boolean) true){
						mitte.append("<td><img src='file:///"+Reha.proghome+"icons/nichtok.gif"+"'>"+/*otest[1].toString()*/""+" 12 Wo."+"</td>\n");
					}else{
						mitte.append("<td><img src='file:///"+Reha.proghome+"icons/ok.gif"+"'>"+/*otest[1].toString()*/""+" 12 Wo."+"</td>\n");
					}

				}
				mitte.append("<td>&nbsp;</td>\n");
			}
			
			mitte.append("</tr>\n");
		}
		/***********************************************************************************/		
		mitte.append("</table>\n");

		return mitte.toString();
	}
	/***************************************************/
	private Object[] Tage28Test(String datumalt,String datumneu){
		Object[] oret = {null,null};
		long wert = 0;
		return oret;
	}
	private Object[] Wochen12Test(String datumalt,String datumneu){
		Object[] oret = {null,null};
		long wert = 0;
		wert = DatFunk.TageDifferenz(datumalt,datumneu);
		oret[1] = (int) Integer.parseInt( Long.toString(wert-1) );
		if( wert > 0){
			oret[0] = (Boolean) true;	
		}else{
			oret[0] = (Boolean) false;
		}
		return (Object[])oret.clone();
	}
	private String getPositionen(){
		StringBuffer  positionen = new StringBuffer();
		positionen.append( (!vecResult.get(0).get(4).equals("") ? vecResult.get(0).get(5)+" x "+vecResult.get(0).get(4) : "")+
				(!vecResult.get(0).get(6).equals("") ? ", "+vecResult.get(0).get(7)+" x "+vecResult.get(0).get(6) : "")+
				(!vecResult.get(0).get(8).equals("") ? ", "+vecResult.get(0).get(9)+" x "+vecResult.get(0).get(8) : "")+
				(!vecResult.get(0).get(10).equals("") ? ", "+vecResult.get(0).get(11)+" x "+vecResult.get(0).get(10) : "") );
		
		return positionen.toString();
	}
	private String getSpanStyle(String pix,String color){
		return "style='font-family: Arial, Helvetica, sans-serif; font-size: "+pix+"px;"+(color.length()>0 ? "color: "+color+";" : "")+ " '>";
	}
	final StringBuffer  bufhead = new StringBuffer();
	final StringBuffer  bufend = new StringBuffer();
	public String ladehead(){
		bufhead.append("<html>\n<head>\n");
		bufhead.append("<STYLE TYPE=\"text/css\">\n");
		bufhead.append("<!--\n");
		bufhead.append("A{text-decoration:none;background-color:transparent;border:none}\n");
		bufhead.append("A.even{text-decoration:underline;color: #000000; background-color:transparent;border:none}\n");
		bufhead.append("A.odd{text-decoration:underline;color: #FFFFFF;background-color:transparent;border:none}\n");
		bufhead.append("TD{font-family: Arial; font-size: 12pt; vertical-align: top;}\n");
		bufhead.append("TD.inhalt {font-family: Arial, Helvetica, sans-serif; font-size: 20px;background-color: #7356AC;color: #FFFFFF;}\n");
		bufhead.append("TD.inhaltinfo {font-family: Arial, Helvetica, sans-serif; font-size: 20px;background-color: #DACFE7; color: #1E0F87;}\n");
		bufhead.append("TD.headline1 {font-family: Arial, Helvetica, sans-serif; font-size: 14px; background-color: #EADFF7; color: #000000;}\n");
		bufhead.append("TD.headline2 {font-family: Arial, Helvetica, sans-serif; font-size: 14px; background-color: #DACFE7; color: #000000;}\n");		
		bufhead.append("TD.headline3 {font-family: Arial, Helvetica, sans-serif; font-size: 14px; background-color: #7356AC; color: #FFFFFF;}\n");		
		bufhead.append("TD.headline4 {font-family: Arial, Helvetica, sans-serif; font-size: 14px; background-color: #1E0F87; color: #FFFFFF;}\n");		
		bufhead.append("TD.itemeven {font-family: Arial, Helvetica, sans-serif; font-size: 14px; background-color: #E6E6E6; color: #000000;}\n");
		bufhead.append("TD.itemodd {font-family: Arial, Helvetica, sans-serif; font-size: 14px; background-color: #737373; color: #F0F0F0;}\n");
		bufhead.append("TD.itemkleineven {font-family: Arial, Helvetica, sans-serif; font-size: 9px; background-color: #E6E6E6; color: #000000;}\n");
		bufhead.append("TD.itemkleinodd {font-family: Arial, Helvetica, sans-serif; font-size: 9px; background-color: #737373; color: #F0F0F0;}\n");
		bufhead.append("TD.header {font-family: Arial, Helvetica, sans-serif; font-size: 14px; background-color: #1E0F87; color: #FFFFFF;}\n");
		bufhead.append("UL {font-family: Arial, Helvetica, sans-serif; font-size: 9px;}\n");
		bufhead.append("UL.paeb { margin-top: 0px; margin-bottom: 0px; }\n");
		bufhead.append("H1 {font-family: Arial, Helvetica, sans-serif; font-size: 12px; background-color: #1E0F87; color: #FFFFFF;}\n");
		bufhead.append("--->\n");
		bufhead.append("</STYLE>\n");
		bufhead.append("</head>\n");
		bufhead.append("<body>\n");
		//bufhead.append("<div style=margin-left:30px;>");
		//bufhead.append("<font face=\"Tahoma\"><style=margin-left=30px;>");
		return bufhead.toString();
		

					
	}
	public String ladeend(){
		bufend.append("</body>\n</html>\n");
		return bufend.toString();
	}
	
	/***************************************************/
	public void holeOffeneRechnungen(Vector<Vector<String>> data){
		String complete = ladehead();
		StringBuffer bdata = new StringBuffer();
		bdata.append("<span "+getSpanStyle("12","")+"Offene RGR-/AFR-Rechnungen</span><br>\n");
		bdata.append("<table width='100%'>\n"); 
		Double gesamt = 0.00;
		//String stmt = "select t1.rdatum,t1.rnr,t1.roffen,t1.pat_intern from rgaffaktura as t1 join pat5 as t2 on (t1.pat_intern=t2.pat_intern) where t1.roffen > '0' and t1.pat_intern = '"+xpatint+"' order by t1.rdatum";
		for( int i = 0; i < data.size();i++){
			bdata.append("<tr>\n");
			bdata.append("<td>"+Integer.toString(i+1)+".</td>\n");
			bdata.append("<td>\n");
			bdata.append(DatFunk.sDatInDeutsch(data.get(i).get(0)));
			bdata.append("</td>\n");
			bdata.append("<td>\n");
			bdata.append(data.get(i).get(1));
			bdata.append("</td>\n");
			bdata.append("<td>\n");
			bdata.append(data.get(i).get(2).replace(".", ",")+" EUR");
			bdata.append("</td>\n");
			bdata.append("</tr>\n");
			gesamt = gesamt+Double.parseDouble(data.get(i).get(2));
		}
		bdata.append("<tr>\n");
		bdata.append("<td>\n");
		bdata.append("&nbsp;");
		bdata.append("</td>\n");
		bdata.append("<td>\n");
		bdata.append("<b>Summe</b>");
		bdata.append("</td>\n");
		bdata.append("<td>\n");
		bdata.append("&nbsp;");
		bdata.append("</td>\n");
		bdata.append("<td>\n");
		bdata.append("<b>"+df.format( gesamt)+"</b> EUR");
		bdata.append("</td>\n");
		bdata.append("</tr>\n");
		
		bdata.append("</table>\n");
		complete = complete+bdata.toString()+ladeend();
		htmlPane1.setText(complete);
		
		bdata.setLength(0);
		bdata.trimToSize();
		complete = "";
		complete = ladehead();
		bdata.append("<span "+getSpanStyle("14","")+"Merkmale für diesen Patient</span><br>\n");
		int durchlauf = 0;
		for(int i = 62; i > 56;i--){
			if(Reha.thisClass.patpanel.patDaten.get(i).equals("T")){
				/*
				vPatMerker.add(inif.getStringProperty("Kriterien", "Krit"+i));
				String simg = inif.getStringProperty("Kriterien", "Image"+i);
				if(simg.equals("")){
					vPatMerkerIcon.add(null);
				}else{
					vPatMerkerIcon.add(new ImageIcon(Reha.proghome+"icons/"+simg));
				}*/
				if(SystemConfig.vPatMerkerIconFile.get(durchlauf) != null){
					bdata.append("<img src='file:///"+SystemConfig.vPatMerkerIconFile.get(durchlauf)+"'>&nbsp;&nbsp;");	
				}
				
				bdata.append("<span "+getSpanStyle("12","#FF0000")+SystemConfig.vPatMerker.get(durchlauf)+"</span><br>\n");
				
				
			}
			durchlauf++;
		}
		complete = complete+bdata.toString()+ladeend();
		htmlPane2.setText(complete);
		 
	}


}

