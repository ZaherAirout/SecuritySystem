package asymmetricclient;

import Protocol.Client;
import Protocol.ConnectionMessage;
import Protocol.Message;
import crypto.RSA;

import java.io.*;
import java.net.Socket;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;

import static asymmetricclient.AsymmetricClient.PATH_OF_PRIVATE_KEY;
import static asymmetricclient.AsymmetricClient.PATH_OF_PUBLIC_KEY;

public class Connector implements Runnable {

    List<Client> clients;
    PublicKey publicKey;
    PrivateKey privateKey;
    Socket socket;
    Client current;

    public Connector(Client current) {
        this.current = current;
    }

    @Override
    public void run() {
        try {

            // Generate Asymmetric Keys
            RSA rsa = new RSA();
            rsa.generateKeys(PATH_OF_PRIVATE_KEY + current.getName(), PATH_OF_PUBLIC_KEY + current.getName());

            socket = new Socket("localhost", 1234);
            // send connection message
            Message message = new ConnectionMessage();

            message.receiver = null;
            message.sender = current;

            // Read Public key and set it in message
            File publicKeyFile = new File(PATH_OF_PUBLIC_KEY + current.getName());
            ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(publicKeyFile));
            publicKey = (PublicKey) inputStream.readObject();
            message.sender.publicKey = publicKey;

            // Write message to output stream
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(message);
            oos.flush();

            // TODO: get online clients from server
            // socket.read()

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }


    }
}
