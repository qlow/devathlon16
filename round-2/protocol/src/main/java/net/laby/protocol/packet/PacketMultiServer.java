package net.laby.protocol.packet;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.laby.protocol.Packet;

import java.util.UUID;

/**
 * Packet sent when the daemon connects & sends all already started servers to the master
 * Class created by qlow | Jan
 */
@NoArgsConstructor
@AllArgsConstructor
public class PacketMultiServer extends Packet {

    @Getter
    private int count;
    @Getter
    private String[] types;
    @Getter
    private UUID[] uuids;
    @Getter
    private int[] ports;

    @Override
    public void read( ByteBuf byteBuf ) {
        this.count = byteBuf.readInt();

        this.types = new String[count];
        this.uuids = new UUID[count];
        this.ports = new int[count];

        for ( int i = 0; i < count; i++ ) {
            this.types[i] = readString( byteBuf );
            this.uuids[i] = UUID.fromString( readString( byteBuf ) );
            this.ports[i] = byteBuf.readInt();
        }
    }

    @Override
    public void write( ByteBuf byteBuf ) {
        byteBuf.writeInt( count );

        for ( int i = 0; i < count; i++ ) {
            writeString( byteBuf, this.types[i] );
            writeString( byteBuf, this.uuids[i].toString() );
            byteBuf.writeInt( this.ports[i] );
        }
    }

}
