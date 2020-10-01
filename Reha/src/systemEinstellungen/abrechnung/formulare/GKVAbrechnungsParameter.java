package systemEinstellungen.abrechnung.formulare;

public class GKVAbrechnungsParameter {
    final FormularParameter taxierung;
    final GKVFormularParameter gkv;
    final FormularParameter privat;
    final FormularParameter bg;
    public FormularParameter rgr ;
    final boolean direktAusdruck;
    final boolean askBefore302Mail;

    public GKVAbrechnungsParameter(FormularParameter taxierung, FormularParameter gkv, FormularParameter privat,
            FormularParameter bg, FormularParameter rgr, boolean direktAusdruck, boolean askBefore302Mail) {
        this.taxierung = taxierung;
        this.gkv =  (GKVFormularParameter) gkv;
        this.privat = privat;
        this.bg = bg;
        this.rgr = rgr;
        this.direktAusdruck = direktAusdruck;
        this.askBefore302Mail = askBefore302Mail;
    }
}
