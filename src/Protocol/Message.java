package Protocol;

import java.io.Serializable;
import java.security.PublicKey;
/*

enum Type {
    TEXT,
    FILE,
    CONNECTION,
    CHECK_ONLINE,
    ONLINE_CLIENTS,
}
*/

public abstract class Message implements Serializable {

    public Client sender;
    public Client receiver;
    public String filename;
    public byte[] content;
    public PublicKey publicKey;
    public byte[] sessionKey;

}
