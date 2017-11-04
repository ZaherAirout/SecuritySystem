package asymmetricclient;

import crypto.RSA;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AsymmetricClient implements Runnable {

    private static final String PATH_OF_PRIVATE_KEY = "D:\\Client\\MyPrivateKey";
    private static final String PATH_OF_PUBLIC_KEY = "D:\\Client\\MyPublicKey";

    @Override
    public void run() {
        try {
            // Generating keys and defining a socket.
            RSA rsa = new RSA();
            rsa.generateKeys(PATH_OF_PRIVATE_KEY, PATH_OF_PUBLIC_KEY);
            Socket socket = new Socket("localhost", 1234);

            // Reading public key of server from socket, and reading private key of client from local pc.
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            PublicKey publicKey = (PublicKey) inputStream.readObject();

            // Entering the sentence which will be sent after encryption.
            System.out.println("Please enter the sentence you want to send:");
//            Scanner s = new Scanner(System.in);
//            String sentence = s.nextLine();
            String sentence = "hello World !!";

            byte[] sentenceArr = sentence.getBytes();
            sentenceArr = rsa.encrypt(sentenceArr, publicKey);
            DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());
            outToServer.writeInt(sentenceArr.length);
            outToServer.write(sentenceArr);

            // Sending public key to server.
            File publicKeyFile = new File(PATH_OF_PUBLIC_KEY);
            inputStream = new ObjectInputStream(new FileInputStream(publicKeyFile));
            publicKey = (PublicKey) inputStream.readObject();
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.writeObject(publicKey);

            // Reading the uppercase sentence back from server using the socket.
            DataInputStream inFromServer = new DataInputStream(socket.getInputStream());
            int length = inFromServer.readInt();
            sentenceArr = new byte[length];
            inFromServer.read(sentenceArr);

            // Reading private key from local pc and dycrypt the received sentence.
            File privateKeyFile = new File(PATH_OF_PRIVATE_KEY);
            inputStream = new ObjectInputStream(new FileInputStream(privateKeyFile));
            PrivateKey privateKey = (PrivateKey) inputStream.readObject();
            sentenceArr = rsa.decrypt(sentenceArr, privateKey);
            sentence = new String(sentenceArr);
            System.out.println(sentence);

            socket.close();
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(AsymmetricClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


}
