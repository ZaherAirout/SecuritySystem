package asymmetricserver;

import Protocol.*;
import javafx.application.Platform;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyException;
import java.util.ArrayList;

public class Receiver extends Protocol.Receiver implements Runnable {
    // Timeout is 2 minutes
    public static final int timeout = 2 * 60 * 1000;
    Client sender;
    private ServerSocket serverSocket;
    private Socket socket;

    public Receiver(ServerSocket serverSocket, ObservableList<Client> clients) {
        super(clients);
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {
        try {
            while (true) {
                socket = serverSocket.accept();/*
                // TODO: Set Timeout to 2mins to release the thread if client is disconnected.
                socket.setSoTimeout(timeout);*/
                // TODO: Complete server receiver protocol
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Message message = (Message) ois.readObject();
                socket.close();

                execute(message);

            }
        } catch (IOException | ClassNotFoundException | KeyException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void execute(ConnectionMessage msg) throws IOException {
        this.sender = msg.sender;

        ArrayList<Client> list = new ArrayList();
        list.addAll(clients);
        list.add(this.sender);

        Platform.runLater(() -> clients.add(this.sender));

        // new connection, so update GUI for every connected client
        for (Client client : list) {
            // connect to client
            Socket socket = new Socket(client.IP, client.port);
            // create update message
            UpdateClientsMessage message = new UpdateClientsMessage(list);
            // send the message
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(message);
            oos.flush();
            // close the connection
            socket.close();
        }
    }

    @Override
    public void execute(TextMessage msg) throws IOException, ClassNotFoundException, KeyException {

        Client receiver = msg.receiver;

        // Connect to the receiver
        Socket socket = new Socket(receiver.IP, receiver.port);
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        // send the message
        oos.writeObject(msg);
        oos.flush();

        // close the connection
        socket.close();

    }

    @Override
    public void execute(CheckOnlineMessage msg) {

    }

    @Override
    public void execute(CloseConnectionMessage msg) {
        Client sender = msg.sender;

        Platform.runLater(() -> clients.remove(sender));
    }

    @Override
    public void execute(UpdateClientsMessage message) {

    }

}
