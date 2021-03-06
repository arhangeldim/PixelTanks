package arhangel.dim.pixeltank.messages;

import arhangel.dim.pixeltank.game.GameObject;
import arhangel.dim.pixeltank.game.scene.Scene;
import arhangel.dim.pixeltank.game.Unit;

import java.nio.ByteBuffer;

/****/
public class SnapshotMessage extends Message {
    private Scene scene;

    public SnapshotMessage() {
    }

    public SnapshotMessage(Scene scene) {
        this.scene = scene;
    }

    public void unpack(ByteBuffer packed) {
        type = packed.get();
        // tiled W, H, tile size
        scene = new Scene(packed.get(), packed.get(), packed.get());
        int size = packed.getInt();
        for (int i = 0; i < size; i++) {
            Unit unit = new Unit();
            unit.unpack(packed.getInt());
            scene.addObject(unit.getId(), unit);
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
        return 1 + 4 + 3 +  4 * scene.getAllObjects().size();
    }

    @Override
    public void packTo(ByteBuffer buffer, int pos) {
        buffer.position(pos);
        buffer.put(MESSAGE_SNAPSHOT);
        buffer.put((byte) scene.getTiledWidth());
        buffer.put((byte) scene.getTiledHeight());
        buffer.put((byte) scene.getTileSize());
        buffer.putInt(scene.getAllObjects().size());
        for (GameObject ob : scene.getAllObjects()) {
            buffer.putInt(ob.pack());
        }
    }

    @Override
    public String toString() {
        return "SnapshotMessage{" +
                "scene=" + scene +
                '}';
    }
}