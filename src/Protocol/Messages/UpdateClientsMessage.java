package Protocol.Messages;

import Protocol.Client;

import java.util.ArrayList;

public class UpdateClientsMessage extends Message {
    public ArrayList<Client> clients;

    public UpdateClientsMessage(ArrayList<Client> clients) {
        this.clients = clients;
    }
}
