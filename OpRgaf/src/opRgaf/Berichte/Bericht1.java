package opRgaf.Berichte;

import java.time.LocalDate;

public class Bericht1 {
    String patIntern;
    int berichtId;
    String arztNum;
    LocalDate erstellDat;
    String versandArt;
    LocalDate versandDat;
    String berTyp;
    String berStand;
    String berBeso;
    String berProg;
    String berVors;
    String diagnose;
    String krBild;
    String verfasser;
    LocalDate rezDatum;
    int id;
    
    // Public std. getter/setters
    public String getPatIntern() {
        return patIntern;
    }
    public void setPatIntern(String patIntern) {
        this.patIntern = patIntern;
    }
    public int getBerichtId() {
        return berichtId;
    }
    public void setBerichtId(int berichtId) {
        this.berichtId = berichtId;
    }
    public String getArztNum() {
        return arztNum;
    }
    public void setArztNum(String arztNum) {
        this.arztNum = arztNum;
    }
    public LocalDate getErstellDat() {
        return erstellDat;
    }
    public void setErstellDat(LocalDate erstellDat) {
        this.erstellDat = erstellDat;
    }
    public String getVersandArt() {
        return versandArt;
    }
    public void setVersandArt(String versandArt) {
        this.versandArt = versandArt;
    }
    public LocalDate getVersandDat() {
        return versandDat;
    }
    public void setVersandDat(LocalDate versandDat) {
        this.versandDat = versandDat;
    }
    public String getBerTyp() {
        return berTyp;
    }
    public void setBerTyp(String berTyp) {
        this.berTyp = berTyp;
    }
    public String getBerStand() {
        return berStand;
    }
    public void setBerStand(String berStand) {
        this.berStand = berStand;
    }
    public String getBerBeso() {
        return berBeso;
    }
    public void setBerBeso(String berBeso) {
        this.berBeso = berBeso;
    }
    public String getBerProg() {
        return berProg;
    }
    public void setBerProg(String berProg) {
        this.berProg = berProg;
    }
    public String getBerVors() {
        return berVors;
    }
    public void setBerVors(String berVors) {
        this.berVors = berVors;
    }
    public String getDiagnose() {
        return diagnose;
    }
    public void setDiagnose(String diagnose) {
        this.diagnose = diagnose;
    }
    public String getKrBild() {
        return krBild;
    }
    public void setKrBild(String krBild) {
        this.krBild = krBild;
    }
    public String getVerfasser() {
        return verfasser;
    }
    public void setVerfasser(String verfasser) {
        this.verfasser = verfasser;
    }
    public LocalDate getRezDatum() {
        return rezDatum;
    }
    public void setRezDatum(LocalDate rezDatum) {
        this.rezDatum = rezDatum;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

}
