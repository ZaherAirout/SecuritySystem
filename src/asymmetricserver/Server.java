/*
 * Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved.
 */
package asymmetricserver;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

/**
 * Server is a custom type which uses FXML to load its content.
 */
public class Server extends GridPane {

    private final ServerController controller;

    public Server() {
        controller = load();
    }

    private ServerController load() {

        final FXMLLoader loader = new FXMLLoader();

        // fx:root is this node.
        loader.setRoot(this);

        // The FXMLLoader should use the class loader that loaded
        // this class (Server).
        loader.setClassLoader(this.getClass().getClassLoader());

        // Server.fxml contains the configuration for 'this'
        loader.setLocation(this.getClass().getResource("Server.fxml"));

        try {
            final Object root = loader.load();
            assert root == this;
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }

        final ServerController serverController = loader.getController();
        assert serverController != null;
        return serverController;
    }

}
