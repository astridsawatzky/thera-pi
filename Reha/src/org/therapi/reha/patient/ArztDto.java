package org.therapi.reha.patient;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import core.Adresse;
import core.Arzt;
import core.LANR;
import mandant.IK;
import sql.DatenquellenFactory;

public class ArztDto {

    private static Logger logger = LoggerFactory.getLogger(ArztDto.class);
    String anrede;
    String titel;
    String nachname;
    String vorname;
    String strasse;
    String plz;
    String ort;
    String facharzt;
    String telefon;
    String fax;
    String matchcode;
    String arztnum;
    String klinik;
    String mtext;
    String email1;
    String email2;
    int id;
    String bsnR;

    public Arzt toArzt() {
        Arzt arzt = new Arzt();
        arzt.setAnrede(anrede);
        arzt.setTitel(titel);
        arzt.setNachname(nachname);
        arzt.setVorname(vorname);
        arzt.setPraxis(new Adresse("", strasse, plz, ort));
        arzt.setFacharzt(facharzt);
        arzt.setTelefon(telefon);
        arzt.setFax(fax);
        arzt.setMatchcode(matchcode);
        arzt.setArztnummer(new LANR(arztnum));
        arzt.setKlinik(klinik);
        arzt.setMtext(mtext);
        arzt.setEmail1(email1);
        arzt.setEmail2(email2);
        arzt.setId(id);
        arzt.setBsnr(bsnR);
        return arzt;
    }

    public ArztDto(Arzt arzt) {

        anrede = arzt.getAnrede();
        titel = arzt.getTitel();
        nachname = arzt.getNachname();
        vorname = arzt.getVorname();
        strasse = arzt.getPraxis().strasse;
        plz = arzt.getPraxis().plz;
        ort = arzt.getPraxis().ort;
        facharzt = arzt.getFacharzt();
        telefon = arzt.getTelefon();
        fax = arzt.getFax();
        matchcode = arzt.getMatchcode();
        arztnum = arzt.getArztnummer().lanr;
        klinik = arzt.getKlinik();
        mtext = arzt.getMtext();
        email1 = arzt.getEmail1();
        email2 = arzt.getEmail2();
        id = arzt.getId();
        bsnR = arzt.getBsnr();

    }

    public ArztDto() {

    }

    @Override
    public String toString() {
        return "ArztDto [anrede=" + anrede + ", titel=" + titel + ", nachname=" + nachname + ", vorname=" + vorname
                + ", strasse=" + strasse + ", plz=" + plz + ", ort=" + ort + ", facharzt=" + facharzt + ", telefon="
                + telefon + ", fax=" + fax + ", matchcode=" + matchcode + ", arztnum=" + arztnum + ", klinik=" + klinik
                + ", mtext=" + mtext + ", email1=" + email1 + ", email2=" + email2 + ", id=" + id + ", bsnR=" + bsnR
                + "]";
    }

    @Override
    public int hashCode() {
        return Objects.hash(anrede, arztnum, bsnR, email1, email2, facharzt, fax, id, klinik, matchcode, mtext,
                nachname, ort, plz, strasse, telefon, titel, vorname);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ArztDto other = (ArztDto) obj;
        return Objects.equals(anrede, other.anrede) && Objects.equals(arztnum, other.arztnum)
                && Objects.equals(bsnR, other.bsnR) && Objects.equals(email1, other.email1)
                && Objects.equals(email2, other.email2) && Objects.equals(facharzt, other.facharzt)
                && Objects.equals(fax, other.fax) && id == other.id && Objects.equals(klinik, other.klinik)
                && Objects.equals(matchcode, other.matchcode) && Objects.equals(mtext, other.mtext)
                && Objects.equals(nachname, other.nachname) && Objects.equals(ort, other.ort)
                && Objects.equals(plz, other.plz) && Objects.equals(strasse, other.strasse)
                && Objects.equals(telefon, other.telefon) && Objects.equals(titel, other.titel)
                && Objects.equals(vorname, other.vorname);
    }

    public static Optional<ArztDto> findbyID(String id, String aktIK) {
        String sql = " SELECT * FROM arzt where id ='" + id + "';";
        Optional<ArztDto> result = Optional.empty();
        DatenquellenFactory df = new DatenquellenFactory(aktIK);
        try (Connection con = df.createConnection();
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(sql);) {
            if (rs.next()) {
                result = Optional.of(ArztDto.of(rs));
            }
        } catch (SQLException e) {
            logger.error("could not get Arzt from DB for id = " + id, e);
        }
        return result;
    }

    private static ArztDto of(ResultSet rs) throws SQLException {
        ArztDto dto = new ArztDto();
        dto.anrede = rs.getString("anrede");
        dto.titel = rs.getString("titel");
        dto.nachname = rs.getString("nachname");
        dto.vorname = rs.getString("vorname");
        dto.strasse = rs.getString("strasse");
        dto.plz = rs.getString("plz");
        dto.ort = rs.getString("ort");
        dto.facharzt = rs.getString("facharzt");
        dto.telefon = rs.getString("telefon");
        dto.fax = rs.getString("fax");
        dto.matchcode = rs.getString("matchcode");
        dto.arztnum = rs.getString("arztnum");
        dto.klinik = rs.getString("klinik");
        dto.mtext = rs.getString("mtext");
        dto.email1 = rs.getString("email1");
        dto.email2 = rs.getString("email2");
        dto.id = rs.getInt("id");
        dto.bsnR = rs.getString("bsnr");

        return dto;
    }

    public static Optional<Arzt> findbyID(String arztid, IK ik) {
         return findbyID(arztid, ik.digitString()).map(ArztDto::toArzt);

    }

    public static List<Arzt> findbyID(String[] arztIds, IK ik) {
        List<Arzt> liste = new LinkedList<>();

        for (String id :arztIds) {
            findbyID(id, ik).ifPresent(liste::add);
        }


        return liste;
    }



}
