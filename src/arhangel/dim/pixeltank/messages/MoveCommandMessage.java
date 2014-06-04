package arhangel.dim.pixeltank.messages;

import arhangel.dim.pixeltank.game.Direction;

import java.nio.ByteBuffer;

/****/
public class MoveCommandMessage extends Message {
    private Direction direction;

    public MoveCommandMessage(Direction cmd) {
        this.direction = cmd;
    }

    public MoveCommandMessage(ByteBuffer packed) {
        type = packed.get();
        byte code = packed.get();
        direction = Direction.byCode(code);
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
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
        buffer.put((byte) direction.getCode());
    }

    @Override
    public String toString() {
        return "MoveCommandMessage{" + "execute=" + direction.getCode() + '}';
    }
}