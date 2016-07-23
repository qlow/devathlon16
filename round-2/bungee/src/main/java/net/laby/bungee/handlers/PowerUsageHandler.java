package net.laby.bungee.handlers;

import io.netty.channel.ChannelHandlerContext;
import net.laby.protocol.JabyBootstrap;
import net.laby.protocol.JabyChannel;
import net.laby.protocol.packet.PacketPowerUsage;

/**
 * Class created by qlow | Jan
 */
public class PowerUsageHandler {

    public static void handle( PacketPowerUsage packetPowerUsage, ChannelHandlerContext ctx ) {
        JabyChannel jabyChannel = JabyBootstrap.getChannels().get( ctx.channel() );

        if ( jabyChannel == null )
            return;

        // Setting usages
        jabyChannel.setCurrentRamUsage( packetPowerUsage.getCurrentRamUsage() );
        jabyChannel.setCurrentCpuUsage( packetPowerUsage.getCurrentCpuUsage() );
    }

}
