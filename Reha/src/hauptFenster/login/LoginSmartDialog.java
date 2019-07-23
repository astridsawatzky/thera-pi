package hauptFenster.login;

import java.awt.Dimension;
import java.awt.Toolkit;

import org.jdesktop.swingx.JXFrame;

import dialoge.RehaSmartDialog;
import rehaContainer.RehaTP;
import systemTools.WinNum;

public class LoginSmartDialog extends RehaSmartDialog {

    public LoginSmartDialog(JXFrame owner, String name, boolean fullsize) {
        super(owner, name);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        RehaTP jtp = new LoginFrame("PasswortDialog" + WinNum.NeueNummer());

        jtp.setVisible(true);
        setAlwaysOnTop(false);
        if (fullsize) {
            setSize(Toolkit.getDefaultToolkit()
                           .getScreenSize());
        } else {
            setSize(new Dimension(700, 300));
        }
        setModal(true);

        setContentPanel(jtp.getContentContainer());
        setLocationRelativeTo(null);

    }

}
