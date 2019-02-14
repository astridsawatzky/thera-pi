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

import org.jdesktop.swingx.JXFrame;

import CommonTools.INIFile;
import CommonTools.StartOOApplication;
import CommonTools.Verschluesseln;
import ag.ion.bion.officelayer.application.IOfficeApplication;
import ag.ion.bion.officelayer.application.OfficeApplicationException;
import logging.Logging;
import mandant.IK;
import sql.Datenquelle;

public class ArztBaustein implements WindowListener, WindowStateListener {

	

	static IOfficeApplication officeapplication = null;
	Connection conn = null;
	//static ArztBaustein thisClass = null;

	JXFrame jFrame = null;



	private ArztBausteinPanel arztbausteinpanel = null;

    public ArztBaustein(Connection connection) {
        conn=connection;
    }

    public static void main(String[] args) throws SQLException {
        new Logging("arztbaustein");
        String proghome = args[0];
        IK  ik = new IK(args[1]);
          String dbIpAndName = "jdbc:mysql://192.168.2.2:3306/dbf";
          String dbUser = "entwickler";
          String dbPassword = "entwickler";
        
        String OpenOfficePfad = "C:/Program Files (x86)/OpenOffice.org 3";
        String OpenOfficeNativePfad = "C:/RehaVerwaltung/Libraries/lib/openofficeorg";
   
        if (args.length > 0) {

            System.out.println("hole daten aus INI-Datei " + proghome);
            INIFile ini = new INIFile(proghome + "ini/" +ik.asString() + "/rehajava.ini");
            dbIpAndName = ini.getStringProperty("DatenBank", "DBKontakt1");
            dbUser = ini.getStringProperty("DatenBank", "DBBenutzer1");
            String pw = ini.getStringProperty("DatenBank", "DBPasswort1");

            if (pw == null) {
                dbPassword = new String("");
            } else {
                Verschluesseln man = Verschluesseln.getInstance();
                dbPassword = man.decrypt(pw);
            }
            OpenOfficePfad = ini.getStringProperty("OpenOffice.org", "OfficePfad");
            OpenOfficeNativePfad = ini.getStringProperty("OpenOffice.org", "OfficeNativePfad");
        }

       officeapplication= starteOfficeApplication(OpenOfficePfad, OpenOfficeNativePfad);
       
       Datenquelle dq = new Datenquelle(ik);
      
        Connection connection = dq.connection();
        ArztBaustein arztbaustein = new ArztBaustein(connection);
        arztbaustein.getJFrame(dbIpAndName,dbUser,dbPassword);

    }

	public JXFrame getJFrame(String dbIpAndName,String dbUser,String dbPassword){
		if (jFrame == null) {
			jFrame = new JXFrame();



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
			jFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			jFrame.setPreferredSize(new Dimension(1024,800));

			jFrame.setTitle("Bausteine für ärztlichen Entlassbericht anlegen / ändern");


			jFrame.getContentPane().setPreferredSize(new Dimension(1024,800));
			jFrame.getContentPane().setLayout(new GridLayout());
			jFrame.getContentPane().add ( arztbausteinpanel=new ArztBausteinPanel(this));

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
		

	}
	@Override
	public void windowClosing(WindowEvent arg0) {
		if(arztbausteinpanel.document != null){
			arztbausteinpanel.document.close();
			arztbausteinpanel.document = null;
			System.out.println("Dokument wurde geschlossen");
		}
		
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

    public static IOfficeApplication starteOfficeApplication(String ooPath, String ooNativePath)
    {
    	IOfficeApplication application =null;
        try {
			 application  = new StartOOApplication(ooPath,ooNativePath).start();
			 System.out.println("OpenOffice ist gestartet und Active ="+application.isActive());
		} catch (OfficeApplicationException e1) {
			e1.printStackTrace();
		}
    	return application;

    }

	private void starteDB(String dbIpAndName,String dbUser, String dbPassword) throws InstantiationException, IllegalAccessException, ClassNotFoundException{

			if (conn != null){
				try{
				conn.close();}
				catch(final SQLException e){}
			}

			Class.forName("com.mysql.jdbc.Driver").newInstance();


        	try {
   				conn = DriverManager.getConnection(dbIpAndName,dbUser,dbPassword);
   				//sqlInfo.setConnection(conn);
    			System.out.println("Datenbankkontakt hergestellt");
        	}
        	catch (final SQLException ex) {
        		System.out.println("SQLException: " + ex.getMessage());
        		System.out.println("SQLState: " + ex.getSQLState());
        		System.out.println("VendorError: " + ex.getErrorCode());

        	}
        	return;
	}


}
