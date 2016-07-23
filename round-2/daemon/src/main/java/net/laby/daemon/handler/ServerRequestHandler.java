package net.laby.daemon.handler;

import io.netty.channel.ChannelHandlerContext;
import net.laby.daemon.JabyDaemon;
import net.laby.daemon.task.ServerStartTask;
import net.laby.protocol.packet.PacketRequestServer;

/**
 * Class created by qlow | Jan
 */
public class ServerRequestHandler {

    public static void handle( PacketRequestServer requestServer, ChannelHandlerContext ctx ) {
        // Starting [amount] servers (adding to queue)
        for ( int i = 0; i < requestServer.getAmount(); i++ ) {
            JabyDaemon.getInstance().getQueueStartTask().getServerQueue().add( new ServerStartTask( requestServer.getType() ) );
        }
    }

}
