package dialoge;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;

import org.jdesktop.swingx.JXDialog;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTitledPanel;
import org.therapi.reha.patient.PatientToolBarLogic;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import CommonTools.JCompTools;
import CommonTools.JRtaTextField;
import events.RehaTPEvent;
import events.RehaTPEventClass;
import events.RehaTPEventListener;
import hauptFenster.Reha;
import socketClients.SMSClient;
import systemEinstellungen.SystemConfig;
import systemTools.ButtonTools;

public class SMSDialog extends JXDialog implements  WindowListener, KeyListener,RehaTPEventListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6736705768583236169L;
	ActionListener al = null;
	private RehaTPEventClass rtp = null;
	private JXTitledPanel jtp = null;
	private MouseAdapter mymouse = null;
	private PinPanel pinPanel = null;
	private JXPanel content = null;
	private String titel;
	PatientToolBarLogic eltern = null;
	JButton[] buts = {null,null};
	JRtaTextField tf = null;
	boolean isSMS = false;
	private JTextArea jta;
	private String info = null;
	private String nummer = "";
	 
	public SMSDialog(JXFrame owner,String titel,PatientToolBarLogic xeltern,boolean SMS, String info,String nummer){
		super(owner, (JComponent)Reha.thisFrame.getGlassPane());
		installListener();

		this.setUndecorated(true);
		this.setName("SMSDlg");	
		this.titel = titel;
		this.eltern = xeltern;
		this.isSMS = SMS;
		this.info = info;
		this.nummer = nummer;
		this.jtp = new JXTitledPanel();
		this.jtp.setName("SMSDlg");
		this.mymouse = new DragWin(this);
		this.jtp.addMouseListener(mymouse);
		this.jtp.addMouseMotionListener(mymouse);
		this.jtp.addKeyListener(this);
		this.jtp.setContentContainer(getContent());
		this.jtp.setTitleForeground(Color.WHITE);
		this.jtp.setTitle( (isSMS ? makeTitel(0) : "<html>"+this.titel+"</html>"));
		this.pinPanel = new PinPanel();
		this.pinPanel.getGruen().setVisible(false);
		this.pinPanel.setName("SMSDlg");
		this.jtp.setRightDecoration(this.pinPanel);
		this.setContentPane(jtp);

		
		this.setModal(true);
		this.setResizable(false);
		this.rtp = new RehaTPEventClass();
		this.rtp.addRehaTPEventListener((RehaTPEventListener) this);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		

		
		/*
		try {
			Enumeration<NetworkInterface> netInter = NetworkInterface.getNetworkInterfaces();
			int n = 0;

			while ( netInter.hasMoreElements() )
			{
			  NetworkInterface ni = netInter.nextElement();
			  if(ni != null){
				  System.out.println( "NetworkInterface " + n++ + ": " + ni.getDisplayName() );

				  for ( InetAddress iaddress : Collections.list(ni.getInetAddresses()) )
				  {
				    System.out.println( "CanonicalHostName: " +
				                         iaddress.getCanonicalHostName() );

				    System.out.println( "IP: " + iaddress.getHostAddress() );

				    System.out.println( "Loopback? " + iaddress.isLoopbackAddress() );
				    System.out.println( "SiteLocal? " + iaddress.isSiteLocalAddress() );
				    System.out.println( "SiteRechable? " +InetAddress.getByName( iaddress.getHostAddress() ).isReachable( 1000 ));
				    System.out.println();
				     
				  }
			  }
			}

		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/

	}
	public void setTextCursor(final int pos){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				jta.requestFocus();
				jta.setCaretPosition(pos);		
			}
		});
		
	}
	public int getTextCursor(){
		return jta.getCaretPosition();
	}
	
	private JXPanel getContent(){
		//                  1          2          3    4      5     6               7 
		String xwerte = "5dlu,fill:0:grow(0.5),125dlu,5dlu,120dlu,fill:0:grow(0.5),5dlu";
		//                 1           2  3   4  5  6   7   8     9           10
		String ywerte = "5dlu,p,5dlu,p,5dlu,p,5dlu,p,fill:0:grow(1.0),p,5dlu";
		FormLayout lay = new FormLayout(xwerte,ywerte);
		CellConstraints cc = new CellConstraints();
		content = new JXPanel();
		content.setBackground(Color.LIGHT_GRAY);
		content.setLayout(lay);
		content.addKeyListener(this);
		tf = new JRtaTextField("normal",false);
		tf.setText(this.info);
		tf.setEditable(false);
		content.add(tf,cc.xyw(2,2,5));
		jta = new JTextArea();
		jta.setFont(new Font("Courier",Font.PLAIN,14));
		jta.setLineWrap(true);
		jta.setName("notitzen");
		jta.setWrapStyleWord(true);
		jta.setEditable(true);
		jta.setBackground(Color.WHITE);
		jta.setForeground(Color.BLUE);
		jta.addKeyListener(this);
		JScrollPane span = JCompTools.getTransparentScrollPane(jta);
		span.validate();
		content.add(span,cc.xywh(2,4,5,6));
		//span.setBackground(Color.WHITE);
		FormLayout lay2 = new FormLayout("fill:0:grow(0.50),60dlu,20dlu,60dlu,fill:0:grow(0.50)",
				"10dlu,p,10dlu");
		CellConstraints cc2 = new CellConstraints();
		JXPanel jpan = new JXPanel();
		jpan.setLayout(lay2);
		buts[0] = ButtonTools.macheButton("SMS senden", "senden",al );
		buts[0].setMnemonic('S');
		jpan.add(buts[0],cc2.xy(2, 2));
		
		buts[1] = ButtonTools.macheButton("abbrechen", "abbrechen",al );
		jpan.add(buts[1],cc2.xy(4, 2));
		
		content.add(jpan,cc.xyw(2,10,5));
		
		content.validate();
		return content;		
	
	}
	private String makeTitel(int anzahl){
		return "<html>"+this.titel+" - "+"<b>"+Integer.toString(anzahl)+" von 160</b> Zeichen"+"</html";
	}
	
	private void installListener(){
		al = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String cmd = arg0.getActionCommand();
				if(cmd.equals("senden")){
					doSenden();
					return;
				}
				if(cmd.equals("abbrechen")){
					FensterSchliessen("dieses");
					return;
				}
				
			}
			
		};
	}
	private void doSenden(){
		if(jta.getText().trim().equals("")){
			JOptionPane.showMessageDialog(null,"Kein Text für SMS-Nachricht eingegeben");
			return;
		}
		String b = ":++:";
		
		
		final StringBuffer buf = new StringBuffer();
		Reha.lastCommId = Long.toString(System.currentTimeMillis());
		Reha.lastCommAction = "SEND-SMS:TO:"+this.nummer+":"+jta.getText(); 
		buf.append(Reha.lastCommId);
		buf.append(b);
		buf.append(SystemConfig.dieseCallbackIP);
		buf.append(b);
		buf.append("SEND-SMS");
		buf.append(b);
		buf.append(this.nummer);
		buf.append(b);
		buf.append(jta.getText());
		//System.out.println("Setze SMS ab mit "+smscomplete);
		try{
			new SwingWorker<Void,Void>(){
				@Override
				protected Void doInBackground() throws Exception {
					try{
						new SMSClient().setzeNachricht(buf.toString());
					}catch(Exception ex){
						ex.printStackTrace();
						JOptionPane.showMessageDialog(null,"<html><b>Kontakt mit "+SystemConfig.hmSMS.get("NAME")+" konnte nicht aufgebaut werden</b></html>");
						FensterSchliessen("dieses");
						
					}
					
					FensterSchliessen("dieses");
					return null;
				}
			}.execute();
			FensterSchliessen("dieses");
		}catch(Exception ex){
			FensterSchliessen("dieses");
			JOptionPane.showMessageDialog(null,"<html><b>Äußere Schleife: Kontakt mit "+SystemConfig.hmSMS.get("NAME")+" konnte nicht aufgebaut werden</b></html>");
		}
	}
	
	public void FensterSchliessen(String welches){
		
		this.jtp.removeMouseListener(this.mymouse);
		this.jtp.removeMouseMotionListener(this.mymouse);
		this.mymouse = null;
		 
		if(this.rtp != null){
			this.rtp.removeRehaTPEventListener((RehaTPEventListener) this);
			this.rtp=null;			
		}
		if(jta != null){
			jta.removeKeyListener(this);
		}
		setVisible(false);
		this.dispose();
	}

	@Override
	public void rehaTPEventOccurred(RehaTPEvent evt) {
		FensterSchliessen("dieses");
	}

	@Override
	public void keyTyped(KeyEvent e) {
		if(isSMS){
			if(jta.getText().length() > 160){
				jta.setText(jta.getText().substring(0,160));
				JOptionPane.showMessageDialog(null, "<html><b>Länge einer SMS ist auf 160 Zeichen begrenzt</b></html>");
				e.consume();
			}
			this.jtp.setTitle(this.makeTitel(jta.getText().length()));
		}
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
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

}
