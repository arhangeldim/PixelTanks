package arhangel.dim.pixeltank.connection;

import arhangel.dim.pixeltank.messages.Message;

/****/
public interface ConnectionListener {
    public void onMessageReceived(Message message);
}
