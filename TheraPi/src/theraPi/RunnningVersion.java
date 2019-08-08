package theraPi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RunnningVersion {
    protected int unterstuetzte = 8;
    private Logger logger =LoggerFactory.getLogger(RunnningVersion.class);;

    boolean isSupported() {
        try {

            return versionnumber() >=required();

        } catch (Exception e) {

            logger.info("Version konnte nicht geprueft werden", e);
        }
        return true;
    }

    private int versionnumber() {
        String version = current();
        if(version.startsWith("1.")) {
            version = version.substring(2, 3);
        } else {
            int dot = version.indexOf(".");
            if(dot != -1) { version = version.substring(0, dot); }
        } return Integer.parseInt(version);
    }

    int required() {
        return unterstuetzte;
    }

    String current() {
        return Runtime.class.getPackage()
                .getSpecificationVersion();
    }

}
