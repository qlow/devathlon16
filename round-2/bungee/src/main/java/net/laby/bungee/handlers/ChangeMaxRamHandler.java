package net.laby.bungee.handlers;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import net.laby.bungee.ApplicationUpdater;
import net.laby.protocol.JabyBootstrap;
import net.laby.protocol.JabyChannel;
import net.laby.protocol.packet.PacketChangeMaxRam;
import net.laby.protocol.utils.JabyUtils;

import java.util.Map;

/**
 * Class created by qlow | Jan
 */
public class ChangeMaxRamHandler {

    public static void handle( PacketChangeMaxRam changeMaxRam, ChannelHandlerContext ctx ) {
        for ( Map.Entry<Channel, JabyChannel> channelEntry : JabyBootstrap.getChannels().entrySet() ) {
            if ( !changeMaxRam.getUuid().toString().equals(channelEntry.getValue().getUuid().toString()) )
                continue;

            System.out.println("[Jaby] Updated RAM from "
                    + JabyUtils.getHostString( channelEntry.getValue().getChannel().remoteAddress() ));
            channelEntry.getValue().setMaxRamUsage( changeMaxRam.getMaxRam() );

            channelEntry.getKey().writeAndFlush( changeMaxRam );

            ApplicationUpdater.updateDaemons();
            return;
        }
    }

}
