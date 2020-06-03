package opRgaf;

import javax.swing.JComboBox;
import javax.swing.RowFilter;

final class OffenePostenComboBox extends JComboBox<CBModel> {

    private static final OffenePostenRowFilter rgNrEnthaeltfilter = null;
    private OffenePostenRowFilter rgNrGleichfilter;

    public OffenePostenComboBox() {
        addItem(new CBModel("Rechnungsnummer =", rgNrGleichfilter));
        addItem(new CBModel("Rechnungsnummer enth\u00e4lt", rgNrEnthaeltfilter));
        addItem(new CBModel("nameenathältabd", new OffenePostenRowFilter()));

    }
}
final class CBModel {
    String anzeigeText="";
    OffenePostenRowFilter filter;

    public CBModel(String anzeige, OffenePostenRowFilter filter) {
        anzeigeText = anzeige;
        this.filter = filter;
    }

  
    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return anzeigeText;
    }
}
