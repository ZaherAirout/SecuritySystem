package asymmetricserver;

import Protocol.*;
import javafx.application.Platform;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyException;

public class Receiver extends Protocol.Receiver implements Runnable {
    // Timeout is 2 minutes
    public static final int timeout = 2 * 60 * 1000;
    private final ObservableList<Client> clients;
    Client sender;
    private ServerSocket serverSocket;
    private Socket socket;

    public Receiver(ServerSocket sScoket, ObservableList<Client> clients) {
        this.serverSocket = sScoket;
        this.clients = clients;
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
                execute(message);
            }
        } catch (IOException | ClassNotFoundException | KeyException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void execute(ConnectionMessage msg) {
        this.sender = msg.sender;
        System.out.println(this.sender);
        Platform.runLater(() -> clients.add(this.sender));
    }

    @Override
    public void execute(TextMessage msg) throws IOException, ClassNotFoundException, KeyException {

    }

    @Override
    public void execute(CheckOnlineMessage msg) {

    }

}
