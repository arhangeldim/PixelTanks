package arhangel.dim.pixeltank;

import arhangel.dim.pixeltank.game.Scene;
import arhangel.dim.pixeltank.game.Unit;
import arhangel.dim.pixeltank.messages.AckMessage;
import arhangel.dim.pixeltank.messages.DeltaMessage;
import arhangel.dim.pixeltank.messages.Message;
import arhangel.dim.pixeltank.messages.MoveCommandMessage;
import arhangel.dim.pixeltank.messages.SnapshotMessage;
import arhangel.dim.pixeltank.protocol.ClientConnectionHandler;
import arhangel.dim.pixeltank.protocol.Protocol;
import arhangel.dim.pixeltank.protocol.SimpleProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/****/
public class GameServer implements ConnectionListener {
    private static Logger logger = LoggerFactory.getLogger(GameServer.class);
    private int port;
    private Protocol protocol;
    private ServerSocket serverSocket;
    private AtomicInteger idHolder = new AtomicInteger(10);
    private Map<Integer, GameConnection> handlers = new HashMap<>();
    private volatile boolean isRunning = false;
    private Scene scene;

    public GameServer(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        isRunning = true;
        serverSocket = new ServerSocket(port);
        protocol = new SimpleProtocol();
        scene = new Scene();
        logger.info("Waiting for a client...");
        while (isRunning) {
            Socket socket = serverSocket.accept();
            int internalId = idHolder.incrementAndGet();//TODO: short vs int
            GameConnection handler = new ClientConnectionHandler(this, socket, internalId);
            handler.addConnectionListener(this);
            handlers.put(internalId, handler);
            handler.start();
        }
    }

    public void stop() {
        isRunning = false;
        for (Map.Entry<Integer, GameConnection> entry : handlers.entrySet()) {
            entry.getValue().stop();
        }
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    public void removeHandler(int internalId) {
        logger.info("Handler removed: " + internalId);
        GameConnection handler = handlers.remove(internalId);
        if (handler != null) {
            handler.stop();
        }
    }

    public void broadcast(Message message) throws IOException {
        for (Map.Entry<Integer, GameConnection> entry : handlers.entrySet()) {
            entry.getValue().send(message);
        }
    }

    @Override
    public void onMessageReceived(Message message) {
        try {
            int senderId = message.getSenderId();
            int type = message.getType();
            GameConnection conn;
            switch (type) {
                case Message.MESSAGE_LOGON:
                    conn = handlers.get(senderId);
                    if (scene.generateUnit(senderId) != null) {
                        conn.send(new AckMessage(AckMessage.STATUS_SUCCESS));
                        conn.send(new SnapshotMessage(scene));
                    } else {
                        conn.send(new AckMessage(AckMessage.STATUS_FAILED));
                    }
                    break;
                case Message.MESSAGE_CMD_MOVE:
                    MoveCommandMessage moveCmdMessage = (MoveCommandMessage) message;
                    Unit unit = scene.getUnit(senderId);
                    if (unit == null) {
                        logger.warn("Unknown unit: " + senderId);
                        return;
                    }
                    if (unit.execute(moveCmdMessage.getCommand())) {
                        DeltaMessage delta = new DeltaMessage();
                        delta.addUnit(unit);
                        broadcast(delta);
                    }
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        int port = 19000;
        GameServer server = null;
        try {
            server = new GameServer(port);
            server.start();

        } catch (IOException e) {
            logger.error("Server failed: {}\nShutting down.", e.getMessage());
            if (server != null) {
                server.stop();
            }
        }
    }
}
