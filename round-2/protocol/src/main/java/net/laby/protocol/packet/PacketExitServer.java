package net.laby.protocol.packet;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.laby.protocol.Packet;

import java.util.UUID;

/**
 * Packet sent when a server stops/exits
 * Class created by qlow | Jan
 */
@NoArgsConstructor
@AllArgsConstructor
public class PacketExitServer extends Packet {

    @Getter
    private UUID uuid;
    @Getter
    private String type;
    @Getter
    private int port;

    @Override
    public void read( ByteBuf byteBuf ) {
        this.uuid = UUID.fromString( readString( byteBuf ) );
        this.type = readString( byteBuf );
        this.port = byteBuf.readInt();
    }

    @Override
    public void write( ByteBuf byteBuf ) {
        writeString( byteBuf, uuid.toString() );
        writeString( byteBuf, type );
        byteBuf.writeInt( port );
    }

}
