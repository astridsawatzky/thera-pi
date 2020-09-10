package hmv;

import java.util.Objects;

public class Diagnose {

    final static String version = "HMR2020";
    final Icd10 icd10_1;
    final Icd10 icd10_2;
    final String diagnosegruppe;
    final Leitsymptomatik leitsymptomatik; // [a-c|x]

    public Diagnose(Icd10 icd10_1, Icd10 icd10_2, String diagnosegruppe, Leitsymptomatik leitsymptomatik) {
        super();
        this.icd10_1 = icd10_1;
        this.icd10_2 = icd10_2;
        this.diagnosegruppe = diagnosegruppe;
        this.leitsymptomatik = leitsymptomatik;
    }


    @Override
    public String toString() {
        return "Diagnose [version=" + version + ", icd10_1=" + icd10_1 + ", icd10_2=" + icd10_2 + ", diagnosegruppe="
                + diagnosegruppe + ", leitsymptomatik=" + leitsymptomatik + "]";
    }

    @Override
    public int hashCode() {
        return Objects.hash(diagnosegruppe, icd10_1, icd10_2, leitsymptomatik);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Diagnose))
            return false;
        Diagnose other = (Diagnose) obj;
        return Objects.equals(diagnosegruppe, other.diagnosegruppe) && Objects.equals(icd10_1, other.icd10_1)
                && Objects.equals(icd10_2, other.icd10_2) && Objects.equals(leitsymptomatik, other.leitsymptomatik);
    }

}
