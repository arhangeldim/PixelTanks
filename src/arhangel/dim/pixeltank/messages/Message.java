package arhangel.dim.pixeltank.messages;

import java.nio.ByteBuffer;

/****/
public abstract class Message {
    public static final byte MESSAGE_ACK = 0;
    public static final byte MESSAGE_LOGON = 1;
    public static final byte MESSAGE_CMD_MOVE = 2;
    public static final byte MESSAGE_SNAPSHOT = 3;
    public static final byte MESSAGE_DELTA = 4;
    public static final byte MESSAGE_FIRE = 5;

    private int senderId;
    protected byte type;

    public byte getType() {
        return type;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public abstract int getSize();

    public abstract void packTo(ByteBuffer buffer, int pos);
}