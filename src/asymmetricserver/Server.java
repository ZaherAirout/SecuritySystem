

package asymmetricserver;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main class for the Server Custom demo.
 * This is boilerplate code:
 * Loads 'Server.fxml', adds the root node to a Scene, and set the scene
 * to the application primary stage.
 * <br>The main difference between Server demo and Server Custom demo is that
 * here the key pad is defined as a custom type named Server. From within
 * Server.fxml we refer to the key pad by its Java class name, Server.
 */
public class Server extends Application {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Application.launch(Server.class);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            String filename = "Server.fxml";
            Pane page = (Pane) FXMLLoader.load(Server.class.getResource(filename));
            Scene scene = new Scene(page);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Server Custom Demo");
            primaryStage.show();
        } catch (Exception ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
