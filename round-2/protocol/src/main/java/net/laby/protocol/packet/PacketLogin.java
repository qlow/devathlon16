package net.laby.protocol.packet;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.laby.protocol.Packet;

/**
 * Packet sent when the client wants to login with a password
 * Class created by qlow | Jan
 */
@NoArgsConstructor
@AllArgsConstructor
public class PacketLogin extends Packet {

    @Getter
    private String password;
    @Getter
    private ClientType clientType;
    @Getter
    private int maxRamUsage;

    @Override
    public void read( ByteBuf byteBuf ) {
        this.password = readString( byteBuf );
        this.clientType = ClientType.values()[byteBuf.readInt()];
        this.maxRamUsage = byteBuf.readInt();
    }

    @Override
    public void write( ByteBuf byteBuf ) {
        writeString( byteBuf, this.password );
        byteBuf.writeInt( clientType.ordinal() );
        byteBuf.writeInt( maxRamUsage );
    }

    public static enum ClientType {
        APPLICATION, DAEMON;
    }

}
