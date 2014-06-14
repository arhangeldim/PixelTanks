package arhangel.dim.pixeltank.game.controller;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 */
public class GameEventHandlerTest extends Assert {
    @Test
    public void testIntersect() throws Exception {
        GameEventHandler h = new GameEventHandler();

        // corners
        assertFalse(h.intersect(10, 10, 10, 0, 0, 10)); // top left
        assertFalse(h.intersect(10, 10, 10, 20, 0, 10)); // top right
        assertFalse(h.intersect(10, 10, 10, 20, 20, 10)); // bottom right
        assertFalse(h.intersect(10, 10, 10, 0, 20, 10)); // bottom left

        // sides
        assertFalse(h.intersect(10, 10, 10, 10, 0, 10)); // top
        assertFalse(h.intersect(10, 10, 10, 20, 10, 10)); // right
        assertFalse(h.intersect(10, 10, 10, 10, 20, 10)); // bottom
        assertFalse(h.intersect(10, 10, 10, 0, 10, 10)); // left

        // center
        assertTrue(h.intersect(10, 10, 10, 15, 10, 10));
    }

}
