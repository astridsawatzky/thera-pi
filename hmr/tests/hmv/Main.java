package hmv;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.Optional;

import core.Adresse;
import core.Arzt;
import core.Befreiung;
import core.Disziplin;
import core.Krankenkasse;
import core.Krankenversicherung;
import core.LANR;
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

            patient.kv = new Krankenversicherung(Optional.of(kk), "0815", VersichertenStatus.RENTNER,
                    befreit);

            patient.geburtstag = LocalDate.of(1904, 2, 29);

            Arzt eisenbart = new ArztFactory().withNachname("Eisenbart")
                                              .withArztnummer(new LANR("081500000"))
                                              .withBsnr("000008150")
                                              .build();

            patient.hauptarzt = Optional.of(eisenbart);

            Context context = new Context(new Mandant("123456789", "test"), new User("bob"), disziplinen, patient);
            loader.setController(new Hmv13(context));

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
}
