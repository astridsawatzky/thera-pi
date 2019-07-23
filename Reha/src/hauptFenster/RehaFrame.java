package hauptFenster;

import javax.swing.JFrame;

import org.jdesktop.swingx.JXFrame;

final class RehaFrame extends JXFrame {

    public RehaFrame(String titel, String titel2) {
        setTitle(titel + titel2);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }
}