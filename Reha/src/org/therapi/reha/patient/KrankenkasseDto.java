package org.therapi.reha.patient;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import core.Krankenkasse;
import mandant.IK;
import sql.DatenquellenFactory;

public class KrankenkasseDto {






    private static final Logger logger = LoggerFactory.getLogger(KrankenkasseDto.class);

           String KUERZEL;               // VARCHAR(6) NULL DEFAULT NULL,
           String PREISGRUPPE;          // VARCHAR(2) NULL DEFAULT NULL,
           String kassen_nam1;          // VARCHAR(60) NULL DEFAULT NULL,
           String kassen_nam2;          // VARCHAR(60) NULL DEFAULT NULL,
           String STRASSE;              // VARCHAR(30) NULL DEFAULT NULL,
           String PLZ;                  // VARCHAR(5) NULL DEFAULT NULL,
           String ORT;                  // VARCHAR(30) NULL DEFAULT NULL,
           String POSTFACH;             // VARCHAR(20) NULL DEFAULT NULL,
           String FAX;                  // VARCHAR(15) NULL DEFAULT NULL,
           String TELEFON;              // VARCHAR(20) NULL DEFAULT NULL,
           String IK_NUM;               // VARCHAR(15) NULL DEFAULT NULL,
           String KV_NUMMER;            // VARCHAR(25) NULL DEFAULT NULL,
           String MATCHCODE;            // VARCHAR(25) NULL DEFAULT NULL,
           String KMEMO;                // LONGTEXT NULL DEFAULT NULL,
           String RECHNUNG;             // VARCHAR(6) NULL DEFAULT NULL,
           String IK_KASSE;             // VARCHAR(15) NULL DEFAULT NULL,
           String IK_PHYSIKA;           // VARCHAR(15) NULL DEFAULT NULL,
           String IK_NUTZER;            // VARCHAR(15) NULL DEFAULT NULL,
           String IK_KOSTENT;           // VARCHAR(15) NULL DEFAULT NULL,
           String IK_KVKARTE;           // VARCHAR(15) NULL DEFAULT NULL,
           String IK_PAPIER;            // VARCHAR(15) NULL DEFAULT NULL,
           String EMAIL1;               // VARCHAR(30) NULL DEFAULT NULL,
           String EMAIL2;               // VARCHAR(30) NULL DEFAULT NULL,
           String EMAIL3;               // VARCHAR(30) NULL DEFAULT NULL,
           int id;
           boolean HMRABRECHNUNG =false;
           String PGKG;                 // VARCHAR(2) NULL DEFAULT NULL,
           String PGMA;                 // VARCHAR(2) NULL DEFAULT NULL,
           String PGER;                 // VARCHAR(2) NULL DEFAULT NULL,
           String PGLO;                 // VARCHAR(2) NULL DEFAULT NULL,
           String PGRH;                 // VARCHAR(2) NULL DEFAULT NULL,
           String PGPO;                 // VARCHAR(2) NULL DEFAULT NULL,


    Optional<Krankenkasse>  findbyId(int id, IK aktIK) {

        String sql = "SELECT  * FROM kass_adr WHERE  kass_adr.id ='" + id + "';";
        Optional<Krankenkasse> result = Optional.empty();
        DatenquellenFactory df = new DatenquellenFactory(aktIK.digitString());
        try (Connection con = df.createConnection();
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(sql);) {
            if (rs.next()) {
                result = Optional.of(KrankenkasseDto.of(rs));
            }
        } catch (SQLException e) {
            logger.error("Krankenkassenid: " +id +" aus " + aktIK, e);
        }
        return result;

    }

    private static Krankenkasse of(ResultSet rs) throws SQLException {
        KrankenkasseDto dto  = new KrankenkasseDto();

   dto. KUERZEL                =  rs.getString("KUERZEL");
   dto. PREISGRUPPE            =  rs.getString("PREISGRUPPE");
   dto. kassen_nam1            =  rs.getString("kassen_nam1");
   dto. kassen_nam2            =  rs.getString("kassen_nam2");
   dto. STRASSE               =   rs.getString("STRASSE");
   dto. PLZ                   =   rs.getString("PLZ");
   dto. ORT                   =   rs.getString("ORT");
   dto. POSTFACH             =   rs.getString("POSTFACH");
   dto. FAX                  =   rs.getString("FAX");
   dto. TELEFON              =   rs.getString("TELEFON");
   dto. IK_NUM               =   rs.getString("IK_NUM");
   dto. KV_NUMMER            =   rs.getString("KV_NUMMER");
   dto. MATCHCODE             =   rs.getString("MATCHCODE");
   dto. KMEMO                 =   rs.getString("KMEMO");
   dto. RECHNUNG             =   rs.getString("RECHNUNG");
   dto. IK_KASSE             =   rs.getString("IK_KASSE");
   dto. IK_PHYSIKA           =   rs.getString("IK_PHYSIKA");
   dto. IK_NUTZER             =   rs.getString("IK_NUTZER");
   dto. IK_KOSTENT           =   rs.getString("IK_KOSTENT");
   dto. IK_KVKARTE            =   rs.getString("IK_KVKARTE");
   dto. IK_PAPIER             =   rs.getString("IK_PAPIER");
   dto. EMAIL1                =   rs.getString("EMAIL1");
   dto. EMAIL2                =   rs.getString("EMAIL2");
   dto. EMAIL3                =   rs.getString("EMAIL3");
   dto. id                      =   rs.getInt("id");
   dto. HMRABRECHNUNG           =   rs.getString("HMRABRECHNUNG").equals("T");
   dto. PGKG                  =   rs.getString("PGKG");
   dto. PGMA                  =   rs.getString("PGMA");
   dto. PGER                  =   rs.getString("PGER");
   dto. PGLO                  =   rs.getString("PGLO");
   dto. PGRH                  =   rs.getString("PGRH");
   dto. PGPO                  =   rs.getString("PGPO");

        return of(dto);
    }

    private static Krankenkasse of(KrankenkasseDto dto) {

        Krankenkasse kk = new Krankenkasse(dto.IK_KASSE);
kk.setName(dto.kassen_nam1);
kk.setId(dto.id);


//dto. KUERZEL       = dto. KUERZEL                   ;
//dto. PREISGRUPPE   = dto. PREISGRUPPE               ;
//dto. kassen_nam1   = dto. kassen_nam1               ;
//dto. kassen_nam2   = dto. kassen_nam2               ;
//dto. STRASSE       = dto. STRASSE                   ;
//dto. PLZ           = dto. PLZ                       ;
//dto. ORT           = dto. ORT                       ;
//dto. POSTFACH      = dto. POSTFACH                  ;
//dto. FAX           = dto. FAX                       ;
//dto. TELEFON       = dto. TELEFON                   ;
//dto. IK_NUM        = dto. IK_NUM                    ;
//dto. KV_NUMMER     = dto. KV_NUMMER                 ;
//dto. MATCHCODE     = dto. MATCHCODE                 ;
//dto. KMEMO         = dto. KMEMO                     ;
//dto. RECHNUNG      = dto. RECHNUNG                  ;
//dto. IK_KASSE      = dto. IK_KASSE                  ;
//dto. IK_PHYSIKA    = dto. IK_PHYSIKA                ;
//dto. IK_NUTZER     = dto. IK_NUTZER                 ;
//dto. IK_KOSTENT    = dto. IK_KOSTENT                ;
//dto. IK_KVKARTE    = dto. IK_KVKARTE                ;
//dto. IK_PAPIER     = dto. IK_PAPIER                 ;
//dto. EMAIL1        = dto. EMAIL1                    ;
//dto. EMAIL2        = dto. EMAIL2                    ;
//dto. EMAIL3        = dto. EMAIL3                    ;
//dto. id            = dto. id                        ;
//dto. HMRABRECHNUNG = dto. HMRABRECHNUNG             ;
//dto. PGKG          = dto. PGKG                      ;
//dto. PGMA          = dto. PGMA                      ;
//dto. PGER          = dto. PGER                      ;
//dto. PGLO          = dto. PGLO                      ;
//dto. PGRH          = dto. PGRH                      ;
//dto. PGPO          = dto. PGPO                      ;





        return kk;
    }

}
