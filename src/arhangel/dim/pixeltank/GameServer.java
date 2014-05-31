package arhangel.dim.pixeltank;

import arhangel.dim.pixeltank.game.Scene;
import arhangel.dim.pixeltank.game.Unit;
import arhangel.dim.pixeltank.messages.AckMessage;
import arhangel.dim.pixeltank.messages.DeltaMessage;
import arhangel.dim.pixeltank.messages.Message;
import arhangel.dim.pixeltank.messages.MoveCommandMessage;
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

    @Override
    public void onMessageReceived(Message message) {
        try {
            int senderId = message.getSenderId();
            logger.info("Handle message from {}: {}", senderId, message);
            int type = message.getType();
            GameConnection conn;
            switch (type) {
                case Message.MESSAGE_LOGON:
                    conn = handlers.get(senderId);
                    conn.send(new AckMessage());
                    Unit unit = new Unit();
                    unit.id = senderId;
                    unit.x = 50;
                    unit.y = 60;
                    scene.addUnit(unit);
                    break;
                case Message.MESSAGE_CMD_MOVE:// TODO: broadcast
                    conn = handlers.get(senderId);
                    MoveCommandMessage moveCmd = (MoveCommandMessage) message;
                    int dir = moveCmd.getDirection();
                    unit = scene.getUnit(senderId);
                    if (unit == null) {
                        logger.warn("Unknown unit: " + senderId);
                        return;
                    }
                    switch (dir) {
                        case MoveCommandMessage.MOVE_DOWN:
                            unit.y += 5;
                            break;
                        case MoveCommandMessage.MOVE_UP:
                            unit.y -= 5;
                            break;
                        case MoveCommandMessage.MOVE_LEFT:
                            unit.x -= 5;
                            break;
                        case MoveCommandMessage.MOVE_RIGHT:
                            unit.x += 5;
                            break;
                    }
                    DeltaMessage delta = new DeltaMessage();
                    delta.addUnit(unit);
                    conn.send(delta);
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        int port = 19000;
        GameServer server = new GameServer(port);
        server.start();
    }
}
