package asymmetricclient;

import Protocol.Client;
import Protocol.Messages.ConnectionMessage;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Connector implements Runnable {

    private static Socket socketToCA;
    private final String serverIP;
    private final int serverPort;
    ObservableList<Client> clients;
    private Socket socket;
    private Client current;
    private KeyPair keyPair;
    private X509Certificate certificate;


    Connector(Client current, ObservableList<Client> clients, String serverIP, int serverPort) {
        this.current = current;
        this.serverIP = serverIP;
        this.serverPort = serverPort;
        this.clients = clients;
    }


    public X509Certificate getCertificate() {
        return certificate;
    }

    public KeyPair getKeyPair() {
        return keyPair;
    }

    @Override
    public void run() {
        try {

            // Generate Asymmetric Keys
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(1024);
            keyPair = kpg.generateKeyPair();

            /* Create a Certificate */
            try {
                socketToCA = new Socket("localhost", 20000);
                ObjectOutputStream output = new ObjectOutputStream(socketToCA.getOutputStream());
                send(current, output);
                send(keyPair.getPublic(), output);

                ObjectInputStream input = new ObjectInputStream(socketToCA.getInputStream());

                certificate = (X509Certificate) receive(input);

            } catch (IOException ex) {
                System.err.println("***************   Certificate Authority Server issue    ***************");
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }

            socket = new Socket(serverIP, serverPort);
            // send connection message
            ConnectionMessage message = new ConnectionMessage();

//            current.publicKey = keyPair.getPublic();
            current.certificate = certificate;
            message.setCertificate(certificate);
            message.receiver = null;
            message.sender = new Client(current);
//            message.setPublicKey(keyPair.getPublic());
            current.setSocket(socket);

            // Write message to output stream
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(message);
            oos.flush();
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }


    }

    private Object receive(ObjectInputStream input) {
        Object o = new Object();
        try {
            o = input.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(Connector.class.getName()).log(Level.SEVERE, null, ex);
        }
        return o;
    }

    private void send(Object text, ObjectOutputStream output) {
        try {
            output.writeObject(text);
        } catch (IOException ex) {
            Logger.getLogger(Connector.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


}
