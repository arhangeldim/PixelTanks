package arhangel.dim.pixeltank.messages;

import arhangel.dim.pixeltank.game.Scene;
import org.junit.Test;

import java.nio.ByteBuffer;

/**
 *
 */
public class SnapshotMessageTest {
    @Test
    public void testPackTo() throws Exception {
        Scene scene = new Scene(20, 20, 10);
        scene.generateUnit(0);
        scene.generateUnit(1);
        SnapshotMessage message = new SnapshotMessage(scene);

        ByteBuffer buffer = ByteBuffer.allocate(message.getSize());
        message.packTo(buffer, 0);

        buffer.position(0);

        message = new SnapshotMessage(buffer);
        System.out.println(message);
    }
}
