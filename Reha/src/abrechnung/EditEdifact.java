package abrechnung;

import hauptFenster.Reha;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

import javax.swing.SwingWorker;
import org.jdesktop.swingx.JXDialog;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTitledPanel;

import CommonTools.JCompTools;
import CommonTools.SqlInfo;
import CommonTools.StringTools;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import dialoge.DragWin;
import dialoge.PinPanel;
import events.RehaTPEvent;
import events.RehaTPEventClass;
import events.RehaTPEventListener;

public class EditEdifact extends JXDialog implements FocusListener, ActionListener, MouseListener, KeyListener,RehaTPEventListener{

	private JXTitledPanel jtp = null;
	private MouseAdapter mymouse = null;
	private PinPanel pinPanel = null;
	private JXPanel content = null;
	private RehaTPEventClass rtp = null;
	
	private JButton[] but = {null,null};
	
	JTextArea area = null;
	
	String reznr = null;
	
	private static final long serialVersionUID = 1L;
	

	
	public EditEdifact(JXFrame owner, String titel,String reznr) {
		super(owner,(JComponent)Reha.thisFrame.getGlassPane());
		
		this.setUndecorated(true);
		
		this.reznr = reznr;
		
		this.setName("RezgebDlg");
		this.jtp = new JXTitledPanel();
		this.jtp.setName("RezgebDlg");
		this.mymouse = new DragWin(this);
		this.jtp.addMouseListener(mymouse);
		this.jtp.addMouseMotionListener(mymouse);
		this.jtp.setContentContainer(getContent());
		this.jtp.setTitleForeground(Color.WHITE);
		this.jtp.setTitle(titel);
		this.pinPanel = new PinPanel();
		this.pinPanel.getGruen().setVisible(false);
		this.pinPanel.setName("RezgebDlg");
		this.jtp.setRightDecoration(this.pinPanel);
		this.setContentPane(jtp);
		this.setModal(true);
		this.setResizable(false);
		this.rtp = new RehaTPEventClass();
		this.rtp.addRehaTPEventListener((RehaTPEventListener) this);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
	}

	/**
	 * 
	 */

	private JXPanel getContent(){
		content = new JXPanel(new BorderLayout());
		content.add(getEdifact(),BorderLayout.CENTER);
		content.add(getButtons(),BorderLayout.SOUTH);
		content.addKeyListener(this);
		return content;
	}

	private JXPanel getEdifact(){
		JXPanel pan = new JXPanel();
		pan.setOpaque(false);
		FormLayout lay = new FormLayout("5dlu,fill:0:grow(1.0),5dlu",
				//1          2          3   
				"5dlu,fill:0:grow(1.0),5dlu");
		
		pan.setLayout(lay);
		CellConstraints cc = new CellConstraints();
		
		area = new JTextArea();
		area.setFont(new Font("Courier",Font.PLAIN,11));
		area.setLineWrap(true);
		area.setName("notitzen");
		area.setWrapStyleWord(true);
		area.setEditable(true);
		area.setBackground(Color.WHITE);
		area.setForeground(Color.BLACK);
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				area.setText(SqlInfo.holeEinzelFeld("select edifact from fertige where rez_nr ='"+reznr+"' LIMIT 1"));
				return null;
			}
		}.execute();
		JScrollPane span = JCompTools.getTransparentScrollPane(area);
		span.validate();
		pan.add(span,cc.xy(2,2,CellConstraints.FILL,CellConstraints.FILL));
		
		return pan;
	}
	private JButton macheBut(String titel,String cmd){
		JButton but = new JButton(titel);
		but.setName(cmd);
		but.setActionCommand(cmd);
		but.addActionListener(this);
		return but;
	}	
	
	private JXPanel getButtons(){
		JXPanel pan = new JXPanel();
		pan.setOpaque(false);
		FormLayout lay = new FormLayout("5dlu,fill:0:grow(0.5),50dlu,10dlu,50dlu,fill:0:grow(0.5),5dlu",
				//1          2         3   4  5  6   7  8   9  10  11  12
				"5dlu,fill:0:grow(0.5),p,fill:0:grow(0.5),5dlu");
		pan.setLayout(lay);
		CellConstraints cc = new CellConstraints();
		pan.add((but[0] = macheBut("Ok","ok")),cc.xy(3,3));
		but[0].addKeyListener(this);
		pan.add((but[1] = macheBut("abbrechen","abbrechen")),cc.xy(5,3));
		but[1].addKeyListener(this);
		return pan;
	}
	
	@Override
	public void rehaTPEventOccurred(RehaTPEvent evt) {
		FensterSchliessen("dieses");
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
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
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if(cmd.equals("ok")){
			SqlInfo.sqlAusfuehren("update fertige set edifact='"+StringTools.Escaped(area.getText())+"' where rez_nr='"+reznr+"' LIMIT 1");
			FensterSchliessen("Dieses");
		}else if(cmd.equals("abbrechen")){
			FensterSchliessen("Dieses");
		}
		
	}

	@Override
	public void focusGained(FocusEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void focusLost(FocusEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	public void FensterSchliessen(String welches){
		this.jtp.removeMouseListener(this.mymouse);
		this.jtp.removeMouseMotionListener(this.mymouse);
		this.content.removeKeyListener(this);
		for(int i = 0; i < 2;i++){
			but[i].removeActionListener(this);
			but[i].removeKeyListener(this);
			but[i] = null;
		}
		this.mymouse = null; 
		if(this.rtp != null){
			this.rtp.removeRehaTPEventListener((RehaTPEventListener) this);
			this.rtp=null;			
		}
		this.pinPanel = null;
		setVisible(false);
		this.dispose();
	}	

}
