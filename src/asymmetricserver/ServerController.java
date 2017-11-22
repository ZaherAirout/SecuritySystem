/*
 * Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved.
 */
package asymmetricserver;

import Protocol.Client;
import asymmetricclient.AsymmetricClient;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.UnaryOperator;

/**
 * The controller for the custom Server component - see 'Server.fxml'
 * and 'Server.java'.
 */
public final class ServerController {

    public static final int PORT = 1055;
    private final int nThreads = 1;
    @FXML
    public TextField port;
    @FXML
    public ListView<Client> clientsListView;
    @FXML
    public TextField name;
    private ExecutorService executor;
    private ServerSocket serverSocket;

    public ServerController() throws IOException, InterruptedException, ClassNotFoundException {

        // Create server socket to receive requests on port.
        serverSocket = new ServerSocket(PORT);
        executor = Executors.newFixedThreadPool(nThreads);
    }

    @FXML
    public void initialize() {

        clientsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        ObservableList<Client> clients = clientsListView.getItems();

        // allow user to input only integer in port TextField
        UnaryOperator<TextFormatter.Change> integerFilter = change -> {
            String newText = change.getControlNewText();
            // if proposed change results in a valid value, return change as-is:
            if (newText.matches("-?([1-9][0-9]*)?")) {
                return change;
            } else if ("-".equals(change.getText())) {

                // if user types or pastes a "-" in middle of current text,
                // toggle sign of value:

                if (change.getControlText().startsWith("-")) {
                    // if we currently start with a "-", remove first character:
                    change.setText("");
                    change.setRange(0, 1);
                    // since we're deleting a character instead of adding one,
                    // the caret position needs to move back one, instead of
                    // moving forward one, so we modify the proposed change to
                    // move the caret two places earlier than the proposed change:
                    change.setCaretPosition(change.getCaretPosition() - 2);
                    change.setAnchor(change.getAnchor() - 2);
                } else {
                    // otherwise just insert at the beginning of the text:
                    change.setRange(0, 0);
                }
                return change;
            }
            // invalid change, veto it by returning null:
            return null;
        };

        // modified version of standard converter that evaluates an empty string
        // as zero instead of null:
        StringConverter<Integer> converter = new IntegerStringConverter() {
            @Override
            public Integer fromString(String s) {
                if (s.isEmpty()) return 0;
                return super.fromString(s);
            }
        };

        port.setTextFormatter(new TextFormatter<>(converter, 0, integerFilter));
        port.setText(String.valueOf(1234));



        /*for (int i = 0; i < nThreads; i++) {
        */
        Receiver receiver = new Receiver(serverSocket, clients);
        executor.execute(receiver);

        /*}*/

    }

    public void createLocalClient() throws InterruptedException, IOException, ClassNotFoundException {

        Client client = new Client(name.getText(), String.valueOf(InetAddress.getLocalHost().getHostAddress()),
                Integer.parseInt(port.getText()), null);

        executor.execute(new AsymmetricClient(client));
    }
}
