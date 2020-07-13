package hmv;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

public class Hmv13 {

    @FXML
    ToggleGroup zuzahlung;
    private ObjectProperty<Zuzahlung> zuzahlungProperty = new SimpleObjectProperty<>();

    @FXML
    TextField kostentraeger;

    @FXML
    TextField name;

    @FXML
    TextField vorname;

    @FXML
    DatePicker geboren;

    @FXML
    TextField kostentraegerKennung;
    @FXML
    TextField versichertenNummer;
    @FXML
    TextField versichertenStatus;

    @FXML
    TextField betriebsstaettenNr;
    @FXML
    TextField lebenslangeArztNr;
    @FXML
    DatePicker rezeptDatum;

    @FXML

    ToggleGroup disziplin;

    @FXML
    TextField icd10Code_1;
    @FXML
    TextField icd10Code_2;
    // do we need 2 textfields?
    @FXML
    TextArea icd10Code_Text;

    // this might be a combobox (wrong name )
    @FXML
    TextField diagnoseGruppe;

    @FXML
    ToggleGroup leitsymptomatik_kuerzel;
    @FXML
    TextArea leitsymptomatik;

    @FXML
    TextField hm_1;
    @FXML
    TextField hm_einheiten_1;

    @FXML
    TextField hm_2;
    @FXML
    TextField hm_einheiten_2;

    @FXML
    TextField hm_3;
    @FXML
    TextField hm_einheiten_3;

    @FXML
    TextField hm_ergaenzend;
    @FXML
    TextField hm_einheiten_ergaenzend;

    @FXML
    CheckBox therapieBericht;

    @FXML
    ToggleGroup hausbesuch;
SimpleBooleanProperty hb = new SimpleBooleanProperty();


    @FXML
    TextField therapieFrequenz;

    @FXML
    CheckBox dringlicherBedarf;
    SimpleBooleanProperty dringlich = new SimpleBooleanProperty();

    @FXML
    TextArea therapieZiele;

    @FXML
    TextField ik_Erbringer;


    @FXML
    public void initialize() {

        zuzahlung.getToggles()
                 .forEach(t -> t.setUserData(Zuzahlung.valueOf(((Node) t).getId()
                                                           .toUpperCase())));
        new ToggleGroupBinding<Zuzahlung>(zuzahlung, zuzahlungProperty);
        hb.bindBidirectional(hausbesuch.getToggles().get(0).selectedProperty());
        dringlich.bindBidirectional(dringlicherBedarf.selectedProperty());
       
    }

    @FXML
    private void setnewbefreiung() {
        hb.set(true);

    }

}
