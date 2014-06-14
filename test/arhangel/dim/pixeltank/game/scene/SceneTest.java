package arhangel.dim.pixeltank.game.scene;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 */
public class SceneTest  extends Assert {
    @Test
    public void testGetTile() throws Exception {
        Scene scene = new Scene(10, 10, 10);
        Tile t = scene.getTile(0, 0);
        assertTrue(0 == t.x && 0 == t.y);

        t = scene.getTile(99, 99);
        assertTrue(9 == t.x && 9 == t.y);

        t = scene.getTile(0, 9);
        assertTrue(0 == t.x && 0 == t.y);

        t = scene.getTile(0, 10);
        assertTrue(0 == t.x && 1 == t.y);

        t = scene.getTile(10, 0);
        assertTrue(1 == t.x && 0 == t.y);
    }
}
