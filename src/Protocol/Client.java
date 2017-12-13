package Protocol;

import java.io.Serializable;
import java.net.Socket;
import java.security.PublicKey;
import java.security.cert.Certificate;

public class Client implements Serializable {
    private String name;
    private Socket socket;
    public Certificate certificate;

    public Client(String name, Certificate certificate) {
        this.name = name;
        this.certificate = certificate;
    }

    public Client(String name) {
        this.name = name;
        this.certificate = null;
    }

    public Client(Client other) {
        this.name = other.getName();
        this.certificate = other.certificate;
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

    public Certificate getCertificate() {
        return certificate;
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