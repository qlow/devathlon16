package net.laby.daemon.handler;

import io.netty.channel.ChannelHandlerContext;
import net.laby.daemon.JabyDaemon;
import net.laby.protocol.packet.PacketChangeMaxRam;

/**
 * Class created by qlow | Jan
 */
public class ChangeMaxRamHandler {

    public static void handle( PacketChangeMaxRam changeMaxRam, ChannelHandlerContext ctx ) {
        JabyDaemon.getInstance().getConfig().setMaxRamUsage( changeMaxRam.getMaxRam() );
        JabyDaemon.getInstance().getConfigManager().save();
    }

}
