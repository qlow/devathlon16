package net.laby.bungee.handlers;

import io.netty.channel.ChannelHandlerContext;
import net.laby.protocol.JabyBootstrap;
import net.laby.protocol.JabyChannel;
import net.laby.protocol.packet.PacketAvailableTypes;

/**
 * Class created by qlow | Jan
 */
public class AvailableTypesHandler {

    public static void handle( PacketAvailableTypes availableTypes, ChannelHandlerContext ctx ) {
        JabyChannel jabyChannel = JabyBootstrap.getChannels().get( ctx.channel() );

        if ( jabyChannel == null )
            return;

        jabyChannel.setAvailableTypes( availableTypes.getTypes() );
    }

}
