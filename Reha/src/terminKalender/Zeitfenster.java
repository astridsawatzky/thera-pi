package terminKalender;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JSplitPane;
import javax.swing.border.SoftBevelBorder;

import org.jdesktop.swingx.JXPanel;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import CommonTools.JRtaTextField;
import CommonTools.ZeitFunk;
import hauptFenster.Reha;
import hauptFenster.UIFSplitPane;

class Zeitfenster extends JDialog implements KeyListener, FocusListener, ActionListener {

    private static final long serialVersionUID = 1L;
    private JXPanel jContentPane = null; // @jve:decl-index=0:visual-constraint="37,10"
    private JRtaTextField NamePatient = null;
    private JRtaTextField Rezeptnummer = null;

    private JRtaTextField Dauer = null;
    private JRtaTextField BeginnStunde = null;
    private JRtaTextField BeginnMinute = null;
    private JRtaTextField EndeStunde = null;
    private JRtaTextField EndeMinute = null;
    private JButton Ok = null;
    private JButton Abbruch = null;
    private JXPanel neuPanel = null;
    private JRadioButton[] rb = { null, null, null };

    private JLabel[] lbl = { null, null, null };

    private enum RechenArt {
        startUndDauer,
        endUndDauer,
        startUndEnde;

    }

    private RechenArt rechenart = RechenArt.startUndDauer;
    private UIFSplitPane jSplitLR = null;
    private JXPanel panelRadio = null;
    private int dividerLocLR = 0;
    private ButtonGroup rechenartbg = new ButtonGroup(); // @jve:decl-index=0:
    private Block rueck;
    private final Block originalWerte;

    /**
     * @param terminblock TODO
     */
    Zeitfenster(Block terminblock) {
        originalWerte = terminblock;
        setSize(299, 77);
        setPreferredSize(new Dimension(299, 77));
        setContentPane(getJContentPane());
        setUndecorated(true);
        setAlwaysOnTop(true);

        wertesetzen(originalWerte);
        NamePatient.requestFocus();
        NamePatient.setCaretPosition(0);

        addKeyListener(this);
    }

    /**
     * This method initializes jContentPane
     *
     * @return javax.swing.JPanel
     */
    private JXPanel getJContentPane() {
        if (jContentPane == null) {

            jContentPane = new JXPanel();
            jContentPane.setBackground(Color.WHITE);
            jContentPane.setBackgroundPainter(Reha.instance.compoundPainter.get("Zeitfenster"));
            jContentPane.setBorder(BorderFactory.createLineBorder(Color.BLUE));
            jContentPane.setOpaque(true);
            jContentPane.setLayout(new BorderLayout());

            jContentPane.setPreferredSize(new Dimension(480, 80));

            panelRadio = new JXPanel(new BorderLayout());
            panelRadio.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));
            panelRadio.setBackgroundPainter(Reha.instance.compoundPainter.get("Zeitfenster"));
            panelRadio.setPreferredSize(new Dimension(480, 80));
            panelRadio.add(radioPan(), BorderLayout.CENTER);

            jContentPane.add(grundSplit(), BorderLayout.CENTER);
            jContentPane.addKeyListener(this);

        }
        jSplitLR.setDividerLocation(399);
        return jContentPane;
    }

    private JXPanel radioPan() {
        JXPanel radio = new JXPanel();
        radio.setOpaque(false);
        radio.setLayout(new GridLayout(3, 1, 0, 3));
        rechenartbg = new ButtonGroup();

        rb[0] = new JRadioButton("rechne Endzeit aus Startzeit + Dauer");
        JRadioButton startUndDauer = rb[0];
        startUndDauer.addKeyListener(this);
        startUndDauer.addActionListener(e -> setRechenartAndFocus(RechenArt.startUndDauer, Dauer));
        startUndDauer.setOpaque(false);
        startUndDauer.setName("startUndDauer");

        rechenartbg.add(startUndDauer);
        radio.add(startUndDauer);

        rb[1] = new JRadioButton("rechne Startzeit aus Endzeit - Dauer");
        JRadioButton endUndDauer = rb[1];
        endUndDauer.addKeyListener(this);
        endUndDauer.addActionListener(e -> setRechenartAndFocus(RechenArt.endUndDauer, Dauer));
        endUndDauer.setOpaque(false);
        endUndDauer.setName("endUndDauer");
        rechenartbg.add(endUndDauer);
        radio.add(endUndDauer);

        rb[2] = new JRadioButton("rechne Dauer aus Startzeit und Endzeit");
        JRadioButton startundEnd = rb[2];
        startundEnd.addKeyListener(this);
        startundEnd.addActionListener(e -> setRechenartAndFocus(RechenArt.startUndEnde, BeginnStunde));
        startundEnd.setOpaque(false);
        startundEnd.setName("startundEnd");
        rechenartbg.add(startundEnd);
        radio.add(startundEnd);

        startUndDauer.setSelected(true);
        return radio;
    }

    private UIFSplitPane grundSplit() {
        jSplitLR = UIFSplitPane.createStrippedSplitPane(JSplitPane.HORIZONTAL_SPLIT, neuLayout(), panelRadio);
        jSplitLR.setOpaque(false);
        jSplitLR.setBackground(Color.WHITE);
        jSplitLR.setDividerSize(7);
        jSplitLR.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent arg0) {
                dividerLocLR = jSplitLR.getDividerLocation();
                int letzte = ((UIFSplitPane) arg0.getSource()).getLastDividerLocation();
                for (int i = 0; i < 1; i++) {
                    if ((letzte == 290) && (dividerLocLR == 279)) {
                        jSplitLR.setDividerLocation(0);
                        break;
                    }
                    if ((letzte == 0) && (dividerLocLR == 279)) {
                        jSplitLR.setDividerLocation(290);
                        break;
                    }
                    if ((letzte == 0) && (dividerLocLR == 0)) {
                        jSplitLR.setDividerLocation(290);
                        break;
                    }
                    if ((letzte == 290) && (dividerLocLR == 290)) {
                        jSplitLR.setDividerLocation(0);
                        break;
                    }

                }
            }
        });

        jSplitLR.setDividerBorderVisible(false);
        jSplitLR.setName("GrundSplitLinksRechts");
        jSplitLR.setOneTouchExpandable(true);
        dividerLocLR = 400;
        jSplitLR.setDividerLocation(400);
        return jSplitLR;
    }

    private JXPanel neuLayout() {
        neuPanel = new JXPanel(); // 1. 2.Min. 3. 4.SS 5. 6.SM 7. 8.ES 9. 10.EM 11. 12. 13.OK 14. 15.Abb
        FormLayout lay = new FormLayout(
                "5dlu,25dlu,10dlu,15dlu,2dlu,15dlu,10dlu,15dlu,2dlu,15dlu,2dlu,6dlu,30dlu,2dlu,30dlu,p:g",
                "2dlu,p,5dlu,p,2dlu,p");
        CellConstraints cc = new CellConstraints();
        neuPanel.setLayout(lay);
        neuPanel.setBackground(Color.WHITE);
        neuPanel.setOpaque(false);

        NamePatient = new JRtaTextField("GROSS", false);
        NamePatient.setBorder(new SoftBevelBorder(SoftBevelBorder.LOWERED));
        NamePatient.setName("NamePatient");
        NamePatient.addKeyListener(this);
        neuPanel.add(NamePatient, cc.xyw(2, 2, 9));

        Rezeptnummer = new JRtaTextField("GROSS", false);
        Rezeptnummer.setBorder(new SoftBevelBorder(SoftBevelBorder.LOWERED));
        Rezeptnummer.setName("Rezeptnummer");
        Rezeptnummer.addKeyListener(this);
        neuPanel.add(Rezeptnummer, cc.xyw(13, 2, 3));

        lbl[0] = new JLabel("Minuten");
        lbl[0].setForeground(Color.WHITE);
        lbl[0].setOpaque(false);
        neuPanel.add(lbl[0], cc.xy(2, 4));

        lbl[1] = new JLabel("Startzeit");
        lbl[1].setForeground(Color.BLUE);
        lbl[1].setOpaque(false);
        neuPanel.add(lbl[1], cc.xyw(4, 4, 4));

        lbl[2] = new JLabel("Endzeit");
        lbl[2].setForeground(Color.RED);
        lbl[2].setOpaque(false);
        neuPanel.add(lbl[2], cc.xyw(8, 4, 4));

        Dauer = new JRtaTextField("ZAHLEN", true);
        Dauer.setHorizontalAlignment(JFormattedTextField.RIGHT);
        Dauer.setName("Dauer");
        Dauer.addFocusListener(this);
        Dauer.addKeyListener(this);
        neuPanel.add(Dauer, cc.xy(2, 6));

        BeginnStunde = new JRtaTextField("STUNDEN", true);
        BeginnStunde.setHorizontalAlignment(JFormattedTextField.RIGHT);
        BeginnStunde.setName("BeginnStunde");
        BeginnStunde.addFocusListener(this);
        BeginnStunde.addKeyListener(this);

        neuPanel.add(BeginnStunde, cc.xy(4, 6));

        BeginnMinute = new JRtaTextField("MINUTEN", true);
        BeginnMinute.setName("BeginnMinute");
        BeginnMinute.addFocusListener(this);
        BeginnMinute.addKeyListener(this);
        neuPanel.add(BeginnMinute, cc.xy(6, 6));

        EndeStunde = new JRtaTextField("STUNDEN", true);
        EndeStunde.setHorizontalAlignment(JFormattedTextField.RIGHT);
        EndeStunde.setName("EndeStunde");
        EndeStunde.addFocusListener(this);
        EndeStunde.addKeyListener(this);
        neuPanel.add(EndeStunde, cc.xy(8, 6));

        EndeMinute = new JRtaTextField("MINUTEN", true);
        EndeMinute.setName("EndeMinute");
        EndeMinute.addFocusListener(this);
        EndeMinute.addKeyListener(this);
        neuPanel.add(EndeMinute, cc.xy(10, 6));

        Ok = new JButton("Ok");
        Ok.setName("Ok");
        Ok.setMnemonic(KeyEvent.VK_O);
        Ok.addKeyListener(this);
        Ok.addActionListener(this);
        neuPanel.add(Ok, cc.xy(13, 6));

        Abbruch = new JButton("Abbruch");
        Abbruch.setName("Abbruch");
        Abbruch.setMnemonic(KeyEvent.VK_A);
        Abbruch.addKeyListener(this);
        Abbruch.addActionListener(this);
        neuPanel.add(Abbruch, cc.xy(15, 6));

        return neuPanel;
    }

    private void Beenden(int endewert) {
        String[] srueck = new String[] { "", "", "", "", "", "" };
        if (endewert == 1) {
            srueck = new String[] { NamePatient.getText(), Rezeptnummer.getText(),
                    BeginnStunde.getText() + ":" + BeginnMinute.getText() + ":00", Dauer.getText(),
                    EndeStunde.getText() + ":" + EndeMinute.getText() + ":00", "" };
        }
        rueck = new Block(srueck);
        this.setVisible(false);
        this.dispose();

    }

    private void wertesetzen(Block werte) {
        NamePatient.setText(werte.getName0());
        Rezeptnummer.setText(werte.getRezeptnr1());
        Dauer.setText(werte.getDauer3());
        BeginnStunde.setText(werte.getStartzeit2()
                                  .split(":")[0]);
        BeginnMinute.setText(werte.getStartzeit2()
                                  .split(":")[1]);
        EndeStunde.setText(werte.getEndzeit4()
                                .split(":")[0]);
        EndeMinute.setText(werte.getEndzeit4()
                                .split(":")[1]);
    }

    @Override
    public void keyPressed(KeyEvent arg0) {
        if (arg0.getKeyCode() == KeyEvent.VK_ESCAPE) {
            arg0.consume();
            Beenden(0);
            return;
        }
        if (((JComponent) arg0.getSource()).getName()
                                           .equals("Ok")) {
            if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
                arg0.consume();
                Beenden(1);
                return;
            }
            if (arg0.getKeyCode() == KeyEvent.VK_CAPS_LOCK) {
                arg0.consume();
                Beenden(0);
                return;
            }
        }
        if (((JComponent) arg0.getSource()).getName()
                                           .equals("Abbruch")) {
            if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
                arg0.consume();
                Beenden(0);
                return;
            }
            if (arg0.getKeyCode() == KeyEvent.VK_CAPS_LOCK) {
                arg0.consume();
                Beenden(0);
                return;
            }
        }
        String name = ((JComponent) arg0.getSource()).getName()
                                                     .trim();
        if (name == "Dauer" || name == "BeginnStunde" || name == "BeginnMinute" || name == "EndeStunde"
                || name == "EndeMinute" || name == "Abbruch" || name == "Rezeptnummer" || name == "NamePatient") {
            if (arg0.getKeyCode() == KeyEvent.VK_CAPS_LOCK) {
                arg0.consume();
                Beenden(0);
                return;
            }
        }

    }

    @Override
    public void keyReleased(KeyEvent arg0) {

    }

    @Override
    public void keyTyped(KeyEvent arg0) {

    }

    @Override
    public void focusGained(FocusEvent arg0) {

    }

    @Override
    public void focusLost(FocusEvent arg0) {

        for (int i = 0; i < 1; i++) {
            if (((JComponent) arg0.getSource()).getName()
                                               .equals("Dauer")) {
                if (rechenart == RechenArt.startUndDauer) {
                    int dauer1 = Integer.parseInt(((JRtaTextField) arg0.getSource()).getText());

                    int dauer2 = (int) ZeitFunk.MinutenSeitMitternacht(
                            BeginnStunde.getText() + ":" + BeginnMinute.getText() + ":00");
                    // String sEnde = String.valueOf();
                    String sEnde = ZeitFunk.MinutenZuZeit(dauer1 + dauer2);
                    EndeStunde.setText(sEnde.split(":")[0]);
                    EndeMinute.setText(sEnde.split(":")[1]);
                }
                if (rechenart == RechenArt.endUndDauer) {
                    int dauer1 = Integer.parseInt(((JRtaTextField) arg0.getSource()).getText());

                    int dauer2 = (int) ZeitFunk.MinutenSeitMitternacht(
                            EndeStunde.getText() + ":" + EndeMinute.getText() + ":00");
                    // String sEnde = String.valueOf();
                    String sEnde = ZeitFunk.MinutenZuZeit(dauer2 - dauer1);
                    BeginnStunde.setText(sEnde.split(":")[0]);
                    BeginnMinute.setText(sEnde.split(":")[1]);
                }

                break;
            }
            if (((JComponent) arg0.getSource()).getName()
                                               .equals("BeginnStunde")) {
                // String sb = String.valueOf();
                String sb = ((JRtaTextField) arg0.getSource()).getText();
                if (sb.isEmpty()) {
                    ((JRtaTextField) arg0.getSource()).requestFocus();
                    return;
                }
                if (sb.length() == 1) {
                    ((JRtaTextField) arg0.getSource()).setText("0" + sb);
                }
                int dauer1 = Integer.parseInt(Dauer.getText());
                int dauer2 = (int) ZeitFunk.MinutenSeitMitternacht(
                        BeginnStunde.getText() + ":" + BeginnMinute.getText() + ":00");
                int dauer3 = (int) ZeitFunk.MinutenSeitMitternacht(
                        EndeStunde.getText() + ":" + EndeMinute.getText() + ":00");
                /*************************/
                if (rechenart == RechenArt.startUndDauer) {
                    // String sEnde = String.valueOf();
                    String sEnde = ZeitFunk.MinutenZuZeit(dauer1 + dauer2);
                    EndeStunde.setText(sEnde.split(":")[0]);
                    EndeMinute.setText(sEnde.split(":")[1]);
                }
                if (rechenart == RechenArt.startUndEnde) {
                    Dauer.setText(Integer.toString(dauer3 - dauer2));
                }
                break;
            }
            if (((JComponent) arg0.getSource()).getName()
                                               .equals("BeginnMinute")) {
                // String sb = String.valueOf();
                String sb = ((JRtaTextField) arg0.getSource()).getText();
                if (sb.isEmpty()) {
                    ((JRtaTextField) arg0.getSource()).requestFocus();
                    return;
                }
                if (sb.length() == 1) {
                    ((JRtaTextField) arg0.getSource()).setText("0" + sb);
                }
                int dauer1 = Integer.parseInt(Dauer.getText());
                int dauer2 = (int) ZeitFunk.MinutenSeitMitternacht(
                        BeginnStunde.getText() + ":" + BeginnMinute.getText() + ":00");
                int dauer3 = (int) ZeitFunk.MinutenSeitMitternacht(
                        EndeStunde.getText() + ":" + EndeMinute.getText() + ":00");
                /*************************/
                if (rechenart == RechenArt.startUndDauer) {
                    // String sEnde = String.valueOf();
                    String sEnde = ZeitFunk.MinutenZuZeit(dauer1 + dauer2);
                    EndeStunde.setText(sEnde.split(":")[0]);
                    EndeMinute.setText(sEnde.split(":")[1]);
                }
                if (rechenart == RechenArt.startUndEnde) {
                    Dauer.setText(Integer.toString(dauer3 - dauer2));
                }

                break;
            }
            if (((JComponent) arg0.getSource()).getName()
                                               .equals("EndeStunde")) {
                // String sb = String.valueOf();
                String sb = ((JRtaTextField) arg0.getSource()).getText();
                if (sb.isEmpty()) {
                    ((JRtaTextField) arg0.getSource()).requestFocus();
                    return;
                }
                if (sb.length() == 1) {
                    ((JRtaTextField) arg0.getSource()).setText("0" + sb);
                }
                // int dauer1 = Integer.valueOf( (String) ((JRtaTextField)Dauer).getText() );
                int dauer1 = (int) ZeitFunk.MinutenSeitMitternacht(
                        EndeStunde.getText() + ":" + EndeMinute.getText() + ":00");
                int dauer2 = (int) ZeitFunk.MinutenSeitMitternacht(
                        BeginnStunde.getText() + ":" + BeginnMinute.getText() + ":00");
                int dauer3 = Integer.parseInt(Dauer.getText()
                                                   .trim());
                if (rechenart == RechenArt.startUndEnde) {
                    Dauer.setText(Integer.toString(dauer1 - dauer2));
                }
                if (rechenart == RechenArt.endUndDauer) {
                    // String sBeginn = String.valueOf();
                    String sBeginn = ZeitFunk.MinutenZuZeit(dauer1 - dauer3);
                    BeginnStunde.setText(sBeginn.split(":")[0]);
                    BeginnMinute.setText(sBeginn.split(":")[1]);
                }

                break;
            }
            if (((JComponent) arg0.getSource()).getName()
                                               .equals("EndeMinute")) {
                // String sb = String.valueOf();
                String sb = ((JRtaTextField) arg0.getSource()).getText();
                if (sb.isEmpty()) {
                    ((JRtaTextField) arg0.getSource()).requestFocus();
                    return;
                }
                if (sb.length() == 1) {
                    ((JRtaTextField) arg0.getSource()).setText("0" + sb);
                }
                // int dauer1 = Integer.valueOf( (String) ((JRtaTextField)Dauer).getText() );
                int dauer1 = (int) ZeitFunk.MinutenSeitMitternacht(
                        EndeStunde.getText() + ":" + EndeMinute.getText() + ":00");
                int dauer2 = (int) ZeitFunk.MinutenSeitMitternacht(
                        BeginnStunde.getText() + ":" + BeginnMinute.getText() + ":00");
                int dauer3 = Integer.parseInt(Dauer.getText()
                                                   .trim());
                if (rechenart == RechenArt.endUndDauer) {
                    String sBeginn;
                    sBeginn = ZeitFunk.MinutenZuZeit(dauer1 - dauer3);
                    BeginnStunde.setText(sBeginn.split(":")[0]);
                    BeginnMinute.setText(sBeginn.split(":")[1]);
                }
                if (rechenart == RechenArt.startUndEnde) {
                    Dauer.setText(Integer.toString(dauer1 - dauer2));
                }
                dauer3 = Integer.parseInt(Dauer.getText()
                                               .trim());
                if ((dauer1 - dauer2) != dauer3) {
                    wertesetzen(originalWerte);
                    Dauer.requestFocus();
                    return;
                }

                break;
            }

        }

    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        System.out.println(rechenartbg.getSelection()
                                      .getSelectedObjects());
        if (((JComponent) arg0.getSource()).getName()
                                           .equals("Ok")) {
            int dauer1 = Integer.parseInt(Dauer.getText());
            int dauer2 = (int) ZeitFunk.MinutenSeitMitternacht(
                    BeginnStunde.getText() + ":" + BeginnMinute.getText() + ":00");
            int dauer3 = (int) ZeitFunk.MinutenSeitMitternacht(
                    EndeStunde.getText() + ":" + EndeMinute.getText() + ":00");

            if (dauer3 <= dauer2) {
                wertesetzen(originalWerte);
                Dauer.requestFocus();
                return;
            }
            if ((dauer3 - dauer2) != dauer1) {
                String sEnde;
                sEnde = ZeitFunk.MinutenZuZeit(dauer2 + dauer1);
                EndeStunde.setText(sEnde.split(":")[0]);
                EndeMinute.setText(sEnde.split(":")[1]);
            }

            dauer2 = (int) ZeitFunk.MinutenSeitMitternacht(
                    BeginnStunde.getText() + ":" + BeginnMinute.getText() + ":00");
            dauer3 = (int) ZeitFunk.MinutenSeitMitternacht(EndeStunde.getText() + ":" + EndeMinute.getText() + ":00");
            dauer1 = dauer3 - dauer2;
            if (dauer1 != Integer.parseInt(Dauer.getText()
                                                .trim())) {
                wertesetzen(originalWerte);
                Dauer.requestFocus();
                return;
            }
            if (dauer1 <= 0) {
                wertesetzen(originalWerte);
                Dauer.requestFocus();
                return;
            }

            Beenden(1);
        }
        if (((JComponent) arg0.getSource()).getName()
                                           .equals("Abbruch")) {
            Beenden(0);
        }

    }

    private void setRechenartAndFocus(RechenArt startundende, JRtaTextField componentToFocus) {
        this.rechenart = startundende;
        jSplitLR.setDividerLocation(399);
        componentToFocus.requestFocus();
    }

    public Block showAndWait(int x, int y) {
        pack();
        setLocation(x, y);
        toFront();
        setModal(true);
        setVisible(true);
        return rueck;
    }
}
