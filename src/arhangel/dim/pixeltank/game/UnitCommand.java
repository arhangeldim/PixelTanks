package arhangel.dim.pixeltank.game;

/**
 *
 */
public enum UnitCommand {
    MOVE_UP(0),
    MOVE_DOWN(1),
    MOVE_LEFT(2),
    MOVE_RIGHT(3),
    FIRE(4);

    int code;

    UnitCommand(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static UnitCommand byCode(int code) throws IllegalArgumentException {
        try{
            return UnitCommand.values()[code];
        }catch( ArrayIndexOutOfBoundsException e ) {
            throw new IllegalArgumentException("Unknown enum value : "+ code);
        }
    }

}
