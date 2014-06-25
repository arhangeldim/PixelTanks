package arhangel.dim.pixeltank.game.controller;

import arhangel.dim.pixeltank.util.SceneUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 */
public class GameEventHandlerTest extends Assert {
    @Test
    public void testIntersect() throws Exception {

        // corners
        assertFalse(SceneUtil.intersect(10, 10, 10, 0, 0, 10)); // top left
        assertFalse(SceneUtil.intersect(10, 10, 10, 20, 0, 10)); // top right
        assertFalse(SceneUtil.intersect(10, 10, 10, 20, 20, 10)); // bottom right
        assertFalse(SceneUtil.intersect(10, 10, 10, 0, 20, 10)); // bottom left

        // sidesSceneUtil
        assertFalse(SceneUtil.intersect(10, 10, 10, 10, 0, 10)); // top
        assertFalse(SceneUtil.intersect(10, 10, 10, 20, 10, 10)); // right
        assertFalse(SceneUtil.intersect(10, 10, 10, 10, 20, 10)); // bottom
        assertFalse(SceneUtil.intersect(10, 10, 10, 0, 10, 10)); // left

        // center
        assertTrue(SceneUtil.intersect(10, 10, 10, 15, 10, 10));
    }

}
