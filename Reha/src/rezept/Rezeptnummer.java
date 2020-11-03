/**
 *
 */
package rezept;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.istack.internal.NotNull;

import core.Disziplin;

import static core.Disziplin.*;

/**
 * Small class to provide RezeptNr magic
 *
 */
public class Rezeptnummer {

    private static final int INVALID = -1;



    private static final Logger LOGGER = LoggerFactory.getLogger(Rezeptnummer.class);



    private final Disziplin disziplin;
    private final int rezeptZiffern;

    public Rezeptnummer() {
        disziplin = INV;
        rezeptZiffern=INVALID;
    }

    /**
     * PRE: expects a String in the format of e.g. "ER101"
     * POST: class members diszi and rezNr are set
     *
     * @param rezNr
     */
    public Rezeptnummer(String rezNr) {
        if ( rezNr == null || rezNr == "" ) {
            disziplin = Disziplin.INV;
            rezeptZiffern=INVALID;
            return;
        }
        String diszi2check = rezNr.replaceAll("[0-9]", "");
        disziplin = Disziplin.ofShort(diszi2check);

        String rezNrDigits = rezNr.replaceAll("[a-zA-Z]", "");
        this.rezeptZiffern = Integer.parseInt(rezNrDigits);
    }

    public Rezeptnummer(@NotNull Disziplin disziplin, int rezeptZiffern) {
        if(disziplin==null) {
            LOGGER.error("Disziplin must not be null.");
            disziplin = INV;
        }
        this.disziplin = disziplin;
        this.rezeptZiffern = rezeptZiffern;
    }

    public Disziplin disziplin() {
        return disziplin;
    }

    public int rezeptZiffern() {
        return rezeptZiffern;
    }

    public String rezeptNummer() {
        // logger.debug("Diszi=" + disziplin + " and rezZiffern=" + rezeptZiffern);
        // TODO: if diszi == (COMMON || INV) => boing!
        if ( disziplin == null || disziplin == Disziplin.INV)
            return null;
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


}
