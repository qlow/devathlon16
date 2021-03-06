package net.laby.bungee.handlers;

import io.netty.channel.ChannelHandlerContext;
import net.laby.bungee.ApplicationUpdater;
import net.laby.bungee.Jaby;
import net.laby.bungee.ServerType;
import net.laby.protocol.JabyBootstrap;
import net.laby.protocol.JabyChannel;
import net.laby.protocol.packet.PacketCopyMode;
import net.laby.protocol.packet.PacketDisconnect;
import net.laby.protocol.packet.PacketLogin;
import net.laby.protocol.packet.PacketLoginSuccessful;

import java.net.InetSocketAddress;

/**
 * Class created by qlow | Jan
 */
public class LoginHandler {

    public static void handle( PacketLogin packetLogin, ChannelHandlerContext ctx ) {
        if ( JabyBootstrap.getChannels().containsKey( ctx.channel() ) )
            return;

        // Checking for the right password
        if ( !packetLogin.getPassword().equals( Jaby.getInstance().getPassword() ) ) {
            // Disconnecting the client if the password was wrong
            ctx.channel().writeAndFlush( new PacketDisconnect( "The password you typed in is wrong!" ) );
            ctx.channel().close();
            return;
        }

        System.out.println( "[Jaby] " + (( InetSocketAddress ) ctx.channel().remoteAddress()).getHostString() + " logged in successfully!" );

        // Sending PacketLoginSuccessful if the login was successful
        ctx.channel().writeAndFlush( new PacketLoginSuccessful() );

        for ( ServerType serverType : ServerType.getServerTypes() ) {
            if ( !serverType.isCopyServerContent() )
                continue;

            ctx.channel().writeAndFlush( new PacketCopyMode( serverType.getType(), true ) );
        }

        if ( !JabyBootstrap.isClient() ) {
            // Adding to channel-map
            JabyBootstrap.getChannels().put( ctx.channel(), new JabyChannel( ctx.channel(), packetLogin.getClientType(),
                    packetLogin.getMaxRamUsage() ) );

            if(packetLogin.getClientType() == PacketLogin.ClientType.DAEMON) {
                // Calling update
                ApplicationUpdater.updateDaemons();
            } else {
                ApplicationUpdater.updateTypes();
                ApplicationUpdater.updateDaemons();
            }

        }
    }

}
