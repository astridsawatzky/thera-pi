package org.thera_pi.updates;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Properties;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.jdesktop.swingx.JXDialog;
import org.jdesktop.swingx.JXPanel;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import CommonTools.INIFile;
import environment.Path;



public class TheraPiUpdates implements WindowListener {
	private static final String PROGHOME = Path.Instance.getProghome();
	private JFrame jFrame = null;
	public static boolean starteTheraPi = false;
	public static boolean updateallowed = false;
	public static boolean showpwdlg = true;
	public static boolean macNotMatch = true;
	public static Connection conn = null;
	public JXDialog dlg;
	public Vector<String> userdaten;
	public UpdatePanel updatePanel;
	public UpdateTab updateTab;
	public String strMACAdr = "";
	public String macAdr;
	public String email;
	public String pw;
	public static boolean dbok = false;
	public static String devcomputer = "";
	public static Image imgtporg;
	public static void main(String[] args) {
		if(args.length > 0){
			starteTheraPi = true;
		}
		
		/*
		Properties systemproperties = System.getProperties();
		  for (Enumeration e = systemproperties.propertyNames(); e.hasMoreElements(); ) {
		    String prop = (String) e.nextElement();
		    System.out.println("Property: " + prop + " , Wert: " + systemproperties.getProperty(prop));
		  }		
		*/
		
		try {
			devcomputer = java.net.InetAddress.getLocalHost().toString();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		if(UpdateConfig.getInstance().isUseActiveMode())
		{
			System.out.println("FTP-Modus = ActiveMode");
		}else{
			System.out.println("FTP-Modus = PassiveMode");
		}

		System.out.println("program home: " + PROGHOME);
		
		
		TheraPiUpdates application = new TheraPiUpdates();
		
		application.createJFrame();
		if(TheraPiUpdates.showpwdlg){
			application.getPwDialog();	
		}else{
			//mit SqlInfo testen ob die Zugangsdaten koorekt sind
			//TheraPiUpdates.updateallowed
			//
			
			
			boolean test = application.testeZugang();
			if(test){
				TheraPiUpdates.updateallowed = true;
				application.starteFTP();
				
			}else{
				TheraPiUpdates.updateallowed = false;
				JOptionPane.showMessageDialog(null,"Sie haben zwar eine Zugangsdatei in Ihrem System integriert, die Zugangsdaten sind allderdings falsch");
				File ftest = new File(PROGHOME+"Libraries/lib/ocf/sig.jar");
				if(ftest.exists()){
					ftest.delete();
				}	
				if(TheraPiUpdates.starteTheraPi){
						try {
							Runtime.getRuntime().exec("java -jar "+PROGHOME+"TheraPi.jar");
						} catch (IOException e) {
							e.printStackTrace();
						}
					System.exit(0);
				}				
				application.jFrame.dispose();
				System.exit(0);
			}
		}
		
		
	}
	public void starteFTP(){
		if(UpdateConfig.getInstance().isDeveloperMode()){
			updateTab.starteFTP();
		}else{
			updatePanel.starteFTP();
		}		
	}
	public boolean testeZugang(){
		String cmd = "select id from regtpuser where email='"+this.email+"' and pw='"+this.pw+"' LIMIT 1";
		Vector<String> testvec = SqlInfo.holeFeld(conn,cmd );

		if(testvec.size() > 0){
			return true;
		}
		return false;
	}
	public JFrame createJFrame(){
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
			//UIManager.setLookAndFeel("com.jgoodies.looks.plastic.PlasticXPLookAndFeel");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		new DatenbankStarten().run();
		long warten = System.currentTimeMillis();
		while(!dbok && (System.currentTimeMillis()-warten < 10000)){
			try {
				Thread.sleep(75);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if(!dbok){
			JOptionPane.showMessageDialog(null,"Kann die Update-Datenbank nicht starten, Update-Explorer wird beendet");
		}
		jFrame = new JFrame();
		jFrame.setUndecorated(true);
		jFrame.addWindowListener(this);
		Dimension ssize = Toolkit.getDefaultToolkit().getScreenSize(); 
		//jFrame.setSize(ssize.width-(ssize.width/4),ssize.height/2);
		jFrame.setTitle("Thera-Pi  Update-Explorer");
		jFrame.setSize(ssize.width*3/4>=800 ? ssize.width*3/4 : 800, ssize.height/2>=600 ? ssize.height/2 : 600);
		jFrame.setPreferredSize(new Dimension(ssize.width*3/4>=800 ? ssize.width*3/4 : 800, ssize.height/2>=600 ? ssize.height/2 : 600));
		//jFrame.setTitle("Thera-Pi  MySql-Konfigurationsassistent");
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jFrame.setLocationRelativeTo(null);
	    try
	    {
	    	strMACAdr = getMacAddress();// getMac();
	    	/*
	        ProcessBuilder pb = new ProcessBuilder( "ipconfig", "/all" );
	        pb.redirectErrorStream( true );
	        Process p = pb.start();
	        p.getOutputStream().close(); // close Process' stdin
	        BufferedReader r = new BufferedReader( new InputStreamReader(p.getInputStream()) );
	                         
	        String strLine = "";
	        while( (strLine = r.readLine()) != null )
	        {
	        	//System.out.println(strLine);
	            if( strLine.trim().startsWith("Physikalische Adresse") )
	            {
	                strMACAdr = strLine.trim().substring( 36 );
	                System.out.println(strMACAdr);
	                break;
	            }
	        }
	        */
	        //System.out.println(strMACAdr);
	    }catch(Exception ex){
	    	ex.printStackTrace();
	    }
	    File fi = new File(PROGHOME+"ini/tpupdateneu.ini");
		if(fi.exists()){
			System.out.println("Datei existiert = "+fi.getName());
			UpdateConfig.isrta = false;
		}else{
			System.out.println("Datei existiert nicht");
		}
		/***************Testen ob die sig.jar noch altes Format hat*************/
		File ftest = new File(PROGHOME+"Libraries/lib/ocf/sig.jar");
		if(ftest.exists()){
			try {
				BufferedReader in = new BufferedReader(new FileReader(PROGHOME+"Libraries/lib/ocf/sig.jar"));
				String test = in.readLine().trim();
				in.close();
				if(!test.startsWith("[Updates]")){
					ftest.delete();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		
		//System.out.println("isrta="+isrta);
		/*
		Verschluesseln dec = Verschluesseln.getInstance();
		dec.init(Verschluesseln.getPassword().toCharArray(), dec.getSalt(), dec.getIterations());
		*/
	    File f = new File(PROGHOME+"Libraries/lib/ocf/sig.jar");
	    if(! f.exists()){
	    	System.out.println("sig nicht vorhanden");
	    	showpwdlg = true;
	    }else{
	    	try {
	    		//INIFile inif = new INIFile("C:/sig.jar");
	    		INIFile inif = new INIFile(PROGHOME+"Libraries/lib/ocf/sig.jar");
	    		
		        
		        this.macAdr = inif.getStringProperty("Updates", "1");
		        this.email = inif.getStringProperty("Updates", "2");
		        this.pw = inif.getStringProperty("Updates", "3");
		        //System.out.println(macAdr+" / "+email+" / "+pw);
		        System.out.println("Client = "+this.macAdr);
		        
	    		/*
	    		this.macAdr = dec.decrypt(inif.getStringProperty("Updates", "1"));
		        this.email = dec.decrypt(inif.getStringProperty("Updates", "2"));
		        this.pw = dec.decrypt(inif.getStringProperty("Updates", "3"));
		        System.out.println(macAdr+" / "+email+" / "+pw);
		        */
		        /*
	    		BufferedReader in = new BufferedReader(new FileReader(UpdateConfig.getProghome()+"Libraries/lib/ocf/sig.jar"));
	    		
	    		this.macAdr = dec.decrypt(in.readLine().trim());
	    		//System.out.println(macAdr);
	    		//System.out.println(strMACAdr);
	    		this.email = dec.decrypt(in.readLine().trim());
	    		//System.out.println(email);
	    		this.pw = dec.decrypt(in.readLine().trim());
	    		//System.out.println(dec.decrypt(pw.trim().replace(System.getProperty("line.separator"),"")));
	    		System.out.println(macAdr+" / "+email+" / "+pw);
	    		in.close();
	    		*/
	    		if(! macAdr.equals(strMACAdr)){
	    			JOptionPane.showMessageDialog(null,"Der Updatekanal wurde für diesen Rechner noch nicht freigeschaltet");
	    			//jFrame.dispose();
	    			//System.exit(0);
	    			showpwdlg = true;
	    			macNotMatch = true;
	    		}else{
	    			//System.out.println(strMACAdr+" = "+dec.decrypt(macAdr.trim()));
	    			showpwdlg = false;
	    		}
	    		imgtporg = new ImageIcon(PROGHOME + "icons/TPorgKlein.png").getImage().getScaledInstance(246,35, Image.SCALE_SMOOTH);
	    		
	    	} catch (Exception e) {
	    		e.printStackTrace();
	    		JOptionPane.showMessageDialog(null,"Es ist ein Fehler aufgetreten\nFehlertext:\n"+e.getMessage());
    			jFrame.dispose();
    			System.exit(0);	    		
	    	}	
	    }
		
		if(UpdateConfig.getInstance().isDeveloperMode()){
			jFrame.getContentPane().add (updateTab = new UpdateTab(this,jFrame));	
		}else{
			jFrame.getContentPane().add (updatePanel = new UpdatePanel(this,jFrame, null));	
		}
		jFrame.pack();
		jFrame.setVisible(true);

		return jFrame;
		
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		if(conn != null){
			try {
				conn.close();
				System.out.println("Verbindung geschlossen in Closed");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		System.exit(0);
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		if(conn != null){
			try {
				conn.close();
				System.out.println("Verbindung geschlossen in Closing");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		System.exit(0);
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
		
		
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
		
		
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
		
		
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
		
		
	}
	public static void RunAjax(String partUrl,String indatei,String testdatei){
		InetAddress dieseMaschine = null;
		try {
			dieseMaschine = java.net.InetAddress.getLocalHost();
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		String url = null;
		if(!dieseMaschine.toString().contains("192.168.2.2")){
			url = partUrl+"?indatei="+indatei+"&tester="+dieseMaschine.toString()+"&datei="+testdatei;
		}


		try {
			URL tester = new URL(url);
			HttpURLConnection httpURLConnection = (HttpURLConnection) tester.openConnection();
			httpURLConnection.setAllowUserInteraction(false);
	        httpURLConnection.setRequestMethod("POST");
	        httpURLConnection.getResponseMessage();
	        //System.out.println(httpURLConnection.getResponseMessage());
	        httpURLConnection.setRequestProperty("Accept", "true");
	        httpURLConnection.setDoOutput(true);
	        httpURLConnection.setUseCaches(false);
	        httpURLConnection.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	final class DatenbankStarten implements Runnable{

		void StarteDB(){
			try{
				Class.forName("de.root1.jpmdbc.Driver");
	    	}
	    	catch ( final Exception e ){
	    		JOptionPane.showMessageDialog(null,"Fehler beim Laden des Datenbanktreibers für Preislisten-Server");
	    		return;
	        }
	    	try {
				Properties connProperties = new Properties();
				connProperties.setProperty("user", "dbo486621783");
				connProperties.setProperty("password", "neuerupdateexplorer");
				connProperties.setProperty("host", "db486621783.db.1and1.com");	        			
				connProperties.setProperty("port", "3306");
				connProperties.setProperty("compression","false");
				connProperties.setProperty("NO_DRIVER_INFO", "1");
				TheraPiUpdates.conn =  DriverManager.getConnection("jdbc:jpmdbc:http://www.thera-pi.org/jpmdbc.php?db486621783",connProperties);
				
				TheraPiUpdates.dbok = true;
				
				System.out.println("Kontakt zur Update-Datenbank hergestellt");
				
	    	} 
	    	catch (final SQLException ex) {
	    		JOptionPane.showMessageDialog(null,"Fehler: Datenbankkontakt zum Update-Server konnte nicht hergestellt werden.");
	    		return;
	    	}
	    	return;
			
			
		}
		@Override
        public void run() {
			int i=0;
			StarteDB();
		}
	}
	
	/***************************************************************/
	public JXDialog getPwDialog(){
		final JXPanel pan = new JXPanel();
		dlg = new JXDialog(jFrame,pan);
		final JTextField field1 = new JTextField();
		final JPasswordField field2 = new JPasswordField();
		final JButton[] buts = {null,null};
		
		KeyListener kl = new KeyListener(){

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode()== KeyEvent.VK_ENTER || e.getKeyCode()== KeyEvent.VK_ESCAPE){
					e.consume();					
				}
			}
			@Override
			public void keyReleased(KeyEvent e) {
			}
			
		};
		FormLayout lay = new FormLayout("10dlu:g,125dlu,5dlu,125dlu,10dlu:g","15dlu:g,p,5dlu,p,15dlu,p,15dlu:g");
		CellConstraints cc = new CellConstraints();
		pan.setPreferredSize(new Dimension(420,150));
		pan.setLayout(lay);
		pan.add(new JLabel("Bitte Ihre Emailadresse eingeben:"),cc.xy(2,2));
		pan.add(field1,cc.xy(4,2));
		pan.add(new JLabel("Bitte Passwort eingeben:"),cc.xy(2,4));
		pan.add(field2,cc.xy(4,4));
		
		buts[0] = new JButton("senden");
		buts[0].addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				if(field1.getText().trim().equals("")){
					JOptionPane.showMessageDialog(null,"Emailadresse muß angegeben werden");
					return;
				}
				String pw = new String(field2.getPassword());
				if(pw.trim().equals("")){
					JOptionPane.showMessageDialog(null,"Passwort muß angegeben werden");
					field2.requestFocus();
					return;
				}
				boolean knownUser = sucheUser(field1.getText().trim(),pw.trim());
				System.out.println("Benutzer is bekannt: "+knownUser);
				if(!knownUser){
					JOptionPane.showMessageDialog(null,"Emailadresse und/oder Passwort sind nicht registriert,\noder passen nicht zusammen!");
					field1.requestFocus();
					return;
				}
				System.out.println("MacNotMatch = "+macNotMatch);
				if(macNotMatch){
					try {

						
						File fi = new File(PROGHOME+"ini/tpupdateneu.ini");
						if(fi.exists()){
							//System.out.println("Vor der Verschlüsselung Datei existiert = "+fi.getName());
							UpdateConfig.isrta = false;
						}else{
							//System.out.println("Vor der Verschlüsselung Datei existiert nicht");
						}
						//System.out.println("isrta="+isrta);
						
						/*
						Verschluesseln dec = Verschluesseln.getInstance();
						dec.init(Verschluesseln.getPassword().toCharArray(), dec.getSalt(), dec.getIterations());
						
						String decmac = dec.encrypt(strMACAdr.trim());
				        FileWriter f = new FileWriter (UpdateConfig.getProghome()+"Libraries/lib/ocf/sig.jar");
				        f.flush();
				        f.close();
				        INIFile inif = new INIFile(UpdateConfig.getProghome()+"Libraries/lib/ocf/sig.jar");
				        inif.setStringProperty("Updates", "1",decmac ,null);
				        inif.setStringProperty("Updates", "2",dec.encrypt(field1.getText()) ,null);
				        inif.setStringProperty("Updates", "3",dec.encrypt(pw) ,null);
				        inif.save();
				        */
				        /*
				        FileWriter f = new FileWriter (UpdateConfig.getProghome()+"Libraries/lib/ocf/sig.jar");
				        f.flush();
				        f.close();
				        */
				        
				        INIFile inif = new INIFile(PROGHOME+"Libraries/lib/ocf/sig.jar");
				        inif.setStringProperty("Updates", "1",strMACAdr.trim() ,null);
				        inif.setStringProperty("Updates", "2",field1.getText() ,null);
				        inif.setStringProperty("Updates", "3",pw ,null);
				        inif.save();
						
				        /*
				        f.write(decmac);
				        f.write(System.getProperty("line.separator"));
				        f.write(dec.encrypt(field1.getText()));
				        f.write(System.getProperty("line.separator"));
				        f.write(dec.encrypt(pw));
				        f.write(System.getProperty("line.separator"));
				        f.flush();
				        f.close();
				        */
					} catch(Exception ex){
						ex.printStackTrace();
					}
					
				}
				TheraPiUpdates.updateallowed = true;
				if(UpdateConfig.getInstance().isDeveloperMode()){
					updateTab.starteFTP();
				}else{
					updatePanel.starteFTP();
				}

				
				dlg.dispose();
			}
		});
		buts[1] = new JButton("abbrechen");
		buts[1].addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				if(TheraPiUpdates.starteTheraPi){
					int anfrage = JOptionPane.showConfirmDialog(null, "Wollen Sie Thera-Pi 1.0 jetzt starten?","Thera-Pi starten?",JOptionPane.YES_NO_OPTION);
					if(anfrage == JOptionPane.YES_OPTION){
						try {
							Runtime.getRuntime().exec("java -jar "+PROGHOME+"TheraPi.jar");
						} catch (IOException ex) {
							ex.printStackTrace();
						}
					}
					System.exit(0);
				}else{
					System.exit(0);
				}
			}
		});
		field1.addKeyListener(kl);
		field2.addKeyListener(kl);
		pan.add(buts[1],cc.xy(2,6));
		pan.add(buts[0],cc.xy(4,6));
		pan.validate();
		dlg.setPreferredSize(new Dimension(550,200));
		dlg.setTitle("Zugang zum Thera-Pi Update-Explorer");
		dlg.validate();
		dlg.pack();
		dlg.setLocationRelativeTo(jFrame);
		dlg.setVisible(true);
		field1.requestFocus();
		return dlg;
	}
	
	public boolean sucheUser(String email,String pw){
		boolean ret = false;
		//Verschluesseln man = Verschluesseln.getInstance();
		//man.init(Verschluesseln.getPassword().toCharArray(), man.getSalt(), man.getIterations());
		try{
			Vector<String> vec = SqlInfo.holeFeld(TheraPiUpdates.conn, "select id from regtpuser");
			//System.out.println(vec);
			//System.out.println("Email = "+email+" / Passwort = "+pw);
			userdaten = SqlInfo.holeSatz(TheraPiUpdates.conn, "regtpuser", " * ", "email='"+email+"' and pw='"+pw+"'");
			//System.out.println(vec);
			//System.out.println(userdaten);
			//System.out.println(userdaten);
			if(userdaten.size() > 0){
				return true;
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return ret;
	}
	
	/***************************************************************/
	
	public static String getMac(){
		InetAddress ip;
		try {
	 
			ip = InetAddress.getLocalHost();
			//System.out.println("Current IP address : " + ip.getHostAddress());
	 
			NetworkInterface network = NetworkInterface.getByInetAddress(ip);
			//System.out.println(network.getDisplayName());
			//System.out.println(network.getInterfaceAddresses());
			//System.out.println(NetworkInterface.getByName(network.getDisplayName()));
			byte[] mac = network.getHardwareAddress();
			//System.out.println("Länge = "+mac.length);
	 
			//System.out.print("Current MAC address : ");
	 
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < mac.length; i++) {
				sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));		
			}
			//System.out.println(sb.toString());
			return sb.toString();
		} catch (UnknownHostException e) {
	 
			e.printStackTrace();
	 
		} catch (SocketException e) {
			
			e.printStackTrace();
		}
		return "";
	}
	public static String getMacAddress() throws Exception {
		 
	    String result = "";
	    try {
	   	 for (NetworkInterface ni : Collections.list(NetworkInterface.getNetworkInterfaces()) ) {
	   			 byte[] hardwareAddress = ni.getHardwareAddress();
	   			 
	   			 if (hardwareAddress != null) {
	   				 for (int i = 0; i < hardwareAddress.length; i++) {
	   					 result += String.format((i == 0 ? "" : "") + "%02X", hardwareAddress[i]);
	   				 }
	   				 if (result.length() > 0 && !ni.isLoopback()) {
	   					
		   				 return result; 
		   			 }
	   			 }
	   	 }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    //System.out.println(result);  
	 return result;
	 }	
}
