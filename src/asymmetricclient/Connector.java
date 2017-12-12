package asymmetricclient;

import Protocol.Client;
import Protocol.ConnectionMessage;
import Protocol.Message;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Connector implements Runnable {

    private final String serverIP;
    private final int serverPort;
    ObservableList<Client> clients;
    private Socket socket;
    private static Socket socketToCA;
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
//            keyPair = RSA.generateKeys();
            keyPair = kpg.generateKeyPair();


            /* Create a Certificate */

            ObjectInputStream input = null;
            ObjectOutputStream output = null;

            try {
                socketToCA = new Socket("localhost", 20000);
                output = new ObjectOutputStream(socketToCA.getOutputStream());
                input = new ObjectInputStream(socketToCA.getInputStream());
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
            /* Check if input and outPut isn't null*/
            assert output != null;
            assert input != null;

            send(current, output);
            send(keyPair.getPublic(), output);
            certificate = (X509Certificate) receive(input);


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
