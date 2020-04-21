package terminKalender;

import java.time.Duration;
import java.time.LocalTime;

import org.slf4j.Logger ;
import org.slf4j.LoggerFactory;

public class TerminDTO {
private final static Logger logger = LoggerFactory.getLogger(TerminDTO.class);
    String bezeichnung;
    String notiz;
    String start;
    String ende;
    int dauerInMin;
    public TerminDTO(String bezeichnung, String notiz, String start, String ende, int dauerInMin) {
        super();
        this.bezeichnung = bezeichnung;
        this.notiz = notiz;
        this.start = start;
        this.ende = ende;
        this.dauerInMin = dauerInMin;
    }



    Termin toTermin() {

        LocalTime start2 = null ;
        try {
            if (start!=null) {
            start2 = LocalTime.parse(start);
            }
        } catch (Exception e) {
            logger.error("coudl not parse starttime: "+ start , e);
        }
        LocalTime ende2 = null;
        try {
            if (ende!=null) {
            ende2 = LocalTime.parse(ende);
            }
        } catch (Exception e) {
            logger.error("could not parse starttime: "+ start , e);
        }


        Duration dur =  Duration.ofMinutes(dauerInMin);

        Termin temp = new Termin(bezeichnung, notiz, start2,dur , ende2);

        return temp.equals(Termin.EMPTY) ? Termin.EMPTY: temp;





    }





}
