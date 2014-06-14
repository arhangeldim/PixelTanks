package arhangel.dim.pixeltank.gui;

import arhangel.dim.pixeltank.connection.ClientConnection;
import arhangel.dim.pixeltank.connection.ConnectionListener;
import arhangel.dim.pixeltank.game.Direction;
import arhangel.dim.pixeltank.game.GameObject;
import arhangel.dim.pixeltank.game.scene.Scene;
import arhangel.dim.pixeltank.messages.AckMessage;
import arhangel.dim.pixeltank.messages.DeltaMessage;
import arhangel.dim.pixeltank.messages.FireMessage;
import arhangel.dim.pixeltank.messages.LogonMessage;
import arhangel.dim.pixeltank.messages.Message;
import arhangel.dim.pixeltank.messages.MoveCommandMessage;
import arhangel.dim.pixeltank.messages.RemoveMessage;
import arhangel.dim.pixeltank.messages.SnapshotMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/****/
public class GameClient extends JFrame implements ConnectionListener {
    Logger logger = LoggerFactory.getLogger(GameClient.class);
    public static final int CANVAS_WIDTH = 600;
    public static final int CANVAS_HEIGHT = 500;
    private Scene scene;
    private DrawCanvas canvas;
    private ClientConnection clientConnection;
    private volatile boolean isLogged = false;

    public GameClient() throws Exception {
        JPanel btnPanel = new JPanel(new FlowLayout());
        JButton btnLeft = new JButton("Login");
        btnPanel.add(btnLeft);
        btnLeft.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    clientConnection.send(new LogonMessage("User"));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });// Set up a custom drawing
        canvas = new DrawCanvas();
        canvas.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());
        cp.add(canvas, BorderLayout.CENTER);
        int serverPort = 19000;
        String address = "127.0.0.1";
        clientConnection = new ClientConnection(address, serverPort);
        clientConnection.addConnectionListener(this);
        clientConnection.start();// "this" JFrame fires KeyEvent
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evt) {
                try {
                    switch (evt.getKeyCode()) {
                        case KeyEvent.VK_LEFT:
                            Message m = new MoveCommandMessage(Direction.LEFT);
                            clientConnection.send(m);//x1 -= 5;repaint();
                            break;
                        case KeyEvent.VK_RIGHT:
                            m = new MoveCommandMessage(Direction.RIGHT);
                            clientConnection.send(m);//x1 += 10;repaint();
                            break;
                        case KeyEvent.VK_DOWN:
                            m = new MoveCommandMessage(Direction.DOWN);
                            clientConnection.send(m);//y1 += 10;repaint();
                            break;
                        case KeyEvent.VK_UP:
                            m = new MoveCommandMessage(Direction.UP);
                            clientConnection.send(m);//y1 -= 10;repaint();
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
        });
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Handle the CLOSE button
        setTitle("Move a Square");
        pack(); // packTo all the components in the JFrame
        setVisible(true); // show it
        requestFocus(); // set the focus to JFrame to receive KeyEvent
    }

    @Override
    public void onMessageReceived(Message message) {
        int type = message.getType();
        switch (type) {
            case Message.MESSAGE_ACK:
                AckMessage ackMessage = (AckMessage) message;
                if (ackMessage.getStatus() == AckMessage.STATUS_SUCCESS) {
                    isLogged = true;
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
                canvas.setPreferredSize(new Dimension(scene.getWidth(), scene.getHeight()));
                repaint();
                break;
            case Message.MESSAGE_DELTA:
                if (!isLogged) {
                    logger.error("Client is not logged");
                    return;
                }
                DeltaMessage deltaMessage = (DeltaMessage) message;
                for (GameObject object : deltaMessage.getDeltaObjects()) {

                    // TODO: have to generate new message(added new player) instead of adding
                    // broadcast logon info about new users
                    if (!scene.updateObject(object.getId(), object)) {
                        scene.addObject(object.getId(), object);
                    }
                }
                repaint();
                break;
            case Message.MESSAGE_REMOVE:
                if (!isLogged) {
                    logger.error("Client is not logged");
                    return;
                }
                RemoveMessage rmMessage = (RemoveMessage) message;
                for (Integer it : rmMessage.getObjectIds()) {
                    scene.removeObject(it);
                }
                repaint();
                break;
            default:
        }
    }

    class DrawCanvas extends JPanel {
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (scene != null) {
                scene.paint(this, g);
            }
        }
    }

    public static void main(String[] args) {// Run GUI codes on the Event-Dispatcher Thread for thread safety
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    new GameClient(); // Let the constructor do the job
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
