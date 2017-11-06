package Protocol;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;

public class ServerHandler extends Handler implements Runnable {

    private final Socket socket;
    HashMap<Client, Socket> clients = null;

    public ServerHandler(HashMap<Client, Socket> clients, Socket socket) {
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

        clients.put(msg.sender, socket);

//        Socket socket = clients.get(msg.receiver);

//        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
//        oos.writeObject(msg);
//        oos.flush();
    }

    @Override
    public void execute(TextMessage msg) throws IOException {
        Socket socket = clients.get(msg.receiver);

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
