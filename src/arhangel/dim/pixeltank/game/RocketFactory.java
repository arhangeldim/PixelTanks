package arhangel.dim.pixeltank.game;

import arhangel.dim.pixeltank.game.scene.Position;
import arhangel.dim.pixeltank.game.scene.Scene;

import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 */
public class RocketFactory {
    private static AtomicInteger idGenerator = new AtomicInteger(50);
    private Scene scene;
    private static volatile RocketFactory instance;
    private static final int SIZE = 5;
    private static final int VELOCITY = 5;

    private RocketFactory(Scene scene) {
        this.scene = scene;
    }

    public static synchronized RocketFactory getObjectFactory(Scene scene) {
        if (instance == null) {
            instance = new RocketFactory(scene);
        }
        return instance;
    }

    public GameObject create(GameObject owner) {
        GameObject rocket = new Unit();
        rocket.setPlayer(owner.getPlayer());
        rocket.setType(GameObjectType.ROCKET);
        rocket.setPosition(new Position(owner.getPosition().x, owner.getPosition().y));
        rocket.setDirection(owner.getDirection());
        rocket.setVelocity(VELOCITY);
        rocket.setSize(SIZE);
        rocket.setId(idGenerator.getAndIncrement());
        return rocket;
    }
}
