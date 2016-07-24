package net.laby.bungee.handlers;

import io.netty.channel.ChannelHandlerContext;
import net.laby.bungee.ApplicationUpdater;
import net.laby.bungee.Jaby;
import net.laby.bungee.ServerType;
import net.laby.protocol.packet.PacketUpdateType;

import java.util.ArrayList;
import java.util.List;

/**
 * Class created by qlow | Jan
 */
public class UpdateTypeHandler {

    public static void handle( PacketUpdateType updateType, ChannelHandlerContext ctx ) {
        if ( updateType.getType().equals( "BungeeCord" ) ) {
            if(updateType.getAction() != 2)
                return;

            List<String> motd = new ArrayList<>();
            for ( String splitedMotd : updateType.getMotd().split( "\n" ) ) {
                if ( splitedMotd.equals( "" ) )
                    continue;

                motd.add( splitedMotd );
            }

            Jaby.getInstance().getConfiguration().set( "motd", motd );
            Jaby.getInstance().getConfigLoader().saveConfig();

            Jaby.getInstance().setMotdString( updateType.getMotd() );
            return;
        }

        ServerType serverType = ServerType.getByName( updateType.getType() );

        switch ( updateType.getAction() ) {
            case 0:
                serverType.setStandby( !serverType.isStandby() );
                Jaby.getInstance().saveServerTypes();
                serverType.shutdown();
                ApplicationUpdater.updateTypes();
                break;
            case 1:
                serverType.shutdown();
                break;
            case 2:
                serverType.setMotd( updateType.getMotd() );
                Jaby.getInstance().saveServerTypes();
                break;
        }
    }

}
