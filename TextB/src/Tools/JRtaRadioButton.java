package Tools;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JRadioButton;

public class JRtaRadioButton extends JRadioButton implements ActionListener, KeyListener {

    public JRtaRadioButton() {
        super();
        addKeyListener(this);
        addActionListener(this);
    }

    public JRtaRadioButton(String ss) {
        super(ss);
        addKeyListener(this);
        addActionListener(this);
    }

    @Override
    public void keyPressed(KeyEvent arg0) {

        int code = arg0.getKeyCode();
        if (code == KeyEvent.VK_ENTER) {
            arg0.consume();
            this.getParent()
                .dispatchEvent(arg0);

        }
    }

    @Override
    public void keyReleased(KeyEvent arg0) {

    }

    @Override
    public void keyTyped(KeyEvent arg0) {

    }

    @Override
    public void actionPerformed(ActionEvent arg0) {

        this.getParent()
            .dispatchEvent(arg0);
    }

}
