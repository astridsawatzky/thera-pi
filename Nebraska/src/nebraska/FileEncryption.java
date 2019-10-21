package nebraska;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Utility class for encrypting/decrypting files.
 *
 * @author Michael Lones
 */
public class FileEncryption {

    Cipher pkCipher, aesCipher;
    byte[] aesKey;
    SecretKeySpec aeskeySpec;

    /**
     * Constructor: creates ciphers
     */
    public FileEncryption() throws GeneralSecurityException {
        // create RSA public key cipher
        pkCipher = Cipher.getInstance(Constants.PUBLIC_KEY_ALGORITHM);
        // create AES shared key cipher
        aesCipher = Cipher.getInstance(Constants.SECRET_KEY_ALGORITHM);
    }

    /**
     * Creates a new AES key
     */
    public void makeKey() throws NoSuchAlgorithmException {
        KeyGenerator kgen = KeyGenerator.getInstance(Constants.SECRET_KEY_ALGORITHM);
        kgen.init(Constants.AES_Key_Size);
        SecretKey key = kgen.generateKey();
        aesKey = key.getEncoded();
        aeskeySpec = new SecretKeySpec(aesKey, Constants.SECRET_KEY_ALGORITHM);
    }

    /**
     * Decrypts an AES key from a file using an RSA private key
     */
    public void loadKey(File in, File privateKeyFile) throws GeneralSecurityException, IOException {
        // read private key to be used to decrypt the AES key
        byte[] encodedKey = new byte[(int) privateKeyFile.length()];

        FileInputStream fileInputStream = new FileInputStream(privateKeyFile);

        fileInputStream.read(encodedKey);
        fileInputStream.close();
        // create private key
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(encodedKey);
        KeyFactory kf = KeyFactory.getInstance(Constants.PUBLIC_KEY_ALGORITHM);
        PrivateKey pk = kf.generatePrivate(privateKeySpec);

        // read AES key
        pkCipher.init(Cipher.DECRYPT_MODE, pk);
        aesKey = new byte[Constants.AES_Key_Size / 8];
        CipherInputStream is = new CipherInputStream(new FileInputStream(in), pkCipher);
        is.read(aesKey);
        is.close();
        aeskeySpec = new SecretKeySpec(aesKey, Constants.SECRET_KEY_ALGORITHM);
    }

    /**
     * Encrypts the AES key to a file using an RSA public key
     */
    public void saveKey(File out, File publicKeyFile) throws IOException, GeneralSecurityException {
        // read public key to be used to encrypt the AES key
        byte[] encodedKey = new byte[(int) publicKeyFile.length()];
        FileInputStream fileInputStream = new FileInputStream(publicKeyFile);
        fileInputStream.read(encodedKey);
        fileInputStream.close();
        // create public key
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(encodedKey);
        KeyFactory kf = KeyFactory.getInstance(Constants.PUBLIC_KEY_ALGORITHM);
        PublicKey pk = kf.generatePublic(publicKeySpec);

        // write AES key
        pkCipher.init(Cipher.ENCRYPT_MODE, pk);
        CipherOutputStream os = new CipherOutputStream(new FileOutputStream(out), pkCipher);
        os.write(aesKey);
        os.close();
    }

    /**
     * Encrypts and then copies the contents of a given file.
     */
    public void encrypt(File in, File out) throws IOException, InvalidKeyException {
        aesCipher.init(Cipher.ENCRYPT_MODE, aeskeySpec);

        FileInputStream is = new FileInputStream(in);
        CipherOutputStream os = new CipherOutputStream(new FileOutputStream(out), aesCipher);

        copy(is, os);

        os.close();
    }

    /**
     * Decrypts and then copies the contents of a given file.
     */
    public void decrypt(File in, File out) throws IOException, InvalidKeyException {
        aesCipher.init(Cipher.DECRYPT_MODE, aeskeySpec);

        CipherInputStream is = new CipherInputStream(new FileInputStream(in), aesCipher);
        FileOutputStream os = new FileOutputStream(out);

        copy(is, os);

        is.close();
        os.close();
    }

    /**
     * Copies a stream.
     */
    private void copy(InputStream is, OutputStream os) throws IOException {
        int i;
        byte[] b = new byte[1024];
        while ((i = is.read(b)) != -1) {
            os.write(b, 0, i);
        }
    }
}
