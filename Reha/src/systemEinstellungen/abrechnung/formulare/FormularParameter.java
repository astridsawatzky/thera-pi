package systemEinstellungen.abrechnung.formulare;

import javax.print.PrintService;

class FormularParameter {
    public final boolean printerEinstellungsAusVorlage;
    public final String template;
    public final PrintService printer;
    public final int numberOfPrintOuts;

    public FormularParameter(String template, PrintService printer, int numberOfPrintOuts) {
        this(false, template, printer, numberOfPrintOuts);
    }

    public FormularParameter(PrintService printer, int numberOfPrintOuts) {
        this(false, "", printer, numberOfPrintOuts);
    }

    public FormularParameter(boolean printerEinstellungsAusVorlage, PrintService printer, int numberOfPrintOuts) {
        this(printerEinstellungsAusVorlage, "", printer, numberOfPrintOuts);
    }

    public FormularParameter(boolean printerEinstellungsAusVorlage, String template, PrintService printer,
            int numberOfPrintOuts) {
        this.printerEinstellungsAusVorlage = printerEinstellungsAusVorlage;
        this.template = template;
        this.printer = printer;
        this.numberOfPrintOuts = numberOfPrintOuts;
    }

    public boolean isPrinterEinstellungsAusVorlage() {
        return printerEinstellungsAusVorlage;
    }

    public String template() {
        return template;
    }

    public PrintService printer() {
        return printer;
    }

    public int numberOfPrintOuts() {
        return numberOfPrintOuts;
    }
}