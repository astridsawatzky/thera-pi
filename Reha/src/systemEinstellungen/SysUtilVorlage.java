package systemEinstellungen;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.jdesktop.swingx.JXPanel;

import hauptFenster.Reha;

public class SysUtilVorlage extends JXPanel {
    private static final long serialVersionUID = 1L;

    public SysUtilVorlage(ImageIcon img) {
        super(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 20));
        setBackgroundPainter(Reha.instance.compoundPainter.get("SystemInit"));
        JLabel jlbl = new JLabel("");
        jlbl.setIcon(img);
        add(jlbl, BorderLayout.CENTER);
    }
}
