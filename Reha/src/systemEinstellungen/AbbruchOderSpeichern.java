package systemEinstellungen;

import javax.swing.JButton;
import javax.swing.JPanel;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class AbbruchOderSpeichern {

    private JPanel footerArea;

    public AbbruchOderSpeichern(SysInitCommon_If owner) {
        JButton btAbbruch = new JButton("abbrechen");
        btAbbruch.addActionListener(e -> owner.Abbruch());
        JButton btSpeichern = new JButton("speichern");
        btSpeichern.addActionListener(e -> owner.Speichern());
        // 1. 2. 3. 4. 5. 6. 7.
        FormLayout jpanlay = new FormLayout("p:g, right:max(150dlu;p), 60dlu, 60dlu, 4dlu, 60dlu, 20dlu",
                // 1. 2. 3. 4.
                "10dlu,p, 10dlu, p");

        PanelBuilder jpan = new PanelBuilder(jpanlay);
        jpan.getPanel()
            .setOpaque(false);
        CellConstraints jpancc = new CellConstraints();

        jpan.addSeparator("", jpancc.xyw(1, 2, 6));
        jpan.add(btAbbruch, jpancc.xy(4, 4));
        jpan.add(btSpeichern, jpancc.xy(6, 4));
        jpan.addLabel("Änderungen übernehmen?", jpancc.xy(2, 4));

        jpan.getPanel()
            .validate();

        footerArea = jpan.getPanel();
    }

    public JPanel getPanel() {
        return footerArea;
    }

}
