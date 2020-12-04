package openMaps;

import java.io.IOException;

class Route {
    Coordinate coordStart = new Coordinate("0", "90");
    Coordinate coordEnd = new Coordinate("0", "90");

    public Route() {
    }

    public Route withStart(String address) throws IOException {
        coordStart = new Nominatim().getCoordsForAddressFromNominatim(address);
        return this;
    }

    public Route withEnd(String address) throws IOException {
        coordEnd = new Nominatim().getCoordsForAddressFromNominatim(address);
        return this;
    }

    Distance getDistanceFromOpenRouteService(String api_key) {

        String urlstart = composeURL(api_key);
        String[] args2 = { urlstart };
        String ergebnis = null;
        try {
            ergebnis = new UrlReader().readfromUrlt(args2);
        } catch (Exception e) {

            e.printStackTrace();
        }
        return new Distance().parseOpenRouteServiceAnswer(ergebnis);

    }

    String composeURL(String api_key) {
        String urlstart = "https://api.openrouteservice.org/v2/directions/driving-car?api_key=" + api_key + "&start="
                + coordStart.getLongitude() + "," + coordStart.getLatitude() + "&end=" + coordEnd.getLongitude() + ","
                + coordEnd.getLatitude();
        return urlstart;
    }
}
