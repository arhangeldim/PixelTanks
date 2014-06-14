package arhangel.dim.pixeltank.game;

import arhangel.dim.pixeltank.game.scene.Position;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/****/
public class Unit extends GameObject {
    private static final Logger logger = LoggerFactory.getLogger(Unit.class);

    private int id;
    private int size;
    private Direction direction;
    private Position position;
    private int velocity;
    private GameObjectType type;

    public Unit() {
    }

    public Unit(Player player) {
        this.player = player;
    }

    public Unit(int id, Position position, int velocity, int size) {
        this.id = id;
        this.position = position;
        this.velocity = velocity;
        this.size = size;
    }

    @Override
    public Position getPosition() {
        return position;
    }

    @Override
    public void setPosition(Position position) {
        this.position = position;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public Direction getDirection() {
        return direction;
    }

    @Override
    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    @Override
    public void setVelocity(int v) {
        this.velocity = v;
    }

    @Override
    public int getVelocity() {
        return velocity;
    }

    @Override
    public GameObjectType getType() {
        return type;
    }

    @Override
    public void setType(GameObjectType type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int pack() {
        if (!validate()) {
            return -1;
        }
        int packed = 0;

        // TODO: collision with id possible because of sign reducing
        packed |= (id & 0x7ff) << 1;
        packed |= (type == GameObjectType.ROCKET) ? 0x1 : 0x0;
        packed |= (position.x) << 12;
        packed |= (position.y) << 22;
        return packed;
    }


    private boolean validate() {
        return (id < 0x1000) && (position.x < 0x400) && (position.y < 0x400);
    }

    public void unpack(long packed) {
        if (packed >= 0) {
            type = ((packed & 0x1) == 0x1) ? GameObjectType.ROCKET : GameObjectType.UNIT;
            id = (short) ((packed >> 1) & 0x7ff);
            int x = (int) ((packed >> 12) & 0x3ff);
            int y = (int) ((packed >> 22) & 0x3ff);
            position = new Position(x, y);
        }
    }

    @Override
    public String toString() {
        return "Unit{" +
                "id=" + id +
                ", type=" + type +
                ", size=" + size +
                ", direction=" + direction +
                ", position=" + position +
                ", velocity=" + velocity +
                ", owner=" + player +
                '}';
    }
}
