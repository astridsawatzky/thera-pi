package terminKalender;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

import systemEinstellungen.SystemConfig;

public class ICalGenerator {
    
    /*
     * static String ort =
     * SystemConfig.hmFirmenDaten.get("Firma1")+(!SystemConfig.hmFirmenDaten.get(
     * "Firma2").equals("") ? " "+SystemConfig.hmFirmenDaten.get("Firma2")+", CRLF"
     * : ", CRLF")+
     * SystemConfig.hmFirmenDaten.get("Strasse")+", "+SystemConfig.hmFirmenDaten.get
     * ("Plz")+" "+SystemConfig.hmFirmenDaten.get("Ort")+", CRLF"+
     * "Telefon: "+SystemConfig.hmFirmenDaten.get("Telefon");
     */
    private static String ort = SystemConfig.hmFirmenDaten.get("Strasse") + " CRLF" + SystemConfig.hmFirmenDaten.get("Plz")
            + " " + SystemConfig.hmFirmenDaten.get("Ort");

    private static final SimpleDateFormat futc = new SimpleDateFormat("yyyyMMdd'T'HHmmss");

    public static String macheKopf() {
        return "BEGIN:VCALENDAR" + System.getProperty("line.separator") + "CALSCALE:GREGORIAN"
                + System.getProperty("line.separator") + "METHOD:PUBLISH" + System.getProperty("line.separator")
                + "PRODID:-//Thera-Pi 1.0//Carpe diem//DE" + System.getProperty("line.separator") + "VERSION:2.0"
                + System.getProperty("line.separator") + "BEGIN:VTIMEZONE" + System.getProperty("line.separator")
                + "TZID:Europe/Berlin" + System.getProperty("line.separator") + "BEGIN:DAYLIGHT"
                + System.getProperty("line.separator") + "TZOFFSETFROM:+0100" + System.getProperty("line.separator")
                + "TZOFFSETTO:+0200" + System.getProperty("line.separator") + "TZNAME:CEST"
                + System.getProperty("line.separator") + "DTSTART:19700329T020000"
                + System.getProperty("line.separator") + "RRULE:FREQ=YEARLY;BYDAY=-1SU;BYMONTH=3"
                + System.getProperty("line.separator") + "END:DAYLIGHT" + System.getProperty("line.separator")
                + "BEGIN:STANDARD" + System.getProperty("line.separator") + "TZOFFSETFROM:+0200"
                + System.getProperty("line.separator") + "TZOFFSETTO:+0100" + System.getProperty("line.separator")
                + "TZNAME:CET" + System.getProperty("line.separator") + "DTSTART:19701025T030000"
                + System.getProperty("line.separator") + "RRULE:FREQ=YEARLY;BYDAY=-1SU;BYMONTH=10"
                + System.getProperty("line.separator") + "END:STANDARD" + System.getProperty("line.separator")
                + "END:VTIMEZONE" + System.getProperty("line.separator");
    }

    public static String macheVevent(String datum, String start, String end, String titel, String beschreibung,
            boolean warnen) {
        StringBuffer buf = new StringBuffer();
        try {
            buf.append("BEGIN:VEVENT" + System.getProperty("line.separator"));
            buf.append("UID:" + macheUID() + System.getProperty("line.separator"));
            // buf.append("CREATED:"+getUTC()+System.getProperty("line.separator"));
            // buf.append("LAST-MODIFIED:"+getUTC()+System.getProperty("line.separator"));
            buf.append("DTSTAMP:" + getUTC() + System.getProperty("line.separator"));
            buf.append("ORGANIZER;CN=\"" + (String) SystemConfig.hmIcalSettings.get("organisatorname") + ", "
                    + "Telefon: " + SystemConfig.hmFirmenDaten.get("Telefon") + " \":MAILTO:"
                    + (String) SystemConfig.hmIcalSettings.get("organisatoremail")
                    + System.getProperty("line.separator"));
            buf.append("SUMMARY:" + ((!((String) SystemConfig.hmIcalSettings.get("praefix")).equals(""))
                    ? (String) SystemConfig.hmIcalSettings.get("praefix") + " " + titel
                    : titel) + System.getProperty("line.separator"));
            buf.append("DTSTART:"/* ;TZID=Europe/Berlin:" */ + setUtcTime(datum + "T" + start)
                    + System.getProperty("line.separator"));
            buf.append("DTEND:"/* ;TZID=Europe/Berlin:" */ + setUtcTime(datum + "T" + end)
                    + System.getProperty("line.separator"));
            buf.append("TRANSP:OPAQUE" + System.getProperty("line.separator"));
            // buf.append("LOCATION:"+ort.replace("CRLF",
            // (System.getProperty("os.name").contains("Windows") ? "\\n" : "\\r\\n" )
            // )+System.getProperty("line.separator"));
            buf.append("LOCATION:" + ort.replace("CRLF", "\\n") + System.getProperty("line.separator"));
            buf.append("DESCRIPTION:" + beschreibung.replace("CRLF", (System.getProperty("os.name")
                                                                            .contains("Windows") ? "\\n" : "\\r\\n"))
                    + System.getProperty("line.separator"));
            if (warnen) {
                buf.append(macheWarnung((String) SystemConfig.hmIcalSettings.get("warnzeitpunkt")));
            }
            buf.append("END:VEVENT" + System.getProperty("line.separator"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return buf.toString();
    }

    static String macheRehaVevent(String datum, String start, String end, String titel, String beschreibung,
            boolean warnen) {
        StringBuffer buf = new StringBuffer();
        try {
            buf.append("BEGIN:VEVENT" + System.getProperty("line.separator"));
            buf.append("UID:" + macheUID() + System.getProperty("line.separator"));
            // buf.append("CREATED:"+getUTC()+System.getProperty("line.separator"));
            // buf.append("LAST-MODIFIED:"+getUTC()+System.getProperty("line.separator"));
            buf.append("DTSTAMP:" + getUTC() + System.getProperty("line.separator"));
            // buf.append("ORGANIZER;CN=\""+(String)SystemConfig.hmIcalSettings.get("organisatorname")+",
            // "+"Telefon: "+SystemConfig.hmFirmenDaten.get("Telefon")+
            // "\":MAILTO:"+(String)SystemConfig.hmIcalSettings.get("organisatoremail")+System.getProperty("line.separator"));

            // buf.append("SUMMARY:"+(
            // !((String)SystemConfig.hmIcalSettings.get("praefix")).equals("") ?
            // (String)SystemConfig.hmIcalSettings.get("praefix")+" "+titel : titel)
            // +System.getProperty("line.separator"));
            buf.append("SUMMARY:" + titel + System.getProperty("line.separator"));

            buf.append("DTSTART:"/* ;TZID=Europe/Berlin:" */ + setUtcTime(datum + "T" + start)
                    + System.getProperty("line.separator"));
            buf.append("DTEND:"/* ;TZID=Europe/Berlin:" */ + setUtcTime(datum + "T" + end)
                    + System.getProperty("line.separator"));
            buf.append("TRANSP:OPAQUE" + System.getProperty("line.separator"));
            // buf.append("LOCATION:"+ort.replace("CRLF",
            // (System.getProperty("os.name").contains("Windows") ? "\\n" : "\\r\\n" )
            // )+System.getProperty("line.separator"));
            buf.append("LOCATION:" + ort.replace("CRLF", "\\n") + System.getProperty("line.separator"));
            buf.append("DESCRIPTION:" + beschreibung.replace("CRLF", (System.getProperty("os.name")
                                                                            .contains("Windows") ? "\\n" : "\\r\\n"))
                    + System.getProperty("line.separator"));
            if (warnen) {
                buf.append(macheWarnung((String) SystemConfig.hmIcalSettings.get("warnzeitpunkt")));
            }
            buf.append("END:VEVENT" + System.getProperty("line.separator"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return buf.toString();
    }

    private static String macheWarnung(String warnung) {
        StringBuffer buf = new StringBuffer();
        try {
            buf.append("BEGIN:VALARM" + System.getProperty("line.separator"));
            buf.append("ACTION:DISPLAY" + System.getProperty("line.separator"));
            // buf.append("TRIGGER;VALUE=DURATION:-PT"+warnung+System.getProperty("line.separator"));
            buf.append("TRIGGER:" + warnung + System.getProperty("line.separator"));
            buf.append("DESCRIPTION:Erinnerung Therapie Termin" + System.getProperty("line.separator"));
            buf.append("END:VALARM" + System.getProperty("line.separator"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return buf.toString();
    }

    private static String macheUID() {
        UUID idOne = UUID.randomUUID();
        return idOne.toString();
    }

    public static String macheEnd() {
        return "END:VCALENDAR" + System.getProperty("line.separator");
    }

    private static String getUTC() {
        final SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
        f.setTimeZone(TimeZone.getTimeZone("UTC"));
        return f.format(new Date()) + "Z";
    }

    /*
     * String[] stitel =
     * {"Ik","Ikbezeichnung","Firma1","Firma2","Anrede","Nachname","Vorname",
     * "Strasse","Plz","Ort","Telefon","Telefax","Email","Internet","Bank","Blz",
     * "Kto",
     * "Steuernummer","Hrb","Logodatei","Zusatz1","Zusatz2","Zusatz3","Zusatz4",
     * "Bundesland"}; hmFirmenDaten = new HashMap<String,String>();
     */

    private static String setUtcTime(String datumkomplett) {
        String ret = "";
        try {
            int year = Integer.parseInt(datumkomplett.substring(0, 4)); // jahr
            int month = (datumkomplett.substring(4, 6)
                                      .startsWith("0") ? Integer.parseInt(datumkomplett.substring(5, 6))
                                              : Integer.parseInt(datumkomplett.substring(4, 6)))
                    - 1;
            int day = (datumkomplett.substring(6, 8)
                                    .startsWith("0") ? Integer.parseInt(datumkomplett.substring(7, 8))
                                            : Integer.parseInt(datumkomplett.substring(6, 8)));
            int hour = (datumkomplett.substring(9, 11)
                                     .startsWith("0") ? Integer.parseInt(datumkomplett.substring(10, 11))
                                             : Integer.parseInt(datumkomplett.substring(9, 11)));
            int minute = (datumkomplett.substring(11, 13)
                                       .startsWith("0") ? Integer.parseInt(datumkomplett.substring(12, 13))
                                               : Integer.parseInt(datumkomplett.substring(11, 13)));
            int second = (datumkomplett.substring(13, 15)
                                       .startsWith("0") ? Integer.parseInt(datumkomplett.substring(14, 15))
                                               : Integer.parseInt(datumkomplett.substring(13, 15)));
            Calendar cal = Calendar.getInstance();
            cal.set(year, month, day, hour, minute, second);
            futc.setTimeZone(TimeZone.getTimeZone("UTC"));
            ret = futc.format(cal.getTime()) + "Z";
            // System.out.println(f.format(cal.getTime())+"Z");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ret;
    }

}
