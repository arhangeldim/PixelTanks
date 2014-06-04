package arhangel.dim.pixeltank.game.scene;

/**
 *
 */
public class Tile {
    int tileSize;
    int x, y;
    boolean isBlocked;

    public Tile(int tileSize, int x, int y, boolean isBlocked) {
        this.tileSize = tileSize;
        this.x = x;
        this.y = y;
        this.isBlocked = isBlocked;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean isBlocked) {
        this.isBlocked = isBlocked;
    }

    @Override
    public String toString() {
        return "Tile{" +
                "x=" + x +
                ", y=" + y +
                ", isBlocked=" + isBlocked +
                '}';
    }
}