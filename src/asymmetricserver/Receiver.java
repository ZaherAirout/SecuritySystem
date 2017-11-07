package asymmetricserver;

import Protocol.*;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.util.Pair;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyException;

public class Receiver extends Protocol.Receiver implements Runnable {
    // Timeout is 2 minutes
    public static final int timeout = 2 * 60 * 1000;
    private final ObservableList<Pair<Client, Socket>> clients;
    Client sender;
    private ServerSocket sSocket;
    private Socket socket;

    public Receiver(ServerSocket sScoket, ObservableList<Pair<Client, Socket>> clients) {
        this.sSocket = sScoket;
        this.clients = clients;
    }

    @Override
    public void run() {
        try {
            socket = sSocket.accept();
            while (true) {
                // TODO: Set Timeout to 2mins to release the thread if client is disconnected.
                socket.setSoTimeout(timeout);
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
    public void execute(ConnectionMessage msg) throws IOException {
        this.sender = msg.sender;
        Pair<Client, Socket> pair = new Pair<>(this.sender, socket);

        Platform.runLater(() -> clients.add(pair));
    }

    @Override
    public void execute(TextMessage msg) throws IOException, ClassNotFoundException, KeyException {

    }

    @Override
    public void execute(CheckOnlineMessage msg) {

    }

}
