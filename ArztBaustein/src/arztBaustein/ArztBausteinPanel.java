package arztBaustein;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.mysql.jdbc.PreparedStatement;
import com.sun.star.beans.PropertyVetoException;
import com.sun.star.beans.UnknownPropertyException;
import com.sun.star.beans.XPropertySet;
import com.sun.star.frame.XLayoutManager;
import com.sun.star.lang.IllegalArgumentException;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.ui.XUIElement;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.view.DocumentZoomType;

import CommonTools.JCompTools;
import CommonTools.JRtaTextField;
import CommonTools.Monitor;
import CommonTools.SqlInfo;
import CommonTools.TableTool;
import CommonTools.TopWindow;
import ag.ion.bion.officelayer.NativeView;
import ag.ion.bion.officelayer.application.IOfficeApplication;
import ag.ion.bion.officelayer.desktop.IFrame;
import ag.ion.bion.officelayer.document.DocumentDescriptor;
import ag.ion.bion.officelayer.document.DocumentException;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.text.ITextCursor;
import ag.ion.bion.officelayer.text.ITextDocument;
import ag.ion.bion.officelayer.text.ITextRange;
import ag.ion.bion.officelayer.text.IViewCursor;
import ag.ion.noa.NOAException;
import ag.ion.noa.filter.OpenDocumentFilter;
import ag.ion.noa.frame.ILayoutManager;
import ag.ion.noa.internal.frame.LayoutManager;
import office.OOTools;

class ArztBausteinPanel extends JXPanel {
    private static final long serialVersionUID = -3384203389588570947L;
    private JXTable bausteintbl;
    private MyBausteinTableModel bausteinmod;

    private IFrame officeFrame;
    private ITextDocument document;
    private JPanel noaPanel;
    private JXPanel noaDummy;
    private NativeView nativeView;

    private JRtaTextField[] jtfs = { null, null, null };
    private JRtaTextField suchenach;

    private String[] rubriken = { " ", "1. Anamnese-2015", "2. Sozialmedizinische Anamnese-2015",
            "3. Aufnahmebefund, Diagnostik während der Rehabilitation-2015", "4. Reha-Prozess und - Ergebnis-2015",
            "5. Empfehlungen für weiterführende Maßnahmen-2015", "Sozialmedizinische Epikrise-2015",
            "01.Allgem. u. klin. Anamnese", "02.Jetzige Beschwerden u...", "03.Gegenwärtige Therapie",
            "04.Allgem. Sozialanamnese", "05.Arbeits- u. Berufsanamnese", "06.Aufnahme, Vorbefunde...",
            "07.Rehabilitationsdiagnosen", "08.Rehabilitationsverlauf", "09.Rehabilitationsergebnis",
            "10.Sozialmed. Epikrise", "99.sonstige Textbausteine" };

    private String[] varnamen = { "heutiges Datum", "Anrede Pat.", "Nachname Pat.", "Vorname Pat.", "Geburtsdatum Pat.",
            "Strasse Pat.", "PLZ Pat.", "Ort Pat.", "Aufnahmedatum", "Entlassdatum", "arbeitsfähig/-unfähig",
            "Der/Die Patient/in", "der/die Patient/in", "Er/Sie", "er/sie", "Seines/Ihres", "seines/ihres",
            "Seine/Ihre", "seine/ihre", "Sein/Ihr", "sein/ihr", "seiner/ihrer", "zum/zur", "Dem/Der Patienten/in",
            "dem/der Patienten/in", "Des/Der Patienten/in", "des/der Patienten/in", "Der/Die Versicherte",
            "der/die Versicherte", "vom Versicherten/von der Versicherten", "Der/Die Rehabilitand/in",
            "der/die Rehabilitand/in", "des/der Rehabilitanden/in", "Der/Die 99-jährige Pat.",
            "der/die 99-jährige Pat.", "freie Variable setzen!!!!" };

    private String[] varinhalt = { "Heute", "Anrede", "PatName", "PatVorname", "Geburtsdatum", "Strasse", "PLZ", "Ort",
            "Aufnahme", "Etlassung", "arbeitsfähig?", "Der/Die Pat.", "der/die Pat.", "Er/Sie", "er/sie",
            "Seines/Ihres", "seines/ihres", "Seine/Ihre", "seine/ihre", "Sein/Ihr", "sein/ihr", "seiner/ihrer",
            "zum/zur", "Dem/Der Pat.", "dem/der Pat.", "Des/Der Pat.", "des/der Pat.", "Der/Die Vers.", "der/die Vers.",
            "vom Vers./von der Vers.", "Der/Die Rehab.", "der/die Rehab.", "des/der Rehab.", "Der/Die 99-jährige",
            "der/die 99-jährige", "frei" };

    private JComboBox<String> jcmbRubriken;
    private JList<String> list;

    private JButton suchen;

    private boolean neu;

    private boolean noapanelready;
    private boolean tablepanelready;
    private JButton jBNeu;
    private JButton jBEdit;
    private JButton jBSave;
    private JButton jBDelete;
    private JButton jBAbort;
    private Monitor monitor;
    private Connection conn;

    ArztBausteinPanel(Connection connection, IOfficeApplication office, Monitor monitor) {
        conn = connection;
        this.monitor = monitor;
        setSize(1024, 800);
        setPreferredSize(new Dimension(1024, 800));
        setLayout(new GridLayout());

        add(constructSplitPaneLR(), BorderLayout.CENTER);

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    noaDummy.add(getOOorgPanel());
                    fillNOAPanel(office);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                noapanelready = true;
                return null;
            }
        }.execute();

        validate();
        setVisible(true);

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                System.out.println(noapanelready + "  /  " + tablepanelready);
                for (int i = 0; i < 10000; i++) {
                    if (noapanelready && tablepanelready) {
                        bausteintbl.setRowSelectionInterval(0, 0);
                        // System.out.println(noapanelready+" / "+tablepanelready);
                        setzeFocus();
                        break;
                    }
                    try {
                        Thread.sleep(25);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }

        }.execute();
    }

    private void setzeFocus() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                suchenach.requestFocus();
            }
        });
    }

    private void controlsEin(boolean ein) {
        jcmbRubriken.setEnabled(ein);
        jtfs[0].setEnabled(ein);
        jtfs[1].setEnabled(ein);
        list.setEnabled(ein);
    }

    private void doDelete() {
        loeschen();
        controlsEin(false);
        neu = false;
        bausteintbl.setEnabled(true);
        setButtonToNeutralMode();
    }

    private void doNeu() {
        jcmbRubriken.setSelectedIndex(0);
        jtfs[0].setText("");
        jtfs[1].setText("");
        controlsEin(true);
        neu = true;
        bausteintbl.setEnabled(false);
        setButtonsToEditMode();
        document.getTextService()
                .getText()
                .setText("");
    }

    private void doSave() {
        speichern(neu);
        controlsEin(false);
        neu = false;
        bausteintbl.setEnabled(true);
        setButtonToNeutralMode();
    }

    private void doBreak() {
        controlsEin(false);
        neu = false;
        bausteintbl.setEnabled(true);
        setButtonToNeutralMode();
        holeIdUndText();
    }

    private void doEdit() {
        int row = bausteintbl.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(null, "Bitte wählen Sie zuerst den Baustein aus den Sie bearbeiten wollen");
            return;
        }
        controlsEin(true);
        neu = false;
        bausteintbl.setEnabled(false);
        setButtonsToEditMode();
    }

    private void setButtonToNeutralMode() {
        regleButton(true, true, false, true, false);
    }

    private void setButtonsToEditMode() {
        regleButton(false, false, true, false, true);
    }

    private void regleButton(boolean neu, boolean edit, boolean save, boolean delete, boolean abbruch) {
        jBNeu.setEnabled(neu);
        jBEdit.setEnabled(edit);
        jBSave.setEnabled(save);
        jBDelete.setEnabled(delete);
        jBAbort.setEnabled(abbruch);
    }

    private void doSearch() {
        String suche = suchenach.getText()
                                .trim();
        if ("".equals(suche)) {
            fuelleTabelle("");
        } else {
            String[] spalten = { "tbtitel", "tbuntert" };
            String where = SqlInfo.macheWhereKlausel("select tbthema,tbuntert,tbtitel,id from tbar where ",
                    suchenach.getText()
                             .trim(),
                    spalten);
            // System.out.println("where = "+where);
            fuelleTabelle(where + " Order BY tbthema");
        }
    }

    private JPanel getOOorgPanel() {
        noaPanel = new JPanel(new GridLayout());
        noaPanel.setPreferredSize(new Dimension(1024, 800));
        noaPanel.validate();
        return noaPanel;
    }

    private JXPanel getnoaDummy() {
        noaDummy = new JXPanel(new GridLayout(0, 1));
        return noaDummy;
    }

    private JXPanel getToolsPanel() {
        JXPanel pan = new JXPanel(); // 1 2 3 4 5 6
        FormLayout lay = new FormLayout("0dlu,fill:0:grow(0.75),5dlu,fill:0:grow(0.25),5dlu,80dlu,0dlu",
                "0dlu,fill:0:grow(1.0),0dlu");
        CellConstraints cc = new CellConstraints();
        pan.setLayout(lay);
        pan.add(getTablePanel(), cc.xy(2, 2));
        pan.add(getControlPanel(), cc.xy(4, 2));
        pan.add(getButtonPanel(), cc.xy(6, 2));
        return pan;
    }

    private JXPanel getPlatzhalterPanel() {
        JXPanel pan = new JXPanel();
        final JXPanel xpan = pan;
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    FormLayout lay = new FormLayout("5dlu,fill:0:grow(1.0),5dlu",
                            "15dlu,p,20dlu,fill:0:grow(1.0),5dlu");
                    CellConstraints cc = new CellConstraints();
                    xpan.setBackground(Color.WHITE);
                    xpan.setLayout(lay);
                    JLabel lab = new JLabel("System-Variable");
                    lab.setForeground(Color.BLUE);
                    xpan.add(lab, cc.xy(2, 2));
                    list = new JList<>();
                    list.setListData(varnamen);
                    list.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent arg0) {
                            if (arg0.getClickCount() == 2 && ((JComponent) arg0.getSource()).isEnabled()) {
                                regleListe();
                            }
                        }

                    });
                    JScrollPane jscr = JCompTools.getTransparentScrollPane(list);
                    jscr.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                    jscr.validate();
                    xpan.add(jscr, cc.xy(2, 4));
                    xpan.validate();
                    list.setEnabled(false);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return null;
            }

        }.execute();
        return pan;
    }

    private JXPanel getButtonPanel() {
        JXPanel pan = new JXPanel();
        try {
            FormLayout lay = new FormLayout("5dlu,fill:0:grow(1.0),5dlu",
                    // 1 2 3 4 5 6 7 8 9 10 11
                    "3dlu,p,5dlu,p,5dlu,p,5dlu,p,5dlu,p,3dlu");
            CellConstraints cc = new CellConstraints();
            pan.setLayout(lay);
            pan.setBackground(Color.GRAY);
            jBNeu = new JButton("neuer Baustein");
            jBNeu.setActionCommand("neu");
            jBNeu.addActionListener(e -> doNeu());
            pan.add(jBNeu, cc.xy(2, 2));

            jBEdit = new JButton("Baustein ändern");
            jBEdit.setActionCommand("edit");
            jBEdit.addActionListener(e -> doEdit());
            pan.add(jBEdit, cc.xy(2, 4));

            jBSave = new JButton("speichern");
            jBSave.setActionCommand("save");
            jBSave.addActionListener(e -> doSave());
            pan.add(jBSave, cc.xy(2, 6));

            jBDelete = new JButton("löschen");
            jBDelete.setActionCommand("delete");
            jBDelete.addActionListener(e -> doDelete());
            pan.add(jBDelete, cc.xy(2, 8));
            jBAbort = new JButton("abbrechen");

            jBAbort.setActionCommand("break");
            jBAbort.addActionListener(e -> doBreak());
            pan.add(jBAbort, cc.xy(2, 10));

            setButtonToNeutralMode();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        pan.validate();
        return pan;
    }

    private JXPanel getControlPanel() {
        JXPanel pan = new JXPanel();
        try {
            FormLayout lay = new FormLayout("0dlu,fill:0:grow(0.5),5dlu,fill:0:grow(0.5),0dlu",
                    // 1 2 3 4 5 6 7 8 9 10 11 12 13 14
                    "3dlu,p,15dlu,p,2dlu,p,2dlu,p,2dlu,p,2dlu,p,2dlu,p,3dlu");
            CellConstraints cc = new CellConstraints();
            KeyListener kl = new KeyAdapter() {

                @Override
                public void keyPressed(KeyEvent arg0) {
                    if (arg0.getSource() instanceof JRtaTextField) {
                        if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
                            ((JComponent) arg0.getSource()).requestFocus();
                            doSearch();
                        }
                    } else if (arg0.getSource() instanceof JButton && arg0.getKeyCode() == KeyEvent.VK_ENTER) {
                        // suchenach.requestFocus();
                        doSearch();
                    }
                }

            };
            pan.setLayout(lay);
            suchenach = new JRtaTextField("nix", true);
            suchenach.addKeyListener(kl);
            suchenach.setToolTipText("Geben Sie hier Ihr Suchkriterium ein");
            pan.add(suchenach, cc.xy(2, 2, CellConstraints.FILL, CellConstraints.DEFAULT));
            suchen = new JButton("suchen");
            suchen.addKeyListener(kl);
            suchen.setActionCommand("suchen");
            suchen.addActionListener(e -> doSearch());
            pan.add(suchen, cc.xy(4, 2));

            pan.add(new JLabel("Haupt-Rubrik"), cc.xyw(2, 4, 3, CellConstraints.DEFAULT, CellConstraints.BOTTOM));
            jcmbRubriken = new JComboBox<String>(rubriken);
            jcmbRubriken.setEnabled(false);
            jcmbRubriken.setMaximumRowCount(5);
            pan.add(jcmbRubriken, cc.xyw(2, 6, 3));

            pan.add(new JLabel("Untergliederung"), cc.xyw(2, 8, 3, CellConstraints.DEFAULT, CellConstraints.BOTTOM));
            JRtaTextField jTfUntergliederung = new JRtaTextField("nix", true);
            jtfs[0] = jTfUntergliederung;
            jtfs[0].setEnabled(false);
            pan.add(jtfs[0], cc.xyw(2, 10, 3));

            pan.add(new JLabel("Titel des Bausteins"),
                    cc.xyw(2, 12, 3, CellConstraints.DEFAULT, CellConstraints.BOTTOM));
            jtfs[1] = new JRtaTextField("nix", true);
            jtfs[1].setEnabled(false);
            pan.add(jtfs[1], cc.xyw(2, 14, 3));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return pan;
    }

    private JScrollPane getTablePanel() {
        bausteinmod = new MyBausteinTableModel();
        String[] columns = { "Haupt-Rubrik", "Untergliederung", "Titel des Bausteins", "id" };
        bausteinmod.setColumnIdentifiers(columns);
        bausteintbl = new JXTable(bausteinmod);
        bausteintbl.getColumn(3)
                   .setMinWidth(0);
        bausteintbl.getColumn(3)
                   .setMaxWidth(0);
        bausteintbl.validate();
        bausteintbl.setHighlighters(HighlighterFactory.createSimpleStriping(HighlighterFactory.CLASSIC_LINE_PRINTER));
        bausteintbl.setName("tabelle");
        bausteintbl.setSelectionMode(0);
        bausteintbl.getSelectionModel()
                   .addListSelectionListener(new BausteinSelectionHandler());
        JScrollPane jscr = JCompTools.getTransparentScrollPane(bausteintbl);
        jscr.validate();
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    fuelleTabelle("");
                    tablepanelready = true;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                return null;
            }

        }.execute();
        return jscr;
    }

    private UIFSplitPane constructSplitPaneLR() {
        UIFSplitPane jSplitLR = UIFSplitPane.createStrippedSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                constructSplitPaneOU(), getPlatzhalterPanel());
        jSplitLR.setOpaque(false);
        jSplitLR.setDividerSize(7);
        jSplitLR.setDividerBorderVisible(true);
        jSplitLR.setName("PatGrundSplitLinksRechts");
        jSplitLR.setOneTouchExpandable(true);
        jSplitLR.setDividerColor(Color.LIGHT_GRAY);
        jSplitLR.setDividerLocation(850);
        jSplitLR.validate();
        jSplitLR.setVisible(true);
        jSplitLR.validate();
        return jSplitLR;
    }

    private UIFSplitPane constructSplitPaneOU() {
        UIFSplitPane jSplitRechtsOU = UIFSplitPane.createStrippedSplitPane(JSplitPane.VERTICAL_SPLIT, getToolsPanel(),
                getnoaDummy());
        jSplitRechtsOU.setOpaque(false);
        jSplitRechtsOU.setDividerSize(7);
        jSplitRechtsOU.setDividerBorderVisible(true);
        jSplitRechtsOU.setName("PatGrundSplitRechteSeiteObenUnten");
        jSplitRechtsOU.setOneTouchExpandable(true);
        jSplitRechtsOU.setDividerColor(Color.LIGHT_GRAY);
        jSplitRechtsOU.setDividerLocation(175);
        jSplitRechtsOU.validate();
        return jSplitRechtsOU;
    }

    private void holeIdUndText() {
        int row = bausteintbl.getSelectedRow();
        if (row >= 0) {
            row = bausteintbl.convertRowIndexToModel(row);
            String id = bausteinmod.getValueAt(row, 3)
                                   .toString();
            holeTextBaustein(id);
            jcmbRubriken.setSelectedItem(bausteinmod.getValueAt(row, 0)
                                                    .toString());
            jtfs[0].setText(bausteinmod.getValueAt(row, 1)
                                       .toString());
            jtfs[1].setText(bausteinmod.getValueAt(row, 2)
                                       .toString());
        }
    }

    private void holeTextBaustein(String id) {
        InputStream ins = SqlInfo.holeStream("tbar", "tbtext", "id='" + id + "'");

        document.getTextService()
                .getText()
                .setText("");

        ITextCursor textCursor = null;
        IViewCursor viewCursor = null;
        try {
            textCursor = document.getTextService()
                                 .getCursorService()
                                 .getTextCursor();
            textCursor.insertDocument(ins, OpenDocumentFilter.FILTER);

            textCursor.gotoStart(false);
            viewCursor = document.getViewCursorService()
                                 .getViewCursor();
            viewCursor.getPageCursor()
                      .jumpToFirstPage();
            viewCursor.getPageCursor()
                      .jumpToStartOfPage();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void fillNOAPanel(IOfficeApplication office) {
        if (noaPanel != null) {
            try {
                System.out.println("Konstruiere das NOA-Panel");
                officeFrame = constructOOOFrame(office, noaPanel);
                document = (ITextDocument) office.getDocumentService()
                                                 .constructNewDocument(officeFrame, IDocument.WRITER,
                                                         DocumentDescriptor.DEFAULT);
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // XXX: Workaround für Java 7
                            new TopWindow(document);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(null,
                                    "Achtung!, der TopWindow-Listener (wichtig für Java 7) konnte nicht korrekt gestartet werden");
                        }
                    }
                });

                OOTools.setzePapierFormat(document, Integer.valueOf(25199), Integer.valueOf(19299));
                OOTools.setzeRaender(document, Integer.valueOf(1000), Integer.valueOf(1000), Integer.valueOf(1000),
                        Integer.valueOf(1000));
                hideElements(LayoutManager.URL_MENUBAR);
                // nativeView.validate();
                try {
                    document.zoom(DocumentZoomType.BY_VALUE, (short) 100);
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
                noaPanel.setVisible(true);
            } catch (Throwable throwable) {
                noaPanel.add(new JLabel("Ein Fehler ist aufgetreten: " + throwable.getMessage()));
            }
            noapanelready = true;
        }
    }

    private IFrame constructOOOFrame(IOfficeApplication officeApplication, final Container parent) throws Throwable {
        System.out.println("Konstruiere die Native-View");
        nativeView = new NativeView();
        parent.add(nativeView);
        parent.validate();
        parent.setVisible(true);
        parent.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                nativeView.setPreferredSize(new Dimension(parent.getWidth() - 5, parent.getHeight() - 5));
                parent.getLayout()
                      .layoutContainer(parent);
                refreshSize();
            }
        });

        nativeView.setPreferredSize(new Dimension(parent.getWidth() - 5, parent.getHeight() - 5));
        parent.getLayout()
              .layoutContainer(parent);
        officeFrame = officeApplication.getDesktopService()
                                       .constructNewOfficeFrame(nativeView);
        return officeFrame;
    }

    private void hideElements(String url) throws UnknownPropertyException, PropertyVetoException,
            IllegalArgumentException, WrappedTargetException, NOAException {
        ILayoutManager layoutManager = officeFrame.getLayoutManager();
        XLayoutManager xLayoutManager = layoutManager.getXLayoutManager();
        XUIElement element = xLayoutManager.getElement(url);
        if (element != null) {
            XPropertySet xps = UnoRuntime.queryInterface(XPropertySet.class, element);
            xps.setPropertyValue("Persistent", Boolean.FALSE);
            xLayoutManager.hideElement(url);
        }
    }

    private void refreshSize() {
        noaPanel.setPreferredSize(new Dimension(noaPanel.getWidth(), noaPanel.getHeight() - 5));
        final Container parent = noaPanel.getParent();
        if (parent instanceof JComponent) {
            ((JComponent) parent).revalidate();
        }
        final Window window1 = SwingUtilities.getWindowAncestor(nativeView.getParent()
                                                                          .getParent());
        if (window1 != null) {
            window1.validate();
        }
        noaPanel.getLayout()
                .layoutContainer(noaPanel);
    }

    private void loeschen() {
        int row;
        String id = "-1";
        row = bausteintbl.getSelectedRow();
        if (row >= 0) {
            row = bausteintbl.convertRowIndexToModel(row);
            id = bausteinmod.getValueAt(row, 3)
                            .toString();
        } else {
            return;
        }
        int anfrage = JOptionPane.showConfirmDialog(null,
                "Wollen Sie diesen Baustein wirklich löschen?\n\n" + "Baustein= " + bausteinmod.getValueAt(row, 2),
                "Wichtige Benutzeranfrage", JOptionPane.YES_NO_OPTION);
        if (anfrage == JOptionPane.YES_OPTION) {
            TableTool.loescheRowAusModel(bausteintbl, -1);
            SqlInfo.sqlAusfuehren("delete from tbar where id='" + id + "' LIMIT 1");
        }
    }

    private void speichern(boolean neu) {
        String id = "";
        int row = 0;
        if (neu) {
            id = Integer.toString(SqlInfo.holeId("tbar", "tbtext"));
            if ("-1".equals(id)) {
                JOptionPane.showMessageDialog(null,
                        "Fehler beim Speichern des neuen Bausteins\nBitte verständigen Sie den Administrator!");
                return;
            }
        } else {
            row = bausteintbl.getSelectedRow();
            if (row >= 0) {
                row = bausteintbl.convertRowIndexToModel(row);
                id = bausteinmod.getValueAt(row, 3)
                                .toString();
            } else {
                JOptionPane.showMessageDialog(null,
                        "Fehler beim Speichern des Bausteins\nBitte verständigen Sie den Administrator!");
                return;
            }
        }
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        try {
            document.getPersistenceService()
                    .store(bout);
            bout.flush();
            bout.close();
        } catch (IOException | DocumentException e) {
            e.printStackTrace();
        }
        String updateQuery = "update tbar set tbthema = ? , tbuntert = ?, tbtitel = ?,"
                + "tbtext = ? where id = ? LIMIT 1";

        try (Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                PreparedStatement ps = (PreparedStatement) conn.prepareStatement(updateQuery)) {
            ps.setString(1, jcmbRubriken.getSelectedItem()
                                        .toString());
            ps.setString(2, jtfs[0].getText());
            ps.setString(3, jtfs[1].getText());
            ps.setBytes(4, bout.toByteArray());
            ps.setInt(5, Integer.parseInt(id));
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (!neu) {
            bausteinmod.setValueAt(jcmbRubriken.getSelectedItem()
                                               .toString(),
                    row, 0);
            bausteinmod.setValueAt(jtfs[0].getText(), row, 1);
            bausteinmod.setValueAt(jtfs[1].getText(), row, 2);
        }
    }

    private void fuelleTabelle(String cmd) {
        monitor.statusChange(Monitor.START);
        String selectQuery = null;
        if ("".equals(cmd)) {
            selectQuery = "select tbthema,tbuntert,tbtitel,id from tbar Order BY tbthema";
        } else {
            selectQuery = cmd;
        }

        bausteinmod.setRowCount(0);

        try (Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                ResultSet rs = stmt.executeQuery(selectQuery))

        {
            while (rs.next()) {
                Vector<String> retvec = new Vector<>();
                retvec.add(rs.getString(1) == null ? "" : rs.getString(1));
                retvec.add(rs.getString(2) == null ? "" : rs.getString(2));
                retvec.add(rs.getString(3) == null ? "" : rs.getString(3));
                retvec.add(rs.getString(4) == null ? "" : rs.getString(4));
                bausteinmod.addRow(retvec);
            }
        } catch (SQLException ev) {
            System.out.println("SQLException: " + ev.getMessage());
            System.out.println("SQLState: " + ev.getSQLState());
            System.out.println("VendorError: " + ev.getErrorCode());
        }

        monitor.statusChange(Monitor.STOP);
    }

    private void regleListe() {
        int pos = list.getSelectedIndex();
        String toSet = "";
        if (pos == (varinhalt.length - 1)) {
            // freie Variable
            Object ret = JOptionPane.showInputDialog(this, "Bitte einen Namen für die Variable eingeben",
                    "Baustein-Variable setzen", 1);
            if (ret != null) {
                toSet = "^" + ret.toString()
                                 .trim()
                        + "^";
            } else {
                toSet = "^NoName set^";
            }
        } else {
            // System-Variable
            toSet = "^" + varinhalt[pos] + "^";
        }
        insertTextAtCurrentPosition(toSet);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                nativeView.requestFocus();
            }
        });
    }

    private void insertTextAtCurrentPosition(String xtext) {
        IViewCursor viewCursor = document.getViewCursorService()
                                         .getViewCursor();
        ITextRange textRange = viewCursor.getStartTextRange();
        textRange.setText(xtext);
        try {
            document.setModified(false);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    void closeDocument() {
        if (document != null) {
            document.close();
            document = null;
            System.out.println("Dokument wurde geschlossen");
        }
    }

    private class MyBausteinTableModel extends DefaultTableModel {
        private static final long serialVersionUID = 1L;

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return String.class;
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            return false;
        }
    }

    private class BausteinSelectionHandler implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                ListSelectionModel lsm = (ListSelectionModel) e.getSource();
                if (!lsm.isSelectionEmpty()) {
                    holeIdUndText();
                }
            }
        }
    }
}
