package hmv;

import java.util.Objects;

import specs.Contracts;

public class Leitsymptomatik {
    final static String A = "A";
    final static String B = "B";
    final static String C = "C";
    final static String X = "X";



    public Leitsymptomatik(String kennung, String text2) {
        Contracts.require(kennung != null, "kennung must not be null");
        this.kennung = kennung.toUpperCase();
    }
    String kennung ;// [a-c|x]
    String text;
    @Override
    public int hashCode() {
        return Objects.hash(kennung, text);
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Leitsymptomatik))
            return false;
        Leitsymptomatik other = (Leitsymptomatik) obj;
        return kennung.equalsIgnoreCase(other.kennung) && Objects.equals(text, other.text);
    }
    @Override
    public String toString() {
        return "Leitsymptomatik [kennung=" + kennung + ", text=" + text + "]";
    }
}
