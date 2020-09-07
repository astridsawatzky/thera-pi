package hmv;

import core.Arzt;
import core.LANR;

public class CoreTestDataFactory {

    public static Arzt createArztEisenbart() {
        return new ArztFactory().withNachname("Eisenbart")
                                          .withArztnummer(new LANR("081500000"))
                                          .withBsnr("000008150")
                                          .build();
    }



}
