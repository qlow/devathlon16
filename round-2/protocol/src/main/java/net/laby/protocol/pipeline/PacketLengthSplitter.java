package net.laby.protocol.pipeline;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * That part of the pipeline reads the length of the incoming packet
 * Class created by qlow | Jan
 */
public class PacketLengthSplitter extends ByteToMessageDecoder {

    @Override
    protected void decode( ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> out ) throws Exception {
        while ( byteBuf.readableBytes() > 0 ) {
            // Marking the current reader-index
            byteBuf.markReaderIndex();

            // Checking for an int in the buffer (a valid length is an int)
            if ( byteBuf.readableBytes() < 4 ) {
                // If there is no valid length in the buffer we can return
                return;
            }

            // Reading length
            int length = byteBuf.readInt();

            // Checking for less readable bytes than the length (shouldn't happen)
            if ( byteBuf.readableBytes() < length ) {
                // Resetting reader-index & returning because there can not be a valid packet with the read length
                byteBuf.resetReaderIndex();
                return;
            }

            // Adding the bytes with this length to the out-list
            out.add( byteBuf.copy( byteBuf.readerIndex(), length ).retain() );

            // Skipping the bytes with the amount of the length
            byteBuf.skipBytes( length );
        }
    }

}
