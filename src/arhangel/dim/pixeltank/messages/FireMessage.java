package arhangel.dim.pixeltank.messages;

import java.nio.ByteBuffer;

/**
 *
 */
public class FireMessage extends Message {

    public FireMessage() {
    }

    public void unpack(ByteBuffer buffer) {
        type = buffer.get();
    }

    @Override
    public int getSize() {
        return 1;
    }

    @Override
    public void packTo(ByteBuffer buffer, int pos) {
        buffer.position(pos);
        buffer.put(MESSAGE_FIRE);
    }
}
