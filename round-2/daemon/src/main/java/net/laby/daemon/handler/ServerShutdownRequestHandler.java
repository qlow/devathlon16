package net.laby.daemon.handler;

import io.netty.channel.ChannelHandlerContext;
import net.laby.daemon.JabyDaemon;
import net.laby.protocol.JabyBootstrap;
import net.laby.protocol.packet.PacketRequestShutdown;

import java.io.IOException;

/**
 * Class created by qlow | Jan
 */
public class ServerShutdownRequestHandler {

    public static void handle( PacketRequestShutdown requestShutdown, ChannelHandlerContext ctx ) {
        Process process = JabyDaemon.getInstance().getStartedServers().get( requestShutdown.getUuid() ).getProcess();

        try {
            process.getOutputStream().write( "stop\n".getBytes() );
            process.getOutputStream().flush();
        } catch ( IOException e ) {
            e.printStackTrace();
        }

        // Removing server from server-list
        JabyDaemon.getInstance().getStartedServers().remove( requestShutdown.getUuid() );

        // Destroying process after 60 seconds
        JabyBootstrap.getExecutorService().execute( new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep( 60000 );
                } catch ( InterruptedException e ) {
                    e.printStackTrace();
                }

                process.destroy();
            }
        } );
    }

}
