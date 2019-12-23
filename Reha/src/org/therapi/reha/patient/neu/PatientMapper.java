package org.therapi.reha.patient.neu;



public class PatientMapper {

    public Patient of(PatientDTO dto) {


        Patient patient = new Patient();
        patient. anrede =dto.anrede;
        patient. titel =dto.titel;
        patient. nachname =dto.nName;
        patient. vorname =dto.vName;
        patient. wohnadresse = new Adresse("",dto.strasse,new PLZ(dto.plz),dto.ort);
        patient. geburtstag = dto.geboren;
        patient. privat =new Telefonnummer(dto.telefonp);
        patient. geschaeft = new Telefonnummer(dto.telefong);
        patient.mobil = new Telefonnummer(dto.telefonm);
        patient. email =new Emailadresse(dto.emailA);
        patient. akut = new Akut(dto.akutDat,dto.akutbis);
        patient. daten = new PlanDaten(dto.termine1,dto.termine2);

        Befreiung befreiung = new Befreiung(dto.befAb,dto.befDat);
        patient. kv = new Krankenversicherung(new Krankenkasse(dto.kassenid),dto.vNummer,dto.kvStatus,befreiung);
        patient. behandler= new Kollege(dto.therapeut);
        patient. hauptarzt = new Arzt(dto.arztid,dto.arztNum, dto.arzt);
        return patient;

    };

    PatientDTO of(Patient pat) {
        PatientDTO dto = new PatientDTO();


        return dto;
    }

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

