package systemEinstellungen;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;

import org.jdesktop.swingx.JXPanel;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import CommonTools.JCompTools;
import CommonTools.JRtaTextField;
import CommonTools.SqlInfo;
import hauptFenster.Reha;

public class SysUtilNummernKreis extends JXPanel implements KeyListener, ActionListener {
    /**
     * 
     */
    private static final long serialVersionUID = 858117043130060154L;
    JRtaTextField[] tfs = { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
            null, null, null, null, null };
    JButton abbruch = null;
    JButton speichern = null;
    Vector<String> originale = new Vector<String>();
    boolean nummernkreisok = true;

    public SysUtilNummernKreis(ImageIcon img) {
        super(new BorderLayout());
        this.setBorder(BorderFactory.createEmptyBorder(15, 40, 20, 20));
        /****/
        setBackgroundPainter(Reha.instance.compoundPainter.get("SystemInit"));
        /****/

        JScrollPane jscr = JCompTools.getTransparentScrollPane(
                (SystemConfig.mitRs ? getVorlagenSeiteMitRs() : getVorlagenSeiteOhneRs()));
        jscr.validate();
        jscr.getVerticalScrollBar()
            .setUnitIncrement(15);
        add(jscr, BorderLayout.CENTER);
        /*
         * if(SystemConfig.mitRs){ add(getVorlagenSeiteMitRs(),BorderLayout.CENTER);
         * }else{ add(getVorlagenSeiteOhneRs(),BorderLayout.CENTER); }
         */
        add(getKnopfPanel(), BorderLayout.SOUTH);

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                fuelleTextFelder();
                return null;
            }

        }.execute();
        validate();
        return;
    }

    /**************
     * Beginn der Methode für die Objekterstellung und -platzierung
     *********/
    private void fuelleTextFelder() {
        // String cmd = "select * from nummern where mandant = '"+Reha.aktIK+"' LIMIT
        // 1";
        String cmd = "select * from nummern LIMIT 1";
        Vector<Vector<String>> vec = SqlInfo.holeFelder(cmd);
        if (vec.size() <= 0) {
            JOptionPane.showMessageDialog(null, "Achtung für den aktuellen Mandanten existiert noch kein Nummernkreis");
            nummernkreisok = false;
            return;
        }
        if (SystemConfig.mitRs) {
            for (int i = 0; i < 19; i++) {
                originale.add(vec.get(0)
                                 .get(i));
                tfs[i].setText(originale.get(i));
            }
        } else {
            for (int i = 0; i < 17; i++) {
                originale.add(vec.get(0)
                                 .get(i));
                tfs[i].setText(originale.get(i));
            }
        }
    }

    private JPanel getVorlagenSeiteOhneRs() {
        // 1. 2. 3. 4. 5. 6. 7. 8. 9.
        FormLayout lay = new FormLayout("right:max(60dlu;p), 4dlu, 40dlu, 4dlu, 40dlu, 4dlu, 40dlu, 4dlu, 40dlu",
                // 1. 2. 3. 4. 5. 6. 7. 8. 9. 10. 11. 12. 13. 14. 15. 16. 17. 18. 19. 20. 21.
                // 22. 23.
                "p, 0dlu, p, p,0dlu,p,2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, " +
                // 21. 22. 23. 24. 25. 26. 27. 28. 29. 30. 31. 32. 33. 34. 35. 36. 37. 38. 39 40
                        "2dlu, p , 2dlu ,p , 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p,  2dlu,  p, 2dlu, p, 2dlu, p, 2dlu, p ,2dlu, p, 2dlu, p ");

        PanelBuilder builder = new PanelBuilder(lay);
        builder.setDefaultDialogBorder();
        builder.getPanel()
               .setOpaque(false);
        CellConstraints cc = new CellConstraints();
        // JLabel lab = new JLabel("Nummernkreis von Mandant: "+Reha.aktIK);
        // lab.setFont(new Font("Tahoma",Font.BOLD,12));
        // builder.add(lab,cc.xyw(3, 4, 7));
        JLabel lab = new JLabel("nächste Nummer für...");
        builder.add(lab, cc.xyw(1, 6, 7));

        builder.addLabel("Patient", cc.xy(1, 8));
        tfs[0] = new JRtaTextField("ZAHLEN", true);
        tfs[0].setName("pat");
        builder.add(tfs[0], cc.xy(3, 8));

        builder.addLabel("KG-Rezept", cc.xy(1, 10));
        tfs[1] = new JRtaTextField("ZAHLEN", true);
        tfs[1].setName("kg");
        builder.add(tfs[1], cc.xy(3, 10));

        builder.addLabel("Massage-Rezept", cc.xy(1, 12));
        tfs[2] = new JRtaTextField("ZAHLEN", true);
        tfs[2].setName("ma");
        builder.add(tfs[2], cc.xy(3, 12));

        builder.addLabel("Ergo-Rezept", cc.xy(1, 14));
        tfs[3] = new JRtaTextField("ZAHLEN", true);
        tfs[3].setName("er");
        builder.add(tfs[3], cc.xy(3, 14));

        builder.addLabel("Logo-Rezept", cc.xy(1, 16));
        tfs[4] = new JRtaTextField("ZAHLEN", true);
        tfs[4].setName("lo");
        builder.add(tfs[4], cc.xy(3, 16));

        builder.addLabel("Reha-Verordnung", cc.xy(1, 18));
        tfs[5] = new JRtaTextField("ZAHLEN", true);
        tfs[5].setName("rh");
        builder.add(tfs[5], cc.xy(3, 18));

        builder.addLabel("Podologie-Verordnung", cc.xy(1, 20));
        tfs[6] = new JRtaTextField("ZAHLEN", true);
        tfs[6].setName("po");
        builder.add(tfs[6], cc.xy(3, 20));

        builder.addLabel("Rechnungsnummer", cc.xy(1, 22));
        tfs[7] = new JRtaTextField("ZAHLEN", true);
        tfs[7].setName("rnr");
        builder.add(tfs[7], cc.xy(3, 22));

        builder.addLabel("ESOL (§302)", cc.xy(1, 24));
        tfs[8] = new JRtaTextField("ZAHLEN", true);
        tfs[8].setName("esol");
        builder.add(tfs[8], cc.xy(3, 24));

        builder.addLabel("Berichte", cc.xy(1, 26));
        tfs[9] = new JRtaTextField("ZAHLEN", true);
        tfs[9].setName("bericht");
        builder.add(tfs[9], cc.xy(3, 26));

        builder.addLabel("Ausfallrechnung", cc.xy(1, 28));
        tfs[10] = new JRtaTextField("ZAHLEN", true);
        tfs[10].setName("afrnr");
        builder.add(tfs[10], cc.xy(3, 28));

        builder.addLabel("Rezeptgebührrechn.", cc.xy(1, 30));
        tfs[11] = new JRtaTextField("ZAHLEN", true);
        tfs[11].setName("rgrnr");
        builder.add(tfs[11], cc.xy(3, 30));

        builder.addLabel("Dokumentation", cc.xy(1, 32));
        tfs[12] = new JRtaTextField("ZAHLEN", true);
        tfs[12].setName("doku");
        builder.add(tfs[12], cc.xy(3, 32));

        builder.addLabel("DFÜ-Nr. (§302)", cc.xy(1, 34));
        tfs[13] = new JRtaTextField("ZAHLEN", true);
        tfs[13].setName("dfue");
        builder.add(tfs[13], cc.xy(3, 34));

        // builder.addLabel("DFÜ-Nr. (§302)",cc.xy(1,32));
        tfs[14] = new JRtaTextField("ZAHLEN", true);
        tfs[14].setName("mandant");

        builder.addLabel("Verkauf bar", cc.xy(1, 36));
        tfs[15] = new JRtaTextField("ZAHLEN", true);
        tfs[15].setName("vbon");
        builder.add(tfs[15], cc.xy(3, 36));

        builder.addLabel("Verkauf Rechnung", cc.xy(1, 38));
        tfs[16] = new JRtaTextField("ZAHLEN", true);
        tfs[16].setName("vrechnung");
        builder.add(tfs[16], cc.xy(3, 38));

        builder.getPanel()
               .validate();
        return builder.getPanel();
    }

    private JPanel getVorlagenSeiteMitRs() {
        // 1. 2. 3. 4. 5. 6. 7. 8. 9.
        FormLayout lay = new FormLayout("right:max(60dlu;p), 4dlu, 40dlu, 4dlu, 40dlu, 4dlu, 40dlu, 4dlu, 40dlu",
                // 1. 2. 3. 4. 5. 6. 7. 8. 9. 10. 11. 12. 13. 14. 15. 16. 17. 18. 19. 20. 21.
                // 22. 23.
                "p, 0dlu, p, p,0dlu,p,2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, " +
                // 21. 22. 23. 24. 25. 26. 27. 28. 29. 30. 31. 32. 33. 34. 35. 36. 37. 38. 39 40
                // 41 42 43 44
                        "2dlu, p , 2dlu ,p , 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p,  2dlu,  p, 2dlu, p, 2dlu, p, 2dlu, p ,2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p ");

        PanelBuilder builder = new PanelBuilder(lay);
        builder.setDefaultDialogBorder();
        builder.getPanel()
               .setOpaque(false);
        CellConstraints cc = new CellConstraints();
        // JLabel lab = new JLabel("Nummernkreis von Mandant: "+Reha.aktIK);
        // lab.setFont(new Font("Tahoma",Font.BOLD,12));
        // builder.add(lab,cc.xyw(3, 4, 7));
        JLabel lab = new JLabel("nächste Nummer für...");
        builder.add(lab, cc.xyw(1, 6, 7));

        builder.addLabel("Patient", cc.xy(1, 8));
        tfs[0] = new JRtaTextField("ZAHLEN", true);
        tfs[0].setName("pat");
        builder.add(tfs[0], cc.xy(3, 8));

        builder.addLabel("KG-Rezept", cc.xy(1, 10));
        tfs[1] = new JRtaTextField("ZAHLEN", true);
        tfs[1].setName("kg");
        builder.add(tfs[1], cc.xy(3, 10));

        builder.addLabel("Massage-Rezept", cc.xy(1, 12));
        tfs[2] = new JRtaTextField("ZAHLEN", true);
        tfs[2].setName("ma");
        builder.add(tfs[2], cc.xy(3, 12));

        builder.addLabel("Ergo-Rezept", cc.xy(1, 14));
        tfs[3] = new JRtaTextField("ZAHLEN", true);
        tfs[3].setName("er");
        builder.add(tfs[3], cc.xy(3, 14));

        builder.addLabel("Logo-Rezept", cc.xy(1, 16));
        tfs[4] = new JRtaTextField("ZAHLEN", true);
        tfs[4].setName("lo");
        builder.add(tfs[4], cc.xy(3, 16));

        builder.addLabel("Reha-Verordnung", cc.xy(1, 18));
        tfs[5] = new JRtaTextField("ZAHLEN", true);
        tfs[5].setName("rh");
        builder.add(tfs[5], cc.xy(3, 18));

        builder.addLabel("Podologie-Verordnung", cc.xy(1, 20));
        tfs[6] = new JRtaTextField("ZAHLEN", true);
        tfs[6].setName("po");
        builder.add(tfs[6], cc.xy(3, 20));

        builder.addLabel("Rehasport-Verordnung", cc.xy(1, 22));
        tfs[7] = new JRtaTextField("ZAHLEN", true);
        tfs[7].setName("rs");
        builder.add(tfs[7], cc.xy(3, 22));

        builder.addLabel("Funktionstr.-Verordnung", cc.xy(1, 24));
        tfs[8] = new JRtaTextField("ZAHLEN", true);
        tfs[8].setName("ft");
        builder.add(tfs[8], cc.xy(3, 24));

        /** kontrolliert bis hierher **/

        builder.addLabel("Rechnungsnummer", cc.xy(1, 26));
        tfs[9] = new JRtaTextField("ZAHLEN", true);
        tfs[9].setName("rnr");
        builder.add(tfs[9], cc.xy(3, 26));

        builder.addLabel("ESOL (§302)", cc.xy(1, 28));
        tfs[10] = new JRtaTextField("ZAHLEN", true);
        tfs[10].setName("esol");
        builder.add(tfs[10], cc.xy(3, 28));

        builder.addLabel("Berichte", cc.xy(1, 30));
        tfs[11] = new JRtaTextField("ZAHLEN", true);
        tfs[11].setName("bericht");
        builder.add(tfs[11], cc.xy(3, 30));

        builder.addLabel("Ausfallrechnung", cc.xy(1, 32));
        tfs[12] = new JRtaTextField("ZAHLEN", true);
        tfs[12].setName("afrnr");
        builder.add(tfs[12], cc.xy(3, 32));

        builder.addLabel("Rezeptgebührrechn.", cc.xy(1, 34));
        tfs[13] = new JRtaTextField("ZAHLEN", true);
        tfs[13].setName("rgrnr");
        builder.add(tfs[13], cc.xy(3, 34));

        builder.addLabel("Dokumentation", cc.xy(1, 36));
        tfs[14] = new JRtaTextField("ZAHLEN", true);
        tfs[14].setName("doku");
        builder.add(tfs[14], cc.xy(3, 36));

        builder.addLabel("DFÜ-Nr. (§302)", cc.xy(1, 38));
        tfs[15] = new JRtaTextField("ZAHLEN", true);
        tfs[15].setName("dfue");
        builder.add(tfs[15], cc.xy(3, 38));

        // builder.addLabel("DFÜ-Nr. (§302)",cc.xy(1,32));

        tfs[16] = new JRtaTextField("ZAHLEN", true);
        tfs[16].setName("mandant");

        builder.addLabel("Verkauf bar", cc.xy(1, 40));
        tfs[17] = new JRtaTextField("ZAHLEN", true);
        tfs[17].setName("vbon");
        builder.add(tfs[17], cc.xy(3, 40));

        builder.addLabel("Verkauf Rechnung", cc.xy(1, 42));
        tfs[18] = new JRtaTextField("ZAHLEN", true);
        tfs[18].setName("vrechnung");
        builder.add(tfs[18], cc.xy(3, 42));

        builder.getPanel()
               .validate();
        return builder.getPanel();
    }

    private JPanel getKnopfPanel() {

        abbruch = new JButton("abbrechen");
        abbruch.setActionCommand("abbrechen");
        abbruch.addActionListener(this);
        speichern = new JButton("speichern");
        speichern.setActionCommand("speichern");
        speichern.addActionListener(this);

        // 1. 2. 3. 4. 5. 6. 7. 8. 9.
        FormLayout jpanlay = new FormLayout("right:max(150dlu;p), 60dlu, 60dlu, 4dlu, 60dlu",
                // 1. 2. 3. 4. 5. 6. 7. 8. 9. 10. 11. 12. 13. 14. 15. 16. 17. 18. 19. 20. 21.
                // 22. 23.
                "5dlu,p, 10dlu, p");

        PanelBuilder jpan = new PanelBuilder(jpanlay);
        jpan.getPanel()
            .setOpaque(false);
        CellConstraints jpancc = new CellConstraints();

        jpan.addSeparator("", jpancc.xyw(1, 2, 5));
        jpan.add(abbruch, jpancc.xy(3, 4));
        jpan.add(speichern, jpancc.xy(5, 4));
        jpan.addLabel("Änderungen übernehmen?", jpancc.xy(1, 4));

        jpan.getPanel()
            .validate();
        return jpan.getPanel();
    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (cmd.equals("abbrechen")) {
            SystemInit.abbrechen();
            return;
        }
        if (cmd.equals("speichern")) {
            if (SystemConfig.mitRs) {
                doSpeichernMitRs();
            } else {
                doSpeichernOhneRs();
            }

            return;
        }
    }

    private void doSpeichernOhneRs() {
        String meldung = "Folgende Nummern wurden geändert\n";
        String cmd = (!nummernkreisok ? "insert into nummern set " : "update nummern set ");
        int edited = 0;
        for (int i = 0; i < 17; i++) {
            if (!tfs[i].getText()
                       .trim()
                       .equals(originale.get(i))) {
                edited++;
                cmd = cmd + (edited > 1 ? "," : "") + tfs[i].getName() + "='" + tfs[i].getText()
                                                                                      .trim()
                        + "'";
                meldung = meldung + tfs[i].getName() + " = " + tfs[i].getText()
                                                                     .trim()
                        + "\n";
            }
        }
        if (edited > 0) {
            cmd = cmd + " LIMIT 1";
            // cmd = cmd+(!nummernkreisok ? "" : " where mandant = '"+Reha.aktIK+"' LIMIT
            // 1");
            meldung = meldung + "\n\n" + "Diese Nummern abspeichern?" + "\n";
            int frage = JOptionPane.showConfirmDialog(null, meldung, "Die geänderten Nummern abspeichern",
                    JOptionPane.YES_NO_OPTION);
            if (frage == JOptionPane.YES_OPTION) {
                SqlInfo.sqlAusfuehren(cmd);
            } else {
                for (int i = 0; i < 17; i++) {
                    tfs[i].setText(originale.get(i));
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Nummernkreis wurde nicht verändert (gute Entscheidung!!)");
            SystemInit.abbrechen();
        }

    }

    private void doSpeichernMitRs() {
        String meldung = "Folgende Nummern wurden geändert\n";
        String cmd = (!nummernkreisok ? "insert into nummern set " : "update nummern set ");
        int edited = 0;
        for (int i = 0; i < 19; i++) {
            if (!tfs[i].getText()
                       .trim()
                       .equals(originale.get(i))) {
                edited++;
                cmd = cmd + (edited > 1 ? "," : "") + tfs[i].getName() + "='" + tfs[i].getText()
                                                                                      .trim()
                        + "'";
                meldung = meldung + tfs[i].getName() + " = " + tfs[i].getText()
                                                                     .trim()
                        + "\n";
            }
        }
        if (edited > 0) {
            cmd = cmd + " LIMIT 1";
            // cmd = cmd+(!nummernkreisok ? "" : " where mandant = '"+Reha.aktIK+"' LIMIT
            // 1");
            meldung = meldung + "\n\n" + "Diese Nummern abspeichern?" + "\n";
            int frage = JOptionPane.showConfirmDialog(null, meldung, "Die geänderten Nummern abspeichern",
                    JOptionPane.YES_NO_OPTION);
            if (frage == JOptionPane.YES_OPTION) {
                SqlInfo.sqlAusfuehren(cmd);
            } else {
                for (int i = 0; i < 19; i++) {
                    tfs[i].setText(originale.get(i));
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Nummernkreis wurde nicht verändert (gute Entscheidung!!)");
            SystemInit.abbrechen();
        }
    }

}
