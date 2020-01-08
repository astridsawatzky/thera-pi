package nebraska;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import utils.NUtils;

public class BCStatics2 {
    public static String getSHA256fromByte(byte[] b) {
        byte[] dig = null;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256", "BC");
            messageDigest.update(b);
            dig = messageDigest.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        return NUtils.toHex(dig);
    }

    public static String getMD5fromByte(byte[] b) {
        byte[] dig = null;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5", "BC");
            messageDigest.update(b);
            dig = messageDigest.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        return NUtils.toHex(dig);
    }

    public static String macheHexDump(String hexstring, int zeilenlaenge, String trenner) {
        String zeile = "";
        String ganzerString = "";
        int bytes = hexstring.length() / 2;

        int zeilen = bytes / zeilenlaenge;

        if (bytes <= zeilenlaenge) {
            zeilen = 1;
        } else if ((((Double.parseDouble(Integer.toString(bytes)) / Double.parseDouble(Integer.toString(zeilenlaenge)))
                % 2.0) != 0) && (bytes > zeilenlaenge)) {
            zeilen = zeilen + 1;
        }
        int stelle;
        int i2;
        for (int i = 0; i < zeilen; i++) {
            zeile = "";
            stelle = i * (zeilenlaenge * 2);
            for (i2 = 0; i2 < (zeilenlaenge * 2); i2 += 2) {
                try {
                    zeile = zeile + hexstring.substring(stelle + i2, stelle + i2 + 2) + trenner;
                } catch (Exception ex) {

                }
            }
            if ((((i * zeilenlaenge) + i2) - zeilenlaenge) < bytes) {
                ganzerString = ganzerString + zeile.trim() + "\n";// System.getProperty("line.separator");
            } else {
                ganzerString = ganzerString + zeile.trim();
            }
        }
        return ganzerString;
    }

    /***************************************************/
}
