package terminKalender;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTitledPanel;

import systemEinstellungen.BehandlerSet;
import systemEinstellungen.BehandlerSets;

class SetWahl extends JDialog {

    private static final long serialVersionUID = 1L;
    private JPanel jContentPane = null; // @jve:decl-index=0:visual-constraint="505,22"
    private JPanel jPanelTasten = null;
    private String[] behandler;
    private String setname;
    private JScrollPane jScrollPane = null;
    private JList jList = null;
    private JButton jButton = null;
    private JButton jButton1 = null;
    String ret = "./.";
    private JList<String> jList1 = null;
    private int wahl;
    private TerminFenster eltern;

    /**
     * @param owner
     */
    SetWahl(TerminFenster xeltern) {
        super();
        eltern = xeltern;
        wahl = eltern.aktuellesSet();
        initialize();
    }

    /**
     * This method initializes this
     *
     * @return void
     */
    private void initialize() {
        this.setSize(293, 247);
        this.setPreferredSize(new Dimension(293, 247));
        this.setTitle("Terminset auswählen");
        this.setBackground(Color.WHITE);
        this.setContentPane(getJContentPane());
        this.setModal(true);
        this.jList1.setSelectedIndex(this.wahl);
        this.setUndecorated(true);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

    }

    /**
     * This method initializes jContentPane
     *
     * @return javax.swing.JPanel
     */
    private JPanel getJContentPane() {
        if (jContentPane == null) {
            jContentPane = new JXTitledPanel();
            JXPanel jcon = (JXPanel) ((JXTitledPanel) jContentPane).getContentContainer();
            ((JXTitledPanel) jContentPane).setTitle("Behandler - Set auswählen....");
            ((JXTitledPanel) jContentPane).setTitleForeground(Color.WHITE);
            jcon.setBackground(Color.WHITE);
            jcon.setLayout(new BorderLayout());
            jcon.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
            jcon.add(getJPanelTasten(), BorderLayout.SOUTH);
            jcon.add(getJScrollPane(), BorderLayout.CENTER);
        }
        return jContentPane;
    }

    /**
     * This method initializes jPanelTasten
     *
     * @return javax.swing.JPanel
     */
    private JPanel getJPanelTasten() {
        if (jPanelTasten == null) {
            jPanelTasten = new JPanel();
            jPanelTasten.setLayout(new FlowLayout());
            jPanelTasten.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0));
            jPanelTasten.setPreferredSize(new Dimension(0, 35));
            jPanelTasten.add(getJButton(), null);
            jPanelTasten.add(getJButton1(), null);
        }
        return jPanelTasten;
    }

    /**
     * This method initializes jScrollPane
     *
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getJScrollPane() {
        if (jScrollPane == null) {
            jScrollPane = new JScrollPane();
            jScrollPane.setBackground(Color.WHITE);
            jScrollPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            jScrollPane.setViewportView(getJList1());
        }
        return jScrollPane;
    }

    /**
     * This method initializes jList
     *
     * @return javax.swing.JList
     */

    /**
     * This method initializes jList
     *
     * @return javax.swing.JList
     */

    private void ListeFuellen(DefaultListModel<String> model) {
        for (BehandlerSet set : BehandlerSets.alleBehandlersets()) {
            model.addElement(set.getName());
        }
        return;
    }

    /**
     * This method initializes jButton
     *
     * @return javax.swing.JButton
     */
    private JButton getJButton() {
        if (jButton == null) {
            jButton = new JButton();
            jButton.setText("Ok");
            jButton.setPreferredSize(new Dimension(102, 20));
            // jButton.setIcon(new ImageIcon("C:/MeinWorkspace/pics/ok.gif"));

            jButton.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    DialogBeenden(jList1.getSelectedValue());
                }
            });
        }
        return jButton;
    }

    /**
     * This method initializes jButton1
     *
     * @return javax.swing.JButton
     */
    private JButton getJButton1() {
        if (jButton1 == null) {
            jButton1 = new JButton();
            jButton1.setPreferredSize(new Dimension(102, 20));
            jButton1.setText("Abbruch");
            // jButton1.setIcon(new ImageIcon("C:/MeinWorkspace/pics/nichtok.gif"));
            jButton1.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    DialogBeenden("./.");
                }
            });
        }
        return jButton1;
    }

    private void DialogBeenden(String setName) {
        eltern.setSwSetWahl(setName);
        this.ret = setName;
        this.dispose();
    }

    /**
     * This method initializes jList1
     *
     * @return javax.swing.JList
     */
    private JList getJList1() {
        final DefaultListModel model = new DefaultListModel();
        if (jList1 == null) {
            jList1 = new JList(model);
            jList1.addKeyListener(new java.awt.event.KeyAdapter() {
                @Override
                public void keyPressed(java.awt.event.KeyEvent e) {
                    if (e.getKeyCode() == 10) {
                        DialogBeenden(jList1.getSelectedValue());
                    }
                    if (e.getKeyCode() == 27) {
                        DialogBeenden("./.");
                    }
                }
            });
            jList1.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        DialogBeenden(jList1.getSelectedValue());
                    }

                }
            });
            ListeFuellen(model);
        }
        return jList1;
    }

    public void setBehandler(String[] behandler) {
        this.behandler = behandler;
    }

    public String[] getBehandler() {
        return behandler;
    }

    public void setSetname(String setname) {
        this.setname = setname;
    }

    public String getSetname() {
        return setname;
    }

} // @jve:decl-index=0:visual-constraint="103,28"
