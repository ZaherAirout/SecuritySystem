package Protocol;

import javafx.collections.ObservableList;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.net.Socket;
import java.security.KeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;

public abstract class Receiver {

    public ObservableList clients;

    public Receiver(ObservableList clients) {
        this.clients = clients;
    }

    public abstract void execute(ConnectionMessage msg, Socket socket) throws IOException;

    public void execute(TextMessage msg, Socket socket) throws IOException, ClassNotFoundException, KeyException, SignatureException, NoSuchProviderException, NoSuchAlgorithmException {
    }


    public void execute(FileMessage msg, Socket socket) throws KeyException, IOException, NoSuchProviderException, NoSuchAlgorithmException, SignatureException {

    }

    public abstract void execute(CheckOnlineMessage msg, Socket socket);

    public abstract void execute(CloseConnectionMessage msg, Socket socket);

    public void execute(Message msg, Socket socket) throws IOException, ClassNotFoundException, KeyException, NoSuchAlgorithmException, SignatureException, NoSuchProviderException {

        if (msg instanceof ConnectionMessage)
            this.execute((ConnectionMessage) msg, socket);
        else if (msg instanceof TextMessage)
            this.execute((TextMessage) msg, socket);
        else if (msg instanceof CheckOnlineMessage)
            this.execute((CheckOnlineMessage) msg, socket);
        else if (msg instanceof UpdateClientsMessage)
            this.execute((UpdateClientsMessage) msg, socket);
        else if (msg instanceof PKMessage)
            this.execute((PKMessage) msg, socket);
        else if (msg instanceof CloseConnectionMessage)
            this.execute((CloseConnectionMessage) msg, socket);
        else if (msg instanceof FileMessage)
            this.execute((FileMessage) msg, socket);
        else
            throw new NotImplementedException();

    }

    public abstract void execute(UpdateClientsMessage message, Socket socket);

    public abstract void execute(PKMessage message, Socket socket);
}
