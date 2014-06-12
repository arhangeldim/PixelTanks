package arhangel.dim.pixeltank.game;

import arhangel.dim.pixeltank.game.scene.Position;

/**
 *
 */
public abstract class GameObject {

    protected Player player;

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public abstract GameObjectType getType();
    public abstract void setType(GameObjectType type);
    public abstract int getId();
    public abstract void setId(int id);
    public abstract Position getPosition();
    public abstract void setPosition(Position position);
    public abstract int getSize();
    public abstract void setSize(int size);
    public abstract Direction getDirection();
    public abstract void setDirection(Direction dir);
    public abstract void setVelocity(int v);
    public abstract int getVelocity();
    public abstract int pack();

}
