package net.laby.protocol.packet;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.laby.protocol.Packet;

import java.util.UUID;

/**
 * Sent to the application when the daemons updated
 * Class created by qlow | Jan
 */
@NoArgsConstructor
@AllArgsConstructor
public class PacketUpdateDaemons extends Packet {

    @Getter
    private int count;

    @Getter
    private String[] ips;

    @Getter
    private UUID[] uuids;

    @Getter
    private int[] daemonsCurrentRam;

    @Getter
    private int[] daemonsMaxRam;

    @Override
    public void read( ByteBuf byteBuf ) {
        this.count = byteBuf.readInt();

        this.ips = new String[count];
        this.uuids = new UUID[count];
        this.daemonsCurrentRam = new int[count];
        this.daemonsMaxRam = new int[count];

        for ( int i = 0; i < count; i++ ) {
            this.ips[i] = readString( byteBuf );
            this.uuids[i] = UUID.fromString( readString( byteBuf ) );
            this.daemonsCurrentRam[i] = byteBuf.readInt();
            this.daemonsMaxRam[i] = byteBuf.readInt();
        }
    }

    @Override
    public void write( ByteBuf byteBuf ) {
        byteBuf.writeInt( count );

        for ( int i = 0; i < count; i++ ) {
            writeString( byteBuf, this.ips[i] );
            writeString( byteBuf, this.uuids[i].toString() );
            byteBuf.writeInt( this.daemonsCurrentRam[i] );
            byteBuf.writeInt( this.daemonsMaxRam[i] );
        }
    }

}
