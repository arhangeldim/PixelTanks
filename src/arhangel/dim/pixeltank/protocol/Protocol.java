package arhangel.dim.pixeltank.protocol;

import arhangel.dim.pixeltank.messages.Message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/****/
public interface Protocol {
    public Message decode(DataInputStream in) throws MessageDecodingException, IOException;

    public void encode(DataOutputStream out, Message message) throws IOException;

    public int getVersion();
}
