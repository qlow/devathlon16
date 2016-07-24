package net.laby.daemon.handler;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
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

        // Adding future, so the client auto-reconnects if the server closes the connection
        ctx.channel().closeFuture().addListener( new ChannelFutureListener() {
            @Override
            public void operationComplete( ChannelFuture channelFuture ) throws Exception {
                if(JabyDaemon.getInstance().isDisabling())
                    return;

                JabyDaemon.getInstance().setConnected( false );
                System.out.println( "[Jaby] Attempting connecting again in 10 seconds..." );

                try {
                    Thread.sleep( 10000L );
                } catch ( InterruptedException e ) {
                    e.printStackTrace();
                }

                JabyDaemon.getInstance().connect();
            }
        } );
    }

}
