package net.laby.devathlon.game;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.laby.devathlon.utils.LocationSerializer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * The ArenaManager loads all arenas
 * Class created by qlow | Jan
 */
public class ArenaManager {

    private final static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private List<Arena> arenas = new ArrayList<>();
    private File directory;

    public ArenaManager( File directory ) {
        this.directory = directory;

        if ( !directory.exists() ) {
            directory.mkdirs();
        }

        // Loading arenas
        load();
    }

    /**
     * Loads the arenas
     */
    private void load() {
        if ( arenas.size() != 0 ) {
            throw new UnsupportedOperationException( "Can't load arenas twice!" );
        }

        // Iterating through all files in arena-directory
        for ( File arenaDirectory : directory.listFiles() ) {
            // Continuing if the file is not a directory
            if ( !arenaDirectory.isDirectory() )
                continue;

            // Initializing config-file & map-folder
            File configFile = new File( arenaDirectory, "config.json" );
            File mapFolder = new File( arenaDirectory, "map" );

            // Continuing if there is no config.yml
            if ( !configFile.exists() ) {
                System.err.println( "[DevAthlon] Didn't find config.json in " + arenaDirectory.getAbsolutePath() + "!" );
                continue;
            }

            // Continuing if there is no map-folder
            if ( !mapFolder.exists() || !mapFolder.isDirectory() ) {
                System.err.println( "[DevAthlon] Didn't find map in " + arenaDirectory.getAbsolutePath() + "!" );
                continue;
            }

            try {
                // Loading config
                ArenaConfig arenaConfig = gson.fromJson( IOUtils.toString( new FileInputStream( configFile ) ), ArenaConfig.class );

                // Loading the world if it is not loaded
                if ( Bukkit.getWorld( arenaConfig.getName() ) == null ) {
                    if ( new File( Bukkit.getWorldContainer(), arenaConfig.getName() ).exists() ) {
                        FileUtils.deleteDirectory( new File( Bukkit.getWorldContainer(), arenaConfig.getName() ) );
                    }

                    if ( new File( mapFolder, "uid.dat" ).exists() ) {
                        new File( mapFolder, "uid.dat" ).delete();
                    }

                    FileUtils.copyDirectory( mapFolder, new File( Bukkit.getWorldContainer(), arenaConfig.getName() ) );
                    Bukkit.createWorld( new WorldCreator( arenaConfig.getName() ) );
                }

                // List with spawn-locations & with sign-locations
                List<Location> spawns = new ArrayList<>();
                List<Location> signs = new ArrayList<>();

                // Iterating through all spawn-strings and adding the deserialized locations to the list
                for ( String spawnString : arenaConfig.getSpawns() ) {
                    spawns.add( LocationSerializer.fromString( spawnString ) );
                }

                // Iterating through all sign-strings and adding the deserialized locations to the list
                for ( String signString : arenaConfig.getSigns() ) {
                    signs.add( LocationSerializer.fromString( signString ) );
                }

                Arena arena = new Arena( arenaConfig, mapFolder, arenaConfig.getName(), spawns, signs );

                // Adding Arena to arena-list
                arenas.add( arena );

                arena.updateSigns();
            } catch ( Exception ex ) {
                ex.printStackTrace();
                System.err.println( "[DevAthlon] Couldn't load " + arenaDirectory.getAbsolutePath() + "!" );
                continue;
            }
        }
    }

    /**
     * Reloads the arenas
     */
    public void reload() {
        arenas.clear();

        load();
    }

    /**
     * Gets the arena by the given name
     *
     * @param name name of the wanted arena
     * @return arena with the given name (null if there is no arena with the given name)
     */
    public Arena getArenaByName( String name ) {
        for ( Arena arena : arenas ) {
            if ( !arena.getName().equalsIgnoreCase( name ) )
                continue;

            return arena;
        }

        return null;
    }

    public List<Arena> getArenas() {
        return arenas;
    }

    /**
     * Arenas' directory
     *
     * @return directory where all arenas are saved
     */
    public File getDirectory() {
        return directory;
    }

    /**
     * State whether the player is in an arena
     *
     * @param player player should be checked
     * @return true if the player is ingame
     */
    public boolean isIngame( Player player ) {
        return getPlayerArena( player ) != null;
    }

    /**
     * Gets the arena the player is in
     *
     * @param player player the check should be for
     * @return player's arena (null if there is no arena the player is in)
     */
    public Arena getPlayerArena( Player player ) {
        // Iterating through all arenas
        for ( Arena arena : arenas ) {
            // Checking whether the player is in the joined-players-list
            if ( !arena.getJoinedPlayers().contains( player.getUniqueId() ) )
                continue;

            // Returning true
            return arena;
        }

        return null;
    }

    /**
     * Saves the given arena's config
     *
     * @param arena arena the config should be saved from
     */
    public void saveConfig( Arena arena ) {
        try {
            PrintWriter printWriter = new PrintWriter( new FileOutputStream( new File( arena.getMapFolder().getParentFile(), "config.json" ) ) );

            printWriter.print( gson.toJson( arena.getArenaConfig() ) );

            printWriter.flush();
            printWriter.close();
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
    }

}
