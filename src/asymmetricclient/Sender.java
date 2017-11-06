package asymmetricclient;

import Protocol.Client;
import Protocol.Message;
import Protocol.TextMessage;
import crypto.AES;
import crypto.RSA;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;

public class Sender implements Runnable {
    private final HashMap<Client, String> sessionKeys;
    private final Socket socket;
    private Message message;
    private String content;

    Sender(Socket socket, HashMap<Client, String> sessionKeys) {
        this.sessionKeys = sessionKeys;
        this.socket = socket;
    }

    @Override
    public void run() {

        try {
            Client receiver = message.receiver;

            // get Session key if exists, or create new one
            String sessionKey = sessionKeys.get(receiver);
            message.sessionKey = null;

            if(sessionKey == null)
            {
                // TODO: Create session key using AES
                sessionKey = "123";

                // Store session key for late usage.
                sessionKeys.put(message.sender, sessionKey);

                message.sessionKey = RSA.encrypt(sessionKey.getBytes(), message.receiver.getPublicKey());
            }

            // TODO: which content to encrypt
            // encrypt content
            message.content = AES.encrypt(content.getBytes(), sessionKey);

            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(message);
            oos.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setMessage(TextMessage message) {
        this.message = message;
    }

    void setContent(String content) {
        this.content = content;
    }
}
