package rehaBillEdit.Tools;

public class IntegerTools {
    public static int trailNullAndRetInt(String zahl) {
        int ret = 0;
        if (zahl == null) {
            return ret;
        }
        int lang = zahl.length();
        if (lang == 0) {
            return ret;
        }
        int i = 0;
        for (i = 0; i < lang; i++) {
            if (!"0".equals(zahl.substring(i, i + 1))) {
                break;
            }
        }
        if (i == (lang - 1)) {
            return ret;
        }
        return Integer.parseInt(zahl.substring(i));
    }
}
