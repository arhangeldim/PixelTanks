package arhangel.dim.pixeltank.gui;

import arhangel.dim.pixeltank.Game;
import arhangel.dim.pixeltank.game.scene.Tile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 *
 */
public class ImageMapGenerator implements MapGenerator {
    private static Logger logger = LoggerFactory.getLogger(ImageMapGenerator.class);
    private ResourceLoader resourceLoader;
    private static final String MAP_FILE = "map1.bmp";

    public ImageMapGenerator(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    private Color rgbToColor(int rgb) {
        int a = (rgb >> 23) & 0xff;
        int r = (rgb >>> 15) & 0xff;
        int g = (rgb >>> 7) & 0xff;
        int b = rgb & 0xff;
        return new Color(r, g, b);
    }

    @Override
    public Tile[][] generateMap(int tileWidth, int tileHeight, int tileSize) {
        Tile[][] map = null;
        try {
            BufferedImage image = resourceLoader.loadImageFromResources(Game.class, MAP_FILE);
            int width = image.getWidth();
            int height = image.getHeight();
            map = new Tile[width][height];
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    Color c = rgbToColor(image.getRGB(i, j));
                    map[i][j] = new Tile(tileSize, i, j, c.equals(Color.BLACK));
                }
            }

        } catch (IOException e) {
            logger.warn("Failed to load resource: {}\n{}", MAP_FILE, e);
        }

        return map;
    }
}
