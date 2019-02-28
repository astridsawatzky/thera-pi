package CommonTools;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public final class ZeitFunk {

	public static long ZeitDifferenzInMinuten(String szeit1, String szeit2) {

		LocalTime lt1 = LocalTime.parse(szeit1);
		LocalTime lt2 = LocalTime.parse(szeit2);

		return ChronoUnit.MINUTES.between(lt1, lt2);
	}

	public static long MinutenSeitMitternacht(String szeit1) {
		return ZeitDifferenzInMinuten("00:00", szeit1.substring(0, 5));

	}

	public static String MinutenZuZeit(long minutes) {
		return LocalTime.of(0, 0).plusMinutes(minutes).toString().concat(":00");

	}

	public static String ZeitPlusMinuten(String zeit, String minuten) {
		LocalTime lt1 = LocalTime.parse(zeit);
		return lt1.plusMinutes(Integer.parseInt(minuten)).toString();
	}

	public static String ZeitMinusMinuten(String zeit, String minuten) {
		LocalTime lt1 = LocalTime.parse(zeit);
		return lt1.minusMinutes(Integer.parseInt(minuten)).toString();

	}


}
