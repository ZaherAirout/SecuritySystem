/*
 * Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved.
 */
package Forms;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

/**
 * Keypad is a custom type which uses FXML to load its content.
 */
public class Keypad extends VBox {
    
    private final KeypadController controller;
    public Keypad() {
        controller = load();
    }
    
    private KeypadController load() {
        
        final FXMLLoader loader = new FXMLLoader();
        
        // fx:root is this node.
        loader.setRoot(this);
        
        // The FXMLLoader should use the class loader that loaded
        // this class (Keypad).
        loader.setClassLoader(this.getClass().getClassLoader());
        
        // Keypad.fxml contains the configuration for 'this'
        loader.setLocation(this.getClass().getResource("Keypad.fxml"));
        
        try {
            final Object root = loader.load();
            assert root == this;
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
        
        final KeypadController keypadController = loader.getController();
        assert keypadController != null;
        return keypadController;
    }
    
    void setValidateCallback(Callback<String,Boolean> callback) {
        controller.setValidateCallback(callback);
    }
}
