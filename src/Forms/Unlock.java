/*
 * Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved.
 */

package Forms;

import asymmetricserver.ServerController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main class for the Unlock Include demo.
 * This is boilerplate code:
 * Loads 'Unlock.fxml', adds the root node to a Scene, and set the scene
 * to the application primary stage.
 * <br>The main difference between Unlock demo and Unlock Include demo is that
 * here the key pad is defined in a separate Keypad.fxml file which is
 * included in Unlock.fxml thanks an fx:include statement.
 */
public class Unlock extends Application {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Application.launch(Unlock.class);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            String filename = "Unlock.fxml";
            Pane page = (Pane) FXMLLoader.load(Unlock.class.getResource(filename));
            Scene scene = new Scene(page);

            scene.getStylesheets().clear();
            scene.getStylesheets().add("Styles/style.css");

            primaryStage.setScene(scene);
            primaryStage.setOnHidden(event -> Platform.exit());
            primaryStage.setTitle("Server - " + InetAddress.getLocalHost().getHostAddress() + ":" + ServerController.PORT);

            primaryStage.show();
        } catch (Exception ex) {
            Logger.getLogger(Unlock.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
