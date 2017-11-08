package asymmetricclient;

import Protocol.Client;
import Protocol.CloseConnectionMessage;
import Protocol.TextMessage;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientController {

    private static final String cwd = System.getProperty("user.dir");
    static final String PATH_OF_PRIVATE_KEY = String.join(File.separator, cwd, "asymmetricclient", "MyPrivateKey");
    static final String PATH_OF_PUBLIC_KEY = String.join(File.separator, cwd, "asymmetricclient", "MyPublicKey");
    public TextField serverIP;
    public TextField serverPort;
    public TextField msg;
    public ListView clientsListView;
    public ProgressIndicator progressIndicator;
    private ExecutorService executorService = Executors.newFixedThreadPool(5);

    private Socket socket = null;

    private Client currentClient = null;

    private HashMap<Client, byte[]> sessionKeys;
    private List<Client> onlineClients;


    private Receiver receiver;
    private Sender sender;
    private Client client;
    private ObservableList<Client> clients;


    @FXML
    public void initialize() {

        sessionKeys = new HashMap<>();

        clients = clientsListView.getItems();

    }

    /***
     * Send Message from currentClient user using GUI to receiver
     * @param e
     */
    public void send(MouseEvent e) {

        // Create message
        TextMessage message = new TextMessage();

        // TODO: receiver from GUI, remove hard-coded
        Client receiver = new Client("Ahmed", "localhost", 1235, null);

        message.sender = currentClient;
        message.receiver = receiver;

        // TODO: message from GUI, remove hard-coded
        String messageContent = "Hello " + receiver.getName();

        // sync so that messages don't get conflicted.
        synchronized (sender) {
            sender.setMessage(message);
            sender.setContent(messageContent);
            executorService.execute(sender);
        }
    }

    void showIndicator() {
        progressIndicator.setVisible(true);
    }

    void hideIndicator() {
        progressIndicator.setVisible(false);
    }

    public void connect() throws InterruptedException {


        // TODO: Hide progress bad
        showIndicator();

        // Connect to server
        Connector connector = new Connector(this.currentClient, clients, serverIP.getText(), Integer.parseInt(serverPort.getText()));
//        executorService.execute(connector);
        connector.run();

        hideIndicator();

       /* socket = connector.socket;
        onlineClients = connector.clients;
*/
        // Receive and Handle messages async
        try {
            System.out.println(currentClient.port);
            receiver = new Receiver(currentClient.port, sessionKeys);
            executorService.execute(receiver);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Create sender to send messages async
//        sender = new Sender(socket, sessionKeys);

        // TODO: show progress bar
        // ProgressBar.setVisiable(true);

    }

    public void setClient(Client client) {
        this.currentClient = client;
    }

    void shutdown() throws IOException {
        socket = new Socket(serverIP.getText(), Integer.parseInt(serverPort.getText()));

        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());

        CloseConnectionMessage message = new CloseConnectionMessage();
        message.sender = currentClient;
        oos.writeObject(message);
        oos.flush();

        socket.close();
    }
}
