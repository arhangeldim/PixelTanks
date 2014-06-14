package arhangel.dim.pixeltank.messages;

import arhangel.dim.pixeltank.game.GameObject;

import java.nio.ByteBuffer;

/**
 *
 */
public class RemoveMessage extends Message {

    private int id;

    public RemoveMessage() {}

    public RemoveMessage(GameObject object) {
        this.id = object.getId();
    }

    public int getObjectId() {
        return id;
    }

    @Override
    public int getSize() {
        return 1 + 4;
    }

    @Override
    public void packTo(ByteBuffer buffer, int pos) {
        buffer.position(pos);
        buffer.put(MESSAGE_REMOVE);
        buffer.putInt(id);
    }

    @Override
    public void unpack(ByteBuffer buffer) {
        type = buffer.get();
        id = buffer.getInt();
    }

    @Override
    public String toString() {
        return "RemoveMessage{" +
                "objectId=" + id +
                '}';
    }
}
