/*
 * Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved.
 */
package asymmetricserver;

import Protocol.Client;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.MouseEvent;

/**
 * The controller for our 'Server' application, see 'Server.fxml'.
 * This class has all the logic to open the theater's doors using JavaFX
 * transitions.
 */
public final class ServerController {


    public ListView<Client> clients;

    @FXML
    public void initialize() {

        clients.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        ObservableList items = clients.getItems();
        items.add(new Client("123", "1234", null));
        items.add(new Client("1235666", "1234", null));
    }

    public void createClient(MouseEvent mouseEvent) {
    }
}