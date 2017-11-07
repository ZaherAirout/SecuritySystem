package asymmetricserver;

import Misc.EncryptedFile;
import Misc.FileManager;
import Protocol.Client;
import Protocol.ServerHandler;
import asymmetricclient.AsymmetricClient;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsymmetricServer {

    private static final String cwd = System.getProperty("user.dir");
    private static final String PATH_OF_PRIVATE_KEY = String.join(File.separator, cwd, "asymmetricserver", "MyPrivateKey");
    private static final String PATH_OF_PUBLIC_KEY = String.join(File.separator, cwd, "asymmetricserver", "MyPublicKey");
    private static final int port = 1234;

    public static void main(String[] args) {
        if (true) {

            FileManager fileManager = FileManager.getInstance();
//            fileManager.clearTemp();
            String fileName = "2.txt";
            byte[] bytes = fileManager.readFile(fileName);

            EncryptedFile encryptedFile = fileManager.encryptFile(bytes, fileName, "zaher");

            fileManager.writeEncryptedFile(encryptedFile, ".\\TEMP");

            EncryptedFile encryptedFile1 = fileManager.readEncryptedFile(".\\TEMP\\2.txt.crypt");
            File file = fileManager.decryptFile(encryptedFile1, "zaher");
            fileManager.openFile(file);
            System.out.println("Fin");

        }
        return;

    }
}
