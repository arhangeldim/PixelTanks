package arhangel.dim.pixeltank.messages;

import arhangel.dim.pixeltank.game.GameObject;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class RemoveMessage extends Message {

    private List<Integer> ids;

    public RemoveMessage() {
        ids = new ArrayList<>();
    }

    public List<Integer> getObjectIds() {
        return ids;
    }

    public void addObjectId(int id) {
        ids.add(id);
    }

    @Override
    public int getSize() {

        return 1 + 4 + 4 * ids.size();
    }

    @Override
    public void packTo(ByteBuffer buffer, int pos) {
        buffer.position(pos);
        buffer.put(MESSAGE_REMOVE);
        buffer.putInt(ids.size());
        for (Integer it : ids) {
            buffer.putInt(it);
        }
    }

    @Override
    public void unpack(ByteBuffer buffer) {
        type = buffer.get();
        int size = buffer.getInt();
        for (int i = 0; i < size; i++) {
            ids.add(buffer.getInt());
        }
    }

    @Override
    public String toString() {
        return "RemoveMessage{" +
                "objectIds=" + ids +
                '}';
    }
}
