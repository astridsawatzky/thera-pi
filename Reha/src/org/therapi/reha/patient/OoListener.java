package org.therapi.reha.patient;

import ag.ion.bion.officelayer.application.IOfficeApplication;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.event.IDocumentEvent;
import ag.ion.bion.officelayer.event.IDocumentListener;
import ag.ion.bion.officelayer.event.IEvent;

abstract class OoListener implements IDocumentListener {

    public IOfficeApplication officeAplication = null;
    protected String datei;
    protected String id;
    protected boolean geaendert = false;
    protected boolean neu = false;
    public boolean warschoninsave = false;
    protected DokumentationPanel eltern;
    IDocument document;

    public OoListener(IOfficeApplication officeAplication, String xdatei, String xid, DokumentationPanel xeltern) {
        this.officeAplication = officeAplication;
        datei = xdatei;
        geaendert = false;
        id = xid;
        eltern = xeltern;
        if (xid.equals("")) {
            neu = true;
        } else {
            neu = false;
        }
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

    }

    @Override
    public void onLoadFinished(IDocumentEvent arg0) {

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
        //// System.out.println("onSave");

    }

    @Override
    public void onSaveAs(IDocumentEvent arg0) {

    }

    @Override
    public void onSaveAsDone(IDocumentEvent arg0) {

    }

    @Override
    public void onSaveDone(IDocumentEvent arg0) {

        //// System.out.println("Savedone");
        IDocument doc = arg0.getDocument();
        if (doc == null) {
            return;
        }
        String file = arg0.getDocument()
                          .getPersistenceService()
                          .getLocation()
                          .getPath();
        file = file.substring(1)
                   .replace("%20", " ");
        if (datei.equals(file)) {
            geaendert = true;
        }
    }

    private void doUebertragen(String file) {

    }

    @Override
    public void onSaveFinished(IDocumentEvent arg0) {

        //// System.out.println("SaveFinisched");

    }

    @Override
    public void disposing(IEvent arg0) {
        /*
         * if(!warschoninsave){ try { IDocument doc = this.document; if(doc == null){
         * //System.out.println("doc=null"); return; } String file =
         * doc.getLocationURL().toString().replaceAll("file:/", ""); if(geaendert &&
         * datei.equals(file)){ final String xfile = file; final int xid =
         * Integer.parseInt(id);
         *
         * Thread.sleep(50); new Thread(){ public void run(){ int frage =
         * JOptionPane.showConfirmDialog(null, "Die Dokumentationsdatei "
         * +xfile+" wurde geändert\n\nWollen Sie die geänderte Fassung in die Patienten-Dokumentation übernehmen?"
         * , "Wichtige Benutzeranfrage", JOptionPane.YES_NO_OPTION); if(frage ==
         * JOptionPane.YES_OPTION){ geaendert = false; try {
         * Reha.instance.patpanel.dokumentation.setCursor(Reha.instance.wartenCursor);
         * Dokumentation.speichernOoDocs(xid, -1, xfile, -1, null, neu); } catch
         * (Exception e) {
         *
         * e.printStackTrace(); }
         *
         * }
         *
         * //Reha.officeapplication.getDesktopService().removeDocumentListener(this);
         * //System.out.println("Listener entfernt - Datei geändert "+xfile); }
         * }.start(); doc.removeDocumentListener(this); }else if(datei.equals(file) &&
         * !geaendert){ doc.removeDocumentListener(this);
         * //System.out.println("Listener entfernt - Datei nicht geändert"+file); }
         * warschoninsave = true;
         *
         * } catch (ag.ion.bion.officelayer.document.DocumentException e) {
         * e.printStackTrace(); } catch (NumberFormatException e) { e.printStackTrace();
         * } catch (Exception e) { e.printStackTrace(); }
         *
         * }else{ //System.out.println("warschoninsave = "+warschoninsave); }
         */

    }

}
