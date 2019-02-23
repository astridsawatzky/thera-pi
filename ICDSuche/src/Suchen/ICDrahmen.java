package Suchen;

import java.awt.Dimension;
import java.sql.Connection;
import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import logging.Logging;
import mandant.IK;
import sql.Datenquelle;



public class ICDrahmen implements Runnable{



	private JFrame jFrame;
	Connection conn;

	private IK ik;
	
//	public static void main(String[] args) throws SQLException {
//		new Logging("icd");
//		setLaF();
//
//		IK ik = new IK("123456789");
//
//		Connection conn = new Datenquelle(ik).connection();
//		System.out.println(conn.isClosed());
//		ICDrahmen icd = new ICDrahmen(conn);
//		icd.getJFrame();
//
//	}


	public ICDrahmen(Connection conn) {
		this.conn = conn;

	}
	@Override
	public void run() {
		getJFrame();
		
	}
	private static void setLaF() {
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
	}


	public JFrame getJFrame(){
		
		jFrame = new JFrame();
		jFrame.setSize(650,600);
		jFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		jFrame.setPreferredSize(new Dimension(650,600));
		jFrame.setTitle("ICD-Suche 2010");
		jFrame.setLocationRelativeTo(null);
		jFrame.setContentPane(new ICDoberflaeche(new SqlInfo(conn)));// .add (jpan);
		jFrame.pack();
		jFrame.setVisible(true);
		return jFrame;
	}


	


}
