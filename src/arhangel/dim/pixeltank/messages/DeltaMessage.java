package arhangel.dim.pixeltank.messages;

import arhangel.dim.pixeltank.game.Unit;

import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;

/****/
public class DeltaMessage extends Message {
    private Set<Unit> units = new HashSet<>();

    public DeltaMessage() {
    }

    public void unpack(ByteBuffer packed) {
        type = packed.get();
        int size = packed.getInt();
        for (int i = 0; i < size; i++) {
            Unit unit = new Unit();
            unit.unpack(packed.getInt());
            units.add(unit);
        }
    }

    public Set<Unit> getUnits() {
        return units;
    }

    public void addUnit(Unit unit) {
        units.add(unit);
    }

    @Override
    public int getSize() {
        int size = 1 + 4 + 4 * units.size();
        return size;
    }

    @Override
    public void packTo(ByteBuffer buffer, int pos) {
        buffer.position(pos);
        buffer.put(MESSAGE_DELTA);
        buffer.putInt(units.size());
        for (Unit u : units) {
            buffer.putInt(u.pack());
        }
    }

    @Override
    public String toString() {
        return "DeltaMessage{" + "units=" + units + '}';
    }
}