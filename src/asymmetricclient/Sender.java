package asymmetricclient;

import Misc.Logger;
import Protocol.Client;
import Protocol.FileMessage;
import Protocol.Message;
import Protocol.TextMessage;
import crypto.AES;
import crypto.RSA;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.*;
import java.util.concurrent.ConcurrentHashMap;

public class Sender implements Runnable {
    private final ConcurrentHashMap<Client, Key> sessionKeys;
    private Message message;
    private String serverIP;
    private int serverPort;
    private PrivateKey privateKey;

    Sender(ConcurrentHashMap<Client, Key> sessionKeys,PrivateKey privateKey, String serverIP, int serverPort) {
        this.sessionKeys = sessionKeys;
        this.serverIP = serverIP;
        this.serverPort = serverPort;
        this.privateKey = privateKey;
    }

    @Override
    public void run() {

        try {
            Socket socket = new Socket(serverIP, serverPort);

            Logger logger = new Logger("./log.txt");
            logger.start();

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

            // sign the message
            Signature signature = Signature.getInstance("SHA1withDSA", "SUN");

            signature.initSign(privateKey);
            signature.update(message.content);

            message.digitalSignature = signature.sign();

            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(message);
            oos.flush();

            logger.stop("Sending Message ");

            socket.close();

        } catch (IOException | NoSuchAlgorithmException | NoSuchProviderException | SignatureException | InvalidKeyException e) {
            e.printStackTrace();
        }
    }

    public void setMessage(TextMessage message) {
        this.message = message;
    }

    public void setMessage(FileMessage message) {
        this.message = message;
    }
}
