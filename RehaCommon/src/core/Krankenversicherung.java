package core;

import java.util.Objects;
import java.util.Optional;

public class Krankenversicherung {
    Optional<Befreiung> befreit =Optional.empty();
    Optional<Krankenkasse> kk;
    VersichertenStatus status;

    String versicherungsnummer;
    public Krankenversicherung(Optional<Krankenkasse> krankenkasse, String versicherungsnummer, VersichertenStatus status, Befreiung befreit) {
       this(
        krankenkasse,versicherungsnummer,status,Optional.ofNullable(befreit) );
    }
    public Krankenversicherung(Optional<Krankenkasse> krankenkasse, String versicherungsnummer, VersichertenStatus status,
            Optional<Befreiung> befreit) {
        this.kk = krankenkasse;
        this.versicherungsnummer = versicherungsnummer;
        this.status = status;
        this.befreit = befreit;
    }


    public Optional<Befreiung> getBefreit() {
        return befreit;
    }
    public Optional<Krankenkasse> getKk() {
        return kk;
    }
    public VersichertenStatus getStatus() {
        return status;
    }
    public String getVersicherungsnummer() {
        return versicherungsnummer;
    }
    @Override
    public int hashCode() {
        return Objects.hash(befreit, kk, status, versicherungsnummer);
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Krankenversicherung other = (Krankenversicherung) obj;
        return Objects.equals(befreit, other.befreit) && Objects.equals(kk, other.kk)
                && Objects.equals(status, other.status)
                && Objects.equals(versicherungsnummer, other.versicherungsnummer);
    }
    @Override
    public String toString() {
        return "Krankenversicherung [befreit=" + befreit + ", kk=" + kk + ", status=" + status
                + ", versicherungsnummer=" + versicherungsnummer + "]";
    }


}
