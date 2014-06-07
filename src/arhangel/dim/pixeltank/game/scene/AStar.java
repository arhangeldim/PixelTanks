package arhangel.dim.pixeltank.game.scene;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 */
public class AStar {

    private static Logger logger = LoggerFactory.getLogger(AStar.class);

    public static final int DEFAULT_COST = 1;

    public static int manhattan(int startX, int startY, int endX, int endY) {
        return Math.abs(startX - endX) + Math.abs(startY - endY);
    }

    private static Entry removeMin(List<Entry> list) {
        if (list.isEmpty()) {
            return null;
        }
        Collections.sort(list, new Comparator<Entry>() {
            @Override
            public int compare(Entry o1, Entry o2) {
                if (o1.cost + o1.manhattan > o2.cost + o2.manhattan)
                    return 1;
                else if (o1.cost + o1.manhattan < o2.cost + o2.manhattan)
                    return -1;
                else
                    return 0;
            }
        });
        return list.remove(0);
    }

    // process only rectangles board
    // non-empty boards
    public static List<Tile> findPath(final Tile[][] board, int startX, int startY, int endX, int endY) {
        int width = board.length;
        int height = board[0].length;

        // create manhattan model
        Entry[][] model = new Entry[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Entry e = new Entry(i, j);
                e.manhattan = manhattan(i, j, endX, endY);
                e.cost = -1;
                e.isBlocked = board[i][j].isBlocked;
                model[i][j] = e;
            }
        }

        Set<Entry> opened = new HashSet<>();
        List<Entry> closed = new ArrayList<>();
        closed.add(model[startX][startY]);

        while (!closed.isEmpty()) {
            logger.info("Opened: {}", opened);
            logger.info("Closed: {}", closed);
            Entry entry = removeMin(closed);
            Entry neighbour;
            opened.add(entry);
            if (entry.x > 0) {
                neighbour = model[entry.x - 1][entry.y];
                checkNeighbour(opened, closed, entry, neighbour);
            }
            if (entry.x < width - 1) {
                neighbour = model[entry.x + 1][entry.y];
                checkNeighbour(opened, closed, entry, neighbour);
            }
            if (entry.y > 0) {
                neighbour = model[entry.x][entry.y - 1];
                checkNeighbour(opened, closed, entry, neighbour);
            }
            if (entry.y < height - 1) {
                neighbour = model[entry.x][entry.y + 1];
                checkNeighbour(opened, closed, entry, neighbour);
            }
        }

        Entry e = model[endX][endY];
        while (e.parent != null) {
            logger.info("<{}", e);
            e = e.parent;
        }

        return null;
    }

    public static void main(String[] args) {
        Tile[][] board = new Tile[10][10];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                board[i][j] = new Tile(10, i, j, false);
            }
        }
        AStar.findPath(board, 0, 0, 9, 9);
    }


    private static void checkNeighbour(Set<Entry> opened, List<Entry> closed, Entry entry, Entry neighbour) {
        if (!opened.contains(neighbour) && !neighbour.isBlocked) {
            int foundCost = entry.cost + DEFAULT_COST;
            if (neighbour.cost < 0 || neighbour.cost > foundCost) {
                neighbour.cost = foundCost;
                neighbour.parent = entry;
                closed.add(neighbour);
            }
        }
    }

    private static class Entry {
        int x, y;
        int manhattan;
        Entry parent;
        int cost;
        boolean isBlocked;

        public Entry(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return "Entry{" +
                    "x=" + x +
                    ", y=" + y +
                    ", manhattan=" + manhattan +
                    ", cost=" + cost +
                    '}';
        }
    }

}
