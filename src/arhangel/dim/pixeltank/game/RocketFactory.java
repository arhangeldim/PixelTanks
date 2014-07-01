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
    private static final int SIZE = 10;
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

        Direction dir = owner.getDirection();
        rocket.setDirection(dir);
        Position pos = new Position(owner.getPosition());
        // center
        int full = owner.getSize();
        int half = owner.getSize() / 2;

        switch (dir) {
            case UP:
                pos.x += half;
                pos.y -= SIZE;
                break;
            case RIGHT:
                pos.x += full;
                pos.y += half;
                break;
            case DOWN:
                pos.x += half;
                pos.y += full;
                break;
            case LEFT:
                pos.x -= SIZE;
                pos.y += half;
                break;
        }
        rocket.setPosition(pos);
        rocket.setVelocity(VELOCITY);
        rocket.setSize(SIZE);
        rocket.setId(idGenerator.getAndIncrement());
        return rocket;
    }
}
