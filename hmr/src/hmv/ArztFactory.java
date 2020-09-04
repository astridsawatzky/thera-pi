package hmv;

import core.Adresse;
import core.Arzt;
import core.LANR;

public class ArztFactory {
    Arzt arzt = new Arzt();

    public ArztFactory withId(int id) {
        arzt.setId(id);
        return this;
    }

    public ArztFactory withAnrede(String anrede) {
        arzt.setAnrede(anrede);
        return this;
    }

    public ArztFactory withTitel(String titel) {
        arzt.setTitel(titel);
        return this;
    }

    public ArztFactory withVorname(String vorname) {
        arzt.setVorname(vorname);
        return this;
    }

    public ArztFactory withNachname(String nachname) {
        arzt.setNachname(nachname);
        return this;
    }

    public ArztFactory withPraxis(Adresse praxis) {
        arzt.setPraxis(praxis);
        return this;
    }

    public ArztFactory withArztnummer(LANR arztnummer) {
        arzt.setArztnummer(arztnummer);
        return this;
    }

    public ArztFactory withFacharzt(String facharzt) {
        arzt.setFacharzt(facharzt);
        return this;
    }

    public ArztFactory withTelefon(String telefon) {
        arzt.setTelefon(telefon);
        return this;
    }

    public ArztFactory withFax(String fax) {
        arzt.setFax(fax);
        return this;
    }

    public ArztFactory withKlinik(String klinik) {
        arzt.setKlinik(klinik);
        return this;
    }

    public ArztFactory withMtext(String mtext) {
        arzt.setMtext(mtext);
        return this;
    }

    public ArztFactory withEmail1(String email1) {
        arzt.setEmail1(email1);
        return this;
    }

    public ArztFactory withEmail2(String email2) {
        arzt.setEmail2(email2);
        return this;
    }

    public ArztFactory withBsnr(String bsnr) {
        arzt.setBsnr(bsnr);
        return this;
    }

    public ArztFactory withMatchcode(String matchcode) {
        arzt.setMatchcode(matchcode);
        return this;
    }

    public Arzt build() {



        return arzt;

    }





}
