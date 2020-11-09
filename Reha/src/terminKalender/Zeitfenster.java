package terminKalender;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalTime;
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

class Zeitfenster extends JDialog implements KeyListener, ActionListener {
    private static final long serialVersionUID = 1L;
    /** @jve:decl-index=0:visual-constraint="37,10" */
    private JXPanel jContentPane;
    private JRtaTextField NamePatient;
    private JRtaTextField Rezeptnummer;

    private JRtaTextField Dauer;
    private JRtaTextField BeginnStunde;
    private JRtaTextField BeginnMinute;
    private JRtaTextField EndeStunde;
    private JRtaTextField EndeMinute;
    private JButton Ok;
    private JButton Abbruch;
    private JXPanel neuPanel;
    private JRadioButton[] rb = { null, null, null };

    private JLabel[] lbl = { null, null, null };

    private enum RechenArt {
        startUndDauer {
            @Override
            boolean benutztStart() {
                return true;
            }

            @Override
            boolean benutztEnde() {
                return false;
            }

            @Override
            boolean benutztDauer() {
                return true;
            }
        },
        endUndDauer {
            @Override
            boolean benutztStart() {
                return false;
            }

            @Override
            boolean benutztEnde() {
                return true;
            }

            @Override
            boolean benutztDauer() {
                return true;
            }
        },
        startUndEnde {
            @Override
            boolean benutztStart() {
                return true;
            }

            @Override
            boolean benutztEnde() {
                return true;
            }

            @Override
            boolean benutztDauer() {
                return false;
            }
        };

        abstract boolean benutztStart();
        abstract boolean benutztEnde();
        abstract boolean benutztDauer();
    }

    private RechenArt rechenart = RechenArt.startUndDauer;
    private UIFSplitPane jSplitLR;
    private JXPanel panelRadio;
    private int dividerLocLR;
    /** @jve:decl-index=0: */
    private ButtonGroup rechenartbg = new ButtonGroup();
    private Block rueck;
    private final Block originalWerte;

    private FocusListener recalculate = new FocusAdapter() {
        @Override
        public void focusLost(FocusEvent arg0) {
            wertesetzen(wertelesen());
        }
    };

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
                if ((letzte == 290 && dividerLocLR == 279) || (letzte == 290 && dividerLocLR == 290)) {
                    jSplitLR.setDividerLocation(0);
                } else if ((letzte == 0 && dividerLocLR == 279) || (letzte == 0 && dividerLocLR == 0)) {
                    jSplitLR.setDividerLocation(290);
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
        Dauer.addFocusListener(recalculate);
        Dauer.addKeyListener(this);
        neuPanel.add(Dauer, cc.xy(2, 6));

        BeginnStunde = new JRtaTextField("STUNDEN", true);
        BeginnStunde.setHorizontalAlignment(JFormattedTextField.RIGHT);
        BeginnStunde.setName("BeginnStunde");
        BeginnStunde.addFocusListener(recalculate);
        BeginnStunde.addKeyListener(this);

        neuPanel.add(BeginnStunde, cc.xy(4, 6));

        BeginnMinute = new JRtaTextField("MINUTEN", true);
        BeginnMinute.setName("BeginnMinute");
        BeginnMinute.addFocusListener(recalculate);
        BeginnMinute.addKeyListener(this);
        neuPanel.add(BeginnMinute, cc.xy(6, 6));

        EndeStunde = new JRtaTextField("STUNDEN", true);
        EndeStunde.setHorizontalAlignment(JFormattedTextField.RIGHT);
        EndeStunde.setName("EndeStunde");
        EndeStunde.addFocusListener(recalculate);
        EndeStunde.addKeyListener(this);
        neuPanel.add(EndeStunde, cc.xy(8, 6));

        EndeMinute = new JRtaTextField("MINUTEN", true);
        EndeMinute.setName("EndeMinute");
        EndeMinute.addFocusListener(recalculate);
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
        if (endewert == 1) {
            rueck = wertelesen();
        } else {
            rueck = Block.EMPTYBLOCK;
        }
        setVisible(false);
        dispose();
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

    private Block wertelesen() {
        Block newblock;
        String name = NamePatient.getText();
        String reznr = Rezeptnummer.getText();
        String nr = originalWerte.getNr5();
        int dauer = Integer.parseInt(Dauer.getText());

        LocalTime beginn = startzeitlesen();

        LocalTime ende = endzeitLesen();

        switch (rechenart) {
        case startUndEnde:
            newblock = new Block(name, reznr, beginn, ende, nr);
            break;
        case endUndDauer:
            newblock = new Block(name, reznr, dauer, ende, nr);
            break;
        case startUndDauer:
            newblock = new Block(name, reznr, beginn, dauer, nr);
            break;
        default:
            newblock = originalWerte;
            break;
        }

        return newblock;
    }

    private LocalTime endzeitLesen() {
        int endStd = Integer.parseInt(EndeStunde.getText());
        int endMin = Integer.parseInt(EndeMinute.getText());
        return LocalTime.of(endStd, endMin);
    }

    private LocalTime startzeitlesen() {
        int beginnstd = Integer.parseInt(BeginnStunde.getText());
        int beginminute = Integer.parseInt(BeginnMinute.getText());
        return LocalTime.of(beginnstd, beginminute);
    }

    @Override
    public void keyPressed(KeyEvent arg0) {
        if (arg0.getKeyCode() == KeyEvent.VK_ESCAPE || arg0.getKeyCode() == KeyEvent.VK_CAPS_LOCK) {
            arg0.consume();
            Beenden(0);
            return;
        }
        if ("Ok".equals(((JComponent) arg0.getSource()).getName()) && arg0.getKeyCode() == KeyEvent.VK_ENTER) {
            arg0.consume();
            Beenden(1);
            return;
        }
        if ("Abbruch".equals(((JComponent) arg0.getSource()).getName()) && arg0.getKeyCode() == KeyEvent.VK_ENTER ) {
            arg0.consume();
            Beenden(0);
        }
    }

    @Override
    public void keyReleased(KeyEvent arg0) {
    }

    @Override
    public void keyTyped(KeyEvent arg0) {
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        if ("Ok".equals(((JComponent) arg0.getSource()).getName())) {
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
            if (dauer3 - dauer2 != dauer1) {
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
                                                .trim()) || dauer1 <= 0) {
            wertesetzen(originalWerte);
            Dauer.requestFocus();
            return;
         }
            Beenden(1);
        }
        if ("Abbruch".equals(((JComponent) arg0.getSource()).getName())) {
            Beenden(0);
        }
    }

    private void setRechenartAndFocus(RechenArt rechenart, JRtaTextField componentToFocus) {
        setRechenart(rechenart);
        jSplitLR.setDividerLocation(399);
        componentToFocus.requestFocus();
    }

    private void setRechenart(RechenArt rechenart) {
        this.rechenart = rechenart;

        BeginnStunde.setEnabled(rechenart.benutztStart());
        BeginnMinute.setEnabled(rechenart.benutztStart());

        EndeStunde.setEnabled(rechenart.benutztEnde());
        EndeMinute.setEnabled(rechenart.benutztEnde());

        Dauer.setEnabled(rechenart.benutztDauer());
    }

    public Block showAndWait(int x, int y) {
        pack();
        setLocation(x, y);
        toFront();
        setRechenart(rechenart);
        setModal(true);
        setVisible(true);
        return rueck;
    }
}
