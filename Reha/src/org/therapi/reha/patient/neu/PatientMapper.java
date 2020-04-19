package org.therapi.reha.patient.neu;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PatientMapper {

    private static final Logger logger =LoggerFactory.getLogger(PatientMapper.class);

    public static Patient of(PatientDTO dto) {


        Patient patient = new Patient();
        patient. anrede =dto.anrede;
        patient. titel =dto.titel;
        patient. nachname =dto.nName;
        patient. vorname =dto.vName;
        patient. wohnadresse = new Adresse("",dto.strasse,new PLZ(dto.plz),dto.ort);
        patient.entfernung =Integer.parseInt( dto.kilometer);
        patient.hasAbweichendeAdresse = dto.abwAdress;
        if (patient.hasAbweichendeAdresse) {
            patient.vertreter = Optional.of(new Person(dto.abwAnrede, dto.abwTitel, dto.abwNName, dto.abwVName));
            patient.abweichende = Optional.of(  new Adresse("", dto.abwStrasse, new PLZ(dto.abwPlz), dto.abwOrt));
        }
        patient. geburtstag = dto.geboren;
        patient. privat =new Telefonnummer(dto.telefonp);
        patient. geschaeft = new Telefonnummer(dto.telefong);
        patient.mobil = new Telefonnummer(dto.telefonm);
        patient. email =new Emailadresse(dto.emailA);
        patient. akut = new Akut(dto.akutDat,dto.akutbis);
        patient. daten = new PlanDaten(dto.termine1,dto.termine2);
        Befreiung befreiung =null;
        if(dto.befAb!=null && dto.befDat!=null) {
             befreiung = new Befreiung(dto.befAb,dto.befDat);

        } else {
            logger.debug("wenigstens ein BefreiungsDatum = null. Von: " +  dto.befAb + " Bis: " + dto.befDat + "befreit ist " + dto.befreit);
        }
        patient. kv = Optional.of( new Krankenversicherung(new Krankenkasse(dto.kvNummer),dto.vNummer,dto.kvStatus,befreiung));
        patient. behandler= Optional.of(new Kollege(dto.therapeut));
        Arzt arzt = new Arzt();
        arzt.id=Integer.parseInt(dto.arztid);
        arzt.arztnummer=new LANR(dto.arztNum);
        arzt.nachname = dto.arzt;
        patient. hauptarzt = Optional.of(arzt);;
        patient.merkmale = new Merkmale(dto.merk1,dto.merk2,dto.merk3,dto.merk4,dto.merk5,dto.merk6);
        return patient;

    };


//    public static PatientDTO of(Patient patient) {
//
//
//        PatientDTO dto = new PatientDTO();
//        patient. anrede =dto.anrede;
//        patient. titel =dto.titel;
//        patient. nachname =dto.nName;
//        patient. vorname =dto.vName;
//        patient. wohnadresse = new Adresse("",dto.strasse,new PLZ(dto.plz),dto.ort);
//        patient.entfernung =Integer.parseInt( dto.kilometer);
//        patient.hasAbweichendeAdresse = dto.abwAdress;
//        if (patient.hasAbweichendeAdresse) {
//            patient.vertreter = Optional.of(new Person(dto.abwAnrede, dto.abwTitel, dto.abwNName, dto.abwVName));
//            patient.abweichende = Optional.of(  new Adresse("", dto.abwStrasse, new PLZ(dto.abwPlz), dto.abwOrt));
//        }
//        patient. geburtstag = dto.geboren;
//        patient. privat =new Telefonnummer(dto.telefonp);
//        patient. geschaeft = new Telefonnummer(dto.telefong);
//        patient.mobil = new Telefonnummer(dto.telefonm);
//        patient. email =new Emailadresse(dto.emailA);
//        patient. akut = new Akut(dto.akutDat,dto.akutbis);
//        patient. daten = new PlanDaten(dto.termine1,dto.termine2);
//        Befreiung befreiung =null;
//        if(dto.befAb!=null && dto.befDat!=null) {
//             befreiung = new Befreiung(dto.befAb,dto.befDat);
//
//        } else {
//            logger.debug("wenigstens ein BefreiungsDatum = null. Von: " +  dto.befAb + " Bis: " + dto.befDat + "befreit ist " + dto.befreit);
//        }
//        patient. kv = Optional.of( new Krankenversicherung(new Krankenkasse(dto.kvNummer),dto.vNummer,dto.kvStatus,befreiung));
//        patient. behandler= Optional.of(new Kollege(dto.therapeut));
//        Arzt arzt = new Arzt();
//        arzt.id=Integer.parseInt(dto.arztid);
//        arzt.arztnummer=new LANR(dto.arztNum);
//        arzt.nachname = dto.arzt;
//        patient. hauptarzt = Optional.of(arzt);;
//        patient.merkmale = new Merkmale(dto.merk1,dto.merk2,dto.merk3,dto.merk4,dto.merk5,dto.merk6);
//        return dto;
//
//    };


    public static Optional<Patient> findbyPat_intern(String pat_intern, String aktIK) {

        return PatientDTO.findbyPat_intern(pat_intern, aktIK)
                         .map(dto -> PatientMapper.of(dto));
    }





//    PatientDTO of(Patient pat) {
//        PatientDTO patient = new PatientDTO();
//        patient.anrede = pat.anrede;
//        patient.titel = pat.titel;
//        patient.nName = pat.nachname;
//        patient.vName = pat.vorname;
//        patient.geboren =  pat.geburtstag;
//        patient.abwAdress = pat.hasAbweichendeAdresse;
//        patient.abwAnrede = pat.abwAnrede;
//        patient.abwTitel = pat.abwTitel;
//        patient.abwNName = pat.abwN_Name;
//        patient.abwVName = pat.abwV_Name;
//        patient.abwStrasse = pat.abweichende.strasse;
//        patient.abwPlz = pat.abweichende.plz.toString();
//        patient.abwOrt = pat.abweichende.ort;
//        patient.kasse = pat.kv.kk.name;
//        patient.kvNummer = pat.kv.kk.ik.digitString();
//        patient.kvStatus = pat.kv.status;
//        patient.vNummer = pat.kv.versicherungsnummer;
//        patient.klinik = pat.klinik;
//        patient.telefonp = pat.privat.nummer;
//        patient.telefong = pat.geschaeft.nummer;
//        patient.telefonm = pat.mobil.nummer;
//        patient.strasse = pat.wohnadresse.strasse;
//        patient.land = pat.land;
//        patient.plz = pat.wohnadresse.plz.plz;
//        patient.ort = pat.wohnadresse.ort;
//        patient.arzt = pat.hauptarzt.nachname;
//        patient.arztNum = pat.hauptarzt.arztnummer.lanr;
//        patient.atel = pat.hauptarzt.telefon.nummer;
//        patient.afax = pat.hauptarzt.fax.nummer;
//        patient.patIntern = pat.patIntern;
//        patient.befreit = pat.kv.befreit.bis.isAfter(LocalDate.now());
//        patient.befDat = Optional.ofNullable(rs.getDate("bef_Dat")).map(d -> d.toLocalDate()).orElse(null);
//        patient.anlDatum = pat.anlageDatum;;
//        patient.akutPat = pat.akut.isAkut();
//        patient.akutDat = pat.akut.seit;
//        patient.akutBeh = pat.akutBeh;
//        patient.termine1 = pat.daten.moeglicheTermine1;
//        patient.termine2 = pat.daten.moeglicheTermine2;
//        patient.vipPat = pat.vip_Pat;
//        patient.erJanein = pat.er_Janein;
//        patient.erDat = Optional.ofNullable(rs.getDate("er_Dat")).map(d -> d.toLocalDate()).orElse(null);
//        patient.befAb = Optional.ofNullable(rs.getDate("bef_Ab")).map(d -> d.toLocalDate()).orElse(null);
//        patient.numfrei1 = pat.numfrei1;
//        patient.numfrei2 = pat.numfrei2;
//        patient.heimbewohn = pat.heimbewohn;
//        patient.abschluss = pat.abschluss;
//        patient.akutbis = pat.akut.bis;
//        patient.datfrei2 = Optional.ofNullable(rs.getDate("datfrei2")).map(d -> d.toLocalDate()).orElse(null);
//        patient.kilometer = String.valueOf(pat.hb.entfernung);
//        patient.charfrei2 = pat.charfrei2;
//        patient.emailA = pat.hauptarzt.email1.adresse;
//        patient.behDauer = pat.behDauer;
//        patient.ber1 = pat.ber1;
//        patient.ber2 = pat.ber2;
//        patient.ber3 = pat.ber3;
//        patient.ber4 = pat.ber4;
//        patient.therapeut = pat.behandler.matchcode;
//        patient.merk6 = pat.merkmale.sechs.gesetzt;
//        patient.merk5 = pat.merkmale.fuenf.gesetzt;
//        patient.merk4 = pat.merkmale.vier.gesetzt;
//        patient.merk3 = pat.merkmale.drei.gesetzt;
//        patient.merk2 = pat.merkmale.zwei.gesetzt;
//        patient.merk1 = pat.merkmale.eins.gesetzt;
//        patient.aerzte = pat.aerzte;
//        patient.patText = pat.memo;
//        patient.anamnese = pat.anamnese;
//        patient.id = pat.db_id;
//        patient.arztid = String.valueOf(pat.hauptarzt.id);
//        patient.kassenid =String.valueOf(pat.kv.kk.id);
//        patient.jahrfrei = pat.jahrfrei;
//        patient.u18ignore = pat.u18ignore;
//        return patient;
//    }

}
/*
 *         PatientDTO patient = new PatientDTO();
        patient.anrede = rs.getString("anrede");
        patient.titel = rs.getString("titel");
        patient.nName = rs.getString("n_Name");
        patient.vName = rs.getString("v_Name");
        patient.geboren = Optional.ofNullable(rs.getDate("geboren")).map(d -> d.toLocalDate()).orElse(null);
        patient.abwAdress = rs.getBoolean("abwAdress");
        patient.abwAnrede = rs.getString("abwAnrede");
        patient.abwTitel = rs.getString("abwTitel");
        patient.abwNName = rs.getString("abwN_Name");
        patient.abwVName = rs.getString("abwV_Name");
        patient.abwStrasse = rs.getString("abwStrasse");
        patient.abwPlz = rs.getString("abwPlz");
        patient.abwOrt = rs.getString("abwOrt");
        patient.kasse = rs.getString("kasse");
        patient.kvNummer = rs.getString("kv_Nummer");
        patient.kvStatus = rs.getString("kv_Status");
        patient.vNummer = rs.getString("v_Nummer");
        patient.klinik = rs.getString("klinik");
        patient.telefonp = rs.getString("telefonp");
        patient.telefong = rs.getString("telefong");
        patient.telefonm = rs.getString("telefonm");
        patient.strasse = rs.getString("strasse");
        patient.land = rs.getString("land");
        patient.plz = rs.getString("plz");
        patient.ort = rs.getString("ort");
        patient.arzt = rs.getString("arzt");
        patient.arztNum = rs.getString("arzt_Num");
        patient.atel = rs.getString("atel");
        patient.afax = rs.getString("afax");
        patient.patIntern = rs.getInt("pat_Intern");
        patient.befreit = rs.getBoolean("befreit");
        patient.befDat = Optional.ofNullable(rs.getDate("bef_Dat")).map(d -> d.toLocalDate()).orElse(null);
        patient.anlDatum = Optional.ofNullable(rs.getDate("anl_Datum")).map(d -> d.toLocalDate()).orElse(null);
        patient.akutPat = rs.getBoolean("akutPat");
        patient.akutDat = Optional.ofNullable(rs.getDate("akutDat")).map(d -> d.toLocalDate()).orElse(null);
        patient.akutBeh = rs.getString("akutBeh");
        patient.termine1 = rs.getString("termine1");
        patient.termine2 = rs.getString("termine2");
        patient.vipPat = rs.getBoolean("vip_Pat");
        patient.erJanein = rs.getBoolean("er_Janein");
        patient.erDat = Optional.ofNullable(rs.getDate("er_Dat")).map(d -> d.toLocalDate()).orElse(null);
        patient.befAb = Optional.ofNullable(rs.getDate("bef_Ab")).map(d -> d.toLocalDate()).orElse(null);
        patient.numfrei1 = rs.getInt("numfrei1");
        patient.numfrei2 = rs.getInt("numfrei2");
        patient.heimbewohn = rs.getBoolean("heimbewohn");
        patient.abschluss = rs.getBoolean("abschluss");
        patient.akutbis = Optional.ofNullable(rs.getDate("akutbis")).map(d -> d.toLocalDate()).orElse(null);
        patient.datfrei2 = Optional.ofNullable(rs.getDate("datfrei2")).map(d -> d.toLocalDate()).orElse(null);
        patient.kilometer = rs.getString("kilometer");
        patient.charfrei2 = rs.getString("charfrei2");
        patient.emailA = rs.getString("emailA");
        patient.behDauer = rs.getInt("behDauer");
        patient.ber1 = rs.getInt("ber1");
        patient.ber2 = rs.getInt("ber2");
        patient.ber3 = rs.getInt("ber3");
        patient.ber4 = rs.getInt("ber4");
        patient.therapeut = rs.getString("therapeut");
        patient.merk6 = rs.getBoolean("merk6");
        patient.merk5 = rs.getBoolean("merk5");
        patient.merk4 = rs.getBoolean("merk4");
        patient.merk3 = rs.getBoolean("merk3");
        patient.merk2 = rs.getBoolean("merk2");
        patient.merk1 = rs.getBoolean("merk1");
        patient.aerzte = rs.getString("aerzte");
        patient.patText = rs.getString("pat_Text");
        patient.anamnese = rs.getString("anamnese");
        patient.id = rs.getInt("id");
        patient.arztid = rs.getString("arztid");
        patient.kassenid = rs.getString("kassenid");
        patient.jahrfrei = rs.getString("jahrfrei");
        patient.u18ignore = rs.getBoolean("u18ignore");
        return patient;
 * */

