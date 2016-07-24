package net.laby.protocol.packet;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.laby.protocol.Packet;

/**
 * Class created by qlow | Jan
 */
@NoArgsConstructor
@AllArgsConstructor
public class PacketUpdateType extends Packet {

    @Getter
    private String type;

    // 0 = standby, 1 = kill all, 2 = set motd
    @Getter
    private int action;

    @Getter
    private String motd;

    @Override
    public void read( ByteBuf byteBuf ) {
        this.type = readString( byteBuf );
        this.action = byteBuf.readInt();

        if ( action == 2 ) {
            this.motd = readString( byteBuf );
        }
    }

    @Override
    public void write( ByteBuf byteBuf ) {
        writeString( byteBuf, type );
        byteBuf.writeInt( action );

        if ( action == 2 ) {
            writeString( byteBuf, motd );
        }
    }

}
