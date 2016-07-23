package net.laby.protocol.packet;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.laby.protocol.Packet;

/**
 * Packet sent when the daemon provides the available types for it
 * Class created by qlow | Jan
 */
@NoArgsConstructor
@AllArgsConstructor
public class PacketAvailableTypes extends Packet {

    @Getter
    private String[] types;

    @Override
    public void read( ByteBuf byteBuf ) {
        this.types = new String[byteBuf.readInt()];

        for ( int i = 0; i < types.length; i++ ) {
            types[i] = readString( byteBuf );
        }
    }

    @Override
    public void write( ByteBuf byteBuf ) {
        byteBuf.writeInt( types.length );

        for ( String type : types ) {
            writeString( byteBuf, type );
        }
    }

}
