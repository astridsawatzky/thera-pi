package org.therapi.reha.patient.neu;

import java.time.LocalDate;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PatientMapper {

    private static final Logger logger =LoggerFactory.getLogger(PatientMapper.class);

    public static Patient of(PatientDTO dto) {


        Patient patient = new Patient(new Adresse("",dto.strasse,dto.plz,dto.ort));
        patient. anrede =dto.anrede;
        patient. titel =dto.titel;
        patient. nachname =dto.nName;
        patient. vorname =dto.vName;
        if(dto.kilometer!=null &&  !dto.kilometer.isEmpty()) {
            patient.entfernung = Integer.parseInt( dto.kilometer);
        }
        patient.hasAbweichendeAdresse = dto.abwAdress;
        if (patient.hasAbweichendeAdresse) {
            patient.vertreter = Optional.of(new Person(dto.abwAnrede, dto.abwTitel, dto.abwNName, dto.abwVName));
            patient.abweichende = Optional.of(  new Adresse("", dto.abwStrasse, dto.abwPlz, dto.abwOrt));
        }
        patient. geburtstag = dto.geboren;
        if(dto.telefonp!=null) {
            patient. privat =Optional.of(new Telefonnummer(dto.telefonp));
        }
        if(dto.telefong!=null) {
            patient. geschaeft =Optional.of( new Telefonnummer(dto.telefong));
        }
        if(dto.telefonm!=null) {
            patient.mobil =Optional.of( new Telefonnummer(dto.telefonm));
        }

        if(dto.emailA!=null) {
            patient. email =new Emailadresse(dto.emailA);
        }
        patient. akut = new Akut(dto.akutDat,dto.akutbis);
        patient. daten = new PlanDaten(dto.termine1,dto.termine2);
        Befreiung befreiung =null;
        if(dto.befAb!=null && dto.befDat!=null) {
             befreiung = new Befreiung(dto.befAb,dto.befDat);

        } else if (dto.befreit){

            logger.debug("wenigstens ein BefreiungsDatum = null. Von: " +  dto.befAb + " Bis: " + dto.befDat + "befreit ist " + dto.befreit);
        }

        try {
            patient. kv = Optional.of( new Krankenversicherung(new Krankenkasse(dto.kvNummer),dto.vNummer,dto.kvStatus,befreiung));
        } catch (Exception e1) {
            patient.kv = Optional.empty();
            logger.error("KV konnte nicht angelegt werden",e1);
        }

        if(dto.therapeut!=null) {
            patient. behandler= Optional.of(new Kollege(dto.therapeut));
        }



        if(!dto.arztid.isEmpty()) {
        Arzt arzt = new Arzt();
            arzt.id = Integer.parseInt(dto.arztid);
        arzt.arztnummer=new LANR(dto.arztNum);
        arzt.nachname = dto.arzt;
        patient. hauptarzt = Optional.of(arzt);;
        }
        patient.merkmale = new Merkmale(dto.merk1,dto.merk2,dto.merk3,dto.merk4,dto.merk5,dto.merk6);

        return patient;

    };


//    public static PatientDTO of(Patient patient) {
//
//
//        PatientDTO dto = new PatientDTO();
//
//        patient. anrede =dto.anrede;
//        patient. titel =dto.titel;
//        patient. nachname =dto.nName;
//        patient. vorname =dto.vName;
//        patient. wohnadresse = new Adresse("",dto.strasse,new PLZ(dto.plz),dto.ort);
//        dto.kilometer = String.valueOf(  patient.entfernung);
//
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
//        if(patient.kv.isPresent()) {
//            dto.kvNummer = patient.kv.get().kk.ik.digitString();
//            dto.vNummer = patient.kv.get().versicherungsnummer;
//            dto.kvStatus = patient.kv.get().getStatus();
//        }
//        patient. behandler= Optional.of(new Kollege(dto.therapeut));
//        Arzt arzt = new Arzt();
//       dto.arztid = String.valueOf( arzt.id);
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





    static PatientDTO of(Patient pat) {
        PatientDTO patientdto = new PatientDTO();
        patientdto.anrede = pat.anrede;
        patientdto.titel = pat.titel;
        patientdto.nName = pat.nachname;
        patientdto.vName = pat.vorname;
        patientdto.geboren =  pat.geburtstag;

        patientdto.abwAdress = pat.hasAbweichendeAdresse;
        patientdto.abwAnrede = pat.abwAnrede;
        patientdto.abwTitel = pat.abwTitel;
        patientdto.abwNName = pat.abwN_Name;
        patientdto.abwVName = pat.abwV_Name;

        if (pat.abweichende.isPresent()) {
            Adresse abWeichendeAdresse = pat.abweichende.get();
            patientdto.abwStrasse = abWeichendeAdresse.strasse;
            patientdto.abwPlz = abWeichendeAdresse.plz.toString();
            patientdto.abwOrt = abWeichendeAdresse.ort;
        }
        if (pat.kv.isPresent()) {
            Krankenversicherung krankenversicherung = pat.kv.get();
            patientdto.kasse = krankenversicherung.kk.name;
            patientdto.kvNummer = krankenversicherung.kk.ik.digitString();
            patientdto.kvStatus = krankenversicherung.status;
            patientdto.vNummer = krankenversicherung.versicherungsnummer;
            patientdto.kassenid = String.valueOf(krankenversicherung.kk.id);
            if (krankenversicherung.befreit.isPresent()) {
                Befreiung befreiung = krankenversicherung.befreit.get();
                patientdto.befreit = befreiung.bis.isAfter(LocalDate.now());
                patientdto.befDat = befreiung.bis;
                patientdto.befAb = befreiung.von;
            }
        }
        patientdto.klinik = pat.klinik;


        if(pat.privat.isPresent()) {
            patientdto.telefonp = pat.privat.get().nummer;
        }
        if(pat.geschaeft.isPresent()) {
            patientdto.telefong =pat.geschaeft.get().nummer;
        }
        if(pat.mobil.isPresent()) {
            patientdto.telefonm =pat.mobil.get().nummer;
        }


        patientdto.strasse = pat.wohnadresse.strasse;
        patientdto.land = pat.land;
        patientdto.plz = pat.wohnadresse.plz;
        patientdto.ort = pat.wohnadresse.ort;
        if (pat.hauptarzt.isPresent()) {
            Arzt hauptArzt = pat.hauptarzt.get();
            patientdto.arzt = hauptArzt.nachname;
            patientdto.arztNum = hauptArzt.arztnummer.lanr;

            if (hauptArzt.telefon.isPresent()) {
            patientdto.atel = hauptArzt.telefon.get().nummer;
            }
            if(hauptArzt.fax.isPresent())
            patientdto.afax = hauptArzt.fax.get().nummer;
            patientdto.emailA = hauptArzt.email1.adresse;
            patientdto.arztid = String.valueOf(hauptArzt.id);
        }
        patientdto.patIntern = pat.patIntern;


        patientdto.anlDatum = pat.anlageDatum;;
        patientdto.akutPat = pat.akut.isAkut();
        patientdto.akutDat = pat.akut.seit;
        patientdto.akutBeh = pat.akutBeh;
        patientdto.termine1 = pat.daten.moeglicheTermine1;
        patientdto.termine2 = pat.daten.moeglicheTermine2;
        patientdto.vipPat = pat.vip_Pat;
        patientdto.erJanein = pat.er_Janein;
        patientdto.erDat = pat.er_Dat;

        patientdto.numfrei1 = pat.numfrei1;
        patientdto.numfrei2 = pat.numfrei2;
        patientdto.heimbewohn = pat.heimbewohn;
        patientdto.abschluss = pat.abschluss;
        patientdto.akutbis = pat.akut.bis;
        patientdto.datfrei2 = pat.datfrei;
        patientdto.kilometer = pat.entfernung==0?null: String.valueOf(pat.entfernung);
        patientdto.charfrei2 = pat.charfrei2;

        patientdto.behDauer = pat.behDauer;
        patientdto.ber1 = pat.ber1;
        patientdto.ber2 = pat.ber2;
        patientdto.ber3 = pat.ber3;
        patientdto.ber4 = pat.ber4;
        if(pat.behandler.isPresent()) {
            patientdto.therapeut = pat.behandler.get().matchcode;
        }
        patientdto.merk6 = pat.merkmale.sechs();
        patientdto.merk5 = pat.merkmale.fuenf();
        patientdto.merk4 = pat.merkmale.vier();
        patientdto.merk3 = pat.merkmale.drei();
        patientdto.merk2 = pat.merkmale.zwei();
        patientdto.merk1 = pat.merkmale.eins();
        patientdto.aerzte = pat.aerzte;
        patientdto.patText = pat.memo.isEmpty()?null:pat.memo;
        patientdto.anamnese = pat.anamnese.isEmpty()?null:pat.anamnese;
        patientdto.id = pat.db_id;


        patientdto.jahrfrei = pat.jahrfrei;
        patientdto.u18ignore = pat.u18ignorieren;
        return patientdto;
    }

}
