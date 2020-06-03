package opRgaf;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import mandant.IK;

public class Gui {

    public static void main(String[] args) {
        JFrame frame = new JFrame();

        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        List<OffenePosten> opListe = new OffenePostenDTO(new IK("123456789")).all();
        OffenePostenTableModel model = new OffenePostenTableModel(opListe);
        OffenePostenJTable opJTable = new OffenePostenJTable(model);

        frame.getContentPane()
             .setLayout(new BorderLayout());
        JTextField eingabeFeld = new JTextField();
        OffenePostenComboBox opComboBox = new OffenePostenComboBox();
        opComboBox.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                System.out.println(e);

                CBModel selectedItem = (CBModel) opComboBox.getSelectedItem();
                if (selectedItem != null) {
                    OffenePostenRowFilter filter = selectedItem.filter;
                    opJTable.setFilter(filter);
                }
            }
        });

        eingabeFeld.getDocument()
                   .addDocumentListener(new DocumentListener() {

                       @Override
                       public void removeUpdate(DocumentEvent e) {
                           update(e);

                       }

                       @Override
                       public void insertUpdate(DocumentEvent e) {
                           update(e);
                       }

                       @Override
                       public void changedUpdate(DocumentEvent e) {
                           update(e);
                       }

                       private void update(DocumentEvent e) {

                           OffenePostenRowFilter filter = ((CBModel) opComboBox.getSelectedItem()).filter;
                           if (filter != null) {
                               filter.setFiltertext(eingabeFeld.getText());
                               opJTable.sorter.sort();
                           }

                       }
                   });
        frame.getContentPane()
             .add(opComboBox, BorderLayout.NORTH);
        frame.getContentPane()
             .add(eingabeFeld, BorderLayout.CENTER);

        frame.getContentPane()
             .add(new JScrollPane(opJTable), BorderLayout.SOUTH);

        frame.pack();
        frame.setVisible(true);
    }
}
