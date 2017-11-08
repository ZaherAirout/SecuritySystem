package Protocol;

import javafx.collections.ObservableList;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.security.KeyException;
import java.util.List;

public abstract class Receiver {

    public List<Client> clients;

    public Receiver(ObservableList<Client> clients) {
        this.clients = clients;
    }

    public abstract void execute(ConnectionMessage msg) throws IOException;

    public abstract void execute(TextMessage msg) throws IOException, ClassNotFoundException, KeyException;

    public abstract void execute(CheckOnlineMessage msg);

    public abstract void execute(CloseConnectionMessage msg);

    public void execute(Message msg) throws IOException, ClassNotFoundException, KeyException {

        if (msg instanceof ConnectionMessage)
            this.execute((ConnectionMessage) msg);
        else if (msg instanceof TextMessage)
            this.execute((TextMessage) msg);
        else if (msg instanceof CheckOnlineMessage)
            this.execute((CheckOnlineMessage) msg);
        else if (msg instanceof UpdateClientsMessage)
            this.execute((UpdateClientsMessage) msg);
        else if (msg instanceof CloseConnectionMessage)
            this.execute((CloseConnectionMessage) msg);
        else
            throw new NotImplementedException();

    }

    public abstract void execute(UpdateClientsMessage message);
}
