package hilfsFenster;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

import org.jdesktop.swingx.JXDialog;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTitledPanel;

import com.jgoodies.forms.layout.FormLayout;

import dialoge.DragWin;
import dialoge.PinPanel;
import events.RehaTPEvent;
import events.RehaTPEventClass;
import events.RehaTPEventListener;
import hauptFenster.Reha;
import krankenKasse.KasseNeuanlage;

public class MlaKassenChecker extends JXDialog implements RehaTPEventListener {
    /**
     * 
     */
    private static final long serialVersionUID = 5920997837364265557L;
    private JXTitledPanel jtp = null;
    private MouseAdapter mymouse = null;
    private PinPanel pinPanel = null;
    private JXPanel content = null;
    private RehaTPEventClass rtp = null;
    // private JRtaTextField[] tfs = {null,null};
    // private JButton[] buts = {null,null};
    boolean neu;
    JLabel labKurz = null;
    JLabel labLang = null;
    ActionListener al = null;
    Object eltern = null;

    public MlaKassenChecker(JXFrame owner, Object xeltern) {
        super(owner, (JComponent) Reha.getThisFrame()
                                      .getGlassPane());
        installListener();
        this.setUndecorated(true);
        this.setName("KuerzelDlg");
        if (xeltern instanceof KasseNeuanlage) {
            this.eltern = xeltern;
        } else {
            this.eltern = xeltern;
        }
        // this.neu = xneu;
        this.jtp = new JXTitledPanel();
        this.jtp.setName("Checker");
        this.mymouse = new DragWin(this);
        this.jtp.addMouseListener(mymouse);
        this.jtp.addMouseMotionListener(mymouse);
        this.jtp.setContentContainer(getContent());
        this.jtp.setTitleForeground(Color.WHITE);
        this.jtp.setTitle("Checker");
        this.pinPanel = new PinPanel();
        this.pinPanel.getGruen()
                     .setVisible(false);
        this.pinPanel.setName("Checker");
        this.jtp.setRightDecoration(this.pinPanel);
        this.setContentPane(jtp);
        this.setModal(true);
        this.setResizable(false);
        this.rtp = new RehaTPEventClass();
        this.rtp.addRehaTPEventListener(this);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

    }

    private JXPanel getContent() {
        String xwerte = "fill:0:grow(0.5),100dlu,5dlu,120dlu,fill:0:grow(0.5)";
        String ywerte = "fill:0:grow(0.5),p,5dlu,p,fill:0:grow(0.5),p";
        FormLayout lay = new FormLayout(xwerte, ywerte);
        // CellConstraints cc = new CellConstraints();
        content = new JXPanel();
        content.setBackground(Color.WHITE);
        content.setLayout(lay);
        return content;
    }

    private void installListener() {
        al = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                String cmd = arg0.getActionCommand();
                if (cmd.equals("speichern")) {
                    // doSpeichern();
                    return;
                }
                if (cmd.equals("abbrechen")) {
                    // FensterSchliessen("dieses");
                    return;
                }
            }
        };
    }

    @Override
    public void rehaTPEventOccurred(RehaTPEvent evt) {
        FensterSchliessen("KassenCheker");
    }

    public void FensterSchliessen(String welches) {
        this.jtp.removeMouseListener(this.mymouse);
        this.jtp.removeMouseMotionListener(this.mymouse);
        this.mymouse = null;
        if (this.rtp != null) {
            this.rtp.removeRehaTPEventListener(this);
            this.rtp = null;
        }
        setVisible(false);
        this.dispose();
    }

}
