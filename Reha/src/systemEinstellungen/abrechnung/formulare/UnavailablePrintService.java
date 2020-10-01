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

class UnavailablePrintService implements PrintService {

        public UnavailablePrintService(String name) {
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