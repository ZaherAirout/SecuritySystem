package Protocol;

import java.util.List;

public class UpdateClientsMessage extends Message {
    public List<Client> clients;

    public UpdateClientsMessage(List<Client> clients) {
        this.clients = clients;
    }
}
