package arhangel.dim.pixeltank.game;

import java.awt.*;

/**
 *
 */
public class Texture {

    public Texture(Color color) {
        this.color = color;
    }

    private Color color;

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
