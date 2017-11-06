/*
 * Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved.
 */
package asymmetricserver;

import Protocol.Client;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * The controller for the custom Server component - see 'Server.fxml'
 * and 'Server.java'.
 */
public final class ServerController implements Initializable{

    @FXML
    public ListView<Client> clients;
    public Button Send;

    @FXML
    public void initialize() {
        clients.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        ObservableList<Client> items = clients.getItems();
        items.add(new Client("zaher", "localhost", null));
        items.add(new Client("Zizo", "172.25.10.10", null));

        clients.getSelectionModel().select(0);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initialize();
    }


    public void keyPressed(MouseEvent event) {

        System.out.println("Hi");
    }
}
