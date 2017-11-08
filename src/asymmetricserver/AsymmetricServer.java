package asymmetricserver;

import Misc.EncryptedFile;
import Misc.FileManager;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class AsymmetricServer {

    private static final String cwd = System.getProperty("user.dir");
    private static final String PATH_OF_PRIVATE_KEY = String.join(File.separator, cwd, "asymmetricserver", "MyPrivateKey");
    private static final String PATH_OF_PUBLIC_KEY = String.join(File.separator, cwd, "asymmetricserver", "MyPublicKey");
    private static final int port = 1234;

    public static void main(String[] args) {

        FileManager fileManager = FileManager.getInstance();
//            fileManager.clearTemp();
        String fileName = "1.jpg";
        byte[] bytes = fileManager.readFile(fileName);

        EncryptedFile encryptedFile = fileManager.encryptFile(bytes, fileName, "zaher");

        fileManager.writeEncryptedFile(encryptedFile, ".\\TEMP");

        EncryptedFile encryptedFile1 = fileManager.readEncryptedFile(".\\TEMP\\1.jpg.crypt");
        File file = fileManager.decryptFile(encryptedFile1, "zaher");
        fileManager.openFile(file);
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        fileManager.clearTemp();
        System.out.println("Fin");

    }
}
