package rehaSql;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JTabbedPane;

import org.jdesktop.swingx.JXHeader;
import org.jdesktop.swingx.JXPanel;

import com.jgoodies.looks.windows.WindowsTabbedPaneUI;

public class RehaSqlTab extends JXPanel {

    /**
     * 
     */
    private static final long serialVersionUID = -7580783032048353314L;

    public RehaSqlTab() {
        super();
        setLayout(new BorderLayout());

        JTabbedPane sqlTab = new JTabbedPane();
        sqlTab.setUI(new WindowsTabbedPaneUI());
        RehaSqlPanel sqlPanel = new RehaSqlPanel();
        sqlTab.add("Sql-Befehle absetzen", sqlPanel);
        RehaSqlEdit sqlEditPanel = new RehaSqlEdit();
        sqlTab.add("Sql-Befehle entwerfen/bearbeiten", sqlEditPanel);
        JXHeader jxh = new JXHeader();
        ((JLabel) jxh.getComponent(1)).setVerticalAlignment(JLabel.NORTH);
        add(jxh, BorderLayout.NORTH);
        add(sqlTab, BorderLayout.CENTER);

        jxh.validate();
        sqlTab.validate();
        validate();

    }

}
