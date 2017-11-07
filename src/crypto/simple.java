package crypto;

public class simple {
    public static byte[] encrypt(byte[] text, String key) {
        byte ptextc[] = text;
        byte ptexti[] = new byte[text.length];
        for (int i = 0; i < text.length; i++) {
            ptexti[i] = (byte) ptextc[i];
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
        int i, z;
        j = 0;
        byte cipher[] = new byte[text.length];
        for (int l = 0; l < text.length; l++) {
            i = (l + 1) % 256;
            j = (j + s[i]) % 256;
            temp = s[i];
            s[i] = s[j];
            s[j] = temp;
            z = s[(s[i] + s[j]) % 256];
            cipher[l] = (byte) (z ^ ptexti[l]);
        }
        return cipher;
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
        int i, Keystream;
        j = 0;
        byte ptext[] = new byte[text.length];
        byte cipher[] = text;
        for (int t = 0; t < text.length; t++) {
            i = (t + 1) % 256;
            j = (j + s[i]) % 256;
            temp = s[i];
            s[i] = s[j];
            s[j] = temp;
            Keystream = s[(s[i] + s[j]) % 256];
            ptext[t] = (byte) (Keystream ^ cipher[t]);
        }
        return ptext;
    }

}
