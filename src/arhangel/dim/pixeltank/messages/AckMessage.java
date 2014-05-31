package arhangel.dim.pixeltank.messages;

import java.nio.ByteBuffer;

/****/
public class AckMessage extends Message {
    public AckMessage() {
    }

    public AckMessage(ByteBuffer buffer) {
        type = buffer.get();
    }

    @Override
    public int getSize() {
        return 1;
    }

    @Override
    public void packTo(ByteBuffer buffer, int pos) {
        buffer.position(pos);
        buffer.put(MESSAGE_ACK);
    }

    @Override
    public String toString() {
        return "AckMessage{}";
    }
}