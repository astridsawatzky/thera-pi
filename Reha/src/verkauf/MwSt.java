package verkauf;

public enum MwSt {

    frei {
        @Override
        public double convertToDB() {
            return 0;
        }
    },
    vermindert {
        @Override
        public double convertToDB() {
            return 7;
        }
    },
    voll {
        @Override
        public double convertToDB() {
            return 19;
        }
    };

    public static MwSt of(double mwst) {
        int mw = (int) mwst;
        switch (mw) {
        case 19:
            return voll;
        case 16:
            return voll;

        case 7:
            return vermindert;
        case 5:
            return vermindert;

        case 0:
            return frei;
        default:
            throw new RuntimeException("unkown value for MwSt" + mwst);
        }
    }

    public abstract double convertToDB();

}
