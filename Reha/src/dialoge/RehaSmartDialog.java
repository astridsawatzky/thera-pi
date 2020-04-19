package dialoge;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;

import org.jdesktop.swingx.JXDialog;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTitledPanel;

import events.RehaTPEvent;
import events.RehaTPEventClass;
import gui.Cursors;
import hauptFenster.Reha;
import systemTools.ListenerTools;

public class RehaSmartDialog extends JXDialog
        implements ISmartDialog, WindowListener, MouseMotionListener, MouseListener, KeyListener {

    private static final long serialVersionUID = -8192593796486061674L;

    private JXPanel jContentPane = null;

    private int clickX;
    private int clickY;


    private boolean insize;

    private int[] waagrecht = { 0, 0 };
    private int[] senkrecht = { 0, 0 };
    private int[] orgbounds = { 0, 0 };
    private int hilfsint = 0;
    private int sizeart;
    private JXTitledPanel jtp = null;
    private RehaTPEventClass xEvent;
    public PinPanel pinPanel = null;
    /**
     * @param
     */
    public RehaSmartDialog(JXFrame owner, String name) {
        // super();

        super(owner, (JComponent) Reha.getThisFrame()
                                      .getGlassPane());
        setModalityType(ModalityType.DOCUMENT_MODAL);
        this.setName(name);
        // this.setAlwaysOnTop(true);
        setName(name);

        initialize();

        // KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);

        try {

            UIManager.setLookAndFeel(Reha.aktLookAndFeel);

        } catch (ClassNotFoundException e1) {

            e1.printStackTrace();
        } catch (InstantiationException e1) {

            e1.printStackTrace();
        } catch (IllegalAccessException e1) {

            e1.printStackTrace();

        } catch (UnsupportedLookAndFeelException e1) {

            e1.printStackTrace();
        }
        final ActionListener listener = new ActionListener() {
            @Override
            public final void actionPerformed(final ActionEvent e) {
                if (hasFocus()) {
                    setVisible(false);
                    dispose();
                }
            }

        };
        /*
         * final KeyStroke keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0,
         * true); getRootPane().registerKeyboardAction(listener, keyStroke,
         * JComponent.WHEN_IN_FOCUSED_WINDOW);
         */
        final KeyStroke keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, true);
        getRootPane().registerKeyboardAction(listener, keyStroke, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

    }

    /**
     * This method initializes this
     *
     * @return void
     */
    private void initialize() {
        this.setUndecorated(true);
        this.setSize(700, 500);
        this.setContentPane(getJContentPane());
        // thisClass = this;
        this.setModal(false);
        this.setResizable(true);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        // this.addKeyListener(this);
        this.addWindowListener(this);
        getRootPane().addKeyListener(this);
        addKeyListener(this);
        // vorher eingeschaltet //xEvent = new RehaTPEventClass();

    }

    /**
     * This method initializes jContentPane
     *
     * @return javax.swing.JPanel
     */
    private JPanel getJContentPane() {
        if (jContentPane == null) {
            jContentPane = new JXPanel();
            jContentPane.setLayout(new BorderLayout());
            jContentPane.setBorder(null);
            jContentPane.add(getJXTitledPanel(), BorderLayout.CENTER);
        }
        return jContentPane;
    }

    /**
     * This method initializes JXTitledPanel
     *
     * @return org.jdesktop.swingx.JXTitledPanel
     */
    private JXTitledPanel getJXTitledPanel() {
        if (jtp == null) {
            jtp = new JXTitledPanel();
            // jtp.addKeyListener(this);
            jtp.setTitleForeground(Color.WHITE);
            jtp.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
            jtp.addMouseListener(this);
            jtp.addMouseMotionListener(this);
            jtp.addKeyListener(this);
        }
        return jtp;
    }

    @Override
    public JXTitledPanel getSmartTitledPanel() {
        return jtp;
    }

    private boolean WertZwischen(int punkt, int kleinerWert, int grosserWert) {
        if (punkt < kleinerWert) {
            return false;
        }
        if (punkt > grosserWert) {
            return false;
        }
        return true;
    }

    @Override
    public JXTitledPanel getTitledPanel() {
        return jtp;
    }

    @Override
    public void setContentPanel(Container cont) {
        this.jtp.setContentContainer(cont);
        // this.jtp.setRightDecoration(new PinPanel());
        this.jtp.setPreferredSize(cont.getPreferredSize());
        this.setName(this.jtp.getContentContainer()
                             .getName());

    }

    public Container getContentPanel() {
        return this.jtp.getContentContainer();
    }

    @Override
    public void aktiviereIcon() {
        if (pinPanel != null) {
            pinPanel.SetzeAktivButton(true);
        }
    }

    @Override
    public void deaktiviereIcon() {
        if (pinPanel != null) {
            pinPanel.SetzeAktivButton(false);
        }
    }

    @Override
    public void setPinPanel(PinPanel pinPanel) {
        this.pinPanel = pinPanel;
        this.jtp.setRightDecoration(this.pinPanel);
    }

    @Override
    public PinPanel getPinPanel() {
        return this.pinPanel;
    }

    @Override
    public void ListenerSchliessen() {
        //// System.out.println("In ListenerSchließen - Basisklasse");
        if (xEvent != null) {
            xEvent.removeRehaTPEventListener(this);
            xEvent = null;
            ListenerTools.removeListeners(this);
            this.removeWindowListener(this);
            getRootPane().removeKeyListener(this);
        }
    }

    public void Schliessen() {
        //// System.out.println("In Schliessen-Funktion der basisklasse");
        try {
            ListenerTools.removeListeners(this);
            this.removeWindowListener(this);
            getRootPane().removeKeyListener(this);
            // Vorher einegeschaltet //xEvent.removeRehaTPEventListener(this);
        } catch (java.lang.NullPointerException ex) {

        }
        this.dispose();

    }

    @Override
    public void rehaTPEventOccurred(RehaTPEvent evt) {
        //// System.out.println("In SmartDialog evt = "+evt);
        try {
            if (evt.getDetails()[1].equals("ROT")) {
                //// System.out.println("RehaSmartDialog hat EventListener gelöscht");
                xEvent.removeRehaTPEventListener(this);
                xEvent = null;
                this.dispose();
            }

        } catch (java.lang.NullPointerException e) {
            //// System.out.println("Event = "+evt.getDetails()[1]);
            //// System.out.println("In RehaSmartDialog - Null Pointerexception = "+e);

        }
    }

    @Override
    public void windowActivated(java.awt.event.WindowEvent e) {
        // Reha.instance.shiftLabel.setText("Dialog -Focus da");
        if (pinPanel != null) {
            jtp.getContentContainer()
               .requestFocus();
            pinPanel.SetzeAktivButton(true);
        }
    }

    @Override
    public void windowClosed(WindowEvent arg0) {

        ListenerSchliessen();
        this.removeWindowListener(this);
        // System.out.println("Basisklasse wird geschlossen - "+getName()+" IgnoreReturn
        // = "+ignorereturn);

    }

    @Override
    public void windowClosing(WindowEvent arg0) {
        // System.out.println("In Closing der Elternklasse "+arg0);

    }

    @Override
    public void windowDeactivated(java.awt.event.WindowEvent e) {
        // Reha.instance.shiftLabel.setText("Dialog -Focus weg");
        if (pinPanel != null) {
            pinPanel.SetzeAktivButton(false);
        }
    }

    @Override
    public void windowDeiconified(WindowEvent arg0) {

    }

    @Override
    public void windowIconified(WindowEvent arg0) {

    }

    @Override
    public void windowOpened(WindowEvent arg0) {

    }

    @Override
    public void setIgnoreReturn(boolean ignore) {

    }

    @Override
    public void mouseClicked(MouseEvent arg0) {
        //// System.out.println("In Click Klick Y bei "+arg0);

    }

    @Override
    public void mouseEntered(MouseEvent arg0) {

    }

    @Override
    public void mouseExited(MouseEvent arg0) {

    }

    public void setClicks(int x, int y) {
        clickY = x;
        clickX = y;
    }

    @Override
    public void mousePressed(java.awt.event.MouseEvent e) {
        //// System.out.println("Klick bei "+e.getY());
        if (e.getY() <= 25) {
            clickY = e.getY();
            clickX = e.getX();
            hilfsint = getWidth() / 2;
            waagrecht[0] = hilfsint - 15;
            waagrecht[1] = hilfsint + 15;
            hilfsint = getHeight() / 2;
            senkrecht[0] = hilfsint - 15;
            senkrecht[1] = hilfsint + 15;
        }
    }

    @Override
    public void mouseReleased(java.awt.event.MouseEvent e) {
        // final java.awt.event.MouseEvent ex = e;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                setCursor(Cursors.cdefault);
                clickX = -1;
                clickY = -1;
                orgbounds[0] = -1;
                orgbounds[1] = -1;
                insize = false;
                setCursor(Cursors.cdefault);
                hilfsint = getWidth() / 2;
                waagrecht[0] = hilfsint - 15;
                waagrecht[1] = hilfsint + 15;
                hilfsint = getHeight() / 2;
                senkrecht[0] = hilfsint - 15;
                senkrecht[1] = hilfsint + 15;
            }
        });

    }

    @Override
    public void mouseDragged(java.awt.event.MouseEvent e) {
        // int center = (int) getSize().getHeight();
        //// System.out.println("in Mousedragged");
        //// System.out.println("Insize = "+insize+" clickY="+clickY);
        //// System.out.println("Klick Y bei "+e.getY());
        // clickX = e.getX();
        if (!insize && clickY > 0) {
            // RehaSmartDialog.thisClass.getLocationOnScreen();
            //// System.out.println("in Mousedragged");
            final java.awt.event.MouseEvent ex = e;
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    setCursor(Cursors.cmove);
                    setLocation(ex.getXOnScreen() - clickX, ex.getYOnScreen() - clickY);
                }
            });

        } else if (insize) {
            final java.awt.event.MouseEvent ex = e;
//            SwingUtilities.invokeLater(new Runnable(){
//                public  void run(){

            Dimension dim = getSize();
            int oX = ex.getXOnScreen();
            int oY = ex.getYOnScreen();
            for (int i = 0; i < 1; i++) {
                if (sizeart == 1) { // nord-west
                    dim.width = (oX > orgbounds[0] ? dim.width - (oX - orgbounds[0]) : dim.width + (orgbounds[0] - oX));
                    dim.height = (oY > orgbounds[1] ? dim.height - (oY - orgbounds[1])
                            : dim.height + (orgbounds[1] - oY));
                    dim.width = (dim.width < 185 ? 185 : dim.width);
                    dim.height = (dim.height < 125 ? 125 : dim.height);
                    orgbounds[0] = oX;
                    orgbounds[1] = oY;
                    setSize(dim);
                    setLocation(ex.getXOnScreen(), ex.getYOnScreen());
                    setCursor(Cursors.cnwsize);
                    break;
                }
                if (sizeart == 2) { // nord-ost
                    dim.width = (oX > orgbounds[0] ? dim.width + (oX - orgbounds[0]) : dim.width - (orgbounds[0] - oX));
                    dim.height = (oY > orgbounds[1] ? dim.height - (oY - orgbounds[1])
                            : dim.height + (orgbounds[1] - oY));
                    dim.width = (dim.width < 185 ? 185 : dim.width);
                    dim.height = (dim.height < 125 ? 125 : dim.height);
                    orgbounds[0] = oX;
                    orgbounds[1] = oY;
                    setSize(dim);
                    setLocation(ex.getXOnScreen() - dim.width, ex.getYOnScreen());
                    setCursor(Cursors.cnesize);
                    break;
                }
                if (sizeart == 3) { // nord
                    dim.height = (oY > orgbounds[1] ? dim.height - (oY - orgbounds[1])
                            : dim.height + (orgbounds[1] - oY));
                    dim.width = (dim.width < 185 ? 185 : dim.width);
                    dim.height = (dim.height < 125 ? 125 : dim.height);
                    orgbounds[0] = oX;
                    orgbounds[1] = oY;
                    setSize(dim);
                    setLocation(ex.getXOnScreen() - ex.getX(), ex.getYOnScreen());
                    setCursor(Cursors.cnsize);
                    break;
                }
                if (sizeart == 4) { // süd-west
                    dim.width = (oX > orgbounds[0] ? dim.width - (oX - orgbounds[0]) : dim.width + (orgbounds[0] - oX));
                    dim.height = (oY > orgbounds[1] ? dim.height + (oY - orgbounds[1])
                            : dim.height - (orgbounds[1] - oY));
                    dim.width = (dim.width < 185 ? 185 : dim.width);
                    dim.height = (dim.height < 125 ? 125 : dim.height);
                    orgbounds[0] = oX;
                    orgbounds[1] = oY;
                    setSize(dim);
                    setLocation(ex.getXOnScreen(), ex.getYOnScreen() - dim.height);
                    setCursor(Cursors.cswsize);
                    break;
                }
                if (sizeart == 5) { // west
                    dim.width = (oX > orgbounds[0] ? dim.width - (oX - orgbounds[0]) : dim.width + (orgbounds[0] - oX));
                    dim.width = (dim.width < 185 ? 185 : dim.width);
                    dim.height = (dim.height < 125 ? 125 : dim.height);
                    orgbounds[0] = oX;
                    orgbounds[1] = oY;
                    setSize(dim);
                    setLocation(ex.getXOnScreen(), ex.getYOnScreen() - ex.getY());
                    setCursor(Cursors.cwsize);
                    break;
                }
                if (sizeart == 6) { // süd-ost
                    dim.width = (oX > orgbounds[0] ? dim.width + (oX - orgbounds[0]) : dim.width - (orgbounds[0] - oX));
                    dim.height = (oY > orgbounds[1] ? dim.height + (oY - orgbounds[1])
                            : dim.height - (orgbounds[1] - oY));
                    dim.width = (dim.width < 185 ? 185 : dim.width);
                    dim.height = (dim.height < 125 ? 125 : dim.height);
                    orgbounds[0] = oX;
                    orgbounds[1] = oY;
                    setSize(dim);
                    setLocation(ex.getXOnScreen() - dim.width, ex.getYOnScreen() - dim.height);
                    setCursor(Cursors.cwsize);
                    break;
                }
                if (sizeart == 7) { // süd
                    dim.height = (oY > orgbounds[1] ? dim.height + (oY - orgbounds[1])
                            : dim.height - (orgbounds[1] - oY));
                    dim.width = (dim.width < 185 ? 185 : dim.width);
                    dim.height = (dim.height < 125 ? 125 : dim.height);
                    orgbounds[0] = oX;
                    orgbounds[1] = oY;
                    setSize(dim);
                    setLocation(ex.getXOnScreen() - ex.getX(), ex.getYOnScreen() - dim.height);
                    setCursor(Cursors.cssize);
                    break;
                }
                if (sizeart == 8) { // ost
                    dim.width = (oX > orgbounds[0] ? dim.width + (oX - orgbounds[0]) : dim.width - (orgbounds[0] - oX));
                    dim.width = (dim.width < 185 ? 185 : dim.width);
                    dim.height = (dim.height < 125 ? 125 : dim.height);
                    orgbounds[0] = oX;
                    orgbounds[1] = oY;
                    setSize(dim);
                    setLocation(ex.getXOnScreen() - ex.getX(), ex.getYOnScreen() - ex.getY());
                    setCursor(Cursors.cesize);
                    break;
                }

                insize = false;
                setCursor(Cursors.cdefault);
            }
            // }
            // });

        } else {

            insize = false;
            setCursor(Cursors.cdefault);
        }
    }

    @Override
    public void mouseMoved(java.awt.event.MouseEvent e) {
        for (int i = 0; i < 1; i++) {
            sizeart = -1;
            setCursor(Cursors.cdefault);
            if ((e.getX() <= 4 && e.getY() <= 4)) { // nord-west
                insize = true;
                sizeart = 1;
                orgbounds[0] = e.getXOnScreen();
                orgbounds[1] = e.getYOnScreen();
                setCursor(Cursors.cnwsize);
                break;
            }
            if ((e.getX() >= (((JComponent) e.getSource()).getWidth() - 4)) && e.getY() <= 4) {// nord-ost
                insize = true;
                sizeart = 2;
                orgbounds[0] = e.getXOnScreen();
                orgbounds[1] = e.getYOnScreen();
                setCursor(Cursors.cnesize);
                break;
            }
            if (e.getY() <= 6 && WertZwischen(e.getX(), waagrecht[0], waagrecht[1])) {// nord
                insize = true;
                sizeart = 3;
                orgbounds[0] = e.getXOnScreen();
                orgbounds[1] = e.getYOnScreen();
                setCursor(Cursors.cnsize);
                break;
            }
            if ((e.getX() <= 4 && e.getY() >= (((JComponent) e.getSource()).getHeight() - 4))) { // s�d-west
                insize = true;
                sizeart = 4;
                orgbounds[0] = e.getXOnScreen();
                orgbounds[1] = e.getYOnScreen();
                setCursor(Cursors.cswsize);
                break;
            }
            if ((e.getX() <= 6) && WertZwischen(e.getY(), senkrecht[0], senkrecht[1])) { // west
                insize = true;
                sizeart = 5;
                orgbounds[0] = e.getXOnScreen();
                orgbounds[1] = e.getYOnScreen();
                setCursor(Cursors.cwsize);
                break;
            }
            if ((e.getX() >= (((JComponent) e.getSource()).getWidth() - 4)) && // s�d-ost
                    e.getY() >= (((JComponent) e.getSource()).getHeight() - 4)) {
                insize = true;
                sizeart = 6;
                orgbounds[0] = e.getXOnScreen();
                orgbounds[1] = e.getYOnScreen();
                setCursor(Cursors.csesize);
                break;
            }
            if (e.getY() >= (((JComponent) e.getSource()).getHeight() - 4)
                    && WertZwischen(e.getX(), waagrecht[0], waagrecht[1])) { // s�d
                insize = true;
                sizeart = 7;
                orgbounds[0] = e.getXOnScreen();
                orgbounds[1] = e.getYOnScreen();
                setCursor(Cursors.cssize);
                break;
            }
            if (e.getX() >= (((JComponent) e.getSource()).getWidth() - 6)
                    && WertZwischen(e.getY(), senkrecht[0], senkrecht[1])) { // ost
                insize = true;
                sizeart = 8;
                orgbounds[0] = e.getXOnScreen();
                orgbounds[1] = e.getYOnScreen();
                setCursor(Cursors.cesize);
                break;
            }

            insize = false;
            sizeart = -1;
            setCursor(Cursors.cdefault);

        }
    }

    @Override
    public void keyPressed(KeyEvent arg0) {

        //// System.out.println("SmartDialog Pressed "+arg0.getKeyCode());
    }

    @Override
    public void keyReleased(KeyEvent arg0) {

        //// System.out.println("SmartDialog Released "+arg0);
    }

    @Override
    public void keyTyped(KeyEvent arg0) {

        //// System.out.println("SmartDialog Typed "+arg0);
    }

} // @jve:decl-index=0:visual-constraint="387,36"
