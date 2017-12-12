package Protocol;

import java.security.PublicKey;

public class ConnectionMessage extends Message {
    PublicKey publicKey;

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }
}
