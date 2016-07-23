package net.laby.daemon.handler;

import io.netty.channel.ChannelHandlerContext;
import net.laby.daemon.JabyDaemon;
import net.laby.protocol.packet.PacketAvailableTypes;
import net.laby.protocol.packet.PacketLoginSuccessful;

/**
 * Class created by qlow | Jan
 */
public class LoginSuccessfulHanndler {

    public static void handle( PacketLoginSuccessful loginSuccessful, ChannelHandlerContext ctx ) {
        System.out.println( "[Jaby] Logged in successfully!" );

        JabyDaemon.getInstance().setLoggedIn( true );
        ctx.channel().writeAndFlush( new PacketAvailableTypes( JabyDaemon.getInstance().getAvailableTypes() ) );
    }

}
