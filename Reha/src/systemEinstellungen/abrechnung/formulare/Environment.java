package systemEinstellungen.abrechnung.formulare;

import java.util.HashMap;
import java.util.Map;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;

public class Environment {


    static Map<String, PrintService> printservices() {
        PrintService[]   printservices=  PrintServiceLookup.lookupPrintServices(null, null);

        Map<String, PrintService> printers = new HashMap<>();
        for (PrintService printService : printservices) {
            printers.put(printService.getName(), printService);
        }
        return java.util.Collections.unmodifiableMap(printers);
    }
}
