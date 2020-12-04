package openMaps;

import org.json.JSONObject;

public class Distance {
    int distanceInM = 0;
    private int seconds = 0;

    Distance parseOpenRouteServiceAnswer(String answer) {

        JSONObject obj = new JSONObject(answer);
        return this.withDistance(Double.parseDouble(new JsonBruteForce().findFirstValuetoKey(obj, "distance").toString()))
                   .withDuration(Double.parseDouble(new JsonBruteForce().findFirstValuetoKey(obj, "duration").toString()));

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
