package net.laby.bungee;

import io.netty.bootstrap.ServerBootstrap;
import lombok.Getter;
import net.laby.bungee.handlers.AvailableTypesHandler;
import net.laby.bungee.handlers.LoginHandler;
import net.laby.bungee.handlers.PowerUsageHandler;
import net.laby.bungee.handlers.ServerExitHandler;
import net.laby.bungee.handlers.ServerStartHandler;
import net.laby.bungee.utils.ConfigLoader;
import net.laby.protocol.JabyBootstrap;
import net.laby.protocol.utils.JabyUtils;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Class created by qlow | Jan
 */
public class Jaby extends Plugin implements Listener {

    private static Jaby instance;
    private ConfigLoader configLoader;

    @Getter
    private String password;
    private List<String> motd;

    @Getter
    private String motdString = "";

    private ServerBootstrap serverBootstrap;

    @Override
    public void onEnable() {
        instance = this;
        ProxyServer.getInstance().getPluginManager().registerListener( this, this );

        Map<String, Object> configDefaults = new HashMap<>();
        configDefaults.put( "password", "verysecurepasswordiswear1337" );
        configDefaults.put( "port", 1337 );
        configDefaults.put( "motd", Arrays.asList( "Zeile 1", "Zeile 2" ) );

        this.configLoader = new ConfigLoader( new File( getDataFolder(), "config.yml" ), configDefaults );
        this.password = JabyUtils.convertToMd5( getConfiguration().getString( "password" ) );
        this.motd = getConfiguration().getStringList( "motd" );

        // Setting motd-string
        for ( String motdElement : motd ) {
            motdString += (motdString.equals( "" ) ? "" : "\n") + motdElement;
        }

        // Registering handlers
        JabyBootstrap.registerHandler(
                LoginHandler.class,
                PowerUsageHandler.class,
                AvailableTypesHandler.class,
                ServerStartHandler.class,
                ServerExitHandler.class );

        int port = getConfiguration().getInt( "port" );

        // Running server-bootstrap
        JabyBootstrap.runServerBootstrap( port, new Consumer<ServerBootstrap>() {
            @Override
            public void accept( ServerBootstrap serverBootstrap ) {
                if ( serverBootstrap == null ) {
                    System.err.println( "[Jaby] Failed listening on port " + port + "!" );
                    System.exit( 0 );
                    return;
                }

                Jaby.this.serverBootstrap = serverBootstrap;

                System.out.println( "[Jaby] Listening on port " + port );

                // Running server-request task
                JabyBootstrap.getExecutorService().execute( new Runnable() {
                    @Override
                    public void run() {
                        while ( true ) {
                            try {
                                Thread.sleep( 5000L );
                            } catch ( InterruptedException e ) {
                                e.printStackTrace();
                            }

                            for ( ServerType serverType : ServerType.getServerTypes() ) {
                                serverType.startServers();
                            }
                        }
                    }
                } );
            }
        } );

    }

    @Override
    public void onDisable() {
        serverBootstrap.group().shutdownGracefully();
    }

    @EventHandler
    public void onPing( ProxyPingEvent event ) {
        ServerPing response = event.getResponse();
        response.setDescriptionComponent( new TextComponent( motdString ) );

        event.setResponse( response );
    }

    /**
     * Jaby's configuration
     *
     * @return Jaby's configuration object
     */
    public Configuration getConfiguration() {
        return configLoader.getConfiguration();
    }

    /**
     * Instance of Jaby
     *
     * @return Jaby's instance
     */
    public static Jaby getInstance() {
        return instance;
    }
}
