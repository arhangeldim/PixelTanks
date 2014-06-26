package arhangel.dim.pixeltank.gui;

import arhangel.dim.pixeltank.GameClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 *
 */
public class GameFrame extends JFrame {
    Logger logger = LoggerFactory.getLogger(GameFrame.class);
    public static final int CANVAS_WIDTH = 600;
    public static final int CANVAS_HEIGHT = 500;
    private DrawCanvas canvas;
    private GameClient client;

    public GameFrame(GameClient client) {
        this.client = client;
        canvas = new DrawCanvas();
        canvas.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());
        cp.add(canvas, BorderLayout.CENTER);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evt) {
                client.handleInput(evt.getKeyCode());
            }
        });
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("PixelTanks");
        pack();
        setVisible(true);
        requestFocus();
    }

    public JPanel getCanvas() {
        return canvas;
    }

    class DrawCanvas extends JPanel {
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (client.getScene() != null) {
                client.getScene().paint(this, g);
            }
        }
    }
}
