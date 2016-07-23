package net.laby.bungee;

import net.laby.bungee.utils.ConfigLoader;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Class created by qlow | Jan
 */
public class Jaby extends Plugin {

    private static Jaby instance;
    private ConfigLoader configLoader;

    @Override
    public void onEnable() {
        instance = this;

        Map<String, Object> configDefaults = new HashMap<>();
        configDefaults.put( "password", "verysecurepasswordiswear1337" );

        this.configLoader = new ConfigLoader( new File( getDataFolder(), "config.yml" ), configDefaults );
    }

    @Override
    public void onDisable() {

    }

    /**
     * Jaby's configuration
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
