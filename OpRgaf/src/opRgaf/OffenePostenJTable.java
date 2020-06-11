package opRgaf;

import static opRgaf.OffenePostenTableModel.BEARBEITUNGSGEBUEHR;
import static opRgaf.OffenePostenTableModel.BEZAHLTAM;
import static opRgaf.OffenePostenTableModel.GESAMTBETRAG;
import static opRgaf.OffenePostenTableModel.MAHNUNGEINS;
import static opRgaf.OffenePostenTableModel.MAHNUNGZWEI;
import static opRgaf.OffenePostenTableModel.OFFEN;
import static opRgaf.OffenePostenTableModel.RGDATUM;
import static opRgaf.OffenePostenTableModel.RGNR;

import java.util.Arrays;

import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;

import CommonTools.DateTableCellEditor;
import CommonTools.MitteRenderer;

final class OffenePostenJTable extends JTable {
    private static final DateTableCellEditor DATE_CELL_EDITOR = new DateTableCellEditor();
    private static final MitteRenderer MITTE_RENDERER = new MitteRenderer();
    private static final MoneyCellRenderer MONEY_RENDERER = new MoneyCellRenderer();
    private static final DateRenderer DATE_RENDERER = new DateRenderer();
    private static final TableCellEditor MONEY_CELL_EDITOR = new MoneyCellEditor();

    private final class Everythingisfine extends RowFilter<OffenePostenTableModel, Integer> {
        @Override
        public boolean include(Entry<? extends OffenePostenTableModel, ? extends Integer> entry) {
            return true;
        }
    }

    TableRowSorter<OffenePostenTableModel> sorter;
    private RowFilter<OffenePostenTableModel, Integer> contentfilter = new Everythingisfine();
    private RowFilter<OffenePostenTableModel, Integer> typefilter = new Everythingisfine();

    OffenePostenJTable(OffenePostenTableModel dm) {
        super(dm);
        setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        sorter = new TableRowSorter<>((OffenePostenTableModel) getModel());

    }

    void setFilter(RowFilter<OffenePostenTableModel, Integer> filter) {

        contentfilter = filter;

        sorter.setRowFilter(RowFilter.andFilter(Arrays.asList(filter, typefilter)));

        setRowSorter(sorter);
        sorter.sort();

    }

    void setTypeFilter(RowFilter<OffenePostenTableModel, Integer> filter) {

        typefilter = filter;

        sorter.setRowFilter(RowFilter.andFilter(Arrays.asList(contentfilter, filter)));

        setRowSorter(sorter);
        sorter.sort();
    }

    @Override
    public TableCellRenderer getCellRenderer(int row, int column) {

        switch (column) {
        case RGNR:
            return MITTE_RENDERER;
        case GESAMTBETRAG:
            return MONEY_RENDERER;
        case OFFEN:
            return MONEY_RENDERER;
        case BEARBEITUNGSGEBUEHR:
            return MONEY_RENDERER;
        case RGDATUM:
            return DATE_RENDERER;
        case BEZAHLTAM:
            return DATE_RENDERER;
        case MAHNUNGEINS:
            return DATE_RENDERER;
        case MAHNUNGZWEI:
            return DATE_RENDERER;

        default:
            return super.getCellRenderer(row, column);
        }

    }

    @Override
    public TableCellEditor getCellEditor(int row, int column) {
        switch (column) {
        case RGDATUM:
            return DATE_CELL_EDITOR;
        case GESAMTBETRAG:
            return MONEY_CELL_EDITOR;
        case OFFEN:
            return MONEY_CELL_EDITOR;
        case BEARBEITUNGSGEBUEHR:
            return MONEY_CELL_EDITOR;

        case BEZAHLTAM:
            return DATE_CELL_EDITOR;
        case MAHNUNGEINS:
            return DATE_CELL_EDITOR;
        case MAHNUNGZWEI:
            return DATE_CELL_EDITOR;

        default:
            return super.getCellEditor(row, column);
        }

    }

}
