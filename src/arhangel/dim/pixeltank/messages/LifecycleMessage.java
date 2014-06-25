package arhangel.dim.pixeltank.messages;

import arhangel.dim.pixeltank.game.GameObject;
import arhangel.dim.pixeltank.game.Player;
import arhangel.dim.pixeltank.game.Unit;

import java.nio.ByteBuffer;

/**
 *
 */
public class LifecycleMessage extends Message {
    public static final byte SPAWNED = 0;
    public static final byte KILLED = 1;

    private byte stage;
    private Player player;
    private Player killer;
    private GameObject object;

    public byte getStage() {
        return stage;
    }

    public void setStage(byte stage) {
        this.stage = stage;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Player getKiller() {
        return killer;
    }

    public void setKiller(Player killer) {
        this.killer = killer;
    }

    public GameObject getObject() {
        return object;
    }

    public void setObject(GameObject object) {
        this.object = object;
    }

    @Override
    public int getSize() {
        return 1 + 1 + 1 + 4 + player.getName().length() * 2 + 4;
    }

    @Override
    public void packTo(ByteBuffer buffer, int pos) {
        buffer.position(pos);
        buffer.put(MESSAGE_LIFECYCLE);
        buffer.put(stage);
        buffer.putInt(player.getId());
        buffer.put((byte) player.getName().length());
        for (int i = 0; i < player.getName().length(); i++) {
            buffer.putChar(player.getName().charAt(i));
        }
        buffer.putInt(object.pack());
    }

    @Override
    public void unpack(ByteBuffer buffer) {
        type = buffer.get();
        stage = buffer.get();
        player = new Player(buffer.getInt());
        int length = buffer.get();
        char[] arr = new char[length];
        for (int i = 0; i < length; i++) {
            arr[i] = buffer.getChar();
        }
        player.setName(new String(arr));
        object = new Unit();
        Unit u = (Unit) object;
        u.unpack(buffer.getInt());
    }
}
