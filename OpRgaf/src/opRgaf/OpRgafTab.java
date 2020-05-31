package opRgaf;

import java.awt.BorderLayout;

import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jdesktop.swingx.JXPanel;

import com.jgoodies.looks.windows.WindowsTabbedPaneUI;

/** TODO: only public because of RehaIO */
class OpRgafTab extends JXPanel implements ChangeListener {
    private static final long serialVersionUID = -6012301447745950357L;

    private JTabbedPane jtb;

    private Header jxh = new Header();;
    OpRgafPanel opRgafPanel;

    private OpRgafMahnungen opRgafMahnungen;

    OpRgafTab(OpRgaf opRgaf) {
        setOpaque(false);
        setLayout(new BorderLayout());
        jtb = new JTabbedPane();
        jtb.setUI(new WindowsTabbedPaneUI());

        opRgafPanel = new OpRgafPanel(this, opRgaf);
        jtb.addTab("Rezeptgebühr-/Ausfall-/Verkaufsrechnungen ausbuchen", opRgafPanel);

        opRgafMahnungen = new OpRgafMahnungen(this, opRgaf);
        jtb.addTab("Rezeptgebühr-/Ausfall-/Verkaufsrechnungen Mahnungen", opRgafMahnungen);

        jtb.addChangeListener(this);

        jtb.addChangeListener(jxh);
        add(jxh, BorderLayout.NORTH);
        add(jtb, BorderLayout.CENTER);

        jxh.validate();
        jtb.validate();
        validate();
    }

    @Override
    public void stateChanged(ChangeEvent arg0) {
        JTabbedPane pane = (JTabbedPane) arg0.getSource();
        int sel = pane.getSelectedIndex();
        try {
            switch (sel) {
            case 0:
                opRgafPanel.initSelection();
                break;
            case 1:
                opRgafMahnungen.initSelection();
                break;
            }
        } catch (Exception ex) {
        }

    }

    void setFirstFocus() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new InitHashMaps();
                opRgafPanel.setzeFocus();
            }
        });
    }

    public String getNotBefore() {
        try {
            return "2010-03-01";
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Fehler beim Bezug des Startdatums, nehme 01.01.1995");
        }
        return "1995-01-01";
    }

    void sucheRezept(String rezept) {
        opRgafPanel.sucheRezept(rezept);
    }
}
