package openMaps;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class CoordinateTest {


    @Test
    public void valuesAreCutTo5Decimals() throws Exception {

        Coordinate coord = new Coordinate("1234.12345000","1.12345999");
        Coordinate coord2 = new Coordinate("1234.12345999","1.12345");
        assertEquals(coord,coord2);
        assertEquals(coord.hashCode(),coord2.hashCode());

    }



}
