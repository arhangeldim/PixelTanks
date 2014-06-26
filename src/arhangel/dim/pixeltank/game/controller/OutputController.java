package arhangel.dim.pixeltank.game.controller;

import arhangel.dim.pixeltank.connection.GameServer;
import arhangel.dim.pixeltank.game.GameObject;
import arhangel.dim.pixeltank.game.Player;
import arhangel.dim.pixeltank.game.Unit;
import arhangel.dim.pixeltank.game.scene.Scene;
import arhangel.dim.pixeltank.messages.AckMessage;
import arhangel.dim.pixeltank.messages.DeltaMessage;
import arhangel.dim.pixeltank.messages.LifecycleMessage;
import arhangel.dim.pixeltank.messages.RemoveMessage;
import arhangel.dim.pixeltank.messages.SnapshotMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 *
 */
public class OutputController implements GameEventListener {

    private static Logger logger = LoggerFactory.getLogger(OutputController.class);
    private GameServer server;
    private Scene scene;

    public Scene getScene() {
        return scene;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public GameServer getServer() {
        return server;
    }

    public void setServer(GameServer server) {
        this.server = server;
    }

    @Override
    public void onMove(GameObject object) {
        try {
            DeltaMessage msg = new DeltaMessage();
            msg.addUnit((Unit) object);
            server.broadcast(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFire(Player player, GameObject object) {
        try {
            LifecycleMessage spawnMessage = new LifecycleMessage();
            spawnMessage.setStage(LifecycleMessage.SPAWNED);
            spawnMessage.setPlayer(player);
            spawnMessage.setObject(object);
            server.broadcast(spawnMessage);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLogon(Player player, GameObject unit) {
        try {
            server.sendTo(player, new AckMessage(AckMessage.STATUS_SUCCESS));
            server.sendTo(player, new SnapshotMessage(scene));

            LifecycleMessage spawnMessage = new LifecycleMessage();
            spawnMessage.setStage(LifecycleMessage.SPAWNED);
            spawnMessage.setPlayer(player);
            spawnMessage.setObject(unit);
            server.broadcast(spawnMessage);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLogout(Player player) {
        try {
            server.sendTo(player, new AckMessage(AckMessage.STATUS_FAILED));
            logger.warn("Failed to log on {}", player);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRocketHit(GameObject victim, GameObject rocket) {
        try {
            logger.info("Player {} was killed by player {}", victim.getPlayer(), rocket.getPlayer());
            RemoveMessage msg = new RemoveMessage();
            msg.addObjectId(victim.getId());
            msg.addObjectId(rocket.getId());

            server.broadcast(msg);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
