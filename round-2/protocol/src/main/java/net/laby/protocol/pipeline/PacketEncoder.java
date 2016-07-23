package net.laby.protocol.pipeline;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.laby.protocol.Packet;
import net.laby.protocol.Protocol;

/**
 * Class created by qlow | Jan
 */
public class PacketEncoder extends MessageToByteEncoder<Packet> {

    @Override
    protected void encode( ChannelHandlerContext channelHandlerContext, Packet packet, ByteBuf byteBuf ) throws Exception {
        // Getting packet-id by packet's class
        int packetId = Protocol.getPacketIdByClass( packet.getClass() );

        // Checking for invalid packet-id
        if ( packetId == -1 ) {
            byteBuf.release();

            System.err.println( "[Jaby] Didn't find the packet-id of the packet " + packet.getClass().getSimpleName() );
            return;
        }

        // Writing packet-id to ByteBuf
        byteBuf.writeInt( packetId );

        // Writing packet to ByteBuf
        packet.write( byteBuf );
    }

}
