package environment;

enum OS {
    WIN, LINUX, MAC, UNKNOWN;

    boolean is(OS toCompare) {
        return this == toCompare;
    }

}
