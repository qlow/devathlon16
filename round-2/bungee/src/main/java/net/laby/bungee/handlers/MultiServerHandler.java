package net.laby.bungee.handlers;

import io.netty.channel.ChannelHandlerContext;
import net.laby.protocol.packet.PacketMultiServer;
import net.laby.protocol.packet.PacketStartServer;
import net.laby.protocol.utils.JabyUtils;

/**
 * Class created by qlow | Jan
 */
public class MultiServerHandler {

    public static void handle( PacketMultiServer multiServer, ChannelHandlerContext ctx ) {
        // Iterating through servers and calling ServerStartHandler#handle for everyone
        for(int i = 0; i < multiServer.getCount(); i++) {
            ServerStartHandler.handle( new PacketStartServer(
                    multiServer.getTypes()[i], multiServer.getUuids()[i], multiServer.getPorts()[i] ), ctx );
        }

        System.out.println("[Jaby] " + JabyUtils.getHostString( ctx.channel().remoteAddress() )
                + " had started " + multiServer.getCount() + " servers already! Registered them!");
    }

}
