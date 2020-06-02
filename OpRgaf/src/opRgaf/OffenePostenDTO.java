package opRgaf;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mandant.IK;
import opRgaf.rezept.Money;
import opRgaf.rezept.Rezeptnummer;
import sql.DatenquellenFactory;

public class OffenePostenDTO {
    private static final Logger logger = LoggerFactory.getLogger(OffenePostenDTO.class);

   private static final String selectall = " SELECT concat(t2.n_name, ', ',t2.v_name,', ',DATE_FORMAT(t2.geboren,'%d.%m.%Y')) as kennung,t1.rnr,t1.rdatum,t1.rgesamt, \n" +
            "           t1.roffen,t1.rpbetrag,t1.rbezdatum,t1.rmahndat1,t1.rmahndat2,t3.kassen_nam1,t1.reznr,t1.id,t1.pat_id \n" +
            "           FROM (SELECT v_nummer as rnr,v_datum as rdatum,v_betrag as rgesamt,v_offen as roffen,0 as rpbetrag,\n" +
            "           v_bezahldatum as rbezdatum,mahndat1 as rmahndat1,mahndat2 as rmahndat2,'' as reznr,verklisteid as id,pat_id as pat_id \n" +
            "           FROM verkliste where v_nummer like 'VR-%' \n" +
            "            UNION SELECT rnr,rdatum,rgesamt,roffen,rpbetrag,rbezdatum,rmahndat1,rmahndat2,reznr,id as id,pat_intern as pat_id \n" +
            "           FROM rgaffaktura ) t1 LEFT JOIN pat5 AS t2 ON (t1.pat_id = t2.pat_intern) LEFT JOIN kass_adr AS t3 ON ( t2.kassenid = t3.id )\n" +
            "          ";

private IK ik;

   public OffenePostenDTO(IK ik) {
    this.ik = ik;
}


private OffenePosten ofResultset(ResultSet rs) throws SQLException {
       OffenePosten ret = new OffenePosten();
   ret.     kennung = new Kennung(rs.getString("kennung"));
   ret.     rgNr                            =rs.getString("rnr");
   ret.     rgDatum                         =rs.getDate("rDatum").toLocalDate();
   ret.     gesamtBetrag                    =Optional.ofNullable(rs.getString("rgesamt") ).map(Money::new).orElse(new Money() );
   ret.     offen                           =Optional.ofNullable(rs.getString("roffen")  ).map(Money::new).orElse(new Money() );
   ret.     bearbeitungsGebuehr             =Optional.ofNullable(rs.getString("rpbetrag")).map(Money::new).orElse(new Money() );
   ret.     bezahltAm                       =Optional.ofNullable(rs.getDate("rbezdatum")).map(d->d.toLocalDate()).orElse(null);
   ret.     mahnungEins                     =Optional.ofNullable(rs.getDate("rmahndat1")).map(d->d.toLocalDate()).orElse(null);
   ret.     mahnungZwei                     =Optional.ofNullable(rs.getDate("rmahndat2")).map(d->d.toLocalDate()).orElse(null);
   ret.     krankenKassenName               =rs.getString("kassen_nam1");
   ret.     rezNummer                       =new Rezeptnummer(rs.getString("reznr"));
   ret.     tabellenId                      =rs.getInt("id");
   ret.     patid                           =rs.getInt("pat_id");



       return ret;
   }


   public List<OffenePosten> all() {

       List<OffenePosten> rgafFakturaListe = new LinkedList<OffenePosten>();
       try (Connection con = new DatenquellenFactory(ik.digitString()).createConnection();) {
           ResultSet rs = con.createStatement()
                             .executeQuery(selectall);
           while (rs.next()) {

               rgafFakturaListe.add(ofResultset(rs));

           }

           return rgafFakturaListe;
       } catch (SQLException e) {
           logger.error("could not retrieve Mitarbeiter from Database", e);
           return Collections.emptyList();
       }

   }


}
