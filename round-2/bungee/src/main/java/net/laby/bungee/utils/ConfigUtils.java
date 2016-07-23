package net.laby.bungee.utils;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Class created by qlow | Jan
 */
public class ConfigUtils {

    private ConfigurationProvider configurationProvider;

    private Configuration configuration;

    private File configFile;
    private Map<String, Object> defaults;

    /**
     * Creates a config-file & loads the config by the file
     *
     * @param configFile config's file
     * @param defaults   the default values of the config
     */
    public ConfigUtils( File configFile, Map<String, Object> defaults ) {
        this.configurationProvider = ConfigurationProvider.getProvider( YamlConfiguration.class );
        this.configFile = configFile;
        this.defaults = defaults;

        loadConfig();
    }

    /**
     * Loads the config
     */
    private void loadConfig() {
        try {
            this.configuration = configurationProvider.load( configFile );
        } catch ( IOException e ) {
            e.printStackTrace();
        }

        // Iterating through the defaults and adding defaults
        for ( Map.Entry<String, Object> defaultEntry : defaults.entrySet() ) {
            addDefault( defaultEntry.getKey(), defaultEntry.getValue() );
        }

        // Saving config
        saveConfig();
    }

    /**
     * Sets the given key to the given value if the key doesn't exist
     *
     * @param key   key
     * @param value value that should be set
     */
    public void addDefault( String key, Object value ) {
        if ( configuration.get( key ) != null )
            return;

        // Setting key
        configuration.set( key, value );
    }

    /**
     * Saves the config-file
     */
    public void saveConfig() {
        try {
            configurationProvider.save( configuration, configFile );
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    /**
     * The config's object
     *
     * @return a configuration-object
     */
    public Configuration getConfiguration() {
        return configuration;
    }

    /**
     * The config's file
     *
     * @return File-object of the config
     */
    public File getConfigFile() {
        return configFile;
    }
}
