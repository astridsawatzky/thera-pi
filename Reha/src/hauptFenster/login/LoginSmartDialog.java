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
        RehaTP jtp = new LoginFrame( "PasswortDialog"+WinNum.NeueNummer());
        
        
        jtp.setVisible(true);
        setAlwaysOnTop(false);
        if(fullsize){
                Dimension ssize = Toolkit.getDefaultToolkit().getScreenSize();
                setSize(new Dimension(ssize.width,ssize.height));
        }else{
                setSize(new Dimension(700,300));
        }
        setModal(true);

        setContentPanel(jtp.getContentContainer());
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        //Calculate the frame location
        int x = (screenSize.width - getWidth()) / 2;
        int y = (screenSize.height - getHeight()) / 2;
        setLocation(x, y);
       
       
    }

}
