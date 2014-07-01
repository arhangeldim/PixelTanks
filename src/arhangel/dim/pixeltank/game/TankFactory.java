package arhangel.dim.pixeltank.game;

import arhangel.dim.pixeltank.game.scene.Position;
import arhangel.dim.pixeltank.game.scene.Scene;
import arhangel.dim.pixeltank.util.SceneUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

/**
 *
 */
public class TankFactory implements GameObjectFactory {
    private Scene scene;
    private Random random;
    private static final int SIZE = 30;
    private static volatile TankFactory instance;

    private TankFactory(Scene scene) {
        this.scene = scene;
        random = new Random();
    }

    public static synchronized GameObjectFactory getObjectFactory(Scene scene) {
        if (instance == null) {
            instance = new TankFactory(scene);
        }
        return instance;
    }

    private Position getRandomPosition() {
        int x, y;
        boolean intersect = false;
        do {
            x = random.nextInt(scene.getTiledWidth()) * scene.getTileSize();
            y = random.nextInt(scene.getTiledHeight()) * scene.getTileSize();

            // TODO: precompile areas, that are already blocked
            for (GameObject o : scene.getAllObjects()) {
                intersect |= SceneUtil.intersect(x, y, SIZE, o.getPosition().x, o.getPosition().y, o.getSize());
            }
        } while (scene.getTile(x, y).isBlocked() || intersect);
        return new Position(x, y);
    }

    private Direction getRandomDirection() {
        int r = random.nextInt(4);
        return Direction.byCode(r);
    }

    @Override
    public GameObject create(@NotNull Player player) {
        GameObject tank = new Unit(player);
        tank.setType(GameObjectType.UNIT);
        //
        tank.setPosition(getRandomPosition());
        tank.setVelocity(5);
        tank.setSize(SIZE);
        tank.setDirection(getRandomDirection());
        tank.setId(player.getId());

        scene.addObject(player.getId(), tank);
        return tank;
    }
}
