package opRgaf;

import java.util.Collections;
import java.util.List;

import environment.Path;
import mandant.IK;

public class OffenePostenBuchenTest {

    public static void main(String[] args) {
        OpRgAfIni iniOpRgAf = new OpRgAfIni(Path.Instance.getProghome(), "ini/", "123456789" , "oprgaf.ini");;
        List<OffenePosten> offenePostenListe = Collections.emptyList();
        OffenePostenBuchen opbn  = new OffenePostenBuchen(iniOpRgAf, new IK("123456789"), offenePostenListe);
       TestFrame frame = new TestFrame(opbn);
       frame.showme();
    }

}
