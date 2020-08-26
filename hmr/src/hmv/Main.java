package hmv;

import java.util.EnumSet;

import core.Disziplin;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import mandant.Mandant;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("HMV13.fxml"));
            EnumSet<Disziplin> disziplinen = EnumSet.of(Disziplin.ER, Disziplin.KG);
            Context context = new Context(new Mandant("123456789", "test"), new User("bob"), disziplinen);
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
