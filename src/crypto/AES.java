package crypto;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;

public class AES {

    // encryptFile method takes plainText and encrypts it with secretKey, returns encrypted string
    static public byte[] encrypt(byte[] plainByte, Key secretKey) {
        byte[] encrypted = new byte[0];
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            byte[] encryptedByte = cipher.doFinal(plainByte);

            Base64.Encoder encoder = Base64.getEncoder();

            encrypted = encoder.encode(encryptedByte);

        } catch (Exception e) {
            System.err.println("Error while initializing Cipher while encrypting text: " + e);
        }
        return encrypted;
    }

    // decryptFile method takes encrypted text and decrypts it with secretKey, returns decrypted string
    static public byte[] decrypt(byte[] encrypted, Key secretKey) {
        Base64.Decoder decoder = Base64.getDecoder();

        byte[] encryptedTextByte = decoder.decode(encrypted);

        byte[] decryptedByte = new byte[0];
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            decryptedByte = cipher.doFinal(encryptedTextByte);

        } catch (Exception e) {
            System.err.println("Error while initializing Cipher while decrypting text: " + e);
        }

        return decryptedByte;
    }

    public static Key generateKey() {

        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256);
            return keyGen.generateKey();
        } catch (Exception e) {
            System.err.println("Error while generating key: " + e);
        }
        return null;
    }

    public static Key regenerateKey(byte[] keyByte) {
        return new SecretKeySpec(keyByte, 0, keyByte.length, "AES");

    }


}
