package net.laby.protocol.packet;

import io.netty.buffer.ByteBuf;
import lombok.NoArgsConstructor;
import net.laby.protocol.Packet;

/**
 * Packet sent when the login was successful
 * Class created by qlow | Jan
 */
@NoArgsConstructor
public class PacketLoginSuccessful extends Packet {

    @Override
    public void read( ByteBuf byteBuf ) {
    }

    @Override
    public void write( ByteBuf byteBuf ) {
    }

}
