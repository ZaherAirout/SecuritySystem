package asymmetricclient;

import Protocol.Client;
import Protocol.TextMessage;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsymmetricClient implements Runnable {

    private static final String cwd = System.getProperty("user.dir");
    static final String PATH_OF_PRIVATE_KEY = String.join(File.separator, cwd, "asymmetricclient", "MyPrivateKey");
    static final String PATH_OF_PUBLIC_KEY = String.join(File.separator, cwd, "asymmetricclient", "MyPublicKey");
    ExecutorService executorService = Executors.newFixedThreadPool(5);

    Socket socket = null;

    Client current = null;
    PublicKey publicKey;

    HashMap<Client, byte[]> sessionKeys;
    List<Client> onlineClients;


    Receiver receiver;
    final Sender sender;


    public AsymmetricClient(Client current) throws IOException, ClassNotFoundException, InterruptedException {

        this.current = current;
        sessionKeys = new HashMap<>();

        // TODO: show progress bar
        // ProgressBar.setVisiable(true);

        // Connect to server
        Connector connector = new Connector(this.current);
        executorService.execute(connector);

        // TODO: Hide progress bad
        // ProgressBar.setVisiable(false);

        // TODO: Remove this
//        Thread.sleep(2000);

        socket = connector.socket;
        onlineClients = connector.clients;

        // Receive and Handle messages async
        receiver = new Receiver(socket, sessionKeys);
        executorService.execute(receiver);

        // Create sender to send messages async
        sender = new Sender(socket, sessionKeys);
    }


    /***
     * Send Message from current user using GUI to receiver
     * @param e
     */
    public void sendMessage(ActionEvent e) {

        // Create message
        TextMessage message = new TextMessage();

        // TODO: receiver from GUI, remove hard-coded
        Client receiver = new Client("Ahmed", "localhost", null);

        message.sender = current;
        message.receiver = receiver;

        // TODO: message from GUI, remove hard-coded
        String messageContent = "Hello " + receiver.getName();

        // sync so that messages don't get conflicted.
        synchronized (sender) {
            sender.setMessage(message);
            sender.setContent(messageContent);
            executorService.execute(sender);
        }
    }

    @Override
    public void run() {
/*

        // send 100 messages to Ahmed PC
        for (int i = 0; i < 3; i++) {

            // TODO: retrieve message from GUI
            TextMessage message = new TextMessage();

            // TODO: retrieve client from GUI
            Client senderClient = new Client("Ahmed", "localhost", null);

            message.sender = senderClient;
            message.receiver = senderClient;

            synchronized (sender) {
                sender.setMessage(message);
                sender.setContent("Hello" + i);
                executorService.execute(sender);
            }


        }
*/


        /*        try {
            // Generating keys and defining a socket.
//            RSA rsa = new RSA();
//            rsa.generateKeys(PATH_OF_PRIVATE_KEY, PATH_OF_PUBLIC_KEY);
            Socket socket = new Socket("localhost", 1234);

            // Reading public key of server from socket, and reading private key of client from local pc.
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            PublicKey publicKey = (PublicKey) inputStream.readObject();

            // Entering the sentence which will be sent after encryption.
            System.out.println("Please enter the sentence you want to send:");
//            Scanner s = new Scanner(System.in);
//            String sentence = s.nextLine();
            String sentence = "hello World !!";

            byte[] sentenceArr = sentence.getBytes();
            sentenceArr = rsa.encrypt(sentenceArr, publicKey);
            DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());
            outToServer.writeInt(sentenceArr.length);
            outToServer.write(sentenceArr);

            // Sending public key to server.
            File publicKeyFile = new File(PATH_OF_PUBLIC_KEY);
            inputStream = new ObjectInputStream(new FileInputStream(publicKeyFile));
            publicKey = (PublicKey) inputStream.readObject();
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.writeObject(publicKey);

            // Reading the uppercase sentence back from server using the socket.
            DataInputStream inFromServer = new DataInputStream(socket.getInputStream());
            int length = inFromServer.readInt();
            sentenceArr = new byte[length];
            inFromServer.read(sentenceArr);

            // Reading private key from local pc and dycrypt the received sentence.
            File privateKeyFile = new File(PATH_OF_PRIVATE_KEY);
            inputStream = new ObjectInputStream(new FileInputStream(privateKeyFile));
            PrivateKey privateKey = (PrivateKey) inputStream.readObject();
            sentenceArr = rsa.decrypt(sentenceArr, privateKey);
            sentence = new String(sentenceArr);
            System.out.println(sentence);

            socket.close();
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(AsymmetricClient.class.getName()).log(Level.SEVERE, null, ex);
        }*/
    }


}
