package asymmetricclient;

import Protocol.Client;
import Protocol.FileMessage;
import Protocol.Message;
import Protocol.TextMessage;
import crypto.AES;
import crypto.RSA;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.Key;
import java.util.concurrent.ConcurrentHashMap;

public class Sender implements Runnable {
    private final ConcurrentHashMap<Client, Key> sessionKeys;
    private Message message;
    private String serverIP;
    private int serverPort;

    Sender(ConcurrentHashMap<Client, Key> sessionKeys, String serverIP, int serverPort) {
        this.sessionKeys = sessionKeys;
        this.serverIP = serverIP;
        this.serverPort = serverPort;
    }

    @Override
    public void run() {

        try {
            Socket socket = new Socket(serverIP, serverPort);

            Client receiver = message.receiver;

            // get Session key if exists, or create new one
            Key sessionKey = null;
            if (sessionKeys.get(receiver) != null)
                sessionKey = sessionKeys.get(receiver);

            message.sessionKey = null;

            if (sessionKey == null) {
                // Create session key using AES
                sessionKey = AES.generateKey();

                // Store session key for late usage.
                assert sessionKey != null;
                sessionKeys.put(receiver, sessionKey);

                // Encrypt session key using RSA asymmetric algorithm
                message.sessionKey = RSA.encrypt(sessionKey.getEncoded(), message.receiver.getPublicKey());
            }

            // Encrypt content using AES symmetric algorithm
            message.content = AES.encrypt(message.content, sessionKey);

            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(message);
            oos.flush();

            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setMessage(TextMessage message) {
        this.message = message;
    }

    public void setMessage(FileMessage message) {
        this.message= message;
    }
}
