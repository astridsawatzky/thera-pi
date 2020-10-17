package arztBaustein;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

class ArztBausteinWindowlistener extends WindowAdapter {
    private ArztBausteinPanel abP;

    ArztBausteinWindowlistener(ArztBausteinPanel panel) {
        this.abP = panel;
    }

    public void windowClosed(WindowEvent arg0) {
        abP.closeDocument();
    }

    public void windowClosing(WindowEvent arg0) {
        abP.closeDocument();
    }
}
