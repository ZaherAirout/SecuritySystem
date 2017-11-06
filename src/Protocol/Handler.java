package Protocol;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;

public abstract class Handler {

    public abstract void execute(ConnectionMessage msg) throws IOException;

    public abstract void execute(TextMessage msg) throws IOException, ClassNotFoundException;

    public abstract void execute(CheckOnlineMessage msg);

    public void exectue(Message msg) throws IOException, ClassNotFoundException {

    }
}
