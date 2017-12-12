package Protocol;

import java.io.Serializable;
import java.net.Socket;
import java.security.PublicKey;

public class Client implements Serializable {
    public PublicKey publicKey;
    private String name;
    private Socket socket;

    public Client(String name, PublicKey publicKey) {
        this.name = name;
        this.publicKey = publicKey;
    }

    public Client(String name) {
        this.name = name;
        this.publicKey = null;
    }

    public Client(Client other) {
        this.name = other.getName();
        this.publicKey = other.publicKey;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Client) {
            Client client = (Client) obj;
            return this.name.equals(client.name);
        }
        return super.equals(obj);
    }

    public String getName() {
        return name;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    @Override
    public String toString() {
        return name;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }
}