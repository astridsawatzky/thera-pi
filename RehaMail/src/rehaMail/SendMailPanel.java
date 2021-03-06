package rehaMail;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import CommonTools.ButtonTools;
import CommonTools.DatFunk;
import CommonTools.DateTableCellRenderer;
import CommonTools.DblCellEditor;
import CommonTools.DoubleTableCellRenderer;
import CommonTools.IconListRenderer;
import CommonTools.JCompTools;
import CommonTools.JRtaTextField;
import CommonTools.MitteRenderer;
import CommonTools.ReaderStart;
import CommonTools.SqlInfo;
import rehaMail.Tools.Rechte;
import rehaMail.Tools.ToolsDialog;
import rehaMail.Tools.UIFSplitPane;
import ag.ion.bion.officelayer.NativeView;
import ag.ion.bion.officelayer.desktop.IFrame;
import ag.ion.bion.officelayer.document.DocumentDescriptor;
import ag.ion.bion.officelayer.text.ITextDocument;

public class SendMailPanel extends JXPanel implements KeyListener {
    /**
     *
     */
    private static final long serialVersionUID = 5871553539357474995L;

    CommonTools.DateTableCellEditor tabDateEditor = new CommonTools.DateTableCellEditor();
    DateTableCellRenderer tabDateRenderer = new DateTableCellRenderer(true);

    DblCellEditor tabDoubleEditor = new DblCellEditor();
    DoubleTableCellRenderer tabDoubleRenderer = new DoubleTableCellRenderer();

    CommonTools.IntTableCellEditor tabIntegerEditor = new CommonTools.IntTableCellEditor();
    CommonTools.IntTableCellRenderer tabIntegerRenderer = new CommonTools.IntTableCellRenderer();

    JRtaTextField sqlstatement = null;

    DecimalFormat dcf = new DecimalFormat("##########0.00");
    SimpleDateFormat datumsFormat = new SimpleDateFormat("dd.MM.yyyy"); // Konv.
    ActionListener al = null;
    JScrollPane jscr = null;
    JXPanel grundpanel;

    /***********/
    JXTable eintab = null;
    EinTableModel einmod = null;
    /***********/

    private IFrame officeFrame = null;
    public ITextDocument document = null;
    private JXPanel noaPanel = null;

    NativeView nativeView = null;
    DocumentDescriptor xdescript = null;

    boolean gelesen = false;
    String aktId = "";
    String aktAbsender = "";
    String aktBetreff = "";
    JButton[] buts = { null, null, null, null, null };

    EinListSelectionHandler listhandler = null;

    Vector<String> attachmentFileName = new Vector<String>();

    RTFEditorPanel rtfEditor = null;
    ObjectInputStream ois = null;
    InputStream ins = null;

    ByteArrayInputStream bins;

    JRtaTextField suchen = null;

    public SendMailPanel() {
        super(new BorderLayout());
        /**************/
        try {
            activateListener();
            /******************/
            JXPanel pan = new JXPanel();
            pan.setOpaque(false);
            String xwert = "fill:0:grow(1.0),p";
            String ywert = "5px,p,5px";
            FormLayout lay = new FormLayout(xwert, ywert);
            CellConstraints cc = new CellConstraints();
            pan.setLayout(lay);

            pan.add(getToolbar(), cc.xy(1, 2, CellConstraints.FILL, CellConstraints.FILL));
            pan.add(getAttachmentButton(), cc.xy(2, 2, CellConstraints.RIGHT, CellConstraints.DEFAULT));
            pan.validate();
            add(pan, BorderLayout.NORTH);
            /*******************/

            add(constructSplitPaneOU(), BorderLayout.CENTER);
            // noaDummy.add(getOOorgPanel());
            // noaPanel.setVisible(true);
            // setVisible(true);
            /*
             * new SwingWorker<Void,Void>(){
             * 
             * @Override protected Void doInBackground() throws Exception {
             * if(!RehaMail.officeapplication.isActive()){
             * //System.out.println("Aktiviere Office...");
             * RehaMail.starteOfficeApplication(); } fillNOAPanel(); checkForNewMail();
             * return null; } }.execute();
             */
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    try {
                        while (!RehaMail.DbOk) {
                            Thread.sleep(20);
                        }
                        checkForNewMail();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    return null;
                }

            }.execute();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        validate();
        /**************/
        /*
         * setOpaque(false); String xwert = "fill:0:grow(1.0),p"; String ywert =
         * "fill:0:grow(0.5),2px,p,2px,fill:0:grow(0.5)"; FormLayout lay = new
         * FormLayout(xwert,ywert); CellConstraints cc = new CellConstraints();
         * setLayout(lay);
         * 
         * add(getTabelle(),cc.xyw(1,1,2));
         */
    }

    /******************************************/
    public void updateMails() {
        /********
         *
         */

    }

    public void checkForNewMail() {
        suchen.setText("");
        listenerAusschalten();
        doStatementAuswerten("select empfaenger_person,"
                + "gelesen,versanddatum,gelesendatum,betreff,id from pimail where absender='" + RehaMail.mailUser
                + "' order by gelesen DESC,versanddatum DESC,gelesendatum DESC");
        for (int i = 0; i < 4; i++) {
            if (buts[i] != null) {
                buts[i].setEnabled(true);
            }
        }
    }

    /******************************************/
    private rehaMail.Tools.UIFSplitPane constructSplitPaneOU() {
        try {
            UIFSplitPane jSplitRechtsOU = UIFSplitPane.createStrippedSplitPane(JSplitPane.VERTICAL_SPLIT, getTabelle(),
                    rtfEditor = new RTFEditorPanel(false, false, false)/* getOOorgPanel() */);
            jSplitRechtsOU.setOpaque(false);
            jSplitRechtsOU.setDividerSize(7);
            jSplitRechtsOU.setDividerBorderVisible(true);
            jSplitRechtsOU.setName("PatGrundSplitRechteSeiteObenUnten");
            jSplitRechtsOU.setOneTouchExpandable(true);
            jSplitRechtsOU.setDividerColor(Color.LIGHT_GRAY);
            jSplitRechtsOU.setDividerLocation(175);
            jSplitRechtsOU.validate();
            return jSplitRechtsOU;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public JXPanel getTabelle() {
        JXPanel pan = new JXPanel();
        pan.setOpaque(false);
        String xwert = "fill:0:grow(1.0)";
        String ywert = "fill:0:grow(1.0)";
        FormLayout lay = new FormLayout(xwert, ywert);
        CellConstraints cc = new CellConstraints();
        pan.setLayout(lay);

        einmod = new EinTableModel();
        einmod.setColumnIdentifiers(
                new String[] { "Empfänger", "gelesen", "Abs.Datum", "Gelesen-Datum", "Betreff", "id" });
        eintab = new JXTable(einmod);

        eintab.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                if (arg0.getClickCount() == 2 && arg0.getButton() == 1) {
                    if (!gelesen) {
                        // holeNeueMail();
                        // setzeGelesen();
                    }
                }
            }
        });
        eintab.getColumn(0)
              .setMinWidth(120);
        eintab.getColumn(0)
              .setMaxWidth(120);
        eintab.getColumn(1)
              .setMaxWidth(50);
        eintab.getColumn(2)
              .setMaxWidth(100);
        eintab.getColumn(2)
              .setMinWidth(100);
        eintab.getColumn(2)
              .setCellEditor(tabDateEditor);
        eintab.getColumn(2)
              .setCellRenderer(tabDateRenderer);
        eintab.getColumn(3)
              .setCellEditor(tabDateEditor);
        eintab.getColumn(3)
              .setCellRenderer(new MitteRenderer());
        eintab.getColumn(3)
              .setMinWidth(155);
        eintab.getColumn(3)
              .setMaxWidth(155);
        eintab.getColumn(5)
              .setMinWidth(0);
        eintab.getColumn(5)
              .setMaxWidth(0);

        eintab.setFont(new Font("Courier New", 12, 12));
        eintab.getSelectionModel()
              .addListSelectionListener((listhandler = new EinListSelectionHandler()));
        eintab.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        eintab.setDragEnabled(true);
        jscr = JCompTools.getTransparentScrollPane(eintab);
        jscr.validate();
        pan.add(jscr, cc.xy(1, 1));
        pan.validate();
        return pan;
    }

    private void activateListener() {

        al = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                String cmd = arg0.getActionCommand();
                if (cmd.equals("attachments")) {
                    new ToolsDlgAktuelleRezepte(null, buts[4].getLocationOnScreen());
                    return;
                }
                if (cmd.equals("newMail")) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            Point pt = RehaMail.thisFrame.getLocationOnScreen();
                            new NewMail("neue Nachricht erstellen", true, new Point(pt.x + 50, pt.y + 50), null, "", "",
                                    false);
                        }
                    });
                    return;
                }
                if (cmd.equals("replyMail")) {
                    if (RehaMail.nachrichtenInBearbeitung) {
                        return;
                    }
                    checkForNewMail();
                    // JOptionPane.showMessageDialog(null, "Funktion noch nicht implementiert");
                    return;
                }
                if (cmd.equals("loeschen")) {
                    if (!Rechte.hatRecht(RehaMail.Sonstiges_NachrichtenLoeschen, false)) {
                        JOptionPane.showMessageDialog(null, "Funktion noch nicht implementiert");
                        return;
                    }
                    doLoeschen();
                    return;
                }

                if (cmd.equals("print")) {

                    new SwingWorker<Void, Void>() {
                        @Override
                        protected Void doInBackground() throws Exception {
                            ByteArrayOutputStream out = new ByteArrayOutputStream();
                            try {
                                rtfEditor.editorArea.getEditorKit()
                                                    .write(out, rtfEditor.editorArea.getDocument(), 0,
                                                            rtfEditor.editorArea.getDocument()
                                                                                .getLength());
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            } catch (BadLocationException e1) {
                                e1.printStackTrace();
                            }
                            out.flush();
                            out.close();
                            ByteArrayInputStream ins = new ByteArrayInputStream(out.toByteArray());
                            office.OOTools.starteWriterMitStream(ins, "mailprint");
                            ins.close();
                            return null;
                        }

                    }.execute();
                    return;
                }

            }
        };
    }

    private JXPanel getAttachmentButton() {
        JXPanel pan = new JXPanel();
        pan.setOpaque(false);

        buts[4] = ButtonTools.macheButton("", "attachments", al);
        buts[4].setIcon(RehaMail.attachmentIco[3]);
        buts[4].setToolTipText("Dateianhänge holen/ansehen");
        // buts[4].setEnabled(false);
        pan.add(buts[4]);
        pan.validate();
        return pan;

    }

    private JToolBar getToolbar() {
        JToolBar jtb = new JToolBar();
        jtb.setOpaque(false);
        jtb.setRollover(true);
        jtb.setBorder(null);
        jtb.addSeparator(new Dimension(0, 30));
        jtb.add((buts[0] = ButtonTools.macheButton("", "newMail", al)));
        buts[0].setIcon(RehaMail.symbole.get("plus"));
        buts[0].setToolTipText("eine neue Nachricht erstellen");
        jtb.addSeparator(new Dimension(15, 30));
        jtb.add((buts[1] = ButtonTools.macheButton("", "replyMail", al)));
        buts[1].setIcon(RehaMail.symbole.get("refresh"));
        buts[1].setToolTipText("auf die gewählte Nachricht antworten");
        jtb.addSeparator(new Dimension(15, 30));
        jtb.add((buts[3] = ButtonTools.macheButton("", "loeschen", al)));
        buts[3].setIcon(RehaMail.symbole.get("minus"));
        buts[3].setToolTipText("die gewählte Nachricht loeschen");
        jtb.addSeparator(new Dimension(75, 30));
        jtb.add((buts[2] = ButtonTools.macheButton("", "print", al)));
        buts[2].setIcon(RehaMail.symbole.get("drucken"));
        buts[2].setToolTipText("die gewählte Nachricht drucken");
        jtb.addSeparator(new Dimension(50, 30));
        jtb.add(new JLabel("Betreff oder Nachricht enthält: "));
        suchen = new JRtaTextField("nix", false);
        // suchen.setSize(new Dimension(60,15));
        suchen.setMaximumSize(new Dimension(150, 25));
        suchen.setFont(new Font("Courier New", 12, 12));
        suchen.setToolTipText("Ein oder mehrere Suchbegriffe eingeben und Enter drücken");
        suchen.setName("suchen");
        suchen.addKeyListener(this);

        jtb.add(suchen);
        jtb.addSeparator(new Dimension(50, 30));

        return jtb;
    }

    /********
     * OO.org-Ged�nse*******
     *
     *
     *
     ************/
    /*
     * private void fillNOAPanel() { if (noaPanel != null) { try { officeFrame =
     * constructOOOFrame(RehaMail.officeapplication, noaPanel); DocumentDescriptor
     * desc = DocumentDescriptor.DEFAULT; desc.setReadOnly(true); document =
     * (ITextDocument)
     * RehaMail.officeapplication.getDocumentService().constructNewDocument(
     * officeFrame, IDocument.WRITER, desc);
     * hideElements(LayoutManager.URL_MENUBAR);
     * hideElements(LayoutManager.URL_STATUSBAR);
     * hideElements(LayoutManager.URL_TOOLBAR_STANDARDBAR);
     * hideElements(LayoutManager.URL_TOOLBAR);
     * 
     * //CommonTools.OOCommonTools.setzePapierFormat(document, new Integer(25199),
     * new Integer(19299)); CommonTools.OOCommonTools.setzeRaender(document, new
     * Integer(1000), new Integer(1000),new Integer(1000),new Integer(1000));
     * 
     * //nativeView.validate(); try { document.zoom(DocumentZoomType.BY_VALUE,
     * (short)80); } catch (DocumentException e) { e.printStackTrace(); }
     * 
     * catch (Throwable throwable) { noaPanel.add(new
     * JLabel("<html>Ein Fehler ist aufgetreten:<br>" +
     * throwable.getMessage()+"</html>")); } } }
     * 
     * private IFrame constructOOOFrame(IOfficeApplication officeApplication, final
     * Container parent) throws Throwable { nativeView = new
     * NativeView(RehaMail.officeNativePfad); parent.add(nativeView);
     * parent.addComponentListener(new ComponentAdapter() { public void
     * componentResized(ComponentEvent e) { refreshSize();
     * nativeView.setPreferredSize(new Dimension(parent.getWidth() - 5,
     * parent.getHeight() - 5)); parent.getLayout().layoutContainer(parent); } });
     * 
     * nativeView.setPreferredSize(new Dimension(parent.getWidth() - 5,
     * parent.getHeight() - 5)); parent.getLayout().layoutContainer(parent);
     * officeFrame =
     * officeApplication.getDesktopService().constructNewOfficeFrame(nativeView);
     * return officeFrame; } private void hideElements(String url ) throws
     * PropertyVetoException, IllegalArgumentException, WrappedTargetException,
     * NOAException, UnknownPropertyException{ ILayoutManager layoutManager =
     * officeFrame.getLayoutManager(); XLayoutManager xLayoutManager =
     * layoutManager.getXLayoutManager(); XUIElement element =
     * xLayoutManager.getElement(url); if (element != null) { XPropertySet xps =
     * (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, element);
     * xps.setPropertyValue("Persistent", new Boolean(false));
     * xLayoutManager.hideElement(url); } }
     * 
     * public final void refreshSize() { noaPanel.setPreferredSize(new
     * Dimension(noaPanel.getWidth() , noaPanel.getHeight()- 5)); final Container
     * parent = noaPanel.getParent(); if (parent instanceof JComponent) {
     * ((JComponent) parent).revalidate(); } final Window window1 =
     * SwingUtilities.getWindowAncestor(nativeView.getParent().getParent()); if
     * (window1 != null) { window1.validate(); }
     * noaPanel.getLayout().layoutContainer(noaPanel);
     * 
     * }
     */
    public void holeAttachments() {
        this.attachmentFileName.clear();
        this.attachmentFileName.trimToSize();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Vector<Vector<String>> vec = SqlInfo.holeFelder(
                        "select file1,file2,file3 from pimail where id ='" + aktId + "' Limit 1");
                for (int i = 0; i < vec.get(0)
                                       .size(); i++) {
                    if (!vec.get(0)
                            .get(i)
                            .trim()
                            .equals("")) {
                        attachmentFileName.add(String.valueOf(vec.get(0)
                                                                 .get(i)
                                                                 .trim()));
                    }
                }
                if (attachmentFileName.size() > 0) {
                    buts[4].setEnabled(true);
                } else {
                    buts[4].setEnabled(false);
                }
            }
        });
    }

    /*********************
     *
     *
     *
     *
     *
     * ******************/

    private void doStatementAuswerten(final String stat) {

        Statement stmt = null;
        ResultSet rs = null;
        // ResultSet md = null;
        eintab.getRowSorter()
              .setSortKeys(null);
        einmod.setRowCount(0);
        Vector<Object> vec = new Vector<Object>();
        int durchlauf = 0;

        // Saudummerweise entspricht der R�ckgabewert von getColumnTypeName() oder
        // getColumnType() nicht der Abfrag von describe tabelle
        // so werden alle Integer-Typen unter INT zusammengefa�t
        // Longtext, Mediumtext, Varchar = alles VARCHAR
        // CHAR kann sowohl ein einzelnes Zeichen als auch enum('T','F') also boolean
        // sein...
        // eigentlich ein Riesenmist!

        eintab.getSelectionModel()
              .removeListSelectionListener(listhandler);
        try {

            stmt = RehaMail.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            rs = stmt.executeQuery(stat);

            while (rs.next()) {
                vec.clear();
                try {
                    vec.add(rs.getString(1) == null ? "" : rs.getString(1));
                    vec.add(rs.getString(2) == null ? Boolean.FALSE
                            : (rs.getString(2)
                                 .equals("T") ? Boolean.TRUE : Boolean.FALSE));
                    vec.add(rs.getDate(3));
                    vec.add(rs.getString(4) == null ? "" : getTimestampString(rs.getString(4)));
                    vec.add(rs.getString(5) == null ? "" : rs.getString(5));
                    vec.add(rs.getString(6) == null ? "" : rs.getString(6));

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                einmod.addRow((Vector<?>) vec.clone());
                if (einmod.getRowCount() == 1) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            eintab.getSelectionModel()
                                  .addListSelectionListener(listhandler);
                            eintab.setRowSelectionInterval(0, 0);
                        }
                    });

                }

                if (durchlauf > 200) {
                    try {
                        eintab.validate();
                        eintab.repaint();
                        Thread.sleep(80);
                        durchlauf = 0;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                durchlauf++;
            }

            eintab.validate();
            eintab.repaint();
            jscr.validate();
            // doSetAbfrageErgebnis();

        } catch (SQLException e) {
            e.printStackTrace();
            // textArea.setText(e.getMessage()+"\n"+textArea.getText());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException sqlEx) { // ignore }
                    rs = null;
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException sqlEx) { // ignore }
                    stmt = null;
                }
            }
        }

    }

    private String getTimestampString(String ts) {
        try {
            return DatFunk.sDatInDeutsch(ts.split(" ")[0].trim()) + "-" + ts.split(" ")[1].trim()
                                                                                          .substring(0, 8);
        } catch (Exception ex) {

        }
        return "";
    }

    class EinTableModel extends DefaultTableModel {
        /**
        *
        */
        private static final long serialVersionUID = 1L;

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 0) {
                return String.class;
            }
            if (columnIndex == 1) {
                return Boolean.class;
            }
            if (columnIndex == 2) {
                return Date.class;
            }
            if (columnIndex == 3) {
                return Timestamp.class;
            }
            if (columnIndex == 4) {
                return String.class;
            }
            if (columnIndex == 5) {
                return String.class;
            }

            return String.class;
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            return false;
        }

    }

    /******************************************************/
    public void allesAufNull() {
        listenerAusschalten();
        try {
            rtfEditor.editorArea.getDocument()
                                .remove(0, rtfEditor.editorArea.getDocument()
                                                               .getLength());
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        eintab.getSelectionModel()
              .removeListSelectionListener(listhandler);
        einmod.setRowCount(0);
        eintab.validate();
        eintab.repaint();
        eintab.getSelectionModel()
              .addListSelectionListener(listhandler);
        RehaMail.updateTitle("unbekannt");
        for (int i = 0; i < 5; i++) {
            buts[i].setEnabled(false);
        }
    }

    /*
     * protected void loescheBilder(){ ITextDocument textDocument =
     * (ITextDocument)document;
     * 
     * XTextGraphicObjectsSupplier graphicObjSupplier =
     * (XTextGraphicObjectsSupplier)
     * UnoRuntime.queryInterface(XTextGraphicObjectsSupplier.class,
     * textDocument.getXTextDocument()); XNameAccess nameAccess =
     * graphicObjSupplier.getGraphicObjects();
     * 
     * String[] names = nameAccess.getElementNames(); try{ for(int i = 0; i <
     * names.length;i++){ Any xImageAny = (Any) nameAccess.getByName(names[i]);
     * Object xImageObject = xImageAny.getObject(); XTextContent xImage =
     * (XTextContent) xImageObject; xImage.dispose(); } }catch(Exception ex){
     * ex.printStackTrace(); }
     * 
     * } protected void loescheParagraphen(){ ITextDocument textDocument =
     * (ITextDocument)document; IParagraph paragraphs[]; try { paragraphs =
     * textDocument.getTextService().getText().getTextContentEnumeration().
     * getParagraphs(); for(int i = 0; i < paragraphs.length; i++) { XTextContent
     * textContent = paragraphs[i].getXTextContent();
     * textContent.getAnchor().setString(""); textContent.dispose(); }
     * 
     * } catch (TextException e) { e.printStackTrace(); }
     * 
     * }
     */
    public void tabelleLeeren() {
        eintab.getSelectionModel()
              .removeListSelectionListener(listhandler);
        einmod.setRowCount(0);
        eintab.validate();
        eintab.repaint();
    }

    public void listenerAusschalten() {
        eintab.getSelectionModel()
              .removeListSelectionListener(listhandler);
    }

    private void doLoeschen() {
        if (einmod.getRowCount() <= 0) {
            tabelleLeeren();
            return;
        }
        listenerAusschalten();
        // System.out.println("einlesen text");
        int[] rows = eintab.getSelectedRows();
        int frage = JOptionPane.showConfirmDialog(null, "Die ausgewählten Emails wirklich löschen",
                "Achtung wichtige Benutzeranfrage", JOptionPane.YES_NO_OPTION);
        if (frage != JOptionPane.YES_OPTION) {
            return;
        }
        for (int i = 0; i < rows.length; i++) {
            aktId = einmod.getValueAt(eintab.convertRowIndexToModel(rows[i]), 5)
                          .toString();
            SqlInfo.sqlAusfuehren("delete from pimail where id='" + aktId + "' LIMIT 1");
        }
        checkForNewMail();
        textLoeschen();

    }

    private void textLoeschen() {
        if (eintab.getRowCount() <= 0 || eintab.getSelectedRow() < 0) {
            try {
                rtfEditor.editorArea.getDocument()
                                    .remove(0, rtfEditor.editorArea.getDocument()
                                                                   .getLength());
                gelesen = false;
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Fehler beim Einlesen der Nachricht");
                e.printStackTrace();
            }
        }
    }

    public void textEinlesen() {
        if (einmod.getRowCount() <= 0) {
            tabelleLeeren();
            return;
        }
        // System.out.println("einlesen text");
        int row = eintab.getSelectedRow();
        while (!RehaMail.DbOk)
            ; // FIXME: never come back without database;
        if (row < 0) {
            tabelleLeeren();
            return;
        }
        gelesen = (Boolean) einmod.getValueAt(eintab.convertRowIndexToModel(row), 1);
        aktId = einmod.getValueAt(eintab.convertRowIndexToModel(row), 5)
                      .toString();
        if (SqlInfo.holeEinzelFeld("select id from pimail where id ='" + aktId + "' LIMIT 1")
                   .equals("")) {
            JOptionPane.showMessageDialog(null, "Diese Nachricht existiert nicht mehr!");
            checkForNewMail();
            return;

        }
        /****************************************************/
        ByteArrayInputStream ins = null;
        try {
            ins = (ByteArrayInputStream) SqlInfo.holeStream("pimail", "emailtext", "id='" + aktId + "'");
            try {
                rtfEditor.editorArea.getDocument()
                                    .remove(0, rtfEditor.editorArea.getDocument()
                                                                   .getLength());
                rtfEditor.editorArea.getEditorKit()
                                    .read(ins, rtfEditor.editorArea.getDocument(), 0);
                ins.close();
                rtfEditor.editorArea.setCaretPosition(0);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Fehler beim Einlesen der Nachricht");
                e.printStackTrace();
            }

            ins.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                holeAttachments();
                return null;
            }

        }.execute();

    }

    class EinListSelectionHandler implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            ListSelectionModel lsm = (ListSelectionModel) e.getSource();
            boolean isAdjusting = e.getValueIsAdjusting();
            if (isAdjusting) {
                return;
            }
            if (lsm.isSelectionEmpty()) {

            } else {
                int minIndex = lsm.getMinSelectionIndex();
                int maxIndex = lsm.getMaxSelectionIndex();
                for (int i = minIndex; i <= maxIndex; i++) {
                    if (lsm.isSelectedIndex(i)) {
                        gelesen = (Boolean) einmod.getValueAt(eintab.convertRowIndexToModel(i), 1);
                        aktId = einmod.getValueAt(eintab.convertRowIndexToModel(i), 5)
                                      .toString();
                        aktAbsender = einmod.getValueAt(eintab.convertRowIndexToModel(i), 0)
                                            .toString();
                        aktBetreff = einmod.getValueAt(eintab.convertRowIndexToModel(i), 4)
                                           .toString();
                        if (RehaMail.thisFrame != null)
                            RehaMail.thisFrame.setCursor(RehaMail.WAIT_CURSOR);
                        textEinlesen();
                        if (RehaMail.thisFrame != null)
                            RehaMail.thisFrame.setCursor(RehaMail.DEFAULT_CURSOR);
                        break;
                    }
                }
            }

        }
    }

    /***********************************/
    private static String toRTF(String toConvert) {
        String convertet = "";
        convertet = toConvert.replace("Ö", "\\''d6")
                             .replace("ö", "\\''f6");
        convertet = convertet.replace("Ä", "\\''c4")
                             .replace("ä", "\\''e6");
        convertet = convertet.replace("Ü", "\\''dc")
                             .replace("ü", "\\''fc");
        convertet = convertet.replace("ß", "\\''df");
        return String.valueOf(convertet);
    }

    @Override
    public void keyPressed(KeyEvent arg0) {
        if (((JComponent) arg0.getSource()).getName()
                                           .equals("suchen")) {

            if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
                if (suchen.getText()
                          .trim()
                          .equals("")) {
                    checkForNewMail();
                    return;
                }
                try {
                    // toRTF(suchen.getText())
                    ((JComponent) arg0.getSource()).requestFocus();
                    String where = SqlInfo.macheWhereKlauselRTF("where absender='" + RehaMail.mailUser + "' AND ",
                            suchen.getText(), new String[] { "betreff", "emailtext" });
                    String cmd = "select empfaenger_person,"
                            + "gelesen,versanddatum,gelesendatum,betreff,id from pimail " + where
                            + " order by gelesen DESC,versanddatum DESC";
                    // System.out.println(where);
                    doStatementAuswerten(cmd);

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                }
            }
        }

    }

    @Override
    public void keyReleased(KeyEvent arg0) {

    }

    @Override
    public void keyTyped(KeyEvent arg0) {

    }

    /*********************************************/
    /************************************************/
    class ToolsDlgAktuelleRezepte {
        public ToolsDlgAktuelleRezepte(String command, Point pt) {
            // boolean testcase = true;
            Object[] obi = new Object[attachmentFileName.size()];
            Map<Object, ImageIcon> icons = new HashMap<Object, ImageIcon>();
            for (int i = 0; i < attachmentFileName.size(); i++) {
                icons.put(attachmentFileName.get(i), testDatei(attachmentFileName.get(i)));
                obi[i] = attachmentFileName.get(i);
            }
            JList list = new JList(obi);
            list.setCellRenderer(new IconListRenderer(icons));
            RehaMail.toolsDlgRueckgabe = -1;
            ToolsDialog tDlg = new ToolsDialog(RehaMail.thisFrame, "Dateianhänge", list);
            tDlg.setPreferredSize(new Dimension(200, 60 + (attachmentFileName.size() * 28)));
            tDlg.setLocation(pt.x - 70, pt.y + 30);
            tDlg.pack();
            tDlg.setModal(true);
            tDlg.activateListener();
            tDlg.setVisible(true);

            if (RehaMail.toolsDlgRueckgabe == -1) {
                return;
            }
            String komplett = RehaMail.progHome + "temp/" + RehaMail.aktIK + "/"
                    + attachmentFileName.get(RehaMail.toolsDlgRueckgabe);

            if (RehaMail.toolsDlgRueckgabe < 0) {
                return;
            }
            if (komplett.toUpperCase()
                        .endsWith(".PDF")
                    || komplett.toUpperCase()
                               .endsWith(".ODT")
                    || komplett.toUpperCase()
                               .endsWith(".OTT")
                    || komplett.toUpperCase()
                               .endsWith(".ODS")) {
                if (!speichereDatei(new String[] { null, RehaMail.progHome + "temp/" + RehaMail.aktIK },
                        attachmentFileName.get(RehaMail.toolsDlgRueckgabe),
                        Integer.toString(RehaMail.toolsDlgRueckgabe + 1))) {
                    return;
                }

                if (komplett.toUpperCase()
                            .endsWith(".PDF")) {
                    new ReaderStart(komplett, RehaMail.pdfReader);
                } else if (komplett.toUpperCase()
                                   .endsWith(".ODT")
                        || komplett.toUpperCase()
                                   .endsWith(".ODT")) {
                    office.OOTools.starteWriterMitDatei(komplett.replace("//", "/"));
                } else if (komplett.toUpperCase()
                                   .endsWith(".ODS")) {
                    office.OOTools.starteCalcMitDatei(komplett);
                }
            } else {
                String[] indatei = dateiDialog(attachmentFileName.get(RehaMail.toolsDlgRueckgabe));
                if (indatei[0] == null) {
                    return;
                }
                if (speichereDatei(indatei, attachmentFileName.get(RehaMail.toolsDlgRueckgabe),
                        Integer.toString(RehaMail.toolsDlgRueckgabe + 1))) {
                    JOptionPane.showMessageDialog(null, "Datei " + attachmentFileName.get(RehaMail.toolsDlgRueckgabe)
                            + " erfolgreich gespeichert!\n\n" + "Verzeichnis: --> " + indatei[1].replace("\\", "/"));

                }

            }

        }

        private ImageIcon testDatei(String filename) {
            if (filename.toUpperCase()
                        .endsWith(".PDF")) {
                return RehaMail.attachmentIco[0];
            } else if (filename.toUpperCase()
                               .endsWith(".ODT")
                    || filename.toUpperCase()
                               .endsWith(".OTT")) {
                return RehaMail.attachmentIco[1];
            } else if (filename.toUpperCase()
                               .endsWith(".ODS")) {
                return RehaMail.attachmentIco[2];
            }
            return RehaMail.attachmentIco[4];
        }
    }

    /*********************************************/
    private boolean speichereDatei(String[] pfade, String datei, String attachnumber) {
        boolean success = true;
        // System.out.println(pfade[0]);
        // System.out.println(pfade[1]);
        String komplett = pfade[1].replace("\\", "/") + "/" + datei;
        try {
            RehaMail.thisFrame.setCursor(RehaMail.WAIT_CURSOR);
            File f = new File(komplett);
            InputStream inputStream = SqlInfo.holeStream("pimail", "attach" + attachnumber, "id='" + aktId + "'");
            OutputStream out = new FileOutputStream(f);
            byte buf[] = new byte[1024];
            int len;
            while ((len = inputStream.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            inputStream.close();
            RehaMail.thisFrame.setCursor(RehaMail.DEFAULT_CURSOR);
        } catch (IOException e) {
            RehaMail.thisFrame.setCursor(RehaMail.DEFAULT_CURSOR);
            JOptionPane.showMessageDialog(null, "Speichern der Datei " + datei + " fehlgeschlagen");

            return false;
        }
        return success;

    }

    private String[] dateiDialog(String pfad) {
        // String sret = "";
        String[] sret = { null, null };
        // System.out.println("Speichern in "+pfad);
        final JFileChooser chooser = new JFileChooser("Verzeichnis auswählen");
        chooser.setDialogType(JFileChooser.SAVE_DIALOG);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        final File file = new File(pfad);

        chooser.setCurrentDirectory(new File(RehaMail.progHome));
        chooser.setSelectedFile(file);
        chooser.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent e) {
                if (e.getPropertyName()
                     .equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)
                        || e.getPropertyName()
                            .equals(JFileChooser.DIRECTORY_CHANGED_PROPERTY)) {
                    // final File f = (File) e.getNewValue();
                }
            }

        });
        chooser.setVisible(true);
        setCursor(RehaMail.thisClass.normalCursor);
        final int result = chooser.showSaveDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            File inputVerzFile = chooser.getSelectedFile();
            String inputVerzStr = inputVerzFile.getPath();

            if (inputVerzFile.getName()
                             .trim()
                             .equals("")) {

                // sret = "";
            } else {
                sret[0] = inputVerzFile.getName()
                                       .trim();
                sret[1] = inputVerzStr;
            }
        } else {
            // sret = ""; //vorlagenname.setText(SystemConfig.oTerminListe.NameTemplate);
        }
        chooser.setVisible(false);

        return sret;
    }

}
