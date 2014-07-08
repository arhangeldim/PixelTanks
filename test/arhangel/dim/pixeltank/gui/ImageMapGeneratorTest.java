package arhangel.dim.pixeltank.gui;

import arhangel.dim.pixeltank.game.scene.Tile;
import org.junit.Assert;
import org.junit.Test;

public class ImageMapGeneratorTest extends Assert {

    @Test
    public void testGenerateMap() throws Exception {
        ResourceLoader resourceLoader = ResourceLoader.getInstance();
        MapGenerator generator = new ImageMapGenerator(resourceLoader);

        Tile[][] tiles = generator.generateMap(10, 10, 10);
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[0].length; j++) {
                if (tiles[i][j].isBlocked())
                    System.out.print(" * ");
                else
                    System.out.print(" . ");
            }
            System.out.println();
        }
    }
}