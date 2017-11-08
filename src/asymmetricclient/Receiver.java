package asymmetricclient;

import Protocol.*;
import crypto.AES;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.Key;
import java.security.KeyException;
import java.util.HashMap;

public class Receiver extends Protocol.Receiver implements Runnable {
    private final HashMap<Client, byte[]> sessionKeys;
    private ServerSocket serverSocket;

    Receiver(int port, HashMap<Client, byte[]> sessionKeys) throws IOException {
        this.sessionKeys = sessionKeys;
        serverSocket = new ServerSocket(port);
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
            sessionKey = sessionKeys.get(msg.receiver);

        if (sessionKey == null || sessionKey.length == 0)
            throw new KeyException("Session Key is not found");

        Key key = AES.regenerateKey(sessionKey);
        String result = new String(AES.decrypt(msg.content, key));

        System.out.println(result);
    }

    @Override
    public void execute(CheckOnlineMessage msg) {

    }

    @Override
    public void execute(CloseConnectionMessage msg) {
        throw new UnsupportedOperationException("Close Connection Message must not be sent to clients.");
    }
}
