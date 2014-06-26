package arhangel.dim.pixeltank.gui;

import arhangel.dim.pixeltank.GameClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

/**
 *
 */
public class LogonFrame extends JFrame {
    Logger logger = LoggerFactory.getLogger(LogonFrame.class);
    private static final int CANVAS_WIDTH = 200;
    private static final int CANVAS_HEIGHT = 50;

    private GameClient client;

    public LogonFrame(GameClient client) {
        this.client = client;
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));

        JTextField hostField = new JTextField("127.0.0.1");
        JTextField portField = new JTextField("19000");
        JTextField loginField = new JTextField("User");

        JButton connectButton = new JButton("Connect");
        connectButton.addActionListener((e) -> {
                    String host = hostField.getText();
                    String port = portField.getText();
                    String login = loginField.getText();
                    logger.info("Connection {}:{} user={}", host, port, login);
                    client.login(host, port, login);
                }
        );
        panel.add(hostField);
        panel.add(portField);
        panel.add(loginField);
        panel.add(connectButton);

        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());
        cp.add(panel, BorderLayout.CENTER);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("PixelTanks");
        pack();
        setVisible(true);
        requestFocus();
    }
}
