package opRgaf;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import javax.swing.RowFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import opRgaf.CommonTools.DateTimeFormatters;
import opRgaf.rezept.Money;
import opRgaf.rezept.Rezeptnummer;

public class OffenePostenTableModel extends AbstractTableModel {

    private static final class OffenePostenTableModelFilter extends RowFilter<OffenePostenTableModel, Integer> {
        @Override
        public boolean include(Entry<? extends OffenePostenTableModel, ? extends Integer> entry) {
            String value = (String) entry.getValue(OffenePostenTableModel.KENNUNG);
            return value.toLowerCase().contains("abd");
        }
    }

    private static final int KENNUNG = 0;
    private static final int RGNR = 1;
    private static final int RGDATUM = 2;
    private static final int GESAMTBETRAG = 3;
    private static final int OFFEN = 4;
    private static final int BEARBEITUNGSGEBUEHR = 5;
    private static final int BEZAHLTAM = 6;
    private static final int MAHNUNGEINS = 7;
    private static final int MAHNUNGZWEI = 8;
    private static final int KRANKENKASSENNAME = 9;
    private static final int REZNUMMER = 10;
    private static final int TABELLENID = 11;
    private static final Logger logger = LoggerFactory.getLogger(OffenePostenTableModel.class);

    private List<OffenePosten> opListe;

    static RowFilter<OffenePostenTableModel, Integer> containsabd = new OffenePostenTableModelFilter();

    public OffenePostenTableModel(List<OffenePosten> opListe) {

        this.opListe = opListe;
    }


    /**
     * @deprecated Use {@link #setFilter(RowFilter)} instead
     */
    TableRowSorter<OffenePostenTableModel> setfilter() {
        return setFilter(containsabd);
    }


    TableRowSorter<OffenePostenTableModel> setFilter(RowFilter<OffenePostenTableModel, Integer> rowFilter) {
        TableRowSorter<OffenePostenTableModel> sorter = new TableRowSorter<>(this);
        sorter.setRowFilter(rowFilter);
       return sorter;

    }

    @Override
    public int getRowCount() {
        return opListe.size();
    }

    @Override
    public int getColumnCount() {
        return 12;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        OffenePosten op = opListe.get(rowIndex);
        switch (columnIndex) {
        case KENNUNG:
            return op.kennung.toString();

        case RGNR:
            return op.rgNr;
        case RGDATUM:
            return op.rgDatum;
        case GESAMTBETRAG:
            return op.gesamtBetrag;
        case OFFEN:
            return op.offen;
        case BEARBEITUNGSGEBUEHR:
            return op.bearbeitungsGebuehr;
        case BEZAHLTAM:
            return op.bezahltAm;
        case MAHNUNGEINS:
            return op.mahnungEins;
        case MAHNUNGZWEI:
            return op.mahnungZwei;
        case KRANKENKASSENNAME:
            return op.krankenKassenName;
        case REZNUMMER:
            return op.rezNummer.rezeptNummer();
        case TABELLENID:
            return op.tabellenId;
        default:
            logger.error("unknown column request column no= " + columnIndex);
            return "";
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

        OffenePosten op = opListe.get(rowIndex);
        switch (columnIndex) {
        case KENNUNG:
            op.kennung = new Kennung((String) aValue);
            break;
        case RGNR:
            op.rgNr = (String) aValue;
            break;
        case RGDATUM:
            op.rgDatum = evaluateForLocalDate(aValue);
            break;
        case GESAMTBETRAG:
            op.gesamtBetrag = evaluateForMoney((String) aValue);
            break;
        case OFFEN:
            op.offen = evaluateForMoney((String) aValue);
            break;
        case BEARBEITUNGSGEBUEHR:
            op.bearbeitungsGebuehr = evaluateForMoney((String) aValue);
            break;
        case BEZAHLTAM:
            op.bezahltAm = evaluateForLocalDate(aValue);
            break;
        case MAHNUNGEINS:
            op.mahnungEins = evaluateForLocalDate(aValue);
            break;
        case MAHNUNGZWEI:
            op.mahnungZwei = evaluateForLocalDate(aValue);
            break;
        case KRANKENKASSENNAME:
            op.krankenKassenName = ((String) aValue);
            break;
        case REZNUMMER:
            op.rezNummer = new Rezeptnummer((String) aValue);
            break;
        case TABELLENID:
            logger.error("Im Leben nicht!!11!!");
            break;
        default:
            logger.error("unknown column request column no= " + columnIndex);
        }
    }

    private Money evaluateForMoney(String aValue) {
        try {
            return new Money(aValue);
        } catch (Exception e) {
            return new Money();
        }
    }

    private LocalDate evaluateForLocalDate(Object aValue) {
        try {
            return LocalDate.parse((CharSequence) aValue, DateTimeFormatters.ddMMYYYYmitPunkt);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
        case KENNUNG:
            return String.class;
        case RGNR:
            return String.class;
        case RGDATUM:
            return LocalDate.class;
        case GESAMTBETRAG:
            return Money.class;
        case OFFEN:
            return Money.class;
        case BEARBEITUNGSGEBUEHR:
            return Money.class;
        case BEZAHLTAM:
            return LocalDate.class;
        case MAHNUNGEINS:
            return LocalDate.class;
        case MAHNUNGZWEI:
            return LocalDate.class;
        case KRANKENKASSENNAME:
            return String.class;
        case REZNUMMER:
            return String.class;
        case TABELLENID:
            return Integer.class;
        }

        return super.getColumnClass(columnIndex);
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
        case KENNUNG:
            return "Name,Vorname,Geburtstag";
        case RGNR:
            return "Rechn-Nr.";
        case RGDATUM:
            return "Rechn-Datum";
        case GESAMTBETRAG:
            return "Gesamtbetrag";
        case OFFEN:
            return "offen";
        case BEARBEITUNGSGEBUEHR:
            return "Bearb.Gebühr";
        case BEZAHLTAM:
            return "bezahlt am";
        case MAHNUNGEINS:
            return "1. Mahnung";
        case MAHNUNGZWEI:
            return "2. Mahnung";
        case KRANKENKASSENNAME:
            return "Krankenkasse";
        case REZNUMMER:
            return "Rezeptnummer";
        case TABELLENID:
            return "id";
        }
        return super.getColumnName(column);
    }

    boolean addRow(OffenePosten op) {
        return opListe.add(op);
    }

    OffenePosten removeRow(int index) {
        return opListe.remove(index);
    }

}
