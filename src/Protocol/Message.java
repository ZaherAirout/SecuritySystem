package Protocol;

import java.io.Serializable;


public abstract class Message implements Serializable {

    public Client sender;
    public Client receiver;
    public byte[] content;
    public byte[] sessionKey;
    public byte[] digitalSignature;

}
