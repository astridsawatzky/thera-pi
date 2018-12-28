package hauptFenster;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.ImageIcon;
import javax.swing.JDesktopPane;

/*******************/
class Hintergrund extends JDesktopPane {

	private static final long serialVersionUID = 1L;
	ImageIcon hgicon;
	int icx,icy;
	AlphaComposite xac1 = null;
	AlphaComposite xac2 = null;
	public Hintergrund(ImageIcon icon){
		super();
		hgicon = icon;
		if(hgicon != null){

			icx = hgicon.getIconWidth()/2;
			icy = hgicon.getIconHeight()/2;
			xac1 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.15f);
			xac2 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1.0f);
		}
		this.setDoubleBuffered(true);
	}

	@Override
	public void paintComponent( Graphics g ) {
		super.paintComponent( g );
		Graphics2D g2d = (Graphics2D)g;

		if(hgicon != null){
			g2d.setComposite(this.xac1);
			g2d.drawImage(hgicon.getImage(), (getWidth()/2)-icx , (getHeight()/2)-icy,null);
			g2d.setComposite(this.xac2);
		}
	}

}
