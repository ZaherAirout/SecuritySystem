package asymmetricclient;

import Misc.EncryptedFile;
import Misc.FileManager;
import Protocol.Client;
import Protocol.Messages.CloseConnectionMessage;
import Protocol.Messages.FileMessage;
import Protocol.Messages.TextMessage;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Key;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Optional;
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


    private ObservableList<Client> clients;

    private KeyPair keyPair;
    private ObservableList<String> messages;
    private X509Certificate certificate;
    private volatile HashMap<Client, PublicKey> publicKeys;
    private volatile HashMap<Client, Key> sessionKeys;

    /* Certificate Authority PublicKey*/
    private PublicKey CA_PK;

    public X509Certificate getCertificate() {
        return certificate;
    }

    public void setCertificate(X509Certificate certificate) {
        this.certificate = certificate;
    }

    private KeyPair getKeyPair() {
        return keyPair;
    }


    public void setCA_PK(PublicKey CA_PK) {
        this.CA_PK = CA_PK;
    }

    private void setKeyPair(KeyPair keyPair) {
        this.keyPair = keyPair;
//        currentClient.publicKey = this.keyPair.getPublic();
    }

    @FXML
    public void initialize() {

        CreateClient();

        sessionKeys = new HashMap<>();
        publicKeys = new HashMap<>();

        clients = clientsListView.getItems();
        serverPortTextField.setText("1025");
        serverIPTextField.setText("localhost");

        messages = messagesList.getItems();
        clientsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    private void CreateClient() {
        Dialog<String> dialog = new Dialog<>();
        dialog.getDialogPane().getStylesheets().clear();
        dialog.getDialogPane().getStylesheets().add("Styles/style.css");

        dialog.setTitle("Client Name");

        ButtonType doneButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(doneButton);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));

        Label label = new Label("Name: ");
        gridPane.add(label, 0, 0);
        TextField name = new TextField();
        name.setPromptText("Name");
        gridPane.add(name, 1, 0);
        dialog.setResizable(false);
        dialog.getDialogPane().setContent(gridPane);
        dialog.showAndWait();
        this.currentClient = new Client(name.getText(), null);
    }

    /***
     * Send Message from currentClient user using GUI to receiver
     */
    public void send() {


        ObservableList<Client> selectedItems = clientsListView.getSelectionModel().getSelectedItems();
        if (selectedItems.size() == 0)
            return;

        for (Client receiver : selectedItems) {

            Sender sender = new Sender(this.sessionKeys, keyPair.getPrivate(), currentClient.getSocket());

            // create text message
            TextMessage message = new TextMessage();

            message.sender = new Client(currentClient);
            message.receiver = new Client(receiver);

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

    public void connect() {

        showIndicator();

        // Connect to server
        Connector connector = new Connector(this.currentClient, clients, serverIPTextField.getText(), Integer.parseInt(serverPortTextField.getText()));

        connector.run();

        setKeyPair(connector.getKeyPair());
        setCertificate(connector.getCertificate());
        setCA_PK(connector.getCA_PK());

        hideIndicator();

        // Receive and Handle messages async
        try {
            Receiver receiver = new Receiver(currentClient, getKeyPair(), this.sessionKeys, clients, messages, CA_PK);
            executorService.execute(receiver);

        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public void upload() throws IOException {


        String filePath = getFile().getPath();
        byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));
        ObservableList<Client> selectedItems = clientsListView.getSelectionModel().getSelectedItems();
        if (selectedItems.size() == 0)
            return;

        for (Client receiver : selectedItems) {

            Sender sender = new Sender(this.sessionKeys, keyPair.getPrivate(), currentClient.getSocket());

            // create text message
            FileMessage message = new FileMessage();

            message.sender = currentClient;
            message.receiver = receiver;
            FileManager fileManager = FileManager.getInstance();
            message.filename = fileManager.getFileName(filePath);
            System.out.println(message.filename);


            message.content = fileBytes;
            sender.setMessage(message);
            executorService.execute(sender);

        }

    }

    public void decrypt() {

        // get the chosen file
        File file = getFile();

        // Get password
        final String[] password = {"text"};
        TextInputDialog dialog = new TextInputDialog("test");
        dialog.getDialogPane().getStylesheets().clear();
        dialog.getDialogPane().getStylesheets().add("Styles/style.css");
        dialog.setTitle("Password");
        dialog.setContentText("Please enter file password:");

        // Traditional way to get the response value.
        Optional<String> result = dialog.showAndWait();


        result.ifPresent(s -> password[0] = s);


        // decrypt it
        FileManager fileManager = FileManager.getInstance();

        EncryptedFile encryptedFile = fileManager.readEncryptedFile(file.getPath());
        File decryptFile = fileManager.decryptFile(encryptedFile, password[0]);
        if (decryptFile == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.getDialogPane().getStylesheets().clear();
            alert.getDialogPane().getStylesheets().add("Styles/style.css");

            alert.setTitle("Wrong Password");
            alert.setContentText("sorry, you entered a wrong password");
            alert.show();
            return;
        }

        // write it to some location in the hard drive
        fileManager.openFile(decryptFile);
    }

    private File getFile() {
        // setup and get the file
        FileChooser fileChooser = new FileChooser();
        return fileChooser.showOpenDialog(serverIPTextField.getScene().getWindow());
    }

    public HashMap<Client, Key> getSessionKeys() {
        return sessionKeys;
    }

    public HashMap<Client, PublicKey> getPublicKeys() {
        return publicKeys;
    }
}
