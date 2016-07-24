package net.laby.handler;

import io.netty.channel.ChannelHandlerContext;
import net.laby.application.Application;
import net.laby.protocol.packet.PacketDisconnect;
import net.laby.utils.Utils;

import javax.swing.*;

/**
 * Class created by LabyStudio
 */
public class DisconnectHandler {

    public static void handle( PacketDisconnect disconnect, ChannelHandlerContext ctx) {
        Utils.showDialog( null, "Disconnected from server", disconnect.getDisconnectReason(), new ImageIcon( Application.class.getResource( "/assets/connectionError.png" ) ) );

        Application.getInstance().getMainGUI().setVisible( true );
    }

}
