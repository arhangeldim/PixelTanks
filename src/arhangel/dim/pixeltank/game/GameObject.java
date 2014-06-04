package arhangel.dim.pixeltank.game;

import arhangel.dim.pixeltank.game.scene.Position;

/**
 *
 */
public interface GameObject {
    public int getId();

    public void setId(int id);

    public Position getPosition();

    public void setPosition(Position position);

    public int getSize();

    public void setSize(int size);

    public Direction getDirection();

    public void setDirection(Direction dir);

    public void setVelocity(int v);

    public int getVelocity();

}
