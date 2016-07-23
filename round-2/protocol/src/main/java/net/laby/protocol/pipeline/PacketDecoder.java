package net.laby.protocol.pipeline;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.EmptyByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import net.laby.protocol.Packet;
import net.laby.protocol.Protocol;

import java.util.List;

/**
 * Class created by qlow | Jan
 */
public class PacketDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode( ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list ) {
        // Checking for empty ByteBuf
        if ( byteBuf instanceof EmptyByteBuf )
            return;

        // Reading packet-id
        int packetId = byteBuf.readInt();

        // Getting packet-class by packet's id
        Class<? extends Packet> packetClass = Protocol.getPacketClassById( packetId );

        // Checking for invalid packet
        if ( packetClass == null ) {
            byteBuf.release();

            System.err.println( "[Jaby] Couldn't get packet by id " + packetId );
            return;
        }

        // Creating instance of packet by the packet-class without any parameters
        Packet packet = null;

        try {
            packet = packetClass.newInstance();
        } catch ( InstantiationException e ) {
            e.printStackTrace();
        } catch ( IllegalAccessException e ) {
            e.printStackTrace();
        }

        // Reading packet-data
        packet.read( byteBuf );

        // Checking for remaining bytes
        if ( byteBuf.readableBytes() > 0 ) {
            System.err.println( "[Jaby] The packet " + packetClass.getSimpleName() + " wasn't read completely!" );
        }

        // Adding to list for further handling
        list.add( packet );
    }

}
