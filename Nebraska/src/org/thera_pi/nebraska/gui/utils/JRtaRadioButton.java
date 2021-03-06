package org.thera_pi.nebraska.gui.utils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JRadioButton;

public class JRtaRadioButton extends JRadioButton
        implements ActionListener, PropertyChangeListener, FocusListener, KeyListener {

    /**
    	 * 
    	 */
    private static final long serialVersionUID = -5635752295896498062L;

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

    public void listenerLoeschen() {
        this.removeFocusListener(this);
        this.removeKeyListener(this);
        this.removeActionListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent arg0) {

        // this.getParent().dispatchEvent(arg0);
    }

    @Override
    public void focusGained(FocusEvent arg0) {

    }

    @Override
    public void focusLost(FocusEvent arg0) {

    }

    @Override
    public void keyPressed(KeyEvent arg0) {

        int code = arg0.getKeyCode();
        if (code == KeyEvent.VK_ENTER) {
            arg0.consume();
            this.transferFocus();
            this.getParent()
                .dispatchEvent(arg0);
            return;
        }
        if (code == KeyEvent.VK_ESCAPE) {
            this.getParent()
                .dispatchEvent(arg0);
            this.getParent()
                .getParent()
                .dispatchEvent(arg0);
            this.getParent()
                .getParent()
                .getParent()
                .dispatchEvent(arg0);
            this.getParent()
                .getParent()
                .getParent()
                .getParent()
                .dispatchEvent(arg0);
            return;
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