package openMaps;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class Route {
	Coordinate coordStart = new Coordinate("0", "90");
	Coordinate coordEnd = new Coordinate("0", "90");

	public Route() {
	}

	public Route withStart(String address) throws IOException {
		coordStart = getCoordsForAddressFromNominatim(address);
		return this;
	}

	public Route withEnd(String address) throws IOException {
		coordEnd = getCoordsForAddressFromNominatim(address);
		return this;
	}

	private Coordinate getCoordsForAddressFromNominatim(String client) throws IOException {
		try {
			client = URLEncoder.encode(client, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			// will never happen
		}
		String urlstart = "https://nominatim.openstreetmap.org/search?q=";
		UrlReader reader = new UrlReader();
		String[] args2 = { urlstart + client + "&format=xml" };
		String ergebnis;
		String lat = null;
		String lon = null;

		ergebnis = reader.readfromUrlt(args2);

		String[] splitted = ergebnis.split(" ");

		for (String test : splitted) {
			if (test.startsWith("lat=")) {
				lat = test.substring(5, test.length() - 1);

			} else if (test.startsWith("lon=")) {
				lon = test.substring(5, test.length() - 1);
			}

		}

		return new Coordinate(lon, lat);
	}

	Distance getDistanceFromOpenRouteService(String api_key) {
		Coordinate from = coordEnd;
		Coordinate to = coordStart;
		String urlstart = "https://api.openrouteservice.org/directions?api_key=" + api_key + "&coordinates="
				+ from.getLongitude() + "%2C" + from.getLatitude() + "%7C" + to.getLongitude() + "%2C"
				+ to.getLatitude() + "&profile=driving-car";
		String[] args2 = { urlstart };
		String ergebnis = null;
		try {
			ergebnis = new UrlReader().readfromUrlt(args2);
		} catch (Exception e) {

			e.printStackTrace();
		}
		return new Distance().parseOpenRouteServiceAnswer(ergebnis);

	}
}
