package systemEinstellungen;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.jdesktop.swingx.JXPanel;

import hauptFenster.Reha;

public class SysUtilVorlage extends JXPanel implements KeyListener, ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SysUtilVorlage(ImageIcon img){
		super(new BorderLayout());
		this.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 20));
		/****/
		setBackgroundPainter(Reha.thisClass.compoundPainter.get("SystemInit"));
		/****/
	     JLabel jlbl = new JLabel("");
	     jlbl.setIcon(img);
	     add(jlbl,BorderLayout.CENTER);
	     //add(getVorlagenSeite());
		return;
	}
	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}

}
