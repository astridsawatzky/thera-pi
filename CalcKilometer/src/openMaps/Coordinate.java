package openMaps;

public class Coordinate {
    private String latitude;
    private String longitude;

    public Coordinate(String lon, String lat) {
        this.longitude = lon;
        this.latitude = lat;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    @Override
    public String toString() {
        return "Coordinate [" + longitude + "," + latitude + "]";
    }

}
