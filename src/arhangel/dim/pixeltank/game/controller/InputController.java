package arhangel.dim.pixeltank.game.controller;

import arhangel.dim.pixeltank.connection.GameServer;
import arhangel.dim.pixeltank.game.GameObject;
import arhangel.dim.pixeltank.game.GameObjectType;
import arhangel.dim.pixeltank.game.Unit;
import arhangel.dim.pixeltank.game.scene.Position;
import arhangel.dim.pixeltank.game.scene.Scene;
import arhangel.dim.pixeltank.messages.Message;
import arhangel.dim.pixeltank.messages.MoveCommandMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 */
public class InputController {
    private static Logger logger = LoggerFactory.getLogger(InputController.class);
    private Scene scene;
    private GameServer server;
    private PhysicalController physicalController;
    private AtomicInteger idCounter = new AtomicInteger(-1);

    public Scene getScene() {
        return scene;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public PhysicalController getPhysicalController() {
        return physicalController;
    }

    public void setPhysicalController(PhysicalController physicalController) {
        this.physicalController = physicalController;
    }

    public GameServer getServer() {
        return server;
    }

    public void setServer(GameServer server) {
        this.server = server;
    }

    public Message hadleInput(Message message) {
        int senderId = message.getSenderId();
        int type = message.getType();
        switch (type) {
            case Message.MESSAGE_CMD_MOVE:
                MoveCommandMessage moveCmdMessage = (MoveCommandMessage) message;
                GameObject unit = scene.getUnit(senderId);
                if (unit == null) {
                    logger.warn("Unknown unit: " + senderId);
                    return null;
                }
                unit.setDirection(moveCmdMessage.getDirection());
                return physicalController.handle(unit);
            case Message.MESSAGE_FIRE:
                Unit owner = scene.getUnit(senderId);
                logger.info("Fire on dir: {}", owner.getDirection());
                GameObject bullet = createBullet(owner);
                scene.addUnit((Unit) bullet);
                new Thread(new BulletTrace(bullet)).start();

            default:


        }
        return null;
    }

    private GameObject createBullet(Unit owner) {
        GameObject bullet = new Unit();
        bullet.setType(GameObjectType.ROCKET);
        bullet.setPosition(new Position(owner.getPosition().x, owner.getPosition().y));
        bullet.setDirection(owner.getDirection());
        bullet.setVelocity(10);
        bullet.setSize(5);
        bullet.setId(idCounter.getAndDecrement());
        return bullet;
    }


    class BulletTrace implements Runnable {

        private GameObject bullet;

        public BulletTrace(GameObject bullet) {
            this.bullet = bullet;
        }

        private void removeBullet() {
            logger.info("Remove bullet from scene: {}", bullet);
            scene.removeUnit(bullet.getId());
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
                    Message msg = physicalController.handle(bullet);
                    if (msg != null) {
                        server.broadcast(msg);
                    } else {
                        logger.info("No delta message. ");
                        removeBullet();
                        return;
                    }

                    TimeUnit.MILLISECONDS.sleep(500);
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

}
