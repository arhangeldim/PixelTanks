package arhangel.dim.pixeltank.messages;

import java.nio.ByteBuffer;

/****/
public class AckMessage extends Message {
    public static final byte STATUS_SUCCESS = 0;
    public static final byte STATUS_FAILED = 1;

    private byte status;

    public AckMessage(byte status) {
        this.status = status;
    }

    public AckMessage(ByteBuffer buffer) {
        type = buffer.get();
        status = buffer.get();
    }

    public byte getStatus() {
        return status;
    }

    public void setStatus(byte status) {
        this.status = status;
    }

    @Override
    public int getSize() {
        return 2;
    }

    @Override
    public void packTo(ByteBuffer buffer, int pos) {
        buffer.position(pos);
        buffer.put(MESSAGE_ACK);
        buffer.put(status);
    }

    @Override
    public String toString() {
        return "AckMessage{" +
                "status=" + status +
                '}';
    }
}