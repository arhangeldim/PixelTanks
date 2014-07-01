package arhangel.dim.pixeltank.game.controller;

import arhangel.dim.pixeltank.game.Direction;
import arhangel.dim.pixeltank.game.GameObject;
import arhangel.dim.pixeltank.game.GameObjectType;
import arhangel.dim.pixeltank.game.Player;
import arhangel.dim.pixeltank.game.RocketFactory;
import arhangel.dim.pixeltank.game.TankFactory;
import arhangel.dim.pixeltank.game.scene.Position;
import arhangel.dim.pixeltank.game.scene.Scene;
import arhangel.dim.pixeltank.game.scene.Tile;
import arhangel.dim.pixeltank.util.SceneUtil;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public class GameEventHandler {
    private static Logger logger = LoggerFactory.getLogger(GameEventHandler.class);

    private Scene scene;
    private List<GameEventListener> listeners = new ArrayList<>();
    private RocketFactory rocketFactory;
    private TankFactory tankFactory;

    public void logon(@NotNull Player player) {
        GameObject unit = tankFactory.create(player);
        if (unit != null) {
            for (GameEventListener l : listeners) {
                l.onLogon(player, unit);
            }
        } else {
            for (GameEventListener l : listeners) {
                l.onLogout(player);
            }
        }
    }

    public void move(@NotNull Player player, Direction direction) {
        GameObject unit = scene.getObject(player.getId());
        if (unit == null) {
            logger.warn("Unknown player: {}", player);
            return;
        }
        unit.setDirection(direction);
        List<GameObject> deltas = detectCollision(unit, direction);
        if (!deltas.isEmpty()) {
            for (GameEventListener l : listeners) {
                // NOTE process for all elements in list
                l.onMove(deltas.get(0));
            }
        }
    }

    public void fire(@NotNull Player player) {
        GameObject owner = scene.getObject(player.getId());
        logger.info("Fire on dir: {}", owner.getDirection());
        GameObject bullet = rocketFactory.create(owner);
        scene.addObject(bullet.getId(), bullet);
        for (GameEventListener l : listeners) {
            l.onFire(player, bullet);
        }
        new Thread(new BulletTrace(bullet)).start();
    }

    private Position validatePosition(Position position, Direction direction, int velocity, int size) {
        Position newPos = new Position(position);
        int x = position.x;
        int y = position.y;
        Tile tile1, tile2;
        switch (direction) {
            case LEFT:
                x -= velocity;
                if (x < 0)
                    return position;
                tile1 = scene.getTile(x, y);
                tile2 = scene.getTile(x, y + size - 1);
                if (tile1.isBlocked() || tile2.isBlocked())
                    return position;
                break;
            case RIGHT:
                x += velocity;
                if (x + size > scene.getWidth())
                    return position;
                tile1 = scene.getTile(x + size - 1, y);
                tile2 = scene.getTile(x + size - 1, y + size - 1);
                if (tile1.isBlocked() || tile2.isBlocked())
                    return position;
                break;
            case UP:
                y -= velocity;
                if (y < 0)
                    return position;
                tile1 = scene.getTile(x, y);
                tile2 = scene.getTile(x + size - 1, y);
                if (tile1.isBlocked() || tile2.isBlocked())
                    return position;
                break;
            case DOWN:
                y += velocity;
                if (y + size > scene.getHeight())
                    return position;
                tile1 = scene.getTile(x, y + size - 1);
                tile2 = scene.getTile(x + size - 1, y + size - 1);
                if (tile1.isBlocked() || tile2.isBlocked())
                    return position;

        }
        newPos.x = x;
        newPos.y = y;
        return newPos;
    }

    public List<GameObject> detectCollision(GameObject object, Direction direction) {
        List<GameObject> deltas = new ArrayList<>();
        Position pos = object.getPosition();
        Position newPos = validatePosition(pos, direction, object.getVelocity(), object.getSize());

        for (GameObject other : scene.getAllObjects()) {
            if (other != object && SceneUtil.intersect(newPos, object.getSize(), other.getPosition(), other.getSize())) {
                if ((other.getType() == GameObjectType.ROCKET && object.getType() == GameObjectType.UNIT)
                        || (other.getType() == GameObjectType.UNIT && object.getType() == GameObjectType.ROCKET)) {
                    // remove objects from scene

                    scene.removeObject(object.getId());
                    scene.removeObject(other.getId());
                    for (GameEventListener l : listeners) {
                        l.onKill(object.getPlayer(), object);
                        l.onKill(other.getPlayer(), other);
                    }
                }
                return deltas;
            }
        }
        if (!newPos.equals(pos)) {
            object.setPosition(newPos);
            deltas.add(object);
        }
        return deltas;
    }

    class BulletTrace implements Runnable {

        private GameObject bullet;

        public BulletTrace(GameObject bullet) {
            this.bullet = bullet;
        }

        private void removeBullet() {
            logger.info("Remove bullet from scene: {}", bullet);

            scene.removeObject(bullet.getId());
            for (GameEventListener l : listeners) {
                l.onKill(bullet.getPlayer(), bullet);
            }
        }

        @Override
        public void run() {
            Position pos = bullet.getPosition();
            int v = bullet.getVelocity();
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    switch (bullet.getDirection()) {
                        case RIGHT:
                            pos.x += v;
                            break;
                        case LEFT:
                            pos.x -= v;
                            break;
                        case UP:
                            pos.y -= v;
                            break;
                        case DOWN:
                            pos.y += v;
                            break;
                    }
                    List<GameObject> deltas = detectCollision(bullet, bullet.getDirection());
                    if (!deltas.isEmpty()) {
                        for (GameEventListener l : listeners) {
                            l.onMove(bullet);
                        }
                    } else {
                        logger.info("No delta message. ");
                        removeBullet();
                        return;
                    }

                    TimeUnit.MILLISECONDS.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    public Scene getScene() {
        return scene;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public void addGameEventListener(GameEventListener listener) {
        listeners.add(listener);
    }

    public RocketFactory getRocketFactory() {
        return rocketFactory;
    }

    public void setRocketFactory(RocketFactory rocketFactory) {
        this.rocketFactory = rocketFactory;
    }

    public TankFactory getTankFactory() {
        return tankFactory;
    }

    public void setTankFactory(TankFactory tankFactory) {
        this.tankFactory = tankFactory;
    }
}
