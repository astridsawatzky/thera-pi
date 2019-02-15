package org.therapi.reha.patient;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.painter.MattePainter;
import org.therapi.reha.patient.TherapieBerichte.MyBerichtTableModel;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import CommonTools.JCompTools;
import CommonTools.JRtaTextField;
import dialoge.PinPanel;
import dialoge.RehaSmartDialog;
import events.RehaTPEvent;
import events.RehaTPEventClass;
import hauptFenster.Reha;
import systemTools.ListenerTools;

public class VorBerichte extends RehaSmartDialog implements ActionListener{
	/**
	 *
	 */
	private static final long serialVersionUID = -3970089431666417693L;
	boolean nurkopie;
	boolean aushistorie;
	public JButton uebernahme;
	public JButton abbrechen;
	public JRtaTextField gegeben;
	public JLabel rueckgeld;
	public JCheckBox diagnoseuebernahme;
	private RehaTPEventClass rtp = null;
	private VorBerichtHintergrund rgb;
	private ArztBericht clazz;

	public JXTable tabbericht = null;
	public MyBerichtTableModel dtblm;
	MattePainter mp = null;
	LinearGradientPaint p = null;


	public VorBerichte(boolean kopie,boolean historie,Point pt,ArztBericht xclazz){
		super(null,"VorberichtLaden");

		this.nurkopie = kopie;
		this.aushistorie = historie;
		this.clazz = xclazz;

		PinPanel pinPanel = new PinPanel();
		pinPanel.setName("VorberichtLaden");
		pinPanel.getGruen().setVisible(false);
		setPinPanel(pinPanel);
		getSmartTitledPanel().setTitle("Text aus Vorbericht laden");
		setSize(750,290);
		setPreferredSize(new Dimension(750,290));
		getSmartTitledPanel().setPreferredSize(new Dimension (750,290));
		setPinPanel(pinPanel);
		rgb = new VorBerichtHintergrund();
		rgb.setLayout(new BorderLayout());


		new SwingWorker<Void,Void>(){

			@Override
			protected Void doInBackground() throws Exception {
			     rgb.setBackgroundPainter(Reha.instance.compoundPainter.get("VorBerichte"));
				return null;
			}

		}.execute();
		rgb.add(getGebuehren(),BorderLayout.CENTER);

		getSmartTitledPanel().setContentContainer(rgb);
		getSmartTitledPanel().getContentContainer().setName("VorberichtLaden");
	    setName("VorberichtLaden");
		setModal(true);
	    //Point lpt = new Point(pt.x-125,pt.y+30);
	    setLocation(pt);

		rtp = new RehaTPEventClass();
		rtp.addRehaTPEventListener(this);

		pack();

		SwingUtilities.invokeLater(new Runnable(){
		 	   @Override
            public  void run()
		 	   {
		 		 setzeFocus();
		 	   }
		});




	}

	private void setzeFocus(){
		SwingUtilities.invokeLater(new Runnable(){
		 	   @Override
            public  void run()
		 	   {
		 			//gegeben.requestFocus();
		 	   }
		});
	}
/****************************************************/


	private JPanel getGebuehren(){     // 1      2    3     4       5               6                7
		FormLayout lay = new FormLayout("10dlu,80dlu,10dlu,80dlu,fill:0:grow(1.00),10dlu",
									//     1   2  3    4       5   6  7
										"15dlu,p,2dlu,100dlu,15dlu,p,15dlu");
		PanelBuilder pb = new PanelBuilder(lay);
		CellConstraints cc = new CellConstraints();

		pb.getPanel().setOpaque(false);

		diagnoseuebernahme = new JCheckBox("Diagnose ebenfalls übernehmen");
		diagnoseuebernahme.setOpaque(false);
		diagnoseuebernahme.setSelected(false);


		pb.add(diagnoseuebernahme,cc.xyw(2,2,3));

		dtblm = Reha.instance.patpanel.berichte.dtblm;
		tabbericht = new JXTable(dtblm);
		tabbericht.getColumn(1).setMinWidth(200);
		tabbericht.getColumn(6).setMinWidth(0);
		tabbericht.getColumn(6).setMaxWidth(0);
		tabbericht.setRowSelectionInterval(0, 0);
		tabbericht.addMouseListener(new MouseAdapter(){
			@Override
            public void mouseClicked(MouseEvent arg0) {
				if(arg0.getClickCount()==2){
					doUebernahme();
				}
			}
		});
		tabbericht.addKeyListener(new KeyAdapter(){
			@Override
            public void keyPressed(KeyEvent arg0) {
				if(arg0.getKeyCode()==KeyEvent.VK_ENTER){
					doUebernahme();
				}
			}
		});


		JScrollPane jscr = JCompTools.getTransparentScrollPane(tabbericht);
		jscr.validate();
		pb.add(jscr,cc.xyw(2,4,4));

		uebernahme = new JButton("Text übernehmen");
		uebernahme.setActionCommand("uebernahme");
		uebernahme.addActionListener(this);
		uebernahme.addKeyListener(this);
		pb.add(uebernahme,cc.xy(2,6));

		abbrechen = new JButton("abbrechen");
		abbrechen.setActionCommand("abbrechen");
		abbrechen.addActionListener(this);
		abbrechen.addKeyListener(this);
		pb.add(abbrechen,cc.xy(4,6));

		pb.getPanel().validate();
		return pb.getPanel();
	}
/****************************************************/

	@Override
    public void rehaTPEventOccurred(RehaTPEvent evt) {
		
		try{
			if(evt.getDetails()[0] != null){
				if(evt.getDetails()[0].equals(this.getName())){
					this.setVisible(false);
					ListenerTools.removeListeners(tabbericht);
					ListenerTools.removeListeners(uebernahme);
					ListenerTools.removeListeners(abbrechen);
					rtp.removeRehaTPEventListener(this);
					rtp = null;
					super.dispose();
					this.dispose();
					//System.out.println("****************Rezeptgebühren -> Listener entfernt**************");
				}
			}
		}catch(NullPointerException ne){
			//System.out.println("In PatNeuanlage" +evt);
		}
	}
	@Override
    public void windowClosed(WindowEvent arg0) {
		
		if(rtp != null){
			this.setVisible(false);
			rtp.removeRehaTPEventListener(this);
			rtp = null;
			ListenerTools.removeListeners(tabbericht);
			ListenerTools.removeListeners(uebernahme);
			ListenerTools.removeListeners(abbrechen);
			super.dispose();
			dispose();
			//System.out.println("****************Rezeptgebühren -> Listener entfernt (Closed)**********");
		}


	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		
		if(arg0.getActionCommand().equals("uebernahme")){
			doUebernahme();
		}
		if(arg0.getActionCommand().equals("abbrechen")){
			this.dispose();
		}

	}

	@Override
    public void keyPressed(KeyEvent event) {
		if(event.getKeyCode()==10){
			event.consume();
			if( ((JComponent)event.getSource()).getName().equals("uebernahme")){
				doUebernahme();
			}
			if( ((JComponent)event.getSource()).getName().equals("abbrechen")){
				this.dispose();
			}

			//System.out.println("Return Gedrückt");
		}
	}
	public void doUebernahme(){
		int row = tabbericht.getSelectedRow();
		int altid = -1;
		if(row >=0){
			altid = Integer.valueOf((String)tabbericht.getValueAt(row,0));
			clazz.vorberichtid = altid;
			clazz.vorberichtdiagnose = diagnoseuebernahme.isSelected();
		}
		this.dispose();
	}

}
class VorBerichtHintergrund extends JXPanel{
	/**
	 *
	 */
	private static final long serialVersionUID = 1049788497941853572L;
	ImageIcon hgicon;
	int icx,icy;
	AlphaComposite xac1 = null;
	AlphaComposite xac2 = null;
	public VorBerichtHintergrund(){
		super();
		hgicon = null;
		xac1 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.15f);
		xac2 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1.0f);

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
