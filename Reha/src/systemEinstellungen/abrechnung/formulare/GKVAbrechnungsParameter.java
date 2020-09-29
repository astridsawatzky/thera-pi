package systemEinstellungen.abrechnung.formulare;

import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.ServiceUIFactory;
import javax.print.attribute.Attribute;
import javax.print.attribute.AttributeSet;
import javax.print.attribute.PrintServiceAttribute;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.event.PrintServiceAttributeListener;

public class GKVAbrechnungsParameter {

    final FormularParameter taxierung;
    final GKVFormularParameter gkv;
    final FormularParameter privat;
    final FormularParameter bg;
    public FormularParameter rgr ;
    final boolean direktAusdruck;
    final boolean askBefore302Mail;

    public GKVAbrechnungsParameter(FormularParameter taxierung, GKVFormularParameter gkv, FormularParameter privat,
            FormularParameter bg, boolean direktAusdruck, boolean askBefore302Mail) {
        super();
        this.taxierung = taxierung;
        this.gkv =  gkv;
        this.privat = privat;
        this.bg = bg;
        this.direktAusdruck = direktAusdruck;
        this.askBefore302Mail = askBefore302Mail;
    }

}

class GKVFormularParameter extends FormularParameter {
    final boolean begleitzettelOnly;

    public GKVFormularParameter(FormularParameter params, boolean begleitZettelonly) {
        super(params.printerEinstellungsAusVorlage, params.template, params.printer, params.numberOfPrintOuts);
        begleitzettelOnly = begleitZettelonly;
    }

}

class FormularParameter {

    final boolean printerEinstellungsAusVorlage;
    final String template;
    final PrintService printer;
    final int numberOfPrintOuts;

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

    public String getTemplate() {
        return template;
    }

    public PrintService getPrinter() {
        return printer;
    }

    public int getNumberOfPrintOuts() {
        return numberOfPrintOuts;
    }
}
    class UnknownPrintService implements PrintService {

        public UnknownPrintService(String name) {
            this.name = name;

        }
        final String name;

        @Override
        public String getName() {
            return name;
        }

        @Override
        public DocPrintJob createPrintJob() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addPrintServiceAttributeListener(PrintServiceAttributeListener listener) {

        }

        @Override
        public void removePrintServiceAttributeListener(PrintServiceAttributeListener listener) {

        }

        @Override
        public PrintServiceAttributeSet getAttributes() {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T extends PrintServiceAttribute> T getAttribute(Class<T> category) {
            throw new UnsupportedOperationException();
        }

        @Override
        public DocFlavor[] getSupportedDocFlavors() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isDocFlavorSupported(DocFlavor flavor) {
            return false;
        }

        @Override
        public Class<?>[] getSupportedAttributeCategories() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isAttributeCategorySupported(Class<? extends Attribute> category) {
            return false;
        }

        @Override
        public Object getDefaultAttributeValue(Class<? extends Attribute> category) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object getSupportedAttributeValues(Class<? extends Attribute> category, DocFlavor flavor,
                AttributeSet attributes) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isAttributeValueSupported(Attribute attrval, DocFlavor flavor, AttributeSet attributes) {
            return false;
        }

        @Override
        public AttributeSet getUnsupportedAttributes(DocFlavor flavor, AttributeSet attributes) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ServiceUIFactory getServiceUIFactory() {
            throw new UnsupportedOperationException();
        }



}
