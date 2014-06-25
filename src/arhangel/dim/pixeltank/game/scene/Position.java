package arhangel.dim.pixeltank.game.scene;

/**
 *
 */
public class Position {
    public int x, y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Position(Position position) {
        this.x = position.x;
        this.y = position.y;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (!(obj instanceof Position))
            return false;
        if (obj == this)
            return true;

        Position other = (Position) obj;
        return (this.x == other.x) && (this.y == other.y);
    }

    @Override
    public int hashCode() {
        int hash = 37;
        hash = 31 * hash + x;
        hash = 31 * hash + y;
        return hash;
    }

    @Override
    public String toString() {
        return "Position{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
