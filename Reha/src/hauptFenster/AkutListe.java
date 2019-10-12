package hauptFenster;

import java.awt.Color;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.SwingWorker;

import com.sun.star.beans.PropertyVetoException;
import com.sun.star.beans.UnknownPropertyException;
import com.sun.star.container.NoSuchElementException;
import com.sun.star.lang.IllegalArgumentException;
import com.sun.star.lang.WrappedTargetException;

import CommonTools.DatFunk;
import CommonTools.SqlInfo;
import ag.ion.bion.officelayer.document.DocumentDescriptor;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.document.IDocumentService;
import ag.ion.bion.officelayer.internal.text.TextTableColumn;
import ag.ion.bion.officelayer.text.ITextDocument;
import ag.ion.bion.officelayer.text.ITextTable;
import ag.ion.bion.officelayer.text.TextException;
import ag.ion.noa.NOAException;
import oOorgTools.OOTools;

/*******************************************************************************************************/
public class AkutListe {
    Vector<Vector<String>> vec = null;
    String felder = "therapeut,n_name,v_name,telefonp,telefong,telefonm,emaila,termine1,termine2,akutdat,akutbis";

    public AkutListe(IDocumentService documentService2) throws TextException {
        vec = SqlInfo.holeSaetze("pat5", felder, "akutpat='T' order by therapeut", Arrays.asList(new String[] {}));
        int lang;
        if ((lang = vec.size()) > 0) {
            IDocumentService documentService = documentService2;

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
            } catch (NoSuchElementException | WrappedTargetException | UnknownPropertyException | PropertyVetoException
                    | IllegalArgumentException e1) {

                e1.printStackTrace();
            }
            /*
             * Saichtext basteln und einsetzen
             */
            ITextTable textTable = null;
            try {
                textTable = textDocument.getTextTableService()
                                        .constructTextTable(lang + 1, 5);

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
                         .setText("Behandler");
                textTable.getCell(1, 0)
                         .getTextService()
                         .getText()
                         .setText("Patient");
                textTable.getCell(2, 0)
                         .getTextService()
                         .getText()
                         .setText("Kontakt");
                textTable.getCell(3, 0)
                         .getTextService()
                         .getText()
                         .setText("von / bis");
                textTable.getCell(4, 0)
                         .getTextService()
                         .getText()
                         .setText("mögliche Termine");

            } catch (TextException exception) {
                exception.printStackTrace();
            }
            String result = "";
            String test = "";
            for (int i = 0; i < lang; i++) {
                try {
                    /*
                     * String felder = "akutbeh," + "n_name,v_name," +
                     * "telefonp,telefong,telefonm,emaila," + "termine1,"termine2"; ,akutdat,akutbis
                     */
                    result = (String) ((Vector<?>) vec.get(i)).get(0);
                    textTable.getCell(0, i + 1)
                             .getTextService()
                             .getText()
                             .setText(result);

                    result = (String) ((Vector<?>) vec.get(i)).get(1) + "\r" + (String) ((Vector<?>) vec.get(i)).get(2);
                    textTable.getCell(1, i + 1)
                             .getTextService()
                             .getText()
                             .setText(result);

                    result = "";
                    test = (String) ((Vector<?>) vec.get(i)).get(3);
                    result = result + (test.trim()
                                           .equals("") ? "" : "p:" + test);
                    test = (String) ((Vector<?>) vec.get(i)).get(4);
                    result = result + (test.trim()
                                           .equals("") ? "" : "\r" + "g:" + test);
                    test = (String) ((Vector<?>) vec.get(i)).get(5);
                    result = result + (test.trim()
                                           .equals("") ? "" : "\r" + "m:" + test);
                    test = (String) ((Vector<?>) vec.get(i)).get(6);
                    result = result + (test.trim()
                                           .equals("") ? "" : "\r" + "e:" + test);
                    textTable.getCell(2, i + 1)
                             .getTextService()
                             .getText()
                             .setText(result);

                    result = "";
                    test = (String) ((Vector<?>) vec.get(i)).get(9);
                    test = (test.trim()
                                .equals("") ? "ab: " : "ab:  " + DatFunk.sDatInDeutsch(test));
                    result = result + test;
                    test = (String) ((Vector<?>) vec.get(i)).get(10);
                    test = (test.trim()
                                .equals("") ? "\rbis: " : "\rbis: " + DatFunk.sDatInDeutsch(test));
                    result = result + test;
                    textTable.getCell(3, i + 1)
                             .getTextService()
                             .getText()
                             .setText(result);

                    result = "";
                    test = (String) ((Vector<?>) vec.get(i)).get(7);
                    result = result + (test.trim()
                                           .equals("") ? "" : test);
                    test = (String) ((Vector<?>) vec.get(i)).get(8);
                    result = result + (test.trim()
                                           .equals("") ? "" : "\r" + test);
                    textTable.getCell(4, i + 1)
                             .getTextService()
                             .getText()
                             .setText(result);

                } catch (TextException exception) {
                    exception.printStackTrace();
                }
            }
            TextTableColumn[] tbc = (TextTableColumn[]) textTable.getColumns();
            tbc[0].setWidth((short) 1500);
            tbc[1].setWidth((short) 1500);
            tbc[2].setWidth((short) 1700);
            tbc[3].setWidth((short) 1500);
            // tbc[4].setWidth((short) 7920);
            // System.out.println("Es gibt insgesamt "+tbc.length+" Column");

            // int cols = textTable.getColumnCount();
            int rows = textTable.getRowCount();
            int rot = Color.RED.getRGB();
            int blau = Color.BLUE.getRGB();
            int magenta = Color.MAGENTA.getRGB();
            for (int i = 0; i < rows; i++) {
                formatCharacter(textTable, 0, i, magenta);
                formatCharacter(textTable, 1, i, blau);
                formatCharacter(textTable, 2, i);
                formatCharacter(textTable, 3, i);
                formatCharacter(textTable, 4, i, rot);
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
            vec.clear();
            vec = null;

        }
    }

    private void formatCharacter(ITextTable textTable, int column, int row) throws TextException {
        textTable.getCell(column, row)
                 .getCharacterProperties()
                 .setFontName("Courier New");
        textTable.getCell(column, row)
                 .getCharacterProperties()
                 .setFontSize(10.f);
        textTable.getCell(column, row)
                 .getCharacterProperties()
                 .setFontBold((row == 0 ? true : false));
    }

    private void formatCharacter(ITextTable textTable, int column, int row, int color) throws TextException {
        formatCharacter(textTable, column, row);
        textTable.getCell(column, row)
                 .getCharacterProperties()
                 .setFontColor(color);
    }
}
