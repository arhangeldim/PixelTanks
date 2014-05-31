package arhangel.dim.pixeltank.game;

/****/
public class Unit {
    public int id;
    public short x;
    public short y;
    public int vx;
    public int vy;// 10 10 12

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
