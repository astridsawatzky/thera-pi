package opRgaf;


enum Strategy {
    gleich {
        @Override
        boolean compare(Comparable first, Comparable second) {
            if (first == null) {
                return second == null;
            }
            return first.compareTo(second) == 0;
        }
    },
    kleiner {
        @Override
        boolean compare(Comparable first, Comparable second) {
            return first.compareTo(second) < 0;
        }
    },
    groesser {
        @Override
        boolean compare(Comparable first, Comparable second) {

            return first.compareTo(second) > 0;
        }
    },
    kleinerOderGleich {
        @Override
        boolean compare(Comparable first, Comparable second) {
            return first.compareTo(second) <= 0;
        }
    },
    groesserOderGleich

    {
        @Override
        boolean compare(Comparable first, Comparable second) {
            return first.compareTo(second) >= 0;
        }
    };

    abstract boolean compare(Comparable first, Comparable second);
}
