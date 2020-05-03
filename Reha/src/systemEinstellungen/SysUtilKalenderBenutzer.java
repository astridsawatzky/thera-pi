package systemEinstellungen;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JXPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import CommonTools.JRtaTextField;
import CommonTools.StringTools;
import ag.ion.bion.officelayer.application.OfficeApplicationException;
import ag.ion.bion.officelayer.document.DocumentDescriptor;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.document.IDocumentService;
import ag.ion.bion.officelayer.text.ITextDocument;
import ag.ion.bion.officelayer.text.ITextTable;
import ag.ion.bion.officelayer.text.TextException;
import ag.ion.noa.NOAException;
import hauptFenster.AktiveFenster;
import hauptFenster.Reha;
import terminKalender.KollegenLaden;

public class SysUtilKalenderBenutzer extends JXPanel {
    private static final Logger logger = LoggerFactory.getLogger(SysUtilKalenderBenutzer.class);

    private static final long serialVersionUID = 1L;


    JComboBox   mitarbeiterAuswahl = new JComboBox();
    JRtaTextField anrede = null;
    JRtaTextField vorname = null;
    JRtaTextField nachname = null;
    JRtaTextField matchcode = null;
    JRtaTextField arbstd = null;
    JComboBox abteilung = null;
    JRtaTextField deftakt = null;
    JRtaTextField kalzeile = null;
    JRtaTextField[] jtfeld = { null, null, null, null, null, null, null };





    JButton neu = null;
    JButton loeschen = null;
    JButton aendern = null;
    JButton speichern = null;
    JButton abbrechen = null;
    JButton export = null;
    JCheckBox nichtAnzeigen = new JCheckBox("");

    String[] abteil = new String[6 + SystemConfig.oGruppen.gruppenNamen.size()];

    public ArrayList<String> kollegenDaten = new ArrayList<String>();
    private boolean lneu = false;
    private int speichernKalZeile = 0;

    SysUtilKalenderBenutzer() {
        super(new BorderLayout());
        this.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 0));
        setBackgroundPainter(Reha.instance.compoundPainter.get("SystemInit"));

        abteil[0] = " ";
        abteil[1] = "KG";
        abteil[2] = "MA";
        abteil[3] = "ER";
        abteil[4] = "LO";
        abteil[5] = "SP";
        for (int i = 6; i < 6 + SystemConfig.oGruppen.gruppenNamen.size(); i++) {
            abteil[i] = SystemConfig.oGruppen.gruppenNamen.get(i - 6);
        }

        JComponent panel1 = getForm1();

        JScrollPane jscroll = new JScrollPane();
        jscroll.setOpaque(false);
        jscroll.getViewport()
               .setOpaque(false);
        jscroll.setBorder(null);
        jscroll.getVerticalScrollBar()
               .setUnitIncrement(15);
        jscroll.setViewportView(panel1);

        this.add(jscroll, BorderLayout.CENTER);
        this.addKeyListener(keyadapter);

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                mitarbeiterAuswahl.requestFocus();
            }
        });

        return;
    }

    private JPanel getForm1() {
        // 1. 2. 3. 4. 5. 6. 7. 8. 9.
        FormLayout lay = new FormLayout("right:max(60dlu;p), 4dlu, 40dlu, 4dlu, 40dlu, 4dlu, 40dlu, 4dlu, 40dlu",
                // 1. 2. 3. 4. 5. 6. 7. 8. 9. 10. 11. 12. 13. 14. 15. 16. 17. 18. 19. 20.
                "p, 10dlu, p, 2dlu,p, 2dlu, p, 10dlu, p, 10dlu, p, 2dlu, p, 2dlu, p, 10dlu, 10dlu, p,  2dlu , p");
        PanelBuilder builder = new PanelBuilder(lay);
        builder.setDefaultDialogBorder();
        builder.getPanel()
               .setOpaque(false);
        CellConstraints cc = new CellConstraints();

        neu = new JButton("neu");
        neu.setPreferredSize(new Dimension(70, 20));
        neu.addActionListener(e->neuHandeln());
        neu.setActionCommand("neu");
        neu.addKeyListener(keyadapter);

        loeschen = new JButton("löschen");
        loeschen.setPreferredSize(new Dimension(70, 20));
        loeschen.addActionListener(e->loeschenHandeln());
        loeschen.setActionCommand("loeschen");
        loeschen.addKeyListener(keyadapter);

        aendern = new JButton("ändern");
        aendern.setPreferredSize(new Dimension(70, 20));
        aendern.addActionListener(e-> aendernHandeln());
        aendern.setActionCommand("aendern");
        aendern.addKeyListener(keyadapter);

        speichern = new JButton("speichern");
        speichern.setPreferredSize(new Dimension(70, 20));
        speichern.addActionListener(e->speichernHandeln());
        speichern.setActionCommand("speichern");
        speichern.addKeyListener(keyadapter);

        abbrechen = new JButton("abbrechen");
        abbrechen.setPreferredSize(new Dimension(70, 20));
        abbrechen.addActionListener(e->abbrechenHandeln());
        abbrechen.setActionCommand("abbrechen");
        abbrechen.addKeyListener(keyadapter);

        export = new JButton("export");
        export.setPreferredSize(new Dimension(70, 20));
        export.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread() {
                    @Override
                    public void run() {
                        listeHandeln();
                    }
                }.start();

            }
        });
        export.setActionCommand("liste");
        export.addKeyListener(keyadapter);



        builder.addLabel("Benutzer auswählen", cc.xy(1, 1));
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                comboFuellen();
            }
        });
        mitarbeiterAuswahl.addActionListener(e-> comboAuswerten());
        mitarbeiterAuswahl.setActionCommand("comboaktion");
        builder.add(mitarbeiterAuswahl, cc.xyw(3, 1, 3));
        builder.addLabel("         MA-Liste", cc.xy(7, 1));
        builder.add(export, cc.xy(9, 1));

        builder.addLabel("Anrede", cc.xy(1, 3));
        anrede = new JRtaTextField("nix", true);
        builder.add(anrede, cc.xy(3, 3));
        builder.addLabel("Vorname", cc.xy(1, 5));
        vorname = new JRtaTextField("nix", true);
        builder.add(vorname, cc.xyw(3, 5, 3));
        builder.addLabel("Nachname", cc.xy(1, 7));
        nachname = new JRtaTextField("nix", true);
        builder.add(nachname, cc.xyw(3, 7, 3));
        builder.addSeparator("Kalenderstammdaten", cc.xyw(1, 9, 9));
        builder.addLabel("Matchcode", cc.xy(1, 11));
        matchcode = new JRtaTextField("nix", true);
        builder.add(matchcode, cc.xyw(3, 11, 3));
        builder.addLabel("Arbeitsstd.", cc.xy(1, 13));
        arbstd = new JRtaTextField("FL", true, "10.2", "RECHTS");
        builder.add(arbstd, cc.xyw(3, 13, 1));
        builder.addLabel("        Abteilung", cc.xy(7, 11));
        abteilung = new JComboBox(abteil);
        abteilung.setSelectedIndex(0);
        builder.add(abteilung, cc.xy(9, 11));
        builder.addLabel("Default-Takt", cc.xy(1, 15));
        deftakt = new JRtaTextField("ZAHLEN", true);
        deftakt.setToolTipText(
                "Dieses Feld ist für eine spätere Erweiterung gedacht und hat derzeit noch keinen Einfluß auf den Programmablauf!");
        builder.add(deftakt, cc.xyw(3, 15, 1));

        kalzeile = new JRtaTextField("NORMAL", true);

        builder.addLabel("nicht anzeigen", cc.xy(7, 15));
        builder.add(nichtAnzeigen, cc.xy(9, 15));

        builder.addSeparator("", cc.xyw(1, 16, 9));

        builder.add(neu, cc.xy(1, 18));
        builder.add(loeschen, cc.xy(3, 18));
        builder.add(aendern, cc.xy(5, 18));
        builder.add(speichern, cc.xy(7, 18));
        builder.add(abbrechen, cc.xy(9, 18));

        knopfGedoense(new int[] { 1, 0, 0, 0, 0 });
        felderEinschalten(false);
        builder.getPanel()
               .addKeyListener(keyadapter);
        return builder.getPanel();

    }

    private void comboFuellen() {
        int von = 0;
        int bis = KollegenLaden.vKKollegen.size();
        if (mitarbeiterAuswahl.getItemCount() > 0) {
            mitarbeiterAuswahl.removeAllItems();
        }
        for (von = 0; von < bis; von++) {
            mitarbeiterAuswahl.addItem(KollegenLaden.getMatchcode(von));
        }
        if (bis >= 0) {
            mitarbeiterAuswahl.setSelectedItem("./.");
        }
        mitarbeiterAuswahl.requestFocus();
    }

    private void knopfGedoense(int[] knopfstatus) {
        neu.setEnabled((knopfstatus[0] == 0 ? false : true));
        loeschen.setEnabled((knopfstatus[1] == 0 ? false : true));
        aendern.setEnabled((knopfstatus[2] == 0 ? false : true));
        speichern.setEnabled((knopfstatus[3] == 0 ? false : true));
        abbrechen.setEnabled((knopfstatus[4] == 0 ? false : true));
    }

    private void comboAuswerten() {
        if (mitarbeiterAuswahl.getSelectedIndex() > 0) {
            holeKollege((String) mitarbeiterAuswahl.getSelectedItem());
            felderFuellen(kollegenDaten);
            knopfGedoense(new int[] { 1, 1, 1, 0, 0 });
        } else {
            kollegenDaten.clear();
            for (int i = 0; i <= 8; i++) {
                kollegenDaten.add("");
            }
            felderFuellen(kollegenDaten);
            knopfGedoense(new int[] { 1, 0, 0, 0, 0 });
        }
        felderEinschalten(false);
    }

    private void felderEinschalten(boolean einschalten) {
        anrede.setEnabled(einschalten);
        anrede.validate();
        vorname.setEnabled(einschalten);
        vorname.validate();
        nachname.setEnabled(einschalten);
        matchcode.setEnabled(einschalten);
        arbstd.setEnabled(einschalten);
        abteilung.setEnabled(einschalten);
        deftakt.setEnabled(einschalten);
        nichtAnzeigen.setEnabled(einschalten);

    }

    private void felderFuellen(ArrayList<String> felder) {
        anrede.setText(felder.get(0));
        vorname.setText(felder.get(1));
        nachname.setText(felder.get(2));
        matchcode.setText(felder.get(3));
        DecimalFormat df = new DecimalFormat("#########0.00");
        arbstd.setText((felder.get(4)
                              .trim()
                              .equals("") ? df.format(new Double(0.00)) : df.format(new Double(felder.get(4)))));
        abteilung.setSelectedItem(felder.get(5));
        deftakt.setText(felder.get(6));
        kalzeile.setText(felder.get(7));
        nichtAnzeigen.setSelected((felder.get(8)
                               .equals("T") ? true : false));
    }

    private void neuHandeln() {
        if (KollegenLaden.vKKollegen.size() == 99) {
            JOptionPane.showMessageDialog(null,
                    "Es existieren bereits 99 Kalenderbenutzer! Derezeit ist die Benutzeranzahl auf 99 limitiert!");
            return;
        }
        lneu = true;
        knopfGedoense(new int[] { 0, 0, 0, 1, 1 });
        kollegenDaten.clear();
        for (int i = 0; i <= 8; i++) {
            kollegenDaten.add("");
        }
        felderEinschalten(true);
        felderFuellen(kollegenDaten);
        anrede.requestFocus();
    }

    private void speichernHandeln() {
        if (matchcode.getText()
                     .trim()
                     .contains(",")) {
            JOptionPane.showMessageDialog(null, "Ein Komma im Feld 'Matchcode' ist nicht erlaubt");
            return;
        }
        if (matchcode.getText()
                     .trim()
                     .equals("")) {
            JOptionPane.showMessageDialog(null, "Das Feld 'Matchcode' darf nicht leer sein");
            return;
        }
        boolean lneueZeile = false;
        String statement = null;
        if (lneu) {
            if (matchVorhanden(matchcode.getText()
                                        .trim())) {
                JOptionPane.showMessageDialog(null, "Dieser 'Matchcode' ist bereits vorhanden");
                return;
            }

            lneueZeile = testObNeueKalZeile();
            if (lneueZeile) {
                statement = "Insert into kollegen2 set Anrede='" + StringTools.Escaped(anrede.getText()
                                                                                             .trim())
                        + "', " + "Vorname='" + StringTools.Escaped(vorname.getText()
                                                                           .trim())
                        + "', " + "Nachname='" + StringTools.Escaped(nachname.getText()
                                                                             .trim())
                        + "', " + "Matchcode='" + StringTools.Escaped(matchcode.getText()
                                                                               .trim())
                        + "', " + "Astunden='" + arbstd.getText()
                                                       .trim()
                                                       .replace(",", ".")
                        + "', " + "Abteilung='" + abteilung.getSelectedItem() + "', " + "Deftakt='" + (deftakt.getText()
                                                                                                              .trim()
                                                                                                              .equals("")
                                                                                                                      ? "0"
                                                                                                                      : deftakt.getText())
                        + "', " + "Nicht_Zeig='" + (nichtAnzeigen.isSelected() ? "T" : "F") + "', " + "Kalzeile='"
                        + Integer.valueOf(speichernKalZeile)
                                 .toString()
                        + "'";
            } else {
                statement = "Insert into kollegen2 set Anrede='"
                        + anrede.getText() + "', " + "Vorname='" + vorname.getText() + "', " + "Nachname='"
                        + nachname.getText() + "', " + "Matchcode='" + matchcode.getText() + "', " + "Astunden='"
                        + arbstd.getText()
                                .trim()
                                .replace(",", ".")
                        + "', " + "Abteilung='" + abteilung.getSelectedItem() + "', " + "Deftakt='" + (deftakt.getText()
                                                                                                              .trim()
                                                                                                              .equals("")
                                                                                                                      ? "0"
                                                                                                                      : deftakt.getText())
                        + "', " + "Nicht_Zeig='" + (nichtAnzeigen.isSelected() ? "T" : "F") + "', " + "Kalzeile='"
                        + Integer.valueOf(speichernKalZeile)
                                 .toString()
                        + "'";
            }
        } else {
            statement = "Update kollegen2 set Anrede='" + anrede.getText() + "', " + "Vorname='" + vorname.getText()
                    + "', " + "Nachname='" + nachname.getText() + "', " + "Matchcode='" + matchcode.getText() + "', "
                    + "Astunden='" + arbstd.getText()
                                           .trim()
                                           .replace(",", ".")
                    + "', " + "Abteilung='" + abteilung.getSelectedItem() + "', " + "Deftakt='" + (deftakt.getText()
                                                                                                          .trim()
                                                                                                          .equals("")
                                                                                                                  ? "0"
                                                                                                                  : deftakt.getText())
                    + "', " + "Nicht_Zeig='" + (nichtAnzeigen.isSelected() ? "T" : "F") + "'" + "where Kalzeile='"
                    + kalzeile.getText() + "'";
        }
        knopfGedoense(new int[] { 1, 1, 1, 0, 0 });
        lneu = false;
        executeStatement(statement);
        String aktuell = matchcode.getText();
        KollegenLaden.Init();
        comboFuellen();
        mitarbeiterAuswahl.setSelectedItem(aktuell);
        comboAuswerten();
        felderEinschalten(false);
        JComponent termin = AktiveFenster.getFensterAlle("TerminFenster");
        if (termin != null) {
            Reha.instance.terminpanel.setCombosOutside();
            JOptionPane.showMessageDialog(null, "Die Kalenderbenutzer wurden geändert!\n"
                    + "Die Behandlersets des aktiven Terminkalender wurden zurückgesetzt.");
        }

    }

    private void loeschenHandeln() {
        knopfGedoense(new int[] { 1, 1, 1, 0, 0 });
        lneu = false;
        String statement = null;
        int anfrage = JOptionPane.showConfirmDialog(null, "Wollen Sie diesen Kalenderbenutzer wirklich löschen",
                "Achtung wichtige Benutzeranfrage", JOptionPane.YES_NO_OPTION);
        if (anfrage == JOptionPane.YES_OPTION) {
            int aktwahl = mitarbeiterAuswahl.getSelectedIndex();
            if (aktwahl > 0) {
                statement = "Delete from kollegen2 where Kalzeile='" + kalzeile.getText() + "'";
                executeStatement(statement);
                KollegenLaden.Init();
                comboFuellen();
                mitarbeiterAuswahl.setSelectedIndex(aktwahl - 1);
                comboAuswerten();
                JComponent termin = AktiveFenster.getFensterAlle("TerminFenster");
                if (termin != null) {
                    Reha.instance.terminpanel.setCombosOutside();
                    JOptionPane.showMessageDialog(null, "Die Kalenderbenutzer wurden geändert!\n"
                            + "Die Behandlersets des aktiven Terminkalender wurden zurückgesetzt.");
                }

            }
        }
    }

    private void aendernHandeln() {
        felderEinschalten(true);
        knopfGedoense(new int[] { 0, 0, 0, 1, 1 });
        anrede.requestFocus();
        lneu = false;
    }

    private void abbrechenHandeln() {
        knopfGedoense(new int[] { 1, 0, 1, 0, 0 });
        lneu = false;
        for (int i = 0; i <= 7; i++) {
            kollegenDaten.add("");
        }
        felderEinschalten(false);
        comboAuswerten();
        SystemInit.abbrechen();
    }

    private void listeHandeln() {
        IDocumentService documentService = null;
        try {
            documentService = Reha.officeapplication.getDocumentService();
        } catch (OfficeApplicationException e) {
            e.printStackTrace();
        }
        IDocument document = null;
        try {
            document = documentService.constructNewDocument(IDocument.WRITER, DocumentDescriptor.DEFAULT);
        } catch (NOAException e) {
            e.printStackTrace();
        }
        ITextDocument textDocument = (ITextDocument) document;
        ITextTable textTable = null;
        try {
            textTable = textDocument.getTextTableService()
                                    .constructTextTable(KollegenLaden.vKKollegen.size() + 1, 3);
        } catch (TextException e) {
            e.printStackTrace();
        }
        try {
            textDocument.getTextService()
                        .getTextContentService()
                        .insertTextContent(textTable);
        } catch (TextException e) {
            e.printStackTrace();
        }
        try {
            textTable.getCell(0, 0)
                     .getTextService()
                     .getText()
                     .setText("Rang im Kalender");
            textTable.getCell(1, 0)
                     .getTextService()
                     .getText()
                     .setText("MatchCode");
            textTable.getCell(2, 0)
                     .getTextService()
                     .getText()
                     .setText("Zeile im Kalender");
        } catch (TextException exception) {
            exception.printStackTrace();
        }

        for (int i = 0; i < KollegenLaden.vKKollegen.size(); i++) {
            try {
                textTable.getCell(0, i + 1)
                         .getTextService()
                         .getText()
                         .setText(Integer.valueOf(i)
                                         .toString());
                textTable.getCell(1, i + 1)
                         .getTextService()
                         .getText()
                         .setText(KollegenLaden.getMatchcode(i));
                textTable.getCell(2, i + 1)
                         .getTextService()
                         .getText()
                         .setText(Integer.valueOf(KollegenLaden.getDBZeile(i))
                                         .toString());
            } catch (TextException exception) {
                exception.printStackTrace();
            }
        }
    }


 KeyListener keyadapter = new KeyAdapter() {

    @Override
    public void keyPressed(KeyEvent arg0) {
        if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
            arg0.consume();
        }

    }

    @Override
    public void keyReleased(KeyEvent arg0) {
        if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
            arg0.consume();
        }

    }

    @Override
    public void keyTyped(KeyEvent arg0) {
        if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
            arg0.consume();
        }

    }
 };
    private boolean testObNeueKalZeile() {
        boolean ret = false;
        if ((KollegenLaden.vKKollegen.size() >= (KollegenLaden.maxKalZeile + 1))) {
            // Es mu� eine neue Kalenderzeile belegt werden.
            speichernKalZeile = KollegenLaden.maxKalZeile + 1;
            ret = true;
            return ret;

        } else {
            // Es mu� nach einer freien also unbelegten Kalenderzeile gesucht werden.
            testeKollegen();
            ret = false;
        }
        return ret;
    }

    /***********************************************************/
    private void holeKollege(String match) {
        try( Statement  stmt = Reha.instance.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                ResultSet   rs = stmt.executeQuery("SELECT * FROM kollegen2 where Matchcode='" + match + "'");


                )

        {

            String test = null;
            while (rs.next()) {
                kollegenDaten.clear();
                test = rs.getString("Anrede");
                kollegenDaten.add(String.valueOf((test != null ? test : "")));
                test = rs.getString("Vorname");
                kollegenDaten.add(String.valueOf((test != null ? test : "")));
                test = rs.getString("Nachname");
                kollegenDaten.add(String.valueOf((test != null ? test : "")));
                test = rs.getString("Matchcode");
                kollegenDaten.add(String.valueOf((test != null ? test : "")));
                test = rs.getString("Astunden");
                kollegenDaten.add(String.valueOf((test != null ? test : "")));
                test = rs.getString("Abteilung");
                kollegenDaten.add(String.valueOf((test != null ? test : "")));
                test = rs.getString("Deftakt");
                kollegenDaten.add(String.valueOf((test != null ? test : "")));
                test = rs.getString("Kalzeile");
                kollegenDaten.add(String.valueOf((test != null ? test : "")));
                test = rs.getString("Nicht_zeig");
                kollegenDaten.add(String.valueOf((test != null ? test : "F")));
            }

        } catch (SQLException ex) {
            logger.error("Something bad happened here",ex);
        }
    }

    private int testeKollegen() {
        int itest = 0;
        try (Statement stmt = Reha.instance.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_UPDATABLE);
                ResultSet rs = stmt.executeQuery("SELECT KALZEILE FROM kollegen2 ORDER BY KALZEILE");) {
            int durchlauf = 0;

            while (rs.next()) {
                if (durchlauf == 0) {
                    itest = rs.getInt("KALZEILE");
                    if (itest > 1) {
                        speichernKalZeile = 1;
                        break;
                    }
                } else {
                    if (rs.getInt("KALZEILE") > (itest + 1)) {
                        speichernKalZeile = itest + 1;
                        break;
                    } else {
                        itest = rs.getInt("KALZEILE");
                    }
                }
                durchlauf++;
            }

        } catch (SQLException ex) {

        }
        return itest;
    }

    private void executeStatement(String match) {
        try (Statement stmt = Reha.instance.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_UPDATABLE);) {
            stmt.execute(match);

        } catch (SQLException ex) {
        }
    }

    /**
     * Ueberprueft, ob der eingebene Matchcode bereits benutzt wird.
     *
     * @param match mtchcode des Mitarbeiters
     * @return true wenn der matchcode schon vorhanden ist.
     */
    private boolean matchVorhanden(String match) {
        boolean ret = true;
        try (Statement stmt = Reha.instance.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_UPDATABLE);
                ResultSet rs = stmt.executeQuery("select count(*) from kollegen2 where matchcode='" + match + "'");) {
            if (rs.next()) {
                if (rs.getInt(1) == 0) {
                    ret = false;
                }
            }
        } catch (SQLException ex) {

        }

        return ret;
    }

}
