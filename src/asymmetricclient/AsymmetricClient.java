package asymmetricclient;

import Protocol.Client;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class AsymmetricClient implements Runnable {


    public AsymmetricClient(Client current) throws IOException, ClassNotFoundException, InterruptedException {

        Parent root;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("asymmetricclient/Client.fxml"));
            root = fxmlLoader.load();

            ClientController controller = fxmlLoader.getController();
            controller.setClient(current);

            Stage stage = new Stage();
            stage.setTitle(current.getName());
            stage.setScene(new Scene(root, 450, 450));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
