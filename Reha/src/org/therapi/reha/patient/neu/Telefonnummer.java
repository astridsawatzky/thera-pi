package org.therapi.reha.patient.neu;

import java.util.Objects;

public class Telefonnummer {

    public Telefonnummer(String telefon) {
        nummer = telefon;
    }

public    String nummer;

@Override
public int hashCode() {
    return Objects.hash(nummer);
}

@Override
public boolean equals(Object obj) {
    if (this == obj)
        return true;
    if (obj == null)
        return false;
    if (getClass() != obj.getClass())
        return false;
    Telefonnummer other = (Telefonnummer) obj;
    return Objects.equals(nummer, other.nummer);
}

@Override
public String toString() {
    return "Telefonnummer [nummer=" + nummer + "]";
}

}
