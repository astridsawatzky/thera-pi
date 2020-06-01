package opRgaf;

import java.util.Vector;

import CommonTools.SqlInfo;

public class PatientenAdressen {
Adresse adresse;
Adresse abweichendeAdresse;
String patid;

public PatientenAdressen(String patientenID) {
    patid= patientenID;
}


String[] getAdressParams(String patid) {
    
    // anr=17,titel=18,nname=0,vname=1,strasse=3,plz=4,ort=5,abwadress=19
    // "anrede,titel,nachname,vorname,strasse,plz,ort"
    String cmd = "select anrede,titel,n_name,v_name,strasse,plz,ort from pat5 where id='" + patid + "' LIMIT 1";
    Vector<Vector<String>> abwvec = SqlInfo.holeFelder(cmd);
    Object[] obj = { abwvec.get(0)
                           .get(0),
            abwvec.get(0)
                  .get(1),
            abwvec.get(0)
                  .get(2),
            abwvec.get(0)
                  .get(3),
            abwvec.get(0)
                  .get(4),
            abwvec.get(0)
                  .get(5),
            abwvec.get(0)
                  .get(6) };
    return AdressTools.machePrivatAdresse(obj, true);
}

String[] holeAbweichendeAdresse(String patid) {
    // "anrede,titel,nachname,vorname,strasse,plz,ort"
    String cmd = "select abwanrede,abwtitel,abwn_name,abwv_name,abwstrasse,abwplz,abwort from pat5 where id='"
            + patid + "' LIMIT 1";
    Vector<Vector<String>> abwvec = SqlInfo.holeFelder(cmd);
    Object[] obj = { abwvec.get(0)
                           .get(0),
            abwvec.get(0)
                  .get(1),
            abwvec.get(0)
                  .get(2),
            abwvec.get(0)
                  .get(3),
            abwvec.get(0)
                  .get(4),
            abwvec.get(0)
                  .get(5),
            abwvec.get(0)
                  .get(6) };
    return AdressTools.machePrivatAdresse(obj, true);
}
}


