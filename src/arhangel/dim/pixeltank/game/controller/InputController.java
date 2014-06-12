package arhangel.dim.pixeltank.game.controller;

import arhangel.dim.pixeltank.connection.GameServer;
import arhangel.dim.pixeltank.game.GameObject;
import arhangel.dim.pixeltank.game.RocketFactory;
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
    private RocketFactory rocketFactory;

    public RocketFactory getRocketFactory() {
        return rocketFactory;
    }

    public void setRocketFactory(RocketFactory rocketFactory) {
        this.rocketFactory = rocketFactory;
    }

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
                GameObject unit = scene.getObject(senderId);
                if (unit == null) {
                    logger.warn("Unknown unit: " + senderId);
                    return null;
                }
                unit.setDirection(moveCmdMessage.getDirection());
                return physicalController.handle(unit);
            case Message.MESSAGE_FIRE:
                GameObject owner = scene.getObject(senderId);
                logger.info("Fire on dir: {}", owner.getDirection());
                GameObject bullet = rocketFactory.create(owner);
                scene.addObject(bullet.getId(), bullet);
                new Thread(new BulletTrace(bullet)).start();

            default:


        }
        return null;
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
