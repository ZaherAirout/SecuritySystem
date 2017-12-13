/*
 * Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved.
 */

package asymmetricclient;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.logging.Level;
import java.util.logging.Logger;

public class AsymClient extends Application {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Application.launch(AsymClient.class);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            String filename = "Client.fxml";
            Pane page = FXMLLoader.load(AsymClient.class.getResource(filename));
            Scene scene = new Scene(page);

            scene.getStylesheets().clear();
            scene.getStylesheets().add("Styles/style.css");

            primaryStage.setScene(scene);
            primaryStage.setOnHidden(event -> Platform.exit());
            primaryStage.setTitle("Client");

            primaryStage.show();
        } catch (Exception ex) {
            Logger.getLogger(AsymClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
