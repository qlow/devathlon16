package net.laby.bungee;

import io.netty.bootstrap.ServerBootstrap;
import net.laby.bungee.handlers.LoginHandler;
import net.laby.bungee.handlers.PowerUsageHandler;
import net.laby.bungee.utils.ConfigLoader;
import net.laby.protocol.JabyBootstrap;
import net.laby.protocol.utils.JabyUtils;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Class created by qlow | Jan
 */
public class Jaby extends Plugin {

    private static Jaby instance;
    private ConfigLoader configLoader;

    private String password;

    private ServerBootstrap serverBootstrap;

    @Override
    public void onEnable() {
        instance = this;

        Map<String, Object> configDefaults = new HashMap<>();
        configDefaults.put( "password", "verysecurepasswordiswear1337" );
        configDefaults.put( "port", 1337 );

        this.configLoader = new ConfigLoader( new File( getDataFolder(), "config.yml" ), configDefaults );
        this.password = JabyUtils.convertToMd5( getConfiguration().getString( "password" ) );

        int port = getConfiguration().getInt( "port" );

        // Registering handlers
        JabyBootstrap.registerHandler( LoginHandler.class, PowerUsageHandler.class );

        // Running server-bootstrap
        JabyBootstrap.runServerBootstrap( port, new Consumer<ServerBootstrap>() {
            @Override
            public void accept( ServerBootstrap serverBootstrap ) {
                Jaby.this.serverBootstrap = serverBootstrap;

                System.out.println( "[Jaby] Listening on port " + port );
            }
        } );
    }

    @Override
    public void onDisable() {
        serverBootstrap.group().shutdownGracefully();
    }

    /**
     * The password in the config (hashed with MD5)
     *
     * @return MD5-hashed password
     */
    public String getPassword() {
        return password;
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
