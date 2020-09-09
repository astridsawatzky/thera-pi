package hmv;

import static org.junit.Assert.*;

import org.junit.Test;

import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

public class Hmv13Test {

    @Test
    public void returnedHmvEqualsOriginal() throws Exception {

        Context context = CoreTestDataFactory.createContext();
        Hmv hmvorig = CoreTestDataFactory.createHmv(context);





        Hmv13 hmvgui = new Hmv13(hmvorig, context, null);

        Hmv hmvfromgui = hmvgui.toHmv();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("HMV13.fxml"));
Main.startHMV13(new Stage(), loader, hmvgui);

        assertNotSame(hmvorig, hmvfromgui);
        assertEquals(hmvorig.toString(), hmvfromgui.toString());

    }





}
