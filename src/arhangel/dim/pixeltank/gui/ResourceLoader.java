package arhangel.dim.pixeltank.gui;

import arhangel.dim.pixeltank.Game;
import arhangel.dim.pixeltank.game.Direction;
import arhangel.dim.pixeltank.game.GameObject;
import arhangel.dim.pixeltank.game.GameObjectType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 *
 */
public class ResourceLoader {
    private static Logger logger = LoggerFactory.getLogger(ResourceLoader.class);

    private BufferedImage[] tankSprites = null;
    private static ResourceLoader instance;

    private ResourceLoader() {
        logger.info("Initialize ResourceLoader");
        tankSprites = new BufferedImage[4];
        BufferedImage spriteRight = loadImageFromResources(Game.class, "tank_sprite.png");
        tankSprites[0] = spriteRight;
        tankSprites[1] = rotateImage(spriteRight, 90); // down
        tankSprites[2] = rotateImage(spriteRight, 180); // left
        tankSprites[3] = rotateImage(spriteRight, 270); //top
    }

    public static synchronized ResourceLoader getInstance() {
        if (instance == null) {
            instance = new ResourceLoader();
        }
        return instance;
    }

    public BufferedImage getSpriteByDirection(GameObject object) {
        // TODO: fix this! Direction should be defined
        if (object.getDirection() == null) {
            object.setDirection(Direction.LEFT);
        }

        switch (object.getDirection()) {
            case RIGHT:
                return tankSprites[0];
            case DOWN:
                return tankSprites[1];
            case LEFT:
                return tankSprites[2];
            case UP:
                return tankSprites[3];
            default:
        }
        return null;
    }

    private BufferedImage loadImageFromResources(Class holder, String path) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(holder.getResource(path));
        } catch (IOException e) {
            logger.error("Failed to load image {} from resources {}", path, holder.getName());
        }
        return image;
    }

    private BufferedImage rotateImage(BufferedImage image, double angle) {
        double rotationRequired = Math.toRadians(angle);
        double x = image.getWidth() / 2;
        double y = image.getHeight() / 2;
        AffineTransform rotationTransform = AffineTransform.getRotateInstance(rotationRequired, x, y);
        AffineTransformOp op = new AffineTransformOp(rotationTransform, AffineTransformOp.TYPE_BILINEAR);
        return op.filter(image, null);
    }
}
