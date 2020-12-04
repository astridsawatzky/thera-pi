package openMaps;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Objects;


class Coordinate {
    private Double latitude;
    private Double longitude;
    private DecimalFormat NACHKOMMA5 = new DecimalFormat(".00000");
    private String display="";
    {
        NACHKOMMA5.setRoundingMode(RoundingMode.FLOOR);
    }

    public Coordinate(String lon, String lat) {
        this.longitude = Double.valueOf(lon);
        this.latitude = Double.valueOf(lat);
    }

    public Coordinate(String lon, String lat, String beschreibung) {
        this(lon,lat);
        this.display =beschreibung;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getShortLatitude() {
        return NACHKOMMA5.format(latitude);
    }

    public String getShortLongitude() {
        return NACHKOMMA5.format(longitude);
    }

    @Override
    public String toString() {
        return "Coordinate [" + getShortLongitude() + "," + getShortLatitude() + "]";
    }

    @Override
    public int hashCode() {
        return Objects.hash(getShortLatitude(), getShortLongitude());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Coordinate other = (Coordinate) obj;
        return Objects.equals(getShortLatitude(), other.getShortLatitude())
                && Objects.equals(getShortLongitude(), other.getShortLongitude());
    }

    public String getDisplay() {
        return display;
    }



}
