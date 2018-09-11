package bmirechner;

import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.Connection;
import java.sql.SQLException;

import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;




public class BMIrahmen extends JApplet implements WindowListener{

	public static BMIrahmen thisClass;
	public static boolean DbOk;
	JFrame jFrame;
	public Connection conn;




	public static void main(String[] args) {

		BMIrahmen frm = new BMIrahmen();
		frm.getJFrame();


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

		jFrame = new JFrame();
		jFrame.setSize(350,450);
		jFrame.setPreferredSize(new Dimension(350,450));
		jFrame.setTitle("BMI-Rechner");
		jFrame.setLocationRelativeTo(null);
		jFrame.setContentPane(new BMIoberflaeche());// .add (jpan);
		thisClass = this;

		jFrame.pack();
		jFrame.setVisible(true);
		return jFrame;
	}



	@Override
	public void windowActivated(WindowEvent arg0) {
		

	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		if(BMIrahmen.thisClass.conn != null){
			try {
				BMIrahmen.thisClass.conn.close();
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
	public void windowClosing(WindowEvent arg0) {
		

	}
}

