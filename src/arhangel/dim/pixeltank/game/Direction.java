package arhangel.dim.pixeltank.game;

/**
 *
 */
public enum Direction {
    UP(0),
    DOWN(1),
    LEFT(2),
    RIGHT(3);

    int code;

    Direction(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static Direction byCode(int code) throws IllegalArgumentException {
        try {
            return Direction.values()[code];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Unknown enum value : " + code);
        }
    }

}
