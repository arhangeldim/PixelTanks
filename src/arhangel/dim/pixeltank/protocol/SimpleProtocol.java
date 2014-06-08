package arhangel.dim.pixeltank.protocol;

import arhangel.dim.pixeltank.messages.AckMessage;
import arhangel.dim.pixeltank.messages.DeltaMessage;
import arhangel.dim.pixeltank.messages.FireMessage;
import arhangel.dim.pixeltank.messages.LogonMessage;
import arhangel.dim.pixeltank.messages.Message;
import arhangel.dim.pixeltank.messages.MoveCommandMessage;
import arhangel.dim.pixeltank.messages.SnapshotMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/****/
public class SimpleProtocol implements Protocol {
    private static Logger logger = LoggerFactory.getLogger(SimpleProtocol.class);
    public static final byte VERSION = 1;

    public class Header {
        byte protocolVersion;
        public int bodySize;

        public Header(byte protocolVersion, int bodySize) {
            this.protocolVersion = protocolVersion;
            this.bodySize = bodySize;
        }

        public Header(ByteBuffer buffer) {
            this.protocolVersion = buffer.get();
            this.bodySize = buffer.getInt();
        }

        private static final int HEADER_SIZE = 5;

        public int getHeaderSize() {
            return HEADER_SIZE;
        }

        public void packTo(ByteBuffer buffer, int pos) {
            buffer.position(pos);
            buffer.put(protocolVersion);
            buffer.putInt(bodySize);
        }

        @Override
        public String toString() {
            return "Header{" + "protocolVersion=" + protocolVersion + ", bodySize=" + bodySize + '}';
        }
    }

    @Override
    public Message decode(DataInputStream in) throws MessageDecodingException, IOException {
        Message message = null;
        byte[] tmp = new byte[Header.HEADER_SIZE];
        in.readFully(tmp);
        ByteBuffer packet = ByteBuffer.wrap(tmp);
        int version = packet.get(0);
        if (version != VERSION) {
            throw new MessageDecodingException("Unexpected protocol version: " + version);
        }
        Header header = new Header(packet);
        tmp = new byte[header.bodySize];
        in.readFully(tmp);
        packet = ByteBuffer.wrap(tmp);
        int type = packet.get(0);
        switch (type) {
            case Message.MESSAGE_LOGON:
                message = new LogonMessage(packet);
                break;
            case Message.MESSAGE_ACK:
                message = new AckMessage(packet);
                break;
            case Message.MESSAGE_CMD_MOVE:
                message = new MoveCommandMessage(packet);
                break;
            case Message.MESSAGE_DELTA:
                message = new DeltaMessage(packet);
                break;
            case Message.MESSAGE_SNAPSHOT:
                message = new SnapshotMessage(packet);
                break;
            case Message.MESSAGE_FIRE:
                message = new FireMessage(packet);
                break;
            default:
                throw new MessageDecodingException("Unknown message type: " + type);
        }
        logger.info("decode: {}, {}", header, message);
        return message;
    }

    @Override
    public void encode(DataOutputStream out, Message message) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(Header.HEADER_SIZE + message.getSize());
        Header header = new Header(VERSION, message.getSize());
        logger.info("encode: {}, {}", header, message
        );
        header.packTo(buffer, 0);
        message.packTo(buffer, Header.HEADER_SIZE);
        out.write(buffer.array());
        out.flush();
    }

    @Override
    public int getVersion() {
        return VERSION;
    }
}