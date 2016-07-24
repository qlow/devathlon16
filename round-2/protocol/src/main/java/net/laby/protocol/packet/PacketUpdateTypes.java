package net.laby.protocol.packet;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.laby.protocol.Packet;

/**
 * Sent to the application when the types updated
 * Class created by qlow | Jan
 */
@NoArgsConstructor
@AllArgsConstructor
public class PacketUpdateTypes extends Packet {

    @Getter
    private int count;

    @Getter
    private String[] typesNames;

    @Getter
    private String[] motds;

    @Getter
    private int[] startedServers;

    @Getter
    private int[] maxServers;

    @Getter
    private boolean[] typesStandby;

    @Override
    public void read( ByteBuf byteBuf ) {
        this.count = byteBuf.readInt();

        this.typesNames = new String[count];
        this.motds = new String[count];
        this.startedServers = new int[count];
        this.maxServers = new int[count];
        this.typesStandby = new boolean[count];

        for ( int i = 0; i < count; i++ ) {
            this.typesNames[i] = readString( byteBuf );
            this.motds[i] = readString( byteBuf );
            this.startedServers[i] = byteBuf.readInt();
            this.maxServers[i] = byteBuf.readInt();
            this.typesStandby[i] = byteBuf.readBoolean();
        }
    }

    @Override
    public void write( ByteBuf byteBuf ) {
        byteBuf.writeInt( count );

        for ( int i = 0; i < count; i++ ) {
            writeString( byteBuf, this.typesNames[i] );
            writeString( byteBuf, this.motds[i] );
            byteBuf.writeInt( this.startedServers[i] );
            byteBuf.writeInt( this.maxServers[i] );
            byteBuf.writeBoolean( this.typesStandby[i] );
        }
    }

}
