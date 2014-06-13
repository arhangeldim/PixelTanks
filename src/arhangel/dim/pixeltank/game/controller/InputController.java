package arhangel.dim.pixeltank.game.controller;

import arhangel.dim.pixeltank.connection.ConnectionListener;
import arhangel.dim.pixeltank.game.Player;
import arhangel.dim.pixeltank.messages.LogonMessage;
import arhangel.dim.pixeltank.messages.Message;
import arhangel.dim.pixeltank.messages.MoveCommandMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class InputController implements ConnectionListener {
    private static Logger logger = LoggerFactory.getLogger(InputController.class);
    private GameEventHandler handler;

    @Override
    public void onMessageReceived(Message message) {
        int type = message.getType();
        Player player = message.getPlayer();
        switch (type) {
            case Message.MESSAGE_LOGON:
                if (player == null) {
                    throw new RuntimeException("Unexpected value");
                }
                player.setName(((LogonMessage) message).getLogin());
                handler.logon(player);
                logger.info("{} logged on", player);
                break;
            case Message.MESSAGE_CMD_MOVE:
                MoveCommandMessage moveCmdMessage = (MoveCommandMessage) message;
                handler.move(player, moveCmdMessage.getDirection());
                return;
            case Message.MESSAGE_FIRE:
                handler.fire(player);
                break;
        }
    }

    public GameEventHandler getHandler() {
        return handler;
    }

    public void setHandler(GameEventHandler handler) {
        this.handler = handler;
    }
}
