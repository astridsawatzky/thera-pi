package systemEinstellungen.abrechnung.formulare;

import java.awt.Component;
import java.awt.Label;
import java.util.Map;

import javax.print.PrintService;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.jdesktop.swingx.JXTitledSeparator;

public class SwingPanel {

    private JPanel mainPanel = new JPanel();
    GKVAbrechnungsParameter gKVAbrechnungsParameter;

    final DetailPanel gkv;
    final DetailPanel tax;
    final DetailPanel rgr;
    final DetailPanel bg;
    final DetailPanel privat;

    public SwingPanel(Map<String, PrintService> availableprinters) {
        gkv = new DetailPanel("Rechnungen", availableprinters);
        tax = new DetailPanel("Taxieren", availableprinters);
        rgr = new DetailPanel("Rezeptgebühren", availableprinters);
        bg = new DetailPanel("Rechnungen", availableprinters);
        privat = new DetailPanel("Rechnungen", availableprinters);

        JPanel gkvPanel = new JPanel();
        gkvPanel.setLayout(new BoxLayout(gkvPanel, BoxLayout.Y_AXIS));

        gkv.panel.setBorder(createcenteredtitledborder("Rechnungen"));
        gkvPanel.add(gkv.panel);

        tax.panel.setBorder(createcenteredtitledborder("Taxieren"));
        gkvPanel.add(tax.panel);
        rgr.panel.setBorder(createcenteredtitledborder("Rezeptgebühren"));
        gkvPanel.add(rgr.panel);

        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(new JXTitledSeparator("GKV-Rechnungen"));
        mainPanel.add(gkvPanel);
        mainPanel.add(new JXTitledSeparator("BG-Rechnungen"));
        mainPanel.add(bg.panel);
        mainPanel.add(new JXTitledSeparator("Privatrechnungen"));
        mainPanel.add(privat.panel);

        mainPanel.add(new JXTitledSeparator("Gemeinsame Einstellungen"));

        // TODO: 2-3 knoeppfe.
        mainPanel.add(new JXTitledSeparator(""));
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.add(new Label("Änderungen übernehmen?"));
        buttonPanel.add(new JButton("wäck damöt"));
        buttonPanel.add(new JButton("spoichern"));
        mainPanel.add(buttonPanel);

    }

    private TitledBorder createcenteredtitledborder(String title) {
        TitledBorder border = new TitledBorder(title);
        border.setTitleJustification(TitledBorder.CENTER);
        return border;
    }

    public void setgKVAbrechnungsParameter(GKVAbrechnungsParameter gKVAbrechnungsParameter) {
        this.gKVAbrechnungsParameter = gKVAbrechnungsParameter;

        gkv.setValues(gKVAbrechnungsParameter.gkv);
        tax.setValues(gKVAbrechnungsParameter.taxierung);
        bg.setValues(gKVAbrechnungsParameter.bg);
        privat.setValues(gKVAbrechnungsParameter.privat);

    }

    public Component panel() {
        // TODO Auto-generated method stub
        return mainPanel;
    }

}
