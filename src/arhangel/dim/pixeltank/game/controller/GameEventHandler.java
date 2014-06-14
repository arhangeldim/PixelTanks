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
        logger.info("TEST: {}, {}", player, direction);
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

    public void fire(Player player) {
        GameObject owner = scene.getObject(player.getId());
        logger.info("Fire on dir: {}", owner.getDirection());
        GameObject bullet = rocketFactory.create(owner);
        scene.addObject(bullet.getId(), bullet);
        new Thread(new BulletTrace(bullet)).start();
    }

    public List<GameObject> detectCollision(GameObject object, Direction direction) {
        List<GameObject> deltas = new ArrayList<>();
        Position pos = object.getPosition();
        // check new position
        int x = pos.x;
        int y = pos.y;
        int v = object.getVelocity();
        Tile tile1, tile2;
        switch (direction) {
            case LEFT:
                x -= v;
                if (x < 0)
                    return deltas;
                tile1 = scene.getTile(x, y);
                tile2 = scene.getTile(x, y + object.getSize() - 1);
                if (tile1.isBlocked() || tile2.isBlocked())
                    return deltas;
                break;
            case RIGHT:
                x += v;
                if (x + object.getSize() > scene.getWidth())
                    return deltas;
                tile1 = scene.getTile(x + object.getSize() - 1, y);
                tile2 = scene.getTile(x + object.getSize() - 1, y + object.getSize() - 1);
                if (tile1.isBlocked() || tile2.isBlocked())
                    return deltas;
                break;
            case UP:
                y -= v;
                if (y < 0)
                    return deltas;
                tile1 = scene.getTile(x, y);
                tile2 = scene.getTile(x + object.getSize() - 1, y);
                if (tile1.isBlocked() || tile2.isBlocked())
                    return deltas;
                break;
            case DOWN:
                y += v;
                if (y + object.getSize() > scene.getHeight())
                    return deltas;
                tile1 = scene.getTile(x, y + object.getSize() - 1);
                tile2 = scene.getTile(x + object.getSize() - 1, y + object.getSize() - 1);
                if (tile1.isBlocked() || tile2.isBlocked())
                    return deltas;

        }

        for (GameObject iter : scene.getAllObjects()) {
            if (iter != object && intersect(x, y, object.getSize(), iter.getPosition().x, iter.getPosition().y, iter.getSize())) {
                logger.info("Intersection! {}, ({},{}) <-> {}", object, x, y,iter);

                if (iter.getType() == GameObjectType.ROCKET && object.getType() == GameObjectType.UNIT) {
                    for (GameEventListener l : listeners) {
                        l.onRocketHit(object, iter);
                    }
                }
                if (iter.getType() == GameObjectType.UNIT && object.getType() == GameObjectType.ROCKET) {
                    for (GameEventListener l : listeners) {
                        l.onRocketHit(iter, object);
                    }
                }
                return deltas;
            }
        }

        pos.x = x;
        pos.y = y;
        deltas.add(object);
        return deltas;
    }

    private boolean isPointIncluded(int px, int py, int x, int y, int size) {
        boolean isIncluded =  ((px > x) && (px < x + size) && (py > y) && (py < y + size));
        return isIncluded;
    }
    private boolean isPointIncludedBorders(int px, int py, int x, int y, int size) {
        boolean isIncluded =  ((px >= x) && (px <= x + size) && (py >= y) && (py <= y + size));
        return isIncluded;
    }

    public boolean intersect(int x1, int y1, int size1, int x2, int y2, int size2) {
        return isPointIncluded(x1, y1, x2, y2, size2)
                || isPointIncluded(x1 + size1, y1, x2, y2, size2)
                || isPointIncluded(x1, y1 + size1, x2, y2, size2)
                || isPointIncluded(x1 + size1, y1 + size1, x2, y2, size2)
                || isPointIncludedBorders(x1 + size1 / 2, y1 + size1 / 2, x2, y2, size2);
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
