package suchen;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import CommonTools.DatFunk;
import mandant.IK;

public class PatWithMatchingVo {
    String    nachname;
    String    vorname ;
    String    geboren;
    String    patIntern;
    String    rezeptnummer;
    String    letzteBehandlung;
    String    therapeut;
    protected final IK ik;
    protected List<PatWithMatchingVo> patientenListe = null;
    private final int cNachname = 1, cVorname = 2, cGeboren = 3, cPatIntern = 4, cRezNr = 5, cTermine = 6, cBehandler = 7;

    public PatWithMatchingVo(IK ik) {
        this.ik = ik;
    }
    public Vector<Vector<String>> getPatList() {
        Vector<Vector<String>> result = new Vector<Vector<String>>();
        PatWithMatchingVo pat = null;
        for (int i = 0; i < patientenListe.size(); i++ ) {
            Vector<String> currVec = new Vector<String>();
            currVec.addAll(Arrays.asList(patientenListe.get(i).nachname,
                    patientenListe.get(i).vorname,
                    patientenListe.get(i).geboren,
                    patientenListe.get(i).patIntern,
                    patientenListe.get(i).rezeptnummer,
                    patientenListe.get(i).letzteBehandlung,
                    patientenListe.get(i).therapeut));
            currVec.trimToSize();
            result.add(currVec);
        }
        result.trimToSize();
        return result;
    }
    public Vector<String> getVoList() {
        Vector<String> result = new Vector<String>();
        PatWithMatchingVo pat = null;
        for (int i = 0; i < patientenListe.size(); i++ ) {
            result.add(patientenListe.get(i).rezeptnummer);
        }
        result.trimToSize();
        return result;
    }
    public PatWithMatchingVo ofResultset(ResultSet rs) throws SQLException {
        PatWithMatchingVo pat = new PatWithMatchingVo(this.ik);
        
        pat.nachname = rs.getString(cNachname);
        pat.vorname = rs.getString(cVorname);
        pat.geboren = rs.getString(cGeboren);
        pat.patIntern = rs.getString(cPatIntern);
        pat.rezeptnummer = rs.getString(cRezNr);
        String termine = rs.getString(cTermine);
        if ((termine == null) || (termine == "")) {
            pat.letzteBehandlung = "";
            pat.therapeut = rs.getString(cBehandler);
        } else {
            String einzelTermine[] = termine.split("@");
            String letzter = einzelTermine[einzelTermine.length-1].trim();
            pat.letzteBehandlung = DatFunk.sDatInDeutsch(letzter);
            pat.therapeut = einzelTermine[einzelTermine.length-4].trim();
        }
        return pat;
    }
}
