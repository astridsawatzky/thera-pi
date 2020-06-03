package opRgaf;

import static opRgaf.OffenePostenTableModel.GESAMTBETRAG;
import static opRgaf.OffenePostenTableModel.KENNUNG;
import static opRgaf.OffenePostenTableModel.KRANKENKASSENNAME;
import static opRgaf.OffenePostenTableModel.OFFEN;
import static opRgaf.OffenePostenTableModel.REZNUMMER;
import static opRgaf.OffenePostenTableModel.RGDATUM;
import static opRgaf.OffenePostenTableModel.RGNR;

import javax.swing.JComboBox;
final class OffenePostenComboBox extends JComboBox<CBModel> {

    private OffenePostenAbstractRowFilter rgNrGleichfilter;

    public OffenePostenComboBox() {
        addItem(new CBModel("Rechnungsnummer =", rgNrGleichfilter));
        addItem(new CBModel("Rechnungsnummer enth\u00e4lt", new OffenePostenTextFilter(RGNR)));
        addItem(new CBModel("REchnungsbetrag", new OffenePostenMoneyFilter(GESAMTBETRAG,Strategy.gleich)));
        addItem(new CBModel("REchnungsbetrag >=", new OffenePostenMoneyFilter(GESAMTBETRAG,Strategy.groesserOderGleich)));
        addItem(new CBModel("REchnungsbetrag <=", new OffenePostenMoneyFilter(GESAMTBETRAG,Strategy.kleinerOderGleich)));
        addItem(new CBModel("REchnungsdatum", new OffenePostenDatumFilter(RGDATUM,Strategy.gleich)));
        addItem(new CBModel("REchnungsdatum >=", new OffenePostenDatumFilter(RGDATUM,Strategy.groesserOderGleich)));
        addItem(new CBModel("REchnungsdatum <=", new OffenePostenDatumFilter(RGDATUM,Strategy.kleinerOderGleich)));

        addItem(new CBModel("offen", new OffenePostenMoneyFilter(OFFEN,Strategy.gleich)));
        addItem(new CBModel("offen >=", new OffenePostenMoneyFilter(OFFEN,Strategy.groesserOderGleich)));
        addItem(new CBModel("offen <=", new OffenePostenMoneyFilter(OFFEN,Strategy.kleinerOderGleich)));
        addItem(new CBModel("REchnungsbetrag", new OffenePostenMoneyFilter(GESAMTBETRAG,Strategy.gleich)));
        addItem(new CBModel("REchnungsbetrag >=", new OffenePostenMoneyFilter(GESAMTBETRAG,Strategy.groesserOderGleich)));
        addItem(new CBModel("REchnungsbetrag <=", new OffenePostenMoneyFilter(GESAMTBETRAG,Strategy.kleinerOderGleich)));
        addItem(new CBModel("Rezeptnummer enth\u00e4lt", new OffenePostenTextFilter(REZNUMMER)));
        addItem(new CBModel("Name enthält", new OffenePostenTextFilter(KENNUNG)));
        addItem(new CBModel("Krankenkasse enthält", new OffenePostenTextFilter(KRANKENKASSENNAME)));
    }
}
final class CBModel {
    String anzeigeText="";
    OffenePostenAbstractRowFilter filter;

    public CBModel(String anzeige, OffenePostenAbstractRowFilter filter) {
        anzeigeText = anzeige;
        this.filter = filter;
    }


    @Override
    public String toString() {
        return anzeigeText;
    }
}
