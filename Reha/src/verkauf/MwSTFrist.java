package verkauf;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class MwSTFrist {
    private static final Logger LOGGER = LoggerFactory.getLogger(MwSTFrist.class);
    private static final List<MwSTFrist> FRISTEN = new LinkedList<>();
    static{
        FRISTEN.add(new MwSTFrist(19, 7, LocalDate.MIN, LocalDate.of(2020, 6, 30)));
        FRISTEN.add(new MwSTFrist(16, 5, LocalDate.of(2020,7,1), LocalDate.of(2020, 12, 31)));
        FRISTEN.add(new MwSTFrist(19, 7, LocalDate.of(2021, 1, 1),LocalDate.MAX));

    }



    public int vollerSatz() {
        return voll;
    }





    public int verminderterSatz() {
        return vermindert;
    }





    public LocalDate beginn() {
        return von;
    }





    public LocalDate ende() {
        return bis;
    }





    private int voll;
    private int vermindert;
    private LocalDate von;
    private LocalDate bis;

public MwSTFrist(int voll, int vermindert, LocalDate von , LocalDate bis) {
    this.voll = voll;
    this.vermindert = vermindert;
    this.von = von;
    this.bis = bis;


}





    public static MwSTFrist of(LocalDate date) {

        for (MwSTFrist frist : FRISTEN) {
            if(date.until(frist.bis,ChronoUnit.DAYS) >=0) {
                 return frist;
            }
        }
        throw new RuntimeException("Das kann nicht passieren");

    }





}
