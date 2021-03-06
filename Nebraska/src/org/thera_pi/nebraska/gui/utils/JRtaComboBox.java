package org.thera_pi.nebraska.gui.utils;

import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Vector;

import javax.swing.JComboBox;

public class JRtaComboBox extends JComboBox implements PropertyChangeListener, FocusListener, KeyListener {
    /**
    	 *
    	 */
    private static final long serialVersionUID = 6867094510690570951L;
    public Vector<?> vec = null;
    public int cmbdisplay;
    public int cmbretvalue;
    public String startElement = "";

    public JRtaComboBox() {
        super();
        addKeyListener(this);
        addActionListener(this);
    }

    public JRtaComboBox(String[] ss) {
        super(ss);
        addKeyListener(this);
        addActionListener(this);
    }

    public JRtaComboBox(Vector<String> ve) {
        super();
        this.vec = ve;
        if (this.vec.get(0) instanceof Vector<?>) {
            fillCombo(this.vec);
        } else {
            fillOneDimension(this.vec);
        }

        addKeyListener(this);
        addActionListener(this);
    }

    public JRtaComboBox(Vector<Vector<String>> ve, int item, int ret) {
        super();
        this.vec = ve;
        this.cmbdisplay = item;
        this.cmbretvalue = ret;
        try {
            if (this.vec == null) {

            } else if (this.vec.get(0) instanceof Vector<?>) {
                fillCombo(this.vec);
            } else {
                fillOneDimension(this.vec);
            }
        } catch (Exception ex) {

        }
        addKeyListener(this);
        addActionListener(this);
    }

    public void listenerLoeschen() {
        this.removeFocusListener(this);
        this.removeKeyListener(this);
        this.removeActionListener(this);
    }

    public void setDataVector2Dim(Vector<Vector<String>> ve, int item, int ret) {
        this.removeAllItems();
        this.vec = ve;
        this.cmbdisplay = item;
        this.cmbretvalue = ret;
        if (this.vec.get(0) instanceof Vector<?>) {
            fillCombo(this.vec);
        } else {
            fillOneDimension(this.vec);
        }
    }

    public void setDataVectorVector(Vector<Vector<String>> ve, int item, int ret) {
        this.removeAllItems();
        this.vec = ve;
        this.cmbdisplay = item;
        this.cmbretvalue = ret;
        if (this.vec.get(0) instanceof Vector<?>) {
            fillCombo(this.vec);
        } else {
            fillOneDimension(this.vec);
        }
    }

    public void setDataVectorWithStartElement(Vector<Vector<String>> ve, int item, int ret, String startElement) {
        this.removeAllItems();
        this.vec = ve;
        this.cmbdisplay = item;
        this.cmbretvalue = ret;
        this.startElement = startElement;
        if (this.vec.get(0) instanceof Vector<?>) {
            fillComboWithStartElement(this.vec, this.startElement);
        } else {
            fillOneDimensionWithStartElement(this.vec, this.startElement);
        }
    }

    public void setRetValueAsDisplayIndex(String retValue) {
        int lang = getItemCount();
        for (int i = 0; i < lang; i++) {
            if (((String) ((Vector<?>) this.vec.get(i)).get(cmbretvalue)).equals(retValue)) {
                setSelectedIndex(i + (this.startElement.equals("") ? 0 : 1));
                break;
            }
        }
    }

    public void setSelectedVecIndex(int index, String vergleich) {
        int lang = getItemCount();
        for (int i = 0; i < lang; i++) {
            if (((String) ((Vector<?>) this.vec.get(i)).get(index)).equals(vergleich)) {
                setSelectedIndex(i + (this.startElement.equals("") ? 0 : 1));
                break;
            }
        }
    }

    public void setDataVector(Vector<String> ve, int item, int ret) {

    }

    private void fillOneDimensionWithStartElement(Vector<?> ve, String startElement) {
        int lang = ve.size();
        addItem(startElement);
        for (int i = 0; i < lang; i++) {
            addItem(ve.get(i));
        }
    }

    private void fillComboWithStartElement(Vector<?> ve, String startElement) {
        // int lang = ve.size()-1;
        int lang = ve.size();
        addItem(startElement);
        for (int i = 0; i < lang; i++) {
            addItem(((Vector<?>) ve.get(i)).get(this.cmbdisplay));
        }

    }

    private void fillOneDimension(Vector<?> ve) {
        int lang = ve.size();
        for (int i = 0; i < lang; i++) {
            addItem(ve.get(i));
        }
    }

    private void fillCombo(Vector<?> ve) {
        // int lang = ve.size()-1;
        int lang = ve.size();
        for (int i = 0; i < lang; i++) {
            addItem(((Vector<?>) ve.get(i)).get(this.cmbdisplay));
        }

    }

    public Object getSecValue() {
        if (this.startElement.equals("")) {
            return (((Vector<?>) vec.get(this.getSelectedIndex())).get(this.cmbretvalue));
        } else {
            return (((Vector<?>) vec.get(this.getSelectedIndex() - 1)).get(this.cmbretvalue));
        }

    }

    public Object getValue() {
        if (this.startElement.equals("")) {
            return (((Vector<?>) vec.get(this.getSelectedIndex())).get(this.cmbretvalue));
        } else {
            return (((Vector<?>) vec.get(this.getSelectedIndex() - 1)).get(this.cmbretvalue));
        }
    }

    public Object getValueAt(int pos) {
        if (vec.size() <= 0) {
            return "";
        }
        if (this.startElement.equals("")) {
            return (((Vector<?>) vec.get(this.getSelectedIndex())).get(pos));
        } else {
            if (this.getSelectedIndex() == 0) {
                return "";
            }
            return (((Vector<?>) vec.get(this.getSelectedIndex() - 1)).get(pos));
        }
    }

    public void setNewValueAtCurrentPosition(int pos, Object newvalue) {

        if (this.startElement.equals("")) {
            ((Vector<Object>) vec.get(this.getSelectedIndex())).set(pos, newvalue);
        } else {
            ((Vector<Object>) vec.get(this.getSelectedIndex() - 1)).set(pos, newvalue);
        }
    }

    public void addNewVector(Vector<String> newvec) {
        ((Vector<Vector<String>>) vec).add(newvec);
        addItem(newvec.get(this.cmbdisplay));
    }

    public void removeVector(int pos) {
        if (this.startElement.equals("")) {
            ((Vector<Object>) vec).remove(pos);
        } else {
            ((Vector<Object>) vec).remove(pos - 1);
        }
        this.removeItemAt(pos);
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
            try {
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
            } catch (Exception ex) {

            }
            return;
        }
        if (code == 38) {
            arg0.consume();
            this.transferFocusBackward();
            this.getParent()
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

        try {
            this.getParent()
                .dispatchEvent(arg0);
        } catch (java.lang.NullPointerException ex) {

        }
    }

}