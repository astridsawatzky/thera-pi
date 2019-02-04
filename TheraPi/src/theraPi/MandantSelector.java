package theraPi;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

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

	public MandantSelector(MandantList liste, ImageIcon imageIcon) {
		setModal(true);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout(0, 0));
		setUndecorated(true);
		setPreferredSize(new Dimension(450, 200));
		getContentPane().add(center(liste.asList()));

		JLabel lblTop = new JLabel("", JLabel.CENTER);
		lblTop.setPreferredSize(new Dimension(0, 55));
		lblTop.setIcon(imageIcon);
		getContentPane().add(lblTop, BorderLayout.NORTH);

		chosen = liste.defaultMandant();
		

	}

	private Component center(List<Mandant> liste) {
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