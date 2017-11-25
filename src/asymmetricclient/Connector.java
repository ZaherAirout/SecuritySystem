package asymmetricclient;

import Protocol.Client;
import Protocol.ConnectionMessage;
import Protocol.Message;
import crypto.RSA;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class Connector implements Runnable {

    private final String serverIP;
    private final int serverPort;
    ObservableList<Client> clients;
    private Socket socket;
    private Client current;
    private KeyPair keyPair;

    Connector(Client current, ObservableList<Client> clients, String serverIP, int serverPort) {
        this.current = current;
        this.serverIP = serverIP;
        this.serverPort = serverPort;
        this.clients = clients;
    }

    public KeyPair getKeyPair() {
        return keyPair;
    }

    @Override
    public void run() {
        try {

            // Generate Asymmetric Keys

            keyPair = RSA.generateKeys();

            socket = new Socket(serverIP, serverPort);
            // send connection message
            Message message = new ConnectionMessage();

            message.receiver = null;
            message.sender = current;
            message.sender.publicKey = keyPair.getPublic();

            // Write message to output stream
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(message);
            oos.flush();

            socket.close();

        } catch (IOException | NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
        }


    }
}
