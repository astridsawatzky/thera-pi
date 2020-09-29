package systemEinstellungen.abrechnung.formulare;

import java.util.Map;

import javax.print.PrintService;

import CommonTools.ini.Settings;

public class parameterMapper {

    private static final int ANZAHL_TAXIERUNGSDRUCK = 1;

    GKVAbrechnungsParameter readSettings(Settings ini) {

        Map<String, PrintService> printers = Environment.printservices();
        ;

        final String HMVGKVRchnung = "HMGKVRechnung";
        boolean taxEinstellungAusDrucker = ini.getBooleanProperty(HMVGKVRchnung, "usePrinterFromTemplate");

        String tdruckerName = ini.getStringProperty(HMVGKVRchnung, "Tdrucker");
        PrintService taxPrinter = printers.getOrDefault(tdruckerName, new UnknownPrintService(tdruckerName));

        FormularParameter taxierung = new FormularParameter(taxEinstellungAusDrucker, taxPrinter,
                ANZAHL_TAXIERUNGSDRUCK);

        String gkvdruckerName = ini.getStringProperty(HMVGKVRchnung, "Rdrucker");
        PrintService gkvPrinter = printers.getOrDefault(gkvdruckerName, new UnknownPrintService(gkvdruckerName));

        int gkvAnzahl = ini.getIntegerProperty(HMVGKVRchnung, "Rexemplare");
        String gkvTemplate = ini.getStringProperty(HMVGKVRchnung, "Rformular");
        boolean auchRechnung = ini.getBooleanProperty(HMVGKVRchnung, "Rauchdrucken");
        GKVFormularParameter gkv = new GKVFormularParameter(new FormularParameter(gkvTemplate, gkvPrinter, gkvAnzahl),
                auchRechnung);
        ;
        ;
        FormularParameter privat = new FormularParameter(taxEinstellungAusDrucker, taxPrinter, ANZAHL_TAXIERUNGSDRUCK);
        ;
        FormularParameter bg = new FormularParameter(taxEinstellungAusDrucker, taxPrinter, ANZAHL_TAXIERUNGSDRUCK);
        ;
        final String GEMEINSAME = "GemeinsameParameter";
        boolean direktAusdruck = ini.getBooleanProperty(GEMEINSAME, "InOfficeStarten");
        boolean askBefore302Mail = ini.getBooleanProperty(GEMEINSAME, "FragenVorEmail");
        GKVAbrechnungsParameter param = new GKVAbrechnungsParameter(taxierung, gkv, privat, bg, direktAusdruck,
                askBefore302Mail);

        return param;

    }

}
/**
 *
 * tf[0].setText(SystemConfig.hmAbrechnung.get("hmgkvformular"));
 * jcmb[0].setSelectedItem(SystemConfig.hmAbrechnung.get("hmgkvrechnungdrucker"));
 * jcmb[1].setSelectedItem(SystemConfig.hmAbrechnung.get("hmgkvtaxierdrucker"));
 * String wert = SystemConfig.hmAbrechnung.get("hmgkvrauchdrucken"); if
 * ("1".equals(wert)) { rbut[1].setSelected(true); } else {
 * rbut[0].setSelected(true); }
 * jcmb[2].setSelectedItem(SystemConfig.hmAbrechnung.get("hmgkvrexemplare"));
 *
 * tf[1].setText(SystemConfig.hmAbrechnung.get("hmpriformular"));
 * jcmb[3].setSelectedItem(SystemConfig.hmAbrechnung.get("hmpridrucker"));
 * jcmb[4].setSelectedItem(SystemConfig.hmAbrechnung.get("hmpriexemplare"));
 * tf[2].setText(SystemConfig.hmAbrechnung.get("hmbgeformular"));
 * jcmb[5].setSelectedItem(SystemConfig.hmAbrechnung.get("hmbgedrucker"));
 * jcmb[6].setSelectedItem(SystemConfig.hmAbrechnung.get("hmbgeexemplare"));
 * wert = SystemConfig.hmAbrechnung.get("hmallinoffice"); if ("1".equals(wert))
 * { rbut[3].setSelected(true); } else { rbut[2].setSelected(true); } wert =
 * SystemConfig.hmAbrechnung.get("hmaskforemail");
 * cbemail.setSelected("1".equals(wert)); if ("1".equals(wert)) {
 * cbemail.setSelected(true); } else { cbemail.setSelected(false); } wert =
 * SystemConfig.hmAbrechnung.get("hmusePrinterFromTemplate");
 * usePrinterFromTemplate = "1".equals(wert);
 *
 */
