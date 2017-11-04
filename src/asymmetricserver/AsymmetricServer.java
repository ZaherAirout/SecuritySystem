package asymmetricserver;

import asymmetricclient.AsymmetricClient;
import crypto.RSA;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AsymmetricServer {

    private static final String cwd = System.getProperty("user.dir");
    private static final String PATH_OF_PRIVATE_KEY = String.join(File.separator, cwd, "asymmetricserver", "MyPrivateKey");
    private static final String PATH_OF_PUBLIC_KEY = String.join(File.separator, cwd, "asymmetricserver", "MyPublicKey");
    private static final int port = 1234;

    public static void main(String[] args) {
        try {
            // Generating keys and definig a server socket.
            RSA rsa = new RSA();
            rsa.generateKeys(PATH_OF_PRIVATE_KEY, PATH_OF_PUBLIC_KEY);
            ServerSocket welcomeSocket = new ServerSocket(port);
            System.out.println("server is running on port " + port);
            System.out.println(PATH_OF_PRIVATE_KEY);

            System.out.println("new Client Generated");
            ExecutorService executor = Executors.newFixedThreadPool(5);

            AsymmetricClient asymmetricClient = new AsymmetricClient();
            executor.execute(asymmetricClient);

            while (true) {
                Socket socket = welcomeSocket.accept();

                // Sending public key to client.
                File publicKeyFile = new File(PATH_OF_PUBLIC_KEY);
                ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(publicKeyFile));
                PublicKey publicKey = (PublicKey) inputStream.readObject();
                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                outputStream.writeObject(publicKey);

                // Reading a sentence from a client.
                DataInputStream inFromClient = new DataInputStream(socket.getInputStream());
                int length = inFromClient.readInt();
                byte[] sentenceArr = new byte[length];
                inFromClient.read(sentenceArr);
                File privateKeyFile = new File(PATH_OF_PRIVATE_KEY);
                inputStream = new ObjectInputStream(new FileInputStream(privateKeyFile));
                PrivateKey privateKey = (PrivateKey) inputStream.readObject();
                sentenceArr = rsa.decrypt(sentenceArr, privateKey);

                // Convert the sentence to uppercase.
                String sentence = new String(sentenceArr);
                sentence = sentence.toUpperCase();
                // Recieving public key from client.
                inputStream = new ObjectInputStream(socket.getInputStream());
                publicKey = (PublicKey) inputStream.readObject();

                // Encrypt the resulted sentnce and resend back to client.
                DataOutputStream outToClient = new DataOutputStream(socket.getOutputStream());
                sentenceArr = sentence.getBytes();
                sentenceArr = rsa.encrypt(sentenceArr, publicKey);
                outToClient.writeInt(sentenceArr.length);
                outToClient.write(sentenceArr);
                outToClient.flush();
                socket.close();
            }
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(AsymmetricServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
