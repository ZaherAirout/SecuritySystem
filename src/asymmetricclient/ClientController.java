package asymmetricclient;

import Misc.EncryptedFile;
import Misc.FileManager;
import Protocol.Client;
import Protocol.CloseConnectionMessage;
import Protocol.TextMessage;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.Key;
import java.security.KeyPair;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientController {

    private final int nThread = 5;
    public TextField serverIPTextField;
    public TextField serverPortTextField;
    public TextField msg;
    public ListView<Client> clientsListView;
    public ProgressIndicator progressIndicator;
    public Button connectButton;
    public ListView<String> messagesList;
    private ExecutorService executorService = Executors.newFixedThreadPool(nThread);

    private Client currentClient = null;

    private ConcurrentHashMap<Client, Key> sessionKeys;

    private ObservableList<Client> clients;
    private String serverIP;
    private int serverPort;


    private KeyPair keyPair;
    private ObservableList<String> messages;

    private KeyPair getKeyPair() {
        return keyPair;
    }

    private void setKeyPair(KeyPair keyPair) {
        this.keyPair = keyPair;
    }

    @FXML
    public void initialize() {
        sessionKeys = new ConcurrentHashMap<>();

        clients = clientsListView.getItems();
        serverPortTextField.setText("1025");
        serverIPTextField.setText("localhost");

        messages = messagesList.getItems();
    }

    /***
     * Send Message from currentClient user using GUI to receiver
     */
    public void send() {

        Sender sender = new Sender(sessionKeys, serverIP, serverPort);

        ObservableList<Client> selectedItems = clientsListView.getSelectionModel().getSelectedItems();
        if (selectedItems.size() == 0)
            return;

        for (Client receiver : selectedItems) {

            // create text message
            TextMessage message = new TextMessage();

            message.sender = currentClient;
            message.receiver = receiver;

            message.content = msg.getText().getBytes();
            sender.setMessage(message);
            executorService.execute(sender);

        }
    }

    private void showIndicator() {
        progressIndicator.setVisible(true);
    }

    private void hideIndicator() {
        progressIndicator.setVisible(false);
        connectButton.setVisible(false);
        serverIPTextField.setVisible(false);
        serverPortTextField.setVisible(false);
    }

    public void connect() throws InterruptedException {

        showIndicator();

        // Connect to server
        Connector connector = new Connector(this.currentClient, clients, serverIPTextField.getText(), Integer.parseInt(serverPortTextField.getText()));
        connector.run();

        setKeyPair(connector.getKeyPair());

        hideIndicator();

        // Receive and Handle messages async
        try {
            Receiver receiver = new Receiver(currentClient, getKeyPair(), sessionKeys, clients, messages);
            executorService.execute(receiver);
        } catch (IOException e) {
            e.printStackTrace();
        }

        serverIP = serverIPTextField.getText();
        serverPort = Integer.parseInt(serverPortTextField.getText());
    }

    public void setClient(Client client) {
        this.currentClient = client;
    }

    void shutdown() throws IOException {

        // connect with the server
        Socket socket = new Socket(serverIPTextField.getText(), Integer.parseInt(serverPortTextField.getText()));

        // send connection message
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());

        CloseConnectionMessage message = new CloseConnectionMessage();
        message.sender = currentClient;
        oos.writeObject(message);
        oos.flush();

        // close the connection
        socket.close();


        // close all the threads
        executorService.shutdownNow();
    }

    public void upload() {

    }

    public void decrypt() throws IOException {

        // get the chosen file
        File file = getFile();
        // encrypt it
        FileManager fileManager = FileManager.getInstance();
        EncryptedFile encryptedFile = fileManager.encryptFile(file, "password");

        // write it to some location in the hard drive
        fileManager.writeEncryptedFile(encryptedFile, file.getPath());
    }

    private File getFile() {
        // setup and get the file
        FileChooser fileChooser = new FileChooser();
        return fileChooser.showOpenDialog(serverIPTextField.getScene().getWindow());
    }
}
