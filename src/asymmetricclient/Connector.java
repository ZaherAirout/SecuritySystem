package asymmetricclient;

import Protocol.Client;
import Protocol.ConnectionMessage;
import Protocol.Message;
import crypto.RSA;

import java.io.*;
import java.net.Socket;
import java.security.PublicKey;
import java.util.List;

import static asymmetricclient.ClientController.PATH_OF_PRIVATE_KEY;
import static asymmetricclient.ClientController.PATH_OF_PUBLIC_KEY;

public class Connector implements Runnable {

    private final String serverIP;
    private final int serverPort;
    List<Client> clients;
    private Socket socket;
    private Client current;

    Connector(Client current, String serverIP, int serverPort) {
        this.current = current;
        this.serverIP = serverIP;
        this.serverPort = serverPort;
    }

    @Override
    public void run() {
        try {

            // Generate Asymmetric Keys
            RSA rsa = new RSA();
            rsa.generateKeys(PATH_OF_PRIVATE_KEY + current.getName(), PATH_OF_PUBLIC_KEY + current.getName());

            socket = new Socket(serverIP, serverPort);
            // send connection message
            Message message = new ConnectionMessage();

            message.receiver = null;
            message.sender = current;

            // Read Public key and set it in message
            File publicKeyFile = new File(PATH_OF_PUBLIC_KEY + current.getName());
            ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(publicKeyFile));
            message.sender.publicKey = (PublicKey) inputStream.readObject();

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
