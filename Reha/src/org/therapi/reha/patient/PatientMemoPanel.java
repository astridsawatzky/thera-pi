package org.therapi.reha.patient;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JXPanel;

import com.jgoodies.looks.windows.WindowsTabbedPaneUI;

import CommonTools.DatFunk;
import CommonTools.ExUndHop;
import CommonTools.JCompTools;
import CommonTools.SqlInfo;
import CommonTools.StringTools;
import floskeln.Floskeln;
import hauptFenster.Reha;
import systemEinstellungen.SystemConfig;

public class PatientMemoPanel extends JXPanel {
    private static final long serialVersionUID = 1894163619378832811L;
    private PatientHauptPanel patientHauptPanel;
    private MouseListener ml;
    private JButton[] memobut = { null, null, null, null, null, null };
    private JTextArea[] pmemo = { null, null };
    PatientMemoPanel(PatientHauptPanel patHauptPanel) {
        setLayout(new BorderLayout());
        setOpaque(false);
        this.patientHauptPanel = patHauptPanel;

        add(getMemoPanel(), BorderLayout.CENTER);
    }

    public void setNewText(String text) {
        if (!"".equals(text)) {
            if (text.indexOf('^') >= 0) {
                String newtext = testeAufPlatzhalter(text);
                String oldtext = getPmemo()[patientHauptPanel.inMemo].getText();
                getPmemo()[patientHauptPanel.inMemo].setText(newtext + "\n" + oldtext);
            } else {
                String oldtext = getPmemo()[patientHauptPanel.inMemo].getText();
                getPmemo()[patientHauptPanel.inMemo].setText(text + "\n" + oldtext);
            }
        }
        caretAufNull();
    }

    private void caretAufNull() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                getPmemo()[patientHauptPanel.inMemo].setCaretPosition(0);
            }
        });
    }

    private String testeAufPlatzhalter(String text) {
        String sret = "";
        text = text.replace("^Datum^", DatFunk.sHeute())
                   .replace("^User^", Reha.aktUser);
        String stext = text;
        int start = 0;
        // int end = 0;
        String dummy;
        int vars = 0;
        // int sysvar = -1;
        boolean noendfound = false;
        while ((start = stext.indexOf('^')) >= 0) {
            noendfound = true;
            for (int i = 1; i < 350; i++) {
                if ("^".equals(stext.substring(start + i, start + i + 1))) {
                    dummy = stext.substring(start, start + i + 1);
                    String sanweisung = dummy
                                             .replace("^", "");
                    Object ret = JOptionPane.showInputDialog(null,
                            "<html>Bitte Wert eingeben für: --\u003E<b> " + sanweisung + " </b> &nbsp; </html>",
                            "Platzhalter gefunden", 1);
                    if (ret == null) {
                        return "";
                    } else {
                        sret = stext.replace(dummy, ((String) ret).trim());
                        stext = sret;
                    }
                    noendfound = false;
                    vars++;
                    break;
                }
            }
            if (noendfound) {
                JOptionPane.showMessageDialog(null, "Der Baustein ist fehlerhaft, eine Übernahme deshalb nicht möglich"
                        + "\n\nVermutete Ursache des Fehlers: es wurde ein Start-/Endezeichen '^' für Variable vergessen\n");
                return "";
            }
        }

        return "".equals(sret) ? text : sret;
    }

    private PatientMemoPanel getInstance() {
        return this;
    }

    private void activateMouseListener() {
        ml = new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == 3
                        && (getPmemo()[0].isEditable() || getPmemo()[1].isEditable())) {
                    Floskeln fl = new Floskeln(Reha.getThisFrame(), "Floskeln", getInstance());
                    fl.setBounds(200, 200, 200, 200);
                    fl.setPreferredSize(new Dimension(200, 200));
                    fl.setLocation(e.getLocationOnScreen());
                    fl.setVisible(true);
                    fl.setAlwaysOnTop(true);
                    fl.setModal(true);
                    fl.setAlwaysOnTop(false);
                    fl = null;
                }
            }


        };
        getPmemo()[0].addMouseListener(ml);
        getPmemo()[1].addMouseListener(ml);
    }

    void fireAufraeumen() {
        for (int i = 0; i < memobut.length; i++) {
            memobut[i].removeActionListener(patientHauptPanel.memoAction);
        }
        getPmemo()[0].removeMouseListener(ml);
        getPmemo()[1].removeMouseListener(ml);
        ml = null;
        patientHauptPanel.memoAction = null;
    }

    void doMemoAction(ActionEvent arg0) {
        String sc = arg0.getActionCommand();
        if ("kedit".equals(sc)) {
            patientHauptPanel.inMemo = 0;
            memobut[0].setEnabled(false);
            memobut[1].setEnabled(true);
            memobut[2].setEnabled(true);
            getPmemo()[0].setForeground(Color.RED);
            getPmemo()[0].setEditable(true);
            getPmemo()[0].setCaretPosition(0);
            memobut[3].setEnabled(false);
            return;
        }
        if ("kedit2".equals(sc)) {
            patientHauptPanel.inMemo = 1;
            memobut[3].setEnabled(false);
            memobut[4].setEnabled(true);
            memobut[5].setEnabled(true);
            getPmemo()[1].setForeground(Color.RED);
            getPmemo()[1].setEditable(true);
            getPmemo()[1].setCaretPosition(0);
            memobut[0].setEnabled(false);
            return;
        }
        if ("ksave".equals(sc)) {
            memobut[0].setEnabled(true);
            memobut[1].setEnabled(false);
            memobut[2].setEnabled(false);
            getPmemo()[0].setForeground(Color.BLUE);
            getPmemo()[0].setEditable(false);
            memobut[3].setEnabled(true);
            String cmd = "update pat5 set anamnese='" + StringTools.Escaped(getPmemo()[0].getText())
                    + "' where id='" + patientHauptPanel.dbPatid + "'";
            new ExUndHop().setzeStatement(cmd);
            patientHauptPanel.inMemo = -1;
            return;
        }
        if ("ksave2".equals(sc)) {
            memobut[3].setEnabled(true);
            memobut[4].setEnabled(false);
            memobut[5].setEnabled(false);
            getPmemo()[1].setForeground(Color.BLUE);
            getPmemo()[1].setEditable(false);
            memobut[0].setEnabled(true);
            String cmd = "update pat5 set pat_text='" + StringTools.Escaped(getPmemo()[1].getText())
                    + "' where id='" + patientHauptPanel.dbPatid + "'";
            new ExUndHop().setzeStatement(cmd);
            patientHauptPanel.inMemo = -1;
            return;
        }
        if ("kbreak".equals(sc)) {
            memobut[0].setEnabled(true);
            memobut[1].setEnabled(false);
            memobut[2].setEnabled(false);
            getPmemo()[0].setForeground(Color.BLUE);
            getPmemo()[0].setEditable(false);
            memobut[3].setEnabled(true);
            getPmemo()[0].setText(SqlInfo.holeSatz("pat5", "anamnese",
                    "id='" + patientHauptPanel.dbPatid + "'", Arrays.asList(new String[] {}))
                                                      .get(0));
            getPmemo()[0].setCaretPosition(0);
            patientHauptPanel.inMemo = -1;
            return;
        }
        if ("kbreak2".equals(sc)) {
            memobut[3].setEnabled(true);
            memobut[4].setEnabled(false);
            memobut[5].setEnabled(false);
            getPmemo()[1].setForeground(Color.BLUE);
            getPmemo()[1].setEditable(false);
            memobut[0].setEnabled(true);
            getPmemo()[1].setText(SqlInfo.holeSatz("pat5", "pat_text",
                    "id='" + patientHauptPanel.dbPatid + "'", Arrays.asList(new String[] {}))
                                                      .get(0));
            getPmemo()[1].setCaretPosition(0);
            patientHauptPanel.inMemo = -1;
        }
    }

    private JXPanel getMemoPanel() {
        JXPanel mittelinksunten = new JXPanel(new BorderLayout());
        mittelinksunten.setOpaque(false);
        mittelinksunten.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        JToolBar jtoolb = new JToolBar();
        jtoolb.setOpaque(false);
        jtoolb.setBorder(null);
        jtoolb.setBorderPainted(false);
        jtoolb.setRollover(true);
        this.memobut[0] = new JButton();
        this.memobut[0].setIcon(SystemConfig.hmSysIcons.get("edit"));
        this.memobut[0].setToolTipText("Langtext editieren");
        this.memobut[0].setActionCommand("kedit");
        this.memobut[0].addActionListener(patientHauptPanel.memoAction);
        jtoolb.add(this.memobut[0]);
        this.memobut[1] = new JButton();
        this.memobut[1].setIcon(SystemConfig.hmSysIcons.get("save"));
        this.memobut[1].setToolTipText("Langtext speichern");
        this.memobut[1].setActionCommand("ksave");
        this.memobut[1].addActionListener(patientHauptPanel.memoAction);
        this.memobut[1].setEnabled(false);
        jtoolb.add(this.memobut[1]);
        jtoolb.addSeparator(new Dimension(40, 0));
        this.memobut[2] = new JButton();
        this.memobut[2].setIcon(SystemConfig.hmSysIcons.get("stop"));
        this.memobut[2].setToolTipText("Langtext bearbeiten abbrechen");
        this.memobut[2].setActionCommand("kbreak");
        this.memobut[2].addActionListener(patientHauptPanel.memoAction);
        this.memobut[2].setEnabled(false);
        jtoolb.add(this.memobut[2]);

        patientHauptPanel.memotab = new JTabbedPane();
        patientHauptPanel.memotab.setUI(new WindowsTabbedPaneUI());
        patientHauptPanel.memotab.setOpaque(false);
        patientHauptPanel.memotab.setBorder(null);

        getPmemo()[0] = new JTextArea();
        getPmemo()[0].setFont(new Font("Courier", Font.PLAIN, 11));
        getPmemo()[0].setLineWrap(true);
        getPmemo()[0].setName("notitzen");
        getPmemo()[0].setWrapStyleWord(true);
        getPmemo()[0].setEditable(false);
        getPmemo()[0].setBackground(Color.WHITE);
        getPmemo()[0].setForeground(Color.BLUE);
        JScrollPane span = JCompTools.getTransparentScrollPane(getPmemo()[0]);
        // span.setBackground(Color.WHITE);
        span.validate();
        JXPanel jpan = JCompTools.getEmptyJXPanel(new BorderLayout());
        jpan.setOpaque(true);
        JXPanel jpan2 = JCompTools.getEmptyJXPanel(new BorderLayout());

        jpan2.setBackgroundPainter(Reha.instance.compoundPainter.get("FliessText"));
        jpan2.add(jtoolb);
        jpan.add(jpan2, BorderLayout.NORTH);
        jpan.add(span, BorderLayout.CENTER);
        patientHauptPanel.memotab.addTab("Notizen", jpan);
        JToolBar jtoolb2 = new JToolBar();
        jtoolb2.setOpaque(false);
        jtoolb2.setBorder(null);
        jtoolb2.setBorderPainted(false);
        jtoolb2.setRollover(true);
        this.memobut[3] = new JButton();
        this.memobut[3].setIcon(SystemConfig.hmSysIcons.get("edit"));
        this.memobut[3].setToolTipText("Langtext editieren");
        this.memobut[3].setActionCommand("kedit2");
        this.memobut[3].addActionListener(patientHauptPanel.memoAction);
        jtoolb2.add(this.memobut[3]);
        this.memobut[4] = new JButton();
        this.memobut[4].setIcon(SystemConfig.hmSysIcons.get("save"));

        this.memobut[4].setToolTipText("Langtext speichern");
        this.memobut[4].setActionCommand("ksave2");
        this.memobut[4].addActionListener(patientHauptPanel.memoAction);
        this.memobut[4].setEnabled(false);
        jtoolb2.add(this.memobut[4]);
        jtoolb2.addSeparator(new Dimension(40, 0));
        this.memobut[5] = new JButton();
        this.memobut[5].setIcon(SystemConfig.hmSysIcons.get("stop"));
        this.memobut[5].setToolTipText("Langtext bearbeiten abbrechen");
        this.memobut[5].setActionCommand("kbreak2");
        this.memobut[5].addActionListener(patientHauptPanel.memoAction);
        this.memobut[5].setEnabled(false);
        jtoolb2.add(this.memobut[5]);

        getPmemo()[1] = new JTextArea();
        getPmemo()[1].setFont(new Font("Courier", Font.PLAIN, 11));
        getPmemo()[1].setLineWrap(true);
        getPmemo()[1].setName("notitzen");
        getPmemo()[1].setWrapStyleWord(true);
        getPmemo()[1].setEditable(false);
        getPmemo()[1].setBackground(Color.WHITE);
        getPmemo()[1].setForeground(Color.BLUE);
        span = JCompTools.getTransparentScrollPane(getPmemo()[1]);
        span.setBackground(Color.WHITE);
        span.validate();
        jpan = JCompTools.getEmptyJXPanel(new BorderLayout());
        jpan.setOpaque(true);
        jpan2 = JCompTools.getEmptyJXPanel(new BorderLayout());
        jpan2.setBackgroundPainter(Reha.instance.compoundPainter.get("FliessText"));
        jpan2.add(jtoolb2);
        jpan.add(jpan2, BorderLayout.NORTH);
        jpan.add(span, BorderLayout.CENTER);
        patientHauptPanel.memotab.addTab("Fehldaten", jpan);

        mittelinksunten.add(patientHauptPanel.memotab, BorderLayout.CENTER);
        mittelinksunten.revalidate();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                activateMouseListener();
            }
        });
        return mittelinksunten;
    }

    public JTextArea[] getPmemo() {
        return pmemo;
    }

    void memoPanelAufNull() {
        getPmemo()[0].setText("");
        getPmemo()[1].setText("");
    }
}
