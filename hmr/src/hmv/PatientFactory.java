package hmv;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import core.Adresse;
import core.Akut;
import core.Arzt;
import core.Emailadresse;
import core.Kollege;
import core.Krankenversicherung;
import core.Patient;
import core.Person;
import core.PlanDaten;
import core.Telefonnummer;
import core.Merkmale;

public class PatientFactory {
	Patient patient = new Patient();
	
	public PatientFactory withAnrede(String anrede) {
		patient.setAnrede(anrede);
        return this;
	}
	
	public PatientFactory withTitel(String titel) {
		patient.setTitel(titel);
        return this;
	}
	
	public PatientFactory withNachname(String nachname) {
		patient.setNachname(nachname);
        return this;
	}

	public PatientFactory withVorname(String vorname) {
		patient.setVorname(vorname);
        return this;
	}

	public PatientFactory withWohnadresse(Adresse wohnadresse) {
		patient.setWohnadresse(wohnadresse);
        return this;
	}

	public PatientFactory withAbweichende(Optional<Adresse> abweichende) {
		patient.setAbweichende(abweichende);
        return this;
	}

	public PatientFactory withGeburtstag(LocalDate geburtstag) {
		patient.setGeburtstag(geburtstag);
        return this;
	}
	
	public PatientFactory withPrivat(Optional<Telefonnummer> privat) {
		patient.setPrivat(privat);
        return this;
	}

	public PatientFactory withGeschaeft(Optional<Telefonnummer> geschaeft) {
		patient.setGeschaeft(geschaeft);
        return this;
	}
	
	public PatientFactory withMobil(Optional<Telefonnummer> mobil) {
		patient.setMobil(mobil);
        return this;
	}

	public PatientFactory withEmail(Emailadresse email) {
		patient.setEmail(email);
        return this;
	}
	
	public PatientFactory withAkut(Akut akut) {
		patient.setAkut(akut);
        return this;
	}
	
	public PatientFactory withDaten(PlanDaten daten) {
		patient.setDaten(daten);
        return this;
	}
	
	public PatientFactory withKv(Krankenversicherung kv) {
		patient.setKv(kv);
        return this;
	}


	public PatientFactory withBehandler(Optional<Kollege> behandler) {
		patient.setBehandler(behandler);
        return this;
	}
	
	public PatientFactory withHauptarzt(Optional<Arzt> hauptarzt) {
		patient.setHauptarzt(hauptarzt);
        return this;
	}

	public PatientFactory withAbwAnrede(String abwAnrede) {
		patient.setAbwAnrede(abwAnrede);
        return this;
	}

	public PatientFactory withAbwTitel(String abwTitel) {
		patient.setAbwTitel(abwTitel);
        return this;
	}

	public PatientFactory withAbwN_Name(String abwN_Name) {
		patient.setAbwN_Name(abwN_Name);
        return this;
	}

	public PatientFactory withMerkmale(Merkmale merkmale) {
		patient.setMerkmale(merkmale);
        return this;
	}

	public PatientFactory withPatIntern(int patIntern) {
		patient.setPatIntern(patIntern);
        return this;
	}
	
	public PatientFactory withAnamnese(String anamnese) {
		patient.setAnamnese(anamnese);
        return this;
	}
	
	public PatientFactory withMemo(String memo) {
		patient.setMemo(memo);
        return this;
	}

	public PatientFactory withDb_Id(int db_id) {
		patient.setDb_id(db_id);
        return this;
	}

	public PatientFactory withAnlageDatum(LocalDate anlageDatum) {
		patient.setAnlageDatum(anlageDatum);
        return this;
	}
	
	public PatientFactory withHasAbweichendeAdresse(boolean hasAbweichendeAdresse) {
		patient.setHasAbweichendeAdresse(hasAbweichendeAdresse);
        return this;
	}
	
	public PatientFactory withVertreter(Optional<Person> vertreter) {
		patient.setVertreter(vertreter);
        return this;
	}
	
	public PatientFactory withEntfernung(int entfernung) {
		patient.setEntfernung(entfernung);
        return this;
	}
	
	public PatientFactory withAerzte(String aerzte) {
		patient.setAerzte(aerzte);
        return this;
	}
	
	public PatientFactory withKlinik(String klinik) {
		patient.setKlinik(klinik);
        return this;
	}

	public PatientFactory withU18ignorieren(boolean u18ignorieren) {
		patient.setU18ignorieren(u18ignorieren);
        return this;
	}
	
	public PatientFactory withLand(String land) {
		patient.setLand(land);
        return this;
	}
	
	public PatientFactory withAkutBeh(String akutBeh) {
		patient.setAkutBeh(akutBeh);
        return this;
	}
	
	public PatientFactory withVip_pat(boolean vip_Pat) {
		patient.setVip_Pat(vip_Pat);
        return this;
	}
	
	public PatientFactory withEr_Janein(boolean er_Janein) {
		patient.setEr_Janein(er_Janein);
        return this;
	}
	
	public PatientFactory withEr_dat(LocalDate er_Dat) {
		patient.setEr_Dat(er_Dat);
        return this;
	}
	
	public PatientFactory withHeimbewohn(boolean heimbewohn) {
		patient.setHeimbewohn(heimbewohn);
        return this;
	}
	
	public PatientFactory withNumfrei1(double numfrei1) {
		patient.setNumfrei1(numfrei1);
        return this;
	}
	
	public PatientFactory withNumfrei2(double numfrei2) {
		patient.setNumfrei1(numfrei2);
        return this;
	}

	public PatientFactory withAbschluss(boolean abschluss) {
		patient.setAbschluss(abschluss);
        return this;
	}
	
	public PatientFactory withDatfrei2(LocalDate datfrei2) {
		patient.setDatfrei2(datfrei2);
        return this;
	}
	
	public PatientFactory withCharfrei2(String charfrei2) {
		patient.setCharfrei2(charfrei2);
        return this;
	}
	
	public PatientFactory withBehDauer(int behDauer) {
		patient.setBehDauer(behDauer);
        return this;
	}

	public PatientFactory withBer1(int ber1) {
		patient.setBer1(ber1);
        return this;
	}
	
	public PatientFactory withBer2(int ber2) {
		patient.setBer2(ber2);
        return this;
	}
	
	public PatientFactory withBer3(int ber3) {
		patient.setBer3(ber3);
        return this;
	}
	
	public PatientFactory withBer4(int ber4) {
		patient.setBer4(ber4);
        return this;
	}
	
	public PatientFactory withJahrfrei(String jahrfrei) {
		patient.setJahrfrei(jahrfrei);
        return this;
	}
	
	public PatientFactory withArztliste(List<Arzt> arztListe) {
		patient.setArztListe(arztListe);
        return this;
	}
	

}
