package arhangel.dim.pixeltank.gui;

import arhangel.dim.pixeltank.game.scene.Tile;

/**
 *
 */
public interface MapGenerator {
    Tile[][] generateMap(int tileWidth, int tileHeight, int tileSize);
}
