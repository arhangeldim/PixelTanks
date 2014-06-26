package arhangel.dim.pixeltank.messages;

import arhangel.dim.pixeltank.game.Player;

import java.nio.ByteBuffer;

/****/
public abstract class Message {
    public static final byte MESSAGE_ACK = 0;
    public static final byte MESSAGE_LOGON = 1;
    public static final byte MESSAGE_CMD_MOVE = 2;
    public static final byte MESSAGE_SNAPSHOT = 3;
    public static final byte MESSAGE_DELTA = 4;
    public static final byte MESSAGE_FIRE = 5;
    public static final byte MESSAGE_REMOVE = 6;
    public static final byte MESSAGE_LIFECYCLE = 7;

    private int senderId;
    private Player player;
    protected byte type;

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

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
    
    public abstract void unpack(ByteBuffer buffer);
}