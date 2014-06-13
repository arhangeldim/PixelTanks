package arhangel.dim.pixeltank.game.controller;

import arhangel.dim.pixeltank.connection.ConnectionListener;
import arhangel.dim.pixeltank.connection.GameServer;
import arhangel.dim.pixeltank.game.GameObject;
import arhangel.dim.pixeltank.game.Player;
import arhangel.dim.pixeltank.game.RocketFactory;
import arhangel.dim.pixeltank.game.TankFactory;
import arhangel.dim.pixeltank.game.scene.Position;
import arhangel.dim.pixeltank.game.scene.Scene;
import arhangel.dim.pixeltank.messages.AckMessage;
import arhangel.dim.pixeltank.messages.Message;
import arhangel.dim.pixeltank.messages.MoveCommandMessage;
import arhangel.dim.pixeltank.messages.SnapshotMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 */
public class InputController implements ConnectionListener {
    private static Logger logger = LoggerFactory.getLogger(InputController.class);
    private Scene scene;
    private GameServer server;
    private PhysicalController physicalController;
    private RocketFactory rocketFactory;
    private TankFactory tankFactory;

    public void hadleInput(Player player, Message message) {
        int type = message.getType();
        switch (type) {
            case Message.MESSAGE_CMD_MOVE:
                MoveCommandMessage moveCmdMessage = (MoveCommandMessage) message;
                GameObject unit = scene.getObject(player.getId());
                if (unit == null) {
                    logger.warn("Unknown player: {}", player);
                    return;
                }
                unit.setDirection(moveCmdMessage.getDirection());
                try {
                    server.broadcast(physicalController.handle(unit));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            case Message.MESSAGE_FIRE:
                GameObject owner = scene.getObject(player.getId());
                logger.info("Fire on dir: {}", owner.getDirection());
                GameObject bullet = rocketFactory.create(owner);
                scene.addObject(bullet.getId(), bullet);
                new Thread(new BulletTrace(bullet)).start();

            default:
                throw new RuntimeException("Unknown command: " + type);
        }
    }

    @Override
    public void onMessageReceived(Message message) {
        try {
            int type = message.getType();
            Player player = message.getPlayer();
            switch (type) {
                case Message.MESSAGE_LOGON:
                    if (player == null) {
                        throw new RuntimeException("Unexpected value");
                    }
                    if (tankFactory.create(player) != null) {
                        server.sendTo(player, new AckMessage(AckMessage.STATUS_SUCCESS));
                        server.sendTo(player, new SnapshotMessage(scene));
                    } else {
                        server.sendTo(player, new AckMessage(AckMessage.STATUS_FAILED));
                    }
                    break;
                case Message.MESSAGE_CMD_MOVE:
                case Message.MESSAGE_FIRE:
                    hadleInput(player, message);
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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


    public void setTankFactory(TankFactory tankFactory) {
        this.tankFactory = tankFactory;
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

    public void setPhysicalController(PhysicalController physicalController) {
        this.physicalController = physicalController;
    }

    public void setServer(GameServer server) {
        this.server = server;
    }

}
