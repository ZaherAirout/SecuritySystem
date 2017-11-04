

package Forms;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Main class for the Unlock Custom demo.
 * This is boilerplate code:
 * Loads 'Unlock.fxml', adds the root node to a Scene, and set the scene
 * to the application primary stage.
 * <br>The main difference between Unlock demo and Unlock Custom demo is that
 * here the key pad is defined as a custom type named Keypad. From within
 * Unlock.fxml we refer to the key pad by its Java class name, Keypad.
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
            primaryStage.setScene(scene);
            primaryStage.setTitle("Unlock Custom Demo");
            primaryStage.show();
        } catch (Exception ex) {
            Logger.getLogger(Unlock.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
