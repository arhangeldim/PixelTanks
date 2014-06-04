package arhangel.dim.pixeltank.game.controller;

import arhangel.dim.pixeltank.game.GameObject;
import arhangel.dim.pixeltank.game.scene.Scene;
import arhangel.dim.pixeltank.messages.Message;
import arhangel.dim.pixeltank.messages.MoveCommandMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class InputController {
    private static Logger logger = LoggerFactory.getLogger(InputController.class);
    private Scene scene;
    private PhysicalController physicalController;

    public Scene getScene() {
        return scene;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public PhysicalController getPhysicalController() {
        return physicalController;
    }

    public void setPhysicalController(PhysicalController physicalController) {
        this.physicalController = physicalController;
    }

    public Message hadleInput(Message message) {
        int senderId = message.getSenderId();
        int type = message.getType();
        switch (type) {
            case Message.MESSAGE_CMD_MOVE:
                MoveCommandMessage moveCmdMessage = (MoveCommandMessage) message;
                GameObject unit = scene.getUnit(senderId);
                if (unit == null) {
                    logger.warn("Unknown unit: " + senderId);
                    return null;
                }
                unit.setDirection(moveCmdMessage.getDirection());
                return physicalController.handle(unit);
            default:


        }
        return null;
    }

}
