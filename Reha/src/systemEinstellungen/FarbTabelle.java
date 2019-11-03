package systemEinstellungen;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JXTable;

import jxTableTools.ColorEditor;
import jxTableTools.ColorRenderer;
import jxTableTools.JLabelRenderer;

final class FarbTabelle extends JXTable {

    public FarbTabelle(KalenderFarbenModel ftm) {
        setModel(ftm);
        setColumnSelectionAllowed(false);
        setRowSelectionAllowed(true);
        setCellSelectionEnabled(true);
        getColumn(2).setCellEditor(new ColorEditor());
        getColumn(2).setCellRenderer(new ColorRenderer(true));
        getColumn(2).getCellEditor()
                    .addCellEditorListener(this);
        getColumn(3).setCellEditor(new ColorEditor());
        getColumn(3).setCellRenderer(new ColorRenderer(true));
        getColumn(3).getCellEditor()
                    .addCellEditorListener(this);
        getColumn(4).setCellRenderer(new JLabelRenderer());
        setSortable(false);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent arg0) {
                arg0.consume();
            }

            @Override
            public void mouseClicked(MouseEvent arg0) {
                arg0.consume();

                if (arg0.getClickCount() == 2) {
                    int row = getSelectedRow();
                    int col = getSelectedColumn();
                    startCellEditing(FarbTabelle.this, row, col);
                }
                if (arg0.getClickCount() == 1) {
                    int row = getSelectedRow();
                    setRowSelectionInterval(row, row);
                }
            }
        });
    }

    private void startCellEditing(JXTable table, int row, int col) {
        final int xrows = row;
        final int xcols = col;
        final JXTable xtable = table;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                xtable.setRowSelectionInterval(xrows, xrows);
                xtable.setColumnSelectionInterval(xcols, xcols);
                xtable.scrollRowToVisible(xrows);
                xtable.editCellAt(xrows, xcols);
            }
        });
    }

}
