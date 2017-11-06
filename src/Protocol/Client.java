package Protocol;

import java.io.Serializable;
import java.security.PublicKey;

public class Client implements Serializable {
    public PublicKey publicKey;
    String name;
    String IP;

    public Client(String name, String IP, PublicKey publicKey) {
        this.name = name;
        this.IP = IP;
        this.publicKey = publicKey;
    }

    @Override
    public int hashCode() {
        return (name + IP).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Client) {
            Client client = (Client) obj;
            return this.name.equals(client.name) && this.IP.equals(client.IP);
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
        return IP + ":" + name;
    }
}