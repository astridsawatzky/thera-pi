package crypt;

import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

public class Verschluesseln {

    private transient String password = "jeLaengerJeBesserPasswortRehaVerwaltung";
    final private transient byte[] salt = { (byte) 0xc9, (byte) 0xc9, (byte) 0xc9, (byte) 0xc9, (byte) 0xc9,
            (byte) 0xc9, (byte) 0xc9, (byte) 0xc9 };

    /** Notwendige Instanzen */
    private Cipher encryptCipher;
    private Cipher decryptCipher;
    private Encoder encoder = Base64.getEncoder();
    private Decoder decoder = Base64.getMimeDecoder();

    /** Verwendete Zeichendecodierung */
    private String charset = "UTF16";

    protected Verschluesseln(String passWord) {

        this.password = passWord;
    }

    protected Verschluesseln() {

    }

    /**
     * Factory method returns Verschluesseln instance with standard settings
     * predefined by class
     * 
     * @return instance
     */
    public static Verschluesseln getInstance() {
        Verschluesseln ver = new Verschluesseln();
        ver.init();

        return ver;

    }

    /**
     * Factory method returns Verschluesseln instance with special password
     * 
     * @return instance
     */
    public static Verschluesseln getInstance(String pass) {
        Verschluesseln ver = new Verschluesseln();
        ver.password = pass;
        ver.init();

        return ver;

    }

    /**
     * initializes mechanism.
     * 
     * @param pass char[]
     * @throws SecurityException
     */
    private void init() throws SecurityException {
        try {
            final PBEParameterSpec ps = new PBEParameterSpec(salt, 20);
            final SecretKeyFactory kf = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
            final SecretKey k = kf.generateSecret(new PBEKeySpec(this.password.toCharArray()));
            encryptCipher = Cipher.getInstance("PBEWithMD5AndDES/CBC/PKCS5Padding");
            encryptCipher.init(Cipher.ENCRYPT_MODE, k, ps);
            decryptCipher = Cipher.getInstance("PBEWithMD5AndDES/CBC/PKCS5Padding");
            decryptCipher.init(Cipher.DECRYPT_MODE, k, ps);
        } catch (Exception e) {
            throw new SecurityException("Could not initialize CryptoLibrary: " + e.getMessage());
        }
    }

    /**
     * encrypts String.
     *
     * @param str String to be encrypted
     * @return String the encrypted String
     * @exception SecurityException Whenever something goes wrong
     */
    public synchronized String encrypt(String str) throws SecurityException {
        try {
            byte[] b = str.getBytes(this.charset);
            byte[] enc = encryptCipher.doFinal(b);
            return encoder.encodeToString(enc);
        } catch (Exception e) {
            throw new SecurityException("Could not encrypt: " + e.getMessage());
        }

    }

    /**
     * decrypts String which has been encrypted by this class.
     *
     * @param str the String to be decrypted
     * @return String the decrypted string
     * @exception SecurityException Description of the Exception
     */
    public synchronized String decrypt(String str) throws SecurityException {
        try {
            byte[] dec = decoder.decode(str);
            byte[] b = decryptCipher.doFinal(dec);
            return new String(b, this.charset);
        } catch (Exception e) {
            throw new SecurityException("Could not decrypt: " + e.getMessage());
        }
    }

    public String getPassword() {
        return password;
    }

}