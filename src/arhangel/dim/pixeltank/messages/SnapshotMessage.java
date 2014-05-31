package arhangel.dim.pixeltank.messages;

import arhangel.dim.pixeltank.game.Scene;

import java.nio.ByteBuffer;

/****/
public class SnapshotMessage extends Message {
    private Scene scene;

    public SnapshotMessage(Scene scene) {
        this.scene = scene;
    }

    @Override
    public int getSize() {
        return 0;
    }

    @Override
    public void packTo(ByteBuffer buffer, int pos) {
    }
}