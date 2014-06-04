package arhangel.dim.pixeltank.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 */
public class BST {

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
