package org.thera_pi.updater;

import java.util.Objects;

public class Version {

    public static final String aktuelleVersion = "2019-10-08-DB=";
    public  final int major;
    public  final int minor ;
    public  final int revision ;

    public Version() {
         major = 1;
         minor = 1;
        revision = 3;
    }

    Version(int major, int minor, int revision) {
        this.major = major;
        this.minor = minor;
       this.revision = revision;
    }

    public  String number() {
        return String.format("%d.%d.%d", major, minor, revision);
    }

    public static String getAktuelleversion() {
        return aktuelleVersion;
    }

    public  int getMajor() {
        return major;
    }

    public  int getMinor() {
        return minor;
    }

    public  int getRevision() {
        return revision;
    }

    @Override
    public String toString() {
        return "Version [major=" + major + ", minor=" + minor + ", revision=" + revision + "]";
    }

    @Override
    public int hashCode() {
        return Objects.hash(major, minor, revision);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Version other = (Version) obj;
        return major == other.major && minor == other.minor && revision == other.revision;
    }

}
