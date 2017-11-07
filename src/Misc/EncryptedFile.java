package Misc;

import java.io.Serializable;

public class EncryptedFile implements Serializable {

    byte[] fileContent;
    String fileName;
    byte[] encryptedText;
    byte[] encryptedKey;

}
