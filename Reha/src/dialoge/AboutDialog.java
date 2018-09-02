package dialoge;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import CommonTools.ButtonTools;
import CommonTools.Environment;
import hauptFenster.Reha;

/**
 * This class shall give credit to where credit is due.
 * Anyone is free to add their name to it IF they contribute.
 * Please do not remove names from here.
 *
 * @author  McM
 */
public class AboutDialog extends JDialog implements ActionListener,KeyListener
{
	private static final long serialVersionUID = 1L;
	private String myTitle;
	public JScrollPane scroller;
	public JTextArea text;
	protected HashMap<String, String> md5Hashes = new HashMap<String, String>();
	JButton[] buts = {null,null};
	enum btIdx {detail,ok};
	int btDetail = btIdx.detail.ordinal();
	int btOK = btIdx.ok.ordinal();
	AboutDialog currInstance = null;
	JFrame instJar = null;
	Boolean processingMD5 = false;

	public AboutDialog(Frame parent, String title)
	{
        super(parent, title);
        myTitle = title;
        currInstance = this;
        
		setLayout(new BorderLayout());
		add(getDialogFrame(),BorderLayout.CENTER);

        setSize(350,200);
        //setBackground( new Color( 0,0,230 ) );
        setLocationRelativeTo(null); 							// center on screen
        
		try {
			calcMD5();
		} catch (NoSuchAlgorithmException e) {
	        System.out.println("calcMD5:  NoSuchAlgorithm");
		} catch (FileNotFoundException e) {
	        System.out.println("calcMD5:  FileNotFound");
		} catch (IOException e) {
	        System.out.println("calcMD5:  IOException");
		}

	} // end constructor

	/*
	 * 'Hintergrund' des About-Dialogs
	 */
	private JPanel getDialogFrame(){
		JPanel DialogFrame = new JPanel();

		FormLayout lay = new FormLayout(
		//        1     2     3
				 "5dlu,90dlu:g,5dlu",				// xwerte,
		//        1     2 3     4        5
				 "5dlu,30dlu:g,10dlu,20dlu,5dlu"	// ywerte
				);
		PanelBuilder builder = new PanelBuilder(lay);
		//PanelBuilder builder = new PanelBuilder(lay, new FormDebugPanel());		// debug mode
		builder.getPanel().setOpaque(false);
		CellConstraints cc = new CellConstraints();

		int colCnt=2, rowCnt=2;
		
		//JPanel upper = new JPanel();
		JPanel upper = createDialogArea();
		builder.add(upper, cc.xy(colCnt,rowCnt++));									// 2,2
		
		//JPanel lower = (JPanel)builder.add(new JPanel(), cc.xy(colCnt,++rowCnt));
		JPanel lower = getButtonRow();
		builder.add(lower, cc.xy(colCnt,++rowCnt,CellConstraints.FILL,CellConstraints.FILL));									// 2,4
		builder.getPanel().validate();
	    DialogFrame = builder.getPanel(); 

		return DialogFrame;
	}

	/*
	 * Textbereich des About-Dialogs
	 */
	protected JPanel createDialogArea() {
		JPanel dialogArea = new JPanel(new GridBagLayout());
		//dialogArea.setBackground(Color.yellow);
		GridBagConstraints gbc = new GridBagConstraints();

        ImageIcon icon = new ImageIcon(Environment.Instance.getProghome()+"icons/Pi_1_0_64x64.png");
		JLabel imgLbl = new JLabel(icon);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(20,10,5,15);		// top, left, bottom, right
        gbc.anchor = GridBagConstraints.NORTH;
		dialogArea.add(imgLbl, gbc);

		JLabel htmlPane = new JLabel(mkCreditsTxt().toString());

		scroller = new JScrollPane(htmlPane);
		scroller.setBorder(null);			// keine Umrandung

		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 1;
        gbc.gridy = 0;
		gbc.insets = new Insets(0,0,0,0);			// reset
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.gridheight = GridBagConstraints.RELATIVE;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.NORTH;
        //gbc.gridheight = GridBagConstraints.REMAINDER;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        dialogArea.add(scroller, gbc);

		return dialogArea;
	}

	/**
	 * @return Text: Copyright (u. Credits)
	 */
	private StringBuffer mkCreditsTxt() {
		StringBuffer strBuf = new StringBuffer();
		strBuf.setLength(0);
		strBuf.trimToSize();
		strBuf.append("<html>");
		strBuf.append("Thera-\u03C0 v1.0 vom "+Reha.aktuelleVersion.replace("-DB=", "")+"<br>nach einer Idee von Jürgen Steinhilber<br><br>");

		// insert credits here:
/*
		strBuf.append("Credits:<br>");
		strBuf.append("Bodo Meissner (bomm)<br>");
		strBuf.append("Ernst Lehmann (lemmi)<br>");
		strBuf.append("?? (drud)<br>");
		strBuf.append("JannyP(jannyp)<br>");
 */		
		strBuf.append("</html>");
		return strBuf;
	}

	/*
	 * Buttons des About-Dialogs
	 */
	private JPanel getButtonRow(){
		JPanel buttonArea = new JPanel();

		FormLayout lay = new FormLayout(
		//        1      2       3
				 "90dlu,20dlu:g,55dlu",				// xwerte,
		//        1     
				 "15dlu"	// ywerte
				);
		PanelBuilder builder = new PanelBuilder(lay);
		//PanelBuilder builder = new PanelBuilder(lay, new FormDebugPanel());		// debug mode
		builder.getPanel().setOpaque(false);
		CellConstraints cc = new CellConstraints();

		int colCnt=1, rowCnt=1;
		
		buts[btDetail] = ButtonTools.macheButton("Installation Details", "instDetail", this);
		buts[btDetail].setMnemonic('d');
		buts[btDetail].setToolTipText("Zeige MD5-Hashes der inst. JARs");
		buts[btDetail] = (JButton) builder.add(buts[btDetail],cc.xy(colCnt++,rowCnt));			// 1,1

		buts[btOK] = ButtonTools.macheButton("OK", "quit", this);
		buts[btOK] = (JButton) builder.add(buts[btOK],cc.xy(++colCnt,rowCnt));					// 3,1
		
		for (JButton b : buts){
			b.addKeyListener(currInstance);														// macht Tastaturbedienung möglich
		}

		buttonArea.add(builder.getPanel());

		return buttonArea;
	}


	public void setFocus(){
		buts[btOK].requestFocus();
	}
	
	/**
	 * @throws NoSuchAlgorithmException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void calcMD5() throws NoSuchAlgorithmException,
			FileNotFoundException, IOException {
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws java.lang.Exception {
				processingMD5 = true;
				MessageDigest md = MessageDigest.getInstance( "MD5" );
				File dir = new File(Environment.Instance.getProghome());
				File[] files = dir.listFiles(
						new FilenameFilter() { 
							@Override 
							public boolean accept(File dir, String name) { 
								return name.endsWith(".jar"); 
								} 
							}
						);
				md5Hashes.clear();
				for (int i = 0; i<files.length; i++){					// HM aufbauen
		            InputStream is=new FileInputStream(files[i]);
		            byte[] daten=new byte[2048];
		            int read=0;
		            md.reset();
		            while( (read = is.read(daten)) > 0)
		                    md.update(daten, 0, read);
		            byte[] hash =  md.digest();
		            BigInteger bi=new BigInteger(1, hash);
		            String output = bi.toString(16);
		            //System.out.println(output+"  "+files[i].getName()); // 5a707acfefbdbdda54166ceee7945437  Nebraska.jar
		            is.close();

					md5Hashes.put(files[i].getName(),bi.toString(16));					
				}
				processingMD5 = false;
				return null;
			}
		}.execute();		
	}

	/*
	 * Panel mit html-Tabelle erzeugen 
	 */
	private JPanel showMD5() {
		JPanel md5Table = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = 0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.gridheight = GridBagConstraints.RELATIVE;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

		JLabel htmlPane = new JLabel(mkMd5Table().toString());

		scroller = new JScrollPane(htmlPane);
		scroller.setBorder(null);			// keine Umrandung
		scroller.getVerticalScrollBar().setUnitIncrement(15);
		scroller.validate();
		//scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		md5Table.add(scroller, gbc);		// scrollen fkt noch nicht!

		return md5Table;
	}

	/**
	 * @return Text: HTML-Tabelle mit MD5-Hashes der inst. JARs erzeugen
	 */
	private StringBuffer mkMd5Table() {
		int calcRows = 0, waitMax = 100;
		StringBuffer strBuf = new StringBuffer();
		Boolean gerade = true;

		strBuf.setLength(0);
		strBuf.trimToSize();
		while (processingMD5){
			if (--waitMax ==0){
				JOptionPane.showMessageDialog(null, "MD5 Calculation failed","Error:",JOptionPane.WARNING_MESSAGE);
				return strBuf;
			}
		}
		strBuf.append("<html>");
		strBuf.append("<table>");
		strBuf.append("<tr><th>file</th><th>MD5</th><th>&nbsp;&nbsp;&nbsp;</th><th>file</th><th>MD5</th></tr>");
		List <String> jars = new ArrayList <String> ( md5Hashes.keySet());
		Collections.sort(jars);
		if ((jars.size()%2)>0) {jars.add("");}
		calcRows = jars.size()/2;

		List <String> leftCol = jars.subList(0, calcRows);
		List <String> rightCol = jars.subList(calcRows, jars.size());
		for (String key : leftCol) {									// Ausgabe sortiert, aufeinanderfolgende untereinander
            //System.out.println(i+": "+jars.get(i)+"    "+jars.get(calcRows+i)); 
			strBuf.append("<tr><td>"+key+"</td><td>"+md5Hashes.get(key)+"</td><td></td>");		// erster Eintrag in Zeile
			key = rightCol.get(leftCol.indexOf(key));
			if (!key.equals("")){
				strBuf.append("<td>"+key+"</td><td>"+md5Hashes.get(key)+"</td></tr>");			// zweiter Eintrag in Zeile								
			} else{
				strBuf.append("<td></td><td></td></tr>");										// mit Leerspalten auffüllen				
			}
		}
		strBuf.append("</table>");
		strBuf.append("</html>");
		return strBuf;
	}

/*
 * Fenster mit MD5-Hashes öffnen (+ About-Dialog schließen)
 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if(cmd.equals("instDetail")){
			//Window win = this.getOwner();				// <- Hauptfenster
			//Container cntnr = this.getParent();		// <- liefert 'das Gleiche'
			//win.removeAll();							// 'killt' alle Fkt. des Hauptfensters -  keine gute Idee!
			if (!currInstance.equals(null)){

//				currInstance.setVisible(false);
//				currInstance.removeAll();				// Fenster zeigt gar keinen Inhalt mehr (ohne remove steht createDialogArea _vor_ MD5-Liste)
				currInstance.dispose();					// eleganter wäre das Panel neu zu füllen - krieg' ich aber nicht hin :-(
				instJar = (new JFrame());				// deshalb: neuer Frame
				instJar.setTitle(myTitle+": installierte JARs");
				instJar.setLayout(new GridBagLayout());
				GridBagConstraints gbc = new GridBagConstraints();
		        gbc.fill = GridBagConstraints.BOTH;
				gbc.insets = new Insets(10,15,10,15);	// top, left, bottom, right

				JPanel newContent = (showMD5());
//				currInstance.add(new JPanel());
//				JPanel newContent = showMD5();
				//instJar.add(newContent,gbc);			// neuen Inhalt in Frame einsetzen ...
				instJar.setContentPane(newContent);
				instJar.pack();		
				//instJar.setSize(new Dimension(instJar.getPreferredSize().height-200,instJar.getPreferredSize().width));// ... und Größe anpassen
				instJar.setSize(30+newContent.getWidth(), 40+newContent.getHeight());
				instJar.addKeyListener(this);

				instJar.setLocationRelativeTo(null); 	// center on screen
				instJar.setVisible(true);
				
			}
			return;
		}
		if(cmd.equals("quit")){
			doQuit();
			return;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
    	int code = e.getKeyCode();

		if(code==KeyEvent.VK_ESCAPE){
			doQuit();
		}
		if(code==KeyEvent.VK_ENTER){
			processButtons();
		}
		if(code == KeyEvent.VK_CONTROL){
			//System.out.println("CTRL pressed");
		}	
	}

	private void processButtons() {
		for (JButton b : buts){
			if (b.hasFocus()){
				b.doClick();				// Button 'drücken'
				break;
			}
		}
	}

	private void doQuit(){
		if (instJar != null){
			instJar.dispose();				// Dialog schließen		
		}
		if (currInstance != null){
			currInstance.dispose();
		}
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

} // end class panel
