package opRgaf;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import opRgaf.CommonTools.DateTimeFormatters;
import opRgaf.rezept.Money;
import opRgaf.rezept.Rezeptnummer;

public class OffenePostenTableModel extends AbstractTableModel {

    static final int KENNUNG = 0;
    static final int RGNR = 1;
    static final int RGDATUM = 2;
    static final int GESAMTBETRAG = 3;
    static final int OFFEN = 4;
    static final int BEARBEITUNGSGEBUEHR = 5;
    static final int BEZAHLTAM = 6;
    static final int MAHNUNGEINS = 7;
     static final int MAHNUNGZWEI = 8;
    static final int KRANKENKASSENNAME = 9;
    static final int REZNUMMER = 10;
    static final int TABELLENID = 11;
    private static final Logger logger = LoggerFactory.getLogger(OffenePostenTableModel.class);

    private List<OffenePosten> opListe ;





    public OffenePostenTableModel(List<OffenePosten> opListe) {

        this.opListe = opListe;

    }


    @Override
    public boolean isCellEditable(int row, int col) {
        //TODO describe and change
        return col > 1 && col < 9;
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
            op.gesamtBetrag = (Money) aValue;
            break;
        case OFFEN:
            op.offen =  (Money) aValue;
            break;
        case BEARBEITUNGSGEBUEHR:
            op.bearbeitungsGebuehr =  (Money) aValue;
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
        fireTableCellUpdated(rowIndex, columnIndex);
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



    OffenePosten getValue (int rowIndex) {
        return opListe.get(rowIndex);
    }



}
