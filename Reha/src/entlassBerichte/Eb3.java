package entlassBerichte;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ContainerAdapter;
import java.awt.event.ContainerEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.jdesktop.swingx.JXPanel;

import com.mysql.jdbc.PreparedStatement;
import com.sun.star.frame.XController;
import com.sun.star.text.XTextViewCursor;
import com.sun.star.text.XTextViewCursorSupplier;
import com.sun.star.uno.UnoRuntime;

import CommonTools.RehaEvent;
import CommonTools.RehaEventClass;
import CommonTools.RehaEventListener;
import CommonTools.SqlInfo;
import CommonTools.TopWindow;
import ag.ion.bion.officelayer.NativeView;
import ag.ion.bion.officelayer.application.IOfficeApplication;
import ag.ion.bion.officelayer.desktop.GlobalCommands;
import ag.ion.bion.officelayer.desktop.IFrame;
import ag.ion.bion.officelayer.document.DocumentDescriptor;
import ag.ion.bion.officelayer.document.DocumentException;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.filter.PDFFilter;
import ag.ion.bion.officelayer.filter.RTFFilter;
import ag.ion.bion.officelayer.text.ITextDocument;
import ag.ion.bion.officelayer.text.ITextRange;
import ag.ion.bion.officelayer.text.IViewCursor;
import ag.ion.noa.frame.ILayoutManager;
import environment.Path;
import hauptFenster.Reha;
import office.OOService;
import office.OOTools;
import umfeld.Betriebsumfeld;

public class Eb3 implements RehaEventListener {
    RehaEventClass rEvent = null;
    public JXPanel pan = null;
    JXPanel parken = null;
    JPanel oopan = null;
    EBerichtPanel eltern = null;
    Container xparent = null;
    NativeView nativeView = null;
    ByteArrayOutputStream outtemp = null;
    public String tempPfad = Path.Instance.getProghome() + "temp/" + Betriebsumfeld.getAktIK() + "/";

    /*********** neue logische Variable ***************/
    boolean newframeok = false;
    boolean bytebufferok = false;
    boolean pdfok = false;
    boolean inseitenaufbau = false;
    boolean framegetrennt = true;
    boolean zugabe = false;
    InputStream startStream = null;

    public Eb3(EBerichtPanel xeltern) {
        eltern = xeltern;
        rEvent = new RehaEventClass();
        rEvent.addRehaEventListener(this);
        pan = new JXPanel(new BorderLayout());
        pan.setDoubleBuffered(true);
        pan.setBorder(BorderFactory.createEmptyBorder(0, 0, 3, 0));
        pan.setVisible(true);
        pan.setName("ooNativePanel");
        if (!new OOService().getOfficeapplication().isActive()) {
            Reha.starteOfficeApplication();
        }
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    inseitenaufbau = true;
                    baueSeite();
                    while (inseitenaufbau) {
                        Thread.sleep(20);
                    }
                } catch (Exception ex) {
                    Reha.instance.progressStarten(false);
                    ex.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    public void beendeSeite() {
        eltern.document.close();
        if (outtemp != null) {
            try {
                outtemp.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void baueSeite() {
        new Thread() {
            @Override
            public void run() {
                new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {

                        try {
                            pdfok = false;

                            eltern.officeFrame = constructOOOFrame(new OOService().getOfficeapplication(), pan);
                            configureOOOFrame(new OOService().getOfficeapplication(), eltern.officeFrame);
                            DocumentDescriptor d = new DocumentDescriptor();
                            d.setTitle("Entlassbericht");
                            // Sofern es sich um eine Berichtsneuanlage handelt
                            if (eltern.neu) {
                                // wenn noch kein frame erstellt wurde und der outbuffe leer ist;

                                // System.out.println("Neuanlage Bericht -> constructNewDocument");
                                if (!new OOService().getOfficeapplication().isActive()) {
                                    Reha.starteOfficeApplication();
                                }
                                eltern.document = (ITextDocument) new OOService().getOfficeapplication().getDocumentService()
                                                                                        .constructNewDocument(
                                                                                                eltern.officeFrame,
                                                                                                IDocument.WRITER, d);
                                SwingUtilities.invokeLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            // Workaround für Java 7
                                            new TopWindow(eltern.document);
                                        } catch (Exception ex) {
                                            JOptionPane.showMessageDialog(null,
                                                    "Achtung!, der TopWindow-Listener (wichtig für Java 7) konnte nicht korrekt gestartet werden");
                                        }
                                    }
                                });

                                OOTools.setzePapierFormat(eltern.document, Integer.valueOf(25199),
                                        Integer.valueOf(19299));
                                OOTools.setzeRaender(eltern.document, Integer.valueOf(1000), Integer.valueOf(1000), Integer.valueOf(1000), Integer.valueOf(1000));
                                framegetrennt = false;
                                eltern.meldeInitOk(2);
                                SwingUtilities.invokeLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        eltern.jry.setSize(eltern.jry.getWidth() + (zugabe ? 1 : -1),
                                                eltern.jry.getHeight());
                                        zugabe = (zugabe ? false : true);
                                    }
                                });
                                eltern.document.setModified(false);
                            } else {
                                if (!new OOService().getOfficeapplication().isActive()) {
                                    // System.out.println("Aktiviere Office...");
                                    Reha.starteOfficeApplication();
                                    Thread.sleep(100);
                                }
                                new SwingWorker<Void, Void>() {
                                    @Override
                                    protected Void doInBackground() throws Exception {
                                        InputStream ins = null;
                                        try {

                                            // System.out.println("starte Dokument mit temp. Stream-Daten");
                                            ins = SqlInfo.holeStream("bericht2", "freitext",
                                                    "berichtid='" + eltern.berichtid + "'");
                                            if (ins.available() > 0) {
                                                DocumentDescriptor descript = new DocumentDescriptor();
                                                descript.setTitle("OpenOffice.org Bericht");
                                                // descript.setFilterDefinition(RTFFilter.FILTER.getFilterDefinition(IDocument.WRITER));
                                                try {
                                                    eltern.document = (ITextDocument) new OOService().getOfficeapplication().getDocumentService()
                                                                                                            .loadDocument(
                                                                                                                    eltern.officeFrame,
                                                                                                                    ins,
                                                                                                                    descript);
                                                    SwingUtilities.invokeLater(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            try {
                                                                // Workaraund für Java 7
                                                                new TopWindow(eltern.document);
                                                            } catch (Exception ex) {
                                                                JOptionPane.showMessageDialog(null,
                                                                        "Achtung!, der TopWindow-Listener (wichtig für Java 7) konnte nicht korrekt gestartet werden");
                                                            }
                                                        }
                                                    });

                                                    eltern.meldeInitOk(2);
                                                    SwingUtilities.invokeLater(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            eltern.jry.setSize(
                                                                    eltern.jry.getWidth() + (zugabe ? 1 : -1),
                                                                    eltern.jry.getHeight());
                                                            zugabe = (zugabe ? false : true);
                                                            try {
                                                                eltern.document.setModified(false);
                                                            } catch (DocumentException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    });
                                                } catch (Exception ex) {
                                                    Reha.starteOfficeApplication();
                                                    eltern.document = (ITextDocument) new OOService().getOfficeapplication().getDocumentService()
                                                                                                            .loadDocument(
                                                                                                                    eltern.officeFrame,
                                                                                                                    ins,
                                                                                                                    descript);
                                                    eltern.meldeInitOk(2);
                                                    SwingUtilities.invokeLater(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            eltern.jry.setSize(
                                                                    eltern.jry.getWidth() + (zugabe ? 1 : -1),
                                                                    eltern.jry.getHeight());
                                                            zugabe = (zugabe ? false : true);
                                                            try {
                                                                eltern.document.setModified(false);
                                                            } catch (DocumentException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    });
                                                }
                                            } else {
                                                DocumentDescriptor descript = new DocumentDescriptor();
                                                descript.setTitle("OpenOffice.org Bericht");
                                                eltern.document = (ITextDocument) new OOService().getOfficeapplication().getDocumentService()
                                                                                                        .constructNewDocument(
                                                                                                                eltern.officeFrame,
                                                                                                                IDocument.WRITER,
                                                                                                                descript);

                                                OOTools.setzePapierFormat(eltern.document, Integer.valueOf(25199),
                                                        Integer.valueOf(19299));
                                                OOTools.setzeRaender(eltern.document, Integer.valueOf(1000), Integer.valueOf(1000), Integer.valueOf(1000), Integer.valueOf(1000));
                                                framegetrennt = false;
                                                eltern.meldeInitOk(2);
                                                eltern.document.setModified(false);
                                                // JOptionPane.showMessageDialog(null, "Kann Daten aus Datenbank nicht
                                                // öffnen");
                                            }
                                            ins.close();
                                            eltern.document.setModified(false);
                                        } catch (Exception ex2) {
                                            inseitenaufbau = false;
                                            ex2.printStackTrace();
                                            return null;
                                        }

                                        new SwingWorker<Void, Void>() {
                                            @Override
                                            protected Void doInBackground() throws Exception {
                                                try {
                                                    String url = tempPfad + "EBfliesstext.pdf";
                                                    outtemp = new ByteArrayOutputStream();
                                                    eltern.document.getPersistenceService()
                                                                   .export(outtemp, new RTFFilter());
                                                    eltern.document.getPersistenceService()
                                                                   .export(url, new PDFFilter());
                                                    outtemp.close();
                                                    bytebufferok = true;
                                                    pdfok = true;
                                                } catch (Exception ex) {
                                                    ex.printStackTrace();
                                                    inseitenaufbau = false;
                                                }
                                                eltern.document.setModified(false);
                                                return null;

                                            }
                                        }.execute();
                                        XController xController = eltern.document.getXTextDocument()
                                                                                 .getCurrentController();
                                        XTextViewCursorSupplier xTextViewCursorSupplier = UnoRuntime.queryInterface(
                                                XTextViewCursorSupplier.class, xController);
                                        XTextViewCursor xtvc = xTextViewCursorSupplier.getViewCursor();
                                        xtvc.gotoStart(false);

                                        // eltern.document.getFrame().getXFrame().getContainerWindow().setVisible(true);
                                        // System.out.println("Status vorhandener Bericht -> am Ende des 2. Durchlaufes
                                        // = "+getStatus());
                                        OOTools.setzePapierFormat(eltern.document, Integer.valueOf(25199),
                                                Integer.valueOf(19299));
                                        OOTools.setzeRaender(eltern.document, Integer.valueOf(1000), Integer.valueOf(1000), Integer.valueOf(1000), Integer.valueOf(1000));
                                        framegetrennt = false;
                                        eltern.meldeInitOk(2);
                                        pan.setSize(pan.getWidth() + 1, pan.getHeight());
                                        eltern.document.setModified(false);
                                        return null;
                                    }
                                }.execute();
                            }

                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                        inseitenaufbau = false;
                        return null;
                    }
                }.execute();
            }
        }.start();

    }

    public JXPanel getSeite() {
        return pan;
    }

    /**********************
     *
     * @param officeApplication
     * @param parent
     * @return
     * @throws Throwable
     */

    private IFrame constructOOOFrame(IOfficeApplication officeApplication, final Container parent) throws Throwable {

        // final NativeView nativeView = new
        // NativeView(SystemConfig.OpenOfficeNativePfad);
        nativeView = new NativeView(OOService.OpenOfficeNativePfad);

        if (nativeView == null) {
            //// System.out.println("nativeView == null");
        }
        if (parent == null) {
            //// System.out.println("parent == null");
        }
        parent.add(nativeView);
        parent.validate();
        parent.setVisible(true);

        parent.addContainerListener(new ContainerAdapter() {
            @Override
            public void componentAdded(ContainerEvent e) {
                //// System.out.println(" added to "+e);
            }

            @Override
            public void componentRemoved(ContainerEvent e) {
                //// System.out.println(" removed from "+e);
            }
        });
        parent.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {

                // System.out.println("In NativeView Resize");
                refreshSize();
                nativeView.setPreferredSize(new Dimension(parent.getWidth(), parent.getHeight() - 5));
                parent.getLayout()
                      .layoutContainer(parent);
                // parent.repaint();
                if (EBerichtPanel.UseNeueRvVariante) {
                    eltern.ebt.getTab1_2015()
                              .refreshSize();
                } else {
                    eltern.ebt.getTab1()
                              .refreshSize();
                }

            }

            @Override
            public void componentHidden(ComponentEvent e) {
                //// System.out.println(e.getComponent().getClass().getName() + " --- Hidden");
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                //// System.out.println(e.getComponent().getClass().getName() + " --- Moved");
            }

            @Override
            public void componentShown(ComponentEvent e) {
                //// System.out.println(e.getComponent().getClass().getName() + " --- Shown");
                nativeView.setPreferredSize(new Dimension(parent.getWidth(), parent.getHeight() - 5));
                parent.getLayout()
                      .layoutContainer(parent);
                parent.setVisible(true);

            }

        });
        nativeView.setPreferredSize(new Dimension(parent.getWidth(), parent.getHeight() - 5));
        parent.getLayout()
              .layoutContainer(parent);
        eltern.officeFrame = officeApplication.getDesktopService()
                                              .constructNewOfficeFrame(nativeView);
        parent.validate();
        //// System.out.println("natveView eingeh�ngt in Panel "+parent.getName());
        return eltern.officeFrame;
    }

    public void tempTextSpeichern() {
        String url = tempPfad + "EBfliesstext.pdf";
        if (eltern.document.isOpen()) {
            if (eltern.document.isModified()) {
                // System.out.println("speichere temporär in: "+url);
                outtemp = new ByteArrayOutputStream();
                try {
                    eltern.document.getPersistenceService()
                                   .store(outtemp);
                    // eltern.document.getPersistenceService().export(outtemp, RTFFilter.FILTER);
                    eltern.document.getPersistenceService()
                                   .export(url, PDFFilter.FILTER);
                    eltern.document.setModified(false);
                    pdfok = true;
                    bytebufferok = false;
                    outtemp.close();
                } catch (DocumentException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                // System.out.println("Dokukment wurde nicht verändert, temporäres speichern
                // daher nicht erforderlich" );
            }

        }
    }

    public void tempStartSpeichern() {

    }

    public boolean textSpeichernInDB(boolean mittemp) {
        PreparedStatement ps = null;
        boolean fehler = false;

        try {
            if (eltern.document == null) {
                Reha.instance.progressStarten(false);
                return false;
            }
            if (!eltern.document.isOpen()) {
                Reha.instance.progressStarten(false);
                return false;
            }
            Reha.instance.progressStarten(true);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try {
                eltern.document.setModified(false);
                nativeView.setVisible(true);
                nativeView.requestFocus();
                eltern.document.getPersistenceService()
                               .store(out);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Fehler beim speichern, bitte erneut speichern drücken");
                fehler = true;
            }
            if (fehler) {
                return false;
            }

            InputStream ins = new ByteArrayInputStream(out.toByteArray());
            String select = "Update bericht2 set freitext = ? where berichtid = ? LIMIT 1";
            ps = (PreparedStatement) Reha.instance.conn.prepareStatement(select);
            ps.setAsciiStream(1, ins);
            ps.setInt(2, eltern.berichtid);
            ps.execute();
            ins.close();

            if (mittemp) {
                if (eltern.document == null) {
                    return false;
                }
                if (eltern.document.isOpen()) {
                    String url = tempPfad + "EBfliesstext.pdf";
                    outtemp = new ByteArrayOutputStream();
                    eltern.document.getPersistenceService()
                                   .store(outtemp);
                    eltern.document.getPersistenceService()
                                   .export(url, PDFFilter.FILTER);
                    pdfok = true;
                    outtemp.close();
                }
            }
            out.close();
            eltern.document.setModified(false);
            Reha.instance.progressStarten(false);

        } catch (Exception ex) {
            ex.printStackTrace();
            Reha.instance.progressStarten(false);
            return false;
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;

    }

    void insertTextAtCurrentPosition(String xtext) {

        IViewCursor viewCursor = eltern.document.getViewCursorService()
                                                .getViewCursor();
        ITextRange textRange = viewCursor.getStartTextRange();
        textRange.setText(xtext);
        try {
            eltern.officeFrame.setFocus();
            eltern.officeFrame.updateDispatches();
            refreshSize();
            eltern.document.setModified(false);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    public void configureOOOFrame(IOfficeApplication officeApplication, IFrame officeFrame) throws Throwable {
        ILayoutManager layoutManager = officeFrame.getLayoutManager();
        layoutManager.hideAll();
        layoutManager.showElement(ILayoutManager.URL_TOOLBAR_STANDARDBAR);
        layoutManager.showElement(ILayoutManager.URL_TOOLBAR_TEXTOBJECTBAR);
        layoutManager.showElement(ILayoutManager.URL_STATUSBAR);

        officeFrame.disableDispatch(GlobalCommands.CLOSE_DOCUMENT);
        officeFrame.disableDispatch(GlobalCommands.CLOSE_WINDOW);
        officeFrame.disableDispatch(GlobalCommands.QUIT_APPLICATION);
        officeFrame.disableDispatch(GlobalCommands.NEW_MENU);
        officeFrame.disableDispatch(GlobalCommands.NEW_DOCUMENT);
        officeFrame.disableDispatch(GlobalCommands.OPEN_DOCUMENT);
        officeFrame.disableDispatch(GlobalCommands.EDIT_DOCUMENT);
        officeFrame.disableDispatch(GlobalCommands.SAVE);
        officeFrame.disableDispatch(GlobalCommands.MAIL_DOCUMENT);
        officeFrame.disableDispatch(GlobalCommands.OPEN_HYPERLINK_DIALOG);
        officeFrame.disableDispatch(GlobalCommands.EDIT_HYPERLINK);
        officeFrame.disableDispatch(GlobalCommands.OPEN_DRAW_TOOLBAR);
        officeFrame.disableDispatch(GlobalCommands.OPEN_NAVIGATOR);
        officeFrame.disableDispatch(GlobalCommands.OPEN_GALLERY);
        officeFrame.disableDispatch(GlobalCommands.OPEN_DATASOURCES);
        officeFrame.disableDispatch(GlobalCommands.OPEN_STYLE_SHEET);
        officeFrame.disableDispatch(GlobalCommands.OPEN_HELP);
        officeFrame.updateDispatches();
    }

    @Override
    public void rehaEventOccurred(RehaEvent evt) {
        if (evt.getRehaEvent()
               .equals("REHAINTERNAL")) {
            if (evt.getDetails()[1].equals("#DEICONIFIED") && evt.getDetails()[0].contains("Gutachten")) {

                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        // System.out.println("Meldung Deiconified");
                        // refreshSize();
                        // pan.setVisible(true);

                    }
                });

            }
            if (evt.getDetails()[1].equals("#SPEICHERNUNDENDE") && evt.getDetails()[0].contains("Gutachten")) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        textSpeichernInDB(true);
                        // speichernSeite();
                        if (eltern.document != null) {
                            if (eltern.document.isOpen()) {
                                eltern.document.close();
                            }
                        }
                    }
                });
            }
            if (evt.getDetails()[1].equals("#SPEICHERNTEMP") && evt.getDetails()[0].contains("Gutachten")) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        textSpeichernInDB(true);
                        // tempTextSpeichern();

                    }
                });

            }
        }
        if (evt.getDetails()[0].contains("GutachtenFenster")) {
            if (evt.getDetails()[1].equals("#SCHLIESSEN")) {
                // System.out.println("Lösche Listener von Eb3-------------->");
                try {
                    if (outtemp != null) {
                        outtemp.close();
                    }
                    if (eltern != null) {
                        if (eltern.document.isModified()) {

                        }
                        if (eltern.document != null) {
                            if (eltern.document.isOpen()) {
                                eltern.document.close();
                                nativeView = null;
                            }
                        }
                    }

                    // trenneFrame(false);
                    outtemp = null;
                    pdfok = false;
                    // gestartet = false;
                    // tempgespeichert = false;

                } catch (IOException e) {
                    e.printStackTrace();
                }

                this.rEvent.removeRehaEventListener(this);
            }
        }
        if (evt.getRehaEvent()
               .equals("OOFrame")) {
            if (evt.getDetails()[1].equals("#TRENNEN")) {
                // refreshSize();
            }
        }

    }

    public final void refreshSize() {
        /*
         * if (pan == null || framegetrennt) { return; }
         */
        pan.setPreferredSize(new Dimension(pan.getWidth(), pan.getHeight() - 5));

        final Container parent = pan.getParent();
        if (parent instanceof JComponent) {
            ((JComponent) parent).revalidate();
        }

        // ... and just in case, call validate() on the top-level window as well
        final Window window1 = SwingUtilities.getWindowAncestor(nativeView.getParent()
                                                                          .getParent());
        if (window1 != null) {
            window1.validate();
        }
        pan.getLayout()
           .layoutContainer(pan);
        // pan.setVisible(true);
    }

}
