package hauptFenster.login;

import rehaContainer.RehaTP;
import systemTools.PassWort;

class LoginFrame extends RehaTP {

    LoginFrame(String name) {
        super();
        setzeName(name);
        setBorder(null);
        setTitle("Passwort-Eingabe");
        setContentContainer(new PassWort().getPanel());
        getContentContainer().setName(name);
    }

}
