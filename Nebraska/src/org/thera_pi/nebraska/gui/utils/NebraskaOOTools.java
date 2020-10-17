package org.thera_pi.nebraska.gui.utils;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.thera_pi.nebraska.gui.NebraskaMain;
import org.thera_pi.nebraska.gui.NebraskaRequestDlg;

import com.sun.star.frame.XController;
import com.sun.star.text.XTextViewCursorSupplier;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.view.XLineCursor;

import CommonTools.DatFunk;
import CommonTools.Monitor;
import ag.ion.bion.officelayer.application.OfficeApplicationException;
import ag.ion.bion.officelayer.document.DocumentDescriptor;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.document.IDocumentDescriptor;
import ag.ion.bion.officelayer.document.IDocumentService;
import ag.ion.bion.officelayer.text.ITextCursor;
import ag.ion.bion.officelayer.text.ITextDocument;
import ag.ion.bion.officelayer.text.ITextField;
import ag.ion.bion.officelayer.text.ITextFieldService;
import ag.ion.bion.officelayer.text.IViewCursor;
import ag.ion.bion.officelayer.text.TextException;
import ag.ion.noa.NOAException;
import ag.ion.noa.printing.IPrinter;

public class NebraskaOOTools {
    public static void loescheLeerenPlatzhalter(ITextDocument textDocument, ITextField placeholders) {
        IViewCursor viewCursor = textDocument.getViewCursorService()
                                             .getViewCursor();
        viewCursor.goToRange(placeholders.getTextRange(), false);
        XController xController = textDocument.getXTextDocument()
                                              .getCurrentController();
        XTextViewCursorSupplier xTextViewCursorSupplier = UnoRuntime.queryInterface(XTextViewCursorSupplier.class,
                xController);
        XLineCursor xLineCursor = UnoRuntime.queryInterface(XLineCursor.class, xTextViewCursorSupplier.getViewCursor());
        xLineCursor.gotoStartOfLine(false);
        xLineCursor.gotoEndOfLine(true);
        ITextCursor textCursor = viewCursor.getTextCursorFromStart();
        textCursor.goLeft((short) 1, false);
        textCursor.gotoRange(viewCursor.getTextCursorFromEnd()
                                       .getEnd(),
                true);
        textCursor.setString("");
    }

    public static void starteStandardFormular(String url, String drucker, Monitor monitor) throws OfficeApplicationException, NOAException, TextException  {
        monitor.statusChange(Monitor.START);

        System.out.println("Starte Datei -> " + url);
        IDocumentService documentService = NebraskaMain.officeapplication.getDocumentService();

        IDocumentDescriptor docdescript = new DocumentDescriptor();
        docdescript.setHidden(true);
        docdescript.setAsTemplate(true);
        IDocument document = null;
        document = documentService.loadDocument(url, docdescript);
        ITextDocument textDocument = (ITextDocument) document;
        /**********************/
        if (drucker != null) {
            String druckerName = null;
            druckerName = textDocument.getPrintService()
                                      .getActivePrinter()
                                      .getName();
            IPrinter iprint = null;
            if (!druckerName.equals(drucker)) {
                iprint = textDocument.getPrintService()
                                     .createPrinter(drucker);
                textDocument.getPrintService()
                            .setActivePrinter(iprint);
            }
        }
        /**********************/
        ITextFieldService textFieldService = textDocument.getTextFieldService();
        ITextField[] placeholders = null;

        placeholders = textFieldService.getPlaceholderFields();
        String placeholderDisplayText = "";
        for (int i = 0; i < placeholders.length; i++) {
            boolean schonersetzt = false;
            placeholderDisplayText = placeholders[i].getDisplayText()
                                                    .toLowerCase();
            System.out.println(placeholderDisplayText);

            /*****************/
            Set<Map.Entry<String, String>> entries = NebraskaRequestDlg.hmZertifikat.entrySet();
            Iterator<Entry<String, String>> it = entries.iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> entry = it.next();
                if (entry.getKey()
                         .toLowerCase()
                         .equals(placeholderDisplayText)) {
                    if (entry.getValue()
                             .trim()
                             .equals("")) {
                        placeholders[i].getTextRange()
                                       .setText("\b");
                    } else {
                        placeholders[i].getTextRange()
                                       .setText((entry.getValue()));
                    }
                    schonersetzt = true;
                    break;
                }
            }
            /*****************/
            if (!schonersetzt) {
                loescheLeerenPlatzhalter(textDocument, placeholders[i]);
            }
            /*****************/
        }

        monitor.statusChange(Monitor.STOP);

        IViewCursor viewCursor = textDocument.getViewCursorService()
                                             .getViewCursor();
        viewCursor.getPageCursor()
                  .jumpToFirstPage();
        textDocument.getFrame()
                    .getXFrame()
                    .getContainerWindow()
                    .setVisible(true);
        textDocument.getFrame()
                    .setFocus();
        try {
            textDocument.getPersistenceService()
                        .store(url.replace(".ott", "-" + DatFunk.sHeute() + ".odt"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

}
