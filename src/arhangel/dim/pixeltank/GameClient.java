package arhangel.dim.pixeltank;

import arhangel.dim.pixeltank.connection.ClientConnection;
import arhangel.dim.pixeltank.connection.ConnectionListener;
import arhangel.dim.pixeltank.game.Direction;
import arhangel.dim.pixeltank.game.GameObject;
import arhangel.dim.pixeltank.game.scene.Scene;
import arhangel.dim.pixeltank.gui.GameFrame;
import arhangel.dim.pixeltank.gui.LogonFrame;
import arhangel.dim.pixeltank.messages.AckMessage;
import arhangel.dim.pixeltank.messages.DeltaMessage;
import arhangel.dim.pixeltank.messages.FireMessage;
import arhangel.dim.pixeltank.messages.LifecycleMessage;
import arhangel.dim.pixeltank.messages.LogonMessage;
import arhangel.dim.pixeltank.messages.Message;
import arhangel.dim.pixeltank.messages.MoveCommandMessage;
import arhangel.dim.pixeltank.messages.RemoveMessage;
import arhangel.dim.pixeltank.messages.SnapshotMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;

/****/
public class GameClient implements ConnectionListener {
    Logger logger = LoggerFactory.getLogger(GameClient.class);
    private Scene scene;
    private ClientConnection clientConnection;
    private volatile boolean isLogged = false;

    private JFrame loginFrame;
    private GameFrame gameFrame;

    public GameClient() {
        loginFrame = new LogonFrame(this);
    }

    public void handleInput(int keyCode) {
        try {
            switch (keyCode) {
                case KeyEvent.VK_LEFT:
                    Message m = new MoveCommandMessage(Direction.LEFT);
                    clientConnection.send(m);
                    break;
                case KeyEvent.VK_RIGHT:
                    m = new MoveCommandMessage(Direction.RIGHT);
                    clientConnection.send(m);
                    break;
                case KeyEvent.VK_DOWN:
                    m = new MoveCommandMessage(Direction.DOWN);
                    clientConnection.send(m);
                    break;
                case KeyEvent.VK_UP:
                    m = new MoveCommandMessage(Direction.UP);
                    clientConnection.send(m);
                    break;
                case KeyEvent.VK_SPACE:
                    m = new FireMessage();
                    clientConnection.send(m);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public Scene getScene() {
        return scene;
    }

    public void login(String host, String port, String login) {
        try {
            clientConnection = new ClientConnection(host, Integer.valueOf(port));
            clientConnection.addConnectionListener(this);
            clientConnection.start();
            clientConnection.send(new LogonMessage(login));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createScene() {
        logger.info("Creating scene...");
        loginFrame.setVisible(false);
        gameFrame = new GameFrame(this);

    }

    @Override
    public void onMessageReceived(Message message) {
        int type = message.getType();
        switch (type) {
            case Message.MESSAGE_ACK:
                AckMessage ackMessage = (AckMessage) message;
                if (ackMessage.getStatus() == AckMessage.STATUS_SUCCESS) {
                    isLogged = true;
                    createScene();
                } else {
                    logger.error("Failed to logon");
                }
                break;
            case Message.MESSAGE_SNAPSHOT:
                if (!isLogged) {
                    logger.error("Client is not logged");
                    return;
                }
                SnapshotMessage snapshotMessage = (SnapshotMessage) message;
                scene = snapshotMessage.getScene();
                gameFrame.getCanvas().setPreferredSize(new Dimension(scene.getWidth(), scene.getHeight()));
                gameFrame.getCanvas().repaint();
                break;
            case Message.MESSAGE_DELTA:
                if (!isLogged) {
                    logger.error("Client is not logged");
                    return;
                }
                DeltaMessage deltaMessage = (DeltaMessage) message;
                for (GameObject object : deltaMessage.getDeltaObjects()) {
                    scene.updateObject(object.getId(), object);
                }
                gameFrame.getCanvas().repaint();
                break;
            case Message.MESSAGE_REMOVE:
                if (!isLogged) {
                    logger.error("Client is not logged");
                    return;
                }
                RemoveMessage rmMessage = (RemoveMessage) message;
                for (Integer it : rmMessage.getObjectIds()) {
                    logger.info("Removed an object: {}", scene.getObject(it));
                    scene.removeObject(it);
                }
                gameFrame.getCanvas().repaint();
                break;
            case Message.MESSAGE_LIFECYCLE:
                if (!isLogged) {
                    logger.error("Client is not logged");
                    return;
                }
                LifecycleMessage lifecycleMessage = (LifecycleMessage) message;
                GameObject object = lifecycleMessage.getObject();
                logger.info("Spawned an object {} by player {}", object, lifecycleMessage.getPlayer().getName());
                scene.addObject(object.getId(), object);
                gameFrame.getCanvas().repaint();
                break;
            default:
        }
    }

    public static void main(String[] args) {
        new GameClient();
    }
}
