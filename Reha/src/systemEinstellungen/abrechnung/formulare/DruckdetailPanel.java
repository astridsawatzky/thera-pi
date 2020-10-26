package systemEinstellungen.abrechnung.formulare;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Map;

import javax.print.PrintService;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.jdesktop.swingx.combobox.MapComboBoxModel;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class DruckdetailPanel {
  static final  String colSpecs = "right:max(50dlu;p), 10dlu, 85dlu,right:35dlu, 4dlu, 50dlu";
    String rowSpecs = " p, 10dlu, p, 3dlu, p, 3dlu, p, 10dlu";


    JComboBox<String> printerCmb = new JComboBox<String>();
    private JTextField formularName = new JTextField();
    JTextField exemplare = new JTextField();

    JPanel panel = createPanel();

    public DruckdetailPanel(String title, Map<String, PrintService> printers) {
        TitledBorder border = new TitledBorder(title);
        border.setTitleJustification(TitledBorder.CENTER);
        panel.setBorder(border);

        printerCmb.setModel(new MapComboBoxModel<String, PrintService>(printers));
        printerCmb.setEditable(true);

        printerCmb.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (index != -1) {
                    comp.setBackground(UIManager.getColor("List.background"));
                }
                return comp;
            }
        });

        printerCmb.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                if (printerCmb.getSelectedIndex() == -1) {
                    printerCmb.getEditor()
                              .getEditorComponent()
                              .setBackground(Color.GRAY);
                } else {
                    printerCmb.getEditor()
                              .getEditorComponent()
                              .setBackground(UIManager.getColor("List.background"));
                }
            }
        });
    }

    void setValues(FormularParameter params) {

        formularName.setText(params.template());
        printerCmb.setSelectedItem(params.printer().getName());
        exemplare.setText(String.valueOf(params.numberOfPrintOuts()));

    }

    private static final JFileChooser chooser = new JFileChooser();
    static {
        chooser.setFileHidingEnabled(false);
        chooser.addChoosableFileFilter(new FileNameExtensionFilter("Open/Libreoffice Vorlagen","odt","ott"));
    }

    private JPanel createPanel() {
        FormLayout layout = new FormLayout(colSpecs, rowSpecs);

        PanelBuilder builder = new PanelBuilder(layout);

        CellConstraints cc = new CellConstraints();

        builder.addLabel("Formular", cc.xy(1, 3));
        builder.add(formularName, cc.xyw(3, 3, 2));

        JButton auswahlBtn = new JButton("Auswahl");
        auswahlBtn.addActionListener(e -> formularName.setText(formularWaehlen()));
        builder.add(auswahlBtn, cc.xy(6, 3));

        builder.addLabel("Drucker", cc.xy(1, 5));

        builder.add(printerCmb, cc.xyw(3, 5, 4));

        builder.addLabel("Exemplare", cc.xy(4, 7));

        builder.add(exemplare, cc.xy(6, 7));

        return builder.getPanel();
    }

    private String formularWaehlen() {
        int showDialog = chooser.showDialog(null, "auswählen");
        if (JFileChooser.APPROVE_OPTION == showDialog) {
            SwingUtilities.invokeLater(() -> {
                formularName.setText(chooser.getSelectedFile()
                                            .getName());
            });
        } else {
            System.out.println(chooser);
        }
        return null;
    }
    FormularParameter formularparameter() {

        boolean printerEinstellungsAusVorlage = false;
        String template = formularName.getText();
        PrintService printer = (PrintService) printerCmb.getSelectedItem();
        int numberOfPrintOuts =Integer.valueOf(exemplare.getText()) ;
        return new FormularParameter(printerEinstellungsAusVorlage, template, printer, numberOfPrintOuts );
    }
}
