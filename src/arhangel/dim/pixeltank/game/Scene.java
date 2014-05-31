package arhangel.dim.pixeltank.game;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/****/
public class Scene {
    Logger logger = LoggerFactory.getLogger(Scene.class);

    public static final int WIDTH = 600;
    public static final int HEIGHT = 500;

    private int width;
    private int height;
    private Terrain[][] tails;

    private Map<Integer, Unit> units = new HashMap<>();
    Terrain groundTerrain = new Terrain(1, false, new Texture(Color.GRAY));


    public Scene() {
        width = WIDTH;
        height = HEIGHT;
        generateScene();
    }

    private void generateScene() {
        tails = new Terrain[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                tails[i][j] = groundTerrain;
            }
        }
    }

    public Unit generateUnit(int clientId) {
        Unit unit = new Unit(this, clientId, 100, 100, 5, 5);
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
                ", units=" + units +
                '}';
    }

    public void paint(Component component, Graphics g) {
        component.setBackground(groundTerrain.getTexture().getColor());
        for (Map.Entry<Integer, Unit> entry : units.entrySet()) {
            Unit unit = entry.getValue();
            g.setColor(Color.BLUE);
            g.fillRect(unit.getX(), unit.getY(), 10, 10);
        }
    }
}
