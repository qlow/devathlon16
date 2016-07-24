package net.laby.bungee;

import io.netty.bootstrap.ServerBootstrap;
import lombok.Getter;
import lombok.Setter;
import net.laby.bungee.handlers.AvailableTypesHandler;
import net.laby.bungee.handlers.LoginHandler;
import net.laby.bungee.handlers.MultiServerHandler;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
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

    @Getter
    @Setter
    private int restartServersAfter;

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
        configDefaults.put( "serverTypes", Arrays.asList() );
        configDefaults.put( "startServerSeconds", 30 );
        configDefaults.put( "restartServersAfter", 30 );

        this.configLoader = new ConfigLoader( new File( getDataFolder(), "config.yml" ), configDefaults );
        this.password = JabyUtils.convertToMd5( getConfiguration().getString( "password" ) );
        this.motd = getConfiguration().getStringList( "motd" );
        this.restartServersAfter = getConfiguration().getInt( "restartServersAfter" );

        // Setting motd-string
        for ( String motdElement : motd ) {
            motdString += (motdString.equals( "" ) ? "" : "\n") + motdElement;
        }

        // Loading server-types
        loadServerTypes();

        // Registering handlers
        JabyBootstrap.registerHandler(
                LoginHandler.class,
                PowerUsageHandler.class,
                AvailableTypesHandler.class,
                ServerStartHandler.class,
                ServerExitHandler.class,
                MultiServerHandler.class );

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
                        try {
                            Thread.sleep( getConfiguration().getInt( "startServerSeconds" ) * 1000 );
                        } catch ( InterruptedException e ) {
                            e.printStackTrace();
                        }

                        while ( true ) {
                            for ( ServerType serverType : ServerType.getServerTypes() ) {
                                serverType.startServers();
                            }

                            try {
                                Thread.sleep( 5000L );
                            } catch ( InterruptedException e ) {
                                e.printStackTrace();
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
     * Loads the server-types in the config
     */
    public void loadServerTypes() {
        // Getting string-list from config
        List<String> serverTypes = getConfiguration().getStringList( "serverTypes" );

        // Getting current server-type-list
        List<ServerType> serverTypeList = ServerType.getServerTypes();

        List<String> allTypeNames = new ArrayList<>();

        for ( String serverType : serverTypes ) {
            // Splitting list-element into three parts
            String[] splitedServerType = serverType.split( ";" );

            String typeName = splitedServerType[0];
            int typeAmount = Integer.parseInt( splitedServerType[1] );
            boolean standby = Boolean.parseBoolean( splitedServerType[2] );

            // Adding to a list with all type-names
            allTypeNames.add( typeName );

            ServerType type;

            // Checking if the server-type exists
            if ( (type = ServerType.getByName( typeName )) != null ) {
                // Changing server-amount & standby
                type.setServerAmount( typeAmount );
                type.setStandby( standby );
                continue;
            }

            // Adding to server-type list
            serverTypeList.add( new ServerType( typeName, standby, typeAmount ) );
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
        // New string-list
        List<String> serverTypesList = new ArrayList<>();

        for ( ServerType serverType : ServerType.getServerTypes() ) {
            serverTypesList.add( serverType.getType() + ";" + serverType.getServerAmount() + ";" + serverType.isStandby() );
        }

        // Setting list to config
        getConfiguration().set( "serverTypes", serverTypesList );
        saveServerTypes();
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
