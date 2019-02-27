package Suchen;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Optional;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;


class ICDoberflaeche extends JXPanel  {



	private final JComboBox<SearchType> suchNachCombobox = new JComboBox<SearchType>(SearchType.values());
	private final JComboBox<Integer> limitCombobox = new JComboBox<Integer>(new Integer[] { 0,1,5,10,20,30,40,50});
	private JButton jBSuchen;
	private JTextArea jtbf = null;
	private MyTBTableModel tbmod = null;
	private JXTable tbtab = null;

	JTextField jTextSuchField = new JTextField("");
	private SqlInfo sqlinfo;

	public ICDoberflaeche(SqlInfo info) {
		sqlinfo = info;
		setOpaque(false);


		//                                   1    2     3   4     5   6       7      8    9  10  11 12  13  14 15    16             17
		FormLayout layob1 = new FormLayout("0dlu,0dlu,10dlu,p,5dlu,100dlu:g,5dlu,100dlu,5dlu,p,20dlu,p,5dlu,50dlu,5dlu,right:0:grow,10dlu,0dlu,0dlu",
				// 1  2     3    4  5           6           7          8            9     10    11
				"0dlu,0dlu,10dlu,p,10dlu,fill:0:grow(0.5),10dlu,  fill:0:grow(0.5),10dlu,10dlu,0dlu");
		CellConstraints c1 = new CellConstraints();
		setLayout(layob1);



		JLabel lblsuche = new JLabel("Suche nach");
		add(lblsuche, c1.xy(4,4));
		add(getSucheNachCombobox(),c1.xy(6,4));


		add(jTextSuchField,c1.xy(8,4));
		add(getButtons(),c1.xy(10,4));
		JLabel lbllimit = new JLabel("Limit");
		add(lbllimit, c1.xy(12,4));
		add(getLimitCombo(),c1.xy(14,4));
		add(getTabelle(),c1.xyw(4,6,13));
		add(getTextarea(),c1.xywh(4,8,13,2));

		registerActionListener(new ICDActionListener(this));

	}
	private void registerActionListener(ActionListener actionListener) {
		ActionListener listener = actionListener;
		jTextSuchField.addKeyListener(new KeyAdapter(){
			@Override
			public void keyPressed(KeyEvent ev){
				if(ev.getKeyCode()==10){
					listener.actionPerformed(new ActionEvent(ev.getSource(),ev.getID(), "suchen"));;
				}
			}
		});

		jBSuchen.addActionListener(listener);
	}
	SearchType type() {
		return suchNachCombobox.getItemAt(suchNachCombobox.getSelectedIndex());
	}
	String suchtext() {
		return jTextSuchField.getText().trim();
	}
	Integer limit() {
		return limitCombobox.getItemAt(limitCombobox.getSelectedIndex());
	}


	private JPanel getSucheNachCombobox(){
		FormLayout comboboxPan = new FormLayout("100dlu:g","p");
		PanelBuilder pcombox = new PanelBuilder(comboboxPan);
		pcombox.getPanel().setOpaque(false);
		CellConstraints ccombox = new CellConstraints();


		suchNachCombobox.setSelectedIndex(1);
		pcombox.add(suchNachCombobox,ccombox.xy(1,1));

		pcombox.getPanel().validate();
		return pcombox.getPanel();
	}



	private JPanel getLimitCombo(){
		FormLayout comboboxPan = new FormLayout("50dlu:g","p");
		PanelBuilder pcombox = new PanelBuilder(comboboxPan);
		pcombox.getPanel().setOpaque(false);
		CellConstraints ccombox = new CellConstraints();


		pcombox.add(limitCombobox,ccombox.xy(1,1));

		pcombox.getPanel().validate();
		return pcombox.getPanel();
	}

	private JPanel getButtons(){
		FormLayout buttonsPan = new FormLayout("60dlu","p");
		PanelBuilder pbuttons = new PanelBuilder(buttonsPan);
		pbuttons.getPanel().setOpaque(false);
		CellConstraints cbottons = new CellConstraints();

		jBSuchen = new JButton("Suchen");
		jBSuchen.setActionCommand("suchen");

		pbuttons.add(jBSuchen,cbottons.xy(1,1));


		pbuttons.getPanel().validate();
		return pbuttons.getPanel();
	}

	private JScrollPane getTabelle(){
		tbmod = new MyTBTableModel();
		tbmod.setColumnIdentifiers(new String[] {"ICD-Code o.Punkte","Titel","id","text"});
		tbtab = new JXTable(tbmod);
		tbtab.getColumn(0).setMaxWidth(150);
		tbtab.getColumn(0).setMinWidth(150);
		tbtab.getColumn(2).setMaxWidth(0);
		tbtab.getColumn(2).setMinWidth(0);
		tbtab.getColumn(2).setPreferredWidth(0);

		tbtab.getColumn(3).setMaxWidth(0);
		tbtab.getColumn(3).setMinWidth(0);
		tbtab.getColumn(3).setPreferredWidth(0);
		tbtab.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tbtab.getSelectionModel().addListSelectionListener( new TBListSelectionHandler());
		tbtab.setHighlighters(HighlighterFactory.createSimpleStriping());
		//tbtab.setRowHeight(20);
		JScrollPane scrollpane = new JScrollPane(tbtab);
		scrollpane.validate();

		return scrollpane;
	}

	private class TBListSelectionHandler implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent e) {
			if(e.getValueIsAdjusting()){
				return;
			}
			ListSelectionModel lsm = (ListSelectionModel)e.getSource();
			if (!lsm.isSelectionEmpty()) {

				int row = tbtab.getSelectedRow();

				String textValue = Optional.ofNullable((String)tbtab.getValueAt(row, 3))
											.orElse("Kein Text für diesen ICD-10 Code vorhanden");
				jtbf.setText(textValue);

			}

		}


	}

	private JScrollPane getTextarea(){
		jtbf = new JTextArea();
		jtbf.setFont(new Font("Courier",Font.PLAIN,11));
		jtbf.setLineWrap(true);
		jtbf.setName("sï¿½tze");
		jtbf.setWrapStyleWord(true);
		jtbf.setEditable(true);
		jtbf.setBackground(Color.WHITE);
		jtbf.setForeground(Color.BLUE);
		JScrollPane scrollpane = new JScrollPane(jtbf);
		scrollpane.validate();

		return scrollpane;
	}


	public void doSuchen(String suchtext, SearchType searchType, int parseInt){

		if(suchtext.equals("")){
			JOptionPane.showMessageDialog(null, "Bitte Suchbegriff(e) eingeben");
			return;
		}

		fillTable(sqlinfo.suchICD(suchtext, searchType, limit()));
	}
	 void fillTable(Vector<Vector<String>> ergebnis) {
		int lang = ergebnis.size();
		tbmod.setRowCount(0);
		if (lang > 0) {
			for (int i = 0; i < lang; i++) {
				tbmod.addRow(ergebnis.get(i));
			}
			tbtab.validate();
			tbtab.setRowSelectionInterval(0, 0);
		}
	}

	private class MyTBTableModel extends DefaultTableModel {
		/**
		 *
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public Class<String> getColumnClass(int columnIndex) {
			return String.class;
		}
	}


}
class ICDActionListener implements ActionListener{
	ICDoberflaeche oberf;

	public ICDActionListener(ICDoberflaeche oberflaeche) {
		oberf = oberflaeche;
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		String cmd = arg0.getActionCommand();
		if(cmd.equals("suchen")){
			oberf.doSuchen(oberf.jTextSuchField.getText().trim(),oberf.type(),oberf.limit());
		}
	}


}