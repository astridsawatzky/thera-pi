package opRgaf;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JPanel;

import CommonTools.Select3ChkBx;

final class OffenePostenCHKBX extends Select3ChkBx {
    OffenePostenCHKBX(String ask, String chkBxOLabel, String chkBxMLabel, String chkBxULabel) {
        super(ask, chkBxOLabel, chkBxMLabel, chkBxULabel);
        chkBxO.setSelected(true);

    }


    @Override
    public void itemStateChanged(ItemEvent e) {
        // keine selbstgespräche

    }

    JPanel getPanel() {
        return checkBoxArea;

    }

    void addMListener(ItemListener listener) {
        chkBxM.addItemListener(listener);

    }
    void addOListener(ItemListener listener) {
        chkBxO.addItemListener(listener);

    }
    void addUListener(ItemListener listener) {
        chkBxU.addItemListener(listener);

    }


    public void initSelection(boolean incRG, boolean incAR, boolean incVK) {
        chkBxO.setSelected(incRG);
        chkBxM.setSelected(incAR);
        chkBxU.setSelected(incVK);

    }
}
