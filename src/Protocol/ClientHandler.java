/*
package Protocol;

import crypto.RSA;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.security.PrivateKey;

import static asymmetricclient.AsymmetricClient.PATH_OF_PRIVATE_KEY;

public class ClientHandler extends Handler implements Runnable {


    private final Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

        while (true) {
            try {

                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Message msg = (Message) ois.readObject();

                execute(msg);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void execute(ConnectionMessage msg) throws IOException {

    }

    @Override
    public void execute(TextMessage msg) throws IOException, ClassNotFoundException {

        // Read Private Key
        File privateKeyFile = new File(PATH_OF_PRIVATE_KEY + msg.sender.getName());
        ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(privateKeyFile));
        PrivateKey privateKey = (PrivateKey) inputStream.readObject();

        // Decrypt text
        RSA rsa = new RSA();
        String result = new String(rsa.decrypt(msg.content, privateKey));

        System.out.println(result);
    }

    @Override
    public void execute(CheckOnlineMessage msg) {

    }

    public void execute(Message msg) throws IOException, ClassNotFoundException {
        super.exectue(msg);
    }
}
*/
