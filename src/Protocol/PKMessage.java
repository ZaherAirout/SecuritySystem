package Protocol;

import java.security.PublicKey;

public class PKMessage extends Message {
    private PublicKey publicKey;

    public PKMessage(Client receiver) {
        super();
        this.receiver = receiver;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;

    }
}

