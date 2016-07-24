package net.laby.handler;

import io.netty.channel.ChannelHandlerContext;
import net.laby.application.Application;
import net.laby.protocol.packet.PacketUpdateDaemons;

/**
 * Class created by qlow | Jan
 */
public class UpdateDaemonsHandler {

    public static void handle( PacketUpdateDaemons updateDaemons, ChannelHandlerContext ctx ) {
        Application.getInstance().setUpdateDaemons( updateDaemons );

        if ( Application.getInstance().getControlTable() != null ) {
            Application.getInstance().getControlTable().refreshDaemonTable();
        }
    }

}
