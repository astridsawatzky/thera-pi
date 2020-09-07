package hmv;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.Optional;

import core.Arzt;
import core.Disziplin;
import core.Krankenkasse;
import core.VersichertenStatus;
import core.Zuzahlung;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import specs.Contracts;

public class Hmv13 {

    ActionListener speichernListener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            // do nothing, just don't blow up!

        }
    };

    public void setSpeichernListener(ActionListener speichernListener) {
        Contracts.require(speichernListener != null, "actionlistener must not be null");

        this.speichernListener = speichernListener;
    }

    @FXML
    ToggleGroup zuzahlung;
    private ObjectProperty<Zuzahlung> zuzahlungProperty = new SimpleObjectProperty<>();

    @FXML
    ComboBox<String> kostentraeger;

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
    ChoiceBox<VersichertenStatus> versichertenStatus;

    @FXML
    TextField betriebsstaettenNr;
    @FXML
    ComboBox<String> lebenslangeArztNr;
    @FXML
    DatePicker rezeptDatum;

    @FXML
    TextField erfasser;
    @FXML
    RadioButton kg;
    @FXML
    RadioButton et;
    @FXML
    RadioButton er;
    @FXML
    RadioButton lo;
    @FXML
    RadioButton po;

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
    ChoiceBox hm_1;
    @FXML
    TextField hm_einheiten_1;

    @FXML
    ChoiceBox hm_2;
    @FXML
    TextField hm_einheiten_2;

    @FXML
    ChoiceBox hm_3;
    @FXML
    TextField hm_einheiten_3;

    @FXML
    ChoiceBox hm_ergaenzend;
    @FXML
    TextField hm_einheiten_ergaenzend;

    @FXML
    CheckBox therapieBericht;

    @FXML
    ToggleGroup hausbesuch;
    // TODO:: dritte option
    ObjectProperty<Hausbesuch> hb = new SimpleObjectProperty<>();

    @FXML
    TextField therapieFrequenz;

    @FXML
    TextField dauer;

    @FXML
    ColorPicker kalenderfarbe;

    @FXML
    CheckBox dringlicherBedarf;
    SimpleBooleanProperty dringlich = new SimpleBooleanProperty();

    @FXML
    TextArea therapieZiele;

    @FXML
    Label ik_Erbringer;
    private Context context;

    public Hmv13(Context context) {
        this.context = context;
    }

    @FXML
    public void initialize() {
        zuzahlung.getToggles()
                 .forEach(t -> t.setUserData(Zuzahlung.valueOf(((Node) t).getId()
                                                                         .toUpperCase())));
        hausbesuch.getToggles()
                  .forEach(t -> t.setUserData(Hausbesuch.valueOf(((Node) t).getId()
                                                                           .toUpperCase())));
        new ToggleGroupBinding<Zuzahlung>(zuzahlung, zuzahlungProperty);
        new ToggleGroupBinding<Hausbesuch>(hausbesuch, hb);

        dringlich.bindBidirectional(dringlicherBedarf.selectedProperty());

        versichertenStatus.setConverter(new StringConverter<VersichertenStatus>() {

            @Override
            public String toString(VersichertenStatus status) {

                return status.getNummer() + " " + status;
            }

            @Override
            public VersichertenStatus fromString(String string) {
                return null;
            }
        });
        versichertenStatus.getItems()
                          .setAll(VersichertenStatus.values());
        versichertenStatus.setValue(context.patient.kv.getStatus());

        ik_Erbringer.setText(context.mandant.ikDigitString());
        erfasser.setText(context.user.anmeldename);
        enableNeededDisciplines();
        selectFirstDisciplin();
        name.setText(context.patient.nachname);
        vorname.setText(context.patient.vorname);
        geboren.setValue(context.patient.geburtstag);

        Optional<Krankenkasse> kk = context.patient.kv.getKk();
        String kostentraegerik = "";
        if (kk.isPresent()) {
            kostentraegerik = kk.get()
                                .getIk()
                                .digitString();
            kostentraeger.setValue(kk.get()
                                     .getName());
        }
        kostentraegerKennung.setText(kostentraegerik);
        versichertenNummer.setText(context.patient.kv.getVersicherungsnummer());

        if (context.patient.hatBefreiung(LocalDate.now())) {

            zuzahlungProperty.set(Zuzahlung.BEFREIT);
        }

        Optional<Arzt> optarzt = context.patient.hauptarzt;
        if (optarzt.isPresent()) {
            lebenslangeArztNr.setValue(optarzt.get()
                                              .getArztnummer().lanr);
            betriebsstaettenNr.setText(optarzt.get()
                                              .getBsnr());
        }

    }

    private void selectFirstDisciplin() {
        switch (context.disziplinen.iterator()
                                   .next()) {
        case KG:
            kg.setSelected(true);
            break;
        case PO:
            po.setSelected(true);
            break;
        case LO:
            lo.setSelected(true);
            break;
        case ER:
            er.setSelected(true);
            break;
        case ET:
            et.setSelected(true);
            break;

        default:
            // egal
            break;
        }
    }

    private void enableNeededDisciplines() {
        for (Disziplin disziplin : context.disziplinen) {
            switch (disziplin) {
            case KG:
                kg.setDisable(false);
                break;
            case PO:
                po.setDisable(false);
                break;
            case LO:
                lo.setDisable(false);
                break;
            case ER:
                er.setDisable(false);
                break;
            case ET:
                et.setDisable(false);
                break;

            default:
                // egal
                break;
            }

        }
    }

    @FXML
    private void setnewbefreiung() {

    }

    @FXML
    private void speichern() {

        markierungenAufheben();
        boolean allesOK = pruefenUndMarkierungenSetzen();

        String command = String.valueOf(allesOK);
        ActionEvent speichernEvent = new ActionEvent(this,ActionEvent.ACTION_PERFORMED, command );
        speichernListener.actionPerformed(speichernEvent);
    }

    private void beep() {
        Toolkit.getDefaultToolkit()
               .beep();
    }



    private boolean pruefenUndMarkierungenSetzen() {
        return mustnotbeempty(leitsymptomatik);
    }

    private boolean mustnotbeempty(Node node) {
        return !((TextArea) node).getText()
                                 .isEmpty();

    }

    private void markierungenAufheben() {
        // TODO Auto-generated method stub

    }

    @FXML

    private void abbrechen() {

    }

    @FXML

    private void hmrcheck() {
        // TODO Auto-generated method stub

    }



}
