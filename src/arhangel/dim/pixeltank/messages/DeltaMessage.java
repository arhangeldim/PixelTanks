package arhangel.dim.pixeltank.messages;

import arhangel.dim.pixeltank.game.GameObject;
import arhangel.dim.pixeltank.game.Unit;

import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;

/****/
public class DeltaMessage extends Message {
    private Set<GameObject> deltaObjects = new HashSet<>();

    public DeltaMessage() {
    }

    public void unpack(ByteBuffer packed) {
        type = packed.get();
        int size = packed.getInt();
        for (int i = 0; i < size; i++) {
            Unit unit = new Unit();
            unit.unpack(packed.getInt());
            deltaObjects.add(unit);
        }
    }

    public Set<GameObject> getDeltaObjects() {
        return deltaObjects;
    }

    public void addUnit(Unit unit) {
        deltaObjects.add(unit);
    }

    @Override
    public int getSize() {
        int size = 1 + 4 + 4 * deltaObjects.size();
        return size;
    }

    @Override
    public void packTo(ByteBuffer buffer, int pos) {
        buffer.position(pos);
        buffer.put(MESSAGE_DELTA);
        buffer.putInt(deltaObjects.size());
        for (GameObject object : deltaObjects) {
            buffer.putInt(object.pack());
        }
    }

    @Override
    public String toString() {
        return "DeltaMessage{" + "deltaObjects=" + deltaObjects + '}';
    }
}