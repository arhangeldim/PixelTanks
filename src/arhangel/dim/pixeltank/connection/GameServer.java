package arhangel.dim.pixeltank.connection;

import arhangel.dim.pixeltank.game.Player;
import arhangel.dim.pixeltank.messages.Message;
import arhangel.dim.pixeltank.protocol.Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/****/
public class GameServer {
    private static Logger logger = LoggerFactory.getLogger(GameServer.class);
    private int port;
    private ServerSocket serverSocket;
    private AtomicInteger idHolder = new AtomicInteger(0);
    private Map<Player, GameConnection> handlers = new HashMap<>();
    private volatile boolean isRunning = false;
    private ConnectionListener clientListener;
    private Protocol protocol;

    public GameServer(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        isRunning = true;
        serverSocket = new ServerSocket(port);
        logger.info("Waiting for a client...");
        while (isRunning) {
            Socket socket = serverSocket.accept();
            int internalId = idHolder.incrementAndGet();
            Player player = new Player(internalId);
            GameConnection handler = new ClientConnectionHandler(this, socket, player);
            handler.addConnectionListener(clientListener);
            handlers.put(player, handler);
            handler.start();
        }
    }

    public void stop() {
        isRunning = false;
        for (Map.Entry<Player, GameConnection> entry : handlers.entrySet()) {
            entry.getValue().stop();
        }
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            logger.error("Failed to close server socket");
        }
    }

    public void setClientListener(ConnectionListener clientListener) {
        this.clientListener = clientListener;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    public void removeHandler(Player player) {
        logger.info("Removing player: " + player);
        if (handlers.containsKey(player)) {
            GameConnection handler = handlers.remove(player);
            if (handler != null) {
                handler.stop();
            }
        } else {
            logger.warn("Cannot remove unknown player {}", player);
        }
    }

    public void sendTo(Player player, Message message) throws IOException {
        if (message == null)
            throw new RuntimeException("Invalid message");
        if (handlers.containsKey(player)) {
            GameConnection conn = handlers.get(player);
            conn.send(message);
        } else {
            throw new RuntimeException("Unknown player: " + player);
        }
    }

    public void broadcast(Message message) throws IOException {
        if (message == null)
            throw new RuntimeException("Invalid message");
        logger.info("Broadcast: {}", message);
        for (Map.Entry<Player, GameConnection> entry : handlers.entrySet()) {
            entry.getValue().send(message);
        }
    }
}
