package terminKalender;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import CommonTools.DateTimeFormatters;
import specs.Contracts;

public class Block {

    public static final Block EMPTYBLOCK =  new Block("",
            "",
            "",
            "",
            "",
            Integer.toString(-1));
    private String name;
    private String rezeptnr;
    private String startzeit;
    private String dauer;
    private String endzeit;
    public String getRezeptnr1() {
        return rezeptnr;
    }

    public String getStartzeit2() {
        return startzeit;
    }

    public String getDauer3() {
        return dauer;
    }

    public String getEndzeit4() {
        return endzeit;
    }

    public String getNr5() {
        return nr;
    }

    private String nr;

    public Block(String name, String rezeptnr, String startzeit, String dauer, String endzeit, String nr) {
        this.name = name;
        this.rezeptnr = rezeptnr;
        this.startzeit = startzeit;
        this.dauer = dauer;
        this.endzeit = endzeit;
        this.nr = nr;
    }


    public Block(String[] daten) {
        Contracts.require(daten.length ==6, "Die Datenlänge passt nicht zu Terminblock" +daten );
        this.name = daten[0];
        this.rezeptnr = daten[1];
        this.startzeit = daten[2];
        this.dauer = daten[3];
        this.endzeit = daten[4];
        this.nr = daten[5];
    }

    /** creates a new Block and calculates duration.
     *
     * @param name
     * @param reznr
     * @param beginn
     * @param ende
     * @param nr
     */
    public Block(String name, String reznr, LocalTime beginn, LocalTime ende,String nr) {
        this(name,reznr,beginn,ChronoUnit.MINUTES.between(beginn, ende) ,ende,nr);
    }

    /**creates a new Block and calculates the end time
     *
     * @param name
     * @param reznr
     * @param beginn
     * @param dauer
     * @param nr
     */
    public Block(String name, String reznr, LocalTime beginn, int dauer,String nr) {
        this(name,reznr,beginn,dauer, beginn.plusMinutes(dauer),nr);
    }

    /**creates a new Block and calculates the start time.
     *
     * @param name
     * @param reznr
     * @param dauer
     * @param ende
     * @param nr
     */
    public Block(String name, String reznr, int dauer, LocalTime ende,String nr) {
        this(name,reznr,ende.minusMinutes(dauer),dauer,ende,nr);
    }




    public Block(String name, String reznr, LocalTime beginn, long dauer, LocalTime ende, String nr) {
        this.name = name;
        this.rezeptnr = reznr;
        this.startzeit = beginn.format(DateTimeFormatter.ISO_LOCAL_TIME);
        this.dauer = String.valueOf(dauer);
        this.endzeit = ende.format(DateTimeFormatter.ISO_LOCAL_TIME);
        this.nr = nr;
    }

    public String getName0() {
        return name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(dauer, endzeit, name, rezeptnr, startzeit);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Block))
            return false;
        Block other = (Block) obj;
        return Objects.equals(dauer, other.dauer) && Objects.equals(endzeit, other.endzeit)
                && Objects.equals(name, other.name) && Objects.equals(rezeptnr, other.rezeptnr)
                && Objects.equals(startzeit, other.startzeit);
    }





}
