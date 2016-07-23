package net.laby.bungee.handlers;

import io.netty.channel.ChannelHandlerContext;
import net.laby.bungee.Jaby;
import net.laby.protocol.packet.PacketLogin;

/**
 * Class created by qlow | Jan
 */
public class LoginHandler {

    public static void handle( PacketLogin packetLogin, ChannelHandlerContext channelHandlerContext ) {
        if ( !packetLogin.getPassword().equals( Jaby.getInstance().getConfiguration().getString( "password" ) ) ) {

            return;
        }
    }

}
