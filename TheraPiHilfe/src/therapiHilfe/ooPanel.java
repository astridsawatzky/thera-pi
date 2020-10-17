package therapiHilfe;

import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JLabel;

import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXPanel;

import ag.ion.bion.officelayer.NativeView;
import ag.ion.bion.officelayer.application.IOfficeApplication;
import ag.ion.bion.officelayer.application.OfficeApplicationException;
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
import ag.ion.bion.officelayer.filter.HTMLFilter;
import ag.ion.bion.officelayer.text.ITextDocument;
import ag.ion.bion.officelayer.web.IWebDocument;

public class ooPanel {

    public ITextDocument doc = null;
    public IFrame frame = null;
    public ICloseListener clListener = null;

    static ooPanel thisClass;
    public JXPanel noaPanel = null;
    private static IFrame officeFrame = null;
    static ITextDocument document = null;
    public static ITextDocument textDocument;
    public static IWebDocument webdocument = null;
    public static IWebDocument webtextDocument;
    final static int ANSICHT_WEB = 1;
    final static int ANSICHT_DOKUMENT = 0;
    public static int ansicht = 0;

    ooPanel(JXPanel jpan) {
        noaPanel = jpan;
        thisClass = this;
        fillNOAPanel();

    }

    private void fillNOAPanel() {
        if (noaPanel != null) {
            try {

                officeFrame = constructOOOFrame(piHelp.officeapplication, noaPanel);
                DocumentDescriptor d = new DocumentDescriptor();
                d.setTitle("piHelp- leeres Dokument");

                document = (ITextDocument) piHelp.officeapplication.getDocumentService()
                                                                   .constructNewDocument(officeFrame, IDocument.WRITER,
                                                                           d);
                ansicht = ANSICHT_DOKUMENT;
                piHelp.thisClass.jFrame.setExtendedState(JXFrame.MAXIMIZED_BOTH);
                piHelp.thisClass.jFrame.validate();

                officeFrame.disableDispatch(GlobalCommands.CLOSE_DOCUMENT);
                officeFrame.disableDispatch(GlobalCommands.QUIT_APPLICATION);
                officeFrame.disableDispatch(GlobalCommands.CLOSE_WINDOW);
                officeFrame.updateDispatches();

                noaPanel.setVisible(true);
                piHelp.thisFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            } catch (Throwable throwable) {
                noaPanel.add(new JLabel("Error in creating the NOA panel: " + throwable.getMessage()));
            }
        }
    }

    private IFrame constructOOOFrame(IOfficeApplication officeApplication, final Container parent) throws Throwable {
        final NativeView nativeView = new NativeView(piHelp.OfficeNativePfad);
        parent.add(nativeView);
        parent.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                nativeView.setPreferredSize(new Dimension(parent.getWidth() - 5, parent.getHeight() - 5));
                parent.getLayout()
                      .layoutContainer(parent);
            }
        });
        nativeView.setPreferredSize(new Dimension(parent.getWidth() - 5, parent.getHeight() - 5));
        parent.getLayout()
              .layoutContainer(parent);
        IFrame officeFrame = officeApplication.getDesktopService()
                                              .constructNewOfficeFrame(nativeView);
        parent.validate();
        return officeFrame;
    }

    public static void schliesseText() {

        if (ansicht == ANSICHT_WEB) {
            if (webdocument.isOpen()) {
                webdocument.close();
            }
            thisClass.noaPanel.remove(0);
        } else {
            document.close();
            thisClass.noaPanel.remove(0);
        }
    }

    public static void neuesNoaPanel() {
        thisClass.fillNOAPanel();
    }

    /********************************************************/

    public static void starteDatei(String datei, boolean alsweb) {


        String startdatei = datei;
        if (!startdatei.contains(".html")) {
            startdatei = datei + ".html";
        }

        try {
            try {

                if (alsweb) {
                    IDocumentDescriptor docdescript = DocumentDescriptor.DEFAULT;
                    docdescript.setURL(datei);
                    docdescript.setFilterDefinition(HTMLFilter.FILTER.getFilterDefinition(IDocument.WEB));
                    webdocument = (IWebDocument) piHelp.officeapplication.getDocumentService()
                                                                         .loadDocument(officeFrame, startdatei,
                                                                                 docdescript);
                    ansicht = ANSICHT_WEB;
                } else {
                    IDocumentDescriptor docdescript = DocumentDescriptor.DEFAULT;
                    docdescript.setURL(datei);
                    docdescript.setFilterDefinition(HTMLFilter.FILTER.getFilterDefinition(IDocument.WRITER));
                    document = (ITextDocument) piHelp.officeapplication.getDocumentService()
                                                                       .loadDocument(officeFrame, startdatei,
                                                                               docdescript);
                    ansicht = ANSICHT_DOKUMENT;
                }
            } catch (DocumentException e) {

                e.printStackTrace();
            }

        } catch (OfficeApplicationException e) {

            e.printStackTrace();
        }

    }

    public static String speichernText(String aktdatei, Boolean neu) {
        String datei = null;
        try {

            datei = new String(aktdatei);
            if (!datei.contains(".html")) {
                datei = datei + ".html";
            } else {

            }
            if (ansicht == ANSICHT_DOKUMENT) {
                File f = new File(datei);

                if (f.exists()) {
                    document.getPersistenceService()
                            .store();
                    Thread.sleep(100);
                } else {
                    document.getPersistenceService()
                            .export(datei, new HTMLFilter());
                    Thread.sleep(100);
                }

            } else {
                File f = new File(datei);
                if (f.exists()) {
                    webdocument.getPersistenceService()
                               .store();
                    Thread.sleep(100);
                } else {
                    webdocument.getPersistenceService()
                               .export(datei, new HTMLFilter());
                    Thread.sleep(100);
                }

            }

        } catch (DocumentException e) {

            e.printStackTrace();
        } catch (InterruptedException e) {

            e.printStackTrace();
        }
        return new String(datei);
    }

    /***********************************************/
    public static void extrahiereBilder(String url) {
        helpFenster.thisClass.bilder.clear();
        BufferedReader infile = null;
        BufferedWriter outfile = null;

        try {
            infile = new BufferedReader(new FileReader(url));
            outfile = new BufferedWriter(new FileWriter(url + ".html"));
            String str;
            while ((str = infile.readLine()) != null) {
                if (str.contains("IMG SRC=")) {
                    outfile.write(testeString(new String(str), "/") + "\n");
                    outfile.flush();
                } else {
                    outfile.write(new String(str) + "\n");
                    outfile.flush();
                }
            }
            outfile.flush();
            outfile.close();
            infile.close();
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    /***********************************************/
    public static String testeString(String webstring, String trenner) {
        int aktuell = 0;
        int wo = 0;

        String meinweb = new String(webstring);
        String ssret = "";
        int lang = meinweb.length();

        while ((wo = webstring.indexOf("IMG SRC=\"", aktuell)) > 0) {
            String nurBild = "";
            boolean start = false;
            boolean austritt = false;
            int iende = 0;
            int istart = 0;
            for (int i = wo; i < lang; i++) {
                for (int d = 0; d < 1; d++) {
                    if ((meinweb.substring(i, i + 1)
                                .equals("\""))
                            && (!start)) {
                        i++;
                        istart = i;
                        start = true;
                        break;
                    }
                    if ((meinweb.substring(i, i + 1)
                                .equals("\""))
                            && (start)) {
                        start = false;
                        iende = i;
                        austritt = true;
                        break;
                    }
                }
                if (austritt) {
                    break;
                }
                if (start) {
                    nurBild = nurBild + meinweb.substring(i, i + 1);
                }
            }
            int ergebnis = nurBild.lastIndexOf(trenner);
            String sret = "";
            if (ergebnis > -1) {
                sret = new String(nurBild.substring(ergebnis + 1));
                String salt = meinweb.substring(istart, iende);
                ssret = new String(meinweb.replaceAll(salt, sret));
                helpFenster.thisClass.bilder.add(sret.replaceAll("%20", " "));
            } else {
                sret = nurBild;
                ssret = new String(meinweb);
                helpFenster.thisClass.bilder.add(nurBild.replaceAll("%20", " "));
            }
            aktuell = new Integer(iende);
        }

        return ssret;

    }
    /***********************************************/
}

class testObVorhanden {
    String svorhanden = null;

    public boolean init(String svorhanden) {
        try (Statement stmtx = piHelp.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                ResultSet rsx = stmtx.executeQuery("select count(*) from htitel where titel=" + svorhanden);) {
            rsx.next();
            if (rsx.getInt(1) <= 0) {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }
}

class DokumentListener implements IDocumentListener {


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
        System.out.println("onLoad Done");

        // System.out.println("************************Dokument
        // geladen************************* "+arg0);

    }

    @Override
    public void onLoadFinished(IDocumentEvent arg0) {
        System.out.println("onLoad Finished");

        // System.out.println("************************Dokument geladen
        // finished************************* "+arg0);
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

        // System.out.println("************************Dokument gespeichert -
        // doneSave************************* "+arg0);
    }

    @Override
    public void onSaveAs(IDocumentEvent arg0) {

    }

    @Override
    public void onSaveAsDone(IDocumentEvent arg0) {
        System.out.println("saveas Done");

    }

    @Override
    public void onSaveDone(IDocumentEvent arg0) {
        System.out.println("save Done");

        // System.out.println("************************Dokument gespeichert -
        // done************************* "+arg0);
    }

    @Override
    public void onSaveFinished(IDocumentEvent arg0) {

        // System.out.println("************************Dokument gespeichert -
        // finished************************* "+arg0);
    }

    @Override
    public void onUnload(IDocumentEvent arg0) {

    }

    @Override
    public void disposing(IEvent arg0) {

    }

}
