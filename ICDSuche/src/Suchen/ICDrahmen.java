package Suchen;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.jdesktop.swingx.JXPanel;



public class ICDrahmen extends JApplet implements WindowListener{

	public static ICDrahmen thisClass;
	public static boolean DbOk;
	JFrame jFrame;
	public Connection conn;
	
	private JXPanel grund;
	   

	public static void main(String[] args) {
		
		ICDrahmen frm = new ICDrahmen();
		frm.getJFrame();
		

	}
	
	public ICDrahmen(){
		thisClass = this;
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
		DatenbankStarten dbstart = new DatenbankStarten();
		dbstart.run();

		grund = new JXPanel(new BorderLayout());
		JScrollPane jscr = JCompTools.getTransparentScrollPane(new ICDoberflaeche());
		//test = new ICDoberflaeche();
		jscr.validate();
		grund.add(jscr);
		getContentPane().add(grund);
	}
	  @Override
    public void start()
	   {
	      super.start();

	   }
	  @Override
    public void stop()
	   {
	      super.stop();
	  	if(ICDrahmen.thisClass.conn != null){
			try {
				ICDrahmen.thisClass.conn.close();
				System.out.println("Datenbankverbindung wurde geschlossen");
			} catch (SQLException e) {
				
				e.printStackTrace();
			}
		}
		System.exit(0);	      

	   }

	
	public JFrame getJFrame(){
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
		//thisClass = this;
		
		jFrame = new JFrame();
		jFrame.setSize(650,600);
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jFrame.setPreferredSize(new Dimension(650,600));
		jFrame.setTitle("ICD-Suche 2010");
		jFrame.setLocationRelativeTo(null);
		jFrame.addWindowListener(this);
		jFrame.setContentPane(new ICDoberflaeche());// .add (jpan);
		thisClass = this;
		DatenbankStarten dbstart = new DatenbankStarten();
		dbstart.run();
		jFrame.pack();
		jFrame.setVisible(true);
		return jFrame;
	}
	final class DatenbankStarten implements Runnable{
		private void StarteDB(){
			final ICDrahmen obj = ICDrahmen.thisClass;

			final String sDB = "SQL";
			if (obj.conn != null){
				try{
				obj.conn.close();}
				catch(final SQLException e){}
			}
			try{
					Class.forName("com.mysql.jdbc.Driver").newInstance();

	    	}
	    	catch ( final Exception e ){
	        		System.out.println(sDB+"Treiberfehler: " + e.getMessage());
	        		ICDrahmen.DbOk = false;
		    		return ;
	        }	
		        	try {
	    				obj.conn = DriverManager.getConnection("jdbc:mysql://192.168.2.2:3306/dbf","entwickler","entwickler");
	    				ICDrahmen.DbOk = true;
		    			System.out.println("Datenbankkontakt hergestellt");
		        	} 
		        	catch (final SQLException ex) {
		        		System.out.println("SQLException-: " + ex.getMessage());
		        		System.out.println("SQLState-: " + ex.getSQLState());
		        		System.out.println("VendorError-: " + ex.getErrorCode());
		        		ICDrahmen.DbOk = false;
		        
		        	}
		        return;
		}
		@Override
        public void run() {
			StarteDB();
		}
	
	
	}
	@Override
	public void windowActivated(WindowEvent arg0) {
		
		
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		if(ICDrahmen.thisClass.conn != null){
			try {
				ICDrahmen.thisClass.conn.close();
				System.out.println("Datenbankverbindung wurde geschlossen");
			} catch (SQLException e) {
				
				e.printStackTrace();
			}
		}
		System.exit(0);
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		if(ICDrahmen.thisClass.conn != null){
			try {
				ICDrahmen.thisClass.conn.close();
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
}
