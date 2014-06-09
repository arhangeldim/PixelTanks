package arhangel.dim.pixeltank.messages;

import java.nio.ByteBuffer;

/****/
public class LogonMessage extends Message {
    String login;

    public LogonMessage() {
    }

    public LogonMessage(String login) {
        this.login = login;
    }

    public void unpack(ByteBuffer buffer) {
        type = buffer.get();
        int length = buffer.getInt();
        char[] tmp = new char[length];
        for (int i = 0; i < length; i++) {
            tmp[i] = buffer.getChar();
        }
        login = new String(tmp);
    }

    @Override
    public int getSize() {
        return 1 + login.length() * 2 + 4;
    }

    @Override
    public void packTo(ByteBuffer buffer, int pos) {
        buffer.position(pos);
        buffer.put(MESSAGE_LOGON);
        buffer.putInt(login.length());
        for (int i = 0; i < login.length(); i++) {
            buffer.putChar(login.charAt(i));
        }
    }

    @Override
    public String toString() {
        return "LogonMessage{" + "login='" + login + "\'}";
    }
}