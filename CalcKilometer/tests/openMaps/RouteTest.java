package openMaps;

import static org.junit.Assert.*;

import org.junit.Test;

public class RouteTest {

    @Test
    public void testCOmposeURL() throws Exception {

     String expected= "https://api.openrouteservice.org/"
             + "v2/directions/"
             + "driving-car?"
             + "api_key=5b3ce3597851110001cf62486150c551e1b94445be33509314cfd064"
             + "&start=8.681495,49.41461"
             + "&end=8.687872,49.420318";


        Route rute  = new Route();
        rute.coordStart= new Coordinate("8.681495","49.41461");
     rute.coordEnd= new Coordinate("8.687872", "49.420318");
        assertEquals(expected, rute.composeURL("5b3ce3597851110001cf62486150c551e1b94445be33509314cfd064"));

    }

}
