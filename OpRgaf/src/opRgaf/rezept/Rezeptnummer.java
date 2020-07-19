/**
 *
 */
package opRgaf.rezept;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import core.Disziplin;

/**
 * Small class to provide RezeptNr magic
 *
 */
public class Rezeptnummer {

    private Disziplin disziplin;
    private int rezeptZiffern;
    private String stornoVermerk ="";

    public Rezeptnummer() {
        disziplin = Disziplin.INV;
    }

    /**
     * PRE: expects a String in the format of e.g. "ER101"
     * POST: class members diszi and rezNr are set
     *
     * @param rezNr
     */

    public Rezeptnummer(String rezNr) {
        if ( rezNr == null || rezNr == "" ) {
            return;
        }
        Pattern pattern = Pattern.compile("(\\p{L}+)(\\p{Nd}+)(\\p{L}*)");
        Matcher matcher = pattern.matcher(rezNr);
        matcher.matches();
        disziplin = ofShort(matcher.group(1));
        rezeptZiffern=Integer.parseInt(matcher.group(2));
        stornoVermerk = Optional.ofNullable(matcher.group(3)).orElse("");
    }

    boolean isStorniert() {
        return !"".equals(stornoVermerk);
    }

    public static Disziplin ofShort(String value) {
        try {
            return Disziplin.valueOf(value);
        } catch (Exception e) {
            return Disziplin.INV;
        }
    }


    // constructor only passing in INT -> take from sysconfig def. diszi or set inv?

    public Rezeptnummer(Disziplin disziplin, int rezeptZiffern) {
        // super();
        this.disziplin = disziplin;
        this.rezeptZiffern = rezeptZiffern;
    }

    public Disziplin disziplin() {
        return disziplin;
    }

    public int rezeptZiffern() {
        return rezeptZiffern;
    }

    /** combines disziplin and ziffern into one string.
     * ignores stornovermerk.
     *
     * Caller must make sure to handle Disziplinen INV und COMMON.
     * @return disziplin + rezeptziffern
     */
    public String rezeptNummer() {
        return disziplin + Integer.toString(rezeptZiffern);
    }
    @Override
    public String toString() {
        return "Rezeptnummer [disziplin=" + disziplin + ", rezeptZiffern=" + rezeptZiffern + "]";
    }

    @Override
    public int hashCode() {
        return Objects.hash(disziplin, rezeptZiffern);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Rezeptnummer other = (Rezeptnummer) obj;
        return disziplin == other.disziplin && rezeptZiffern == other.rezeptZiffern;
    }

    /** stornierung von rechnungen wurde bislang dadurch erreicht,
     * dass an die rezeptnummern ein 'S' mit einer moeglichen Bemerkung angefuegt wurde.
     *
     * @return
     */
    public String stornoVermerk() {
        if(isStorniert()) {
            return stornoVermerk.substring(1);
        }
        return stornoVermerk;
    }


}
