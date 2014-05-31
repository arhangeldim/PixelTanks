package arhangel.dim.pixeltank.messages;

import arhangel.dim.pixeltank.game.Scene;
import arhangel.dim.pixeltank.game.Unit;

import java.nio.ByteBuffer;

/****/
public class SnapshotMessage extends Message {
    private Scene scene;

    public SnapshotMessage(Scene scene) {
        this.scene = scene;
    }

    public SnapshotMessage(ByteBuffer packed) {
        type = packed.get();
        scene = new Scene();
        int size = packed.getInt();
        for (int i = 0; i < size; i++) {
            Unit unit = new Unit();
            unit.unpack(packed.getInt());
            scene.addUnit(unit);
        }
    }

    public Scene getScene() {
        return scene;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    @Override
    public int getSize() {
        return 1 + 4 + 4 * scene.getAllUnits().size();
    }

    @Override
    public void packTo(ByteBuffer buffer, int pos) {
        buffer.position(pos);
        buffer.put(MESSAGE_SNAPSHOT);
        buffer.putInt(scene.getAllUnits().size());
        for (Unit u : scene.getAllUnits()) {
            buffer.putInt(u.pack());
        }
    }

    @Override
    public String toString() {
        return "SnapshotMessage{" +
                "scene=" + scene +
                '}';
    }
}