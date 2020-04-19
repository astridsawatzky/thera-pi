package org.therapi.reha.patient.neu;

import java.util.Objects;

public class PLZ {
  public  String plz;


    public PLZ(String PLZ) {
        this.plz = PLZ;
    }


    @Override
    public String toString() {
        return plz;
    }


    @Override
    public int hashCode() {
        return Objects.hash(plz);
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PLZ other = (PLZ) obj;
        return Objects.equals(plz, other.plz);
    }
}
