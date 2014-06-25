package arhangel.dim.pixeltank.messages;

import arhangel.dim.pixeltank.game.GameObject;
import arhangel.dim.pixeltank.game.GameObjectFactory;
import arhangel.dim.pixeltank.game.Player;
import arhangel.dim.pixeltank.game.TankFactory;
import arhangel.dim.pixeltank.game.scene.Scene;
import org.junit.Assert;
import org.junit.Test;

import java.nio.ByteBuffer;

/**
 *
 */
public class LifecycleMessageTest extends Assert {

    @Test
    public void pack() {
        LifecycleMessage m = new LifecycleMessage();
        Player p = new Player(12);
        p.setName("Test");
        m.setPlayer(p);

        Scene scene = new Scene(30, 20, 20);
        GameObjectFactory gameObjectFactory = TankFactory.getObjectFactory(scene);
        GameObject object = gameObjectFactory.create(p);
        m.setObject(object);
        m.setStage(LifecycleMessage.SPAWNED);

        ByteBuffer buffer = ByteBuffer.allocate(m.getSize());
        m.packTo(buffer, 0);

        buffer.position(0);
        LifecycleMessage restored = new LifecycleMessage();
        restored.unpack(buffer);

        assertEquals(p.getName(), restored.getPlayer().getName());
        assertEquals(p.getId(), restored.getPlayer().getId());
        //assertEquals(object, restored.getObject());
    }
}
