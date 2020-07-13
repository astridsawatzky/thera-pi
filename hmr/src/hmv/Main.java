package hmv;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.transform.Scale;
import javafx.fxml.FXMLLoader;


public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
             Parent root = FXMLLoader.load(getClass().getResource("HMV13.fxml"));
            double scaleFactor =1;
            Scene scene = new Scene(root,630*scaleFactor ,900*scaleFactor);
            Scale scale = new Scale(scaleFactor,scaleFactor,0,0);
            scene.getRoot().getTransforms().add(scale);
            primaryStage.setResizable(true);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
