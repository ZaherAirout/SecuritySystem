package asymmetricclient;

import Protocol.Client;
import Protocol.TextMessage;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ClientController {

    private static final String cwd = System.getProperty("user.dir");
    static final String PATH_OF_PRIVATE_KEY = String.join(File.separator, cwd, "asymmetricclient", "MyPrivateKey");
    static final String PATH_OF_PUBLIC_KEY = String.join(File.separator, cwd, "asymmetricclient", "MyPublicKey");
    public TextField serverIP;
    public TextField serverPort;
    public TextField msg;
    public ListView clientsListView;
    private ExecutorService executorService = Executors.newFixedThreadPool(5);

    private Socket socket = null;

    private Client current = null;

    private HashMap<Client, byte[]> sessionKeys;
    private List<Client> onlineClients;


    private Receiver receiver;
    private Sender sender;
    private Client client;


    @FXML
    public void initialize() {

        sessionKeys = new HashMap<>();

    }

    /***
     * Send Message from current user using GUI to receiver
     * @param e
     */
    public void send(MouseEvent e) {

        // Create message
        TextMessage message = new TextMessage();

        // TODO: receiver from GUI, remove hard-coded
        Client receiver = new Client("Ahmed", "localhost", 1235, null);

        message.sender = current;
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

    public void connect() {
        // Connect to server
        Connector connector = new Connector(this.current, serverIP.getText(), Integer.parseInt(serverPort.getText()));
        Future<?> submit = executorService.submit(connector);

        // TODO: Hide progress bad
        // ProgressBar.setVisiable(false);

        // TODO: Remove this
//        Thread.sleep(2000);

       /* socket = connector.socket;
        onlineClients = connector.clients;
*/
        // Receive and Handle messages async
        try {
            receiver = new Receiver(current.port, sessionKeys);
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
        this.current = client;
    }
}
