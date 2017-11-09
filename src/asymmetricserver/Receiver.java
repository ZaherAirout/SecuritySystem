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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Receiver extends Protocol.Receiver implements Runnable {

    private final int nThreads = 4;
    private Client sender;
    private ServerSocket serverSocket;
    private Socket socket;
    private ExecutorService executorService;

    public Receiver(ServerSocket serverSocket, ObservableList<Client> clients) {
        super(clients);
        this.serverSocket = serverSocket;
        executorService = Executors.newFixedThreadPool(nThreads);
    }

    @Override
    public void run() {
        try {
            while (true) {
                Socket socket = serverSocket.accept();

                executorService.execute(() -> {
                    try {
                        // read the message
                        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                        Message message = (Message) ois.readObject();
                        ois.close();
                        socket.close();
                        // execute the specific task depending on the type of the message
                        execute(message);
                    } catch (IOException | KeyException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                });

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void execute(ConnectionMessage msg) throws IOException {
        this.sender = msg.sender;

        ArrayList<Client> list = new ArrayList<Client>();
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
    public void execute(FileMessage msg) throws KeyException, IOException {
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
