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
