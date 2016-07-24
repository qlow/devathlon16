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
public class PacketChangeMaxRam extends Packet {

    @Getter
    private UUID uuid;

    @Getter
    private int maxRam;

    @Override
    public void read( ByteBuf byteBuf ) {
        this.uuid = UUID.fromString( readString( byteBuf ) );
        this.maxRam = byteBuf.readInt();
    }

    @Override
    public void write( ByteBuf byteBuf ) {
        writeString( byteBuf, uuid.toString() );
        byteBuf.writeInt( maxRam );
    }

}
