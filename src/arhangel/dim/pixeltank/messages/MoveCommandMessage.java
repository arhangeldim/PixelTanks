package arhangel.dim.pixeltank.messages;

import java.nio.ByteBuffer;

/****/
public class MoveCommandMessage extends Message {
    public static final byte MOVE_UP = 0;
    public static final byte MOVE_DOWN = 1;
    public static final byte MOVE_RIGHT = 2;
    public static final byte MOVE_LEFT = 3;
    private byte direction;

    public MoveCommandMessage(byte direction) {
        this.direction = direction;
    }

    public MoveCommandMessage(ByteBuffer packed) {
        type = packed.get();
        direction = packed.get();
    }

    public byte getDirection() {
        return direction;
    }

    public void setDirection(byte direction) {
        this.direction = direction;
    }

    @Override
    public int getSize() {
        return 2;
    }

    @Override
    public void packTo(ByteBuffer buffer, int pos) {
        buffer.position(pos);
        buffer.put(MESSAGE_CMD_MOVE);
        buffer.put(direction);
    }

    @Override
    public String toString() {
        return "MoveCommandMessage{" + "direction=" + direction + '}';
    }
}