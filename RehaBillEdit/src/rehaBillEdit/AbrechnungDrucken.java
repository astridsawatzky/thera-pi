package rehaBillEdit;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JOptionPane;

import com.sun.star.beans.XPropertySet;

import ag.ion.bion.officelayer.document.DocumentDescriptor;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.document.IDocumentDescriptor;
import ag.ion.bion.officelayer.document.IDocumentService;
import ag.ion.bion.officelayer.text.ITextDocument;
import ag.ion.bion.officelayer.text.ITextField;
import ag.ion.bion.officelayer.text.ITextFieldService;
import ag.ion.bion.officelayer.text.ITextTable;
import ag.ion.bion.officelayer.text.ITextTableCell;
import ag.ion.bion.officelayer.text.ITextTableCellProperties;
import ag.ion.bion.officelayer.text.TextException;
import office.OOTools;

class AbrechnungDrucken {
    private int aktuellePosition;
    private ITextTable textTable;
    private ITextTable textEndbetrag;
    private ITextDocument textDocument;
    private int positionen;

    private DecimalFormat dfx = new DecimalFormat("0.00");

    private BigDecimal rechnungsBetrag = new BigDecimal(Double.valueOf("0.00"));
    private BigDecimal rechnungsGesamt = new BigDecimal(Double.valueOf("0.00"));
    private BigDecimal rechnungsRezgeb = new BigDecimal(Double.valueOf("0.00"));
    private HashMap<String, String> hmAdresse = new HashMap<>();
    AbrechnungDrucken(/* AbrechnungGKV eltern, */String url) throws Exception {
        // this.eltern = eltern;
        starteDokument(url);
    }

    void setIKundRnr(String papierIk, String rnr, HashMap<String, String> hmap) {
        this.hmAdresse = hmap;
        try {
            setRechnungsBetrag();
            ersetzePlatzhalter();
            textDocument.getFrame()
                        .getXFrame()
                        .getContainerWindow()
                        .setVisible(true);
        } catch (TextException e) {
            JOptionPane.showMessageDialog(null, "Fehler im Rechnungsdruck, Fehler = TextException");
            e.printStackTrace();
        }
    }

    void setDaten(String nameVorname, String status, String rezNr, Vector<String> posvec,
            Vector<BigDecimal> anzahlvec, Vector<BigDecimal> anzahltagevec, Vector<BigDecimal> einzelpreis,
            Vector<BigDecimal> gesamtpreis, Vector<BigDecimal> zuzahlung, boolean mitPauschale) throws Exception {
        BigDecimal netto;
        positionen = posvec.size();
        int anz;
        String dummy;
        BigDecimal gesamtPreise = new BigDecimal("0.00");
        BigDecimal gesamtZuzahlung = new BigDecimal("0.00");
        BigDecimal gesamtNetto = new BigDecimal("0.00");

        textTable.addRow(positionen + 2);
        ITextTableCell[] tcells;

        tcells = textTable.getRow(aktuellePosition + 1)
                          .getCells();
        setPositionenCells(false, tcells);
        textTable.getCell(0, aktuellePosition + 1)
                 .getTextService()
                 .getText()
                 .setText(nameVorname);
        textTable.getCell(0, aktuellePosition + 2)
                 .getTextService()
                 .getText()
                 .setText(status + " - " + rezNr);
        int i;
        for (i = 0; i < positionen; i++) {
            textTable.getCell(1, aktuellePosition + i + 2)
                     .getTextService()
                     .getText()
                     .setText(posvec.get(i));
            anz = Double.valueOf(anzahlvec.get(i)
                                          .doubleValue())
                        .intValue();
            textTable.getCell(2, aktuellePosition + i + 2)
                     .getTextService()
                     .getText()
                     .setText(Integer.toString(anz));
            textTable.getCell(3, aktuellePosition + i + 2)
                     .getTextService()
                     .getText()
                     .setText(dfx.format(einzelpreis.get(i)
                                                    .doubleValue()));
            textTable.getCell(4, aktuellePosition + i + 2)
                     .getTextService()
                     .getText()
                     .setText(dfx.format(gesamtpreis.get(i)
                                                    .doubleValue()));
            textTable.getCell(5, aktuellePosition + i + 2)
                     .getTextService()
                     .getText()
                     .setText(dfx.format(zuzahlung.get(i)
                                                  .doubleValue()));
            netto = gesamtpreis.get(i)
                               .subtract(zuzahlung.get(i));
            textTable.getCell(6, aktuellePosition + i + 2)
                     .getTextService()
                     .getText()
                     .setText(dfx.format(netto.doubleValue()));
            tcells = textTable.getRow(aktuellePosition + i + 2)
                              .getCells();
            setPositionenCells(false, tcells);
            gesamtPreise = gesamtPreise.add(gesamtpreis.get(i));
            gesamtZuzahlung = gesamtZuzahlung.add(zuzahlung.get(i));
        }
        if (mitPauschale) {
            gesamtZuzahlung = gesamtZuzahlung.add(BigDecimal.valueOf(Double.valueOf("10.00")));
        }

        gesamtNetto = gesamtNetto.add(gesamtPreise.subtract(gesamtZuzahlung));
        rechnungsRezgeb = rechnungsRezgeb.add(gesamtZuzahlung);
        rechnungsGesamt = rechnungsGesamt.add(gesamtPreise);
        rechnungsBetrag = rechnungsBetrag.add(gesamtNetto);
        tcells = textTable.getRow(aktuellePosition + i + 2)
                          .getCells();
        setPositionenCells(true, tcells);
        dummy = dfx.format(gesamtPreise.doubleValue());
        textTable.getCell(4, aktuellePosition + i + 2)
                 .getTextService()
                 .getText()
                 .setText(dummy);
        dummy = dfx.format(gesamtZuzahlung.doubleValue());
        textTable.getCell(5, aktuellePosition + i + 2)
                 .getTextService()
                 .getText()
                 .setText(dummy);
        dummy = dfx.format(gesamtNetto.doubleValue());
        textTable.getCell(6, aktuellePosition + i + 2)
                 .getTextService()
                 .getText()
                 .setText(dummy);
        aktuellePosition += positionen + 2;
    }

    private void setRechnungsBetrag() throws TextException {
        textEndbetrag.getCell(2, 0)
                     .getTextService()
                     .getText()
                     .setText(dfx.format(rechnungsGesamt.doubleValue()) + " EUR");
        textEndbetrag.getCell(3, 1)
                     .getTextService()
                     .getText()
                     .setText(dfx.format(rechnungsRezgeb.doubleValue()) + " EUR");
        textEndbetrag.getCell(4, 2)
                     .getTextService()
                     .getText()
                     .setText(dfx.format(rechnungsBetrag.doubleValue()) + " EUR");
    }

    private void setPositionenCells(boolean italicAndBold, ITextTableCell[] tcells) throws Exception {
        ITextTableCellProperties props = null;
        for (int i2 = 0; i2 < tcells.length; i2++) {
            props = tcells[i2].getProperties();
            XPropertySet xprops = props.getXPropertySet();
            xprops.setPropertyValue("TopBorderDistance", 0);
            xprops.setPropertyValue("BottomBorderDistance", 0);
            // xprops.setPropertyValue("LeftBorderDistance", 0);
            // xprops.setPropertyValue("RightBorderDistance", 0);
            tcells[i2].getCharacterProperties()
                      .setFontSize(8.f);
            tcells[i2].getCharacterProperties()
                      .setFontUnderline(false);
            tcells[i2].getCharacterProperties()
                      .setFontItalic(italicAndBold);
            tcells[i2].getCharacterProperties()
                      .setFontBold(italicAndBold);
        }
    }

        private void starteDokument(String url) throws Exception {
        IDocumentService documentService;
        documentService = RehaBillEdit.officeapplication.getDocumentService();
        IDocumentDescriptor docdescript = new DocumentDescriptor();
        docdescript.setHidden(true);
        docdescript.setAsTemplate(true);
        IDocument document;
        document = documentService.loadDocument(url, docdescript);
        textDocument = (ITextDocument) document;
        OOTools.druckerSetzen(textDocument, RehaBillEdit.hmAbrechnung.get("hmgkvrechnungdrucker"));
        textTable = textDocument.getTextTableService()
                                .getTextTable("Tabelle1");
        textEndbetrag = textDocument.getTextTableService()
                                    .getTextTable("Tabelle2");
    }

    private void ersetzePlatzhalter() {
        ITextFieldService textFieldService = textDocument.getTextFieldService();
        ITextField[] placeholders = null;
        try {
            placeholders = textFieldService.getPlaceholderFields();
        } catch (TextException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < placeholders.length; i++) {
            if ("<gkv1>".equals(placeholders[i].getDisplayText()
                               .toLowerCase())) {
                placeholders[i].getTextRange()
                               .setText(hmAdresse.get("<gkv1>"));
            } else if ("<gkv2>".equals(placeholders[i].getDisplayText()
                                      .toLowerCase())) {
                placeholders[i].getTextRange()
                               .setText(hmAdresse.get("<gkv2>"));
            } else if ("<gkv3>".equals(placeholders[i].getDisplayText()
                                      .toLowerCase())) {
                placeholders[i].getTextRange()
                               .setText(hmAdresse.get("<gkv3>"));
            } else if ("<gkv4>".equals(placeholders[i].getDisplayText()
                                      .toLowerCase())) {
                placeholders[i].getTextRange()
                               .setText(hmAdresse.get("<gkv4>"));
            } else if ("<gkv5>".equals(placeholders[i].getDisplayText()
                                      .toLowerCase())) {
                placeholders[i].getTextRange()
                               .setText(hmAdresse.get("<gkv5>"));
            } else if ("<gkv6>".equals(placeholders[i].getDisplayText()
                                      .toLowerCase())) {
                placeholders[i].getTextRange()
                               .setText(hmAdresse.get("<gkv6>"));
            }
        }
    }
}
