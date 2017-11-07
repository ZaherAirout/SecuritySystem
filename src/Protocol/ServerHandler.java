package Protocol;

import javafx.collections.ObservableList;
import javafx.util.Pair;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

public class ServerHandler extends Handler implements Runnable {

    private final Socket socket;
    ObservableList<Pair<Client, Socket>> clients = null;

    public ServerHandler(ObservableList<Pair<Client, Socket>> clients, Socket socket) {
        this.clients = clients;
        this.socket = socket;
    }

    @Override
    public void run() {
        while (true) {
            try {

                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Message msg = (Message) ois.readObject();

                execute(msg);

            } catch (IOException | ClassNotFoundException | NotImplementedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void execute(ConnectionMessage msg) throws IOException {

        clients.add(new Pair(msg.sender, socket));

//        Socket socket = clients.get(msg.receiver);

//        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
//        oos.writeObject(msg);
//        oos.flush();
    }

    @Override
    public void execute(TextMessage msg) throws IOException {
        Socket socket = null;

        for (Pair<Client, Socket> pair : clients)
            if (pair.getKey() == msg.receiver) {
                socket = pair.getValue();
                break;
            }

        if (socket == null)
            throw new SocketException("Disconnected");

        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(msg);
        oos.flush();
    }

    @Override
    public void execute(CheckOnlineMessage msg) {

    }

    public void execute(Message msg) throws IOException, ClassNotFoundException {
        super.exectue(msg);
    }
}
