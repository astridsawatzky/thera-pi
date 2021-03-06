package patientenFenster;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import CommonTools.Colors;
import CommonTools.JCompTools;
import CommonTools.JRtaTextField;
import CommonTools.SqlInfo;
import commonData.ArztVec;
import dialoge.PinPanel;
import dialoge.RehaSmartDialog;
import events.RehaTPEvent;
import events.RehaTPEventClass;
import hauptFenster.Reha;

public class ArztAuswahl extends RehaSmartDialog {

    private static final long serialVersionUID = -3341922213135473923L;
    public JFormattedTextField tf = null;
    String suchkrit = "";
    String suchid = "";
    public JXTable arztwahltbl = null;
    public JButton neuarzt = null;
    public JButton suchearzt = null;
    public JButton uebernahmearzt = null;
    public JButton abbrechenarzt = null;
    public MyArztWahlModel arztwahlmod = null;
    public JRtaTextField[] elterntfs;
    public Container dummyPan = null;
    public Container dummyArzt = null;
    JXPanel content = null;
    ArztNeuKurz ank = null;
    public JXPanel grundPanel = null;
    public String arztbisher;
    private ArztVec myArzt = null;
    final int cNachname = 0, cVorname = 1, cStrasse = 2, cOrt = 3, cArztnum = 4, cBs = 5, cId = 6;

    private RehaTPEventClass rtp = null;
    private static final Logger LOGGER = LoggerFactory.getLogger(ArztAuswahl.class);;


    public ArztAuswahl(JXFrame owner, String name, String[] suchegleichnach, JRtaTextField[] elterntf, String arzt) {
        super(owner, name);
        setSize(550, 350);
        this.suchkrit = suchegleichnach[0].split(" - ")[0];
        this.suchid = suchegleichnach[1];
        this.elterntfs = elterntf;
        this.arztbisher = arzt.split(" - ")[0];
        super.getSmartTitledPanel().setTitleForeground(Color.WHITE);
        super.getSmartTitledPanel().setTitle("Arzt auswählen");
        this.setName("ArztKurz");

        pinPanel = new PinPanel();
        pinPanel.setName("ArztKurz");
        pinPanel.getGruen()
                .setVisible(false);
        this.setPinPanel(pinPanel);
        rtp = new RehaTPEventClass();
        rtp.addRehaTPEventListener(this);
        myArzt = new ArztVec();
        if (suchid.length() > 0) {
            myArzt.init(suchid);
        } else {
            myArzt.createEmptyVec();
        }

        grundPanel = new JXPanel(new BorderLayout());

        grundPanel.setBackgroundPainter(Reha.instance.compoundPainter.get("ArztAuswahl"));
        content = getAuswahl();
        grundPanel.add(content, BorderLayout.CENTER);
        getSmartTitledPanel().setContentContainer(grundPanel);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.ALT_MASK);
                ((JComponent) getContentPanel()).getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                                                .put(stroke, "doSuchen");
                ((JComponent) getContentPanel()).getActionMap()
                                                .put("doSuchen", new ArztWahlAction());
            }
        });
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                setzeFocus();
            }
        });
    }

    private void setzeFocus() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                tf.requestFocus();
            }
        });
    }

    @Override
    public void rehaTPEventOccurred(RehaTPEvent evt) {
        try {
            if (evt.getDetails()[0] != null) {
                if (evt.getDetails()[0].equals(this.getName())) {
                    doAbbrechen();
                    if (rtp != null) {
                        this.setVisible(false);
                        rtp.removeRehaTPEventListener(this);
                        rtp = null;
                        pinPanel = null;
                        this.dispose();
                        super.dispose();
                    }
                }
            }
        } catch (NullPointerException ne) {

            LOGGER.error("something bad happens here",ne);
        }
    }

    private JXPanel getAuswahl() {
        JXPanel jpan = new JXPanel();
        jpan.setOpaque(false);
        jpan.setBackground(Color.WHITE);
        FormLayout lay = new FormLayout("5dlu,p,3dlu,60dlu,3dlu,40dlu,fill:0:grow(1.00),5dlu",
                "3dlu,p,3dlu,150dlu:g,5dlu");
        CellConstraints cc = new CellConstraints();
        jpan.setLayout(lay);
        jpan.add(new JLabel("Arzt finden:"), cc.xy(2, 2));
        tf = new JFormattedTextField();
        tf.setText(this.suchkrit);
        tf.setName("suchfeld");
        KeyListener akl = new ArztListener();
        tf.addKeyListener(akl);
        jpan.add(tf, cc.xy(4, 2));
        /************************/
        FormLayout lay2 = new FormLayout(
                "fill:0:grow(0.25),2dlu,fill:0:grow(0.25),2dlu,fill:0:grow(0.25),2dlu,fill:0:grow(0.25)", "p");
        CellConstraints cc2 = new CellConstraints();
        JXPanel neupan = new JXPanel();
        neupan.setOpaque(false);
        neupan.setLayout(lay2);
        neuarzt = new JButton("neu");
        neuarzt.setToolTipText("neuen Arzt anlegen");
        neuarzt.setName("neuarzt");
        neuarzt.setMnemonic(KeyEvent.VK_N);
        neuarzt.addKeyListener(akl);
        neuarzt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                neuAnlageArzt();
            }
        });
        neupan.add(neuarzt, cc2.xy(1, 1));
        suchearzt = new JButton("suchen");
        suchearzt.setToolTipText("suche Arzt");
        suchearzt.setName("suchearzt");
        suchearzt.setMnemonic(KeyEvent.VK_S);
        suchearzt.addKeyListener(akl);
        suchearzt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fuelleTabelle(tf.getText()
                                .trim());
                tf.requestFocus();
            }
        });
        neupan.add(suchearzt, cc2.xy(3, 1));
        uebernahmearzt = new JButton("übernahme");
        uebernahmearzt.setToolTipText("ausgewählten Arzt übernehmen");
        uebernahmearzt.setName("suchearzt");
        uebernahmearzt.addKeyListener(akl);
        uebernahmearzt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                werteUebergeben();
            }
        });
        neupan.add(uebernahmearzt, cc2.xy(5, 1));

        abbrechenarzt = new JButton("abbrechen");
        abbrechenarzt.setToolTipText("Arztauswahl abbrechen");
        abbrechenarzt.setName("suchearzt");
        abbrechenarzt.setMnemonic(KeyEvent.VK_A);
        abbrechenarzt.addKeyListener(akl);
        abbrechenarzt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doAbbrechen();
            }
        });
        neupan.add(abbrechenarzt, cc2.xy(7, 1));
        neupan.validate();
        jpan.add(neupan, cc.xyw(6, 2, 2));
        arztwahlmod = new MyArztWahlModel();
        String[] column = { "Name", "Vorname", "Strasse", "Ort", "LANR", "BSNR", "" };
        arztwahlmod.setColumnIdentifiers(column);
        arztwahltbl = new JXTable(arztwahlmod);
        arztwahltbl.addKeyListener(akl);
        arztwahltbl.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                if (arg0.getClickCount() == 2) {
                    werteUebergeben();
                }
            }
        });
        arztwahltbl.setName("arzttabelle");
        arztwahltbl.setHighlighters(HighlighterFactory.createSimpleStriping(Colors.Blue.alpha(0.1f)));
        arztwahltbl.setDoubleBuffered(true);
        arztwahltbl.setEditable(false);
        arztwahltbl.setSortable(true);
        arztwahltbl.setSelectionMode(0);
        arztwahltbl.getColumn(0)
                   .setMinWidth(100);
        arztwahltbl.getColumn(6)
                   .setMinWidth(0);
        arztwahltbl.getColumn(6)
                   .setMaxWidth(0);
        arztwahltbl.setHorizontalScrollEnabled(true);
        if ((this.suchid.length()) > 0 && (!this.suchid.equals("-1"))) {
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    fuelleIdTabelle(suchid);
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            setzeFocus();
                        }
                    });
                    return null;
                }
            }.execute();
        } else {
            if (this.suchkrit.trim()
                             .length() > 0) {
                new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        fuelleTabelle(suchkrit);
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                setzeFocus();
                            }
                        });
                        return null;
                    }
                }.execute();
            } else {
                new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                setzeFocus();
                            }
                        });
                        return null;
                    }
                }.execute();
            }
        }
        JScrollPane span = JCompTools.getTransparentScrollPane(arztwahltbl);
        jpan.add(span, cc.xyw(2, 4, 6));
        return jpan;
    }

    public void fuelleTabelle(String suchkrit) {
        arztwahlmod.setRowCount(0);
        arztwahltbl.validate();
        // {"Name","Vorname","Strasse","Ort", "LANR",""};
        String[] zweikrit = suchkrit.split(" ");
        String krit = "";
        if (zweikrit.length > 1) {
            krit = "(nachname like '%" + zweikrit[0].trim() + "%' or vorname like '%" + zweikrit[0].trim()
                    + "%' or klinik like '%" + zweikrit[0].trim() + "%' or arztnum like '%" + zweikrit[0].trim()
                    + "%' or ort like '%" + zweikrit[0].trim() + "%') AND " + "(nachname like '%" + zweikrit[1].trim()
                    + "%' or vorname like '%" + zweikrit[1].trim() + "%' or klinik like '%" + zweikrit[1].trim()
                    + "%' or arztnum like '%" + zweikrit[1].trim() + "%' or ort like '%" + zweikrit[1].trim()
                    + "%') order by nachname";

        } else {
            krit = "nachname like '%" + suchkrit + "%' or vorname like '%" + suchkrit + "%' or klinik like '%"
                    + suchkrit + "%' or arztnum like '%" + suchkrit + "%' or ort like '%" + suchkrit
                    + "%' order by nachname";
        }

        Vector<Vector<String>> vec = SqlInfo.holeSaetze("arzt", "nachname,vorname,strasse,ort,arztnum,bsnr,id", krit,
                Arrays.asList(new String[] {}));
        int bis = vec.size();
        int i;
        for (i = 0; i < bis; i++) {
            arztwahlmod.addRow(vec.get(i));
        }
    }

    public void fuelleIdTabelle(String suchid) {
        arztwahlmod.setRowCount(0);
        arztwahltbl.validate();
        Vector<Vector<String>> vec = SqlInfo.holeSaetze("arzt", "nachname,vorname,strasse,ort,arztnum,bsnr,id",
                "id='" + suchid + "'", Arrays.asList(new String[] {}));
        int bis = vec.size();
        int i;
        for (i = 0; i < bis; i++) {
            arztwahlmod.addRow(vec.get(i));
        }
    }

    public void werteUebergeben() {
        int i = arztwahltbl.getSelectedRow();
        if (i >= 0) {
            int model = arztwahltbl.convertRowIndexToModel(i);
            elterntfs[0].setText((String) arztwahlmod.getValueAt(model, this.cNachname));
            elterntfs[1].setText((String) arztwahlmod.getValueAt(model, this.cArztnum));
            elterntfs[2].setText((String) arztwahlmod.getValueAt(model, this.cId));
            String id = (String) arztwahlmod.getValueAt(model, this.cId);
            myArzt.init(id);
            if (rtp != null) {
                rtp.removeRehaTPEventListener(this);
                rtp = null;
                pinPanel = null;
            }
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(null, "Kein Arzt für die Datenübernahme (in der Tabelle) ausgewählt!");
            setzeFocus();
        }
    }

    public ArztVec getArztRecord () {
       return myArzt;
   }

    /************************************************************/
    public void neuAnlageArzt() {
        super.getSmartTitledPanel().setTitle("Arzt neu anlegen");
        if (ank == null) {
            ank = new ArztNeuKurz(this);
        } else {
            ank.allesAufNull();
        }
        grundPanel.remove(this.content);
        grundPanel.add(ank, BorderLayout.CENTER);
        ank.setzteFocus();
        grundPanel.validate();
        repaint();
    }

    public void zurueckZurTabelle(JRtaTextField[] jtfs) {
        super.getSmartTitledPanel().setTitle("Arzt auswählen");
        if (jtfs != null) {
            Vector<String> vec = new Vector<String>();
            vec.add(jtfs[2].getText());
            vec.add(jtfs[3].getText());
            vec.add(jtfs[4].getText());
            vec.add(jtfs[6].getText());
            vec.add(jtfs[7].getText());
            vec.add(jtfs[8].getText());
            vec.add(jtfs[14].getText());
            arztwahlmod.addRow(vec);
            arztwahltbl.validate();
        }
        try {
            grundPanel.remove(ank);
        } catch (java.lang.NullPointerException ex) {

        }
        grundPanel.add(this.content);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                tf.requestFocus();
            }
        });
        grundPanel.validate();
        repaint();
        ank = null;
    }

    public void doAbbrechen() {
        if (arztbisher.length() <= 1) {
            elterntfs[0].setText("***nachtragen!!!***");
            elterntfs[1].setText("999999999");
            myArzt.setNName("***nachtragen!!!***");
            myArzt.setLANR("999999999");

        } else {
            elterntfs[0].setText(arztbisher);
        }
        if (rtp != null) {
            rtp.removeRehaTPEventListener(this);
            rtp = null;
            pinPanel = null;
        }
        dispose();
    }

    class ArztWahlAction extends AbstractAction {
        private static final long serialVersionUID = -6371294487538741375L;

        @Override
        public void actionPerformed(ActionEvent e) {

            if (e.getActionCommand()
                 .equals("f")) {
                tf.requestFocus();
            }
            if (e.getActionCommand()
                 .equals("n")) {
                neuAnlageArzt();
            }

        }
    }

    class ArztListener implements KeyListener {

        @Override
        public void keyPressed(KeyEvent arg0) {
            if (((JComponent) arg0.getSource()).getName()
                                               .equals("suchfeld")
                    && arg0.getKeyCode() == KeyEvent.VK_ENTER) {
                arg0.consume();
                fuelleTabelle(tf.getText()
                                .trim());
            } else if (((JComponent) arg0.getSource()).getName()
                                                      .equals("suchfeld")
                    && arg0.getKeyCode() == KeyEvent.VK_DOWN) {
                arztwahltbl.requestFocus();
                arztwahltbl.setRowSelectionInterval(0, 0);
            } else if (((JComponent) arg0.getSource()).getName()
                                                      .equals("arzttabelle")
                    && arg0.getKeyCode() == KeyEvent.VK_ENTER) {
                arg0.consume();
                werteUebergeben();
            } else if (((JComponent) arg0.getSource()).getName()
                                                      .equals("neuarzt")
                    && arg0.getKeyCode() == KeyEvent.VK_ENTER) {
                arg0.consume();
                neuAnlageArzt();
            }

            if (arg0.getKeyCode() == KeyEvent.VK_ESCAPE) {
                arg0.consume();
                doAbbrechen();
            }
        }

        @Override
        public void keyReleased(KeyEvent arg0) {

        }

        @Override
        public void keyTyped(KeyEvent arg0) {

        }

    }

}

class MyArztWahlModel extends DefaultTableModel {
    private static final long serialVersionUID = 1L;

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == 0) {
            return String.class;
        } else {
            return String.class;
        }
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return true;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return ((List<?>) getDataVector().get(rowIndex)).get(columnIndex);
    }

}
