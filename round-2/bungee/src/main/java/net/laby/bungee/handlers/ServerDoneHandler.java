package net.laby.bungee.handlers;

import io.netty.channel.ChannelHandlerContext;
import net.laby.bungee.Jaby;
import net.laby.bungee.ServerType;
import net.laby.protocol.packet.PacketServerDone;
import net.md_5.bungee.api.event.LoginEvent;

import java.util.Map;
import java.util.UUID;

/**
 * Class created by qlow | Jan
 */
public class ServerDoneHandler {

    public static void handle( PacketServerDone serverDone, ChannelHandlerContext ctx ) {
        ServerType serverType = ServerType.getByName( serverDone.getType() );

        for ( Map.Entry<UUID, LoginEvent> joiningPlayerEntry : serverType.getJoiningPlayers().entrySet() ) {
            if(!joiningPlayerEntry.getValue().getIntents().contains( Jaby.getInstance() ))
                continue;

            joiningPlayerEntry.getValue().completeIntent( Jaby.getInstance() );
        }
    }

}
