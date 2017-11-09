package asymmetricclient;

import Misc.FileManager;
import Misc.Logger;
import Protocol.*;
import crypto.AES;
import crypto.RSA;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.Key;
import java.security.KeyException;
import java.security.KeyPair;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Receiver extends Protocol.Receiver implements Runnable {
    private final ConcurrentHashMap<Client, Key> sessionKeys;
    private final ObservableList<String> messages;
    private final ExecutorService executorService;
    private final int nThread = 4;
    private ServerSocket serverSocket;
    private Client currentClient;
    private KeyPair pairKey;

    Receiver(Client currentClient, KeyPair pairKey, ConcurrentHashMap<Client, Key> sessionKeys, ObservableList<Client> clients, ObservableList<String> messages) throws IOException {
        super(clients);

        this.sessionKeys = sessionKeys;
        this.serverSocket = new ServerSocket(currentClient.port);
        this.currentClient = currentClient;
        this.pairKey = pairKey;
        this.messages = messages;
        this.executorService = Executors.newFixedThreadPool(nThread);
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket socket = serverSocket.accept();

                executorService.execute(() -> {
                    try {
                        Logger logger = new Logger("./log.txt");
                        logger.start();

                        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                        Message msg = (Message) ois.readObject();
                        execute(msg);

                        logger.stop("Receiving message ");
                    } catch (IOException | ClassNotFoundException | KeyException e) {
                        e.printStackTrace();
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void execute(ConnectionMessage msg) throws IOException {

    }

    @Override
    public void execute(TextMessage msg) throws IOException, ClassNotFoundException, KeyException {

        Key sessionKey = null;

        byte[] sessionKeyArr = msg.sessionKey;
        if (sessionKeyArr != null) {
            sessionKey = AES.regenerateKey(RSA.decrypt(sessionKeyArr, pairKey.getPrivate()));
            sessionKeys.put(msg.sender, sessionKey);
        }
        if (sessionKey == null)
            sessionKey = sessionKeys.get(msg.sender);

        if (sessionKey == null)
            throw new KeyException("Session Key is not found");

        String result = new String(AES.decrypt(msg.content, sessionKey));

        Platform.runLater(() -> messages.add(0, "" + msg.sender.getName() + ":  " + result));
    }

    @Override
    public void execute(FileMessage msg) throws KeyException {
        Key sessionKey = null;

        byte[] sessionKeyArr = msg.sessionKey;
        if (sessionKeyArr != null) {
            sessionKey = AES.regenerateKey(RSA.decrypt(sessionKeyArr, pairKey.getPrivate()));
            sessionKeys.put(msg.sender, sessionKey);
        }
        if (sessionKey == null)
            sessionKey = sessionKeys.get(msg.sender);

        if (sessionKey == null)
            throw new KeyException("Session Key is not found");


        FileManager fileManager = FileManager.getInstance();

        byte[] decryptedContent = AES.decrypt(msg.content, sessionKey);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                messages.add("" + msg.sender.getName() + ":  File Received -->" + msg.filename);

                File receivedFile = fileManager.writeFile(msg.filename, decryptedContent);
                Dialog<String> dialog = new Dialog<>();
                dialog.setTitle("File Received ");
                dialog.setHeaderText("You have received a new file From " + msg.sender.getName());
                dialog.setResizable(false);


                Button btnOpen = new Button("Open File");
                btnOpen.setOnMouseClicked(event -> fileManager.openFile(receivedFile));

                Button btnPath = new Button("Select Path");
                Button btnSave = new Button("Save");

                DirectoryChooser pathChooser = new DirectoryChooser();

                final String[] path = {".\\"};
                btnPath.setOnMouseClicked(event ->
                {
                    File file = pathChooser.showDialog(dialog.getOwner());
                    path[0] = file.getPath();
                });
                System.out.println(path[0]);

                PasswordField password = new PasswordField();
                password.setPromptText("Enter Password");
                btnSave.setOnMouseClicked(event -> {
                            try {
                                fileManager.writeEncryptedFile(fileManager.encryptFile(receivedFile, password.getText()), path[0]);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Save");
                            String s = "Successfully Saved in " + path[0];
                            alert.setContentText(s);
                            alert.show();
                        }
                );
                GridPane grid = new GridPane();
                grid.add(btnOpen, 2, 1);
                grid.add(password, 1, 3);
                grid.add(btnPath, 2, 3);
                grid.add(btnSave, 3, 3);

                dialog.getDialogPane().setContent(grid);

                ButtonType buttonTypeOk = new ButtonType("Okay", ButtonBar.ButtonData.OK_DONE);
                dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);

                dialog.showAndWait();

            }
        });

    }

    @Override
    public void execute(CheckOnlineMessage msg) {

    }

    @Override
    public void execute(CloseConnectionMessage msg) {
        throw new UnsupportedOperationException("Close Connection Message must not be sent to clients.");
    }

    @Override
    public void execute(UpdateClientsMessage msg) {
        Platform.runLater(() -> {
            clients.clear();
            msg.clients.removeIf(client -> client.equals(currentClient));
            clients.addAll(msg.clients);
        });
    }
}
