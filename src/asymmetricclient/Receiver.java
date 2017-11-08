package asymmetricclient;

import Protocol.*;
import crypto.AES;
import crypto.RSA;
import javafx.application.Platform;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.Key;
import java.security.KeyException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.util.HashMap;

public class Receiver extends Protocol.Receiver implements Runnable {
    private final HashMap<Client, byte[]> sessionKeys;
    private final ObservableList<String> messages;
    private ServerSocket serverSocket;
    private Client currentClient;
    private KeyPair pairKey;

    Receiver(Client currentClient, KeyPair pairKey, HashMap<Client, byte[]> sessionKeys, ObservableList<Client> clients, ObservableList<String> messages) throws IOException {
        super(clients);
        this.sessionKeys = sessionKeys;
        serverSocket = new ServerSocket(currentClient.port);
        this.currentClient = currentClient;
        this.pairKey = pairKey;
        this.messages = messages;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket socket = serverSocket.accept();

                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Message msg = (Message) ois.readObject();

                execute(msg);
            } catch (IOException | ClassNotFoundException | KeyException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void execute(ConnectionMessage msg) throws IOException {

    }

    @Override
    public void execute(TextMessage msg) throws IOException, ClassNotFoundException, KeyException {

        byte[] sessionKey = msg.sessionKey;

        if (sessionKey == null || sessionKey.length == 0)
            sessionKey = sessionKeys.get(msg.sender);

        if (sessionKey == null || sessionKey.length == 0)
            throw new KeyException("Session Key is not found");

        PrivateKey aPrivate = pairKey.getPrivate();
        Key key = AES.regenerateKey(RSA.decrypt(sessionKey, aPrivate));
        String result = new String(AES.decrypt(msg.content, key));

        messages.add("" + msg.receiver.getName() + ":  " + result);

    }

    @Override
    public void execute(CheckOnlineMessage msg) {

    }

    @Override
    public void execute(CloseConnectionMessage msg) {
        throw new UnsupportedOperationException("Close Connection Message must not be sent to clients.");
    }

    @Override
    public void execute(UpdateClientsMessage msg) {
        Platform.runLater(() -> {
            clients.clear();
            msg.clients.removeIf(client -> client.equals(currentClient));
            clients.addAll(msg.clients);
        });
    }
}
