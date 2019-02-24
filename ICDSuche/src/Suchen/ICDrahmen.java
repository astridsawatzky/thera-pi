package Suchen;

import java.awt.Dimension;
import java.sql.Connection;

import javax.swing.JFrame;

public class ICDrahmen implements Runnable{

	private JFrame jFrame;
	Connection conn;
	
//	public static void main(String[] args) throws SQLException {
//		private IK ik;
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
