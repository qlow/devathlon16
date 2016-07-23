package net.laby.protocol.pipeline;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.laby.protocol.JabyBootstrap;
import net.laby.protocol.Packet;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Class created by qlow | Jan
 */
public class PacketHandler extends SimpleChannelInboundHandler<Packet> {

    private Channel channel;
    private List<Packet> sentBeforeChannelActive = new ArrayList<>();

    public PacketHandler( Channel channel ) {
        this.channel = channel;
    }

    @Override
    public void channelActive( ChannelHandlerContext ctx ) throws Exception {
        super.channelActive( ctx );

        // Iterating through the packets that was sent before the channel was active
        for ( Packet packet : sentBeforeChannelActive ) {
            channel.writeAndFlush( packet );
        }

        sentBeforeChannelActive.clear();
    }

    @Override
    protected void channelRead0( ChannelHandlerContext channelHandlerContext, Packet packet ) throws Exception {
        if ( !JabyBootstrap.getHandlers().containsKey( packet.getClass() ) )
            return;

        // Calling handlers
        for ( Method method : JabyBootstrap.getHandlers().get( packet.getClass() ) ) {
            method.invoke( null, packet, channelHandlerContext );
        }
    }

    /**
     * Sends a packet to the channel of the PacketHandler
     *
     * @param packet packet that should be sent
     */
    public void sendPacket( Packet packet ) {
        // Adding to the list with the sent packets before the channel was active if the channel isn't active
        if ( !channel.isActive() ) {
            sentBeforeChannelActive.add( packet );
            return;
        }

        // Writing to channel
        channel.writeAndFlush( packet );
    }

}
