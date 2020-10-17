package rehaKassenbuch;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.sun.star.beans.PropertyVetoException;
import com.sun.star.beans.UnknownPropertyException;
import com.sun.star.container.NoSuchElementException;
import com.sun.star.lang.IllegalArgumentException;
import com.sun.star.lang.IndexOutOfBoundsException;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.sheet.XSheetCellCursor;
import com.sun.star.sheet.XSpreadsheet;
import com.sun.star.sheet.XSpreadsheets;
import com.sun.star.uno.UnoRuntime;

import CommonTools.ButtonTools;
import CommonTools.DatFunk;
import CommonTools.DateTableCellEditor;
import CommonTools.DblCellEditor;
import CommonTools.DoubleTableCellRenderer;
import CommonTools.JCompTools;
import CommonTools.JRtaComboBox;
import CommonTools.SqlInfo;
import CommonTools.TableTool;
import ag.ion.bion.officelayer.application.OfficeApplicationException;
import ag.ion.bion.officelayer.document.DocumentDescriptor;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.document.IDocumentDescriptor;
import ag.ion.bion.officelayer.document.IDocumentService;
import ag.ion.bion.officelayer.spreadsheet.ISpreadsheetDocument;
import ag.ion.noa.NOAException;
import office.OOTools;

public class RehaKassenbuchEdit extends JXPanel implements TableModelListener {

    RehaKassenbuchTab eltern;
    JButton[] buts = { null, null, null, null };
    ActionListener al = null;
    Vector<String> datavec = new Vector<String>();
    JRtaComboBox combo = null;

    MyKassenbuchTableModel tabmod = null;
    JXTable tab = null;

    DecimalFormat dcf = new DecimalFormat("###0.00");

    Vector<Vector<String>> feldNamen = null;

    JLabel anzahlsaetze = null;

    int calcrow = 0;
    ISpreadsheetDocument spreadsheetDocument = null;
    IDocument document = null;
    XSheetCellCursor cellCursor = null;

    public RehaKassenbuchEdit(RehaKassenbuchTab rkbtab) {
        super();
        eltern = rkbtab;
        setLayout(new BorderLayout());
        activateListener();
        add(getContent(), BorderLayout.CENTER);

    }

    private JXPanel getContent() {
        // 1 2 3
        String xwerte = "10dlu,fill:0:grow(1.0),10dlu";
        // 1 2 3 4 5 6
        String ywerte = "10dlu,fill:0:grow(1.0),2dlu,p,2dlu,10dlu";
        FormLayout lay = new FormLayout(xwerte, ywerte);
        CellConstraints cc = new CellConstraints();
        JXPanel jpan = new JXPanel();
        jpan.setLayout(lay);

            doKBErmitteln();
        tabmod = new MyKassenbuchTableModel();
        feldNamen = SqlInfo.holeFelder("describe kasse");
        String[] spalten = new String[feldNamen.size()];
        for (int i = 0; i < feldNamen.size(); i++) {
            spalten[i] = feldNamen.get(i)
                                  .get(0);
        }
        tabmod.setColumnIdentifiers(spalten);
        tab = new JXTable(tabmod);
        tab.setCellSelectionEnabled(true);
        tab.setAutoStartEditOnKeyStroke(true);
        tab.setHighlighters(HighlighterFactory.createSimpleStriping(HighlighterFactory.CLASSIC_LINE_PRINTER));
        tab.setSortable(false);
        tabmod.addTableModelListener(this);
        tab.getColumn(0)
           .setCellRenderer(new DoubleTableCellRenderer());

        tab.getColumn(0)
           .setCellEditor(new DblCellEditor());
        tab.getColumn(1)
           .setCellRenderer(new DoubleTableCellRenderer());
        tab.getColumn(1)
           .setCellEditor(new DblCellEditor());
        tab.getColumn(3)
           .setMinWidth(350);
        tab.unregisterKeyboardAction(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
        MyListener myEnterListener = new MyListener();
        tab.registerKeyboardAction(myEnterListener, "Enter", KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false),
                JComponent.WHEN_FOCUSED);

        DateTableCellEditor tble = new DateTableCellEditor();
        tab.getColumnModel()
           .getColumn(2)
           .setCellEditor(tble);
        JScrollPane jscr = JCompTools.getTransparentScrollPane(tab);
        jscr.validate();
        jpan.add(jscr, cc.xy(2, 2, CellConstraints.FILL, CellConstraints.FILL));

        /***************/
        // 1 2 3 4 5 6 7 8 9 1 2 4
        FormLayout lay2 = new FormLayout("0dlu,65dlu,5dlu,65dlu,5dlu,65dlu,5dlu,65dlu,0dlu:g", "10dlu,p,10dlu");
        CellConstraints cc2 = new CellConstraints();
        JXPanel jpan2 = new JXPanel(lay2);
        combo = new JRtaComboBox();
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                doKBErmitteln();
                return null;
            }
        }.execute();
        combo.setActionCommand("combo");
        combo.addActionListener(al);
        jpan2.add(combo, cc2.xy(2, 2));
        buts[0] = ButtonTools.macheButton("neue Buchung", "buchungneu", al);
        buts[0].setMnemonic(KeyEvent.VK_N);
        jpan2.add(buts[0], cc2.xy(4, 2));
        buts[1] = ButtonTools.macheButton("löschen Buchung", "buchungloeschen", al);
        buts[1].setMnemonic(KeyEvent.VK_L);
        jpan2.add(buts[1], cc2.xy(6, 2));
        buts[2] = ButtonTools.macheButton("Calc starten", "calc", al);
        buts[2].setMnemonic(KeyEvent.VK_C);
        jpan2.add(buts[2], cc2.xy(8, 2));

        anzahlsaetze = new JLabel("");
        anzahlsaetze.setForeground(Color.BLUE);
        jpan2.add(anzahlsaetze, cc.xy(9, 2, CellConstraints.RIGHT, CellConstraints.DEFAULT));
        jpan2.validate();

        jpan.add(jpan2, cc.xy(2, 4));

        jpan.validate();

        return jpan;
    }

    private class MyListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String actionCmd = e.getActionCommand();
            if (actionCmd.equals("Enter")) {
                int row = tab.getSelectedRow();
                int col = tab.getSelectedColumn();
                tab.getCellEditor(row, col)
                   .stopCellEditing();
                if (col == tab.getColumnCount() - 1) {
                    col = 0;
                } else {
                    col++;
                }
                tab.setRowSelectionInterval(row, row);
                tab.setColumnSelectionInterval(col, col);
            }
        }
    }

    private void activateListener() {
        al = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                String cmd = arg0.getActionCommand();
                if (cmd.equals("combo")) {
                    fuelleTabelle(combo.getSelectedIndex());
                    return;
                }
                if (cmd.equals("buchungneu")) {
                    doNeuSatz();
                    anzahlsaetze.setText(Integer.toString(tab.getRowCount()) + " Buchungssätze");
                    return;
                }
                if (cmd.equals("buchungloeschen")) {
                    doLoeschenSatz();
                    anzahlsaetze.setText(Integer.toString(tab.getRowCount()) + " Buchungssätze");
                    return;
                }
                if (cmd.equals("calc")) {
                    try {
                        starteCalc();
                    } catch (NoSuchElementException e) {
                        e.printStackTrace();
                    } catch (WrappedTargetException e) {
                        e.printStackTrace();
                    } catch (UnknownPropertyException e) {
                        e.printStackTrace();
                    } catch (PropertyVetoException e) {
                        e.printStackTrace();
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (OfficeApplicationException e) {
                        e.printStackTrace();
                    } catch (NOAException e) {
                        e.printStackTrace();
                    } catch (IndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                    return;
                }
            }
        };
    }

    private void doNeuSatz() {
        if (combo.getSelectedIndex() <= 0) {
            return;
        }
        String cmd = "insert into " + combo.getSelectedItem() + " set ktext='',rez_nr=''";
        int id = SqlInfo.holeIdSimple(combo.getSelectedItem()
                                           .toString(),
                cmd);
        Vector<Vector<String>> vecx = SqlInfo.holeFelder("select * from " + combo.getSelectedItem()
                                                                                 .toString()
                + " where id = '" + Integer.toString(id) + "' LIMIT 1");
        tabmod.addRow((Vector<?>) vecx.get(0)
                                      .clone());
        tab.requestFocus();
        tab.scrollCellToVisible(tab.getRowCount() - 1, 0);
        tab.setRowSelectionInterval(tab.getRowCount() - 1, tab.getRowCount() - 1);
        tab.setColumnSelectionInterval(0, 0);
    }

    private void doLoeschenSatz() {
        if (tab.getSelectedRow() < 0) {
            return;
        }
        SqlInfo.sqlAusfuehren("delete from " + combo.getSelectedItem()
                                                    .toString()
                + " where id = '" + tab.getValueAt(tab.getSelectedRow(), 6)
                                       .toString()
                + "' LIMIT 1");
        TableTool.loescheRow(tab, tab.getSelectedRow());
    }

    public void doKBErmitteln() {

        long zeit = System.currentTimeMillis();

        Vector<Vector<String>> vec = SqlInfo.holeFelder("show tables");
        datavec.clear();
        datavec.add("./.");
        for (int i = 0; i < vec.size(); i++) {
            if (vec.get(i)
                   .get(0)
                   .startsWith("kb_")) {
                datavec.add(vec.get(i)
                               .get(0));
            }
        }
        if (combo == null) {
            System.out.println("combo == null");
            return;
        }
        combo.setDataVector(datavec);
        combo.setSelectedIndex(0);
        fuelleTabelle(0);
    }

    public void fuelleTabelle(int tabindex) {
        tabmod.setRowCount(0);
        tab.validate();

        if (tabindex <= 0) {
            anzahlsaetze.setText("");
            return;
        }
        tabmod.setRowCount(0);
        tab.validate();
        tab.repaint();
        String mystmt = "select * from " + datavec.get(tabindex) + " order by id";
        try (Statement stmt = RehaKassenbuch.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_UPDATABLE); ResultSet rs = stmt.executeQuery(mystmt);) {
            Vector<Object> vec = new Vector<Object>();
            int durchlauf = 0;
            int lang = feldNamen.size();

            while (rs.next()) {
                vec.clear();
                try {
                    for (int i = 0; i < lang; i++) {
                        if (feldNamen.get(i)
                                     .get(1)
                                     .contains("varchar(")) {
                            vec.add((rs.getString(i + 1) == null ? ""
                                    : (rs.getString(i + 1)
                                         .equals("") ? "" : rs.getString(i + 1))));
                        } else if (feldNamen.get(i)
                                            .get(1)
                                            .contains("enum(")) {
                            vec.add((rs.getString(i + 1) == null ? Boolean.FALSE
                                    : (rs.getString(i + 1)
                                         .equals("T") ? Boolean.TRUE : Boolean.FALSE)));
                        } else if (feldNamen.get(i)
                                            .get(1)
                                            .contains("decimal(")) {
                            if (rs.getBigDecimal(i + 1) == null) {
                                vec.add(Double.parseDouble("0.00"));
                            } else {
                                vec.add(rs.getBigDecimal(i + 1)
                                          .doubleValue());
                            }
                        } else if (feldNamen.get(i)
                                            .get(1)
                                            .contains("tinyint(")) {
                            vec.add(rs.getInt(i + 1));
                        } else if (feldNamen.get(i)
                                            .get(1)
                                            .contains("int(")) {
                            vec.add(rs.getInt(i + 1));
                        } else if (feldNamen.get(i)
                                            .get(1)
                                            .contains("date")) {
                            vec.add(rs.getDate(i + 1));
                        } else {
                            vec.add((rs.getString(i + 1) == null ? "" : rs.getString(i + 1)));
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                tabmod.addRow((Vector<?>) vec.clone());
                if (durchlauf > 200) {
                    try {
                        tab.validate();
                        tab.repaint();
                        Thread.sleep(75);
                        durchlauf = 0;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                durchlauf++;
            }
            tab.requestFocus();
            if (tab.getRowCount() > 0) {
                tab.scrollCellToVisible(tab.getRowCount() - 1, 0);
                tab.setRowSelectionInterval(tab.getRowCount() - 1, tab.getRowCount() - 1);
                tab.setColumnSelectionInterval(0, 0);
            }
            anzahlsaetze.setText(Integer.toString(tab.getRowCount()) + " Buchungssätze");
            tab.validate();
            tab.repaint();

        } catch (SQLException ev) {
        }

    }

    @Override
    public void tableChanged(TableModelEvent arg0) {
        if (arg0.getType() == TableModelEvent.INSERT) {
            return;
        }
        if (arg0.getType() == TableModelEvent.UPDATE) {
            try {
                int col = arg0.getColumn();
                int row = arg0.getFirstRow();
                String value = "";

                if (tabmod.getColumnClass(col) == Boolean.class) {
                    value = (tabmod.getValueAt(row, col) == Boolean.FALSE ? "F" : "T");
                } else if (tabmod.getColumnClass(col) == Date.class) {
                    value = DatFunk.sDatInSQL(tabmod.getValueAt(row, col)
                                                    .toString());
                } else if (tabmod.getColumnClass(col) == Double.class) {
                    value = dcf.format(tabmod.getValueAt(row, col))
                               .replace(",", ".");
                } else if (tabmod.getColumnClass(col) == Integer.class) {
                    value = Integer.toString((Integer) tabmod.getValueAt(row, col));
                } else if (tabmod.getColumnClass(col) == String.class) {
                    value = tabmod.getValueAt(row, col)
                                  .toString();
                }
                direktSchreiben(row, col, value);

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Fehler in der Dateneingbe");
            }

            return;
        }


    }

    class MyKassenbuchTableModel extends DefaultTableModel {

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (feldNamen.get(columnIndex)
                         .get(1)
                         .contains("varchar(")) {
                return String.class;
            } else if (feldNamen.get(columnIndex)
                                .get(1)
                                .contains("enum(")) {
                return Boolean.class;
            } else if (feldNamen.get(columnIndex)
                                .get(1)
                                .contains("decimal(")) {
                return Double.class;
            } else if (feldNamen.get(columnIndex)
                                .get(1)
                                .contains("tinyint(")) {
                return Integer.class;
            } else if (feldNamen.get(columnIndex)
                                .get(1)
                                .contains("int(")) {
                return Integer.class;
            } else if (feldNamen.get(columnIndex)
                                .get(1)
                                .contains("date")) {
                return Date.class;
            }
            return String.class;
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            if (col == 6) {
                return false;
            }
            return true;
        }

    }

    private void direktSchreiben(int row, int col, String value) {
        if (combo.getSelectedIndex() <= 0) {
            return;
        }

        String id = tabmod.getValueAt(row, 6)
                          .toString();

        String cmd = "update " + combo.getSelectedItem()
                                      .toString()
                + " set " + feldNamen.get(col)
                                     .get(0)
                + " = '" + value + "' where id = '" + id + "' LIMIT 1";
        SqlInfo.sqlAusfuehren(cmd);

    }

    private void starteCalc()
            throws OfficeApplicationException, NOAException, NoSuchElementException, WrappedTargetException,
            UnknownPropertyException, PropertyVetoException, IllegalArgumentException, IndexOutOfBoundsException {
        int tabindex = combo.getSelectedIndex();
        if (tabindex <= 0) {
            return;
        }
        fuelleTabelle(tabindex);
        if (RehaKassenbuch.officeapplication.isPresent()) {
            IDocumentService documentService = RehaKassenbuch.officeapplication.get()
                                                                               .getDocumentService();
            IDocumentDescriptor docdescript = new DocumentDescriptor();
            docdescript.setHidden(true);
            docdescript.setAsTemplate(false);
            document = documentService.constructNewDocument(IDocument.CALC, DocumentDescriptor.DEFAULT);
            spreadsheetDocument = (ISpreadsheetDocument) document;

            XSpreadsheets spreadsheets = spreadsheetDocument.getSpreadsheetDocument()
                                                            .getSheets();
            String sheetName = "Tabelle1";
            XSpreadsheet spreadsheet1 = UnoRuntime.queryInterface(XSpreadsheet.class,
                    spreadsheets.getByName(sheetName));
            cellCursor = spreadsheet1.createCursor();
            final ISpreadsheetDocument xspredsheetDocument = spreadsheetDocument;
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    xspredsheetDocument.getFrame()
                                       .getXFrame()
                                       .getContainerWindow()
                                       .setVisible(true);
                    xspredsheetDocument.getFrame()
                                       .setFocus();
                }
            });
            OOTools.doColWidth(spreadsheetDocument, sheetName, 3, 3, 1000);
            OOTools.doColWidth(spreadsheetDocument, sheetName, 4, 4, 10000);
            OOTools.doColNumberFormat(spreadsheetDocument, sheetName, 0, 1, 2);

            OOTools.doCellValue(cellCursor, 0, 0, "EINNAHME");
            OOTools.doCellValue(cellCursor, 1, 0, "AUSGABE");
            OOTools.doCellValue(cellCursor, 2, 0, "DATUM");
            OOTools.doCellValue(cellCursor, 4, 0, "TEXT");
            Vector<Vector<Object>> vec = tabmod.getDataVector();
            for (int i = 0; i < vec.size(); i++) {
                if (vec.get(i)
                       .get(0) instanceof Double) {
                    if (((Double) vec.get(i)
                                     .get(0)) != 0.) {
                        OOTools.doCellValue(cellCursor, 0, i + 1, vec.get(i)
                                                                     .get(0));
                    }
                    if (((Double) vec.get(i)
                                     .get(1)) != 0.) {
                        OOTools.doCellValue(cellCursor, 1, i + 1, vec.get(i)
                                                                     .get(1));
                    }
                }
                if (vec.get(i)
                       .get(0) instanceof java.lang.String) {
                    if (((Double) vec.get(i)
                                     .get(0)) != 0.) {
                        OOTools.doCellValue(cellCursor, 0, i + 1, vec.get(i)
                                                                     .get(0));
                    }
                    if (((Double) vec.get(i)
                                     .get(1)) != 0.) {
                        OOTools.doCellValue(cellCursor, 1, i + 1, vec.get(i)
                                                                     .get(1));
                    }
                }

                try {
                    OOTools.doCellValue(cellCursor, 2, i + 1, DatFunk.sDatInDeutsch(((Date) vec.get(i)
                                                                                               .get(2)).toString()));
                } catch (Exception ex) {
                }
                OOTools.doCellValue(cellCursor, 4, i + 1, vec.get(i)
                                                             .get(3));

            }
        }
    }

}
