/*
 * Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved.
 */
package asymmetricserver;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;

import java.io.IOException;
import java.net.ServerSocket;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The controller for the custom Server component - see 'Server.fxml'
 * and 'Server.java'.
 */
public final class ServerController {

    public static final int PORT = 1025;
    private final int nThreads = 1;
    @FXML
    public ListView<Item> clientsListView;
    //    public HashMap<String, PublicKey> publicKeys;
    public HashMap<String, Certificate> certificate;
    private ExecutorService executor;
    private ServerSocket serverSocket;

    public ServerController() throws IOException, InterruptedException, ClassNotFoundException {

        // Create server socket to receive requests on port.
        serverSocket = new ServerSocket(PORT);
        executor = Executors.newFixedThreadPool(nThreads);
//        publicKeys = new HashMap<>();
        certificate = new HashMap<>();
    }

    @FXML
    public void initialize() {

        clientsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        ObservableList<Item> clients = clientsListView.getItems();

        Receiver receiver = new Receiver(serverSocket, clients, certificate);
        executor.execute(receiver);
    }


}
