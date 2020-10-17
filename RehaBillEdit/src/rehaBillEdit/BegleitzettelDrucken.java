package rehaBillEdit;

import java.util.HashMap;

import javax.swing.JOptionPane;

import ag.ion.bion.officelayer.document.DocumentDescriptor;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.document.IDocumentDescriptor;
import ag.ion.bion.officelayer.document.IDocumentService;
import ag.ion.bion.officelayer.text.ITextDocument;
import ag.ion.bion.officelayer.text.ITextField;
import ag.ion.bion.officelayer.text.ITextFieldService;
import ag.ion.bion.officelayer.text.TextException;
import office.OOTools;

class BegleitzettelDrucken {
    private ITextDocument textDocument;
    private int anzahlRezepte;
    private String kostentrIK;
    private String kostentrName;
    private String rechnungNummer;
    private HashMap<String, String> annahmeStelle;

    public BegleitzettelDrucken(int anzahlRezepte, String kostentrIK, String kostentrName,
            HashMap<String, String> annahme, String rnr, String url) throws Exception {
        this.anzahlRezepte = anzahlRezepte;
        this.annahmeStelle = annahme;
        this.kostentrIK = kostentrIK;
        this.kostentrName = kostentrName;
        this.rechnungNummer = rnr;

        try {
            System.out.println("URL = " + url);
            starteDokument(url);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Fehler im Modul starteDokument() Begleitzettl\n" + ex.getMessage());
        }
        Thread.sleep(100);

        try {
            ersetzePlatzhalter();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    "Fehler im Modul ersetzePlatzhalter() Begleitzettl\n" + ex.getMessage());
        }

        textDocument.getFrame()
                    .getXFrame()
                    .getContainerWindow()
                    .setVisible(true);
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
                               .setText(annahmeStelle.get("<gkv1>"));
            } else if ("<gkv2>".equals(placeholders[i].getDisplayText()
                                      .toLowerCase())) {
                placeholders[i].getTextRange()
                               .setText(annahmeStelle.get("<gkv2>"));
            } else if ("<gkv3>".equals(placeholders[i].getDisplayText()
                                      .toLowerCase())) {
                placeholders[i].getTextRange()
                               .setText(annahmeStelle.get("<gkv3>"));
            } else if ("<gkv4>".equals(placeholders[i].getDisplayText()
                                      .toLowerCase())) {
                placeholders[i].getTextRange()
                               .setText(annahmeStelle.get("<gkv4>"));
            } else if ("<gkv5>".equals(placeholders[i].getDisplayText()
                                      .toLowerCase())) {
                placeholders[i].getTextRange()
                               .setText(this.kostentrIK);
            } else if ("<gkv6>".equals(placeholders[i].getDisplayText()
                                      .toLowerCase())) {
                placeholders[i].getTextRange()
                               .setText(this.kostentrName);
            } else if ("<gkv7>".equals(placeholders[i].getDisplayText()
                                      .toLowerCase())) {
                placeholders[i].getTextRange()
                               .setText(RehaBillEdit.hmFirmenDaten.get("Firma1") + " "
                                       + RehaBillEdit.hmFirmenDaten.get("Firma2") + "\n"
                                       + RehaBillEdit.hmFirmenDaten.get("Strasse") + "\n"
                                       + RehaBillEdit.hmFirmenDaten.get("Plz") + " "
                                       + RehaBillEdit.hmFirmenDaten.get("Ort"));
            } else if ("<gkv8>".equals(placeholders[i].getDisplayText()
                                      .toLowerCase())) {
                placeholders[i].getTextRange()
                               .setText(RehaBillEdit.aktIK);
            } else if ("<gkv9>".equals(placeholders[i].getDisplayText()
                                      .toLowerCase())) {
                placeholders[i].getTextRange()
                               .setText(this.rechnungNummer);
            } else if ("<gkv10>".equals(placeholders[i].getDisplayText()
                                      .toLowerCase())) {
                placeholders[i].getTextRange()
                               .setText(Integer.toString(this.anzahlRezepte));
            } else if ("<gkv11>".equals(placeholders[i].getDisplayText()
                                      .toLowerCase())) {
                if (this.anzahlRezepte == 1) {
                    placeholders[i].getTextRange()
                                   .setText(this.anzahlRezepte + " Originalrezept");
                } else {
                    placeholders[i].getTextRange()
                                   .setText(this.anzahlRezepte + " Originalrezepte");
                }
            } else if ("<gkv12>".equals(placeholders[i].getDisplayText()
                                      .toLowerCase())) {
                placeholders[i].getTextRange()
                               .setText(annahmeStelle.get("<gkv12>"));
            }
        }
    }
}
