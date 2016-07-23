package net.laby.protocol.packet;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.laby.protocol.Packet;

/**
 * Packet sent when the client was disconnected by the server
 * Class created by qlow | Jan
 */
@NoArgsConstructor
@AllArgsConstructor
public class PacketDisconnect extends Packet {

    @Getter
    private String disconnectReason;

    @Override
    public void read( ByteBuf byteBuf ) {
        this.disconnectReason = readString( byteBuf );
    }

    @Override
    public void write( ByteBuf byteBuf ) {
        writeString( byteBuf, this.disconnectReason );
    }

}
