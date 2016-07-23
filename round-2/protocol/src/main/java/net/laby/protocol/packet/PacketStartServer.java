package net.laby.protocol.packet;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.laby.protocol.Packet;

import java.util.UUID;

/**
 * Packet sent when a server with a type starts
 * Class created by qlow | Jan
 */
@NoArgsConstructor
@AllArgsConstructor
public class PacketStartServer extends Packet {

    @Getter
    private String type;

    @Getter
    private UUID uuid;

    @Getter
    private int port;

    @Override
    public void read( ByteBuf byteBuf ) {
        this.type = readString( byteBuf );
        this.uuid = UUID.fromString( readString( byteBuf ) );
        this.port = byteBuf.readInt();
    }

    @Override
    public void write( ByteBuf byteBuf ) {
        writeString( byteBuf, type );
        writeString( byteBuf, uuid.toString() );
        byteBuf.writeInt( this.port );
    }

}
