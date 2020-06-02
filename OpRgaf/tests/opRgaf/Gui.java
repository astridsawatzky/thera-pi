package opRgaf;

import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

import mandant.IK;

public class Gui {



    public static void main(String[] args) {
        JFrame frame = new JFrame();

        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        List<OffenePosten> opListe = new OffenePostenDTO(new IK("123456789")).all();
        OffenePostenTableModel model = new OffenePostenTableModel(opListe );
        OffenePostenJTable view = new OffenePostenJTable(model);

        view.setFilter(OffenePostenTableModel.containsabd);
        frame.getContentPane().add(new JScrollPane(view));

        frame.pack();
        frame.setVisible(true);
    }
}
