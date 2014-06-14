package arhangel.dim.pixeltank.game;

import arhangel.dim.pixeltank.game.scene.Position;
import arhangel.dim.pixeltank.game.scene.Scene;

import java.util.Random;

/**
 *
 */
public class TankFactory implements GameObjectFactory {
    private Scene scene;
    private Random random;
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
        do {
            x = random.nextInt(scene.getTiledWidth()) * scene.getTileSize();
            y = random.nextInt(scene.getTiledHeight()) * scene.getTileSize();
        } while (scene.getTile(x, y).isBlocked());
        return new Position(x, y);
    }

    @Override
    public GameObject create(Player player) {
        GameObject tank = new Unit(player);
        tank.setType(GameObjectType.UNIT);
        //
        tank.setPosition(getRandomPosition());
        tank.setVelocity(5);
        tank.setSize(10);
        tank.setDirection(Direction.LEFT);
        tank.setId(player.getId());

        scene.addObject(player.getId(), tank);
        return tank;
    }
}
