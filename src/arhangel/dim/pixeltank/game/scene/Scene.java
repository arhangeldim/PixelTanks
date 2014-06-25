package arhangel.dim.pixeltank.game.scene;

import arhangel.dim.pixeltank.game.GameObject;
import arhangel.dim.pixeltank.game.GameObjectType;
import arhangel.dim.pixeltank.gui.ResourceLoader;
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
    private Map<Integer, GameObject> gameObjectMap = new HashMap<>();
    private ResourceLoader resourceLoader;

    public Scene(int tiledWidth, int tiledHeight, int tileSize) {
        this.width = tileSize * tiledWidth;
        this.height = tileSize * tiledHeight;
        this.tileSize = tileSize;
        this.tiledWidth = tiledWidth;
        this.tiledHeight = tiledHeight;
        tiles = new Tile[tiledWidth][tiledHeight];
        generateScene();
        resourceLoader = ResourceLoader.getInstance();
    }

    public int getTileSize() {
        return tileSize;
    }

    public int getTiledWidth() {
        return tiledWidth;
    }

    public int getTiledHeight() {
        return tiledHeight;
    }

    public Tile getTile(Position position) {
        return getTile(position.x, position.y);
    }

    public Tile getTile(int x, int y) {
        int xPos = (x) / tileSize;
        int yPos = (y) / tileSize;
        Tile tile = tiles[xPos][yPos];
        logger.info("({}, {}) -> {}", x, y, tile);
        return tile;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
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

    public boolean updateObject(int id, GameObject unit) {
        if (gameObjectMap.containsKey(id)) {
            gameObjectMap.put(id, unit);
            return true;
        }
        return false;
    }

    public void addObject(int clientId, GameObject unit) {
        logger.debug("Add an object: {}", unit);
        gameObjectMap.put(clientId, unit);
    }

    public void removeObject(int clientId) {
        gameObjectMap.remove(clientId);
    }

    public Collection<GameObject> getAllObjects() {
        return gameObjectMap.values();
    }

    public GameObject getObject(int id) {
        GameObject gameObject = gameObjectMap.get(id);
        logger.info("Get object by id: {}, {}", id, gameObject);
        return gameObject;
    }

    @Override
    public String toString() {
        return "Scene{" +
                "width=" + width +
                ", height=" + height +
                ", tileSize=" + tileSize +
                ", tiledWidth=" + tiledWidth +
                ", tiledHeight=" + tiledHeight +
                ", gameObjectMap=" + gameObjectMap +
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
        for (Map.Entry<Integer, GameObject> entry : gameObjectMap.entrySet()) {
            GameObject gameObject = entry.getValue();
            Position pos = gameObject.getPosition();
            if (gameObject.getType() == GameObjectType.UNIT) {
                g.setColor(Color.BLUE);
                gameObject.setSize(30);
                g.drawImage(resourceLoader.getSpriteByDirection(gameObject), pos.x, pos.y, component);
            } else {
                // rocket
                g.setColor(Color.RED);
                gameObject.setSize(5);
                g.fillOval(pos.x, pos.y, gameObject.getSize(), gameObject.getSize());
            }
        }
    }
}
