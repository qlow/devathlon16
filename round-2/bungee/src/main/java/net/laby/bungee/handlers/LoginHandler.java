package net.laby.bungee.handlers;

import io.netty.channel.ChannelHandlerContext;
import net.laby.bungee.Jaby;
import net.laby.protocol.JabyBootstrap;
import net.laby.protocol.JabyChannel;
import net.laby.protocol.packet.PacketDisconnect;
import net.laby.protocol.packet.PacketLogin;
import net.laby.protocol.packet.PacketLoginSuccessful;

/**
 * Class created by qlow | Jan
 */
public class LoginHandler {

    public static void handle( PacketLogin packetLogin, ChannelHandlerContext ctx ) {
        // Checking for the right password
        if ( !packetLogin.getPassword().equals( Jaby.getInstance().getPassword() ) ) {
            // Disconnecting the client if the password was wrong
            ctx.channel().writeAndFlush( new PacketDisconnect( "The password you typed in is wrong!" ) );
            return;
        }

        // Sending PacketLoginSuccessful if the login was successful
        ctx.channel().writeAndFlush( new PacketLoginSuccessful() );

        if ( !JabyBootstrap.isClient() ) {
            // Adding to channel-map
            JabyBootstrap.getChannels().put( ctx.channel(), new JabyChannel( ctx.channel(), packetLogin.getClientType(),
                    packetLogin.getMaxRamUsage() ) );
        }
    }

}
