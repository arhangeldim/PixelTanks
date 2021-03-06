package arhangel.dim.pixeltank.connection;

import arhangel.dim.pixeltank.game.Player;
import arhangel.dim.pixeltank.messages.Message;
import arhangel.dim.pixeltank.protocol.MessageDecodingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/****/
public class ClientConnectionHandler implements GameConnection {
    private static Logger logger = LoggerFactory.getLogger(ClientConnectionHandler.class);
    private Socket socket;
    private Player player;
    private GameServer server;
    private Thread worker;
    private DataInputStream in;
    private DataOutputStream out;
    private List<ConnectionListener> listeners;

    public ClientConnectionHandler(final GameServer server, Socket socket, final Player player) throws IOException {
        logger.info("Creating handler for client: {}", player);
        this.server = server;
        this.socket = socket;
        this.player = player;
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
        listeners = new ArrayList<>();
        worker = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (!Thread.currentThread().isInterrupted()) {
                        try {
                            Message message = server.getProtocol().decode(in);
                            message.setPlayer(player);
                            for (ConnectionListener listener : listeners) {
                                listener.onMessageReceived(message);
                            }
                        } catch (MessageDecodingException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    logger.error("Failed to read from socket.");
                } finally {
                    server.removeHandler(player);
//                    interrupt();
                }
            }
        });
    }

    @Override
    public void send(Message message) {
        try {
            server.getProtocol().encode(out, message);
            out.flush();
        } catch (IOException e) {
            System.out.println("Failed to write to socket. " + this);
            closeResources();
            server.removeHandler(player);//interrupt();
        }
    }

    @Override
    public void addConnectionListener(ConnectionListener listener) {
        listeners.add(listener);
    }

    private void closeResources() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            logger.error("Failed to close resources.");
        }
    }

    @Override
    public void start() throws IOException {
        if (worker != null) {
            worker.start();
        }
    }

    @Override
    public void stop() {
        if (worker != null) {
            worker.interrupt();
        }
    }

    public String toString() {
        return "Handler [player= " + player + "];";
    }
}
