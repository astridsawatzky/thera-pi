package systemEinstellungen.abrechnung.formulare;

import java.awt.Component;
import java.awt.Label;
import java.util.Map;

import javax.print.PrintService;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.jdesktop.swingx.JXTitledSeparator;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class SwingPanel {

    private JCheckBox askbeforemail = new JCheckBox("vor dem Versand immer fragen");
    private JPanel mainPanel = new JPanel();
    GKVAbrechnungsParameter gKVAbrechnungsParameter;

    final DruckdetailPanel gkv;
    final DruckdetailPanel tax;
    final DruckdetailPanel rgr;
    final DruckdetailPanel bg;
    final DruckdetailPanel privat;
    private JCheckBox   direktdruck = new JCheckBox("nicht in Office anzeigen, direkt an Drucker senden");

    public SwingPanel(Map<String, PrintService> availableprinters) {
        gkv = new DruckdetailPanel("Rechnungen", availableprinters);

        tax = new DruckdetailPanel("Taxieren", availableprinters);
        rgr = new DruckdetailPanel("Rchnungen", availableprinters);
        bg = new DruckdetailPanel("Rechnungen", availableprinters);
        privat = new DruckdetailPanel("Rechnungen", availableprinters);

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
        FormLayout layout = new FormLayout(DruckdetailPanel.colSpecs, " p, 3dlu, p, 3dlu, p, 3dlu, p, 10dlu");

        PanelBuilder builder = new PanelBuilder(layout);

        CellConstraints cc = new CellConstraints();
        builder.addLabel("Druck",cc.xy(1, 1));

        builder.add(direktdruck,cc.xyw(3, 1,4));

        builder.addLabel("302 er Mail",cc.xy(1, 3));
        builder.add(askbeforemail,cc.xyw(3, 3,4));


        mainPanel.add(builder.getPanel());

        JPanel buttonPanel = new JPanel();
        mainPanel.add(new JXTitledSeparator());

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
        rgr.setValues(gKVAbrechnungsParameter.rgr);
        privat.setValues(gKVAbrechnungsParameter.privat);

    }

    public Component panel() {
        return mainPanel;
    }

    public GKVAbrechnungsParameter abrechnungparameter() {
        boolean direktAusdruck =direktdruck.isSelected();
        boolean askBefore302Mail =askbeforemail.isSelected();

      return  new GKVAbrechnungsParameter(tax.formularparameter(), gkv.formularparameter(), privat.formularparameter(), bg.formularparameter(), rgr.formularparameter(), direktAusdruck, askBefore302Mail);
    }

}
