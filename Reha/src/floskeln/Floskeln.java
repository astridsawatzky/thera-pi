package floskeln;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingx.JXDialog;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTitledPanel;
import org.therapi.reha.patient.PatientMemoPanel;

import CommonTools.JCompTools;
import CommonTools.ini.INITool;
import CommonTools.ini.Settings;
import dialoge.DragWin;
import dialoge.PinPanel;
import environment.Path;
import events.RehaTPEvent;
import events.RehaTPEventClass;
import events.RehaTPEventListener;
import hauptFenster.Reha;
import systemEinstellungen.SystemConfig;
import umfeld.Betriebsumfeld;

public class Floskeln extends JXDialog
        implements ActionListener, MouseListener, WindowListener, KeyListener, RehaTPEventListener {
    /**
     *
     */
    private static final long serialVersionUID = 151598806426374786L;

    private JXTitledPanel jtp = null;
    private MouseAdapter mymouse = null;
    private PinPanel pinPanel = null;
    private JXPanel content = null;
    private RehaTPEventClass rtp = null;

    private JButton abfeuern = null;

    private JXTable tab = null;

    DefaultTableModel tblDataModel = null;
    PatientMemoPanel memopan = null;

    public Floskeln(JXFrame owner, String titel, PatientMemoPanel aktFocus) {
        super(owner, (JComponent) Reha.getThisFrame()
                                      .getGlassPane());
        this.setUndecorated(true);
        this.setName("Floskeln");

        this.jtp = new JXTitledPanel();
        this.jtp.setName("Floskeln");
        this.mymouse = new DragWin(this);
        this.jtp.addMouseListener(mymouse);
        this.jtp.addMouseMotionListener(mymouse);
        this.jtp.addMouseListener(this);
        this.jtp.setContentContainer(getContent());
        this.jtp.setTitleForeground(Color.WHITE);
        this.jtp.setTitle(titel);
        this.pinPanel = new PinPanel();
        this.pinPanel.getGruen()
                     .setVisible(false);
        this.pinPanel.setName("Floskeln");
        this.jtp.setRightDecoration(this.pinPanel);
        this.setContentPane(jtp);
        // this.setModal(true);
        this.setResizable(false);
        this.rtp = new RehaTPEventClass();
        this.rtp.addRehaTPEventListener(this);
        this.memopan = aktFocus;
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

    }

    private JXPanel getContent() {
        content = new JXPanel(new BorderLayout());
        content.add(new JScrollPane(), BorderLayout.CENTER);
        if ((Boolean) SystemConfig.hmPatientenWerkzeugDlgIni.get("ToolsDlgShowButton")) {
            abfeuern = new JButton("ausführen....");
            abfeuern.setActionCommand("abfeuern");
            abfeuern.addActionListener(this);
            content.add(abfeuern, BorderLayout.SOUTH);
        }
        tblDataModel = new DefaultTableModel();
        tblDataModel.setColumnIdentifiers(new String[] { "Floskel", "" });
        tab = new JXTable(tblDataModel);
        tab.getColumnModel()
           .getColumn(1)
           .setMinWidth(0);
        tab.getColumnModel()
           .getColumn(1)
           .setMaxWidth(0);
        tab.addMouseListener(this);
        tab.addKeyListener(this);
        tab.setEditable(false);
        JScrollPane scr = JCompTools.getTransparentScrollPane(tab);
        scr.validate();
        content.add(scr, BorderLayout.CENTER);

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    fuellen();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return null;
            }

        }.execute();
        return content;
    }

    private void fuellen() {
        Settings ini = INITool.openIni(Path.Instance.getProghome() + "ini/" + Betriebsumfeld.getAktIK() + "/", "floskeln.ini");
        int floskeln = Integer.parseInt(ini.getStringProperty("Floskeln", "FloskelAnzahl"));
        Vector<String> vec = new Vector<String>();
        for (int i = 0; i < floskeln; i++) {
            vec.clear();
            vec.add(ini.getStringProperty("Floskeln", "FloskelTitel" + Integer.toString(i + 1)));
            vec.add(ini.getStringProperty("Floskeln", "FloskelInhalt" + Integer.toString(i + 1)));
            tblDataModel.addRow((Vector<?>) vec.clone());
        }
        tab.repaint();
        if (tab.getRowCount() > Reha.instance.lastSelectedFloskel && Reha.instance.lastSelectedFloskel >= 0) {
            tab.setRowSelectionInterval(Reha.instance.lastSelectedFloskel, Reha.instance.lastSelectedFloskel);
        }
        tab.requestFocus();
    }

    /************************************/
    /************************************/
    private void auswerten() {

    }

    /************************************/
    /************************************/

    @Override
    public void rehaTPEventOccurred(RehaTPEvent evt) {
        try {
            if (evt.getDetails()[0] != null) {
                if (evt.getDetails()[0].equals(this.getName())) {
                    this.setVisible(false);
                    this.dispose();
                    memopan.setNewText("");
                }
            }
        } catch (NullPointerException ne) {

        }

    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == 10) {
            e.consume();
            Reha.instance.lastSelectedFloskel = tab.getSelectedRow();
            setVisible(false);
            auswerten();
            dispose();
            memopan.setNewText(String.valueOf(tab.getValueAt(tab.getSelectedRow(), 1)));
        } else if (e.getKeyCode() == 27) {
            setVisible(false);
            dispose();
            memopan.setNewText("");
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosing(WindowEvent e) {
    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getClickCount() == 2 && e.getButton() == 1) {
            e.consume();
            Reha.instance.lastSelectedFloskel = tab.getSelectedRow();
            setVisible(false);
            auswerten();
            dispose();
            memopan.setNewText(String.valueOf(tab.getValueAt(tab.getSelectedRow(), 1)));
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            String cmd = e.getActionCommand();
            if (cmd.equals("abfeuern")) {
                Reha.instance.lastSelectedFloskel = tab.getSelectedRow();
                setVisible(false);
                auswerten();
                dispose();
                memopan.setNewText(String.valueOf(tab.getValueAt(tab.getSelectedRow(), 1)));
            }
        } catch (Exception ex) {

        }
    }

}
