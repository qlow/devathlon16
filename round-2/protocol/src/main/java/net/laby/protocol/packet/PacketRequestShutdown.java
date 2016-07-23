package net.laby.protocol.packet;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.laby.protocol.Packet;

import java.util.UUID;

/**
 * Class created by qlow | Jan
 */
@NoArgsConstructor
@AllArgsConstructor
public class PacketRequestShutdown extends Packet {

    @Getter
    private UUID uuid;

    @Override
    public void read( ByteBuf byteBuf ) {
        this.uuid = UUID.fromString( readString( byteBuf ) );
    }

    @Override
    public void write( ByteBuf byteBuf ) {
        writeString( byteBuf, uuid.toString() );
    }

}
