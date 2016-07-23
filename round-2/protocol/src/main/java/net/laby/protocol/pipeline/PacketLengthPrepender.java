package net.laby.protocol.pipeline;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * That part of the pipeline writes the length of the incoming packet
 * Class created by qlow | Jan
 */
public class PacketLengthPrepender extends MessageToByteEncoder<ByteBuf> {

    @Override
    protected void encode( ChannelHandlerContext channelHandlerContext, ByteBuf in, ByteBuf out ) throws Exception {
        // Writing the length of the in-ByteBuf to the outgoing ByteBuf
        out.writeInt( in.readableBytes() );

        // Writing remaining bytes of the in-ByteBuf to the outgoing ByteBuf
        out.writeBytes( in );
    }

}
