package arztBaustein;



import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXFrame;

import com.sun.star.uno.Exception;

import CommonTools.INIFile;
import CommonTools.SqlInfo;
import CommonTools.StartOOApplication;
import CommonTools.Verschluesseln;
import ag.ion.bion.officelayer.application.IOfficeApplication;
import ag.ion.bion.officelayer.application.OfficeApplicationException;

public class ArztBaustein implements WindowListener, WindowStateListener {

	/**
	 * @param args
	 */
	private static String OpenOfficePfad = "C:/Program Files (x86)/OpenOffice.org 3";
	static IOfficeApplication officeapplication = null;
	static String OpenOfficeNativePfad = "C:/RehaVerwaltung/Libraries/lib/openofficeorg";
	
	Connection conn = null;
	static boolean DbOk = false;
	static ArztBaustein thisClass = null;

	JXFrame jFrame = null;

	private static String dbIpAndName = "jdbc:mysql://192.168.2.2:3306/dbf";
	private static String dbUser = "entwickler";
	private static String dbPassword = "entwickler";
	
	private ArztBausteinPanel arztbausteinpanel = null;
	private SqlInfo sqlInfo;
	public static void main(String[] args) {
	
		if(args.length > 0){
			System.out.println("hole daten aus INI-Datei "+args[0]);
			INIFile ini = new INIFile(args[0]+"ini/"+args[1]+"/rehajava.ini");
			dbIpAndName = ini.getStringProperty("DatenBank","DBKontakt1");
			dbUser = ini.getStringProperty("DatenBank","DBBenutzer1");
			String pw = ini.getStringProperty("DatenBank","DBPasswort1");
			String decrypted = null;
			if(pw != null){
				Verschluesseln man = Verschluesseln.getInstance();
				decrypted = man.decrypt (pw);
			}else{
				decrypted = new String("");
			}
			dbPassword = decrypted.toString();
			OpenOfficePfad = ini.getStringProperty("OpenOffice.org","OfficePfad");
			OpenOfficeNativePfad = ini.getStringProperty("OpenOffice.org","OfficeNativePfad");
		}
		
		starteOfficeApplication();
		ArztBaustein arztbaustein = new ArztBaustein();
		arztbaustein.sqlInfo = new SqlInfo();
		arztbaustein.getJFrame(args);

	}
	
	public JXFrame getJFrame(String[] args){
		if (jFrame == null) {
			jFrame = new JXFrame();
			sqlInfo.setFrame(jFrame);
			thisClass = this;
			
			new SwingWorker<Void,Void>(){
				@Override
				protected Void doInBackground() throws Exception {
					try {
						starteDB();
					} catch (InstantiationException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
					return null;
				}
				
			}.execute();

			try {
				UIManager.setLookAndFeel("com.jgoodies.looks.plastic.PlasticXPLookAndFeel");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (UnsupportedLookAndFeelException e) {
				e.printStackTrace();
			}
			

			jFrame.addWindowListener(this);
			jFrame.addWindowStateListener(this);
			jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			jFrame.setPreferredSize(new Dimension(1024,800));

			jFrame.setTitle("Bausteine für ärztlichen Entlassbericht anlegen / ändern");
			

			jFrame.getContentPane().setPreferredSize(new Dimension(1024,800));
			jFrame.getContentPane().setLayout(new GridLayout());
			jFrame.getContentPane().add ( (arztbausteinpanel=new ArztBausteinPanel()));

			jFrame.setVisible(true);

			jFrame.pack();
						
		}	
		return jFrame;
	}

	
	
	@Override
	public void windowActivated(WindowEvent arg0) {
	}
	@Override
	public void windowClosed(WindowEvent arg0) {
		if(arztbausteinpanel.document != null){
			arztbausteinpanel.document.close();
			arztbausteinpanel.document = null;
		}
		if(conn != null){
			try {
				conn.close();
				System.out.println("Datenbankverbindung wurde geschlossen-2");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		System.exit(0);
		
	}
	@Override
	public void windowClosing(WindowEvent arg0) {
		if(arztbausteinpanel.document != null){
			arztbausteinpanel.document.close();
			arztbausteinpanel.document = null;
			System.out.println("Dokument wurde geschlossen");
		}
		if(conn != null){
			try {
				conn.close();
				conn = null;
				System.out.println("Datenbankverbindung wurde geschlossen");
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
	@Override
	public void windowStateChanged(WindowEvent arg0) {
	}
	
    public static void starteOfficeApplication()
    { 
    	try {
			officeapplication = new StartOOApplication(ArztBaustein.OpenOfficePfad,ArztBaustein.OpenOfficeNativePfad).start(false);
			 System.out.println("OpenOffice ist gestartet und Active ="+officeapplication.isActive());
		} catch (OfficeApplicationException e1) {
			e1.printStackTrace();
		}
	
    }
	
	private void starteDB() throws InstantiationException, IllegalAccessException, ClassNotFoundException{

			if (conn != null){
				try{
				conn.close();}
				catch(final SQLException e){}
			}
			
			Class.forName("com.mysql.jdbc.Driver").newInstance();


        	try {
   				conn = DriverManager.getConnection(dbIpAndName,dbUser,dbPassword);
   				sqlInfo.setConnection(conn);
    			DbOk = true;
    			System.out.println("Datenbankkontakt hergestellt");
        	} 
        	catch (final SQLException ex) {
        		System.out.println("SQLException: " + ex.getMessage());
        		System.out.println("SQLState: " + ex.getSQLState());
        		System.out.println("VendorError: " + ex.getErrorCode());
        		DbOk = false;
		        
        	}
        	return;
	}		


}
