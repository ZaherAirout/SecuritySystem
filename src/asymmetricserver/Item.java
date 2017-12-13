package asymmetricserver;

import Protocol.Client;

import java.io.Serializable;
import java.net.Socket;

public class Item implements Serializable {
    private Client client;
    private Socket socket;

    public Item(Client client, Socket socket) {
        this.client = client;
        this.socket = socket;
    }

    public Socket getSocket() {
        return socket;
    }

    @Override
    public String toString() {
        return client.getName();
    }

    public Client getClient() {
        return client;
    }
}