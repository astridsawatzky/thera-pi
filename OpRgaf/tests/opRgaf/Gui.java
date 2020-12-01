package opRgaf;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import mandant.IK;

public class Gui {

    public static void main(String[] args) throws SQLException {

        JFrame frame = new JFrame();

        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        List<OffenePosten> opListe = new OffenePostenDTO(new IK("123456789")).all();
        OffenePostenTableModel model = new OffenePostenTableModel(opListe);
        OffenePostenJTable opJTable = new OffenePostenJTable(model);

        frame.getContentPane()
             .setLayout(new BorderLayout());
        JTextField eingabeFeld = new JTextField();
        OffenePostenComboBox opComboBox = new OffenePostenComboBox(1);


        verknuepfen(opJTable, eingabeFeld, opComboBox);
        frame.getContentPane()
             .add(opComboBox, BorderLayout.NORTH);
        frame.getContentPane()
             .add(eingabeFeld, BorderLayout.CENTER);

        frame.getContentPane()
             .add(new JScrollPane(opJTable), BorderLayout.SOUTH);

        OffenePostenCHKBX select3ChkBx = new OffenePostenCHKBX();

        verknuepfe(opJTable, select3ChkBx);
        opJTable.sorter.sort();
        frame.getContentPane()
             .add(select3ChkBx.getPanel(), BorderLayout.WEST);
        frame.pack();
        frame.setVisible(true);
    }

    private static void verknuepfen(OffenePostenJTable opJTable, JTextField eingabeFeld,
            OffenePostenComboBox opComboBox) {
        opComboBox.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                System.out.println(e);
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    CBModel selectedItem = (CBModel) opComboBox.getSelectedItem();
                    if (selectedItem != null) {
                        OffenePostenAbstractRowFilter filter = selectedItem.filter;
                        opJTable.setContentFilter(filter);
                        opJTable.sorter.sort();

                    }
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

                           OffenePostenAbstractRowFilter filter = ((CBModel) opComboBox.getSelectedItem()).filter;
                           if (filter != null) {
                               filter.setFiltertext(eingabeFeld.getText());
                               opJTable.sorter.sort();
                           }

                       }
                   });
    }

    private static void verknuepfe(OffenePostenJTable opJTable, OffenePostenCHKBX select3ChkBx) {
        OffenePostenSchaltbarerTextFilter rgrTypefilter = new OffenePostenSchaltbarerTextFilter(OffenePostenTableModel.RGNR ,"rgr",true) ;
        OffenePostenSchaltbarerTextFilter afrTypefilter = new OffenePostenSchaltbarerTextFilter(OffenePostenTableModel.RGNR ,"afr",false) ;
        OffenePostenSchaltbarerTextFilter vrTypefilter = new OffenePostenSchaltbarerTextFilter(OffenePostenTableModel.RGNR ,"vr",false) ;


        List<OffenePostenSchaltbarerTextFilter> filters = Arrays.asList(rgrTypefilter , afrTypefilter,vrTypefilter);
        opJTable.setTypeFilter(RowFilter.orFilter(filters) );


        select3ChkBx.addOListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                rgrTypefilter.set(e.getStateChange() == ItemEvent.SELECTED);
                opJTable.sorter.sort();

            }
        });
        select3ChkBx.addMListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                afrTypefilter.set(e.getStateChange() == ItemEvent.SELECTED);
                opJTable.sorter.sort();
            }
        });
        select3ChkBx.addUListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                vrTypefilter.set(e.getStateChange() == ItemEvent.SELECTED);
                opJTable.sorter.sort();
            }
        });
    }
}
