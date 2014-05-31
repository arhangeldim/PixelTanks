package arhangel.dim.pixeltank.game;


/**
 *
 */
public class Terrain {

    private int movementCost;
    private boolean isWater;
    private Texture texture;

    public Terrain(int movementCost, boolean isWater, Texture texture) {
        this.movementCost = movementCost;
        this.isWater = isWater;
        this.texture = texture;
    }

    public int getMovementCost() {
        return movementCost;
    }

    public void setMovementCost(int movementCost) {
        this.movementCost = movementCost;
    }

    public boolean isWater() {
        return isWater;
    }

    public void setWater(boolean isWater) {
        this.isWater = isWater;
    }

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }
}
