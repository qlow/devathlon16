package net.laby.bungee.handlers;

import io.netty.channel.ChannelHandlerContext;
import net.laby.bungee.ServerType;
import net.laby.protocol.packet.PacketExitServer;

/**
 * Class created by qlow | Jan
 */
public class ServerExitHandler {

    public static void handle( PacketExitServer exitServer, ChannelHandlerContext ctx ) {
        // Removing server
        ServerType.getByName( exitServer.getType() ).getServers().remove( exitServer.getUuid() );
    }

}
