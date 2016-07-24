package net.laby.bungee;

import io.netty.bootstrap.ServerBootstrap;
import lombok.Getter;
import net.laby.bungee.handlers.AvailableTypesHandler;
import net.laby.bungee.handlers.LoginHandler;
import net.laby.bungee.handlers.MultiServerHandler;
import net.laby.bungee.handlers.PowerUsageHandler;
import net.laby.bungee.handlers.ServerDoneHandler;
import net.laby.bungee.handlers.ServerExitHandler;
import net.laby.bungee.handlers.ServerStartHandler;
import net.laby.bungee.utils.ConfigLoader;
import net.laby.protocol.JabyBootstrap;
import net.laby.protocol.utils.JabyUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Class created by qlow | Jan
 */
public class Jaby extends Plugin implements Listener {

    private static Jaby instance;
    private final static Random random = new Random();
    private ConfigLoader configLoader;

    @Getter
    private String password;

    private List<String> motd;
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
        configDefaults.put( "messages.no-servers-found", "&cEs wurde kein Server gefunden!" );

        this.configLoader = new ConfigLoader( new File( getDataFolder(), "config.yml" ), configDefaults );

        if(getConfiguration().getSection( "serverType" ) == null) {
            getConfiguration().set( "serverType.template.amount", 1 );
            getConfiguration().set( "serverType.template.standby", true );
            getConfiguration().set( "serverType.template.motd", Arrays.asList( "&bFirst", "&cepic line" ) );
            getConfiguration().set( "serverType.template.addresses", "nicedomain.ilovethis.com" );
            getConfiguration().set( "serverType.template.secondsUntilStopAtNoPlayers", 120 );
            getConfiguration().set( "serverType.template.copyServerContent", false );

            configLoader.saveConfig();
        }

        this.password = JabyUtils.convertToMd5( getConfiguration().getString( "password" ) );
        this.motd = getConfiguration().getStringList( "motd" );

        // Building MOTD
        for ( String motdEntry : motd ) {
            motdString += ChatColor.translateAlternateColorCodes( '&', motdEntry ) + "\n";
        }

        // Loading server-types
        loadServerTypes();

        // Running KillTask
        new KillTask();

        // Registering handlers
        JabyBootstrap.registerHandler(
                LoginHandler.class,
                PowerUsageHandler.class,
                AvailableTypesHandler.class,
                ServerStartHandler.class,
                ServerExitHandler.class,
                MultiServerHandler.class,
                ServerDoneHandler.class );

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
        ServerType serverType = ServerType.getByAddress(
                event.getConnection().getVirtualHost().getHostString().toLowerCase() );

        if ( serverType == null ) {
            response.setDescriptionComponent( new TextComponent( motdString ) );
        } else {
            response.setDescriptionComponent( new TextComponent( serverType.getMotd() ) );
        }

        event.setResponse( response );
    }

    @EventHandler
    public void onLogin( LoginEvent event ) {
        String hostName = event.getConnection().getVirtualHost().getHostString();
        ServerType serverType = ServerType.getByAddress( hostName.toLowerCase() );

        if ( serverType == null ) {
            return;
        }

        if ( serverType.isStarted() )
            return;

        if ( !serverType.startServers() ) {
            event.setCancelled( true );
            event.setCancelReason( ChatColor.translateAlternateColorCodes( '&', getConfiguration().getString( "messages.no-servers-found" ) ) );
            return;
        }

        event.registerIntent( this );
        serverType.getJoiningPlayers().put( event.getConnection().getUniqueId(), event );
    }

    @EventHandler
    public void onJoin( ServerConnectEvent event ) {
        String hostname = event.getPlayer().getPendingConnection().getVirtualHost().getHostString();
        ServerType serverType = ServerType.getByAddress( hostname.toLowerCase() );

        if ( serverType == null )
            return;

        if ( !serverType.getJoiningPlayers().containsKey( event.getPlayer().getUniqueId() ) ) {
            if ( serverType.getServers().size() != 0 ) {
                // Sending to server
                event.setTarget( ProxyServer.getInstance().getServerInfo( serverType.getType() + "-"
                        + (getServerByIndex( serverType, random.nextInt( serverType.getServers().size() ) )
                        .getPort() % 40000) ) );
            } else {
                if ( event.getTarget() == null ) {
                    // Kicking because there is no server with this type
                    event.getPlayer().disconnect( new TextComponent( ChatColor.translateAlternateColorCodes( '&',
                            getConfiguration().getString( "messages.no-servers-found" ) ) ) );
                } else {
                    event.getPlayer().sendMessage( new TextComponent( ChatColor.translateAlternateColorCodes( '&',
                            getConfiguration().getString( "messages.no-servers-found" ) ) ) );
                }
            }

            return;
        }

        if ( serverType.getServers().size() == 0 ) {
            event.getPlayer().sendMessage( new TextComponent( ChatColor.translateAlternateColorCodes( '&',
                    getConfiguration().getString( "messages.no-servers-found" ) ) ) );
            return;
        }

        String serverName = serverType.getType() + "-"
                + (getServerByIndex( serverType, serverType.getServers().size() - 1 ).getPort() % 40000);
        // Setting target & removing from joining players
        event.setTarget( ProxyServer.getInstance().getServerInfo( serverName ) );
        serverType.getJoiningPlayers().remove( event.getPlayer().getUniqueId() );
    }

    private JabyServer getServerByIndex( ServerType serverType, int index ) {
        int i = 0;

        for ( UUID serverKey : serverType.getServers().keySet() ) {
            if ( i == index ) {
                return serverType.getServers().get( serverKey );
            }

            i++;
        }

        return null;
    }

    /**
     * Loads the server-types in the config
     */
    public void loadServerTypes() {
        // Getting string-list from config
        Configuration serverTypes = getConfiguration().getSection( "serverType" );

        // Getting current server-type-list
        List<ServerType> serverTypeList = ServerType.getServerTypes();

        List<String> allTypeNames = new ArrayList<>();

        for ( String serverTypeKey : serverTypes.getKeys() ) {
            int typeAmount = getConfiguration().getInt( "serverType." + serverTypeKey + ".amount" );
            boolean standby = getConfiguration().getBoolean( "serverType." + serverTypeKey + ".standby" );
            List<String> motd = getConfiguration().getStringList( "serverType." + serverTypeKey + ".motd" );
            List<String> addresses = getConfiguration().getStringList( "serverType." + serverTypeKey + ".addresses" );
            int secondsUntilStopAtNoPlayers = getConfiguration().getInt( "serverType."
                    + serverTypeKey + ".secondsUntilStopAtNoPlayers" );
            boolean copyServerContent = getConfiguration().getBoolean( "serverType." + serverTypeKey + ".copyServerContent" );

            // Adding to a list with all type-names
            allTypeNames.add( serverTypeKey );

            // Buildung MOTD
            String motdString = "";

            for ( String motdEntry : motd ) {
                motdString += ChatColor.translateAlternateColorCodes( '&', motdEntry ) + "\n";
            }

            ServerType type;

            // Checking if the server-type exists
            if ( (type = ServerType.getByName( serverTypeKey )) != null ) {
                // Changing server-amount & standby
                type.setServerAmount( typeAmount );
                type.setStandby( standby );
                type.setMotd( motdString );
                type.setSecondsUntilStop( secondsUntilStopAtNoPlayers );
                type.setAddresses( addresses );
                type.setCopyServerContent( copyServerContent );
                continue;
            }

            // Adding to server-type list
            serverTypeList.add( new ServerType( serverTypeKey, standby, typeAmount, motdString,
                    secondsUntilStopAtNoPlayers, addresses, copyServerContent ) );
        }

        // Iterating through all server-types
        for ( Iterator<ServerType> serverTypeIterator = serverTypeList.iterator(); serverTypeIterator.hasNext(); ) {
            ServerType serverType = serverTypeIterator.next();

            // Checking if there is no server-type with this name in the config
            if ( allTypeNames.contains( serverType.getType() ) )
                continue;

            // Shutdowning and removing server
            serverType.shutdown();
            serverTypeIterator.remove();

            // Log message
            System.out.println( "[Jaby] Removed server-type " + serverType + "!" );
        }
    }

    /**
     * Saves the server-types into the config
     */
    public void saveServerTypes() {
        for ( ServerType serverType : ServerType.getServerTypes() ) {
            getConfiguration().set( "serverType." + serverType.getType() + ".amount", serverType.getServerAmount() );
            getConfiguration().set( "serverType." + serverType.getType() + ".standby", serverType.isStandby() );
            getConfiguration().set( "serverType." + serverType.getType() + ".motd", serverType.getMotd() );
            getConfiguration().set( "serverType." + serverType.getType() + ".secondsUntilStopAtNoPlayers",
                    serverType.getSecondsUntilStop() );
            getConfiguration().set( "serverType." + serverType.getType() + ".addresses", serverType.getAddresses() );
            getConfiguration().set( "serverType." + serverType.getType() + ".copyServerContent",
                    serverType.isCopyServerContent() );
        }

        configLoader.saveConfig();
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
