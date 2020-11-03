package therapi.abrechnung.split;

import java.util.HashSet;
import java.util.Set;

import rezept.Money;
import rezept.Rezeptnummer;

public class Splitting {
    public static final Set<Rezeptnummer> splitter = new HashSet<>();

    public void add(Rezeptnummer nr) {
        splitter.add(nr);
    }

    public Money berechnePauschale(Rezeptnummer nr, Money pausch) {
        if (splitter.contains(nr)) {
            return Money.ZERO;
        }

        return pausch;
    }

    public boolean isSplitter(Rezeptnummer nr) {
        return splitter.contains(nr);
    }

}
