package arhangel.dim.pixeltank.game.controller;

import arhangel.dim.pixeltank.game.Direction;
import arhangel.dim.pixeltank.game.GameObject;
import arhangel.dim.pixeltank.game.Player;
import arhangel.dim.pixeltank.game.RocketFactory;
import arhangel.dim.pixeltank.game.TankFactory;
import arhangel.dim.pixeltank.game.scene.Position;
import arhangel.dim.pixeltank.game.scene.Scene;
import arhangel.dim.pixeltank.game.scene.Tile;
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

    public void logon(Player player) {
        if (player == null) {
            throw new RuntimeException("Unexpected value");
        }
        if (tankFactory.create(player) != null) {
            for (GameEventListener l : listeners) {
                l.onLogon(player);
            }
        } else {
            for (GameEventListener l : listeners) {
                l.onLogout(player);
            }
        }
    }

    public void move(Player player, Direction direction) {
        GameObject unit = scene.getObject(player.getId());
        if (unit == null) {
            logger.warn("Unknown player: {}", player);
            return;
        }
        unit.setDirection(direction);
        List<GameObject> deltas = detectCollision(unit);
        if (!deltas.isEmpty()) {
            for (GameEventListener l : listeners) {
                // NOTE process for all elements in list
                l.onMove(deltas.get(0));
            }
        }
    }

    public void fire(Player player) {
        GameObject owner = scene.getObject(player.getId());
        logger.info("Fire on dir: {}", owner.getDirection());
        GameObject bullet = rocketFactory.create(owner);
        scene.addObject(bullet.getId(), bullet);
        new Thread(new BulletTrace(bullet)).start();
    }

    public List<GameObject> detectCollision(GameObject object) {
        List<GameObject> deltas = new ArrayList<>();
        Position pos = object.getPosition();
        // check new position
        int x = pos.x;
        int y = pos.y;
        int v = object.getVelocity();
        Direction dir = object.getDirection();
        Tile tile1, tile2;
        switch (dir) {
            case LEFT:
                x -= v;
                if (x < 0)
                    return deltas;
                tile1 = scene.getTile(x, y);
                tile2 = scene.getTile(x, y + object.getSize());
                if (tile1.isBlocked() || tile2.isBlocked())
                    return deltas;
                break;
            case RIGHT:
                x += v;
                if (x + object.getSize() > scene.getWidth())
                    return deltas;
                tile1 = scene.getTile(x + object.getSize() - 1, y);
                tile2 = scene.getTile(x + object.getSize() - 1, y + object.getSize());
                if (tile1.isBlocked() || tile2.isBlocked())
                    return deltas;
                break;
            case UP:
                y -= v;
                if (y < 0)
                    return deltas;
                tile1 = scene.getTile(x, y);
                tile2 = scene.getTile(x + object.getSize(), y);
                if (tile1.isBlocked() || tile2.isBlocked())
                    return deltas;
                break;
            case DOWN:
                y += v;
                if (y + object.getSize() > scene.getHeight())
                    return deltas;
                tile1 = scene.getTile(x, y + object.getSize() - 1);
                tile2 = scene.getTile(x + object.getSize(), y + object.getSize() - 1);
                if (tile1.isBlocked() || tile2.isBlocked())
                    return deltas;

        }

        pos.x = x;
        pos.y = y;
        deltas.add(object);
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
                    logger.info("Handle bullet: {}", bullet);
                    List<GameObject> deltas = detectCollision(bullet);
                    if (!deltas.isEmpty()) {
                        for (GameEventListener l : listeners) {
                            l.onMove(bullet);
                        }
                    } else {
                        logger.info("No delta message. ");
                        removeBullet();
                        return;
                    }

                    TimeUnit.MILLISECONDS.sleep(500);
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
