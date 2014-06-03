package arhangel.dim.pixeltank.game;

/****/
public class Unit {
    private Scene scene;
    private int id;
    private int x;
    private int y;
    private int vx;
    private int vy;// 10 10 12

    public Unit() {
    }

    public Unit(Scene scene, int id, int x, int y, int vx, int vy) {
        this.scene = scene;
        this.id = id;
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean execute(UnitCommand cmd) {
        switch (cmd) {
            case MOVE_DOWN:
                if (scene.isValidPosition(x, y + vy)) {
                    y += vy;
                    return true;
                }
                break;
            case MOVE_UP:
                if (scene.isValidPosition(x, y - vy)) {
                    y -= vy;
                    return true;
                }
                break;
            case MOVE_LEFT:
                if (scene.isValidPosition(x - vx, y)) {
                    x -= vx;
                    return true;
                }
                break;
            case MOVE_RIGHT:
                if (scene.isValidPosition(x + vx, y)) {
                    x += vy;
                    return true;
                }
                break;
            case FIRE:
                System.out.println("Baabah!");
                break;
            default:
                System.out.println("Unknown command: " + cmd);

        }
        return false;
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
        packed |= id & 0xfff;
        packed |= ((int) x) << 12;
        packed |= ((int) y) << 22;
        return packed;
    }

    private boolean validate() {
        return (id < 0x1000) && (x < 0x400) && (y < 0x400);
    }

    public void unpack(long packed) {
        if (packed >= 0) {
            id = (short) (packed & 0xfff);
            x = (short) ((packed >> 12) & 0x3ff);
            y = (short) ((packed >> 22) & 0x3ff);
        }
    }

    @Override
    public String toString() {
        return "Unit{" + "id=" + id + ", x=" + x + ", y=" + y + ", vx=" + vx + ", vy=" + vy + '}';
    }
}
