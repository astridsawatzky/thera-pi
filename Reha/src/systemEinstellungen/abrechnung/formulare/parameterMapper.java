package systemEinstellungen.abrechnung.formulare;

import java.util.Map;

import javax.print.PrintService;

import CommonTools.ini.Settings;

public class parameterMapper {
    private static final int ANZAHL_TAXIERUNGSDRUCK = 1;

    GKVAbrechnungsParameter readSettings(Settings ini) {
        Map<String, PrintService> printers = Environment.printservices();
        final String HMVGKVRchnung = "HMGKVRechnung";

        FormularParameter taxierung = readTaxierung(ini, printers, HMVGKVRchnung);

        GKVFormularParameter gkv = readGkvSettings(ini, printers, HMVGKVRchnung);

        FormularParameter privat = readdprivatSetting(ini, printers);

        FormularParameter bg = readBGESettings(ini, printers);

        String rgrRechnungSection= "RGRRechnung";
        String rgrPrinterName = ini.getStringProperty(rgrRechnungSection, "Rdrucker");
        FormularParameter rgr = new FormularParameter(printers.getOrDefault(rgrPrinterName, new UnavailablePrintService(rgrPrinterName)), 1);

        final String GEMEINSAME = "GemeinsameParameter";

        boolean direktAusdruck = ini.getBooleanProperty(GEMEINSAME, "InOfficeStarten");
        boolean askBefore302Mail = ini.getBooleanProperty(GEMEINSAME, "FragenVorEmail");

        return new GKVAbrechnungsParameter(taxierung, gkv, privat, bg,rgr, direktAusdruck, askBefore302Mail);
    }

    private FormularParameter readBGESettings(Settings ini, Map<String, PrintService> printers) {
        final String bgRechnungSection = "HMBGERechnung";
        String bgFormular = ini.getStringProperty(bgRechnungSection, "Bformular");
        String bgPrinterName = ini.getStringProperty(bgRechnungSection, "Bdrucker");
        PrintService bgPrinter = printers.getOrDefault(bgPrinterName, new UnavailablePrintService(bgPrinterName));
        int bgExemplare = ini.getIntegerProperty(bgRechnungSection, "Bexemplare");

        FormularParameter bg = new FormularParameter(bgFormular, bgPrinter, bgExemplare);
        return bg;
    }

    private FormularParameter readdprivatSetting(Settings ini, Map<String, PrintService> printers) {
        final String privatRechnungSection = "HMPRIRechnung";
        String privatFormular = ini.getStringProperty(privatRechnungSection, "Pformular");
        String privatPrinterName = ini.getStringProperty(privatRechnungSection, "Pdrucker");
        PrintService privatPrinter = printers.getOrDefault(privatPrinterName,
                new UnavailablePrintService(privatPrinterName));
        int privatExemplare = ini.getIntegerProperty(privatRechnungSection, "Pexemplare");
        FormularParameter privat = new FormularParameter(privatFormular, privatPrinter, privatExemplare);
        return privat;
    }

    private GKVFormularParameter readGkvSettings(Settings ini, Map<String, PrintService> printers,
            final String section) {
        String gkvdruckerName = ini.getStringProperty(section, "Rdrucker");
        PrintService gkvPrinter = printers.getOrDefault(gkvdruckerName, new UnavailablePrintService(gkvdruckerName));

        int gkvAnzahl = ini.getIntegerProperty(section, "Rexemplare");
        String gkvTemplate = ini.getStringProperty(section, "Rformular");
        boolean auchRechnung = ini.getBooleanProperty(section, "Rauchdrucken");
        GKVFormularParameter gkv = new GKVFormularParameter(new FormularParameter(gkvTemplate, gkvPrinter, gkvAnzahl),
                auchRechnung);
        return gkv;
    }

    private FormularParameter readTaxierung(Settings ini, Map<String, PrintService> printers, final String section) {
        boolean taxEinstellungAusDrucker = ini.getBooleanProperty(section, "usePrinterFromTemplate");

        String tdruckerName = ini.getStringProperty(section, "Tdrucker");
        PrintService taxPrinter = printers.getOrDefault(tdruckerName, new UnavailablePrintService(tdruckerName));

        FormularParameter taxierung = new FormularParameter(taxEinstellungAusDrucker, taxPrinter,
                ANZAHL_TAXIERUNGSDRUCK);
        return taxierung;
    }
}
