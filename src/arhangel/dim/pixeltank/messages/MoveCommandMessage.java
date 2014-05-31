package arhangel.dim.pixeltank.messages;

import arhangel.dim.pixeltank.game.UnitCommand;

import java.nio.ByteBuffer;

/****/
public class MoveCommandMessage extends Message {
    private UnitCommand command;

    public MoveCommandMessage(UnitCommand cmd) {
        this.command = cmd;
    }

    public MoveCommandMessage(ByteBuffer packed) {
        type = packed.get();
        byte code = packed.get();
        command = UnitCommand.byCode(code);
    }

    public UnitCommand getCommand() {
        return command;
    }

    public void setCommand(UnitCommand command) {
        this.command = command;
    }

    @Override
    public int getSize() {
        return 2;
    }

    @Override
    public void packTo(ByteBuffer buffer, int pos) {
        buffer.position(pos);
        buffer.put(MESSAGE_CMD_MOVE);
        buffer.put((byte) command.getCode());
    }

    @Override
    public String toString() {
        return "MoveCommandMessage{" + "execute=" + command.getCode() + '}';
    }
}