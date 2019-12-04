package openMaps;

public class Distance {
    int distanceInM = 0;
    int seconds = 0;

    Distance parseOpenRouteServiceAnswer(String answer) {
        int start = answer.indexOf("distance");
        start = answer.indexOf(":", start) + 1;
        int stop = answer.indexOf(",", start);
        String distanceString = answer.substring(start, stop);
        start = answer.indexOf("duration");
        start = answer.indexOf(":", start) + 1;
        stop = answer.indexOf("}", start);
        String durationString = answer.substring(start, stop);

        return this.withDistance(Double.parseDouble(distanceString))
                   .withDuration(Double.parseDouble(durationString));

    }

    private Distance withDuration(double durationinSec) {
        this.seconds = (int) durationinSec;
        return this;
    }

    private Distance withDistance(double distanceInMetern) {
        this.distanceInM = (int) distanceInMetern;
        return this;
    }

    public int getMeter() {

        return distanceInM;
    }

    public int getDuration() {
        return seconds;
    }

    public double getKilometer() {
        return distanceInM / 1000;
    }

    public int getDurationInMinutes() {
        return seconds / 60;
    }

    @Override
    public String toString() {
        return "Distance [getMeter()=" + getMeter() + ", getDuration()=" + getDuration() + ", getKilometer()="
                + getKilometer() + ", getDurationInMinutes()=" + getDurationInMinutes() + "]";
    }

}
