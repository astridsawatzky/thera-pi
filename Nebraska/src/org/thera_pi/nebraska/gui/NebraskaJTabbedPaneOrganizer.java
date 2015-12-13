package org.thera_pi.nebraska.gui;

import java.awt.BorderLayout;
import java.io.File;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXHeader;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTitledPanel;


public class NebraskaJTabbedPaneOrganizer extends JXPanel implements ChangeListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6554044579478248774L;
	public JXTitledPanel jxTitel;
	public JTabbedPane jtb;
	public JXHeader jxh;
	private Vector<String> vectitel = new Vector<String>();
	private Vector<String> vecdescript = new Vector<String>();
	private Vector<ImageIcon> vecimg = new Vector<ImageIcon>();
	public NebraskaZertAntrag zertAntrag;
	public NebraskaZertExplorer zertExplorer;
	public NebraskaToolPanel toolPanel;
	public NebraskaJTabbedPaneOrganizer(){
		super();
		try{
		setOpaque(false);
		setLayout(new BorderLayout());
		jtb = new JTabbedPane();
		doHeader();
		
		zertExplorer = new NebraskaZertExplorer();
		jtb.addTab("Zertifikate auswerten / manuell Verschlüsseln", zertExplorer);
		
		zertAntrag = new NebraskaZertAntrag(this);
		jtb.addTab("Zertifikats-Antrag stellen",zertAntrag );
		
		toolPanel = new NebraskaToolPanel(this);
		jtb.addTab("Nebraska Tools",toolPanel );
		//jtb.addTab("Manuell verschlüsseln", new JXPanel());
		//jtb.addTab("Test- und Experimentierpanel", new NebraskaTestPanel());
		jtb.addChangeListener(this);
        jxh = new JXHeader();
        ((JLabel)jxh.getComponent(1)).setVerticalAlignment(JLabel.NORTH);
        add(jxh, BorderLayout.NORTH);
        add(jtb, BorderLayout.CENTER);
        
        jxh.validate();
        jtb.validate();
		validate();
		zertAntrag.setzeFocus();
		}catch(Exception ex){
			ex.printStackTrace();
		}


	}
	public NebraskaZertExplorer getZertExplorer(){
		return zertExplorer;
	}
	public void setHeader(int header){
        jxh.setTitle(vectitel.get(header));
        jxh.setDescription(vecdescript.get(header));
        jxh.setIcon(vecimg.get(header));
        jxh.validate();
	}
	@Override
	public void stateChanged(ChangeEvent arg0) {
		JTabbedPane pane = (JTabbedPane)arg0.getSource();
        int sel = pane.getSelectedIndex();
        jxh.setTitle(vectitel.get(sel));
        jxh.setDescription(vecdescript.get(sel));
        jxh.setIcon(vecimg.get(sel));   

	}
	public void erstTest(){
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				try{
					zertExplorer.erstTest();	
				}catch(Exception ex){
					ex.printStackTrace();
				}
				return null;
			}
		}.execute();
		
	}
	public void setAlgText(String text){
		int sel = jtb.getSelectedIndex();
		if(sel==0){
			jxh.setDescription(vecdescript.get(sel)+"\n\n"+text);
			jxh.validate();
		}
		
	}
	private void doHeader(){
		ImageIcon ico;
        String ss = System.getProperty("user.dir")+File.separator+"icons"+File.separator+"nebraska_scale.jpg";
        ico = new ImageIcon(ss);
        
		vectitel.add("Manuell verschlüsseln");
		vecdescript.add("....Hier können Sie Dateien manuell verschlüsseln\n" +
                "Weshalb auch immer....");
		vecimg.add(ico);

		vectitel.add("Antrag auf Zertifizierung bei der ITSG stellen");
		vecdescript.add("....Geben Sie hier bitte Ihre Stammdaten ein\n" +
				"Achtung: keine Umlaute und kein 'ß' verwenden, ansonsten wird Ihr Antrag von der ITSG abgelehnt!!!\n"+
                "Wenn die Angaben komplett sind können Sie den Antrag ausdrucken, unterzeichnen und anschließend per FAX\n" +
                "an die ITSG senden. (FAX-Nr. der ITSG finden Sie auf dem Antrag).\n\n"+
                "Wenn Sie die Schaltfläche 'Request-erzeugen' drücken, wird für Sie ein Schlüsselpaar sowie ein Zertifikatsrequest erzeugt den Sie dann per E-Mail an die ITSG versenden können.");
		vecimg.add(ico);
		
		
		vectitel.add("Werkzeuge für Ihren Keystore - wählen Sie zuerst auf der Seite Zertifikate auswerten einen Mandanten (IK) aus");
		vecdescript.add("....Wenn Sie eine oder mehrere Funktionen dieser Seite\n" +
                "ausführen wollen, müssen Sie schon sehr genau wissen was Sie tun !\n\n" +
                "Bevor Sie mit irgendwelchen Aktionen starten, fertigen Sie bitte zuerst eine Sicherungskopie\n"+
                "Ihes Keystore-Verzeichnisses an !");
		vecimg.add(ico);
		

		vectitel.add("Test- und Experimentierpanel");
		vecdescript.add("....Diese Seite ist bislang noch Bodo und Jürgen vorbehalten (leider).\n" +
                "Hier werden die Funktionen die später Nebraska zu dem machen was Nebraske ist\n"+
                "entwickelt und getestet");
		vecimg.add(ico);
	}

}
