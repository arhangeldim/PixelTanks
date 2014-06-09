package arhangel.dim.pixeltank.game.scene;

import arhangel.dim.pixeltank.game.Unit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/****/
public class Scene {
    Logger logger = LoggerFactory.getLogger(Scene.class);

    private int width, height;
    private int tileSize;
    private int tiledWidth, tiledHeight;
    private Tile[][] tiles;

    private static final int UNIT_SIZE = 20;

    public Scene(int tiledWidth, int tiledHeight, int tileSize) {
        this.width = tileSize * tiledWidth;
        this.height = tileSize * tiledHeight;
        this.tileSize = tileSize;
        this.tiledWidth = tiledWidth;
        this.tiledHeight = tiledHeight;
        tiles = new Tile[tiledWidth][tiledHeight];
        generateScene();
    }


    public int getTileSize() {
        return tileSize;
    }

    public void setTileSize(int tileSize) {
        this.tileSize = tileSize;
    }

    public int getTiledWidth() {
        return tiledWidth;
    }

    public void setTiledWidth(int tiledWidth) {
        this.tiledWidth = tiledWidth;
    }

    public int getTiledHeight() {
        return tiledHeight;
    }

    public void setTiledHeight(int tiledHeight) {
        this.tiledHeight = tiledHeight;
    }

    // TODO: check this !
    public Tile getTile(int x, int y) {
        int xPos = (x) / tileSize;
        int yPos = (y) / tileSize;
        Tile tile = tiles[xPos][yPos];
        logger.info("({}, {}) -> {}", x, y, tile);
        return tile;
    }


    private Map<Integer, Unit> units = new HashMap<>();
    Terrain groundTerrain = new Terrain(1, false, new Texture(Color.GRAY));


    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    private void generateScene() {
        tiles = new Tile[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                tiles[i][j] = new Tile(tileSize, i, j, false);
            }
        }

        for (int i = 3; i < 7; i++) {
            tiles[i][4].isBlocked = true;

        }
    }

    public Unit generateUnit(int clientId) {
        Unit unit = new Unit(clientId, new Position(100, 100), 5, UNIT_SIZE);
        units.put(clientId, unit);
        return unit;
    }

    public boolean updateUnit(int id, Unit unit) {
        if (units.containsKey(id)) {
            units.put(id, unit);
            return true;
        }
        return false;
    }

    public void addUnit(int clientId, Unit unit) {
        units.put(clientId, unit);
    }

    public void removeUnit(int clientId) {
        units.remove(clientId);
    }


    public Collection<Unit> getAllUnits() {
        return units.values();
    }

    public void addUnit(Unit unit) {
        logger.info("Added new unit {}", unit);
        units.put(unit.getId(), unit);
    }

    public Unit getUnit(int id) {
        Unit unit = units.get(id);
        logger.info("Get unit by id: {}, {}", id, unit);
        return unit;
    }

    public boolean isValidPosition(int x, int y) {
        return (x >= 0 && x <= width && y >= 0 && y <= height);
    }

    @Override
    public String toString() {
        return "Scene{" +
                "width=" + width +
                ", height=" + height +
                ", tileSize=" + tileSize +
                ", tiledWidth=" + tiledWidth +
                ", tiledHeight=" + tiledHeight +
                ", units=" + units +
                '}';
    }

    public void paint(Component component, Graphics g) {
        component.setBackground(Color.WHITE);
        for (int i = 0; i < tiledWidth; i++) {
            for (int j = 0; j < tiledHeight; j++) {
                g.setColor((i + j) % 2 == 0 ? Color.BLUE : Color.BLACK);
                Tile t = tiles[i][j];
                if (t.isBlocked) {
                    g.setColor(Color.BLACK);
                    g.fillRect(t.x * tileSize, t.y * tileSize, t.tileSize, t.tileSize);
                }
                g.setColor(Color.BLUE);
                g.drawRect(t.x * tileSize, t.y * tileSize, t.tileSize, t.tileSize);
            }
        }


        //component.setBackground(groundTerrain.getTexture().getColor());
        for (Map.Entry<Integer, Unit> entry : units.entrySet()) {
            Unit unit = entry.getValue();
            g.setColor(Color.BLUE);
            Position pos = unit.getPosition();
            g.fillRect(pos.x, pos.y, UNIT_SIZE, UNIT_SIZE);
        }
    }
}
