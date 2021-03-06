package gBriefe;

import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.sun.star.view.DocumentZoomType;

import ag.ion.bion.officelayer.NativeView;
import ag.ion.bion.officelayer.application.IApplicationAssistant;
import ag.ion.bion.officelayer.application.ILazyApplicationInfo;
import ag.ion.bion.officelayer.application.IOfficeApplication;
import ag.ion.bion.officelayer.application.OfficeApplicationException;
import ag.ion.bion.officelayer.application.OfficeApplicationRuntime;
import ag.ion.bion.officelayer.desktop.GlobalCommands;
import ag.ion.bion.officelayer.desktop.IFrame;
import ag.ion.bion.officelayer.document.DocumentDescriptor;
import ag.ion.bion.officelayer.document.DocumentException;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.document.IDocumentDescriptor;
import ag.ion.bion.officelayer.event.ICloseListener;
import ag.ion.bion.officelayer.event.IDocumentEvent;
import ag.ion.bion.officelayer.event.IDocumentListener;
import ag.ion.bion.officelayer.event.IEvent;
import ag.ion.bion.officelayer.internal.application.ApplicationAssistant;
import ag.ion.bion.officelayer.text.ITextDocument;
import ag.ion.bion.officelayer.text.ITextField;
import ag.ion.bion.officelayer.text.ITextFieldService;
import ag.ion.bion.officelayer.text.TextException;
import ag.ion.bion.officelayer.web.IWebDocument;
import ag.ion.noa.NOAException;
import ag.ion.noa.frame.ILayoutManager;
import office.OOService;

public class OOoPanel {
    public ITextDocument doc;
    public IFrame frame;
    public ICloseListener clListener;


    public JPanel noaPanel;
    private static IFrame officeFrame;
    static ITextDocument document;
    public static ITextDocument textDocument;
    public static IWebDocument webdocument;
    public static IWebDocument webtextDocument;
    static final int ANSICHT_WEB = 1;
    static final int ANSICHT_DOKUMENT = 0;
    public static int ansicht;
    DokumentListener doclistener;

    public OOoPanel(JPanel jpan) {
        noaPanel = jpan;
        fillNOAPanel();
        try {
            configureOOOFrame(GBriefe.officeapplication.get(), officeFrame);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void dokumentLaden(String datei, Vector vec, boolean direktPrint) {
        IDocumentDescriptor docdescript = new DocumentDescriptor();
        docdescript.setAsTemplate(true);
        docdescript.setURL(datei);

        // docdescript.setHidden(true);
        // docdescript.setFilterDefinition(HTMLFilter.FILTER.getFilterDefinition(IDocument.WRITER));
        System.out.println("***************Datei = *****************");
        try {
            // document = (ITextDocument)
            // GBriefe.officeapplication.getDocumentService().loadDocument(datei,
            // docdescript);
            document = (ITextDocument) GBriefe.officeapplication.get().getDocumentService()
                                                                .loadDocument(officeFrame, datei, docdescript);
            document.zoom(DocumentZoomType.BY_VALUE, (short) 75);
        } catch (OfficeApplicationException | DocumentException e) {
            e.printStackTrace();
        }

        ITextFieldService textFieldService = document.getTextFieldService();
        ITextField[] placeholders = null;
        try {
            placeholders = textFieldService.getPlaceholderFields();
        } catch (TextException e) {
            e.printStackTrace();
        }
        int alter = Integer.parseInt((String) vec.get(6));
        for (int i = 0; i < placeholders.length; i++) {
            String placeholderDisplayText = placeholders[i].getDisplayText();
            System.out.println("Platzhalter-Text = " + placeholderDisplayText);

            if ("<Anrede>".equals(placeholderDisplayText)) {
                if (alter > 13) {
                    placeholders[i].getTextRange()
                                   .setText((String) vec.get(0));
                } else {
                    placeholders[i].getTextRange()
                                   .setText("");
                }
            }
            if ("<Banrede>".equals(placeholderDisplayText)) {
                placeholders[i].getTextRange()
                               .setText((String) vec.get(1));
            }
            if ("<Strasse>".equals(placeholderDisplayText)) {
                placeholders[i].getTextRange()
                               .setText((String) vec.get(3));
            }
            if ("<Ort>".equals(placeholderDisplayText)) {
                placeholders[i].getTextRange()
                               .setText((String) vec.get(4));
            }
            if ("<BriefAnrede>".equals(placeholderDisplayText)) {
                placeholders[i].getTextRange()
                               .setText((String) vec.get(2));
            }
            if ("<Jahre>".equals(placeholderDisplayText)) {
                if (alter >= 20) {
                    placeholders[i].getTextRange()
                                   .setText((String) vec.get(6) + "-sten");
                } else {
                    placeholders[i].getTextRange()
                                   .setText((String) vec.get(6) + "-ten");
                }
            }
        }
        if (direktPrint) {
            try {
                // document.getFrame().getXFrame().getContainerWindow().setVisible(true);
                document.print();
                Toolkit.getDefaultToolkit()
                       .beep();
                SteuerPanel.thisClass.setzteFertig();
            } catch (DocumentException e) {
                e.printStackTrace();
            }
        }
    }

    public void dokumentSchreibeText(String datei) {
        if (document != null) {
            document.close();
        }
        DocumentDescriptor d = new DocumentDescriptor();
        try {
            document = (ITextDocument) GBriefe.officeapplication.get().getDocumentService()
                                                                .constructNewDocument(officeFrame, IDocument.WRITER, d);
        } catch (NOAException | OfficeApplicationException e) {
            e.printStackTrace();
        }
    }

    private void fillNOAPanel() {
        if (noaPanel != null && GBriefe.officeapplication.isPresent()) {
            try {
                IOfficeApplication officeApplication = GBriefe.officeapplication.get();
                officeFrame = constructOOOFrame(officeApplication, noaPanel);
                System.out.println("nach constructOOOFrame");

                DocumentDescriptor d = new DocumentDescriptor();
                d.setTitle("Geburtstagsbriefe");
                document = (ITextDocument) officeApplication.getDocumentService()
                                                                    .constructNewDocument(officeFrame, IDocument.WRITER,
                                                                            d);

                // textDocument = (ITextDocument)document;
                if (doclistener == null) {
                    doclistener = new DokumentListener(officeApplication);
                }
                officeApplication.getDesktopService()
                                         .addDocumentListener(doclistener);
                officeFrame.disableDispatch(GlobalCommands.CLOSE_DOCUMENT);
                officeFrame.disableDispatch(GlobalCommands.QUIT_APPLICATION);
                officeFrame.disableDispatch(GlobalCommands.CLOSE_WINDOW);
                officeFrame.updateDispatches();

                noaPanel.setVisible(true);
                GBriefe.thisFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            } catch (Throwable throwable) {
                noaPanel.add(new JLabel("Error in creating the NOA panel: " + throwable.getMessage()));
            }
        }
    }

    private IOfficeApplication startOOO() throws Throwable {
        IApplicationAssistant applicationAssistant = new ApplicationAssistant(OOService.OpenOfficeNativePfad);
        // IApplicationAssistant applicationAssistant = new
        // ApplicationAssistant(System.getProperty("user.dir") + "\\lib");
        ILazyApplicationInfo[] appInfos = applicationAssistant.getLocalApplications();
        for (int i = 0; i < appInfos.length; i++) {
            System.out.println(appInfos[i]);
        }

        if (appInfos.length < 1) {
            throw new Throwable("No OpenOffice.org Application found.");
        }
        HashMap configuration = new HashMap();
        configuration.put(IOfficeApplication.APPLICATION_HOME_KEY, appInfos[0].getHome());
        configuration.put(IOfficeApplication.APPLICATION_TYPE_KEY, IOfficeApplication.LOCAL_APPLICATION);
        IOfficeApplication officeAplication = OfficeApplicationRuntime.getApplication(configuration);

        officeAplication.setConfiguration(configuration);
        officeAplication.activate();
        return officeAplication;
    }

    private IFrame constructOOOFrame(IOfficeApplication officeApplication, final Container parent) throws Throwable {
        final NativeView nativeView = new NativeView(OOService.OpenOfficeNativePfad);

        if (parent == null) {
            System.out.println("nativeView == null");
        }
        System.out.println("Pfad = " + OOService.OpenOfficeNativePfad);
        parent.add(nativeView);
        System.out.println("nach add nativeView");
        parent.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                nativeView.setPreferredSize(new Dimension(parent.getWidth() - 5, parent.getHeight() - 5));
                parent.getLayout()
                      .layoutContainer(parent);
            }
        });
        if (nativeView != null) {
            System.out.println("nativeView ist nicht null");
        }
        System.out.println(parent.getWidth() + " / " + parent.getHeight());
        nativeView.setPreferredSize(new Dimension(parent.getWidth() - 5, parent.getHeight() - 5));
        parent.getLayout()
              .layoutContainer(parent);
        IFrame officeFrame = officeApplication.getDesktopService()
                                              .constructNewOfficeFrame(nativeView);
        System.out.println("nach officeFrame");
        parent.validate();
        return officeFrame;
    }

    public static void configureOOOFrame(IOfficeApplication officeApplication, IFrame officeFrame) throws Throwable {
        ILayoutManager layoutManager = officeFrame.getLayoutManager();
        /*
         * layoutManager.hideAll();
         * layoutManager.showElement(ILayoutManager.URL_TOOLBAR_STANDARDBAR);
         * layoutManager.showElement(ILayoutManager.URL_TOOLBAR_TEXTOBJECTBAR);
         * layoutManager.showElement(ILayoutManager.URL_STATUSBAR);
         *
         * officeFrame.disableDispatch(GlobalCommands.CLOSE_DOCUMENT);
         * officeFrame.disableDispatch(GlobalCommands.CLOSE_WINDOW);
         * officeFrame.disableDispatch(GlobalCommands.QUIT_APPLICATION);
         * officeFrame.disableDispatch(GlobalCommands.NEW_MENU);
         * officeFrame.disableDispatch(GlobalCommands.NEW_DOCUMENT);
         * officeFrame.disableDispatch(GlobalCommands.OPEN_DOCUMENT);
         * officeFrame.disableDispatch(GlobalCommands.EDIT_DOCUMENT);
         * officeFrame.disableDispatch(GlobalCommands.DIREKT_EXPORT_DOCUMENT);
         * officeFrame.disableDispatch(GlobalCommands.MAIL_DOCUMENT);
         * officeFrame.disableDispatch(GlobalCommands.OPEN_HYPERLINK_DIALOG);
         * officeFrame.disableDispatch(GlobalCommands.EDIT_HYPERLINK);
         * officeFrame.disableDispatch(GlobalCommands.OPEN_DRAW_TOOLBAR);
         * officeFrame.disableDispatch(GlobalCommands.OPEN_NAVIGATOR);
         * officeFrame.disableDispatch(GlobalCommands.OPEN_GALLERY);
         * officeFrame.disableDispatch(GlobalCommands.OPEN_DATASOURCES);
         * officeFrame.disableDispatch(GlobalCommands.OPEN_STYLE_SHEET);
         * officeFrame.disableDispatch(GlobalCommands.OPEN_HELP);
         * //officeFrame.disableDispatch(GlobalCommands.PRINT_PREVIEW);
         */

        officeFrame.updateDispatches();

        // officeFrame.getDispatch(".uno:PrintLayout").dispatch();
    }
}

class DokumentListener implements IDocumentListener {
    private IOfficeApplication officeAplication;

    public DokumentListener(IOfficeApplication officeAplication) {
        this.officeAplication = officeAplication;
    }

    @Override
    public void onAlphaCharInput(IDocumentEvent arg0) {
    }

    @Override
    public void onFocus(IDocumentEvent arg0) {
    }

    @Override
    public void onInsertDone(IDocumentEvent arg0) {
    }

    @Override
    public void onInsertStart(IDocumentEvent arg0) {
    }

    @Override
    public void onLoad(IDocumentEvent arg0) {
    }

    @Override
    public void onLoadDone(IDocumentEvent arg0) {
        System.out.println("************************Dokument geladen************************* " + arg0);
    }

    @Override
    public void onLoadFinished(IDocumentEvent arg0) {
        System.out.println("************************Dokument geladen finished************************* " + arg0);
        /*
         * try {
         * Reha.officeapplication.getDesktopService().removeDocumentListener(this); }
         * catch (OfficeApplicationException e) {
         *
         * e.printStackTrace(); }
         */
    }

    @Override
    public void onModifyChanged(IDocumentEvent arg0) {
    }

    @Override
    public void onMouseOut(IDocumentEvent arg0) {
    }

    @Override
    public void onMouseOver(IDocumentEvent arg0) {
    }

    @Override
    public void onNew(IDocumentEvent arg0) {
    }

    @Override
    public void onNonAlphaCharInput(IDocumentEvent arg0) {
    }

    @Override
    public void onSave(IDocumentEvent arg0) {
        System.out.println("************************Dokument gespeichert - doneSave************************* " + arg0);
    }

    @Override
    public void onSaveAs(IDocumentEvent arg0) {
    }

    @Override
    public void onSaveAsDone(IDocumentEvent arg0) {
    }

    @Override
    public void onSaveDone(IDocumentEvent arg0) {
        System.out.println("************************Dokument gespeichert - done************************* " + arg0);
    }

    @Override
    public void onSaveFinished(IDocumentEvent arg0) {
        System.out.println("************************Dokument gespeichert - finished************************* " + arg0);
    }

    @Override
    public void onUnload(IDocumentEvent arg0) {
    }

    @Override
    public void disposing(IEvent arg0) {
    }
}
