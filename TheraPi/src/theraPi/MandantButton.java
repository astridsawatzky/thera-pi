package theraPi;

import javax.swing.JButton;

import mandant.Mandant;

class MandantButton extends JButton {
	private Mandant mandant;

	public MandantButton(Mandant mandant) {
		this.mandant= mandant;
		this.setText(mandant.name() + " - IK" + mandant.ik());

	}

	public Mandant mandant() {
		return mandant;
	}
}
