package systemTools;

public class IntegerTools {

    public static int trailNullAndRetInt(String zahl) {
        if (zahl == null || zahl.isEmpty()) {
            return 0;
        }

        int parseInt = Integer.parseInt(zahl);
        if (parseInt == 0) {
            throw new NumberFormatException();
        }
        return parseInt < 10 ? 0 : parseInt;
    }

}
