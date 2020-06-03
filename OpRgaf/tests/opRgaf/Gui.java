package opRgaf;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.WindowConstants;

import mandant.IK;

public class Gui {



    public static void main(String[] args) {
        JFrame frame = new JFrame();

        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        List<OffenePosten> opListe = new OffenePostenDTO(new IK("123456789")).all();
        OffenePostenTableModel model = new OffenePostenTableModel(opListe );
        OffenePostenJTable view = new OffenePostenJTable(model);

        frame.getContentPane().setLayout(new BorderLayout());
        OffenePostenComboBox opComboBox = new OffenePostenComboBox();
        opComboBox.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                System.out.println(e);

                CBModel selectedItem = (CBModel) opComboBox.getSelectedItem();
                if(selectedItem!=null)
                view.setFilter(selectedItem.filter);
            }
        });
        frame.getContentPane().add(opComboBox,BorderLayout.NORTH);
        frame.getContentPane().add(new JTextField(),BorderLayout.CENTER);
        frame.getContentPane().add(new JScrollPane(view),BorderLayout.SOUTH);

        frame.pack();
        frame.setVisible(true);
    }
}
