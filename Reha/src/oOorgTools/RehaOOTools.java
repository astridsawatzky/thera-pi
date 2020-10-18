package oOorgTools;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import CommonTools.Monitor;
import ag.ion.bion.officelayer.application.OfficeApplicationException;
import ag.ion.bion.officelayer.document.DocumentDescriptor;
import ag.ion.bion.officelayer.document.DocumentException;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.document.IDocumentDescriptor;
import ag.ion.bion.officelayer.document.IDocumentService;
import ag.ion.bion.officelayer.text.ITextDocument;
import ag.ion.bion.officelayer.text.ITextField;
import ag.ion.bion.officelayer.text.ITextFieldService;
import ag.ion.bion.officelayer.text.IViewCursor;
import ag.ion.bion.officelayer.text.TextException;
import ag.ion.noa.NOAException;
import ag.ion.noa.printing.IPrinter;
import gui.Cursors;
import hauptFenster.Reha;
import office.OOService;
import office.OOTools;
import systemEinstellungen.SystemConfig;

public class RehaOOTools {
    public static void starteStandardFormular(String url, String drucker, Monitor monitor) {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                starts(monitor);
                return null;
            }

        }.execute();
        IDocumentService documentService = null;
        ITextDocument textDocument;

        OOService ooService = new OOService();
        if (!ooService.getOfficeapplication()
                      .isActive()) {
            try {
                ooService.start();
            } catch (FileNotFoundException | OfficeApplicationException e) {
                e.printStackTrace();
            }
        }
        try {
            documentService = ooService.getOfficeapplication()
                                       .getDocumentService();
        } catch (OfficeApplicationException e) {
            e.printStackTrace();
        }

        IDocumentDescriptor docdescript = new DocumentDescriptor();
        docdescript.setHidden(true);
        docdescript.setAsTemplate(true);
        IDocument document = null;

        try {
            document = documentService.loadDocument(url, docdescript);
        } catch (NOAException e) {
            e.printStackTrace();
        }
        textDocument = (ITextDocument) document;

        if (drucker != null) {
            String druckerName = null;
            try {
                druckerName = textDocument.getPrintService()
                                          .getActivePrinter()
                                          .getName();
            } catch (NOAException e) {
                e.printStackTrace();
            }
            IPrinter iprint = null;
            if (!druckerName.equals(drucker)) {
                try {
                    iprint = textDocument.getPrintService()
                                         .createPrinter(drucker);
                } catch (NOAException e) {
                    e.printStackTrace();
                }
                try {
                    textDocument.getPrintService()
                                .setActivePrinter(iprint);
                } catch (NOAException e) {
                    e.printStackTrace();
                }
            }
        }
        ITextFieldService textFieldService = textDocument.getTextFieldService();

        ITextField[] placeholders = null;
        try {
            placeholders = textFieldService.getPlaceholderFields();
        } catch (TextException e) {
            e.printStackTrace();
        }

        String placeholderDisplayText = "";
        try {
            for (int i = 0; i < placeholders.length; i++) {
                boolean schonersetzt = false;
                try {
                    placeholderDisplayText = placeholders[i].getDisplayText()
                                                            .toLowerCase();
                } catch (com.sun.star.uno.RuntimeException ex) {
                    ex.printStackTrace();
                    System.out.println("Fehler bei Placehoder " + i);
                    System.out.println("Insgesamt Placeholderanzahl = " + placeholders.length);
                }

                Set<?> entries = SystemConfig.hmAdrPDaten.entrySet();
                Iterator<?> it = entries.iterator();
                while (it.hasNext()) {
                    Entry<?, ?> entry = (Entry<?, ?>) it.next();
                    if (entry.getKey()
                             .toString()
                             .toLowerCase()
                             .equals(placeholderDisplayText)) {
                        if ("".equals(entry.getValue()
                                           .toString()
                                           .trim())) {
                            placeholders[i].getTextRange()
                                           .setText("\b");
                        } else {
                            placeholders[i].getTextRange()
                                           .setText(entry.getValue()
                                                         .toString());
                        }
                        schonersetzt = true;
                        break;
                    }
                }
                entries = SystemConfig.hmAdrKDaten.entrySet();
                it = entries.iterator();
                while (it.hasNext() && !schonersetzt) {
                    Entry<?, ?> entry = (Entry<?, ?>) it.next();
                    if (((String) entry.getKey()).toLowerCase()
                                                 .equals(placeholderDisplayText)) {
                        if ("".equals(((String) entry.getValue()).trim())) {
                            OOTools.loescheLeerenPlatzhalter(textDocument, placeholders[i]);
                        } else {
                            placeholders[i].getTextRange()
                                           .setText((String) entry.getValue());
                        }
                        schonersetzt = true;
                        break;
                    }
                }
                entries = SystemConfig.hmAdrADaten.entrySet();
                it = entries.iterator();
                while (it.hasNext() && !schonersetzt) {
                    Entry<?, ?> entry = (Entry<?, ?>) it.next();
                    if (((String) entry.getKey()).toLowerCase()
                                                 .equals(placeholderDisplayText)) {
                        if ("".equals(((String) entry.getValue()).trim())) {
                            OOTools.loescheLeerenPlatzhalter(textDocument, placeholders[i]);
                        } else {
                            placeholders[i].getTextRange()
                                           .setText((String) entry.getValue());
                        }
                        schonersetzt = true;
                        break;
                    }
                }
                entries = SystemConfig.hmAdrRDaten.entrySet();
                it = entries.iterator();
                while (it.hasNext() && !schonersetzt) {
                    Entry<?, ?> entry = (Entry<?, ?>) it.next();
                    if (((String) entry.getKey()).toLowerCase()
                                                 .equals(placeholderDisplayText)) {
                        if ("".equals(((String) entry.getValue()).trim())) {
                            placeholders[i].getTextRange()
                                           .setText("");
                            OOTools.loescheLeerenPlatzhalter(textDocument, placeholders[i]);
                        } else {
                            placeholders[i].getTextRange()
                                           .setText((String) entry.getValue());
                        }
                        schonersetzt = true;
                        break;
                    }
                }
                if (!schonersetzt) {
                    OOTools.loescheLeerenPlatzhalter(textDocument, placeholders[i]);
                }
            }
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Fehler in der Dokumentvorlage");
            if (document != null) {
                document.close();
                try {
                    ooService.getOfficeapplication()
                             .deactivate();
                } catch (OfficeApplicationException e) {
                    e.printStackTrace();
                }
                ooService.getOfficeapplication()
                         .dispose();
                return;
            }
        }

        OOTools.sucheNachPlatzhalter(textDocument);

        try {
            OOTools.refreshTextFields(textDocument.getXTextDocument());
        } catch (Exception e) {
            e.printStackTrace();
        }

        IViewCursor viewCursor = textDocument.getViewCursorService()
                                             .getViewCursor();
        viewCursor.getPageCursor()
                  .jumpToFirstPage();
        final ITextDocument xtextDocument = textDocument;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                stopps(monitor);
                xtextDocument.getFrame()
                             .getXFrame()
                             .getContainerWindow()
                             .setVisible(true);
                xtextDocument.getFrame()
                             .setFocus();
            }

        });
    }

    public static void erstzeNurPlatzhalter(ITextDocument textDocument, Monitor monitor) {
        ITextFieldService textFieldService = textDocument.getTextFieldService();


        try {
            ITextField[] placeholders = textFieldService.getPlaceholderFields();
            for (ITextField placeholder : placeholders) {
                boolean schonersetzt = false;

                String placeholderDisplayText = placeholder.getDisplayText() ;

                HashMap<String, String> hmAdrPDaten = SystemConfig.hmAdrPDaten;


                Set<Entry<String, String>> entries = hmAdrPDaten.entrySet();
                for (Entry<String, String> entry : entries) {
                    if (entry.getKey()

                             .equalsIgnoreCase(placeholderDisplayText)) {
                        if ("".equals(entry.getValue()

                                           .trim())) {
                            placeholder.getTextRange()
                                           .setText("\b");
                        } else {
                            placeholder.getTextRange()
                                           .setText(entry.getValue());
                        }
                        schonersetzt = true;
                        break;
                    }
                }
                Set<Entry<String, String>> entries1 = SystemConfig.hmAdrKDaten.entrySet();
                Iterator<Entry<String, String>> it1 = entries1.iterator();
                while (it1.hasNext() && !schonersetzt) {
                    Entry<String, String> entry = it1.next();
                    if (entry.getKey()
                             .equalsIgnoreCase(placeholderDisplayText)) {
                        if ("".equals(((String) entry.getValue()).trim())) {
                            OOTools.loescheLeerenPlatzhalter(textDocument, placeholder);
                        } else {
                            placeholder.getTextRange()
                                           .setText((String) entry.getValue());
                        }
                        schonersetzt = true;
                        break;
                    }
                }
                entries1 = SystemConfig.hmAdrADaten.entrySet();
                it1 = entries1.iterator();
                while (it1.hasNext() && !schonersetzt) {
                    Entry<String, String> entry = it1.next();
                    if (entry.getKey()
                             .equalsIgnoreCase(placeholderDisplayText)) {
                        if ("".equals(((String) entry.getValue()).trim())) {
                            OOTools.loescheLeerenPlatzhalter(textDocument, placeholder);
                        } else {
                            placeholder.getTextRange()
                                           .setText((String) entry.getValue());
                        }
                        schonersetzt = true;
                        break;
                    }
                }
                entries1 = SystemConfig.hmAdrRDaten.entrySet();
                it1 = entries1.iterator();
                while (it1.hasNext() && !schonersetzt) {
                    Entry<String, String> entry = it1.next();
                    if (entry.getKey()
                             .equalsIgnoreCase(placeholderDisplayText)) {
                        if ("".equals(((String) entry.getValue()).trim())) {
                            placeholder.getTextRange()
                                           .setText("");
                            OOTools.loescheLeerenPlatzhalter(textDocument, placeholder);
                        } else {
                            placeholder.getTextRange()
                                           .setText((String) entry.getValue());
                        }
                        schonersetzt = true;
                        break;
                    }
                }
                if (!schonersetzt) {
                    OOTools.loescheLeerenPlatzhalter(textDocument, placeholder);
                }
            }
        } catch (IllegalArgumentException | TextException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Fehler in der Dokumentvorlage");
            textDocument.close();
            try {
                new OOService().getOfficeapplication()
                               .deactivate();
            } catch (OfficeApplicationException e) {
                e.printStackTrace();
            }
            new OOService().getOfficeapplication()
                           .dispose();
            return;
        }

        OOTools.sucheNachPlatzhalter(textDocument);

        IViewCursor viewCursor = textDocument.getViewCursorService()
                                             .getViewCursor();
        viewCursor.getPageCursor()
                  .jumpToFirstPage();
        final ITextDocument xtextDocument = textDocument;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                stopps(monitor);
                xtextDocument.getFrame()
                             .getXFrame()
                             .getContainerWindow()
                             .setVisible(true);
                xtextDocument.getFrame()
                             .setFocus();
            }

        });
    }

    public static synchronized void starteRGKopie(String url, String drucker, Monitor monitor) {
        IDocumentService documentService = null;
        ITextDocument textDocument;
        stopps(monitor);

        if (!new OOService().getOfficeapplication()
                            .isActive()) {
            try {
                new OOService().start();
            } catch (OfficeApplicationException | FileNotFoundException e) {
            }
        }
        try {
            documentService = new OOService().getOfficeapplication()
                                             .getDocumentService();
        } catch (OfficeApplicationException e) {
            e.printStackTrace();
        }
        IDocumentDescriptor docdescript = new DocumentDescriptor();
        docdescript.setHidden(true);
        docdescript.setAsTemplate(true);
        IDocument document = null;

        try {
            document = documentService.loadDocument(url, docdescript);
        } catch (NOAException e) {
            e.printStackTrace();
        }
        textDocument = (ITextDocument) document;
        if (drucker != null) {
            String druckerName = null;
            try {
                druckerName = textDocument.getPrintService()
                                          .getActivePrinter()
                                          .getName();
            } catch (NOAException e) {
                e.printStackTrace();
            }
            // Wenn nicht gleich wie in der INI angegeben -> Drucker wechseln
            IPrinter iprint = null;
            if (!druckerName.equals(drucker)) {
                try {
                    iprint = textDocument.getPrintService()
                                         .createPrinter(drucker);
                } catch (NOAException e) {
                    e.printStackTrace();
                }
                try {
                    textDocument.getPrintService()
                                .setActivePrinter(iprint);
                } catch (NOAException e) {
                    e.printStackTrace();
                }
            }
        }
        ITextFieldService textFieldService = textDocument.getTextFieldService();
        ITextField[] placeholders = null;
        try {
            placeholders = textFieldService.getPlaceholderFields();
        } catch (TextException e) {
            e.printStackTrace();
        }
        String placeholderDisplayText = "";
        try {
            for (int i = 0; i < placeholders.length; i++) {
                // boolean loeschen = false;
                boolean schonersetzt = false;
                try {
                    placeholderDisplayText = placeholders[i].getDisplayText()
                                                            .toLowerCase();
                    // System.out.println(placeholderDisplayText);
                } catch (com.sun.star.uno.RuntimeException ex) {
                    // System.out.println("************catch()*******************");
                    ex.printStackTrace();
                }

                Set<?> entries = SystemConfig.hmAdrPDaten.entrySet();
                Iterator<?> it = entries.iterator();
                while (it.hasNext()) {
                    Entry<?, ?> entry = (Entry<?, ?>) it.next();
                    if (entry.getKey()
                             .toString()
                             .toLowerCase()
                             .equals(placeholderDisplayText)) {
                        if ("".equals(entry.getValue()
                                           .toString()
                                           .trim())) {
                            placeholders[i].getTextRange()
                                           .setText("\b");
                            // OOTools.loescheLeerenPlatzhalter(textDocument, placeholders[i]);
                        } else {
                            placeholders[i].getTextRange()
                                           .setText(entry.getValue()
                                                         .toString());
                        }
                        schonersetzt = true;
                        break;
                    }
                }
                entries = SystemConfig.hmRgkDaten.entrySet();
                it = entries.iterator();
                while (it.hasNext() && !schonersetzt) {
                    Entry<?, ?> entry = (Entry<?, ?>) it.next();
                    if (((String) entry.getKey()).toLowerCase()
                                                 .equals(placeholderDisplayText)) {
                        if ("".equals(((String) entry.getValue()).trim())) {
                            placeholders[i].getTextRange()
                                           .setText("");
                            OOTools.loescheLeerenPlatzhalter(textDocument, placeholders[i]);
                        } else {
                            placeholders[i].getTextRange()
                                           .setText((String) entry.getValue());
                        }
                        schonersetzt = true;
                        break;
                    }
                }
                if (!schonersetzt) {
                    OOTools.loescheLeerenPlatzhalter(textDocument, placeholders[i]);
                }
            }
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Fehler in der Dokumentvorlage");
            if (document != null) {
                document.close();
                try {
                    new OOService().getOfficeapplication()
                                   .deactivate();
                } catch (OfficeApplicationException e) {
                    e.printStackTrace();
                }
                new OOService().getOfficeapplication()
                               .dispose();
                return;
            }
        }
        stopps(monitor);
        IViewCursor viewCursor = textDocument.getViewCursorService()
                                             .getViewCursor();
        viewCursor.getPageCursor()
                  .jumpToFirstPage();

        final ITextDocument xtextDocument = textDocument;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                xtextDocument.getFrame()
                             .getXFrame()
                             .getContainerWindow()
                             .setVisible(true);
                xtextDocument.getFrame()
                             .setFocus();
            }
        });
    }

    public static synchronized void starteBacrodeFormular(String url, String drucker, Monitor monitor) {
        // hidden external dependencies
        String rezBarcodeDrucker = SystemConfig.rezBarcodeDrucker;
        HashMap<String, String> hmAdrPDaten = SystemConfig.hmAdrPDaten;
        HashMap<String, String> hmAdrKDaten = SystemConfig.hmAdrKDaten;
        HashMap<String, String> hmAdrADaten = SystemConfig.hmAdrADaten;
        HashMap<String, String> hmAdrRDaten = SystemConfig.hmAdrRDaten;
        boolean direktDruck = SystemConfig.oTerminListe.DirektDruck;

        starteBarcodeFormular(url, monitor, rezBarcodeDrucker, hmAdrPDaten, hmAdrKDaten, hmAdrADaten, hmAdrRDaten,
                direktDruck);
    }

    private static void starteBarcodeFormular(String url, Monitor monitor, String rezBarcodeDrucker,
            HashMap<String, String> hmAdrPDaten, HashMap<String, String> hmAdrKDaten,
            HashMap<String, String> hmAdrADaten, HashMap<String, String> hmAdrRDaten, boolean direktDruck) {
        IDocumentService documentService = null;
        starts(monitor);
        // System.out.println("Starte Datei -> "+url);
        OOService ooService = new OOService();
        if (!ooService.getOfficeapplication()
                      .isActive()) {
            try {
                ooService.start();
            } catch (FileNotFoundException | OfficeApplicationException e) {
                e.printStackTrace();
            }
        }
        try {
            documentService = ooService.getOfficeapplication()
                                       .getDocumentService();
        } catch (OfficeApplicationException e) {
            e.printStackTrace();
        }
        IDocumentDescriptor docdescript = new DocumentDescriptor();
        docdescript.setHidden(true);
        docdescript.setAsTemplate(true);
        IDocument document = null;
        // ITextTable[] tbl = null;
        try {
            document = documentService.loadDocument(url, docdescript);
        } catch (NOAException e) {
            e.printStackTrace();
        }
        ITextDocument textDocument = (ITextDocument) document;
        OOTools.druckerSetzen(textDocument, rezBarcodeDrucker);
        ITextFieldService textFieldService = textDocument.getTextFieldService();
        ITextField[] placeholders = null;
        try {
            placeholders = textFieldService.getPlaceholderFields();
        } catch (TextException e) {
            e.printStackTrace();
        }
        String placeholderDisplayText = "";
        for (int i = 0; i < placeholders.length; i++) {
            boolean schonersetzt = false;
            try {
                placeholderDisplayText = placeholders[i].getDisplayText()
                                                        .toLowerCase();
            } catch (com.sun.star.uno.RuntimeException ex) {
                ex.printStackTrace();
            }

            Set<?> entries = hmAdrPDaten.entrySet();
            Iterator<?> it = entries.iterator();
            while (it.hasNext()) {
                Entry<?, ?> entry = (Entry<?, ?>) it.next();
                if (((String) entry.getKey()).toLowerCase()
                                             .equals(placeholderDisplayText)) {
                    if ("".equals(((String) entry.getValue()).trim())) {
                        placeholders[i].getTextRange()
                                       .setText("\b");
                    } else {
                        placeholders[i].getTextRange()
                                       .setText((String) entry.getValue());
                    }
                    schonersetzt = true;
                    break;
                }
            }
            entries = hmAdrKDaten.entrySet();
            it = entries.iterator();
            while (it.hasNext() && !schonersetzt) {
                Entry<?, ?> entry = (Entry<?, ?>) it.next();
                if (((String) entry.getKey()).toLowerCase()
                                             .equals(placeholderDisplayText)) {
                    if ("".equals(((String) entry.getValue()).trim())) {
                        OOTools.loescheLeerenPlatzhalter(textDocument, placeholders[i]);
                    } else {
                        placeholders[i].getTextRange()
                                       .setText((String) entry.getValue());
                    }
                    schonersetzt = true;
                    break;
                }
            }

            entries = hmAdrADaten.entrySet();
            it = entries.iterator();
            while (it.hasNext() && !schonersetzt) {
                Entry<?, ?> entry = (Entry<?, ?>) it.next();
                if (((String) entry.getKey()).toLowerCase()
                                             .equals(placeholderDisplayText)) {
                    if ("".equals(((String) entry.getValue()).trim())) {
                        // placeholders[i].getTextRange().setText("\b");
                        OOTools.loescheLeerenPlatzhalter(textDocument, placeholders[i]);
                    } else {
                        placeholders[i].getTextRange()
                                       .setText((String) entry.getValue());
                    }
                    schonersetzt = true;
                    break;
                }
            }

            entries = hmAdrRDaten.entrySet();
            it = entries.iterator();
            while (it.hasNext() && !schonersetzt) {
                Entry<?, ?> entry = (Entry<?, ?>) it.next();
                if (((String) entry.getKey()).toLowerCase()
                                             .equals(placeholderDisplayText)) {
                    if ("".equals(((String) entry.getValue()).trim())) {
                        placeholders[i].getTextRange()
                                       .setText("");
                        OOTools.loescheLeerenPlatzhalter(textDocument, placeholders[i]);
                    } else {
                        placeholders[i].getTextRange()
                                       .setText((String) entry.getValue());
                    }
                    schonersetzt = true;
                    break;
                }
            }
            if (!schonersetzt) {
                OOTools.loescheLeerenPlatzhalter(textDocument, placeholders[i]);
            }
        }
        stopps(monitor);
        IViewCursor viewCursor = textDocument.getViewCursorService()
                                             .getViewCursor();
        viewCursor.getPageCursor()
                  .jumpToFirstPage();

        if (!direktDruck) {
            textDocument.getFrame()
                        .getXFrame()
                        .getContainerWindow()
                        .setVisible(true);
            textDocument.getFrame()
                        .setFocus();
        } else {
            final ITextDocument xdoc = textDocument;
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    try {
                        // textDocument.print();
                        xdoc.print();
                        Thread.sleep(50);
                        // textDocument.close();
                        xdoc.close();
                        Thread.sleep(100);
                    } catch (DocumentException | InterruptedException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }.execute();
        }
    }

    public static void starteTaxierung(String url, HashMap<String, String> taxWerte, Monitor monitor)
            throws OfficeApplicationException, NOAException, TextException {
        IDocumentService documentService;
        starts(monitor);
        if (!new OOService().getOfficeapplication()
                            .isActive()) {
            try {
                new OOService().start();
            } catch (OfficeApplicationException | FileNotFoundException e) {
            }
        }

        documentService = new OOService().getOfficeapplication()
                                         .getDocumentService();

        IDocumentDescriptor docdescript = new DocumentDescriptor();
        docdescript.setHidden(true);
        docdescript.setAsTemplate(true);
        IDocument document;

        document = documentService.loadDocument(url, docdescript);
        ITextDocument textDocument = (ITextDocument) document;
        if ("0".equals(SystemConfig.hmAbrechnung.get("hmusePrinterFromTemplate"))) {
            OOTools.druckerSetzen(textDocument, SystemConfig.hmAbrechnung.get("hmgkvtaxierdrucker"));
        }
        ITextFieldService textFieldService = textDocument.getTextFieldService();
        ITextField[] placeholders;

        placeholders = textFieldService.getPlaceholderFields();
        String placeholderDisplayText = "";
        // System.out.println("Platzhalteranzahl = "+placeholders.length);
        for (int i = 0; i < placeholders.length; i++) {
            placeholderDisplayText = placeholders[i].getDisplayText()
                                                    .toLowerCase();
            // System.out.println(i+" - "+placeholderDisplayText);
            Set<?> entries = taxWerte.entrySet();
            Iterator<?> it = entries.iterator();
            while (it.hasNext()) {
                Entry<?, ?> entry = (Entry<?, ?>) it.next();
                if (((String) entry.getKey()).toLowerCase()
                                             .equals(placeholderDisplayText)) {
                    placeholders[i].getTextRange()
                                   .setText((String) entry.getValue());

                    break;
                }
            }
        }
        if ("1".equals(SystemConfig.hmAbrechnung.get("hmallinoffice"))) {
            textDocument.getFrame()
                        .getXFrame()
                        .getContainerWindow()
                        .setVisible(true);
        } else {
            final ITextDocument xdoc = textDocument;
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    try {
                        xdoc.print();
                        Thread.sleep(50);
                        xdoc.close();
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }.execute();
        }
    }

    private static void starts(Monitor monitor) {
        monitor.statusChange(Monitor.START);
    }

    private static void stopps(Monitor monitor) {
        monitor.statusChange(Monitor.STOP);
    }

    public static void starteTherapieBericht(String url) {
        IDocumentService documentService = null;
        if (!new OOService().getOfficeapplication()
                            .isActive()) {
            try {
                new OOService().start();
            } catch (OfficeApplicationException | FileNotFoundException e) {
            }
        }
        try {
            documentService = new OOService().getOfficeapplication()
                                             .getDocumentService();
        } catch (OfficeApplicationException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Fehler im OpenOffice-System - Therapiebericht kann nicht erstellt werden");
            return;
        }
        IDocumentDescriptor docdescript = new DocumentDescriptor();
        docdescript.setHidden(true);
        docdescript.setAsTemplate(true);
        IDocument document = null;
        try {
            document = documentService.loadDocument(url, docdescript);
        } catch (NOAException e) {
            e.printStackTrace();
        }
        try {
            Thread.sleep(100);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }

        ITextDocument textDocument = (ITextDocument) document;
        ITextFieldService textFieldService = textDocument.getTextFieldService();
        ITextField[] placeholders = null;
        try {
            placeholders = textFieldService.getPlaceholderFields();
        } catch (TextException e) {
            e.printStackTrace();
        }
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            Vector<String> docPlatzhalter = new Vector<>();
            int i;
            int anzahlph = placeholders.length;

            for (i = 0; i < anzahlph; i++) {
                docPlatzhalter.add(placeholders[i].getDisplayText()
                                                  .substring(0, 1)
                        + placeholders[i].getDisplayText()
                                         .substring(1, 2)
                                         .toUpperCase()
                        + placeholders[i].getDisplayText()
                                         .substring(2)
                                         .toLowerCase());
            }

            String wert;
            for (i = 0; i < anzahlph; i++) {
                if (SystemConfig.hmAdrBDaten.get(docPlatzhalter.get(i)) != null) {
                    wert = SystemConfig.hmAdrBDaten.get(docPlatzhalter.get(i));
                    wert = wert == null ? "" : wert;
                    if ("".equals(wert.trim())) {
                        OOTools.loescheLeerenPlatzhalter(textDocument, placeholders[i]);
                    } else {
                        placeholders[i].getTextRange()
                                       .setText(wert);
                    }
                }
            }
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
        IViewCursor viewCursor = textDocument.getViewCursorService()
                                             .getViewCursor();
        viewCursor.getPageCursor()
                  .jumpToFirstPage();
        final ITextDocument xtextDocument = textDocument;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Reha.getThisFrame()
                    .setCursor(Cursors.normalCursor);
                xtextDocument.getFrame()
                             .getXFrame()
                             .getContainerWindow()
                             .setVisible(true);
                xtextDocument.getFrame()
                             .setFocus();
            }
        });
    }

    public static synchronized ITextDocument starteGKVBericht(String url, String drucker, Monitor monitor) {
        starts(monitor);
        IDocumentService documentService = null;
        try {
            documentService = new OOService().getOfficeapplication()
                                             .getDocumentService();
        } catch (OfficeApplicationException e) {
            e.printStackTrace();
        }
        IDocumentDescriptor docdescript = new DocumentDescriptor();
        docdescript.setHidden(true);
        docdescript.setAsTemplate(true);
        IDocument document = null;
        try {
            document = documentService.loadDocument(url, docdescript);
        } catch (NOAException e) {
            e.printStackTrace();
        }
        ITextDocument textDocument = (ITextDocument) document;
        if (drucker != null) {
            String druckerName = null;
            try {
                druckerName = textDocument.getPrintService()
                                          .getActivePrinter()
                                          .getName();
            } catch (NOAException e) {
                e.printStackTrace();
            }
            // Wenn nicht gleich wie in der INI angegeben -> Drucker wechseln
            IPrinter iprint = null;
            if (!druckerName.equals(drucker)) {
                try {
                    iprint = textDocument.getPrintService()
                                         .createPrinter(drucker);
                } catch (NOAException e) {
                    e.printStackTrace();
                }
                try {
                    textDocument.getPrintService()
                                .setActivePrinter(iprint);
                } catch (NOAException e) {
                    e.printStackTrace();
                }
            }
        }
        ITextFieldService textFieldService = textDocument.getTextFieldService();
        ITextField[] placeholders = null;
        try {
            placeholders = textFieldService.getPlaceholderFields();
        } catch (TextException e) {
            e.printStackTrace();
        }
        String placeholderDisplayText = "";
        for (int i = 0; i < placeholders.length; i++) {
            boolean schonersetzt = false;
            try {
                placeholderDisplayText = placeholders[i].getDisplayText()
                                                        .toLowerCase();
            } catch (com.sun.star.uno.RuntimeException ex) {
                ex.printStackTrace();
            }

            Set<?> entries = SystemConfig.hmAdrPDaten.entrySet();
            Iterator<?> it = entries.iterator();
            while (it.hasNext()) {
                Entry<?, ?> entry = (Entry<?, ?>) it.next();
                if (((String) entry.getKey()).toLowerCase()
                                             .equals(placeholderDisplayText)) {
                    if ("".equals(((String) entry.getValue()).trim())) {
                        placeholders[i].getTextRange()
                                       .setText("\b");
                    } else {
                        placeholders[i].getTextRange()
                                       .setText((String) entry.getValue());
                    }
                    schonersetzt = true;
                    break;
                }
            }
            entries = SystemConfig.hmAdrKDaten.entrySet();
            it = entries.iterator();
            while (it.hasNext() && !schonersetzt) {
                Entry<?, ?> entry = (Entry<?, ?>) it.next();
                if (((String) entry.getKey()).toLowerCase()
                                             .equals(placeholderDisplayText)) {
                    if ("".equals(((String) entry.getValue()).trim())) {
                        OOTools.loescheLeerenPlatzhalter(textDocument, placeholders[i]);
                    } else {
                        placeholders[i].getTextRange()
                                       .setText((String) entry.getValue());
                    }
                    schonersetzt = true;
                    break;
                }
            }
            entries = SystemConfig.hmAdrADaten.entrySet();
            it = entries.iterator();
            while (it.hasNext() && !schonersetzt) {
                Entry<?, ?> entry = (Entry<?, ?>) it.next();
                if (((String) entry.getKey()).toLowerCase()
                                             .equals(placeholderDisplayText)) {
                    if ("".equals(((String) entry.getValue()).trim())) {
                        // placeholders[i].getTextRange().setText("\b");
                        OOTools.loescheLeerenPlatzhalter(textDocument, placeholders[i]);
                    } else {
                        placeholders[i].getTextRange()
                                       .setText((String) entry.getValue());
                    }
                    // placeholders[i].getTextRange().setText(((String)entry.getValue()));
                    schonersetzt = true;
                    break;
                }
            }

            entries = SystemConfig.hmEBerichtDaten.entrySet();
            it = entries.iterator();
            while (it.hasNext() && !schonersetzt) {
                Entry<?, ?> entry = (Entry<?, ?>) it.next();
                if (((String) entry.getKey()).toLowerCase()
                                             .equals(placeholderDisplayText)) {
                    if ("".equals(((String) entry.getValue()).trim())) {
                        OOTools.loescheLeerenPlatzhalter(textDocument, placeholders[i]);
                    } else {
                        placeholders[i].getTextRange()
                                       .setText((String) entry.getValue());
                    }
                    schonersetzt = true;
                    break;
                }
            }

            if (!schonersetzt && !"<bblock1>".equalsIgnoreCase(placeholders[i].getDisplayText())) {
                OOTools.loescheLeerenPlatzhalter(textDocument, placeholders[i]);
            }
        }

        return textDocument;
    }

    public static synchronized void ooOrgAnmelden(Monitor monitor) {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                IDocumentDescriptor docdescript = new DocumentDescriptor();
                docdescript.setHidden(true);
                IDocument document = null;
                ITextDocument textDocument = null;
                starts(monitor);
                try {
                    if (!new OOService().getOfficeapplication()
                                        .isActive()) {
                        try {
                            new OOService().start();
                        } catch (OfficeApplicationException | FileNotFoundException e) {
                        }
                    }

                    IDocumentService documentService = new OOService().getOfficeapplication()
                                                                      .getDocumentService();
                    document = documentService.constructNewDocument(IDocument.WRITER, docdescript);
                    textDocument = (ITextDocument) document;
                    textDocument.close();
                    System.err.println("Initiales Dokument wurde produziert und wieder geschlossen");
                    stopps(monitor);
                    Reha.instance.messageLabel.setForeground(Color.BLACK);
                    Reha.instance.messageLabel.setText("OpenOffice.org: Init o.k.");
                } catch (OfficeApplicationException | NOAException exception) {
                    Reha.instance.messageLabel.setText("OO.org: nicht Verfügbar");
                    exception.printStackTrace();
                }
                Reha.getThisFrame()
                    .setCursor(Cursors.normalCursor);
                return null;
            }

        }.execute();
    }
}
