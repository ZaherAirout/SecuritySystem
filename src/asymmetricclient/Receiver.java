package asymmetricclient;

import Protocol.*;
import crypto.AES;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.security.KeyException;
import java.util.HashMap;

public class Receiver extends Protocol.Receiver implements Runnable {
    private final HashMap<Client, String> sessionKeys;
    private Socket socket;

    Receiver(Socket socket, HashMap<Client, String> sessionKeys) {
        this.socket = socket;
        this.sessionKeys = sessionKeys;
    }

    @Override
    public void run() {
        while (true) {
            try {

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

        String sessionKey = new String(msg.sessionKey);

        if (sessionKey == null || sessionKey.isEmpty())
            sessionKey = sessionKeys.get(msg.sender);

        if (sessionKey == null || sessionKey.isEmpty())
            sessionKey = sessionKeys.get(msg.receiver);

        if (sessionKey == null || sessionKey.isEmpty())
            throw new KeyException("Session Key is not found");

        String result = new String(AES.decrypt(msg.content, sessionKey));

        System.out.println(result);
    }

    @Override
    public void execute(CheckOnlineMessage msg) {

    }
}
