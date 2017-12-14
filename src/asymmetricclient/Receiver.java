package asymmetricclient;

import Misc.FileManager;
import Misc.Logger;
import Protocol.Client;
import Protocol.Messages.*;
import crypto.AES;
import crypto.RSA;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.security.*;
import java.security.cert.*;
import java.security.cert.Certificate;
import java.util.Date;
import java.util.HashMap;

public class Receiver extends Protocol.Receiver implements Runnable {
    private final HashMap<Client, Key> sessionKeys;
    private final ObservableList<String> messages;
    private Client currentClient;
    private KeyPair pairKey;

    /* Certificate Authority PublicKey*/
    private PublicKey CA_PK;


    Receiver(Client currentClient, KeyPair pairKey, HashMap<Client, Key> sessionKeys, ObservableList<Client> clients, ObservableList<String> messages, PublicKey CA_PK) throws IOException {
        super(clients);

        this.sessionKeys = sessionKeys;
        this.currentClient = currentClient;
        this.pairKey = pairKey;
        this.messages = messages;
        this.CA_PK = CA_PK;
    }

    @Override
    public void run() {
        Socket socket = currentClient.getSocket();
        ObjectInputStream ois = null;
        while (true) {
            try {
                Logger logger = new Logger("./log.txt");
                logger.start();

                ois = new ObjectInputStream(socket.getInputStream());

                Message msg = (Message) ois.readObject();

                execute(msg, socket);

                logger.stop("Receiving message ");

            } catch (IOException | ClassNotFoundException | NoSuchAlgorithmException | KeyException | SignatureException | NoSuchProviderException e) {
            }
        }
    }

    @Override
    public void execute(ConnectionMessage msg, Socket socket) throws IOException {

    }

    @Override
    public void execute(TextMessage msg, Socket socket) throws IOException, ClassNotFoundException, KeyException, SignatureException, NoSuchProviderException, NoSuchAlgorithmException {

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

        byte[] decryptedContent = AES.decrypt(msg.content, sessionKey);
        String result = new String(decryptedContent);

        String verificationRes = Verify(msg) ? " - Data verified." : " - Cannot verify data.";
        Platform.runLater(() -> messages.add(0, "" + msg.sender.getName() + ":  " + result + verificationRes));
    }

    @Override
    public void execute(FileMessage msg, Socket socket) throws KeyException, NoSuchProviderException, NoSuchAlgorithmException, SignatureException {
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

        String verificationRes = Verify(msg) ? " - Data verified." : " - Cannot verify data.";


        Platform.runLater(() -> {
            messages.add("" + msg.sender.getName() + ":  File Received -->" + msg.filename + " Verification Status :" + verificationRes);

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

        });

    }

    private boolean Verify(Message msg) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, NoSuchProviderException {
        Signature verifier = Signature.getInstance("SHA1withRSA");
        X509Certificate certificate = (X509Certificate) msg.sender.certificate;
        PublicKey publicKey = certificate.getPublicKey();
        try {
            if (CA_PK != null)
                certificate.verify(CA_PK);
            else {
                System.err.println("******************************************");
                System.err.println("***********UnKnown CA PublicKey**********");
                System.err.println("******************************************");
                showAlert("Warning", "UnKnown CA PublicKey");
            }
            certificate.checkValidity(new Date());
        } catch (CertificateExpiredException e) {
            System.err.println("******************************************");
            System.err.println("*********** Certificate Expired **********");
            System.err.println("******************************************");
            showAlert("Warning", "Certificate Expired");

        } catch (CertificateNotYetValidException e) {
            System.err.println("******************************************");
            System.err.println("*******Certificate NOT YET VALID**********");
            System.err.println("******************************************");
            showAlert("Warning", "Certificate NOT YET VALID");

//            return false;
        } catch (CertificateException e) {
            System.err.println("******************************************");
            System.err.println("********Certificate not trusted***********");
            System.err.println("******************************************");
            showAlert("Warning", "Certificate not trusted");
        }
        verifier.initVerify(publicKey);
        byte[] digitalSignature = msg.digitalSignature;
        verifier.update(msg.content);

        return verifier.verify(digitalSignature);

    }

    @Override
    public void execute(CheckOnlineMessage msg, Socket socket) {

    }

    @Override
    public void execute(CloseConnectionMessage msg, Socket socket) {
        throw new UnsupportedOperationException("Close Connection Message must not be sent to clients.");
    }

    @Override
    public void execute(UpdateClientsMessage msg, Socket socket) {
        System.out.println("Client " + currentClient.getName() + " is receiving list of the new clients");
        for (Client client : msg.clients) {
            System.out.println(client.getName());
        }
        System.out.println("done");
        Platform.runLater(() -> {
            clients.clear();
            msg.clients.removeIf(client -> client.equals(currentClient));
            ((ObservableList<Client>) clients).addAll(msg.clients);
        });
    }

    private void showAlert(String title, String text) {
        Platform.runLater(() -> {

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(title);
            alert.setContentText(text);
            alert.show();
        });
    }

    @Override
    public void execute(PKMessage message, Socket socket) {

/*
        PublicKey receiverPK = message.getPublicKey();
        Client receiver = message.receiver;

        // Create session key using AES
        Key sessionKey = AES.generateKey();

        // Store session key for late usage.
        assert sessionKey != null;
        sessionKeys.put(receiver, sessionKey);
*/
    }
}
