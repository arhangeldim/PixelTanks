package arhangel.dim.pixeltank.game;

import arhangel.dim.pixeltank.game.scene.Position;
import arhangel.dim.pixeltank.game.scene.Scene;

import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 */
public class TankFactory implements GameObjectFactory {
    private Scene scene;
    private static volatile TankFactory instance;

    private TankFactory(Scene scene) {
        this.scene = scene;
    }

    public static synchronized GameObjectFactory getObjectFactory(Scene scene) {
        if (instance == null) {
            instance = new TankFactory(scene);
        }
        return instance;
    }

    @Override
    public GameObject create(Player player) {
        GameObject tank = new Unit(player);
        //
        tank.setPosition(new Position(100, 100));
        tank.setVelocity(5);
        tank.setSize(10);
        tank.setDirection(Direction.LEFT);
        tank.setId(player.getId());

        scene.addObject(player.getId(), tank);
        return tank;
    }
}
