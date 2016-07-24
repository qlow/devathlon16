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
public class PacketRequestServer extends Packet {

    @Getter
    private String type;
    @Getter
    private int amount;

    @Override
    public void read( ByteBuf byteBuf ) {
        this.type = readString( byteBuf );
        this.amount = byteBuf.readInt();
    }

    @Override
    public void write( ByteBuf byteBuf ) {
        writeString( byteBuf, this.type );
        byteBuf.writeInt( amount );
    }

}
