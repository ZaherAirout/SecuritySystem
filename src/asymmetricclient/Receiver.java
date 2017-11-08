package asymmetricclient;

import Misc.FileManager;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Receiver extends Protocol.Receiver implements Runnable {
    private final ConcurrentHashMap<Client, Key> sessionKeys;
    private final ObservableList<String> messages;
    private final ExecutorService executorService;
    private final int nThread = 4;
    private ServerSocket serverSocket;
    private Client currentClient;
    private KeyPair pairKey;

    Receiver(Client currentClient, KeyPair pairKey, ConcurrentHashMap<Client, Key> sessionKeys, ObservableList<Client> clients, ObservableList<String> messages) throws IOException {
        super(clients);

        this.sessionKeys = sessionKeys;
        this.serverSocket = new ServerSocket(currentClient.port);
        this.currentClient = currentClient;
        this.pairKey = pairKey;
        this.messages = messages;
        this.executorService = Executors.newFixedThreadPool(nThread);
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket socket = serverSocket.accept();

                executorService.execute(() -> {
                    try {
                        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                        Message msg = (Message) ois.readObject();
                        execute(msg);
                    } catch (IOException | ClassNotFoundException | KeyException e) {
                        e.printStackTrace();
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void execute(ConnectionMessage msg) throws IOException {

    }

    @Override
    public void execute(TextMessage msg) throws IOException, ClassNotFoundException, KeyException {

        Key sessionKey = null;

        byte[] sessionKeyArr = msg.sessionKey;
        if (sessionKeyArr != null) {
            sessionKey = AES.regenerateKey(RSA.decrypt(sessionKeyArr, pairKey.getPrivate()));
            sessionKeys.put(msg.sender, sessionKey);
        }
        if (sessionKey == null)
            sessionKey = sessionKeys.get(msg.sender);

        if (sessionKey == null)
            throw new KeyException("Session Key is not found");

        String result = new String(AES.decrypt(msg.content, sessionKey));

        Platform.runLater(() -> messages.add("" + msg.receiver.getName() + ":  " + result));
    }

    @Override
    public void execute(FileMessage msg) throws KeyException {
        Key sessionKey = null;

        byte[] sessionKeyArr = msg.sessionKey;
        if (sessionKeyArr != null) {
            sessionKey = AES.regenerateKey(RSA.decrypt(sessionKeyArr, pairKey.getPrivate()));
            sessionKeys.put(msg.sender, sessionKey);
        }
        if (sessionKey == null)
            sessionKey = sessionKeys.get(msg.sender);

        if (sessionKey == null)
            throw new KeyException("Session Key is not found");


        FileManager fileManager = FileManager.getInstance();

        byte[] decryptedContent = AES.decrypt(msg.content, sessionKey);
        fileManager.openFile(fileManager.writeFile(msg.filename, decryptedContent));
        messages.add("" + msg.receiver.getName() + ":  File Received -->" + msg.filename);

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
