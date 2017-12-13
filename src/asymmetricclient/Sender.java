package asymmetricclient;

import Misc.Logger;
import Protocol.Client;
import Protocol.Messages.FileMessage;
import Protocol.Messages.Message;
import Protocol.Messages.TextMessage;
import crypto.AES;
import crypto.RSA;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.*;
import java.util.HashMap;

public class Sender implements Runnable {
    private HashMap<Client, Key> sessionKeys;
    private Message message;
    private PrivateKey privateKey;
    private Socket socket;

    Sender(HashMap<Client, Key> sessionKeys, PrivateKey privateKey, Socket socket) {
        this.sessionKeys = sessionKeys;
        this.privateKey = privateKey;
        this.socket = socket;
    }

    @Override
    public void run() {

        try {
//            Socket socket = new Socket(serverIP, serverPort);

            Logger logger = new Logger("./log.txt");
            logger.start();

            Client receiver = message.receiver;
            message.receiver.setSocket(null);
            message.sender.setSocket(null);

//            message.receiver = new Client(message.receiver);
//            message.sender = new Client(message.sender);

            // get Session key if exists, or create new one
            Key sessionKey = null;
            if (sessionKeys.get(receiver) != null)
                sessionKey = sessionKeys.get(receiver);

            message.sessionKey = null;

            if (sessionKey == null) {

                PublicKey publicKey = message.receiver.publicKey;

                // Create session key using AES
                sessionKey = AES.generateKey();

                // Encrypt session key using RSA asymmetric algorithm
                message.sessionKey = RSA.encrypt(sessionKey.getEncoded(), publicKey);
                sessionKeys.put(receiver, sessionKey);
            }

            // Encrypt content using AES symmetric algorithm
            message.content = AES.encrypt(message.content, sessionKey);

            // sign the message
            Signature signer = Signature.getInstance("SHA1withRSA");

            signer.initSign(this.privateKey);
            signer.update(message.content);

            message.digitalSignature = signer.sign();

            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(message);
            oos.flush();

            logger.stop("Sending Message ");

        } catch (IOException | NoSuchAlgorithmException | SignatureException | InvalidKeyException e) {
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
