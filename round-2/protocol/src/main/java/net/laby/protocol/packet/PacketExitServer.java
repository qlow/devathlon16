package net.laby.protocol.packet;

import io.netty.buffer.ByteBuf;
import net.laby.protocol.Packet;

/**
 * Packet sent when a server stops/exits
 * Class created by qlow | Jan
 */
public class PacketExitServer extends Packet {

    @Override
    public void read( ByteBuf byteBuf ) {

    }

    @Override
    public void write( ByteBuf byteBuf ) {

    }

}
