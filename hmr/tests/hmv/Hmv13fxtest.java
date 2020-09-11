package hmv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import java.io.Closeable;
import java.io.IOException;

import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

public class Hmv13fxtest extends ApplicationTest implements Closeable {
    Context context = CoreTestDataFactory.createContext();
    Hmv hmvorig = CoreTestDataFactory.createHmv(context);
    private Hmv13 hmv13;

    @Override
    public void start(Stage primaryStage) throws Exception {
        hmv13 = new Hmv13(hmvorig, context, context.disziplinen);



        FXMLLoader loader = new FXMLLoader(hmv13.getClass()
                                                .getResource("HMV13.fxml"));
        loader.setController(hmv13);

        double scaleFactor = 1;
        Scene scene = new Scene(loader.load(), 630 * scaleFactor, 900 * scaleFactor);
        Scale scale = new Scale(scaleFactor, scaleFactor, 0, 0);
        scene.getRoot()
             .getTransforms()
             .add(scale);
        primaryStage.setResizable(true);
        primaryStage.setScene(scene);
        hmv13.setSpeichernListener(new HMVSpeichernListener(new HmvSaver() {

            @Override
            public boolean save(Hmv hmv) {
                // TODO Auto-generated method stub entwurf
                return false;
            }
        }, new HmvSaver() {

            @Override
            public boolean save(Hmv hmv) {
                // TODO Auto-generated method stub in echt
                return false;
            }
        }, this));

    }

    @Test
    public void testName() throws Exception {
        Object hmvfromgui = hmv13.toHmv();
        assertNotSame(hmvorig, hmvfromgui);
        assertEquals(hmvorig.toString(), hmvfromgui.toString());
    }

    @Override
    public void close() throws IOException {
        // it is taken care of by TestFx

    }

}
