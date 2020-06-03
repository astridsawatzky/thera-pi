package core;

import static org.junit.Assert.assertFalse;

import java.util.List;

import org.junit.Test;

import mandant.Mandant;

public class RgafFakturaDtoTest {

    @Test
    public void testall() throws Exception {
        Mandant m = new mandant.Mandant("123456789", "blabla");
       List<RgafFaktura> result = new RgafFakturaDto(m).all();
       assertFalse(result.isEmpty());
    }
}
