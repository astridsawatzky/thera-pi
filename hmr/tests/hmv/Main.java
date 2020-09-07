package hmv;

import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.EnumSet;
import java.util.Optional;

import core.Adresse;
import core.Arzt;
import core.Befreiung;
import core.Disziplin;
import core.Krankenkasse;
import core.Krankenversicherung;
import core.Patient;
import core.VersichertenStatus;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import mandant.IK;
import mandant.Mandant;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("HMV13.fxml"));
            EnumSet<Disziplin> disziplinen = EnumSet.of(Disziplin.ER, Disziplin.KG);
            Patient patient = new Patient(new Adresse("", "hohle gasse 5", "12345", "Baumburg"));
            patient.nachname = "Lant";
            patient.vorname = "Simon";
            Krankenkasse kk = new KrankenkasseFactory().withIk(new IK("999999999"))
                                                       .withName("donotpay")
                                                       .build();
            Befreiung befreit = new Befreiung(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 12, 31));

            patient.kv = new Krankenversicherung(Optional.of(kk), "0815", VersichertenStatus.RENTNER, befreit);

            patient.geburtstag = LocalDate.of(1904, 2, 29);

            patient.hauptarzt = Optional.of(CoreTestDataFactory.createArztEisenbart());

            Context context = new Context(new Mandant("123456789", "test"), new User("bob"), disziplinen, patient);
            Hmv13 controller = new Hmv13(context);
            controller.setSpeichernListener(al);
            loader.setController(controller);

            double scaleFactor = 1;
            Scene scene = new Scene(loader.load(), 630 * scaleFactor, 900 * scaleFactor);
            Scale scale = new Scale(scaleFactor, scaleFactor, 0, 0);
            scene.getRoot()
                 .getTransforms()
                 .add(scale);
            primaryStage.setResizable(true);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    ActionListener al = new HMVSpeichernListener(new HmvSaver() {

        @Override
        public boolean save(Hmv13 hmv) {
            // TODO Auto-generated method stub entwurf
            return false;
        }
    }, new HmvSaver() {

        @Override
        public boolean save(Hmv13 hmv) {
            // TODO Auto-generated method stub in echt
            return false;
        }
    });
    /**
     *
     * if (allesOK) { speicherAnfrage(); } else {
     *
     * beep(); boolean antwort = benutzeranfrageEntwurfspeichern(); if (antwort) {
     * speichern_als_entwurf(); } else { nothing(); } }
     *
     */
}
