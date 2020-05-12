package systemEinstellungen;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.swing.*;

import org.jdesktop.swingx.JXPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import CommonTools.JRtaTextField;
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
import mandant.IK;
import mitarbeiter.Mitarbeiter;
import mitarbeiter.MitarbeiterDto;
import terminKalender.KollegenListe;

class Swingma {
    private Mitarbeiter ma;

    public Swingma(Mitarbeiter ma) {
        this.ma = ma;

    }

    Mitarbeiter getMitarbeiter() {
        return ma;
    }

    @Override
    public String toString() {
        return getMitarbeiter().getMatchcode();
    }
}

class SysUtilKalenderBenutzer extends JXPanel {
    private static final Logger logger = LoggerFactory.getLogger(SysUtilKalenderBenutzer.class);

    private static final long serialVersionUID = 1L;

    private JTextField anrede = null;
    private JRtaTextField vorname = null;
    private JRtaTextField nachname = null;
    private JRtaTextField matchcode = null;
    private JFormattedTextField arbstd = null;
    private JRtaTextField deftakt = null;

    private JButton neu = null;
    private JButton loeschen = null;
    private JButton aendern = null;
    private JButton speichern = null;
    private JButton abbrechen = null;
    private JButton export = null;
    private JCheckBox nichtAnzeigen = new JCheckBox("");

    private JComboBox<Abteilung> abteilCombo;
    private JComboBox<Swingma> maComboBox;

    private List<Mitarbeiter> ma;

    private IK ik;

    /**
     * @deprecated Use {@link #SysUtilKalenderBenutzer(IK)} instead
     */
    SysUtilKalenderBenutzer() {
        this(Reha.instance.mandant()
                          .ik());
    }

    SysUtilKalenderBenutzer(IK ik) {
        super(new BorderLayout());
        this.ik = ik;

        this.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 0));

        setBackgroundPainter(Reha.instance.compoundPainter.get("SystemInit"));

        ma = new MitarbeiterDto(ik).all();

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
                maComboBox.requestFocus();
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
        neu.addActionListener(e -> neuHandeln());
        neu.setActionCommand("neu");
        neu.addKeyListener(keyadapter);

        loeschen = new JButton("löschen");
        loeschen.setPreferredSize(new Dimension(70, 20));
        loeschen.addActionListener(e -> loeschenHandeln());
        loeschen.setActionCommand("loeschen");
        loeschen.addKeyListener(keyadapter);

        aendern = new JButton("ändern");
        aendern.setPreferredSize(new Dimension(70, 20));
        aendern.addActionListener(e -> aendernHandeln());
        aendern.setActionCommand("aendern");
        aendern.addKeyListener(keyadapter);

        speichern = new JButton("speichern");
        speichern.setPreferredSize(new Dimension(70, 20));
        speichern.addActionListener(e -> speichernHandeln());
        speichern.setActionCommand("speichern");
        speichern.addKeyListener(keyadapter);

        abbrechen = new JButton("abbrechen");
        abbrechen.setPreferredSize(new Dimension(70, 20));
        abbrechen.addActionListener(e -> abbrechenHandeln());
        abbrechen.setActionCommand("abbrechen");
        abbrechen.addKeyListener(keyadapter);

        export = new JButton("export");
        export.setPreferredSize(new Dimension(70, 20));
        export.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                listeDrucken();

            }
        });
        export.setActionCommand("liste");
        export.addKeyListener(keyadapter);

        builder.addLabel("Benutzer auswählen", cc.xy(1, 1));
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
        builder.addLabel("Default-Takt", cc.xy(1, 15));

        deftakt = new JRtaTextField("ZAHLEN", true);
        deftakt.setToolTipText(
                "Dieses Feld ist für eine spätere Erweiterung gedacht und hat derzeit noch keinen Einfluß auf den Programmablauf!");
        builder.add(deftakt, cc.xyw(3, 15, 1));


        builder.addLabel("nicht anzeigen", cc.xy(7, 15));
        builder.add(nichtAnzeigen, cc.xy(9, 15));

        builder.addSeparator("", cc.xyw(1, 16, 9));

        builder.add(neu, cc.xy(1, 18));
        builder.add(loeschen, cc.xy(3, 18));
        builder.add(aendern, cc.xy(5, 18));
        builder.add(speichern, cc.xy(7, 18));
        builder.add(abbrechen, cc.xy(9, 18));

        buttonsEmptyMode();
        builder.getPanel()
               .addKeyListener(keyadapter);

        Swingma[] swingmaarray = ma.stream()
                                   .map(Swingma::new)
                                   .sorted(Comparator.comparing(Swingma::getMitarbeiter))
                                   .collect(Collectors.toList())
                                   .toArray(new Swingma[0]);

        List<Abteilung> all = new AbteilungDTO(ik).all();
        abteilCombo = new JComboBox<Abteilung>(all.toArray(new Abteilung[all.size()]));

        maComboBox = new JComboBox<Swingma>(swingmaarray);
        maComboBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                Swingma swMitarbeiter = (Swingma) maComboBox.getSelectedItem();

                if (swMitarbeiter == null) {
                    felderleeren();
                    felderEinschalten(false);
                    buttonsEmptyMode();
                } else {
                    fillForm(swMitarbeiter.getMitarbeiter());
                    buttonsViewMode();
                    felderEinschalten(false);
                }
            }

        });

        builder.add(maComboBox, cc.xyw(3, 1, 3));
        builder.add(abteilCombo, cc.xy(9, 11));

        eingabenDeactivate();
        return builder.getPanel();

    }

    private void fillForm(Mitarbeiter ma) {
        anrede.setText(ma.getAnrede());
        vorname.setText(ma.getVorname());
        nachname.setText(ma.getNachname());
        matchcode.setText(ma.getMatchcode());
        arbstd.setText(String.valueOf(ma.getAstunden()));
        deftakt.setText(String.valueOf(ma.getDeftakt()));

        abteilCombo.getModel()
                   .setSelectedItem(new Abteilung(ma.getAbteilung()));
        nichtAnzeigen.setSelected(ma.isNicht_zeig());

    }

    private void knopfGedoense(int[] knopfstatus) {
        neu.setEnabled((knopfstatus[0] == 0 ? false : true));
        loeschen.setEnabled((knopfstatus[1] == 0 ? false : true));
        aendern.setEnabled((knopfstatus[2] == 0 ? false : true));
        speichern.setEnabled((knopfstatus[3] == 0 ? false : true));
        abbrechen.setEnabled((knopfstatus[4] == 0 ? false : true));
    }

    private void eingabenDeactivate() {
        felderEinschalten(false);
    }

    private void buttonsEmptyMode() {
        knopfGedoense(new int[] { 1, 0, 0, 0, 0 });
    }

    private void felderEinschalten(boolean einschalten) {
        anrede.setEnabled(einschalten);
        anrede.validate();
        vorname.setEnabled(einschalten);
        vorname.validate();
        nachname.setEnabled(einschalten);
        matchcode.setEnabled(einschalten);
        arbstd.setEnabled(einschalten);
        deftakt.setEnabled(einschalten);
        nichtAnzeigen.setEnabled(einschalten);
        abteilCombo.setEnabled(einschalten);

    }

    private void felderleeren() {
        maComboBox.setSelectedItem(null);
        anrede.setText("");
        vorname.setText("");
        nachname.setText("");
        matchcode.setText("");
        arbstd.setText("");
        deftakt.setText("");

        abteilCombo.getModel()
                   .setSelectedItem(null);
        nichtAnzeigen.setSelected(false);

    }

    private void neuHandeln() {

        felderleeren();

        if (ma.size() == 99) {
            JOptionPane.showMessageDialog(null,
                    "Es existieren bereits 99 Kalenderbenutzer! Derezeit ist die Benutzeranzahl auf 99 limitiert!");
            return;
        }
        buttonsEditMode();
        eingabenActivate();
        anrede.requestFocus();
    }

    private void speichernHandeln() {
        if (matchcode.getText()

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
        if (maComboBox.getSelectedIndex() == -1) {

            Optional<Mitarbeiter> maxzeile = ma.stream()
                                               .max(Comparator.comparingInt(Mitarbeiter::getKalzeile));

            int neueKalenderZeile;
            Integer groesteKalenderzeile = maxzeile.map(mitarbeiter -> mitarbeiter.getKalzeile())
                                                   .orElse(0);
            if (ma.size() > groesteKalenderzeile) {
                neueKalenderZeile = groesteKalenderzeile + 1;

            } else {

                neueKalenderZeile = findKalzeilenGapFromDB();
            }

            ;

            Mitarbeiter neuerMitarbeiter = new Mitarbeiter();
            neuerMitarbeiter.setAnrede(anrede.getText()
                                             .trim());
            neuerMitarbeiter.setVorname(vorname.getText()
                                               .trim());
            neuerMitarbeiter.setNachname(nachname.getText()
                                                 .trim());
            neuerMitarbeiter.setMatchcode(matchcode.getText()
                                                   .trim());
            double astunden;

            try {
                astunden = Double.parseDouble(arbstd.getText()
                                                    .trim()
                                                    .replace(",", "."));
            } catch (NumberFormatException e) {
                astunden = 0.0;
            }

            neuerMitarbeiter.setAstunden(astunden);
            neuerMitarbeiter.setKalzeile(neueKalenderZeile);

            new MitarbeiterDto(ik).save(neuerMitarbeiter);
            Swingma newItem = new Swingma(neuerMitarbeiter);
            maComboBox.addItem(newItem);
            maComboBox.setSelectedItem(newItem);

        } else {
            Mitarbeiter currentMa = maComboBox.getItemAt(maComboBox.getSelectedIndex())
                                              .getMitarbeiter();
            currentMa.setAnrede(anrede.getText()
                                      .trim());
            currentMa.setVorname(vorname.getText()
                                        .trim());
            currentMa.setNachname(nachname.getText()
                                          .trim());
            currentMa.setMatchcode(matchcode.getText()
                                            .trim());
            double astunden;

            try {
                astunden = Double.parseDouble(arbstd.getText()
                                                    .trim()
                                                    .replace(",", "."));
            } catch (NumberFormatException e) {
                astunden = 0.0;
                arbstd.setText("0");
            }
            currentMa.setAstunden(astunden);
            new MitarbeiterDto(ik).save(currentMa);
        }

        buttonsViewMode();
        KollegenListe.Init();
        eingabenDeactivate();
        JComponent termin = AktiveFenster.getFensterAlle("TerminFenster");
        if (termin != null) {
            Reha.instance.terminpanel.setCombosOutside();
            JOptionPane.showMessageDialog(null, "Die Kalenderbenutzer wurden geändert!\n"
                    + "Die Behandlersets des aktiven Terminkalender wurden zurückgesetzt.");
        }

    }

    private int findKalzeilenGapFromDB() {

        return new MitarbeiterDto(ik).findgap();
    }

    private void buttonsViewMode() {
        knopfGedoense(new int[] { 1, 1, 1, 0, 0 });
    }

    private void loeschenHandeln() {
        buttonsViewMode();
        int anfrage = JOptionPane.showConfirmDialog(null, "Wollen Sie diesen Kalenderbenutzer wirklich löschen",
                "Achtung wichtige Benutzeranfrage", JOptionPane.YES_NO_OPTION);
        if (anfrage == JOptionPane.YES_OPTION) {
            Swingma aktwahl = (Swingma) maComboBox.getSelectedItem();
            if (new MitarbeiterDto(ik).delete(aktwahl.getMitarbeiter())) {
                maComboBox.removeItem(aktwahl);

                KollegenListe.Init();

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
        eingabenActivate();
        buttonsEditMode();
        anrede.requestFocus();
    }

    private void eingabenActivate() {
        felderEinschalten(true);
    }

    private void buttonsEditMode() {
        knopfGedoense(new int[] { 0, 0, 0, 1, 1 });
    }

    private void abbrechenHandeln() {
        Object selectedItem = maComboBox.getSelectedItem();
        buttonsEmptyMode();
        felderleeren();
        eingabenDeactivate();
        maComboBox.setSelectedItem(selectedItem);
    }

    private void listeDrucken() {
        new Thread() {
            @Override
            public void run() {
                IDocumentService documentService = null;
                try {
                    documentService = Reha.officeapplication.getDocumentService();

                    ITextDocument textDocument = (ITextDocument) documentService.constructNewDocument(IDocument.WRITER,
                            DocumentDescriptor.DEFAULT);
                    ITextTable textTable = null;

                    textTable = textDocument.getTextTableService()
                                            .constructTextTable(1, 3);

                    textDocument.getTextService()
                                .getTextContentService()
                                .insertTextContent(textTable);

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

                    for (Mitarbeiter mitarbeiter : ma) {
                        textTable.addRow(1);
                        textTable.getCell(0, textTable.getRowCount() - 1)
                                 .getTextService()
                                 .getText()
                                 .setText(String.valueOf(textTable.getRowCount() - 1));
                        textTable.getCell(1, textTable.getRowCount() - 1)
                                 .getTextService()
                                 .getText()
                                 .setText(mitarbeiter.getMatchcode());
                        textTable.getCell(2, textTable.getRowCount() - 1)
                                 .getTextService()
                                 .getText()
                                 .setText(String.valueOf(mitarbeiter.getKalzeile()));

                    }

                } catch (TextException | OfficeApplicationException | NOAException exception) {
                   logger.error("Could not create Mitarbeiterlistendokument",exception);
                }
            }
        }.start();
    }

    private KeyListener keyadapter = new KeyAdapter() {

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




}
