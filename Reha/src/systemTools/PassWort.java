package systemTools;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JXHeader;

import com.sun.star.awt.KeyModifier;

import dialoge.RehaSmartDialog;
import environment.Path;
import events.RehaTPEvent;
import events.RehaTPEventClass;
import hauptFenster.Reha;
import terminKalender.ParameterLaden;

public class PassWort  {

	JPanel loginPanel = new JPanel();

	ActionListener submitted = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			passWortCheck();

		}
	};

	/**
	 *
	 */
	private static final long serialVersionUID = -702150863186759990L;
	private JPasswordField pwTextFeld = null;
	private JButton pwButton = null;
	private int falscherLogin = 0;

	public PassWort() {
		super();

		loginPanel.setBorder(null);
		loginPanel.setLayout(new BorderLayout());
		ImageIcon imageIcon = new ImageIcon(Path.Instance.getProghome() + "icons/schluessel3.gif");
		JXHeader header = new JXHeader("Benutzer Authentifizierung",
				"In diesem Fenster geben Sie Ihr persönliches Passwort ein.\n"
						+ "Abhängig vom Ihrem Passwort, haben Sie Zugang zu allen Programmteilen "
						+ "die für Sie persönlich freigeschaltet wurden.\n"
						+ "Noch kein Passwort? Dann geben Sie bitte das Universalpasswort ein. \n\n"
						+ "Hinweis--> Nach 3-maliger Falscheingabe wird der Administrator per Email über den fehlgeschlagenen Login-Versuch informiert.",
				imageIcon);
		loginPanel.add(header, BorderLayout.NORTH);

		JPanel jgrid = new JPanel(new GridLayout(4, 1));
		jgrid.setBorder(null);

		jgrid.add(new JLabel(""));

		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.anchor = GridBagConstraints.CENTER;
		gridBagConstraints.fill = GridBagConstraints.NONE;
		gridBagConstraints.ipadx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.weightx = 1.0D;
		gridBagConstraints.weighty = 1.0D;
		gridBagConstraints.insets = new Insets(10, 10, 10, 10);

		JPanel jp = new JPanel(new FlowLayout());
		jp.setBorder(null);
		JLabel label = new JLabel("Bitte Passwort eingeben:  ");
		jp.add(label);
		pwTextFeld = new JPasswordField();
		pwTextFeld.setPreferredSize(new Dimension(120, 25));
		pwTextFeld.addKeyListener(consumeInput);
		jp.add(pwTextFeld);

		jgrid.add(jp);

		GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.gridx = 0;
		gridBagConstraints2.anchor = GridBagConstraints.CENTER;
		gridBagConstraints2.fill = GridBagConstraints.NONE;
		gridBagConstraints2.ipadx = 0;
		gridBagConstraints2.gridy = 1;
		gridBagConstraints2.weightx = 1.0D;
		gridBagConstraints2.weighty = 0.5D;
		gridBagConstraints2.insets = new Insets(10, 0, 10, 0);
		pwButton = new JButton("Benutzer authentifizieren");
		pwButton.setPreferredSize(new Dimension(160, 25));
		pwButton.addActionListener(submitted);
		JPanel butpanel = new JPanel(new FlowLayout());
		butpanel.setBorder(null);
		butpanel.add(pwButton);
		jgrid.add(butpanel);
		jgrid.add(new JLabel(""));

		loginPanel.add(jgrid, BorderLayout.CENTER);
		loginPanel.setVisible(true);
		loginPanel.addKeyListener(consumeInput);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				pwTextFeld.requestFocus();
			}
		});

	}

	private void passWortCheck() {

		int size;
		boolean check = false;
		String name = "", rechte = "", test = String.valueOf(pwTextFeld.getPassword());
		size = ParameterLaden.pKollegen.size();
		for (int i = 0; i < size; i++) {
			//// System.out.println(ParameterLaden.pKollegen.get(i).get(1));
			if (test.equals(ParameterLaden.pKollegen.get(i).get(1))) {
				name = ParameterLaden.pKollegen.get(i).get(0);
				rechte = ParameterLaden.pKollegen.get(i).get(2);
				//// System.out.println("Rechte = "+rechte);
				Reha.progRechte = rechte;
				Reha.getThisFrame().setTitle(Reha.Titel + Reha.Titel2 + "  -->  [Benutzer: " + name + "]");
				Reha.aktUser = name;
				check = true;
				break;
			}
		}
		reactOnPwCheck(check);
	}

	private void reactOnPwCheck(boolean check) {
		if (check) {
			// Korrekter Login
			loginPanel.setName(this.grundContainer().getName());
			RehaTPEvent rEvt = new RehaTPEvent(this);
			rEvt.setRehaEvent("PinPanelEvent");
			rEvt.setDetails(loginPanel.getName(), "ROT");
			RehaTPEventClass.fireRehaTPEvent(rEvt);
			Reha.getThisFrame().setVisible(true);
			Reha.getThisFrame().validate();
			this.grundContainer().Schliessen();

		} else {
			JOptionPane.showMessageDialog(null, "Benutzer mit diesem Passwort ist nicht vorhanden\n\nVersuch "
					+ Integer.toString(falscherLogin + 1) + " von 3");
			falscherLogin = falscherLogin + 1;
			pwTextFeld.requestFocus();
			if (falscherLogin == 3) {
				// Hier Email an Admin
				falscherLogin = 0;
				System.exit(0);
			}
		}
	}

	KeyListener consumeInput = new KeyAdapter() {

		@Override
		public void keyPressed(KeyEvent e) {

			int code = e.getKeyCode();
			if (code == KeyEvent.VK_ESCAPE) {
				e.consume();
			} else if (code == KeyEvent.VK_ENTER) {
				e.consume();
				passWortCheck();
			} else if ((e.getModifiers() == KeyModifier.MOD1) || (e.getModifiers() == KeyModifier.MOD2)) {
				e.consume();
			}

		}

	};

	private RehaSmartDialog grundContainer() {
		return (RehaSmartDialog) loginPanel.getParent().getParent().getParent().getParent().getParent();
	}

	public Container getPanel() {
		return loginPanel;
	}


}
