package systemEinstellungen.abrechnung.formulare;

class GKVFormularParameter extends FormularParameter {
    final boolean begleitzettelOnly;

    public GKVFormularParameter(FormularParameter params, boolean begleitZettelonly) {
        super(params.printerEinstellungsAusVorlage, params.template, params.printer, params.numberOfPrintOuts);
        begleitzettelOnly = begleitZettelonly;
    }
}