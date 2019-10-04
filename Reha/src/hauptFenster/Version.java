package hauptFenster;

public class Version {

    public static final String aktuelleVersion = "2019-10-01-DB=";
    public static final int major = 1;
    public static final int minor = 1;
    public static final int revision = 2;

    public static String number() {
        return String.format("%d.%d.%d", major, minor,revision);
    }

}
