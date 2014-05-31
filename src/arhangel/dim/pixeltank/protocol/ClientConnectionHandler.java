package arhangel.dim.pixeltank.protocol;

import arhangel.dim.pixeltank.ConnectionListener;
import arhangel.dim.pixeltank.GameConnection;
import arhangel.dim.pixeltank.GameServer;
import arhangel.dim.pixeltank.messages.Message;
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
    private int clientlId;
    private GameServer server;
    private Thread worker;
    private DataInputStream in;
    private DataOutputStream out;
    private List<ConnectionListener> listeners;

    public ClientConnectionHandler(final GameServer server, Socket socket, int clientId) throws IOException {
        logger.info("Creating handler for client: {}", clientId);
        this.server = server;
        this.socket = socket;
        this.clientlId = clientId;
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
                            message.setSenderId(clientlId);
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
                    server.removeHandler(clientlId);
                    //interrupt();
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
            server.removeHandler(clientlId);//interrupt();
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
        return "Handler [id= " + clientlId + "];";
    }
}
