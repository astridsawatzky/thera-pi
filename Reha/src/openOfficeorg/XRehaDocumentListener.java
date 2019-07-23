package openOfficeorg;

import ag.ion.bion.officelayer.application.IOfficeApplication;
import ag.ion.bion.officelayer.event.DocumentAdapter;

public class XRehaDocumentListener extends DocumentAdapter {
    public IOfficeApplication officeAplication;

    public XRehaDocumentListener(IOfficeApplication officeAplication) {
        this.officeAplication = officeAplication;
    }
}
