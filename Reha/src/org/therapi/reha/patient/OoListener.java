package org.therapi.reha.patient;

import ag.ion.bion.officelayer.application.IOfficeApplication;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.event.DocumentAdapter;
import ag.ion.bion.officelayer.event.IDocumentEvent;

abstract class OoListener extends DocumentAdapter {
    public IOfficeApplication officeAplication;
    protected String datei;
    protected boolean geaendert;
    public boolean warschoninsave;
    protected DokumentationPanel eltern;
    IDocument document;

    public OoListener(IOfficeApplication officeAplication, String xdatei, DokumentationPanel xeltern) {
        this.officeAplication = officeAplication;
        datei = xdatei;
        geaendert = false;
        eltern = xeltern;
    }

    @Override
    public void onSaveDone(IDocumentEvent arg0) {
        IDocument doc = arg0.getDocument();
        if (doc == null) {
            return;
        }

        geaendert = true;
    }
}
