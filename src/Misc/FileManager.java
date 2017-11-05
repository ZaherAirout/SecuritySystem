package Misc;

import crypto.AES;

import javax.crypto.Cipher;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Key;
import java.util.Base64;
import java.util.Objects;

// FileManager implementation for Secure File Transfer with "Diffie-Hellman key exchange"
public class FileManager {
    private String EncryptionFlag = "TEST";
    private String TEMP = ".\\TEMP\\";
    // FileManager singleton instance for Multithread-safe implementation
    private static final FileManager instance = new FileManager();

    // file input stream for reading text files
    private FileInputStream inStream;

    // file output stream for writing text files
    private FileOutputStream outStream;


    // readFile method reads file from given path and returns string of file contents
    public byte[] readFile(String fileName) {
        byte[] result = new byte[0];
        Path path = Paths.get(fileName);
        try {
            result = Files.readAllBytes(path);
        } catch (IOException e) {
            System.err.println("Error while reading the file " + fileName + ": " + e);
        }

        return result;
    }

    // writeFile method creates a file with given name and fills it with given content.
    public void writeFile(String fileName, byte[] fileContentBytes) {
        try {
            outStream = new FileOutputStream(fileName);

            outStream.write(fileContentBytes);

        } catch (Exception e) {
            System.err.println("Error while writing into file " + fileName + ": " + e);
        } finally {
            try {
                if (outStream != null) {
                    outStream.close();
                }
            } catch (Exception ex) {
                System.err.println("Error while closing File I/O: " + ex);
            }
        }
    }

    // splits filename from the path and returns filename string
    public String getFileName(String filePath) {

        String[] split = filePath.split("\\/");

        return split[split.length - 1];
    }

    // encryptFile method takes plainText and encrypts it with secretKey, returns encrypted string
    public EncryptedFile encryptFile(byte[] plainByte, String fileName, String key) {
        EncryptedFile encryptedFile = new EncryptedFile();
        encryptedFile.fileContent = AES.encrypt(plainByte, key);
        encryptedFile.encryptedText = AES.encrypt(EncryptionFlag.getBytes(), key);
        encryptedFile.fileName = fileName;
        return encryptedFile;
    }

    // decryptFile method takes encrypted text and decrypts it with secretKey, returns decrypted string
    public File decryptFile(EncryptedFile encryptedFile, String key) {
        File file = new File(TEMP + encryptedFile.fileName);
        DataOutputStream dos;
        try {
            dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
            String testString = new String(AES.decrypt(encryptedFile.encryptedText, key));
            if (!Objects.equals(testString, EncryptionFlag))
                throw new Exception("Password Mismatch");
            byte[] decryptFile = AES.decrypt(encryptedFile.fileContent, key);
            dos.write(decryptFile);
            dos.flush();
            dos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }


    // returns instance of FileManager singleton class.
    public static FileManager getInstance() {
        return instance;
    }

    public void openFile(File file) {
        try {
            Desktop.getDesktop().open(file);
        } catch (IOException e) {
            System.err.println("Error while opening the File : " + e);
        }
    }

    public void clearTemp() {

    }
}