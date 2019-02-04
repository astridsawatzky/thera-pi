package theraPi;

public class RunnningVersion {
    private String runtimeVersion = Runtime.class.getPackage().getImplementationVersion();
    protected String unterstuetzte = "1.8.";

    boolean isSupported() {
        return !runtimeVersion.startsWith(unterstuetzte);
    }

     String required() {
        return  unterstuetzte ;
    }

    String current() {
        return runtimeVersion;
    }

}
