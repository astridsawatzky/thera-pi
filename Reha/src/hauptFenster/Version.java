package hauptFenster;

public class Version {

    public static final String aktuelleVersion = "2019-06-06-DB=";
    public static final int major = 1;
    public static final int minor = 1;
    public static final int revision = 0;

    public static String number() {
        return String.format("%d.%d", major, minor);
    }

}
