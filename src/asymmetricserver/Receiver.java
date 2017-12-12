package asymmetricserver;

import Protocol.*;
import javafx.application.Platform;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Receiver extends Protocol.Receiver implements Runnable {

    private final int nThreads = 4;
    HashMap<String, PublicKey> publicKeys;
    private ServerSocket serverSocket;
    private ExecutorService executorService;

    public Receiver(ServerSocket serverSocket, ObservableList<Item> clients, HashMap<String, PublicKey> publicKeys) {
        super(clients);
        this.serverSocket = serverSocket;
        executorService = Executors.newFixedThreadPool(nThreads);
        this.publicKeys = publicKeys;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Socket socket = serverSocket.accept();

                executorService.execute(() -> {
                    try {
                        while (true) {
                            // read the message
                            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                            Message message = (Message) ois.readObject();

                            if (message.receiver != null)
                                message.receiver.publicKey = publicKeys.get(message.receiver.getName());
                            // execute the specific task depending on the type of the message
                            execute(message, socket);
                        }
                    } catch (IOException | KeyException | ClassNotFoundException | NoSuchAlgorithmException | SignatureException | NoSuchProviderException e) {
                        e.printStackTrace();
                    }

                });

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void execute(ConnectionMessage receivedMsg, Socket socket) throws IOException {
        Client sender = receivedMsg.sender;

        ArrayList<Item> clients = new ArrayList<>();
        clients.addAll(this.clients);
        clients.add(new Item(sender, socket));
        publicKeys.put(sender.getName(), receivedMsg.getPublicKey());

        Platform.runLater(() -> this.clients.add(new Item(sender, socket)));

        ArrayList<Client> onlineClients = new ArrayList<>();
        for (Object object : clients) {
            Client client = ((Item) object).getClient();
            client.publicKey = publicKeys.get(client.getName());
            onlineClients.add(client);
        }
        // create update newMsg
        UpdateClientsMessage newMsg = new UpdateClientsMessage(onlineClients);
        System.out.println(receivedMsg.sender.getName());
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(newMsg);
        oos.flush();

        System.out.println("sending list to clients");
        // new connection, so update GUI for every connected client
        for (Object object : clients) {
            Item item = (Item) object;
            System.out.println("Client name: " + item.getClient().getName());
            Socket clientSocket = item.getSocket();
            // send the newMsg
            oos = new ObjectOutputStream(clientSocket.getOutputStream());
            oos.writeObject(newMsg);
            oos.flush();
        }
        System.out.println("done");
    }

    @Override
    public void execute(TextMessage msg, Socket socket) throws IOException, ClassNotFoundException, KeyException {
        forwardToReceiver(msg);
    }

    @Override
    public void execute(FileMessage msg, Socket socket) throws KeyException, IOException {
        forwardToReceiver(msg);
    }

    private void forwardToReceiver(Message msg) throws IOException {
        Client receiver = msg.receiver;

        Socket clientSocket = ((ObservableList<Item>) clients).filtered(item -> item.getClient().equals(receiver)).get(0).getSocket();

        if (clientSocket == null) {
            System.out.println("Not found");
            return;
        }

        // Connect to the receiver
        ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
        // send the message
        oos.writeObject(msg);
        oos.flush();
    }

    @Override
    public void execute(CheckOnlineMessage msg, Socket socket) {

    }

    @Override
    public void execute(CloseConnectionMessage msg, Socket socket) {
        Client sender = msg.sender;

        Platform.runLater(() -> clients.remove(sender));
    }

    @Override
    public void execute(UpdateClientsMessage message, Socket socket) {

    }

    @Override
    public void execute(PKMessage message, Socket socket) {
        /*Client receiver = message.receiver;

        PublicKey publicKey = publicKeys.get(receiver.getName());

        try {
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            message.setPublicKey(publicKey);

            oos.writeObject(message);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
*/
    }

}
