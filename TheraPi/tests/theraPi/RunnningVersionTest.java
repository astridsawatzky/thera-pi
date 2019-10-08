package theraPi;

import static org.junit.Assert.*;

import org.junit.Test;

public class RunnningVersionTest {
    RunnningVersion version1_5 = new RunnningVersion() {
        String current() {
            return "1.5.";
        };
    };

    RunnningVersion version1_6 = new RunnningVersion() {
        String current() {
            return "1.6.";
        };
    };
    RunnningVersion version1_7 = new RunnningVersion() {
        String current() {
            return "1.7.";
        };
    };
    RunnningVersion version1_8 = new RunnningVersion() {
        String current() {
            return "1.8.";
        };
    };
    RunnningVersion version9 = new RunnningVersion() {
        String current() {
            return "9.";
        };
    };

    RunnningVersion version10 = new RunnningVersion() {
        String current() {
            return "10.";
        };
    };
    RunnningVersion version11 = new RunnningVersion() {
        String current() {
            return "11.";
        };
    };

    RunnningVersion versionUnknown = new RunnningVersion() {
        String current() {
            return null;
        };
    };

    @Test
    public void versionenUnter1_8_sindUngueltig() throws Exception {
        assertFalse(version1_5.isSupported());
        assertFalse(version1_6.isSupported());
        assertFalse(version1_7.isSupported());
    }

    @Test
    public void version1_8_istGueltig() throws Exception {
        assertTrue(version1_8.isSupported());
    }

    @Test
    public void versionenUeber1_8_sindGueltig() throws Exception {
        assertTrue(version9.isSupported());
        assertTrue(version10.isSupported());
    }

    @Test
    public void nichtErmittelteVersionIstGueltig() throws Exception {
        assertTrue(versionUnknown.isSupported());
    }

}
