package hmv;

import java.util.Objects;

import specs.Contracts;

public class Leitsymptomatik {
    final static String A = "A";
    final static String B = "B";
    final static String C = "C";
    final static String X = "X";



    public Leitsymptomatik(String kennung, String langtext) {
        Contracts.require(kennung != null, "kennung must not be null");
        Contracts.require(langtext != null, "langtext must not be null");
        this.kennung = kennung.toUpperCase();
        this.text = langtext;
    }
   final String kennung ;// [a-c|x]
   final String text;
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
        return Objects.equals(kennung, other.kennung) && Objects.equals(text, other.text);
    }
    @Override
    public String toString() {
        return "Leitsymptomatik [kennung=" + kennung + ", text=" + text + "]";
    }
}
