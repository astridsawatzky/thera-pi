package hmv;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.scene.control.Alert.AlertType;

final class HMVSpeichernListener implements ActionListener {

    private HmvSaver entwurf;
    private HmvSaver inEcht;


    public HMVSpeichernListener(HmvSaver entwurf, HmvSaver inEcht) {
        this.entwurf = entwurf;
        this.inEcht = inEcht;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        boolean basstScho = Boolean.valueOf(e.getActionCommand());
        Hmv13 hmv = (Hmv13) e.getSource();
        Node source = (Node)hmv.dauer;
        if (basstScho) {
            speichern(hmv);
            schliessen(source);
        } else {
            benutzerfragenwastun(hmv,source);
        }

    }

    private void schliessen(Node source) {
        Stage stage  = (Stage) source.getScene().getWindow();
        stage.close();

    }

    private void benutzerfragenwastun(Hmv13 hmv , Node node) {
        Alert alert = new Alert(AlertType.CONFIRMATION, " Als Entwurf speichern", ButtonType.YES, ButtonType.NO,
                ButtonType.CANCEL);
        ButtonType ergebnis = alert.showAndWait().orElse(ButtonType.CANCEL);

        if (ergebnis == ButtonType.YES) {
            entwurfspeichern(hmv);
            schliessen(node);
        } else if (ergebnis == ButtonType.NO) {
            schliessen(node);
        }

    }

    private void entwurfspeichern(Hmv13 hmv) {

        entwurf.save(hmv);


    }


    private void speichern(Hmv13 hmv) {
        inEcht.save(hmv);

    }
}
