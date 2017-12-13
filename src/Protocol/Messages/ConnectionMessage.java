package Protocol.Messages;

import java.security.PublicKey;
import java.security.cert.Certificate;

public class ConnectionMessage extends Message {
//    PublicKey publicKey;
    Certificate certificate;


    public void setCertificate(Certificate certificate) {
        this.certificate = certificate;
    }

    public Certificate getCertificate() {
        return certificate;
    }

//    public PublicKey getPublicKey() {
//        return publicKey;
//    }

//    public void setPublicKey(PublicKey publicKey) {
//        this.publicKey = publicKey;
//    }
}
