/*
 * Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved.
 */
package asymmetricserver;

import Protocol.Client;
import asymmetricclient.AsymmetricClient;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.util.Pair;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The controller for the custom Server component - see 'Server.fxml'
 * and 'Server.java'.
 */
public final class ServerController implements Initializable {

    private static final int port = 1234;
    @FXML
    public ListView<Pair<Client, Socket>> clientsListView;
    public TextField name;
    ExecutorService executor = Executors.newFixedThreadPool(5);
    ServerSocket serverSocket;
    private ObservableList<Pair<Client, Socket>> clients;

//    HashMap<Client, Socket> clients = new HashMap<>();

    public ServerController() throws IOException, InterruptedException, ClassNotFoundException {

        // Create server socket to receive requests on port.
        serverSocket = new ServerSocket(port);

/*
        AsymmetricClient asymmetricClient = new AsymmetricClient(new Client("Ahmed", "localhost", null));
        executor.execute(asymmetricClient);

*/
/*
        Socket socket1 = serverSocket.accept();
        ServerHandler handler1 = new ServerHandler(clients, socket1);
        executor.execute(handler1);*/
    }

    @FXML
    public void initialize() {

        clientsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        clientsListView.setCellFactory(param -> new ListCell<Pair<Client, Socket>>() {
            @Override
            protected void updateItem(Pair<Client, Socket> item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null)
                    setText(item.getKey().toString());
            }
        });

        clients = clientsListView.getItems();
        clients.add(new Pair<Client, Socket>(new Client("zizo", "1234", null), new Socket()));

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initialize();
    }

    public void createLocalClient() throws InterruptedException, IOException, ClassNotFoundException {
        Client client = new Client(name.getText(), String.valueOf(Inet4Address.getLocalHost()), null);
        AsymmetricClient asymmetricClient = new AsymmetricClient(client);

        executor.execute(asymmetricClient);
        Receiver receiver = new Receiver(serverSocket, clients);
        executor.execute(receiver);
    }
}
