package net.laby.bungee.handlers;

import io.netty.channel.ChannelHandlerContext;
import net.laby.bungee.Jaby;
import net.laby.bungee.JabyServer;
import net.laby.bungee.ServerType;
import net.laby.protocol.packet.PacketStartServer;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

import java.net.InetSocketAddress;

/**
 * Class created by qlow | Jan
 */
public class ServerStartHandler {

    public static void handle( PacketStartServer startServer, ChannelHandlerContext ctx ) {
        InetSocketAddress socketAddress = ( InetSocketAddress ) ctx.channel().remoteAddress();

        // Adding server to server-type
        ServerType.getByName( startServer.getType() ).getServers().put( startServer.getUuid(),
                new JabyServer( startServer.getUuid(), startServer.getType(),
                        socketAddress.getHostName(), startServer.getPort() ) );

        Object serverInfo = null;
        String name = startServer.getType() + "-" + startServer.getPort();

        try {
            serverInfo = Class.forName( "net.md_5.bungee.BungeeServerInfo" )
                    .getConstructor( String.class, InetSocketAddress.class, String.class, boolean.class )
                    .newInstance( name, new InetSocketAddress( socketAddress.getHostName(), startServer.getPort() ),
                            Jaby.getInstance().getMotdString(), false );
        } catch ( Exception e ) {
            e.printStackTrace();
        }

        // Adding server to bungeecord-list
        ProxyServer.getInstance().getServers().put( name, ( ServerInfo ) serverInfo );
    }

}
