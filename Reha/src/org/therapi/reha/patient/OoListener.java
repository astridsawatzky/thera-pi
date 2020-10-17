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
    IDocument document;

    public OoListener(IOfficeApplication officeAplication, String xdatei) {
        this.officeAplication = officeAplication;
        datei = xdatei;
        geaendert = false;
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
