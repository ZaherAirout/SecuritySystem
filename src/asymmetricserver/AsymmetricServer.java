package asymmetricserver;

import java.io.IOException;

public class AsymmetricServer {

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {

//        Thread.sleep(2000);
//
//        AsymmetricClient asymmetricClient2 = new AsymmetricClient(new Client("Nour", "localhost", null));
//        executor.execute(asymmetricClient2);
//
//        Socket socket2 = serverSocket.accept();
//        ServerHandler handler2 = new ServerHandler(clients, socket2);
//        executor.execute(handler2);

//        while (true) {
//            Socket socket = serverSocket.accept();
//            ServerHandler handler = new ServerHandler(clients, socket);
//            handler.run();
//            executor.execute(handler);
//        }


        /*try {
            // Generating keys and definig a server socket.
            RSA rsa = new RSA();
            rsa.generateKeys(PATH_OF_PRIVATE_KEY, PATH_OF_PUBLIC_KEY);
            ServerSocket welcomeSocket = new ServerSocket(port);
            System.out.println("server is running on port " + port);
            System.out.println(PATH_OF_PRIVATE_KEY);

            System.out.println("new Client Generated");
            ExecutorService executor = Executors.newFixedThreadPool(5);

            AsymmetricClient asymmetricClient = new AsymmetricClient();
            executor.execute(asymmetricClient);

            while (true) {
                Socket socket = welcomeSocket.accept();

                // Sending public key to client.
                File publicKeyFile = new File(PATH_OF_PUBLIC_KEY);
                ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(publicKeyFile));
                PublicKey publicKey = (PublicKey) inputStream.readObject();
                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                outputStream.writeObject(publicKey);

                // Reading a sentence from a client.
                DataInputStream inFromClient = new DataInputStream(socket.getInputStream());
                int length = inFromClient.readInt();
                byte[] sentenceArr = new byte[length];
                inFromClient.read(sentenceArr);
                File privateKeyFile = new File(PATH_OF_PRIVATE_KEY);
                inputStream = new ObjectInputStream(new FileInputStream(privateKeyFile));
                PrivateKey privateKey = (PrivateKey) inputStream.readObject();
                sentenceArr = rsa.decrypt(sentenceArr, privateKey);

                // Convert the sentence to uppercase.
                String sentence = new String(sentenceArr);
                sentence = sentence.toUpperCase();
                // Recieving public key from client.
                inputStream = new ObjectInputStream(socket.getInputStream());
                publicKey = (PublicKey) inputStream.readObject();

                // Encrypt the resulted sentnce and resend back to client.
                DataOutputStream outToClient = new DataOutputStream(socket.getOutputStream());
                sentenceArr = sentence.getBytes();
                sentenceArr = rsa.encrypt(sentenceArr, publicKey);
                outToClient.writeInt(sentenceArr.length);
                outToClient.write(sentenceArr);
                outToClient.flush();
                socket.close();
            }
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(AsymmetricServer.class.getName()).log(Level.SEVERE, null, ex);
        }*/
    }
}
