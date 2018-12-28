package theraPi;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import mandant.Mandant;

final class MandantSelector extends JDialog {

	private Mandant chosen;
	private ActionListener listener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			MandantButton but = (MandantButton) e.getSource();
			chosen = but.mandant();

			dispose();

		}
	};

	public MandantSelector(MandantList liste) {
		setModal(true);
		getContentPane().setLayout(new BorderLayout(0, 0));
		setUndecorated(true);
		setPreferredSize(new Dimension(450, 200));
		getContentPane().add(center(liste));

		JLabel lblTop = new JLabel("", JLabel.CENTER);
		lblTop.setPreferredSize(new Dimension(0, 55));
		lblTop.setIcon(new ImageIcon(TheraPi.proghome + "icons/TPorg.png"));
		getContentPane().add(lblTop, BorderLayout.NORTH);

		chosen = liste.defaultMandant();
		

	}

	private Component center(MandantList liste) {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(liste.size(), 1));
		for (Mandant man : liste) {
			MandantButton button = new MandantButton(man);
			button.addActionListener(listener);
			panel.add(button);
		}
		return panel;
	}

	public Mandant chosen() {
		return chosen;
	}
}