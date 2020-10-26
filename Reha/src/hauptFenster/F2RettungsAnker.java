package hauptFenster;

import java.awt.Color;

import javax.swing.SwingWorker;

import com.sun.star.beans.PropertyVetoException;
import com.sun.star.beans.UnknownPropertyException;
import com.sun.star.container.NoSuchElementException;
import com.sun.star.lang.IllegalArgumentException;
import com.sun.star.lang.WrappedTargetException;

import ag.ion.bion.officelayer.application.OfficeApplicationException;
import ag.ion.bion.officelayer.document.DocumentDescriptor;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.document.IDocumentService;
import ag.ion.bion.officelayer.internal.text.TextTableColumn;
import ag.ion.bion.officelayer.text.ITextDocument;
import ag.ion.bion.officelayer.text.ITextTable;
import ag.ion.bion.officelayer.text.TextException;
import ag.ion.noa.NOAException;
import office.OOService;
import office.OOTools;

public class F2RettungsAnker {
    public F2RettungsAnker() throws TextException {
        IDocumentService documentService = null;
        try {
            if (!new OOService().getOfficeapplication().isActive()) {
                Reha.starteOfficeApplication();
            }
            documentService = new OOService().getOfficeapplication().getDocumentService();
        } catch (OfficeApplicationException e) {
            e.printStackTrace();
        }
        IDocument document = null;
        try {
            DocumentDescriptor docdecript = new DocumentDescriptor();
            docdecript.setHidden(true);
            document = documentService.constructNewDocument(IDocument.WRITER, docdecript);

            // document = documentService.constructNewDocument(IDocument.WRITER,
            // DocumentDescriptor.DEFAULT);
        } catch (NOAException e) {
            e.printStackTrace();
        }

        ITextDocument textDocument = (ITextDocument) document;
        /*
         * IParagraph paragraph =
         * textDocument.getTextService().getTextContentService().constructNewParagraph()
         * ;
         */
        try {
            OOTools.setzePapierFormat(textDocument, 21000, 29700);
            OOTools.setzeRaender(textDocument, 500, 500, 500, 500);
        } catch (NoSuchElementException e1) {

            e1.printStackTrace();
        } catch (WrappedTargetException e1) {

            e1.printStackTrace();
        } catch (UnknownPropertyException e1) {

            e1.printStackTrace();
        } catch (PropertyVetoException e1) {

            e1.printStackTrace();
        } catch (IllegalArgumentException e1) {

            e1.printStackTrace();
        }
        /*
         * Saichtext basteln und einsetzen
         */
        ITextTable textTable = null;
        try {
            textTable = textDocument.getTextTableService()
                                    .constructTextTable(Reha.terminLookup.size() + 1, 5);

        } catch (TextException e) {
            e.printStackTrace();
        }

        try {

            textDocument.getTextService()
                        .getTextContentService()
                        .insertTextContent(textTable);

        } catch (TextException e) {
            e.printStackTrace();
        }
        try {
            textTable.getCell(0, 0)
                     .getTextService()
                     .getText()
                     .setText("lfNr.");
            textTable.getCell(1, 0)
                     .getTextService()
                     .getText()
                     .setText("durchgeführt von/am/um");
            textTable.getCell(2, 0)
                     .getTextService()
                     .getText()
                     .setText("was/wo/wann");
            textTable.getCell(3, 0)
                     .getTextService()
                     .getText()
                     .setText("Termindaten vor Aktion");
            textTable.getCell(4, 0)
                     .getTextService()
                     .getText()
                     .setText("Termindaten nach Aktion");
            for (int i = 1; i < Reha.terminLookup.size() + 1; i++) {
                textTable.getCell(0, i)
                         .getTextService()
                         .getText()
                         .setText(Integer.toString(i));
                textTable.getCell(1, i)
                         .getTextService()
                         .getText()
                         .setText(Reha.terminLookup.get(i - 1)
                                                   .get(0)
                                                   .get(4)
                                 + "\r" + Reha.terminLookup.get(i - 1)
                                                           .get(0)
                                                           .get(3)
                                 + " - " + Reha.terminLookup.get(i - 1)
                                                            .get(0)
                                                            .get(2));
                textTable.getCell(2, i)
                         .getTextService()
                         .getText()
                         .setText("Funktion = " + Reha.terminLookup.get(i - 1)
                                                                   .get(0)
                                                                   .get(5)
                                 + "\r" + "Spalte von = " + Reha.terminLookup.get(i - 1)
                                                                             .get(0)
                                                                             .get(0)
                                 + "\r" + "Kalendertag = " + Reha.terminLookup.get(i - 1)
                                                                              .get(0)
                                                                              .get(1));
                textTable.getCell(3, i)
                         .getTextService()
                         .getText()
                         .setText(" Name: " + Reha.terminLookup.get(i - 1)
                                                               .get(1)
                                                               .get(0)
                                 + "\r" + "RezNr: " + Reha.terminLookup.get(i - 1)
                                                                       .get(1)
                                                                       .get(1)
                                 + "\r" + "Start: " + Reha.terminLookup.get(i - 1)
                                                                       .get(1)
                                                                       .get(2)
                                 + "\r" + " Ende: " + Reha.terminLookup.get(i - 1)
                                                                       .get(1)
                                                                       .get(4)
                                 + "\r" + "Dauer: " + Reha.terminLookup.get(i - 1)
                                                                       .get(1)
                                                                       .get(3));
                textTable.getCell(4, i)
                         .getTextService()
                         .getText()
                         .setText(" Name: " + Reha.terminLookup.get(i - 1)
                                                               .get(2)
                                                               .get(0)
                                 + "\r" + "RezNr: " + Reha.terminLookup.get(i - 1)
                                                                       .get(2)
                                                                       .get(1)
                                 + "\r" + "Start: " + Reha.terminLookup.get(i - 1)
                                                                       .get(2)
                                                                       .get(2)
                                 + "\r" + " Ende: " + Reha.terminLookup.get(i - 1)
                                                                       .get(2)
                                                                       .get(4)
                                 + "\r" + "Dauer: " + Reha.terminLookup.get(i - 1)
                                                                       .get(2)
                                                                       .get(3));

            }
            // textTable.getCell(4,1).getTextService().getText().setText(Reha.terminLookup.toString());

        } catch (TextException exception) {
            exception.printStackTrace();
        }

        TextTableColumn[] tbc = (TextTableColumn[]) textTable.getColumns();
        /*
         * int width = 0; for(int i = 0; i < tbc.length;i++){ width +=
         * (int)tbc[i].getWidth(); } System.out.println(width);
         */
        tbc[0].setWidth((short) 500);
        tbc[1].setWidth((short) 2375);
        tbc[2].setWidth((short) 2375);
        tbc[3].setWidth((short) 2375);
        tbc[4].setWidth((short) 2375);

        int rows = textTable.getRowCount();
        int rot = Color.RED.getRGB();
        int blau = Color.BLUE.getRGB();
        int magenta = Color.MAGENTA.getRGB();
        int irgendwas = new Color(231, 120, 23).getRGB();
        for (int i = 0; i < rows; i++) {
            textTable.getCell(0, i)
                     .getCharacterProperties()
                     .setFontName("Courier New");
            textTable.getCell(0, i)
                     .getCharacterProperties()
                     .setFontSize(9.f);
            textTable.getCell(0, i)
                     .getCharacterProperties()
                     .setFontBold((i == 0 ? true : false));
            textTable.getCell(0, i)
                     .getCharacterProperties()
                     .setFontColor(magenta);
            textTable.getCell(1, i)
                     .getCharacterProperties()
                     .setFontName("Courier New");
            textTable.getCell(1, i)
                     .getCharacterProperties()
                     .setFontSize(9.f);
            textTable.getCell(1, i)
                     .getCharacterProperties()
                     .setFontColor(blau);
            textTable.getCell(1, i)
                     .getCharacterProperties()
                     .setFontBold((i == 0 ? true : false));
            textTable.getCell(2, i)
                     .getCharacterProperties()
                     .setFontName("Courier New");
            textTable.getCell(2, i)
                     .getCharacterProperties()
                     .setFontSize(9.f);
            textTable.getCell(2, i)
                     .getCharacterProperties()
                     .setFontBold((i == 0 ? true : false));
            textTable.getCell(3, i)
                     .getCharacterProperties()
                     .setFontName("Courier New");
            textTable.getCell(3, i)
                     .getCharacterProperties()
                     .setFontSize(9.f);
            textTable.getCell(3, i)
                     .getCharacterProperties()
                     .setFontBold((i == 0 ? true : false));
            textTable.getCell(3, i)
                     .getCharacterProperties()
                     .setFontColor(irgendwas);
            textTable.getCell(4, i)
                     .getCharacterProperties()
                     .setFontName("Courier New");
            textTable.getCell(4, i)
                     .getCharacterProperties()
                     .setFontSize(9.f);
            textTable.getCell(4, i)
                     .getCharacterProperties()
                     .setFontBold((i == 0 ? true : false));
            textTable.getCell(4, i)
                     .getCharacterProperties()
                     .setFontColor(rot);
        }

        final IDocument xdoc = document;
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                xdoc.getFrame()
                    .getXFrame()
                    .getContainerWindow()
                    .setVisible(true);
                return null;
            }

        }.execute();
    }

}
