package hmv;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Closeable;
import java.io.IOException;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import speichern.HmvSaver;
import javafx.scene.control.ButtonType;

final class HMVSpeichernListener implements ActionListener {

    private HmvSaver entwurf;
    private HmvSaver inEcht;
    private Closeable closeMe;


    public HMVSpeichernListener(HmvSaver entwurf, HmvSaver inEcht, Closeable closeMe) {
        this.entwurf = entwurf;
        this.inEcht = inEcht;
        this.closeMe = closeMe;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        boolean basstScho = Boolean.valueOf(e.getActionCommand());
        Hmv hmv = (Hmv) e.getSource();
        if (basstScho) {
            speichern(hmv);
            schliessen(closeMe);
        } else {
            beep();
            benutzerfragenwastun(hmv,closeMe);
        }

    }
    private void beep() {
        Toolkit.getDefaultToolkit()
               .beep();
    }

    private void schliessen(Closeable closeMe2) {
        try {
            closeMe2.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private void benutzerfragenwastun(Hmv hmv , Closeable closeMe2) {
        Alert alert = new Alert(AlertType.CONFIRMATION, " Als Entwurf speichern", ButtonType.YES, ButtonType.NO,
                ButtonType.CANCEL);
        ButtonType ergebnis = alert.showAndWait().orElse(ButtonType.CANCEL);

        if (ergebnis == ButtonType.YES) {
            entwurfspeichern(hmv);
            schliessen(closeMe2);
        } else if (ergebnis == ButtonType.NO) {
            schliessen(closeMe2);
        }

    }

    private void entwurfspeichern(Hmv hmv) {

        entwurf.save(hmv);


    }


    private void speichern(Hmv hmv) {
        inEcht.save(hmv);

    }
}
