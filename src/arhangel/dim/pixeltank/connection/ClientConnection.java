package arhangel.dim.pixeltank.connection;

import arhangel.dim.pixeltank.messages.LogonMessage;
import arhangel.dim.pixeltank.messages.Message;
import arhangel.dim.pixeltank.protocol.MessageDecodingException;
import arhangel.dim.pixeltank.protocol.Protocol;
import arhangel.dim.pixeltank.protocol.SimpleProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/****/
public class ClientConnection implements GameConnection {
    Logger logger = LoggerFactory.getLogger(ClientConnection.class);
    private Socket socket;
    private ClientThread connectionThread;
    private List<ConnectionListener> listeners;
    private Protocol protocol;
    private DataInputStream in;
    private DataOutputStream out;

    public ClientConnection(String address, int port) throws UnknownHostException, IOException {
        InetAddress ipAddress = InetAddress.getByName(address);
        logger.info("Connecting to {}:{}", address, port);
        socket = new Socket(ipAddress, port);
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
        listeners = new ArrayList<>();
        protocol = new SimpleProtocol();
    }

    public void start() throws IOException {
        connectionThread = new ClientThread();
        connectionThread.setName("Client-connection handler");
        connectionThread.start();//TODO: test
        send(new LogonMessage("Dima"));
    }

    public void stop() {
        if (connectionThread != null) {
            connectionThread.interrupt();
        }
    }

    @Override

    public void addConnectionListener(ConnectionListener listener) {
        listeners.add(listener);
    }

    @Override
    public void send(Message message) throws IOException {
        try {
            protocol.encode(out, message);
        } catch (IOException e) {
            logger.error("Shutdown. Failed to send: {}", message);
            closeResources();
            stop();
        }
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

    private class ClientThread extends Thread {
        @Override
        public void run() {
            try {
                while (!isInterrupted()) {
                    try {
                        Message message = protocol.decode(in);
                        for (ConnectionListener listener : listeners) {
                            listener.onMessageReceived(message);
                        }
                    } catch (MessageDecodingException e) {
                        logger.error("Failed to process input message: {}", e.getMessage());
                    }
                }
            } catch (IOException e) {
                logger.error("Shutdown. Failed to read from socket.");
                closeResources();
                interrupt();
            }
        }
    }
}
