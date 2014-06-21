package arhangel.dim.pixeltank.game;

import arhangel.dim.pixeltank.game.scene.Scene;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 */
public class GameObjectTest extends Assert {

    @Test
    public void testPack() {
        Player p = new Player(1);
        Scene scene = new Scene(30, 20, 20);
        GameObjectFactory factory = TankFactory.getObjectFactory(scene);
        GameObject object = factory.create(p);

        long packed = object.pack();

        Unit restored = new Unit();
        restored.unpack(packed);

        assertEquals(object.getId(), restored.getId());
        assertEquals(object.getPosition(), restored.getPosition());
        assertEquals(object.getDirection(), restored.getDirection());
    }
}
