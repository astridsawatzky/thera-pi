package org.therapi.reha.patient.neu;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sql.DatenquellenFactory;

public class PatientDTO {

    public PatientDTO() {
    }

     String anrede;
     String titel;
     String nName;
     String vName;
     LocalDate geboren;
     boolean abwAdress;
     String abwAnrede="";
     String abwTitel="";
     String abwNName="";
     String abwVName="";
     String abwStrasse="";
     String abwPlz="";
     String abwOrt="";
     String kasse;
     String kvNummer;
     String kvStatus;
     String vNummer;
     String klinik;
     String telefonp;
     String telefong;
     String telefonm;
     String strasse;
     String land;
     String plz;
     String ort;
     String arzt;
     String arztNum;
     String atel;
     String afax;
    int patIntern;
     boolean befreit;
     LocalDate befDat;
     LocalDate anlDatum;
     boolean akutPat;
     LocalDate akutDat;
     String akutBeh;
     String termine1;
     String termine2;
     boolean vipPat;
     boolean erJanein;
     LocalDate erDat;
     LocalDate befAb;
     double numfrei1;
     double numfrei2;
     boolean heimbewohn;
     boolean abschluss;
     LocalDate akutbis;
     LocalDate datfrei2;
     int kilometer;
     String charfrei2;
     String emailA;
     int behDauer;
     int ber1;
     int ber2;
     int ber3;
     int ber4;
     String therapeut;
     boolean merk6;
     boolean merk5;
     boolean merk4;
     boolean merk3;
     boolean merk2;
     boolean merk1;
     String aerzte;
    String patText;
    String anamnese;
     int id;
     String arztid="";
     String kassenid="";
     String jahrfrei;
     boolean u18ignore;
    private static final Logger logger =LoggerFactory .getLogger(PatientDTO.class);

    public static PatientDTO of(ResultSet rs) throws SQLException {

        PatientDTO patient = new PatientDTO();
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
        patient.kilometer = rs.getInt("kilometer");
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

    }

   static  Optional<PatientDTO> findbyPat_intern(String pat_intern, String aktIK) {
        String sql = " SELECT * FROM pat5 where PAT_INTERN ='" + pat_intern + "';";
        Optional<PatientDTO> result = Optional.empty();
        DatenquellenFactory df = new DatenquellenFactory(aktIK);
        try (Connection con = df.createConnection();
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(sql);) {
            if (rs.next()) {
                result = Optional.of(PatientDTO.of(rs));
            }
        } catch (SQLException e) {
            logger.error("could not get Patient for id = " + pat_intern  , e);
        }
        return result;
    }

@Override
public int hashCode() {
    return Objects.hash(abschluss, abwAdress, abwAnrede, abwNName, abwOrt, abwPlz, abwStrasse, abwTitel, abwVName,
            aerzte, afax, akutBeh, akutDat, akutPat, akutbis, anamnese, anlDatum, anrede, arzt, arztNum, arztid, atel,
            befAb, befDat, befreit, behDauer, ber1, ber2, ber3, ber4, charfrei2, datfrei2, emailA, erDat, erJanein,
            geboren, heimbewohn, id, jahrfrei, kasse, kassenid, kilometer, klinik, kvNummer, kvStatus, land, merk1,
            merk2, merk3, merk4, merk5, merk6, nName, numfrei1, numfrei2, ort, patIntern, patText, plz, strasse,
            telefong, telefonm, telefonp, termine1, termine2, therapeut, titel, u18ignore, vName, vNummer, vipPat);
}

@Override
public boolean equals(Object obj) {
    if (this == obj)
        return true;
    if (obj == null)
        return false;
    if (getClass() != obj.getClass())
        return false;
    PatientDTO other = (PatientDTO) obj;
    return abschluss == other.abschluss && abwAdress == other.abwAdress && Objects.equals(abwAnrede, other.abwAnrede)
            && Objects.equals(abwNName, other.abwNName) && Objects.equals(abwOrt, other.abwOrt)
            && Objects.equals(abwPlz, other.abwPlz) && Objects.equals(abwStrasse, other.abwStrasse)
            && Objects.equals(abwTitel, other.abwTitel) && Objects.equals(abwVName, other.abwVName)
            && Objects.equals(aerzte, other.aerzte) && Objects.equals(afax, other.afax)
            && Objects.equals(akutBeh, other.akutBeh) && Objects.equals(akutDat, other.akutDat)
            && akutPat == other.akutPat && Objects.equals(akutbis, other.akutbis)

            && Objects.equals(anamnese, other.anamnese) && Objects.equals(anlDatum, other.anlDatum)
            && Objects.equals(anrede, other.anrede) && Objects.equals(arzt, other.arzt)
            && Objects.equals(arztNum, other.arztNum)
          && Objects.equals(arztid, other.arztid)
            && Objects.equals(atel, other.atel) && Objects.equals(befAb, other.befAb)

                        && Objects.equals(befDat, other.befDat) && befreit == other.befreit && behDauer == other.behDauer
            && ber1 == other.ber1 && ber2 == other.ber2 && ber3 == other.ber3 && ber4 == other.ber4
            && Objects.equals(charfrei2, other.charfrei2) && Objects.equals(datfrei2, other.datfrei2)
            && Objects.equals(emailA, other.emailA) && Objects.equals(erDat, other.erDat) && erJanein == other.erJanein
            && Objects.equals(geboren, other.geboren) && heimbewohn == other.heimbewohn && id == other.id
            && Objects.equals(jahrfrei, other.jahrfrei)
            && Objects.equals(kassenid, other.kassenid) && Objects.equals(kilometer, other.kilometer)
            && Objects.equals(klinik, other.klinik) && Objects.equals(kvNummer, other.kvNummer)
            && Objects.equals(kvStatus, other.kvStatus) && Objects.equals(land, other.land) && merk1 == other.merk1
            && merk2 == other.merk2 && merk3 == other.merk3 && merk4 == other.merk4 && merk5 == other.merk5
            && merk6 == other.merk6 && Objects.equals(nName, other.nName)
            && Double.doubleToLongBits(numfrei1) == Double.doubleToLongBits(other.numfrei1)
            && Double.doubleToLongBits(numfrei2) == Double.doubleToLongBits(other.numfrei2)
            && Objects.equals(ort, other.ort) && patIntern == other.patIntern && Objects.equals(patText, other.patText)
            && Objects.equals(plz, other.plz) && Objects.equals(strasse, other.strasse)
            && Objects.equals(telefong, other.telefong) && Objects.equals(telefonm, other.telefonm)
            && Objects.equals(telefonp, other.telefonp) && Objects.equals(termine1, other.termine1)
            && Objects.equals(termine2, other.termine2) && Objects.equals(therapeut, other.therapeut)
            && Objects.equals(titel, other.titel) && u18ignore == other.u18ignore && Objects.equals(vName, other.vName)
            && Objects.equals(vNummer, other.vNummer) && vipPat == other.vipPat
            ;
}

@Override
public String toString() {
    return "PatientDTO [anrede=" + anrede + ", titel=" + titel + ", nName=" + nName + ", vName=" + vName + ", geboren="
            + geboren + ", abwAdress=" + abwAdress + ", abwAnrede=" + abwAnrede + ", abwTitel=" + abwTitel
            + ", abwNName=" + abwNName + ", abwVName=" + abwVName + ", abwStrasse=" + abwStrasse + ", abwPlz=" + abwPlz
            + ", abwOrt=" + abwOrt + ", kvNummer=" + kvNummer + ", kvStatus=" + kvStatus
            + ", vNummer=" + vNummer + ", klinik=" + klinik + ", telefonp=" + telefonp + ", telefong=" + telefong
            + ", telefonm=" + telefonm + ", strasse=" + strasse + ", land=" + land + ", plz=" + plz + ", ort=" + ort
            + ", arzt=" + arzt + ", arztNum=" + arztNum + ", atel=" + atel + ", afax=" + afax + ", patIntern="
            + patIntern + ", befreit=" + befreit + ", befDat=" + befDat + ", anlDatum=" + anlDatum + ", akutPat="
            + akutPat + ", akutDat=" + akutDat + ", akutBeh=" + akutBeh + ", termine1=" + termine1 + ", termine2="
            + termine2 + ", vipPat=" + vipPat + ", erJanein=" + erJanein + ", erDat=" + erDat + ", befAb=" + befAb
            + ", numfrei1=" + numfrei1 + ", numfrei2=" + numfrei2 + ", heimbewohn=" + heimbewohn + ", abschluss="
            + abschluss + ", akutbis=" + akutbis + ", datfrei2=" + datfrei2 + ", kilometer=" + kilometer
            + ", charfrei2=" + charfrei2 + ", emailA=" + emailA + ", behDauer=" + behDauer + ", ber1=" + ber1
            + ", ber2=" + ber2 + ", ber3=" + ber3 + ", ber4=" + ber4 + ", therapeut=" + therapeut + ", merk6=" + merk6
            + ", merk5=" + merk5 + ", merk4=" + merk4 + ", merk3=" + merk3 + ", merk2=" + merk2 + ", merk1=" + merk1
            + ", aerzte=" + aerzte + ", patText=" + patText + ", anamnese=" + anamnese + ", id=" + id + ", arztid="
            + arztid + ", kassenid=" + kassenid + ", jahrfrei=" + jahrfrei + ", u18ignore=" + u18ignore + "]";
}



}
