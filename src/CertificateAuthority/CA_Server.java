package CertificateAuthority;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ServerSocketFactory;

import asymmetricserver.Server;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class CA_Server {

    private static ServerSocket serverSocket;
    static Vector<String> users = new Vector<>();
    static Vector<ClientHandler> clients = new Vector<>();
    static Vector<X509Certificate> certs = new Vector<>();
    static KeyPair pair;

    public static void main(String... args) throws IOException {
        KeyPairGenerator keyGen;
        try {
            // create the keys
            keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(1024);
            pair = keyGen.generateKeyPair();
        } catch (Exception ex) {
            Logger.getLogger(CA_Server.class.getName()).log(Level.SEVERE, null, ex);
        }

        serverSocket = ServerSocketFactory.getDefault().createServerSocket(20000);
        while (true) {
            try {
                final Socket socketToClient = serverSocket.accept();
                System.out.println("client accepted");
                ClientHandler clientHandler = new ClientHandler(socketToClient);
                clients.add(clientHandler);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    static class ClientHandler extends Thread {

        String user;
        ObjectInputStream input;
        ObjectOutputStream output;

        ClientHandler(Socket socket) throws Exception {
            try {
                input = new ObjectInputStream(socket.getInputStream());
                output = new ObjectOutputStream(socket.getOutputStream());
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
            user = recieve().toString();
            users.add(user);
            PublicKey publicKey = (PublicKey) recieve();

            // generate the certificate
            Security.addProvider(new BouncyCastleProvider());
            X509Certificate cert = X509V1CreateExample.generateV1Certificate(publicKey, pair.getPrivate(), user);
            send(pair.getPublic());
            send(cert);
            certs.add(cert);
        }

        void setUser(String user) {
            this.user = user;
        }

        Object recieve() {
            Object o = new Object();
            try {
                o = input.readObject();
            } catch (IOException ex) {
                return null;
//                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
            return o;
        }

        void send(Object text) {
            try {
                output.writeObject(text);
            } catch (IOException ex) {
//                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        void broadcast(Object text) {
            for (ClientHandler clientHandler : clients) {
                if (!clientHandler.user.equals(user)) {
                    clientHandler.send(text);
                }
            }
        }

        void broadcast(Object text, Object tag) {
            for (ClientHandler clientHandler : clients) {
                if (!clientHandler.user.equals(user)) {
                    clientHandler.send(tag);
                    clientHandler.send(text);
                }
            }
        }

        void sendToUser(Object text, Object user, Object tag) {
            for (ClientHandler clientHandler : clients) {
                if (clientHandler.user.equals(user.toString())) {
                    clientHandler.send(tag);
                    clientHandler.send(text);
                }
            }
        }

        @Override
        public void run() {
        }
    }
}
