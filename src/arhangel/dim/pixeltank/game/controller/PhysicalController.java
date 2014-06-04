package arhangel.dim.pixeltank.game.controller;

import arhangel.dim.pixeltank.game.Direction;
import arhangel.dim.pixeltank.game.GameObject;
import arhangel.dim.pixeltank.game.Position;
import arhangel.dim.pixeltank.game.Scene;
import arhangel.dim.pixeltank.game.Tile;
import arhangel.dim.pixeltank.game.Unit;
import arhangel.dim.pixeltank.messages.DeltaMessage;
import arhangel.dim.pixeltank.messages.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class PhysicalController {
    private static final Logger logger = LoggerFactory.getLogger(PhysicalController.class);
    private Scene scene;
    private List<Unit> deltas;


    public Scene getScene() {
        return scene;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public Message handle(GameObject object) {

        deltas = new ArrayList<>();
        detectCollision(object);
        logger.info("Collision: " + deltas);
        if (deltas.isEmpty()) {
            return null;
        }
        DeltaMessage msg = new DeltaMessage();
        for (Unit u : deltas) {
            msg.addUnit(u);
        }
        return msg;
    }

    public void detectCollision(GameObject object) {
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
                    return;
                tile1 = scene.getTile(x, y);
                tile2 = scene.getTile(x, y + object.getSize());
                if (tile1.isBlocked() || tile2.isBlocked())
                    return;
                break;
            case RIGHT:
                x += v;
                if (x + object.getSize() > scene.getWidth())
                    return;
                tile1 = scene.getTile(x + object.getSize() - 1, y);
                tile2 = scene.getTile(x + object.getSize() - 1, y + object.getSize());
                if (tile1.isBlocked() || tile2.isBlocked())
                    return;
                break;
            case UP:
                y -= v;
                if (y < 0)
                    return;
                tile1 = scene.getTile(x, y);
                tile2 = scene.getTile(x + object.getSize(), y);
                if (tile1.isBlocked() || tile2.isBlocked())
                    return;
                break;
            case DOWN:
                y += v;
                if (y + object.getSize() > scene.getHeight())
                    return;
                tile1 = scene.getTile(x, y + object.getSize() - 1);
                tile2 = scene.getTile(x + object.getSize(), y + object.getSize() - 1);
                if (tile1.isBlocked() || tile2.isBlocked())
                    return;

        }

        pos.x = x;
        pos.y = y;

        deltas.add((Unit) object);
    }
}
