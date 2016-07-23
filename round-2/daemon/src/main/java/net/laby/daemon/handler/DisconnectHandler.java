package net.laby.daemon.handler;

import io.netty.channel.ChannelHandlerContext;
import net.laby.daemon.JabyDaemon;
import net.laby.protocol.JabyBootstrap;
import net.laby.protocol.packet.PacketDisconnect;

/**
 * Class created by qlow | Jan
 */
public class DisconnectHandler {

    public static void handle( PacketDisconnect disconnect, ChannelHandlerContext ctx ) {
        System.err.println( "[Jaby] Disconnected from server! Reason: " + disconnect.getDisconnectReason() );
        JabyDaemon.getInstance().setConnected( false );

        JabyBootstrap.getExecutorService().execute( new Runnable() {
            @Override
            public void run() {
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
