package arhangel.dim.pixeltank.game;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 */
public class SceneGenerator {

    public static TiledScene generate(int tiledWidth, int tiledHeight, int tileSize) {
        TiledScene tiledScene = new TiledScene(tiledWidth, tiledHeight, tileSize);

        for (int i = 0; i < tiledWidth; i++) {
            for (int j = 0; j < tiledHeight; j++) {
                tiledScene.tiles[i][j] = new Tile(tileSize, i, j, false);
            }
        }

        for (int i = 3; i < 7; i++) {
            tiledScene.tiles[i][4].isBlocked = true;

        }


        return tiledScene;
    }


}

class TiledScene {
    private static final Logger logger = LoggerFactory.getLogger(TiledScene.class);

    int width, height;
    int tileSize;
    int tiledWidth, tiledHeight;
    Tile[][] tiles;

    TiledScene(int tiledWidth, int tiledHeight, int tileSize) {
        this.width = tileSize * tiledWidth;
        this.height = tileSize * tiledHeight;
        this.tileSize = tileSize;
        this.tiledWidth = tiledWidth;
        this.tiledHeight = tiledHeight;
        tiles = new Tile[tiledWidth][tiledHeight];
    }

    public Tile getTile(int x, int y) {
        logger.info("{}, {}", x, y);
        int xPos = (x + 1) / tileSize;
        int yPos = (y + 1) / tileSize;
        Tile tile = tiles[xPos][yPos];
        logger.info("({}, {}) -> {}", x, y, tile);
        return tile;
    }

    public void render(Component canvas, Graphics g) {
        for (int i = 0; i < tiledWidth; i++) {
            for (int j = 0; j < tiledHeight; j++) {
                g.setColor((i + j) % 2 == 0 ? Color.BLUE : Color.BLACK);
                Tile t = tiles[i][j];
                g.setColor(t.isBlocked ? Color.BLACK : Color.BLUE);
                g.fillRect(t.x * tileSize, t.y * tileSize, t.tileSize, t.tileSize);
            }
        }
    }


}

class Tile {
    int tileSize;
    int x, y;
    boolean isBlocked;

    public Tile(int tileSize, int x, int y, boolean isBlocked) {
        this.tileSize = tileSize;
        this.x = x;
        this.y = y;
        this.isBlocked = isBlocked;
    }
}


class Test extends JFrame {

    private TiledScene scene;
    private Rectangle unit;

    enum Direction {
        LEFT,
        RIGHT,
        UP,
        DOWN,
    }

    public Test() {
        scene = SceneGenerator.generate(10, 10, 50);
        JPanel canvas = new DrawCanvas();
        canvas.setPreferredSize(new Dimension(500, 500));
        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());
        cp.add(canvas, BorderLayout.CENTER);
        unit = new Rectangle(0, 0, 50, 50);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evt) {
                try {
                    switch (evt.getKeyCode()) {
                        case KeyEvent.VK_LEFT:
                            check(Direction.LEFT);
                            repaint();
                            break;
                        case KeyEvent.VK_RIGHT:
                            check(Direction.RIGHT);
                            repaint();
                            break;
                        case KeyEvent.VK_DOWN:
                            check(Direction.DOWN);
                            repaint();
                            break;
                        case KeyEvent.VK_UP:
                            check(Direction.UP);
                            repaint();
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Handle the CLOSE button
        setTitle("Scene generator");
        pack();
        setVisible(true);
        requestFocus();
    }

    private boolean check(Direction dir) {
        int v = 10;
        int x = unit.x;
        int y = unit.y;
        int w = unit.width;
        int h = unit.height;
        switch (dir) {
            case LEFT:
                Tile tile = scene.getTile(x - v, y);
                if (!tile.isBlocked) {
                    unit.x -= v;
                }
                break;
            case RIGHT:
                tile = scene.getTile(x + w + v, y);
                if (!tile.isBlocked) {
                    unit.x += v;
                }
                break;
            case UP:
                tile = scene.getTile(x, y - v);
                if (!tile.isBlocked) {
                    unit.y -= v;
                }
                break;
            case DOWN:
                tile = scene.getTile(x, y + w + v);
                if (!tile.isBlocked) {
                    unit.y += v;
                }
                break;
        }


        return false;
    }

    public static void main(String[] args) {
        JFrame f = new Test();
    }

    class DrawCanvas extends JPanel {
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            scene.render(this, g);
            g.setColor(Color.RED);
            ((Graphics2D) g).fill(unit);
        }
    }

}

class BST {

    private static final Logger logger = LoggerFactory.getLogger(BST.class);

    class Leaf {
        int x, y, width, height;
        Leaf left, right;

        Leaf(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public Leaf getLeft() {
            return left;
        }

        public void setLeft(Leaf left) {
            this.left = left;
        }

        public Leaf getRight() {
            return right;
        }

        public void setRight(Leaf right) {
            this.right = right;
        }

        @Override
        public String toString() {
            return "Leaf{" +
                    "x=" + x +
                    ", y=" + y +
                    ", width=" + width +
                    ", height=" + height +
                    '}';
        }
    }

    public static final int MIN_LEAF_SIZE = 5;
    private List<Leaf> leafs;
    private Random random = new Random();
    private int depth = 3;

    public BST() {
        leafs = new ArrayList<>();

    }

    public void split(int[][] array) {
        Leaf leaf = new Leaf(0, 0, array.length, array[0].length);
        doSplit(leaf, true);
    }

    private void doSplit(Leaf leaf, boolean isVSplit) {
        if ((isVSplit && (leaf.width <= MIN_LEAF_SIZE)) || (!isVSplit && (leaf.height <= MIN_LEAF_SIZE))) {
            logger.info("Added leaf: {}", leaf);
            leafs.add(leaf);
            return;
        }
        int r = random.nextInt(3) % 2 + 2; // 1, 2
        if (isVSplit) {
            int width = leaf.width / r;
            Leaf left = new Leaf(leaf.x, leaf.y, width, leaf.height);
            Leaf right = new Leaf(leaf.x + width, leaf.y, leaf.width - width, leaf.height);
            logger.info("V, r={}, P={}, l={}, r={}", r, leaf, left, right);
            doSplit(left, false);
            doSplit(right, false);
        } else {
            int height = leaf.height / r;
            Leaf top = new Leaf(leaf.x, leaf.y, leaf.width, height);
            Leaf bottom = new Leaf(leaf.x, leaf.y + height, leaf.width, leaf.height - height);
            logger.info("H, r={}, P={}, t={}, b={}", r, leaf, top, bottom);
            doSplit(top, true);
            doSplit(bottom, true);
        }
    }

    public static void main(String[] args) {
        BST bst = new BST();
        int[][] array = new int[10][10];
        bst.split(array);
        logger.info("leafs {}", bst.leafs);
    }
}
