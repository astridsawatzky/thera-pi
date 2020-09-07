package core;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Patient {

	public String anrede;
	public String titel;
	public String nachname;
	public String vorname;
	public Adresse wohnadresse;
	public Optional<Adresse> abweichende = Optional.empty();
	public LocalDate geburtstag;
	public Optional<Telefonnummer> privat = Optional.empty();
	public Optional<Telefonnummer> geschaeft = Optional.empty();
	public Optional<Telefonnummer> mobil = Optional.empty();
	public Emailadresse email;
	public Akut akut = new Akut(false, null, null);
	public PlanDaten daten = new PlanDaten("", "");
	public Krankenversicherung kv = new Krankenversicherung(Optional.empty(), null, VersichertenStatus.INVALID,
			Optional.empty());
	public Optional<Kollege> behandler = Optional.empty();
	public Optional<Arzt> hauptarzt = Optional.empty();
	public String abwAnrede;
	public String abwTitel;
	public String abwN_Name;
	public String abwV_Name;
	public Merkmale merkmale = new Merkmale();
	public int patIntern;
	public String anamnese = "";
	public String memo = "";

	public int db_id;
	public LocalDate anlageDatum;
	public boolean hasAbweichendeAdresse;
	public Optional<Person> vertreter = Optional.empty();
	public int entfernung;
	public String aerzte;
	public String klinik;
	public boolean u18ignorieren;
	// unused ?
	public String land;
	public String akutBeh;
	public boolean vip_Pat;
	public boolean er_Janein;
	public LocalDate er_Dat;
	public boolean heimbewohn;
	public double numfrei1;
	public double numfrei2;
	public boolean abschluss;
	public LocalDate datfrei2;
	public String charfrei2;
	public int behDauer;
	public int ber1;
	public int ber2;
	public int ber3;
	public int ber4;
	public String jahrfrei;
	public List<Arzt> arztListe = Collections.emptyList();

	public Patient(Adresse adresse) {
		wohnadresse = adresse;

	}

	public boolean istHeimbewohner() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(abschluss, abwAnrede, abwN_Name, abwTitel, abwV_Name, abweichende, aerzte, akut, akutBeh,
				anamnese, anlageDatum, anrede, behDauer, behandler, ber1, ber2, ber3, ber4, charfrei2, daten, 2, db_id,
				email, entfernung, er_Dat, er_Janein, geburtstag, geschaeft, hasAbweichendeAdresse, hauptarzt,
				heimbewohn, jahrfrei, klinik, kv, land, memo, merkmale, mobil, nachname, numfrei1, numfrei2, patIntern,
				privat, titel, u18ignorieren, vertreter, vip_Pat, vorname, wohnadresse);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Patient other = (Patient) obj;
		return abschluss == other.abschluss && Objects.equals(abwAnrede, other.abwAnrede)
				&& Objects.equals(abwN_Name, other.abwN_Name) && Objects.equals(abwTitel, other.abwTitel)
				&& Objects.equals(abwV_Name, other.abwV_Name) && Objects.equals(abweichende, other.abweichende)
				&& Objects.equals(aerzte, other.aerzte) && Objects.equals(akut, other.akut)
				&& Objects.equals(akutBeh, other.akutBeh) && Objects.equals(anamnese, other.anamnese)
				&& Objects.equals(anlageDatum, other.anlageDatum) && Objects.equals(anrede, other.anrede)
				&& behDauer == other.behDauer && Objects.equals(behandler, other.behandler) && ber1 == other.ber1
				&& ber2 == other.ber2 && ber3 == other.ber3 && ber4 == other.ber4
				&& Objects.equals(charfrei2, other.charfrei2) && Objects.equals(daten, other.daten)
				&& Objects.equals(datfrei2, other.datfrei2) && db_id == other.db_id
				&& Objects.equals(email, other.email) && entfernung == other.entfernung
				&& Objects.equals(er_Dat, other.er_Dat) && er_Janein == other.er_Janein
				&& Objects.equals(geburtstag, other.geburtstag) && Objects.equals(geschaeft, other.geschaeft)
				&& hasAbweichendeAdresse == other.hasAbweichendeAdresse && Objects.equals(hauptarzt, other.hauptarzt)
				&& heimbewohn == other.heimbewohn && Objects.equals(jahrfrei, other.jahrfrei)
				&& Objects.equals(klinik, other.klinik) && Objects.equals(kv, other.kv)
				&& Objects.equals(land, other.land) && Objects.equals(memo, other.memo)
				&& Objects.equals(merkmale, other.merkmale) && Objects.equals(mobil, other.mobil)
				&& Objects.equals(nachname, other.nachname)
				&& Double.doubleToLongBits(numfrei1) == Double.doubleToLongBits(other.numfrei1)
				&& Double.doubleToLongBits(numfrei2) == Double.doubleToLongBits(other.numfrei2)
				&& patIntern == other.patIntern && Objects.equals(privat, other.privat)
				&& Objects.equals(titel, other.titel) && u18ignorieren == other.u18ignorieren
				&& Objects.equals(vertreter, other.vertreter) && vip_Pat == other.vip_Pat
				&& Objects.equals(vorname, other.vorname) && Objects.equals(wohnadresse, other.wohnadresse);
	}

	@Override
	public String toString() {
		return "Patient [anrede=" + anrede + ", titel=" + titel + ", nachname=" + nachname + ", vorname=" + vorname
				+ ", wohnadresse=" + wohnadresse + ", abweichende=" + abweichende + ", geburtstag=" + geburtstag
				+ ", privat=" + privat + ", geschaeft=" + geschaeft + ", mobil=" + mobil + ", email=" + email
				+ ", akut=" + akut + ", daten=" + daten + ", kv=" + kv + ", behandler=" + behandler + ", hauptarzt="
				+ hauptarzt + ", abwAnrede=" + abwAnrede + ", abwTitel=" + abwTitel + ", abwN_Name=" + abwN_Name
				+ ", abwV_Name=" + abwV_Name + ", merkmale=" + merkmale + ", patIntern=" + patIntern + ", anamnese="
				+ anamnese + ", memo=" + memo + ", db_id=" + db_id + ", anlageDatum=" + anlageDatum
				+ ", hasAbweichendeAdresse=" + hasAbweichendeAdresse + ", vertreter=" + vertreter + ", entfernung="
				+ entfernung + ", aerzte=" + aerzte + ", klinik=" + klinik + ", u18ignorieren=" + u18ignorieren
				+ ", land=" + land + ", akutBeh=" + akutBeh + ", vip_Pat=" + vip_Pat + ", er_Janein=" + er_Janein
				+ ", er_Dat=" + er_Dat + ", heimbewohn=" + heimbewohn + ", numfrei1=" + numfrei1 + ", numfrei2="
				+ numfrei2 + ", abschluss=" + abschluss + ", datfrei=" + datfrei2 + ", charfrei2=" + charfrei2
				+ ", behDauer=" + behDauer + ", ber1=" + ber1 + ", ber2=" + ber2 + ", ber3=" + ber3 + ", ber4=" + ber4
				+ ", jahrfrei=" + jahrfrei + "]";
	}

	public boolean hatBefreiung(LocalDate date) {
		if (kv.befreit.isPresent()) {
			Befreiung befreiung = kv.befreit.get();
			return befreiung.istbefreit(date);
		}
		return false;
	}

	/*
	 * Folgender Absatz von Methoden für die PatientFactory Klasse inserted for
	 * /hmr/src/hmv/PatientFactory.java by Marvin
	 */

	public Patient() {
		// TODO Auto-generated constructor stub
	}

	public void setAnrede(String anrede) {
		this.anrede = anrede;
	}

	public void setTitel(String titel) {
		this.titel = titel;
	}

	public void setNachname(String nachname) {
		this.nachname = nachname;
	}

	public void setVorname(String vorname) {
		this.vorname = vorname;
	}

	public void setWohnadresse(Adresse wohnadresse) {
		this.wohnadresse = wohnadresse;
	}

	public void setAbweichende(Optional<Adresse> abweichende) {
		this.abweichende = abweichende;
	}

	public void setGeburtstag(LocalDate geburtstag) {
		this.geburtstag = geburtstag;
	}

	public void setPrivat(Optional<Telefonnummer> privat) {
		this.privat = privat;
	}

	public void setGeschaeft(Optional<Telefonnummer> geschaeft) {
		this.geschaeft = geschaeft;
	}

	public void setMobil(Optional<Telefonnummer> mobil) {
		this.mobil = mobil;
	}

	public void setEmail(Emailadresse email) {
		this.email = email;
	}

	public void setAkut(Akut akut) {
		this.akut = akut;
	}

	public void setDaten(PlanDaten daten) {
		this.daten = daten;
	}

	public void setKv(Krankenversicherung kv) {
		this.kv = kv;
	}

	public void setBehandler(Optional<Kollege> behandler) {
		this.behandler = behandler;
	}

	public void setHauptarzt(Optional<Arzt> hauptarzt) {
		this.hauptarzt = hauptarzt;
	}

	public void setAbwAnrede(String abwAnrede) {
		this.abwAnrede = abwAnrede;
	}

	public void setAbwTitel(String abwTitel) {
		this.abwTitel = abwTitel;
	}

	public void setAbwN_Name(String abwN_Name) {
		this.abwN_Name = abwN_Name;
	}

	public void setAbwV_Name(String abwV_Name) {
		this.abwV_Name = abwV_Name;
	}

	public void setMerkmale(Merkmale merkmale) {
		this.merkmale = merkmale;
	}

	public void setPatIntern(int patIntern) {
		this.patIntern = patIntern;
	}

	public void setAnamnese(String anamnese) {
		this.anamnese = anamnese;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public void setDb_id(int db_id) {
		this.db_id = db_id;
	}

	public void setAnlageDatum(LocalDate anlageDatum) {
		this.anlageDatum = anlageDatum;
	}

	public void setHasAbweichendeAdresse(boolean hasAbweichendeAdresse) {
		this.hasAbweichendeAdresse = hasAbweichendeAdresse;
	}

	public void setVertreter(Optional<Person> vertreter) {
		this.vertreter = vertreter;
	}

	public void setEntfernung(int entfernung) {
		this.entfernung = entfernung;
	}

	public void setAerzte(String aerzte) {
		this.aerzte = aerzte;
	}

	public void setKlinik(String klinik) {
		this.klinik = klinik;
	}

	public void setU18ignorieren(boolean u18ignorieren) {
		this.u18ignorieren = u18ignorieren;
	}

	public void setLand(String land) {
		this.land = land;
	}

	public void setAkutBeh(String akutBeh) {
		this.akutBeh = akutBeh;
	}

	public void setVip_Pat(boolean vip_Pat) {
		this.vip_Pat = vip_Pat;
	}

	public void setEr_Janein(boolean er_Janein) {
		this.er_Janein = er_Janein;
	}

	public void setEr_Dat(LocalDate er_Dat) {
		this.er_Dat = er_Dat;
	}

	public void setHeimbewohn(boolean heimbewohn) {
		this.heimbewohn = heimbewohn;
	}

	public void setNumfrei1(double numfrei1) {
		this.numfrei1 = numfrei1;
	}

	public void setNumfrei2(double numfrei2) {
		this.numfrei2 = numfrei2;
	}

	public void setAbschluss(boolean abschluss) {
		this.abschluss = abschluss;
	}

	public void setDatfrei2(LocalDate datfrei2) {
		this.datfrei2 = datfrei2;
	}

	public void setCharfrei2(String charfrei2) {
		this.charfrei2 = charfrei2;
	}

	public void setBehDauer(int behDauer) {
		this.behDauer = behDauer;
	}

	public void setBer1(int ber1) {
		this.ber1 = ber1;
	}

	public void setBer2(int ber2) {
		this.ber2 = ber2;
	}

	public void setBer3(int ber3) {
		this.ber3 = ber3;
	}

	public void setBer4(int ber4) {
		this.ber4 = ber4;
	}

	public void setJahrfrei(String jahrfrei) {
		this.jahrfrei = jahrfrei;
	}

	public void setArztListe(List<Arzt> arztListe) {
		this.arztListe = arztListe;
	}

}
