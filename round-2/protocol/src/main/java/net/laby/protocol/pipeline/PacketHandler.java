package net.laby.protocol.pipeline;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Getter;
import net.laby.protocol.JabyBootstrap;
import net.laby.protocol.Packet;
import net.laby.protocol.packet.PacketDisconnect;
import net.laby.protocol.packet.PacketLogin;
import net.laby.protocol.utils.JabyUtils;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Class created by qlow | Jan
 */
public class PacketHandler extends SimpleChannelInboundHandler<Packet> {

    @Getter
    private Channel channel;
    private List<Packet> sentBeforeChannelActive = new ArrayList<>();

    public PacketHandler( Channel channel ) {
        this.channel = channel;
    }

    @Override
    public void exceptionCaught( ChannelHandlerContext ctx, Throwable cause ) throws Exception {
        if ( !(cause instanceof IOException) ) {
            System.err.println( "[Jaby] Exception caught (" + JabyUtils.getHostString( ctx.channel().remoteAddress() ) + "):" );
            cause.printStackTrace();
        }
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
    public void channelInactive( ChannelHandlerContext ctx ) throws Exception {
        super.channelInactive( ctx );

        if ( !JabyBootstrap.isClient() ) {
            System.out.println( "[Jaby] " + JabyUtils.getHostString( ctx.channel().remoteAddress() ) + " disconnected!" );
            JabyBootstrap.getChannels().remove( ctx.channel() );
        } else {
            System.out.println( "[Jaby] Disconnected from server!" );
        }
    }

    @Override
    protected void channelRead0( ChannelHandlerContext channelHandlerContext, Packet packet ) throws Exception {
        if ( !JabyBootstrap.getHandlers().containsKey( packet.getClass() ) )
            return;

        // Checking for client-login
        if ( !JabyBootstrap.isClient() && !(packet instanceof PacketLogin) && !JabyBootstrap.getChannels().containsKey( channelHandlerContext.channel() ) ) {
            channelHandlerContext.writeAndFlush( new PacketDisconnect( "You sent packets while you weren't logged in!" ) );
            channelHandlerContext.channel().close();
            return;
        }

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
