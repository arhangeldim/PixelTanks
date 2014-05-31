package arhangel.dim.pixeltank;

import arhangel.dim.pixeltank.messages.Message;

import java.io.IOException;

/****/
public interface GameConnection {
    public void start() throws IOException;

    public void stop();

    public void send(Message message) throws IOException;

    public void addConnectionListener(ConnectionListener listener);
}
