package net.laby.protocol.packet;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.laby.protocol.Packet;

/**
 * Packet sent when the copy-mode for a type is (de)activated
 * Class created by qlow | Jan
 */
@NoArgsConstructor
@AllArgsConstructor
public class PacketCopyMode extends Packet {

    @Getter
    private String type;

    @Getter
    private boolean copyMode;

    @Override
    public void read( ByteBuf byteBuf ) {
        this.type = readString( byteBuf );
        this.copyMode = byteBuf.readBoolean();
    }

    @Override
    public void write( ByteBuf byteBuf ) {
        writeString( byteBuf, this.type );
        byteBuf.writeBoolean( copyMode );
    }

}
