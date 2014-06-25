package arhangel.dim.pixeltank.util;

/**
 *
 */
public class SceneUtil {
    private static boolean isPointIncluded(int px, int py, int x, int y, int size) {
        return ((px > x) && (px < x + size) && (py > y) && (py < y + size));
    }
    private static boolean isPointIncludedBorders(int px, int py, int x, int y, int size) {
        return ((px >= x) && (px <= x + size) && (py >= y) && (py <= y + size));
    }

    public static boolean intersect(int x1, int y1, int size1, int x2, int y2, int size2) {
        return isPointIncluded(x1, y1, x2, y2, size2)
                || isPointIncluded(x1 + size1, y1, x2, y2, size2)
                || isPointIncluded(x1, y1 + size1, x2, y2, size2)
                || isPointIncluded(x1 + size1, y1 + size1, x2, y2, size2)
                || isPointIncludedBorders(x1 + size1 / 2, y1 + size1 / 2, x2, y2, size2);
    }
}
