/*
 * Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved.
 */
package Forms;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.util.Callback;

/**
 * The controller for the custom Server component - see 'Server.fxml'
 * and 'Server.java'.
 */
public final class KeypadController implements Initializable {
            
    @FXML //  fx:id="del"
    private Button del; // Value injected by FXMLLoader

    @FXML //  fx:id="ok"
    private Button ok; // Value injected by FXMLLoader

    @FXML //  fx:id="display"
    private PasswordField display; // Value injected by FXMLLoader

    private Callback<String, Boolean> validateCallback = null;
    
    // Handler for Button[Button[id=null, styleClass=button]] onAction
    // Handler for Button[fx:id="del"] onAction
    // Handler for Button[fx:id="ok"] onAction
    public void keyPressed(ActionEvent event) {
        // handle the event here
        if (event.getTarget() instanceof Button) {
            if (event.getTarget() == del && !display.getText().isEmpty()) {                
                delete();
            } else if (event.getTarget() == ok) {
                validateCallback.call(display.getText());
                display.setText("");
            } else if (event.getTarget() != del) {
                append(((Button)event.getTarget()).getText());
            }
            event.consume();
        }
    }
    public void abc(){
        System.out.println("keypad");
    }
    
    private void delete() {
        display.setText(display.getText().substring(0, display.getText().length() -1));
    }
    
    private void append(String s) {
        String text = display.getText();
        if (text == null) text = "";
        display.setText(text+s);
    }

    @Override // This method is called by the FXMLLoader when initialization is complete
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
        assert del != null : "fx:id=\"del\" was not injected: check your FXML file 'Server.fxml'.";
        assert ok != null : "fx:id=\"ok\" was not injected: check your FXML file 'Server.fxml'.";
        assert display != null : "fx:id=\"password\" was not injected: check your FXML file 'Server.fxml'.";
    }
    
    void setValidateCallback(Callback<String,Boolean> validateCB) {
        validateCallback = validateCB;
    }
    
}
