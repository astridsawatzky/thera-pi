package terminKalender;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.UUID;

import systemEinstellungen.SystemConfig;

public class ICalGenerator {
	static String CLRF = "=0D=0A";
	static String ort = SystemConfig.hmFirmenDaten.get("Firma1")+" "+SystemConfig.hmFirmenDaten.get("Firma2")+"CRLF"+
			SystemConfig.hmFirmenDaten.get("Strasse")+", "+SystemConfig.hmFirmenDaten.get("Plz")+" "+SystemConfig.hmFirmenDaten.get("Ort")+"CRLF"+
			"Telefon: "+SystemConfig.hmFirmenDaten.get("Telefon");
	
	public static String macheKopf(){
		return "BEGIN:VCALENDAR"+System.getProperty("line.separator")+
				"VERSION:2.0"+System.getProperty("line.separator")+
				"BEGIN:VTIMEZONE"+System.getProperty("line.separator")+
				"TZID:Europe/Berlin"+System.getProperty("line.separator")+
				"BEGIN:DAYLIGHT"+System.getProperty("line.separator")+
				"TZOFFSETFROM:+0100"+System.getProperty("line.separator")+
				"TZOFFSETTO:+0200"+System.getProperty("line.separator")+
				"TZNAME:CEST"+System.getProperty("line.separator")+
				"DTSTART:19700329T020000"+System.getProperty("line.separator")+
				"RRULE:FREQ=YEARLY;BYDAY=-1SU;BYMONTH=3"+System.getProperty("line.separator")+
				"END:DAYLIGHT"+System.getProperty("line.separator")+
				"BEGIN:STANDARD"+System.getProperty("line.separator")+
				"TZOFFSETFROM:+0200"+System.getProperty("line.separator")+
				"TZOFFSETTO:+0100"+System.getProperty("line.separator")+
				"TZNAME:CET"+System.getProperty("line.separator")+
				"DTSTART:19701025T030000"+System.getProperty("line.separator")+
				"RRULE:FREQ=YEARLY;BYDAY=-1SU;BYMONTH=10"+System.getProperty("line.separator")+
				"END:STANDARD"+System.getProperty("line.separator")+
				"END:VTIMEZONE"+System.getProperty("line.separator")
				;
	}
	
	public static String macheVevent(String datum, String start, String end, String titel, String beschreibung,boolean warnen){
		StringBuffer buf = new StringBuffer();
		buf.append("BEGIN:VEVENT"+System.getProperty("line.separator"));
		buf.append("UID:"+macheUID()+System.getProperty("line.separator"));
		//buf.append("CREATED:"+getUTC()+System.getProperty("line.separator"));
		//buf.append("LAST-MODIFIED:"+getUTC()+System.getProperty("line.separator"));
		//buf.append("DTSTAMP:"+getUTC()+System.getProperty("line.separator"));
		buf.append("SUMMARY:"+titel+System.getProperty("line.separator"));
		buf.append("DTSTART;TZID=Europe/Berlin:"+datum+"T"+start+System.getProperty("line.separator"));
		buf.append("DTEND;TZID=Europe/Berlin:"+datum+"T"+end+System.getProperty("line.separator"));
		buf.append("TRANSP:OPAQUE"+System.getProperty("line.separator"));
		//buf.append("LOCATION:"+ort.replace("CRLF", (System.getProperty("os.name").contains("Windows") ? "\\n" : "\\r\\n" ) )+System.getProperty("line.separator"));
		buf.append("LOCATION:"+ort.replace("CRLF", "\\ " )+System.getProperty("line.separator"));
		buf.append("DESCRIPTION:"+beschreibung.replace("CRLF", (System.getProperty("os.name").contains("Windows") ? "\\n" : "\\r\\n" ) )+System.getProperty("line.separator"));
		if(warnen){
			buf.append(macheWarnung((String) SystemConfig.hmIcalSettings.get("warnzeitpunkt")));
		}
		buf.append("END:VEVENT"+System.getProperty("line.separator"));
		return buf.toString();
	}
	
	public static String macheWarnung(String warnung){
		StringBuffer buf = new StringBuffer();
		buf.append("BEGIN:VALARM"+System.getProperty("line.separator"));
		buf.append("ACTION:DISPLAY"+System.getProperty("line.separator"));
		//buf.append("TRIGGER;VALUE=DURATION:-PT"+warnung+System.getProperty("line.separator"));
		buf.append("TRIGGER:"+warnung+System.getProperty("line.separator"));
		buf.append("DESCRIPTION:Erinnerung Therapie Termin"+System.getProperty("line.separator"));
		buf.append("END:VALARM"+System.getProperty("line.separator"));
		return buf.toString();
	}
	
	public static String macheUID(){
		UUID idOne = UUID.randomUUID();
		return idOne.toString();
	}
	
	public static String macheEnd(){
		return "END:VCALENDAR"+System.getProperty("line.separator");
	}
	public static String getUTC(){
		final SimpleDateFormat f = new SimpleDateFormat("yyyyMMddHHmmss");
        f.setTimeZone(TimeZone.getTimeZone("UTC"));
        return f.format(new Date())+"Z";
	}
	
	/*
	String[] stitel = {"Ik","Ikbezeichnung","Firma1","Firma2","Anrede","Nachname","Vorname",
			"Strasse","Plz","Ort","Telefon","Telefax","Email","Internet","Bank","Blz","Kto",
			"Steuernummer","Hrb","Logodatei","Zusatz1","Zusatz2","Zusatz3","Zusatz4","Bundesland"};
	hmFirmenDaten = new HashMap<String,String>();
	*/
	

}
