package core;

import java.util.Objects;

public class Emailadresse {
    public static final Emailadresse EMPTY = new Emailadresse("");

    public Emailadresse(String emailA) {
        this.adresse=emailA;
    }

    String adresse;

    public String getAdresse() {
        return adresse;
    }

    @Override
    public int hashCode() {
        return Objects.hash(adresse);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Emailadresse other = (Emailadresse) obj;
        return Objects.equals(adresse, other.adresse);
    }

}
