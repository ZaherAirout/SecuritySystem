package crypto;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

public class AES {

    public static byte[] encrypt(byte[] text, String key) {
        byte ptexti[] = new byte[text.length];
        for (int i = 0; i < text.length; i++) {
            ptexti[i] = (byte) text[i];
        }
        char keyc[] = key.toCharArray();
        int keyi[] = new int[key.length()];
        for (int i = 0; i < key.length(); i++) {
            keyi[i] = (int) keyc[i];
        }
        int s[] = new int[256];
        int k[] = new int[256];
        for (int i = 0; i < 255; i++) {
            s[i] = i;
            k[i] = keyi[i % key.length()];
        }
        int j = 0, temp;
        for (int i = 0; i < 255; i++) {
            j = (j + s[i] + k[i]) % 256;
            temp = s[i];
            s[i] = s[j];
            s[j] = temp;
        }
        byte cipher[] = new byte[text.length];
        generateCipher(text.length, ptexti, s, cipher);
        return cipher;
    }

    private static void generateCipher(int textLength, byte[] ptexti, int[] s, byte[] cipher) {
        int i;
        int temp;
        int z;
        int j = 0;
        for (int l = 0; l < textLength; l++) {
            i = (l + 1) % 256;
            j = (j + s[i]) % 256;
            temp = s[i];
            s[i] = s[j];
            s[j] = temp;
            z = s[(s[i] + s[j]) % 256];
            cipher[l] = (byte) (z ^ ptexti[l]);
        }
    }

    public static byte[] decrypt(byte[] text, String key) {
        char keyc[] = key.toCharArray();
        int keyi[] = new int[key.length()];
        for (int i = 0; i < key.length(); i++)
            keyi[i] = (int) keyc[i];
        int s[] = new int[256];
        for (int i = 0; i < 256; i++)
            s[i] = i;
        int j = 0, temp;
        for (int i = 0; i < 256; i++) {
            j = (j + s[i] + keyi[i % key.length()]) % 256;
            temp = s[i];
            s[i] = s[j];
            s[j] = temp;
        }
        byte ptext[] = new byte[text.length];
        generateCipher(text.length, ptext, s, text);

        return ptext;
    }

    public static Key generateKey(byte[] sharedKey) {
        // AES supports 128 bit keys. So, just take first 16 bits of DH generated key.
        byte[] byteKey = new byte[16];
        System.arraycopy(sharedKey, 0, byteKey, 0, 16);
        // convert given key to AES format
        try {
            return new SecretKeySpec(byteKey, "AES");
        } catch (Exception e) {
            System.err.println("Error while generating key: " + e);
        }
        return null;
    }

}
