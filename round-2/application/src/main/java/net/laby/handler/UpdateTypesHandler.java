package net.laby.handler;

import io.netty.channel.ChannelHandlerContext;
import net.laby.application.Application;
import net.laby.protocol.packet.PacketUpdateTypes;

/**
 * Class created by qlow | Jan
 */
public class UpdateTypesHandler {

    public static void handle( PacketUpdateTypes updateTypes, ChannelHandlerContext ctx ) {
        Application.getInstance().setUpdateTypes( updateTypes );

        if ( Application.getInstance().getControlTable() != null ) {
            Application.getInstance().getControlTable().refreshServerTable();
        }
    }

}
