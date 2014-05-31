package arhangel.dim.pixeltank;

import arhangel.dim.pixeltank.game.Scene;
import arhangel.dim.pixeltank.game.Unit;
import arhangel.dim.pixeltank.game.UnitCommand;
import arhangel.dim.pixeltank.messages.AckMessage;
import arhangel.dim.pixeltank.messages.DeltaMessage;
import arhangel.dim.pixeltank.messages.LogonMessage;
import arhangel.dim.pixeltank.messages.Message;
import arhangel.dim.pixeltank.messages.MoveCommandMessage;
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
public class MoveSquareFrame extends JFrame implements ConnectionListener {
    Logger logger = LoggerFactory.getLogger(MoveSquareFrame.class);
    public static final int CANVAS_WIDTH = 600;
    public static final int CANVAS_HEIGHT = 500;
    public static final Color LINE_COLOR = Color.BLACK;
    public static final Color CANVAS_BACKGROUND = Color.WHITE;
    private int x1 = CANVAS_WIDTH / 2;
    private int y1 = CANVAS_HEIGHT / 2;
    private int width = 20;
    private int height = 20;
    private Scene scene;
    private DrawCanvas canvas;
    private ClientConnection clientConnection;
    private volatile boolean isLogged = false;

    public MoveSquareFrame() throws Exception {
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
        JPanel canvas = new DrawCanvas();
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
                            Message m = new MoveCommandMessage(UnitCommand.MOVE_LEFT);
                            clientConnection.send(m);//x1 -= 5;repaint();
                            break;
                        case KeyEvent.VK_RIGHT:
                            m = new MoveCommandMessage(UnitCommand.MOVE_RIGHT);
                            clientConnection.send(m);//x1 += 10;repaint();
                            break;
                        case KeyEvent.VK_DOWN:
                            m = new MoveCommandMessage(UnitCommand.MOVE_DOWN);
                            clientConnection.send(m);//y1 += 10;repaint();
                            break;
                        case KeyEvent.VK_UP:
                            m = new MoveCommandMessage(UnitCommand.MOVE_UP);
                            clientConnection.send(m);//y1 -= 10;repaint();
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
                repaint();
                break;
            case Message.MESSAGE_DELTA:
                if (!isLogged) {
                    logger.error("Client is not logged");
                    return;
                }
                DeltaMessage deltaMessage = (DeltaMessage) message;
                Unit delta = (Unit) deltaMessage.getUnits().toArray()[0];
                scene.updateUnit(delta.getId(), delta);
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
                    new MoveSquareFrame(); // Let the constructor do the job
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
