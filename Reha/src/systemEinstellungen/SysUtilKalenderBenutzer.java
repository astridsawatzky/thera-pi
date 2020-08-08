package systemEinstellungen;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FocusTraversalPolicy;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.swing.*;
import javax.swing.text.MaskFormatter;

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
import opRgaf.CommonTools.DateTimeFormatters;
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

    private JButton neuBt = null;
    private JButton loeschenBt = null;
    private JButton aendernBt = null;
    private JButton speichernBt = null;
    private JButton abbrechenBt = null;
    private JButton exportBt = null;
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
        FormLayout lay = new FormLayout("right:max(60dlu;p), 4dlu, 40dlu, 4dlu, 40dlu, 4dlu, 40dlu, 40dlu, 40dlu",
                // 1. 2. 3. 4. 5. 6. 7. 8. 9. 10. 11. 12. 13. 14. 15. 16. 17. 18. 19. 20.
                "p, 10dlu, p, 2dlu,p, 2dlu, p, 10dlu, p,  2dlu, p, 10dlu,"
                        + " 10dlu, p, 2dlu, p, 2dlu, p, 10dlu, 10dlu, p,  2dlu , p");
        PanelBuilder builder = new PanelBuilder(lay);
        builder.setDefaultDialogBorder();
        builder.getPanel()
               .setOpaque(false);
        CellConstraints cc = new CellConstraints();

        neuBt = new JButton("neu");
        neuBt.setPreferredSize(new Dimension(70, 20));
        neuBt.addActionListener(e -> neuHandeln());
        neuBt.setActionCommand("neu");
        neuBt.addKeyListener(keyadapter);

        loeschenBt = new JButton("löschen");
        loeschenBt.setPreferredSize(new Dimension(70, 20));
        loeschenBt.addActionListener(e -> loeschenHandeln());
        loeschenBt.setActionCommand("loeschen");
        loeschenBt.addKeyListener(keyadapter);

        aendernBt = new JButton("ändern");
        aendernBt.setPreferredSize(new Dimension(70, 20));
        aendernBt.addActionListener(e -> aendernHandeln());
        aendernBt.setActionCommand("aendern");
        aendernBt.addKeyListener(keyadapter);

        speichernBt = new JButton("speichern");
        speichernBt.setPreferredSize(new Dimension(70, 20));
        speichernBt.addActionListener(e -> speichernHandeln());
        speichernBt.setActionCommand("speichern");
        speichernBt.addKeyListener(keyadapter);

        abbrechenBt = new JButton("abbrechen");
        abbrechenBt.setPreferredSize(new Dimension(70, 20));
        abbrechenBt.addActionListener(e -> abbrechenHandeln());
        abbrechenBt.setActionCommand("abbrechen");
        abbrechenBt.addKeyListener(keyadapter);

        exportBt = new JButton("export");
        exportBt.setPreferredSize(new Dimension(70, 20));
        exportBt.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                listeDrucken();

            }
        });
        exportBt.setActionCommand("liste");
        exportBt.addKeyListener(keyadapter);
        builder.addLabel("Benutzer auswählen", cc.xy(1, 1));
        builder.addLabel("    MA-Liste", cc.xy(7, 1));
        builder.add(exportBt, cc.xy(9, 1));

        builder.addLabel("Anrede", cc.xy(1, 3));
        anrede = new JRtaTextField("nix", true);
        builder.add(anrede, cc.xy(3, 3));
        builder.addLabel("Vorname", cc.xy(1, 5));
        vorname = new JRtaTextField("nix", true);
        builder.add(vorname, cc.xyw(3, 5, 3));
        builder.addLabel("Nachname", cc.xy(1, 7));

        nachname = new JRtaTextField("nix", true);
        builder.add(nachname, cc.xyw(3, 7, 3));

        builder.addLabel("geboren", cc.xy(7, 3));

        geboren = new JFormattedTextField(newDateMask());
        geboren.setDisabledTextColor(Color.red);
        builder.add(geboren, cc.xy(9, 3));

        builder.addLabel("Stra\u00dfe", cc.xy(7, 5));
        strasse = new JTextField();
        strasse.setDisabledTextColor(Color.red);
        builder.add(strasse, cc.xyw(8, 5, 2));

        builder.addLabel("PLZ   Ort", cc.xy(7, 7));
        plz = new JTextField();
        plz.setDisabledTextColor(Color.red);
        builder.add(plz, cc.xy(8, 7));
        ort = new JTextField();
        ort.setDisabledTextColor(Color.red);
        builder.add(ort, cc.xy(9, 7));

        builder.addLabel("Tel", cc.xy(1, 9));
        telefon1 = new JTextField();
        telefon1.setDisabledTextColor(Color.red);
        builder.add(telefon1, cc.xyw(3, 9, 3));
        builder.addLabel("Tel", cc.xy(7, 9));
        telefon2 = new JTextField();
        telefon2.setDisabledTextColor(Color.red);
        builder.add(telefon2, cc.xyw(8, 9, 2));

        builder.addSeparator("Kalenderstammdaten", cc.xyw(1, 12, 9));
        builder.addLabel("Matchcode", cc.xy(1, 14));

        matchcode = new JRtaTextField("nix", true);
        builder.add(matchcode, cc.xyw(3, 14, 3));
        builder.addLabel("Arbeitsstd.", cc.xy(1, 16));

        arbstd = new JRtaTextField("FL", true, "10.2", "RECHTS");
        builder.add(arbstd, cc.xyw(3, 16, 1));

        builder.addLabel("        Abteilung", cc.xy(7, 14));

        builder.addLabel("Default-Takt", cc.xy(1, 18));

        deftakt = new JRtaTextField("ZAHLEN", true);
        deftakt.setToolTipText(
                "Dieses Feld ist für eine spätere Erweiterung gedacht und hat derzeit noch keinen Einfluß auf den Programmablauf!");
        builder.add(deftakt, cc.xyw(3, 18, 1));

        builder.addLabel("nicht anzeigen", cc.xy(7, 18));
        builder.add(nichtAnzeigen, cc.xy(9, 18));

        builder.addSeparator("", cc.xyw(1, 19, 9));

        builder.add(neuBt, cc.xy(1, 21));
        builder.add(loeschenBt, cc.xy(3, 21));
        builder.add(aendernBt, cc.xy(5, 21));
        builder.add(speichernBt, cc.xy(8, 21));
        builder.add(abbrechenBt, cc.xy(9, 21));

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
                    buttonsEmptyMode();
                } else {
                    fillForm(swMitarbeiter.getMitarbeiter());
                    buttonsViewMode();
                }
                eingabenDeactivate();
            }

        });

        builder.add(maComboBox, cc.xyw(3, 1, 3));
        builder.add(abteilCombo, cc.xy(9, 14));

        eingabenDeactivate();

        traversal();
        builder.getPanel().setFocusCycleRoot(true);
        builder.getPanel()
               .setFocusTraversalPolicy(traversal());
        return builder.getPanel();

    }

    private KalBenutzerTraversalPolicy traversal() {
        List<Component> components = new LinkedList<>();
        components.add(maComboBox);
        components.add(anrede);
        components.add(vorname);
        components.add(nachname);
        components.add(geboren);
        components.add(strasse);
        components.add(plz);
        components.add(ort);
        components.add(telefon1);
        components.add(telefon2);
        components.add(matchcode);
        components.add(abteilCombo);
        components.add(arbstd);
        components.add(deftakt);
        components.add(nichtAnzeigen);
        return new KalBenutzerTraversalPolicy(components);
    }

    private MaskFormatter newDateMask() {
        MaskFormatter mf = null;
        try {
            mf = new MaskFormatter("##.##.####");
        } catch (ParseException e1) {
            // Cannot happen
        }
        return mf;
    }

    private void fillForm(Mitarbeiter ma) {
        anrede.setText(ma.getAnrede());
        vorname.setText(ma.getVorname());
        nachname.setText(ma.getNachname());

        strasse.setText(ma.getStrasse());
        plz.setText(ma.getPlz());
        ort.setText(ma.getOrt());

        telefon1.setText(ma.getTelefon1());
        telefon2.setText(ma.getTelfon2());

        LocalDate geborenma = ma.getGeboren();
        String format = geborenma == null ? null : geborenma.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        geboren.setText(format);

        matchcode.setText(ma.getMatchcode());
        arbstd.setText(String.valueOf(ma.getAstunden()));
        deftakt.setText(String.valueOf(ma.getDeftakt()));

        abteilCombo.getModel()
                   .setSelectedItem(new Abteilung(ma.getAbteilung()));
        nichtAnzeigen.setSelected(ma.isNicht_zeig());

    }

    /**
     * @deprecated Use
     *             {@link #knopfGedoense(boolean,boolean, boolean, boolean, boolean)}
     *             instead
     */
    private void knopfGedoense(int[] knopfstatus) {
        knopfGedoense(knopfstatus[0] == 0 ? false : true, knopfstatus[1] == 0 ? false : true,
                knopfstatus[2] == 0 ? false : true, knopfstatus[3] == 0 ? false : true,
                knopfstatus[4] == 0 ? false : true);
    }

    private void knopfGedoense(boolean neu, boolean loeschen, boolean aendern, boolean speichern, boolean abbrechen) {
        neuBt.setEnabled(neu);
        loeschenBt.setEnabled(loeschen);
        aendernBt.setEnabled(aendern);
        speichernBt.setEnabled(speichern);
        abbrechenBt.setEnabled(abbrechen);
    }

    private void eingabenDeactivate() {
        felderEinschalten(false);
    }

    private void buttonsEmptyMode() {
        knopfGedoense(true, false, false, false, false);
    }

    private void buttonsViewMode() {
        knopfGedoense(true, true, true, false, false);
    }

    private void buttonsEditMode() {
        knopfGedoense(false, false, false, true, true);
    }

    private void felderEinschalten(boolean einschalten) {
        anrede.setEnabled(einschalten);
        anrede.validate();
        vorname.setEnabled(einschalten);
        vorname.validate();
        nachname.setEnabled(einschalten);
        geboren.setEnabled(einschalten);
        strasse.setEnabled(einschalten);
        plz.setEnabled(einschalten);
        ort.setEnabled(einschalten);
        telefon1.setEnabled(einschalten);
        telefon2.setEnabled(einschalten);

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
        geboren.setText("");
        strasse.setText("");
        plz.setText("");
        ort.setText("");
        telefon1.setText("");
        telefon2.setText("");

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

            Mitarbeiter tempMitarbeiter = new Mitarbeiter();
            fuelleWerteAusGUi(tempMitarbeiter);

            int neueKalenderZeile = findAvailableKalZeile();

            tempMitarbeiter.setKalzeile(neueKalenderZeile);

            new MitarbeiterDto(ik).save(tempMitarbeiter);

            Swingma newItem = new Swingma(tempMitarbeiter);
            maComboBox.addItem(newItem);
            maComboBox.setSelectedItem(newItem);

        } else {
            Mitarbeiter tempMitarbeiter = maComboBox.getItemAt(maComboBox.getSelectedIndex())
                                                    .getMitarbeiter();
            fuelleWerteAusGUi(tempMitarbeiter);
            new MitarbeiterDto(ik).save(tempMitarbeiter);
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

    private int findAvailableKalZeile() {
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
        return neueKalenderZeile;
    }

    private void fuelleWerteAusGUi(Mitarbeiter mitarbeiter) {
        mitarbeiter.setAnrede(anrede.getText()
                                    .trim());
        mitarbeiter.setVorname(vorname.getText()
                                      .trim());
        mitarbeiter.setNachname(nachname.getText()
                                        .trim());
        mitarbeiter.setMatchcode(matchcode.getText()
                                          .trim());

        mitarbeiter.setGeboren(evaluateGeboren());

        mitarbeiter.setStrasse(strasse.getText());
        mitarbeiter.setPlz(plz.getText());
        mitarbeiter.setOrt(ort.getText());
        mitarbeiter.setTelefon1(telefon1.getText());
        mitarbeiter.setTelfon2(telefon2.getText());

        int selectedAbteil = abteilCombo.getSelectedIndex();
        if (selectedAbteil != -1) {
            mitarbeiter.setAbteilung(abteilCombo.getItemAt(selectedAbteil).bezeichnung);
        }

        mitarbeiter.setAstunden(evaluateArbeitstunden());

    }

    private LocalDate evaluateGeboren() {

        String text = geboren.getText()
                             .trim();
        if (".  .".equals(text)) {
            return null;
        }
        return LocalDate.parse(text, DateTimeFormatters.dMYYYYmitPunkt);
    }

    private double evaluateArbeitstunden() {
        double astunden;
        try {
            astunden = Double.parseDouble(arbstd.getText()
                                                .trim()
                                                .replace(",", "."));
        } catch (NumberFormatException e) {
            astunden = 0.0;
        }
        return astunden;
    }

    private int findKalzeilenGapFromDB() {

        return new MitarbeiterDto(ik).findgap();
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
                    logger.error("Could not create Mitarbeiterlistendokument", exception);
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

    private JFormattedTextField geboren;

    private JTextField strasse;

    private JTextField plz;

    private JTextField ort;

    private JTextField telefon1;

    private JTextField telefon2;

    public static class KalBenutzerTraversalPolicy extends FocusTraversalPolicy {
        List<Component> order = new LinkedList<>();

        public KalBenutzerTraversalPolicy(List<Component> components) {
            this.order.addAll(components);
        }

        @Override
        public Component getComponentAfter(Container focusCycleRoot, Component aComponent) {
            int idx = (order.indexOf(aComponent) + 1) % order.size();
            return order.get(idx);
        }

        @Override
        public Component getComponentBefore(Container focusCycleRoot, Component aComponent) {
            int idx = order.indexOf(aComponent) - 1;
            if (idx < 0) {
                idx = order.size() - 1;
            }
            return order.get(idx);
        }

        @Override
        public Component getDefaultComponent(Container focusCycleRoot) {
            return order.get(0);
        }

        @Override
        public Component getLastComponent(Container focusCycleRoot) {
            return order.get(order.size() - 1);
        }
        @Override
        public Component getFirstComponent(Container focusCycleRoot) {
            return order.get(0);
        }
    }
}
